package com.example.kccistc.parkingarea.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class LocationDB extends SQLiteOpenHelper {
    public LocationDB(Context context) {
        super(context, "location.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE LOCATION (id integer primary key autoincrement, DESTINATION text);");
    }

    // 목적지 설정
    public void insert(String destination){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("INSERT INTO LOCATION(ID, DESTINATION) VALUES(NULL, ?);", new Object[]{ destination});

        db.close();
    }

    public void delete(){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM LOCATION");

        db.close();
    }

    // 설정해 둔 목적지 검색
    public String selectByDestination(){
        String destination = "";
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT DESTINATION FROM LOCATION", null);
//        Log.e("bbb","test");
        while(cursor.moveToNext()){
            destination = cursor.getString(0);
//            Log.e("bbb",cursor.getString(0)+"");
        }
        db.close();
        return destination;
    }

    // 목적지 수정
    public void update(String destination, String orginalDest){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("UPDATE LOCATION SET DESTINATION=? WHERE DESTINATION = ?", new Object[]{destination, orginalDest});

        db.close();
    }

    // DB에 내용이 있는지 확인
    public int isSelect(){
        int count=0;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM LOCATION",null);

        while (cursor.moveToNext())
            count = cursor.getCount();

        db.close();
        return count;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
