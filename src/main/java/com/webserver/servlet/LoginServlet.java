package com.webserver.servlet;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * 用来处理用户登陆的操作
 */
public class LoginServlet extends HttpServlet {
    public void service(HttpRequest request, HttpResponse response) {

        String userName = request.getParameter("username");
        String password = request.getParameter("password");

        try (RandomAccessFile raf= new RandomAccessFile("user.dat", "rw")
        ) {
            //boolean addName=true;
            for (int i = 0; i < raf.length()/100; i++) {
                raf.seek(100*i);
                byte[] data = new byte[32];
                raf.read(data);
                String name = new String(data, "UTF-8").trim();
                if (name.equals(userName)) {
                    raf.read(data);
                    if (new String(data,"UTF-8").trim().equals(password)){
                        forward("/myweb/login_success.html",request,response);
                        return;
                    }
                break;
                }
            }
            forward("/myweb/login_fail.html",request,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

