package com.snail.common.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProxyTask implements Runnable {
	private Socket receiverSocket;
	private Socket sendSocket;

	private long totalUpload = 0l;//总计上行比特数  
	private long totalDownload = 0l;//总计下行比特数  

	public ProxyTask(Socket socket) {
		this.receiverSocket = socket;
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

			InputStream receiverInputStream = receiverSocket.getInputStream();
			OutputStream receiverOutStream = receiverSocket.getOutputStream();

			//从客户端流数据中读取头部，获得请求主机和端口  
			HttpHeader header = HttpHeader.readHeader(receiverInputStream);

			//添加请求日志信息  
			builder.append("From    Host  ：" + receiverSocket.getInetAddress());
			builder.append(" Port  ：" + receiverSocket.getPort());
			builder.append(" Method：" + header.getMethod());
			builder.append(" Request Host  ：" + header.getHost());
			builder.append(" Request Port  ：" + header.getPort()).append("\r\n");

			//如果没解析出请求请求地址和端口，则返回错误信息  
			if (header.getHost() == null || header.getPort() == null) {
				receiverOutStream.write(SERVERERROR.getBytes());
				receiverOutStream.flush();
				return;
			}

			// 查找主机和端口  
			sendSocket = new Socket(header.getHost(), Integer.parseInt(header.getPort()));
			sendSocket.setKeepAlive(true);
			InputStream senderInputStream = sendSocket.getInputStream();
			OutputStream senderOutStream = sendSocket.getOutputStream();
			//新开一个线程将返回的数据转发给客户端,串行会出问题，尚没搞明白原因  
			Thread replyThread = new DataSendThread(senderInputStream, receiverOutStream);
			replyThread.start();
			if (header.getMethod().equals(HttpHeader.METHOD_CONNECT)) {
				// 将已联通信号返回给请求页面  
				receiverOutStream.write(AUTHORED.getBytes());
				receiverOutStream.flush();
			} else {
				//http请求需要将请求头部也转发出去  
				byte[] headerData = header.toString().getBytes();
				totalUpload += headerData.length;
				senderOutStream.write(headerData);
				senderOutStream.flush();
			}
			//读取客户端请求过来的数据转发给服务器  
			readForwardData(receiverInputStream, senderOutStream);
			//等待向客户端转发的线程结束  
			replyThread.join();
		} catch (Exception e) {
			e.printStackTrace();
			if (!receiverSocket.isOutputShutdown()) {
				//如果还可以返回错误状态的话，返回内部错误  
				try {
					receiverSocket.getOutputStream().write(SERVERERROR.getBytes());
				} catch (IOException e1) {
				}
			}
		} finally {
			try {
				if (receiverSocket != null) {
					receiverSocket.close();
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

	/** 
	 * 避免多线程竞争把日志打串行了 
	 * @param msg 
	 */
	private synchronized void logRequestMsg(String msg) {
		System.out.println(msg);
	}

	/** 
	 * 读取客户端发送过来的数据，发送给服务器端 
	 *  
	 * @param receiverInputStream 
	 * @param senderOutStream 
	 */
	private void readForwardData(InputStream receiverInputStream, OutputStream senderOutStream) {
		byte[] buffer = new byte[4096];
		try {
			int len;
			while ((len = receiverInputStream.read(buffer)) != -1) {
				if (len > 0) {
					senderOutStream.write(buffer, 0, len);
					senderOutStream.flush();
				}
				totalUpload += len;
				if (receiverSocket.isClosed() || sendSocket.isClosed()) {
					break;
				}
			}
		} catch (Exception e) {
			try {
				sendSocket.close();// 尝试关闭远程服务器连接，中断转发线程的读阻塞状态  
			} catch (IOException e1) {
			}
		}
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
			byte[] buffer = new byte[4096];
			try {
				int len;
				while ((len = isOut.read(buffer)) != -1) {
					if (len > 0) {
						osIn.write(buffer, 0, len);
						osIn.flush();
						totalDownload += len;
					}
					if (receiverSocket.isOutputShutdown() || sendSocket.isClosed()) {
						break;
					}
				}
			} catch (Exception e) {
			}
		}
	}

}