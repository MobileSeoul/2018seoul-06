package com.example.kccistc.parkingarea.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kccistc.parkingarea.list.NewsList;
import com.example.kccistc.parkingarea.R;

import java.util.List;

public class NewsAdapter extends BaseAdapter {

    Context context;
    List<NewsList> list;
    int layout;
    LayoutInflater inflater;

    public NewsAdapter(Context context, int layout, List<NewsList> list){
        this.context = context;
        this.list = list;
        this.layout = layout;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        if(view == null) {
            view = inflater.inflate(layout, null);
        }

        TextView publishedAt = view.findViewById(R.id.publishedAt);
        TextView title = view.findViewById(R.id.title);
        TextView channelTitle = view.findViewById(R.id.channelTitle);
        ImageView newsImg = view.findViewById(R.id.newsImg);

        Glide.with(context).load(list.get(i).getUrl().toString()).into(newsImg);
        publishedAt.setText(list.get(i).getPublishedAt());
        title.setText(list.get(i).getTitle());
        channelTitle.setText(list.get(i).getChannelTitle());

        return view;
    }

    @Override
    public int getCount() {
        return 2;
    }



    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
