package com.sp.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;

@Service("common.xmlSerializer")
public class XMLSerializer {
	// XML을 JSON으로 변환
	/*
		<dependency>
		    <groupId>org.json</groupId>
		    <artifactId>json</artifactId>
		    <version>20180813</version>
		</dependency> 
	 */
	
	public String xmlToJson(String spec) {
		// XML 문서를 String 형태의 JSON으로 변환(공공 API 등에서 활용)
		
		String resultJSON = null;
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection)new URL(spec).openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			
			StringBuilder sb = new StringBuilder();
            String s;
            while ((s=br.readLine()) != null) {
                sb.append(s);
            }
            JSONObject job = XML.toJSONObject(sb.toString());
            resultJSON = job.toString();
            // resultJSON = job.toString(4); // 4 : 들여쓰기 값
            System.out.println(resultJSON);
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			if(conn!=null) {
				try {
					conn.disconnect();
				} catch (Exception e2) {
				}
			}
		}
		
		return resultJSON;
	}
	
	public String xmlToString(String spec) {
		// XML 문서를 String 형태의 XML로 변환(공공 API 등에서 활용)
		
		String resultXML = null;
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection)new URL(spec).openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			
			StringBuilder sb = new StringBuilder();
            String s;
            while ((s=br.readLine()) != null) {
                sb.append(s);
            }
            resultXML= sb.toString();
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			if(conn!=null) {
				try {
					conn.disconnect();
				} catch (Exception e2) {
				}
			}
		}
		
		return resultXML;
	}
}
