package com.webserver.servlet;

import com.webserver.http.HttpRequest;
import com.webserver.http.HttpResponse;

import java.io.File;

public  abstract class HttpServlet {
    public abstract void service(HttpRequest request,HttpResponse response);

    public void forward(String path,HttpRequest request,HttpResponse response){
        response.setEntity(new File("./webapps"+path));
    }
}
