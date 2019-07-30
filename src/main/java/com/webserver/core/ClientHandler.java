package com.webserver.core;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import com.webserver.context.ServerContext;
import com.webserver.exception.EmptyRequestException;
import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;
import com.webserver.servlet.HttpServlet;
import com.webserver.servlet.LoginServlet;
import com.webserver.servlet.RegServlet;

/**
 * 该线程负责处理一个客户端的连接
 * @author ta
 *
 */
public class ClientHandler implements Runnable{
	private Socket socket;
	public ClientHandler(Socket socket) {
		this.socket = socket;
	}
	public void run() {
		try {
			//处理一个客户端的请求分为三步
			
			//1准备工作
			HttpRequest request = new HttpRequest(socket);
			HttpResponse response = new HttpResponse(socket);
			//2处理请求
			//2.1根据请求对象，获取请求的抽象路径
			String uri = request.getRequestURI();
			//2.2根据请求路径判断是否为请求业务

			HttpServlet servlet= ServerContext.getServlet(uri);
			if(servlet!=null) {
				servlet.service(request,response);
				System.out.println("动态资源");
			}
			else {
				//2.3根据抽象路径去webapps目录下寻找该资源
				File file = new File("./webapps"+uri);
				//2.4判断请求的资源是否存在?
				if(file.exists()) {
					System.out.println("ClientHandler:该资源已找到!");
					//将用户请求的资源设置到response中
					response.setEntity(file);
					
				}else {
					System.out.println("ClientHandler:该资源不存在!");
					response.setStatusCode(404);
					response.setStatusReason("NOT FOUND");
					response.setEntity(new File("./webapps/root/404.html"));
					
				}
			}

			response.flush();

		}catch (EmptyRequestException e){
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				//响应完毕后与客户端断开连接
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}




