package com.example.kccistc.parkingarea.db;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DB {

    private String DATABASE_NAME = null;
    private String TABLE_NAME = "seoul";
    private int DATABASE_VERSION = 1;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private String filePath;
    private String fileName;
    private Context context;
    private static DB isDB;

    public static DB setDB() {
        if (isDB == null)
            isDB = new DB();
        return isDB;
    }


    public void isCheckDB(Context context, String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.context = context;


        File file = new File(this.filePath + "/" + fileName);
        File folder = new File(this.filePath);
        if (file.isFile()) {
            if (openDatabase()) {
                println("성공");
            } else
                println("실패");
        } else {
            folder.mkdirs();
            CopyDB();
            if (openDatabase()) {
                println("성공");
            } else
                println("실패");
        }
    }

    public void CopyDB() {
        AssetManager assetManager = context.getResources().getAssets();

        File outfile = new File(filePath + "/" + fileName);

        InputStream is = null;

        FileOutputStream fo = null;

        long filesize = 0;

        try {

            is = assetManager.open(fileName, AssetManager.ACCESS_BUFFER);
            filesize = is.available();

            if (outfile.length() <= 0) {
                byte[] tempdata = new byte[(int) filesize];
                is.read(tempdata);
                is.close();
                outfile.createNewFile();
                fo = new FileOutputStream(outfile);
                fo.write(tempdata);
                fo.close();
            }
        } catch (IOException e) {
        }
    }

    private boolean openDatabase() {
        DATABASE_NAME = filePath + "/" + fileName;
        println("opening database [" + DATABASE_NAME + "].");

        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getReadableDatabase();

        return true;
    }

    public static class CCTV {
        String addr;
        String manageOffice;
        String tellNum;
        double latitude;
        double longitude;

        public CCTV(){}
        public CCTV(String addr,
             String manageOffice,
             String tellNum,
             double latitude,
             double longitude) {
            this.addr = addr;
            this.manageOffice = manageOffice;
            this.tellNum = tellNum;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getAddr() {
            return addr;
        }

        public String getManageOffice() {
            return manageOffice;
        }

        public String getTellNum() {
            return tellNum;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    public ArrayList searchCCTV(String search) {
        ArrayList<CCTV> cctvs = null;

        println("\nexecuteRawQueryParam called.\n");

        cctvs = new ArrayList<>();

        String SQL = "select * "
                + " from " + TABLE_NAME
                + " where addr like '%" + search + "%'";

        String[] args = {};

        Cursor c1 = db.rawQuery(SQL, args);


        for (int i = 0; i < c1.getCount(); i++) {
            c1.moveToNext();

            cctvs.add(new CCTV(
                    c1.getString(2), c1.getString(3) , c1.getString(5),
                    Double.parseDouble(c1.getString(7)), Double.parseDouble(c1.getString(8))));
        }

        c1.close();
        return cctvs;

    }


    public ArrayList navirouteCCTV(double lat1, double lat2, double lng1, double lng2) {
        ArrayList<CCTV> CCTVs = null;

        println("\nexecuteRawQueryParam called.\n");

        CCTVs = new ArrayList<>();

        String SQL = "select * "
                + " from " + TABLE_NAME
                + " where latitude between " + lat1 + " and " + lat2 + " and longitude between " + lng1 + " and " + lng2;

        String[] args = {};
//Log.e("aaa","SQL: "+SQL);
        Cursor c1 = db.rawQuery(SQL, args);


        for (int i = 0; i < c1.getCount(); i++) {
            c1.moveToNext();

            CCTVs.add(new CCTV(
                    c1.getString(2), c1.getString(3) , c1.getString(5),
                    Double.parseDouble(c1.getString(7)), Double.parseDouble(c1.getString(8))));

        }

        c1.close();
        return CCTVs;
    }


    private void println(String msg) {
        Log.d("DB", msg);
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
//            println("creating table [" + TABLE_NAME + "].");
//
//            try {
//                String DROP_SQL = "drop table if exists " + TABLE_NAME;
//                db.execSQL(DROP_SQL);
//            } catch(Exception ex) {
//                Log.e(TAG, "Exception in DROP_SQL", ex);
//            }
//
//            String CREATE_SQL = "create table " + TABLE_NAME + "("
//                    + " _id integer PRIMARY KEY autoincrement, "
//                    + " name text, "
//                    + " age integer, "
//                    + " phone text)";
//
//            try {
//                db.execSQL(CREATE_SQL);
//            } catch(Exception ex) {
//                Log.e(TAG, "Exception in CREATE_SQL", ex);
//            }
//
//            println("inserting records.");
//
//            try {
//                db.execSQL( "insert into " + TABLE_NAME + "(name, age, phone) values ('John', 20, '010-7788-1234');" );
//                db.execSQL( "insert into " + TABLE_NAME + "(name, age, phone) values ('Mike', 35, '010-8888-1111');" );
//                db.execSQL( "insert into " + TABLE_NAME + "(name, age, phone) values ('Sean', 26, '010-6677-4321');" );
//            } catch(Exception ex) {
//                Log.e(TAG, "Exception in insert SQL", ex);
//            }

            // ====Send SMS====

            db.execSQL(
                    "CREATE TABLE MESSAGE " +
                            "(id integer primary key autoincrement, " +
                            "name text," +
                            "phone_num text, " +
                            "contents text);");
        }

        // ====Send SMS==== start
        public void insert(String name, String phone_num, String contents) {
            // 전화번호와 긴급메시지 내용 등록
            SQLiteDatabase db = getWritableDatabase();

            db.execSQL("INSERT INTO MESSAGE(ID, NAME, PHONE_NUM, CONTENTS) " +
                    "VALUES (NULL,?,?,?);", new Object[]{name, phone_num, contents});

            db.close();
        }

        public ArrayList<String> selectByName(String name) {
            ArrayList<String> list = new ArrayList<>();

            SQLiteDatabase db = getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT NAME, PHONE_NUM, CONTENTS " +
                    "FROM MESSAGE WHERE NAME=?", new String[]{name});
            while (cursor.moveToNext()) {
                String isExit = cursor.getString(1);
                list.add(isExit);
            }


            db.close();

            return list;
        }// ====Send SMS==== end


        public void onOpen(SQLiteDatabase db) {
            println("opened database [" + DATABASE_NAME + "].");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("DB", "Upgrading database from version " + oldVersion + " to " + newVersion + ".");
        }
    }
}