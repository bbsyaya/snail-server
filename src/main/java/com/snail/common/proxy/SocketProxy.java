package com.snail.common.proxy;

import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketProxy {

	private static final int listenPort = 8002;

	public static void main(String[] args) throws Exception {
		//		setProxyPort();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		ServerSocket serverSocket = new ServerSocket(listenPort);
		final ExecutorService tpe = Executors.newCachedThreadPool();
		System.out.println("listening port:" + listenPort + "……");

		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				socket.setKeepAlive(true);
				//加入任务列表，等待处理  
				tpe.execute(new ProxyTask(socket));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void setProxyPort() {
		Properties prop = System.getProperties();

		System.getProperties().put("proxySet", "true");
		// 设置http访问要使用的代理服务器的地址
		prop.put("proxyHost", "127.0.0.1");

		// 设置http访问要使用的代理服务器的端口
		prop.put("proxyPort", listenPort + "");

		System.getProperties().put("http.proxyUser", "someUserName");
		System.getProperties().put("http.proxyPassword", "somePassword");
	}

}
