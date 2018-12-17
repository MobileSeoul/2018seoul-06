package com.example.kccistc.parkingarea.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kccistc.parkingarea.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Fragment_First extends Fragment {
    // ListView rankView;
    ListView rankView;
    private String htmlPageUrl = "https://www.naver.com"; // 네이버 홈페이지 크롤링
    private String htmlContentInStringFormat="";
    String no;
    String list;

    ArrayList<String> urlList;
    ArrayList<String> infoList;

//    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment__first,null);

        rankView = view.findViewById(R.id.rankView);

        // ========================= 네이버 실검 순위 가져오기 =======================

//        rankView.setMovementMethod(new ScrollingMovementMethod());// 스크롤이 가능한 텍스트뷰로 만들기



        Fragment_First.JsoupAsyncTask jsoupAsyncTask = new Fragment_First.JsoupAsyncTask();
        jsoupAsyncTask.execute();

        rankView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlList.get(i)));
                startActivity(intent);
            }
        });


//        // ============== 실검 새로고침하기 소스 ===============
//        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//
//            }
//        });
//
//        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);


        return view;
    }

    // ============= 네이버 실검 크롤링 ===============
    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(htmlPageUrl).get();

                Elements titles = doc.select("h3.ah_ltit");
                Elements lis = doc.select("div.PM_CL_realtimeKeyword_list_base li.ah_item");
//                Elements rankNo = doc.select("a.ah_a span.ah_r");
//                Elements rankList = doc.select("a.ah_a span.ah_k");
                for(Element e : titles){
                    String ms = e.text();
                    //타이틀 가져오기
                    htmlContentInStringFormat += e.text().trim()+"\n";
                }

                urlList = new ArrayList<>();
                infoList = new ArrayList<>();

                for(int i=0; i< lis.size(); i++){
                    String url = lis.get(i).select("a").attr("href");
//                    Log.e("dddd", url);

                    urlList.add(url);

                    no = lis.get(i).select("span.ah_r").text();
                    list = lis.get(i).select("span.ah_k").text();
//                    htmlContentInStringFormat += "[Rank. " + no + "] " + list + "\n";
                    infoList.add(no + "위. " + list);
                }


//                Log.e("listlist", urlList.toString());
//                Log.e("listlist", urlList.size() + "");


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        ArrayAdapter<String> adapter;
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            rankView.setText(htmlContentInStringFormat);
            try {
                adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, infoList);
            }catch (Exception e){
//                Log.e("nullException", "not link adapter");
            }


            rankView.setAdapter(adapter);
        }
    }
}
