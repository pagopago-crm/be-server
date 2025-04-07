package com.crm.docs.common.util;

import org.springframework.stereotype.Component;

public final class FileUtil {

	private FileUtil() {}

	public static String getMimeType(String fileType){
		String resultMimeType = null;
		switch (fileType){
			case "java":
				resultMimeType = "text/x-java-source";
				break;
			case "py":
				resultMimeType = "text/x-python";
				break;
			case "js":
				resultMimeType = "application/javascript";
				break;
			case "json":
				resultMimeType = "application/json";
				break;
			case "xml":
				resultMimeType = "application/xml";
				break;
			case "html":
				resultMimeType = "text/html";
				break;
			case "css":
				resultMimeType = "text/css";
				break;
			default: resultMimeType = "text/plain";
				break;
		}

		return resultMimeType;
	}
}
