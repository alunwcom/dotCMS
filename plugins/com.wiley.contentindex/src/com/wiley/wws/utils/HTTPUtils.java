package com.wiley.wws.utils;

import java.io.ByteArrayInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.dotmarketing.util.Logger;

/**
 * Created by Gareth Wright
 * Date: 01/10/12
 */
public class HTTPUtils {
	
	public static Document postRequest(String uri, String request) {
		Document response = null; 

		try {		
			HttpClient client = new HttpClient();
			client.getParams().setParameter("http.protocol.content-charset", "UTF-8");
			PostMethod postRequest = new PostMethod(uri); 
			postRequest.setRequestBody(request);
//	    	Logger.info( HTTPUtils.class, "Request URI: " + request );
			try {
				client.executeMethod(postRequest);
				String responseStr = postRequest.getResponseBodyAsString();
//		    	Logger.info( HTTPUtils.class, "Response: " + responseStr );
				if (!StringUtils.contains(responseStr,"faultcode")) {
					SAXReader reader = new SAXReader();
					response = reader.read( new ByteArrayInputStream(responseStr.getBytes("UTF-8")));
					}
				} finally {
					postRequest.releaseConnection();
				}
			} catch (Exception e) {
		        Logger.error( HTTPUtils.class, e.getMessage());
			}
		return response;
	}
	
}
