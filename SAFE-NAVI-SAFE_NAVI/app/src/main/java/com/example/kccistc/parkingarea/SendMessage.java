package com.example.kccistc.parkingarea;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kccistc.parkingarea.db.MessageDB;

public class SendMessage extends AppCompatActivity {
    Button sendBtn;

    EditText editTextName;
    EditText editTextPhoneNo;
    EditText editTextSMS;

    RadioButton radioBtn1;
    RadioButton radioBtn2;
    RadioButton radioBtn3;

    TextView messageDefault1;
    TextView messageDefault2;
    TextView messageDefault3;

    private String isName;
    private String isPhoneNo;
    private String isSms;

    MessageDB db;

    int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        sendBtn = findViewById(R.id.sendBtn);
        editTextName = findViewById(R.id.editTextName);
        editTextPhoneNo = findViewById(R.id.editTextPhoneNo);
        editTextSMS = findViewById(R.id.editTextSMS);

        radioBtn1 = findViewById(R.id.radioBtn1);
        radioBtn2 = findViewById(R.id.radioBtn2);
        radioBtn3 = findViewById(R.id.radioBtn3);

        messageDefault1 = findViewById(R.id.messageDefault1);
        messageDefault2 = findViewById(R.id.messageDefault2);
        messageDefault3 = findViewById(R.id.messageDefault3);

        db = new MessageDB(this);

        // '수정' 버튼을 클릭했을 때 실행
        Intent intent = getIntent();
        int isUpdate = intent.getIntExtra("isUpdate",0);
        if(isUpdate == 1){
            sendBtn.setText("수정하기");
            editTextName.setText(db.selectGetName());
            editTextPhoneNo.setText(db.selectGetPhoneNo());
            editTextSMS.setText(db.selectGetSms());

            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 수정 버튼 클릭시
                    String newName = editTextName.getText().toString();
                    String newPhoneNo = editTextPhoneNo.getText().toString();
                    String newSms = editTextSMS.getText().toString();
                    String orginalName = db.selectGetName();
//                    Log.e("newName", newName);
//                    Log.e("newPhoneNo", newPhoneNo);
//                    Log.e("newSms", newSms);
//                    Log.e("dbName", orginalName);

                    db.update(newName, newPhoneNo, newSms, orginalName);

                    Toast.makeText(SendMessage.this, "긴급메시지를 수정하였습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else{
            // '등록하기' 버튼 클릭시
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // permission check
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//
//                        if (checkSelfPermission(Manifest.permission.SEND_SMS)
//                                == PackageManager.PERMISSION_DENIED) {
//
//                            Log.d("permission", "permission denied to SEND_SMS - requesting it");
//                            String[] permissions = {Manifest.permission.SEND_SMS};
//
//                            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
//
//                        }
//                    }

                    isName = editTextName.getText().toString();
                    isPhoneNo = editTextPhoneNo.getText().toString();
                    isSms = editTextSMS.getText().toString();

                    db.insert(isName, isPhoneNo, isSms);

//                    Log.e("name", db.selectGetName());
//                    Log.e("phoneNo", db.selectGetPhoneNo());
//                    Log.e("sms", db.selectGetSms());

                    Toast.makeText(SendMessage.this, "긴급메시지를 등록하였습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        radioBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioBtn2.setChecked(false);
                radioBtn3.setChecked(false);

                editTextSMS.setText(messageDefault1.getText());
            }
        });

        radioBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioBtn1.setChecked(false);
                radioBtn3.setChecked(false);

                editTextSMS.setText(messageDefault2.getText());
            }
        });

        radioBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioBtn1.setChecked(false);
                radioBtn2.setChecked(false);

                editTextSMS.setText(messageDefault3.getText());
            }
        });
    }
}
