package com.webserver.core;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WebServer主类
 * 
 * WebServer是模拟Tomcat实现的一个Web容器
 * 
 * Web容器的主要职责是管理其下部署的多个webapp
 * 并且通过TCP协议与HTTP协议等与客户端建立连接，
 * 使得用户可以通过客户端(通常是浏览器)与服务端
 * 建立连接并访问webapp中的功能
 * 
 * 一个Webapp称为一个网络应用，它主要包括的内容
 * 有:网页，素材，逻辑代码(java程序)。可以简单的
 * 理解为一个"网站"就是一个运行在Web容器上的一个
 * webapp.
 * 
 * 
 * TCP协议:是处于传输层的协议，负责在两台计算机
 * 之间传输数据使用
 * 
 * HTTP协议:处于应用层的协议，负责规定两台计算机
 * 之间的应用交互和传输数据的内容，格式等定义。
 * 
 * @author ta
 *
 */
public class WebServer {
	private ServerSocket server;
	private ExecutorService threadPool;
	/**
	 * 构造方法，用来初始化服务端
	 */
	public WebServer() {
		try {
			System.out.println("正在启动服务端...");
			server = new ServerSocket(8088);
			threadPool= Executors.newFixedThreadPool(10);
			//System.out.println("服务端启动完毕!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 服务端开始工作的方法
	 */
	public void start() {
		try {
			while(true) {
				System.out.println("等待客户端连接...");
				Socket socket = server.accept();
				System.out.println("一个客户端连接了!");
				
				//启动一个线程处理该客户端请求
				ClientHandler handler 
					= new ClientHandler(socket);
				threadPool.execute(handler);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		WebServer server = new WebServer();
		server.start();
	}
}









