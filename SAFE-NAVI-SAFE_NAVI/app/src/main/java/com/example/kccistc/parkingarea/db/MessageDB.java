package com.example.kccistc.parkingarea.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MessageDB extends SQLiteOpenHelper {
    public MessageDB(Context context) {
        super(context, "message.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE MESSAGE " +
                        "(id integer primary key autoincrement, " +
                        "name text," +
                        "phone_num text, " +
                        "contents text);");
    }

    public void insert(String name, String phone_num, String contents){
        // 전화번호와 긴급메시지 내용 등록
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("INSERT INTO MESSAGE(ID, NAME, PHONE_NUM, CONTENTS) " +
                "VALUES (NULL,?,?,?);" ,new Object[]{name, phone_num, contents});

        db.close();
    }

    public ArrayList<String> selectByName(String name){
        ArrayList<String> list = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT NAME, PHONE_NUM, CONTENTS " +
                "FROM MESSAGE WHERE NAME=?",new String[]{name});
        while (cursor.moveToNext()){
            String isExit = cursor.getString(1);
            list.add(isExit);
        }


        db.close();

        return list;
    }

    public String selectGetName(){
        String getName = null;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT NAME " +
                "FROM MESSAGE",null);

        while (cursor.moveToNext()){
            getName = cursor.getString(0);
        }


//        db.close();

        return getName;
    }

    public String selectGetPhoneNo(){
        String getName = null;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT PHONE_NUM " +
                "FROM MESSAGE",null);

        while (cursor.moveToNext()){
            getName = cursor.getString(0);
        }


//        db.close();

        return getName;
    }

    public String selectGetSms(){
        String getName = null;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT CONTENTS " +
                "FROM MESSAGE",null);

        while(cursor.moveToNext()){
            getName = cursor.getString(0);
        }


//        db.close();

        return getName;
    }

    // 업데이트
    public void update(String name, String phoneNo, String sms, String orginalName){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL(
                "UPDATE MESSAGE SET NAME=?, PHONE_NUM=?, CONTENTS=? WHERE NAME=?",
                    new String[]{name, phoneNo, sms, orginalName});

        db.close();
    }

    public void delete(){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM MESSAGE");

        db.close();
    }

    public int seletAll(){
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM MESSAGE", null);
        while (cursor.moveToNext()){
            count = cursor.getCount();
        }

//        db.close();
        return count;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
