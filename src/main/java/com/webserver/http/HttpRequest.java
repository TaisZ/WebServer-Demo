package com.webserver.http;

import com.webserver.exception.EmptyRequestException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求对象
 * 该类的每一个实例用于表示客户端发送过来的一个
 * 请求内容
 * HTTP协议规定一个请求由三部分构成:
 * 请求行，消息头，消息正文
 * @author ta
 *
 */
public class HttpRequest {
	//请求行相关信息定义
	//请求的方式
	private String method;
	//资源的抽象路径
	private String uri;
	//请求使用的协议版本
	private String protocol;
	//uri中"?"左侧的请求部分内容
	private String requestURI;
	//uri中"?"右侧的参数部分内容
	private String queryString;
	//保存每一个具体的参数，key:参数名 value:参数值
	private Map<String,String> parameters = new HashMap<>();
	//消息头相关信息定义
	private Map<String,String> headers = new HashMap<>();

	//消息正文相关信息定义
	
	
	//和客户端连接相关信息
	private Socket socket;
	private InputStream in;
	/**
	 * 构造方法，用来初始化HttpRequest
	 * 初始化的过程就是解析请求的过程
	 */
	public HttpRequest(Socket socket) throws EmptyRequestException {
		try {
			this.socket = socket;
			this.in = socket.getInputStream();
			//解析请求
			parseRequestLine();
			parseHeaders();
			parseContent();

		}catch (EmptyRequestException e){
			throw e;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 解析请求行
	 */
	private void parseRequestLine() throws EmptyRequestException {
		System.out.println("HttpRequest:开始解析请求行...");
		try {
			//读取客户端发送过来的第一行字符串(请求行内容)
			String line = readLine();
			System.out.println(line);
			//如果出现空请求,解决可能出现的下标越界
			if ("".equals(line)) throw new EmptyRequestException("空请求异常");

			String[] data = line.split("\\s");
			method = data[0];
			uri = data[1];
			protocol = data[2];
			
			//进一步解析uri
			parseURI();

		} catch (EmptyRequestException e){
			throw e;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("HttpRequest:解析请求行完毕!");
	}
	/**
	 * 进一步解析uri
	 */
	private void parseURI() {
		/*
		 * uri存在两种情况
		 * 1:有参数，比如:
		 *   /myweb/reg?username=xxx&password=xxx&...
		 * 
		 * 2:没有参数,比如:
		 *   /myweb/index.html
		 *   
		 * 因此,解析过程为:
		 * 1:首先判断uri是否含有参数(有没有"?")  
		 *   1.1:没有参数，则直接将uri的值赋值
		 *       给requestURI即可
		 *       
		 *   1.2:有参数，执行步骤2
		 *       
		 * 2:将uri按照"?"进行拆分，将"?"左侧的内容
		 *   赋值给requestURI。将"?"右侧内容赋值给
		 *   queryString。
		 * 
		 * 3:进一步拆分参数部分,将queryString按照
		 *   "&"拆分为若干个参数，每一个参数再按照
		 *   "="拆分为参数名与参数值，并将它们以key
		 *   value形式存入到parameters这个Map中
		 *   完成全部解析工作。    
		 */
		if(!uri.contains("?")) {
			//没有"?"
			requestURI = uri;
		}else {
			String[] data = uri.split("\\?");
			requestURI = data[0];
			if(data.length>1) {//判断"?"右侧是否有内容
				queryString = data[1];
				//将参数中所有%XX的内容还原成对应文字
				try {
					queryString=URLDecoder.decode(queryString,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				//进一步拆分参数
				data = queryString.split("&");
				for(String line : data) {
					String[] para = line.split("=");
					if(para.length>1) {
						parameters.put(para[0], para[1]);
					}else {
						parameters.put(para[0], null);
					}
				}
			}
		}

		System.out.println("requestURI:"+requestURI);
		System.out.println("queryString:"+queryString);
		System.out.println("parameters:"+parameters);
	}
	
	/**
	 * 解析消息头
	 */
	private void parseHeaders() {
		//System.out.println("HttpRequest:开始解析消息头...");
		try {
			/*
			 * 消息头是由若干行组成的，每一行是一个
			 * 消息头，格式为:
			 * 消息头名字: 消息头值(CRLF)
			 * 最后一个消息头发送完毕后，客户端会单独发送一个
			 * CRLF表示消息头全部发送完毕。
			 * 
			 * 因此，我们在这里解析消息头应当:
			 * 1:循环调用readLine方法读取每一行字符串
			 * 2:判断读取的这行是否为空字符串("")，若是空字符
			 *   串则表示单独读取到了CRLF，循环应当停止
			 * 3:若读取到的不是空字符串，则这一行内容就是一个
			 *   消息头，那么我们应当将该行字符串按照冒号空格
			 *   (": ")进行拆分来得到消息头的名字和对应的值，
			 *   然后将名字作为key，值作为value存入到headers
			 *   这个Map中完成解析工作  
			 */
			while(true) {
				String line = readLine();
				if("".equals(line)) {
					break;
				}
				String[] data = line.split(": ");
				headers.put(data[0], data[1]);
			}
			System.out.println("headers:"+headers);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		//System.out.println("HttpRequest:解析消息头完毕!");
	}
	/**
	 * 解析消息正文
	 */
	private void parseContent() {
		//System.out.println("HttpRequest:开始解析消息正文...");
		
		//System.out.println("HttpRequest:解析消息正文完毕!");
	}
	
	/**
	 * 通过Socket得到的输入流读取一行字符串。(以CRLF
	 * 结尾的)
	 */
	private String readLine()throws IOException {
		/*
		 * 测试读取一行字符串，若连续读取的两个
		 * 字符分别为CR,LF就可以停止，然后将读
		 * 取到的字符组成一个字符串
		 */
		StringBuilder builder = new StringBuilder();
		int d = -1;
		//c1表示上次读取到的字符，c2表示本次读取到的字符
		char c1='a',c2='a';
		while((d = in.read())!=-1) {
			c2 = (char)d;
			//判断是否连续读取到了CR,LF
			if(c1==13&&c2==10) {
				break;
			}
			builder.append(c2);
			c1 = c2;
		}
		String line = builder.toString().trim();
		return line;
	}
	public String getMethod() {
		return method;
	}
	public String getUri() {
		return uri;
	}
	public String getProtocol() {
		return protocol;
	}
	public String getHeader(String name) {
		return headers.get(name);
	}
	public String getRequestURI() {
		return requestURI;
	}
	public String getQueryString() {
		return queryString;
	}
	/**
	 * 根据给定的参数名获取该参数对应的值
	 * @param name
	 * @return
	 */
	public String getParameter(String name) {
		return parameters.get(name);
	}
	
}









