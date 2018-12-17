package com.example.kccistc.parkingarea.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kccistc.parkingarea.list.MovieListDaily;
import com.example.kccistc.parkingarea.R;
import com.example.kccistc.parkingarea.fragment.Fragment_Third;

import java.util.List;

public class MovieAdapter extends BaseAdapter {
    Fragment_Third context;
    int layout;
    List<MovieListDaily> list;

    LayoutInflater inflater;

    public MovieAdapter(Fragment_Third context, int layout, List<MovieListDaily> list){
        this.context = context;
        this.layout = layout;
        this.list = list;

        // 이부분이 안되는데 어떻게 수정해야 될까요..
        inflater = context.getActivity().getLayoutInflater();
    }

    @Override
    public int getCount() {
        return list.size();
    }

//    private String rank;    //순위
//    private String openDt; //상영 날짜
//    private String MovieNm; //영화 이름
//    private String audiAcc; //누적 관객수

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = inflater.inflate(layout,null);
        }

        TextView movieNm = view.findViewById(R.id.movieNm);
        TextView openDt = view.findViewById(R.id.openDt);
        TextView audiAcc = view.findViewById(R.id.audiAcc);

        movieNm.setText(list.get(i).getRank() + ". " + list.get(i).getMovieNm());
        openDt.setText("    상영 날짜 : " + list.get(i).getOpenDt());
        audiAcc.setText("    누적 관객수 : " + list.get(i).getAudiAcc() + "(명)");


        return view;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

}
