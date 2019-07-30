package com.webserver.context;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 所有与HTTP协议规定的内容
 * @author ta
 *
 */
public class HttpContext {
	/**
	 * 保存所有资源后缀名与Content-Type对应的值
	 * key:资源后缀名，如:png
	 * value:Content-Type对应的值，如:image/png
	 */
	private static Map<String,String> mime_mapping = new HashMap<>();
	
	static {
		initMimeMapping();
	}
	/**
	 * 初始化mime_mapping
	 */
	private static void initMimeMapping() {
//		mime_mapping.put("png", "image/png");
//		mime_mapping.put("gif", "image/gif");
//		mime_mapping.put("jpg", "image/jpeg");
//		mime_mapping.put("css", "text/css");
//		mime_mapping.put("html", "text/html");
//		mime_mapping.put("js", "application/javascript");
		/*
		 * 使用dom4j解析conf/web.xml文件
		 * 将根标签下所有名为<mime-mapping>的子
		 * 标签获取回来，然后将该标签中的子标签:
		 * <extension>中的文本作为key
		 * <mime-type>中的文本作为value
		 * 保存到mime_mapping这个Map中完成初始化
		 */
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new File("conf/web.xml"));
			Element root = doc.getRootElement();
			List<Element> list = root.elements("mime-mapping");
			for(Element e : list) {
				String key = e.elementTextTrim("extension");
				String value = e.elementTextTrim("mime-type");
				mime_mapping.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(mime_mapping.size());
	}
	/**
	 * 根据资源后缀名获取对应的Content-Type的值
	 * @param ext
	 * @return
	 */
	public static String getMimeType(String ext) {
		return mime_mapping.get(ext);
	}
	
	public static void main(String[] args) {
		String type = getMimeType("png");
		System.out.println(type);
	}
}






