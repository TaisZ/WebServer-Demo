package com.webserver.context;

import com.webserver.servlet.HttpServlet;
import com.webserver.servlet.LoginServlet;
import com.webserver.servlet.RegServlet;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务端配置信息
 */
public class ServerContext {
    private static Map<String, HttpServlet> servlet_mapping= new HashMap<>();

    static {
        initServletMapping();
    }

    private static void initServletMapping() {
        /**
         * 使用dom4j读取conf/servelets.xml，将根目录下所有<servlet>标签读取出来
         * 将每个标签的属性： path作为key，className通过反射机制加载该类的类对象并实例化
         * 对应的Servlet，将实例化之后的对象作为value存入mapping
         */
        SAXReader saxReader= new SAXReader();
        Document doc= null;
        try {
            doc = saxReader.read(new File("conf/servlets.xml"));

        Element root= doc.getRootElement();
        List<Element> list= root.elements("servlet");
        for (Element e:list){
            String className= e.attributeValue("className");
            Class cls= Class.forName(className);
            servlet_mapping.put(e.attributeValue("path"),(HttpServlet)cls.newInstance());
        }
//        servlet_mapping.put("/myweb/reg.html",new RegServlet());
//        servlet_mapping.put("/myweb/login.html",new LoginServlet());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HttpServlet getServlet(String uri){
            return servlet_mapping.get(uri);
    }
}
