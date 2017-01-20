package com.snail.common.proxy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.snail.common.https.CertificateUtils;
import com.snail.common.https.SocketUtils;

import ch.qos.logback.core.net.SyslogOutputStream;

public class ProxyTask implements Runnable {
	private Socket proxySocket;
	private Socket sendSocket;

	private long totalUpload = 0l;//总计上行比特数  
	private long totalDownload = 0l;//总计下行比特数  

	public ProxyTask(Socket socket) {
		this.proxySocket = socket;
	}

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	/** 已连接到请求的服务器 */
	private static final String AUTHORED = "HTTP/1.1 200 Connection established\r\n\r\n";

	/** 内部错误 */
	private static final String SERVERERROR = "HTTP/1.1 500 Connection FAILED\r\n\r\n";

	@Override
	public void run() {

		StringBuilder builder = new StringBuilder();
		try {
			builder.append("\r\n").append("Request Time  ：" + sdf.format(new Date()));

			InputStream proxyInputStream = proxySocket.getInputStream();
			OutputStream proxyOutStream = proxySocket.getOutputStream();

			//从客户端流数据中读取头部，获得请求主机和端口  
			HttpHeader header = HttpHeader.readHeader(proxyInputStream);

			//添加请求日志信息  
			builder.append("From    Host  ：" + proxySocket.getInetAddress());
			builder.append(" Port  ：" + proxySocket.getPort());
			builder.append(" Method：" + header.getMethod());
			builder.append(" Request Host  ：" + header.getHost());
			builder.append(" Request Port  ：" + header.getPort()).append("\r\n");

			System.out.println("header = " + String.valueOf(header));
			System.out.println("host = " + String.valueOf(header.getHost()));
			if (!header.getHost().contains("baidu")) {
				return;
			}
			
			//如果没解析出请求请求地址和端口，则返回错误信息  
			if (header.getHost() == null || header.getPort() == null) {
				proxyOutStream.write(SERVERERROR.getBytes());
				proxyOutStream.flush();
				return;
			}

			// 查找主机和端口  
			sendSocket = new Socket(header.getHost(), Integer.parseInt(header.getPort()));
			sendSocket.setKeepAlive(true);
			InputStream senderInputStream = sendSocket.getInputStream();
			OutputStream senderOutStream = sendSocket.getOutputStream();
			
			Thread replyThread = new DataSendThread(senderInputStream, proxyOutStream);
			replyThread.start();
			if (header.getMethod().equals(HttpHeader.METHOD_CONNECT)) {
				// 将已联通信号返回给请求页面  
//				this.generateCertificate(proxyInputStream);
				proxyOutStream.write(AUTHORED.getBytes());
				proxyOutStream.flush();
			} else {
				//http请求需要将请求头部也转发出去  
				byte[] headerData = header.toString().getBytes();
				totalUpload += headerData.length;
				senderOutStream.write(headerData);
				senderOutStream.flush();
			}
			//读取客户端请求过来的数据转发给服务器  
			readForwardData(proxyInputStream, senderOutStream);
			//等待向客户端转发的线程结束  
			replyThread.join();
		} catch (Exception e) {
			e.printStackTrace();
			if (!proxySocket.isOutputShutdown()) {
				//如果还可以返回错误状态的话，返回内部错误  
				try {
					proxySocket.getOutputStream().write(SERVERERROR.getBytes());
				} catch (IOException e1) {
				}
			}
		} finally {
			try {
				if (proxySocket != null) {
					proxySocket.close();
				}
			} catch (IOException e) {
			}
			if (sendSocket != null) {
				try {
					sendSocket.close();
				} catch (IOException e) {
				}
			}
			//纪录上下行数据量和最后结束时间并打印  
			builder.append("\r\n").append("Up    Bytes  ：" + totalUpload);
			builder.append("\r\n").append("Down  Bytes  ：" + totalDownload);
			builder.append("\r\n").append("Closed Time  ：" + sdf.format(new Date()));
			builder.append("\r\n");
			logRequestMsg(builder.toString());
		}
	}

//	private void generateCertificate(InputStream proxyInputStream) {
//		DataInputStream dataInputStream = new DataInputStream(proxyInputStream);
//		try {
//			int length = 4096;
//			//第一步 获取客户端发送的支持的加密规则，包括hash算法，这里选用SHA1作为hash
//			byte[] clientSupportHash = SocketUtils.readBytes(dataInputStream, length);
//			String clientHash = new String(clientSupportHash);
//			String hash = clientHash;
//			System.out.println("客户端发送了hash算法为:" + hash);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		
//		
//	}

	/** 
	 * 避免多线程竞争把日志打串行了 
	 * @param msg 
	 */
	private synchronized void logRequestMsg(String msg) {
//		System.out.println(msg);
	}

	/** 
	 * 读取客户端发送过来的数据，发送给服务器端 
	 *  
	 * @param receiverInputStream 
	 * @param senderOutStream 
	 */
	private void readForwardData(InputStream receiverInputStream, OutputStream senderOutStream) {
		byte[] buffer = new byte[4096];
		StringBuilder ss = new StringBuilder();
		try {
			int len;
			while ((len = receiverInputStream.read(buffer)) != -1) {
				if (len > 0) {
					senderOutStream.write(buffer, 0, len);
					ss.append(new String(buffer, "utf-8"));
					senderOutStream.flush();
				}
				totalUpload += len;
				if (proxySocket.isClosed() || sendSocket.isClosed()) {
					break;
				}
			}
		} catch (Exception e) {
			try {
				sendSocket.close();// 尝试关闭远程服务器连接，中断转发线程的读阻塞状态  
			} catch (IOException e1) {
			}
		}
		
		System.out.println(ss);
	}

	/** 
	 * 将服务器端返回的数据转发给客户端 
	 *  
	 * @param isOut 
	 * @param osIn 
	 */
	class DataSendThread extends Thread {
		private InputStream isOut;
		private OutputStream osIn;

		DataSendThread(InputStream senderInputStream, OutputStream receiverOutStream) {
			this.isOut = senderInputStream;
			this.osIn = receiverOutStream;
		}

		@Override
		public void run() {
			StringBuilder builder = new StringBuilder();
			byte[] buffer = new byte[4096];
			
			try {
				int len;
				while ((len = isOut.read(buffer)) != -1) {
					if (len > 0) {
						osIn.write(buffer, 0, len);
						builder.append(new String(buffer, 0, len, "UTF-8" ));
						osIn.flush();
						totalDownload += len;
					}
					if (proxySocket.isOutputShutdown() || sendSocket.isClosed()) {
						break;
					}
				}
			} catch (Exception e) {
			}
			System.out.println("BUILDER = " + builder);
		}
		
	}

}