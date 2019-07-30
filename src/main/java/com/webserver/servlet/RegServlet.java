package com.webserver.servlet;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Scanner;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

/**
 * 用来处理用户注册业务
 * @author ta
 *
 */
public class RegServlet extends HttpServlet {
	public void service(HttpRequest request,HttpResponse response) {
		/*
		 * 用户注册
		 * 1:通过request获取用户表单输入的信息
		 * 2:将该用户注册信息写入文件保存
		 * 3:响应客户端注册结果页面
		 */
		System.out.println("RegServlet:开始处理注册业务...");
		//1 获取注册页面上表单中用户输入的注册信息
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String nickname = request.getParameter("nickname");
		int age = Integer.parseInt(
			request.getParameter("age")
		);//NumberFormatException
		System.out.println("username:"+username);
		System.out.println("password:"+password);
		System.out.println("nickname:"+nickname);
		//System.out.println("age:"+age);
		
		/*
		 * 2将该用户信息保存到user.dat文件中
		 * 每个用户占用100字节，其中用户名，密码
		 * 昵称为字符串各占32字节，年龄为int占4字节
		 */
		try(
			RandomAccessFile raf
				= new RandomAccessFile("user.dat","rw");
		){
			/**
			 * 首先要检查user.dat文件中是否已经存在该用户，如果存在直接给用户显示提示页面：
			 * user-exist.html，告知其该用户已经存在。否则才执行注册操作
			 */
			boolean addName=true;
			for (int i=0;i<raf.length()/100;i++){
				byte[] data= new byte[32];
				raf.read(data);
				String name=new String(data,"UTF-8").trim();
				if (name.equals(username)) {
					forward("/myweb/user_existed.html",request,response);
					addName=false;
				}
			}

			if (addName) {
				//现将指针移动到文件末尾，以便追加新记录
				raf.seek(raf.length());

				//写用户名
				byte[] data = username.getBytes("UTF-8");
				data = Arrays.copyOf(data, 32);
				raf.write(data);

				//写密码
				data = password.getBytes("UTF-8");
				data = Arrays.copyOf(data, 32);
				raf.write(data);

				//写昵称
				data = nickname.getBytes("UTF-8");
				data = Arrays.copyOf(data, 32);
				raf.write(data);

				//写年龄
				raf.writeInt(age);

				//响应客户端注册成功页面
				forward("/myweb/user_success.html",request,response);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("RegServlet:处理注册业务完毕!");
		
	}
}




