package com.example.kccistc.parkingarea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Network {
	public static String getData(String address) {
		StringBuffer result = new StringBuffer();
		
		try {
			URL url = new URL(address);
			URLConnection conn = url.openConnection();

			InputStream in = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader reader = new BufferedReader(isr);

//			String result = "";
			
			while (true) {
				String data = reader.readLine();
				if (data == null)
					break;
//				result = result + data;
				result.append(data + "\n");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();
	}
	
	// Shift + Ctrl + O : 전체 import
	public static void main(String[] args) {
		try {
			URL url = new URL("http://nlotto.co.kr/gameResult.do?method=byWin&drwNo=1");
			URLConnection conn = url.openConnection();

			InputStream in = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader reader = new BufferedReader(isr);

//			String result = "";
			StringBuffer result = new StringBuffer();
			while (true) {
				String data = reader.readLine();
				if (data == null)
					break;
//				result = result + data;
				result.append(data + "\n");
			}
//			System.out.println(result);
			
			int sIdx = result.indexOf("<p class=\"number\">");
			int eIdx = result.indexOf("</p>", sIdx);
			
			String p = result.substring(sIdx, eIdx);
			System.out.println(p);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}