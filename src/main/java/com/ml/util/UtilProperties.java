package com.ml.util;

import java.io.*;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * 读取Properties文件
 * 
 * @Author ZZY
 */
public class UtilProperties {

	/**
	 * 默认去src下的config.properties的值
	 * 
	 * @return
	 */
	public static String getConfig(String key) {
		Properties properties = new Properties();
		// 使用ClassLoader加载properties配置文件生成对应的输入流
		InputStream in = UtilProperties.class.getClassLoader().getResourceAsStream("application.properties");
		// 使用properties对象加载输入流
		try {
			properties.load(in);
			// 获取key对应的value值
			return properties.getProperty(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 *
	 * @param src Properties 文件地址和文件名字
	 * @param key 要读取的key
	 * @return value 返回值
	 */
	public static String readProperties1(String src, String key) {
		Properties properties = new Properties();
		// 使用ClassLoader加载properties配置文件生成对应的输入流
		InputStream in = UtilProperties.class.getClassLoader().getResourceAsStream(src);
		// 使用properties对象加载输入流
		try {
			properties.load(in);
			// 获取key对应的value值
			return properties.getProperty(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String readProperties2(String src, String key) {
		Properties properties = new Properties();
		// 使用InPutStream流读取properties文件
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader("E:/config.properties"));
			properties.load(bufferedReader);
			// 获取key对应的value值
			return properties.getProperty(key);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public static String readProperties3(String src, String key) {
		try {
			// config为属性文件名，放在包com.test.config下，如果是放在src下，直接用config即可
			// properties.load(bufferedReader);
			ResourceBundle resource = ResourceBundle.getBundle(src);
			// 获取key对应的value值
			return resource.getString("key");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 根据 Properties 配置文件的 键 读取 值
	 * 
	 * @param fileName     配置文件的名字
	 * @param filePath     配置文件的地址 null 和 "" 认为是src 目录下
	 * @param propertyName 要获取值的 键
	 * @return String
	 */
	public static String getPropertyValueByName(String fileName, String filePath, String propertyName) {
		// 获取word配置文件
		Properties p = new Properties();
		InputStream is;
		String basePath = "";
		// 这样也能取到scr的目录
		// String basePath = request.getSession().getServletContext().getRealPath("/");
		// +/WEB-INF/classes/
		if (filePath == null || "".equals(filePath)) {
			if (Thread.currentThread().getContextClassLoader().getResource("/") != null) {
				basePath = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
			} else {
				// 说明没有启动项目测试
				String localPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
				// 这里拼接 一个src 目录下的地址 根据需要自己 拼接
				String s = "/ml";// 自己的项目目录
				basePath = localPath.split("/")[1] + "/" + localPath.split("/")[2] + "/" + localPath.split("/")[3] + s
						+ "/src";
				System.out.println(basePath);
				// 不拼接的话 配置文件 要放到 项目的 根目录 不是src下 而是 src 同级
				// readPath = fileName;
			}
		} else {
			basePath = filePath;
		}
		try {
			// 获取配置文件
			is = new FileInputStream(basePath + "/" + fileName);
			p.load(is);
			is.close();
		} catch (FileNotFoundException e) {
			System.out.println("未找到" + basePath + "目录下的：" + fileName + "配置文件");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO异常");
			e.printStackTrace();
		}
		String value = p.getProperty(propertyName);
		return value;
	}

}
