package com.example.kccistc.parkingarea;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.kccistc.parkingarea.db.MessageDB;

import java.util.ArrayList;

public class PopupActivity extends Activity {
    Button mOnClose;
    Button mOnUpdate;

    String name;
    String phoneNo;
    String sms;

    // 현재 위치 위도, 경도 google
    String url;
    private double lat;
    private double lng;

    MessageDB db;// DB생성

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        db = new MessageDB(this);

        phoneNo = db.selectGetPhoneNo();
        sms = db.selectGetSms();

//        37.661168, 127.303704
        // 구글지도 현재위치 잡기
//        lat = ContentActivity.getLat();
//        lng = ContentActivity.getLng();
        lat = ContentActivity.getLat();
        lng = ContentActivity.getLng();
        url = "http://map.naver.com/?dlevel=8&lat="+lat+"&lng="+lng;

//        Log.e("url", url+"");


//        if(phoneNo!=null && sms!=null){
//            Log.e("phoneNo",phoneNo);
//            Log.e("sms",sms);
//        }

        mOnClose = findViewById(R.id.mOnClose);
        mOnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 팝업 '닫기' 버튼 클릭시 작동
                try {
                    //전송
//                    if(phoneNo!=null && sms!=null){
//                        Log.e("phoneNo",phoneNo);
//                        Log.e("sms",sms);
//                    }
                    // '메시지' 전송
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                    smsManager.sendTextMessage(phoneNo, null, url.toString(), null, null);
                    Toast.makeText(getApplicationContext(), "전송 완료!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "SMS faild, please try again later!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }


                //액티비티(팝업) 닫기
                finish();
            }
        });

        mOnUpdate = findViewById(R.id.mOnUpdate);
        mOnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // '수정' 버튼 클릭시
                int isUpdate = 1;
                Intent intent = new Intent(PopupActivity.this, SendMessage.class);
                intent.putExtra("isUpdate", isUpdate);
                startActivity(intent);

                finish();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

//    @Override
//    public void onBackPressed() {
//        //안드로이드 백버튼 막기
//        return;
//    }

}
