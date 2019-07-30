package com.webserver.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.webserver.context.HttpContext;

import java.util.Set;

/**
 * 响应对象
 * 该类的每一个实例表示服务端发送给客户端的响应内容
 * 一个响应应当由三部分构成:
 * 状态行，响应头，响应正文
 * @author ta
 *
 */
public class HttpResponse {
	//状态行相关信息定义
	private int statusCode = 200;
	private String statusReason = "OK";
	
	
	//响应头相关信息定义
	private Map<String,String> headers = new HashMap<>();
	
	
	
	//响应正文相关信息定义
	//正文实体对象
	private File entity;
	
	
	
	//与连接相关的信息
	private Socket socket;
	private OutputStream out;
	
	public HttpResponse(Socket socket) {
		try {
			this.socket = socket;
			this.out = socket.getOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 将当前相应对象内容以一个标准的HTTP响应格式
	 * 发送给客户端
	 */
	public void flush() {
		/*
		 * 1:发送状态行
		 * 2:发送响应头
		 * 3:发送响应正文
		 */
		try {
			//System.out.println("HttpResponse:开始发送响应");
			sendStatusLine();
			sendHeaders();
			sendContent();
			//System.out.println("HttpResponse:发送响应完毕!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 发送状态行
	 */
	private void sendStatusLine() {
		try {
			//System.out.println("HttpResponse:开始发送状态行");
			String line = "HTTP/1.1" + " " + statusCode + " " + statusReason;
			out.write(line.getBytes("ISO8859-1"));
			out.write(13);// written CR
			out.write(10);// written LF
			System.out.println("HttpResponse:状态行内容:"+line);
			//System.out.println("HttpResponse:发送状态行完毕!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 发送响应头
	 */
	private void sendHeaders() {
		try {
			//System.out.println("HttpResponse:开始发送响应头");
			
			Set<Entry<String,String>> entrySet 
							= headers.entrySet();
			for(Entry<String,String> header:entrySet) {
				String name = header.getKey();
				String value = header.getValue();
				String line = name+": "+value;
				out.write(line.getBytes("ISO8859-1"));
				out.write(13);
				out.write(10);
				System.out.println(line);
			}
			
			//单独发送CRLF表示响应头发送完毕
			out.write(13);
			out.write(10);
			//System.out.println("HttpResponse:发送响应头完毕");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 发送响应正文
	 */
	private void sendContent() {
		//System.out.println("HttpResponse:开始发送响应正文");
		if(entity!=null) {
			try(
				FileInputStream fis
					= new FileInputStream(entity);
			){
				int len = -1;
				byte[] data = new byte[1024*10];
				while((len = fis.read(data))!=-1) {
					out.write(data, 0, len);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("HttpResponse:发送响应正文完毕");
	}
	public File getEntity() {
		return entity;
	}
	/**
	 * 设置响应正文对应的实体文件
	 * 
	 * 由于一个响应包含正文时一定会对应的包含
	 * 两个响应头：Content-Type与Content-Length
	 * 因此，在设置正文文件的同时自动根据该文件
	 * 设置这两个响应头。
	 * 
	 * 
	 * @param entity
	 */
	public void setEntity(File entity) {
		//获取到实体文件的名字 index.html
		String fileName = entity.getName();
		//获取该文件的后缀(文件类型)
		int index = fileName.lastIndexOf(".")+1;
		String ext = fileName.substring(index);
		//根据文件后缀获取对应的Content-Type的值
		String type = HttpContext.getMimeType(ext);
		
		
		putHeader("Content-Type", type);
		putHeader("Content-Length", entity.length()+"");
		
		this.entity = entity;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusReason() {
		return statusReason;
	}
	public void setStatusReason(String statusReason) {
		this.statusReason = statusReason;
	}
	/**
	 * 设置响应头
	 * @param name 响应头的名字
	 * @param value 响应头对应的值
	 */
	public void putHeader(String name,String value) {
		this.headers.put(name, value);
	}
	
	
}









