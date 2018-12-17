package com.example.kccistc.parkingarea.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kccistc.parkingarea.list.CctvList;
import com.example.kccistc.parkingarea.R;

import java.util.List;

public class MyAdapter extends BaseAdapter {
    Context context;
    int layout;
    List<CctvList> list;

    LayoutInflater inflater;

    public MyAdapter(Context context, int layout, List<CctvList> list) {
        this.context = context;
        this.layout = layout;
        this.list = list;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(view == null) {
            view = inflater.inflate(layout, null);
        }

        TextView title = view.findViewById(R.id.title);
        TextView title2 = view.findViewById(R.id.title2);
        TextView title3 = view.findViewById(R.id.title3);
        TextView title4 = view.findViewById(R.id.title4);
        TextView title5 = view.findViewById(R.id.title5);
        TextView title6 = view.findViewById(R.id.title6);
        TextView title7 = view.findViewById(R.id.title7);

        /*
            private String purpose;// 설치목적구분
            private String addr;// 소재지지번주소
            private String manageOffice;// 관리기관명
            private int countCamera;// 카메라대수
            private int setDate;// 설치년월
            private String tellNum;// 관리기관전화번호
            private int dataUpdate;// 데이터기준일자
        */
        title.setText("설치목적구분 : "+ list.get(i).getPurpose());
        title2.setText("소재지지번주소 : "+ list.get(i).getAddr());
        title3.setText("관리기관명 : "+ list.get(i).getManageOffice());
        title4.setText("카메라대수 : "+ list.get(i).getCountCamera());
        title5.setText("설치년월 : "+ list.get(i).getSetDate());
        title6.setText("관리기관전화번호 : "+ list.get(i).getTellNum());
        title7.setText("데이터기준일자 : "+ list.get(i).getDataUpdate());

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
