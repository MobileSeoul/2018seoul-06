package com.example.kccistc.parkingarea;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class SafeHelp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_safe_help);

        TextView txtText = findViewById(R.id.txtText);

        Button callToService = findViewById(R.id.callToService);
        Button closeService = findViewById(R.id.closeService);

        String locationName = "";
        String telnum = "";

        if (getIntent().getStringExtra("locationName") != null) {
            locationName = getIntent().getStringExtra("locationName");
            switch (locationName) {
                case "강동구":
                    telnum = "02-3425-5009";
                case "강북구":
                    telnum = "02-901-6693";
                case "성동구":
                    telnum = "02-2286-6262";
                case "용산구":
                    telnum = "02-2199-6300";
                case "양천구":
                    telnum = "02-2620-3399";
                case "도봉구":
                    telnum = "02-2091-3109";
                case "금천구":
                    telnum = "02-2627-2414";
                case "중랑구":
                    telnum = "02-2094-1148";
                case "구로구":
                    telnum = "02-860-2525";
                default:
                    telnum = "120";
            }

            txtText.setText("현재위치인 " + locationName + "로 서비스를 신청하시겠습니까?");
        }

        final String finalTelnum = telnum;
        callToService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + finalTelnum));
                startActivity(intent);
                finish();
            }
        });

        closeService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }
}
