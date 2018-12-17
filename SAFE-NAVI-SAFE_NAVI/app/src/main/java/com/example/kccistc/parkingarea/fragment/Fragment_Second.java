package com.example.kccistc.parkingarea.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.kccistc.parkingarea.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Fragment_Second extends Fragment {
    ListView rankDaumView;
    private String htmlPageUrl = "https://www.daum.net"; // 다음 홈페이지 크롤링
    private String htmlContentInStringFormat="";
    String no;
    String list;

    ArrayList<String> urlList;
    ArrayList<String> infoList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment__second,container,false);

        rankDaumView = view.findViewById(R.id.rankDaumView);

        rankDaumView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlList.get(i)));
                startActivity(intent);
            }
        });

        new JsoupAsyncTask().execute();

        return view;
    }


    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Document doc = Jsoup.connect(htmlPageUrl).get();

                Elements titles = doc.select("div.hotissue_builtin h4");
                Elements lis = doc.select("div.hotissue_builtin li");
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

                    no = lis.get(i).select("div.rank_cont:nth-child(1) > span.num_pctop:first-child > span").text();
                    list = lis.get(i).select("div.rank_cont:nth-child(1) a.link_issue:first-child").text();
//                    htmlContentInStringFormat += "[Rank. " + no + "] " + list + "\n";
                    infoList.add(no + ". " + list);
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
            try{
                adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, infoList);
            }catch(Exception e){
//                Log.e("nullException", "not link adapter");
            }


            rankDaumView.setAdapter(adapter);
        }
    }
}