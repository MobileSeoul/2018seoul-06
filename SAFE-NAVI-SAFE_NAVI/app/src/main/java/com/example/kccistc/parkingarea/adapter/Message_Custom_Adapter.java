package com.example.kccistc.parkingarea.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.kccistc.parkingarea.R;

import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;

public class Message_Custom_Adapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<String> items;
    private LayoutInflater inflater;
    private int Check = -1;

    public Message_Custom_Adapter(Context context, int layout, ArrayList<String> items) {
        this.context = context;
        this.layout = layout;
        this.items = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(layout, viewGroup, false);
        }

        RadioButton radioBtn = view.findViewById(R.id.radioBtn);
        TextView radioText = view.findViewById(R.id.radioText);

        if (Check != i && Check != -1)
            radioBtn.setChecked(false);

        radioText.setText(items.get(i));

        radioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check = i;
                notifyDataSetChanged();
            }
        });
        return view;
    }

    public String getSms() {
        if (Check == -1)
            return null;
        else
            return items.get(Check);
    }
}
