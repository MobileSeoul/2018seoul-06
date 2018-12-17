package com.example.kccistc.parkingarea.tmap;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by user on 2017-01-21.
 * WGS84GEO를 EPSG3857로 변환하는 클래스
 * HttpURLConnection 사용
 */

public class Coordinate {
    HttpURLConnection connection = null;
    String urlString = "https://api2.sktelecom.com/tmap/geo/coordconvert?";
//    https://apis.skplanetx.com/tmap/geo/coordconvert?

    public void sendingGetRequest(String latitude, String longtitude) throws Exception {  // Get 요청 메소드
        System.out.println("시작");

        String request_parameter = "version=1&" + "callback=&" + "lon=" + latitude + "&" + "lon=" + longtitude + "&fromCoord=WGS84GEO&" + "toCoord=EPSGS3857";
        URL url = new URL(urlString + request_parameter);

        System.out.println("요청 url: " + url);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // get 요청으로 설정
        connection.setRequestMethod("GET");

        System.out.println("response 읽어오기");
        // Response 읽어오기
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

        String output;
        StringBuffer response = new StringBuffer();

        while((output = br.readLine())!=null){
            response.append(output);
        }

        br.close();
        System.out.println(response.toString());
    }

}
