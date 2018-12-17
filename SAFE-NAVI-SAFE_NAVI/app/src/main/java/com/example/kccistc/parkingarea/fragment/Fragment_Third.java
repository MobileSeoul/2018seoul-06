package com.example.kccistc.parkingarea.fragment;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kccistc.parkingarea.adapter.MovieAdapter;
import com.example.kccistc.parkingarea.list.MovieListDaily;
import com.example.kccistc.parkingarea.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Fragment_Third extends Fragment {
    List<MovieListDaily> list;
    ListView rankListView;
    TextView dateTV;

    int year, month, date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment__third,container,false);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-1);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        date = calendar.get(Calendar.DAY_OF_MONTH)+1;

        rankListView = view.findViewById(R.id.rankMovieView);

        new MovieTask().execute();

        return view;
    }// end main

    class MovieTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("http://www.kobis.or.kr/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json?"
                        + "key=cb436042579568eebc290db61d4b35f1&targetDt="+String.format("%04d%02d%02d",year,month,date-1));

                URLConnection conn = url.openConnection();
                InputStream is = conn.getInputStream();
                InputStreamReader isr = new InputStreamReader(is,"UTF-8");
                BufferedReader reader = new BufferedReader(isr);

                String result = "";
                while (true){
                    String data = reader.readLine();
                    if(data==null) break;

                    result += data;
                }

                list = new ArrayList<>();
                JSONObject jobj = new JSONObject(result);

                for (int i=0; i<10; i++){
                    JSONObject obj = jobj.getJSONObject("boxOfficeResult").getJSONArray("dailyBoxOfficeList").getJSONObject(i);

                    String rank = obj.getString("rank");
                    String movieNm = obj.getString("movieNm");
                    String openDt = obj.getString("openDt");
                    String audiAcc = obj.getString("audiAcc");

                    MovieListDaily movieListDaily = new MovieListDaily();
                    movieListDaily.setRank(rank);
                    movieListDaily.setMovieNm(movieNm);
                    movieListDaily.setOpenDt(openDt);
                    movieListDaily.setAudiAcc(audiAcc);
                    list.add(movieListDaily);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        MovieAdapter adapter = null;
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {
                adapter = new MovieAdapter(Fragment_Third.this,R.layout.movie_list_item,list);
            }catch (Exception e){
//                Log.e("nullException", "not link adapter");
            }

            rankListView.setAdapter(adapter);
        }
    }
}