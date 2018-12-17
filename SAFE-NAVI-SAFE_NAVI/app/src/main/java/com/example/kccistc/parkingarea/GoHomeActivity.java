package com.example.kccistc.parkingarea;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kccistc.parkingarea.db.DB;
import com.example.kccistc.parkingarea.db.LocationDB;
import com.example.kccistc.parkingarea.db.MessageDB;
import com.example.kccistc.parkingarea.tmap.CCTV;
import com.example.kccistc.parkingarea.tmap.Coordinate;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GoHomeActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback, LocationListener, TMapView.OnCalloutRightButtonClickCallback, NavigationView.OnNavigationItemSelectedListener {
    private Coordinate coordinate;
    private List<CCTV> cctvs;   // cctv 가져온 정보 저장할 List
    private LocationManager locationManager;
    private Context mContext;
    private Toolbar toolbar;

    TMapData tmapdata;
    TMapView tmapview;

    ImageView backimg;
    ImageView compass_button;
    ImageView current_button;      //현재위치 버튼

    Button cctv_button;       //cctv 보기 버튼
    Button navi_button;         //길찾기 버튼

    ImageButton start_button;
    ImageButton end_button;

    TextView start_edit;
    EditText end_edit;

    //현재위치 위도,경도 저장할 변수
    private double current_lat = 0;
    private double current_lon = 0;

    //출발지 이름, 위도, 경도 저장할 변수
    private String start_name = null;
    private double start_lat = 0;
    private double start_lon = 0;

    //도착지 이름, 위도, 경도 저장할 변수
    private String end_name = null;
    private double end_lat = 0;
    private double end_lon = 0;

    private boolean compass_count = false;  //나침반 버튼을 스위치처럼 사용하기 위한 변수
    private boolean current_count = false;  //현재위치 버튼을 스위치처럼 사용하기 위한 변수
    private boolean navi_count = false;     //길찾기 버튼을 스위치처럼 사용하기 위한 변수
    private boolean cctv_count = false;     //CCTV 버튼을 스위치처럼 사용하기 위한 변수

    Timer mTimer = new Timer();       //현재위치 잡기 위한 타이머
    Timer trackingTimer;              //트래킹 모드
    TimerTask trackingTask;

    private double time; //거리시간 계산
    TextView dis_time; // 거리계산 보여주기 위한 변수

    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_LOCATION = 2;

    //카메라 접근
    private Camera camera;
    boolean isOnCamera = false;

    private double distances;  // 거리
    private String getDistance;

    LocationDB db; // 목적지 넣을 DB 생성
    String end = null;

    // 도착지 변수
    String getName;
    double getlat;
    double getlon;

    // 트래킹 모드 체크
    boolean isTracking = false;

    //CCTV 체크
    boolean isCCTV = false;

    // 타이머 변수
    CountDownTimer countDownTimer;
    private int countTimer;
    private boolean isClick = true;

    String search_name = null;

    // 현재위치 지도
    private String url;

    //현재위치 변할 때마다 잡아줌
    @Override
    public void onLocationChange(Location location) {
//        current_lat = getLat();
//        current_lon = getLng();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gohome_main);
        setTitle("집으로");

        // 화면 안꺼지게 하기
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey("ab19368d-24d3-4c4a-9348-a5e4ec1834af");

        tmapdata = new TMapData();

        current_lat = ContentActivity.getLat();
        current_lon = ContentActivity.getLng();

        url = "http://map.naver.com/?dlevel=8&lat="+current_lat+"&lng="+current_lon;


        db = new LocationDB(this);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());

        compass_button = (ImageView) findViewById(R.id.compass_button);
        current_button = (ImageView) findViewById(R.id.current_button);
        cctv_button = (Button) findViewById(R.id.cctv_button);
        navi_button = (Button) findViewById(R.id.navi_button);
        start_button = (ImageButton) findViewById(R.id.start_button);
        end_button = (ImageButton) findViewById(R.id.end_button);
        start_edit = findViewById(R.id.start_edit);
        end_edit = (EditText) findViewById(R.id.end_edit);
        dis_time = findViewById(R.id.dis_time); // 거리계산 보여주기


        // ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //CCTV ON-OFF
        FloatingActionButton cctv_onoff = findViewById(R.id.cctv_onoff);
        cctv_onoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navi_count & !isCCTV) {
                    cctvOn(tmapview);
                    isCCTV = true;
                }
                else if(navi_count & isCCTV) {
                    tmapview.removeAllMarkerItem();
                    isCCTV = false;
                }
//                Log.e("aaa","navi_count: "+navi_count+" isCCTV: "+isCCTV);
            }
        });

        // 트래킹모드 버튼
        FloatingActionButton trackingMode = findViewById(R.id.trackingMode);
        trackingMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTracking) {
                    trackingTask = new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                current_lat = ContentActivity.getLat();
                                current_lon = ContentActivity.getLng();
                                current(tmapview);
                                tmapview.setCenterPoint(current_lon, current_lat);
                            } catch (Exception e) {
                                Toast.makeText(GoHomeActivity.this, "오류가 발생하였습니다!", Toast.LENGTH_LONG).show();
                            }
                        }
                    };

                    trackingTimer = new Timer();

                    trackingTimer.schedule(trackingTask, 0, 1000);

                    isTracking = true;
                } else {
                    trackingTimer.cancel();
                    tmapview.removeMarkerItem("current_point");
                    tmapview.setSightVisible(false);
                    isTracking = false;
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera = Camera.open();
                if (!isOnCamera) {
                    Snackbar.make(view, "라이트 켜짐", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Camera.Parameters param = camera.getParameters();
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(param);
                    camera.startPreview();

                    isOnCamera = true;
                } else {
                    Snackbar.make(view, "라이트 꺼짐", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Camera.Parameters param = camera.getParameters();
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(param);
                    camera.startPreview();

                    isOnCamera = false;
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // end Toolbar

        //위치 액세스 권한 넣어주기
        int permissionCheck1 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);

        //승인되면 보여줌
        if (permissionCheck1 == PackageManager.PERMISSION_GRANTED) {

            ConnectivityManager con_manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobile = con_manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifi = con_manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            //인터넷 연결 안되어있으면 토스트창으로 띄워주기
            if (wifi.isConnected() || mobile.isConnected()) {

            } else {
                Toast.makeText(this, "인터넷을 연결해주세요", Toast.LENGTH_SHORT).show();
            }

            FrameLayout framelayout = (FrameLayout) findViewById(R.id.map_view);


            tmapview.setCenterPoint(current_lon, current_lat, true);

            framelayout.addView(tmapview);


            tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
            tmapview.setIconVisibility(false);
            tmapview.setZoomLevel(14);
            tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
            tmapview.setTrackingMode(true);    //트래킹 모드 없음
            tmapview.setCompassMode(false);   //나침반 모드 해제
            tmapview.setTMapLogoPosition(TMapView.TMapLogoPositon.POSITION_BOTTOMLEFT);

            //현재위치 탐색 시작
            TMapGpsManager gps = new TMapGpsManager(GoHomeActivity.this);
            gps.setMinTime(1000);       //위치변경 인식 최소시간을 설정
            gps.setMinDistance(5);      //위치변경 인식 최소거리를 설정
            gps.setProvider(gps.GPS_PROVIDER);  //위치탐색 타입을 설정(GPS_PROVIDER는 위성기반의 위치탐색)
            gps.setLocationCallback();  //현재 위치상태 변경시 호출되는 콜백인터페이스 설정하고 성공여부를 반환
            gps.OpenGps();

            if (current_lon != 0 && current_lat != 0) {
                tmapview.setLocationPoint(current_lat, current_lon);
            }

            //출발지 검색
//            start_button.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View arg0) {
//                    search_start(tmapview);
//
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(start_button.getWindowToken(), 0);
//                }
//            });

//            search_end(tmapview);

            //도착지 검색
            end_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    search_end(tmapview);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(end_button.getWindowToken(), 0);
                }
            });

//            start_edit.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View arg0) {
//                    start_edit.setTextColor(Color.BLACK);
//                    start_name = null;
//                }
//            });
            start_edit.setTextColor(Color.BLUE); // <== 변경

            end_edit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    end_edit.setTextColor(Color.BLACK);
                    end_name = null;
                }
            });

            //나침반 모드 설정 및 해제
            compass_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {

                    //compass가 false로 되어있는 경우(= 안켜져있는 경우)
                    if (compass_count == false) {
                        tmapview.setCompassMode(true);
                        compass_count = true;
                    }
                    //compass가 true로 되어있는 경우
                    else {
                        tmapview.setCompassMode(false);
                        compass_count = false;
                    }
                }
            });

            //현재위치 잡기
            current_button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {

                    //current_count가 false로 되어있는 경우(= 안켜져있는 경우)
                    if (current_count == false) {

                        Toast.makeText(GoHomeActivity.this, "현재위치 잡는중", Toast.LENGTH_LONG).show();

                        long start_time = System.currentTimeMillis();
                        long wait_time = 5000;
                        long end_time = start_time + wait_time;

                        current_lat = ContentActivity.getLat();
                        current_lon = ContentActivity.getLng();

                        while (System.currentTimeMillis() < end_time) {
                            if (current_lat != 0 && current_lon != 0) {
                                tmapview.setCenterPoint(current_lon, current_lat, true);   //화면 중심으로 이동시킴
                                current_count = true;
                                break;
                            }
                        }


                        //5초동안 돌렸는데 아직 현재위치 못잡은 경우에 토스트 함수로 띄워줌
                        if (current_lat != 0 || current_lon != 0) {

                            TimerTask task = new TimerTask() {
                                @Override
                                public void run() {
                                    try {
                                        current(tmapview);
                                    } catch (Exception e) {
                                        Toast.makeText(GoHomeActivity.this, "오류가 발생하였습니다!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            };

                            mTimer = new Timer();

                            mTimer.schedule(task, 1000, 1000);
                        } else {
                            Toast.makeText(GoHomeActivity.this, "아직 현재위치가 잡히지 않았습니다.\n다시 시도해주세요!", Toast.LENGTH_LONG).show();
                        }
                    }//if(current_count==false)

                    //현재위치 버튼 켜져있는거 끄려고 할 때
                    else {
                        mTimer.cancel();    //타이머 취소
                        mTimer = null;     //타이머 초기화

                        Toast.makeText(GoHomeActivity.this, "현재위치 모드를 종료합니다!", Toast.LENGTH_LONG).show();

                        tmapview.removeMarkerItem("current_point"); //마커 삭제
                        tmapview.setTrackingMode(false);    //트래킹 없애기
                        tmapview.setSightVisible(false);     //시야 삭제

                        current_count = false;              //버튼 꺼져있는 걸로 설정하기
                    }
                }
            });


//            db.delete();

            //길찾기
            navi_button.setOnClickListener(new View.OnClickListener() {
                //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(View arg0) {
//                    String start = null;

//                    start = start_edit.getText().toString();
                    end = end_edit.getText().toString();
//                    Log.e("end", end);

                    // '도착지'를 설정하고 DB에 저장
                    int isExit = db.isSelect();
                    if (isExit == 0) {
                        db.insert(end);
//                        //Log.e("aaa", "저장");
                    }
//                    else
//                        Log.e("isExit", "'도착지'는 이미 DB에 등록되어있습니다.");


                    if (navi_count == false && end != null && end_edit.length() != 0) {
                        navigation(tmapview);
                        navi_button.setText("도착지 종료");
                        navi_count = true;
                        isCCTV = true;
//                        cctvOn(tmapview);
//                        cctv_button.setText("CCTV 끄기");
                    } else {
                        if (end_edit.length() != 0) {
                            if(isTracking)
                                trackingTimer.cancel();

                            isCCTV = false;
                            tmapview.removeAllTMapCircle();     //원 삭제
                            tmapview.removeAllMarkerItem();     //CCTV 마커 삭제
                            tmapview.removeTMapPath();          //길찾기 경로 삭제
                            tmapview.setMapPosition(TMapView.POSITION_NAVI);
                            //navi_time.setVisibility(View.GONE);    //걸리는 거리, 시간 안보이게 처리
                            showMarker1(tmapview, "현재 내 위치", current_lat, current_lon);
                            showMarker2(tmapview, end_name, end_lat, end_lon);
                            navi_button.setText("길찾기");
                            navi_count = false;
                            cctv_count = false;
//                        cctv_button.setText("CCTV 보기");
                            navi_button.setText("도착지 설정");
                        }
                        Toast.makeText(getApplicationContext(), "목적지를 설정해주세요!", Toast.LENGTH_LONG).show();
                    }
                }
            });

            // '길찾기 수정'버튼 클릭시
            Button updateBtn = findViewById(R.id.updateBtn);
            updateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String newDest = end_edit.getText().toString();
                        db.update(newDest, db.selectByDestination());
                        Toast.makeText(getApplicationContext(), "도착지가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "도착지 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }

                }
            });


            // '도착지'가 설정되어있으면 텍스트뷰에 입력
            db = new LocationDB(this);
            String dest = db.selectByDestination();
//            Log.e("aaa","destination:"+ dest.toString());
            if (dest != null) {

                end_edit.setText(dest);
                end_edit.clearFocus();
                search_end(tmapview);
                end_edit.setTextColor(Color.BLUE);
//                Log.e("길이", end_edit.getText().length() + "");

            }


            //롱클릭 메소드
            tmapview.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
                @Override
                public void onLongPressEvent(ArrayList<TMapMarkerItem> markerItems, ArrayList<TMapPOIItem> poilist, TMapPoint point) {

                    System.out.println("롱 클릭 이벤트 발생");

                    final TMapPoint long_point = point;

                    try {
                        String point_address = tmapdata.convertGpsToAddress(long_point.getLatitude(), long_point.getLongitude());   //위도, 경도 값을 주소로 반환
                        showMarker3(tmapview, point_address, point.getLatitude(), point.getLongitude());
                    } catch (Exception e) {
                        System.out.println("오류발생 : " + e);
                        Toast.makeText(GoHomeActivity.this, "오류가 발생하였습니다!", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

        //권한 거부했을때
        else {
            //거부 다음 동작 정의해야
            ActivityCompat.requestPermissions(GoHomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
        }
    }//onCreate 끝

    //현재위치 함수
    public void current(TMapView tmapView) {

        //마커 만들기 시작
        TMapPoint cpoint = new TMapPoint(current_lat, current_lon);      //현재위치로 표시될 좌표의 위도와 경도를 설정
        TMapMarkerItem cItem = new TMapMarkerItem();                        //마커아이템 만들기

        cItem.setTMapPoint(cpoint);                                          //마커에 포인트 설정
        cItem.setName("현재 위치");                                         //이름 셋
        cItem.setVisible(TMapMarkerItem.VISIBLE);                           //보이게하기

        Bitmap current_flag = BitmapFactory.decodeResource(getResources(), R.drawable.current_point);    //마크에 쓸 이미지 만들기
        cItem.setIcon(current_flag);                                  //마크에 비트맵이미지 추가

        cItem.setPosition(0.5f, 0.5f);                                //위치 어디쯤에 마크를 둘지 설정

        tmapView.addMarkerItem("current_point", cItem);             //(최종)뷰에 마크 추가

        tmapView.setSightVisible(true);                              //시야 보이게

        tmapView.setLocationPoint(current_lon, current_lat);       //현재위치로 설정
    }//current

    //위도, 경도로 마커찍기
    public void showMarker1(final TMapView tmapView, final String name, final double lat, final double lon) {

        TMapPoint tpoint = new TMapPoint(lat, lon);

        TMapMarkerItem tItem = new TMapMarkerItem();

        tItem.setTMapPoint(tpoint);
        tItem.setName(name);
        tItem.setVisible(TMapMarkerItem.VISIBLE);
        tItem.setCanShowCallout(true);
        tItem.setCalloutTitle(name);

        Bitmap right_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.yello_arrow);
        tItem.setCalloutRightButtonImage(right_bitmap);

        Bitmap current_flag = BitmapFactory.decodeResource(getResources(), R.drawable.blue_flag2);
        tItem.setIcon(current_flag);

        tItem.setPosition(0.5f, 0.5f);

        tmapView.addMarkerItem(name, tItem);


//        tmapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
//            @Override
//            public void onCalloutRightButton(TMapMarkerItem markerItem) {
//
//                final String getName = markerItem.getName();
//                final double getlat = markerItem.getTMapPoint().getLatitude();
//                final double getlon = markerItem.getTMapPoint().getLongitude();
//
//                start_name = getName;
//                start_lat = getlat;
//                start_lon = getlon;
//
//                Toast.makeText(GoHomeActivity.this, "출발지로 설정됨", Toast.LENGTH_LONG).show();
//
//                start_edit.setText(getName);
//                start_edit.setTextColor(Color.BLUE);
//
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(start_edit.getWindowToken(), 0);
//
//
//            }
//        });

    }//showMarker

    //위도, 경도로 마커찍기
    public void showMarker2(final TMapView tmapView, final String name, final double lat, final double lon) {

        TMapPoint tpoint = new TMapPoint(lat, lon);

        TMapMarkerItem tItem = new TMapMarkerItem();

        tItem.setTMapPoint(tpoint);
        tItem.setName(name);
        tItem.setVisible(TMapMarkerItem.VISIBLE);
        tItem.setCanShowCallout(true);
        tItem.setCalloutTitle(name);

        Bitmap right_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.yello_arrow);
        tItem.setCalloutRightButtonImage(right_bitmap);

        Bitmap current_flag = BitmapFactory.decodeResource(getResources(), R.drawable.red_flag2);
        tItem.setIcon(current_flag);

        tItem.setPosition(0.5f, 0.5f);

        tmapView.addMarkerItem(name, tItem);

//        Log.e("aaa", "" + lat);
        tmapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {

                final String getName = markerItem.getName();
                final double getlat = markerItem.getTMapPoint().getLatitude();
                final double getlon = markerItem.getTMapPoint().getLongitude();

                end_name = getName;
                end_lat = getlat;
                end_lon = getlon;

                Toast.makeText(GoHomeActivity.this, "도착지로 설정됨", Toast.LENGTH_LONG).show();

                end_edit.setText(getName);
                end_edit.setTextColor(Color.BLUE);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(end_edit.getWindowToken(), 0);
            }
        });

    }//showMarker2

    //위도, 경도로 마커찍기
    public void showMarker3(final TMapView tmapView, final String name, final double lat, final double lon) {

        TMapPoint tpoint = new TMapPoint(lat, lon);

        TMapMarkerItem tItem = new TMapMarkerItem();

        tItem.setTMapPoint(tpoint);
        tItem.setName(name);
        tItem.setVisible(TMapMarkerItem.VISIBLE);
        tItem.setCanShowCallout(true);
        tItem.setCalloutTitle(name);

        Bitmap right_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.yello_arrow);
        tItem.setCalloutRightButtonImage(right_bitmap);

        Bitmap current_flag = BitmapFactory.decodeResource(getResources(), R.drawable.red_marker);
        tItem.setIcon(current_flag);

        tItem.setPosition(0.5f, 0.5f);

        tmapView.addMarkerItem(name, tItem);


        tmapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {

                final String getName = markerItem.getName();
                final double getlat = markerItem.getTMapPoint().getLatitude();
                final double getlon = markerItem.getTMapPoint().getLongitude();

//                "출발지로 설정",
                final CharSequence[] items = {"도착지로 설정"};

                AlertDialog.Builder builder = new AlertDialog.Builder(GoHomeActivity.this);

                builder.setTitle(getName)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                if (which == 0) {
                                    //기존 출발지를 설정하였다면 기존 마크 삭제
                                    if (end_name != null) {
                                        tmapView.removeMarkerItem2(end_name);                       //도착 마크 지우기
                                        showMarker3(tmapView, end_name, end_lat, end_lon);      //기존 마크(3)로 변경
                                    }

                                    end_name = getName;            //도착지점 이름 초기화
                                    end_lat = getlat;              //도착지점 위도 초기화
                                    end_lon = getlon;             //도착지점 경도 초기화

                                    tmapView.removeMarkerItem2(getName);           //기존 마커 제거
                                    showMarker2(tmapView, getName, getlat, getlon);    //빨강(도착) 깃발 마커 추가

                                    end_edit.setText(end_name);
                                    end_edit.setTextColor(Color.BLUE);

                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(end_edit.getWindowToken(), 0);

                                    Toast.makeText(GoHomeActivity.this, "'" + getName + "' 도착지 설정 됨", Toast.LENGTH_LONG).show();

                                }
                                /*
                                * System.out.println("확인 0 : 출발지 설정 중");

                                    //기존 출발지를 설정하였다면 기존 마크 삭제
                                    if (start_name != null) {
                                        tmapView.removeMarkerItem2(start_name);                       //도착 마크 지우기
                                        showMarker3(tmapView, start_name, start_lat, start_lon);      //기존 마크(3)로 변경
                                    }

                                    start_name = getName;          //시작지점 이름 초기화
                                    start_lat = getlat;            //시작지점 위도 초기화
                                    start_lon = getlon;            //시작지점 경도 초기화

                                    tmapView.removeMarkerItem2(getName);           //기존 마커 제거
                                    showMarker1(tmapView, getName, getlat, getlon);    //파랑(출발) 깃발 마커 추가

                                    start_edit.setText(start_name);
                                    start_edit.setTextColor(Color.BLUE);

                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(start_edit.getWindowToken(), 0);

                                    Toast.makeText(GoHomeActivity.this, "'" + getName + "' 출발지로 설정 됨", Toast.LENGTH_LONG).show();
                                * */
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }//showMarker3

    // cctv 마커
    public void cctvMarker(TMapView tmapView, final String address, final String management, final String tel, double lat, double lon) {
        String name = address;

        TMapPoint tpoint = new TMapPoint(lat, lon);

        TMapMarkerItem tItem = new TMapMarkerItem();

        tItem.setTMapPoint(tpoint);
        tItem.setName(name);
        tItem.setVisible(TMapMarkerItem.VISIBLE);
        tItem.setCanShowCallout(true);
        tItem.setCalloutTitle(name);
        tItem.setCalloutSubTitle("관리기관 전화번호: " + tel);

        Bitmap right_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.yello_arrow);
        tItem.setCalloutRightButtonImage(right_bitmap);

        //tItem.setEnableClustering(true);    // 마커에 대한 클러스터링 유무
        tItem.setAutoCalloutVisible(true);  // 풍선뷰 자동으로 활성화

        tmapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {
                AlertDialog.Builder alert = new AlertDialog.Builder(GoHomeActivity.this);

                alert.setTitle("CCTV");
                alert.setMessage("주소 : " + address + "\n관리기관명 : " + management + "\n관리기관 전화번호 : " + tel);
                alert.setPositiveButton("뒤로가기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                alert.show();
            }
        });

        Bitmap current_flag = BitmapFactory.decodeResource(getResources(), R.drawable.cctv_marker);
        tItem.setIcon(current_flag);

        tItem.setPosition(0.5f, 0.5f);
        tmapView.addMarkerItem(name, tItem);
    }


    //길찾기 (얘는 됨)
    public void navigation(final TMapView tMapView) {

        if (end_name != null) {
            Toast.makeText(this, "길찾기 시작", Toast.LENGTH_LONG).show();

            tMapView.removeAllMarkerItem();     //이전에 있던 모든 마크 지우기
            tMapView.removeAllTMapPolygon();    //이전에 있던 경로 지우기
            tMapView.setZoomLevel(17);
            tMapView.setCenterPoint(current_lon, current_lat, true); // ==< 변경
//            tMapView.setTrackingMode(true);

            //try - catch 문으로 오류 대비
            try {
                TMapPoint startpoint = new TMapPoint(current_lat, current_lon); // ==< 변경
                TMapPoint endpoint = new TMapPoint(end_lat, end_lon);

                showMarker1(tMapView, "현재 위치", current_lat, current_lon); // ==< 변경
                showMarker2(tMapView, end_name, end_lat, end_lon);

                tmapdata.findPathDataWithType(
                        TMapData.TMapPathType.PEDESTRIAN_PATH,
                        startpoint, endpoint,
                        new TMapData.FindPathDataListenerCallback() {
                            @Override
                            public void onFindPathData(TMapPolyLine polyLine) {
                                polyLine.setLineColor(Color.BLUE);
                                polyLine.setLineWidth(10);
                                tMapView.addTMapPath(polyLine);

                                final double distance = polyLine.getDistance();
                                final double time = distance;
                                final double times = time / 60; // 걸리는 시간
                                distances = distance / 1000; // 걸리는 거리
                                getDistance = String.format("%,.2f", distances);

//                                Log.e("time : ", time + "");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dis_time.setText(" : " + (int) times + " 분 / " + getDistance + "km ");

                                        // 타이머 끝나면 메시지 전송
                                        countTimer = (int) time;

                                        // Vibrator 객체를 얻어서 진동시작
                                        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                                        final Button alertBtn = findViewById(R.id.alertBtn);
                                        // '긴급 메시지 설정' 클릭시
                                        alertBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (isClick) { // '긴급 메시지 시작' 클릭시
                                                    captureView(findViewById(R.id.map_view));
                                                    countDownTimer = new CountDownTimer(((long) time) * 1000, 1000) {
                                                        @Override
                                                        public void onTick(long millisUntilFinished) {
                                                            TextView alertMsg = findViewById(R.id.alertMsg);
                                                            countTimer--;
                                                            int min = (int) countTimer / 60;
                                                            int second = (int) countTimer % 60;
                                                            alertMsg.setText(" : " + String.valueOf(min) + " 분 " + String.valueOf(second) + " 초");
//                                                        alertMsg.setText(countTimer);
//                                                            Log.e("countTimer", countTimer + "");

                                                            if (countTimer == 0) {

                                                                // MessageDB에서 데이터 받기
                                                                MessageDB db = new MessageDB(getApplicationContext());
                                                                String phoneNo = db.selectGetPhoneNo();
                                                                String sms = db.selectGetSms();

                                                                // 타이머가 0이 되면 '메시지' 전송
                                                                try {
                                                                    SmsManager smsManager = SmsManager.getDefault();
                                                                    smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                                                                    smsManager.sendTextMessage(phoneNo, null, url.toString(), null, null);

                                                                    Toast.makeText(GoHomeActivity.this, "메시지를 전송합니다.",
                                                                            Toast.LENGTH_SHORT).show();

                                                                    long[] pattern = {100, 300, 100, 700, 300, 2000}; //짝수 : 대기 , 홀수 : 진동
                                                                    vibrator.vibrate(pattern, -1); // 0 : 무한반복, -1 : 반복없음


                                                                    alertBtn.setText("긴급 메시지 시작");
                                                                } catch (Exception e) {
                                                                    Toast.makeText(getApplicationContext(), "전송 실패, 다시 시도해주세요.",
                                                                            Toast.LENGTH_SHORT).show();
                                                                }

                                                                countDownTimer.onFinish();
                                                                return;
                                                            }

                                                        }

                                                        @Override
                                                        public void onFinish() {
                                                            cancel();
                                                        }
                                                    }.start();

                                                    alertBtn.setText("긴급 메시지 종료");
                                                    isClick = false;
                                                } else {

                                                    Toast.makeText(getApplicationContext(), "긴급메시지 발송을 종료합니다.",
                                                            Toast.LENGTH_SHORT).show();
                                                    alertBtn.setText("긴급 메시지 시작");
                                                    countDownTimer.cancel();
                                                    isClick = true;
                                                }

                                            }
                                        });

                                        // end 타이머
                                    }
                                });
                            }
                        });

            } catch (Exception e) {
                Toast.makeText(this, "오류가 발생하였습니다!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "출발지 또는 목적지를 설정해주세요!", Toast.LENGTH_LONG).show();
        }

    }//navigation


    public void captureView(View View) {
        View.buildDrawingCache();
        Bitmap captureView = View.getDrawingCache();
        FileOutputStream fos;

        String strFolderPath = Environment.getExternalStorageDirectory() + "/location";
        File folder = new File(strFolderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String strFilePath = strFolderPath + "/" + System.currentTimeMillis() + ".png";

        File fileCacheItem = new File(strFilePath);

        try {
            fos = new FileOutputStream(fileCacheItem);
            captureView.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            Log.e("aaa", strFolderPath);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //원 그리기
    //double
    public void drawCircle(TMapView tmapView, double lat, double lon, String address) {

        //포인트 설정하기
        TMapPoint tpoint = new TMapPoint(lat, lon);

        TMapCircle tcircle = new TMapCircle();

        tcircle.setCenterPoint(tpoint);  //포인트 변수가 들어가야 함 (TMapPoint)
        tcircle.setRadius(50);
        tcircle.setAreaColor(Color.BLUE);
        tcircle.setLineColor(Color.BLUE);
        tcircle.setCircleWidth(2);
        tcircle.setAreaAlpha(50);
        tcircle.setLineAlpha(0);
        tcircle.setRadiusVisible(true);

        tmapView.addTMapCircle(address, tcircle);
    }//drawCircle

//    //검색기능
//    public void search_start(final TMapView tmapView) {
//
//        tmapView.removeAllMarkerItem();            //이전에 있던 마커들 모두 지우기
//
//        //출발지를 설정했다면 다시 그려주기
//        if (start_name != null) {
//            showMarker1(tmapView, start_name, start_lat, start_lon);
//        }
//        //도착지를 설정했다면 다시 그려주기
//        if (end_name != null) {
//            showMarker2(tmapView, end_name, end_lat, end_lon);
//        }
//        String search_name = null;
//
//        //검색어가 있을 경우 text 가져오기
//        if (start_edit.getText().length() != 0) {
//            search_name = start_edit.getText().toString();
//        }
//
//        //검색어 없을 경우
////        if (search_name == null) {
////            Toast.makeText(this, "검색어를 입력해주세요!", Toast.LENGTH_LONG).show();
////        }
//
//        //검색어 있을 경우
//        else {
//            Toast.makeText(this, "검색 중...", Toast.LENGTH_LONG).show();
//
//            //try - catch 문으로 오류 대비
//            try {
//                TMapData tmapdata = new TMapData();     //통합검색 POI 데이터를 요청
//                final ArrayList<TMapPOIItem> poiItems;
//
//                tmapView.setZoomLevel(16);
//
//                //통합검색 POI데이터를 검색개수만큼 요청!
//                tmapdata.findAllPOI(search_name, 20, new TMapData.FindAllPOIListenerCallback() {
//
//                    @Override
//                    public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
//
//                        Looper.prepare();
//
//                        if (poiItem.size() != 0) {
//
//                            for (int i = 0; i < poiItem.size(); i++) {
//
//                                TMapPOIItem item = poiItem.get(i);
//
//                                String name = item.getPOIName();
//                                double lat = item.getPOIPoint().getLatitude();
//                                double lon = item.getPOIPoint().getLongitude();
//
//                                showMarker1(tmapView, name, lat, lon);        //마커 띄워주기
//                            }   //for문
//
//                            //제일 먼저 검색되는 결과 중심점으로 설정
//                            tmapView.setCenterPoint(poiItem.get(0).getPOIPoint().getLongitude(), poiItem.get(0).getPOIPoint().getLatitude(), true);
//                            Toast.makeText(GoHomeActivity.this, "검색 완료", Toast.LENGTH_LONG).show();
//                        }   //if문
//
//                        else {
//                            Toast.makeText(GoHomeActivity.this, "검색 결과 없음", Toast.LENGTH_LONG).show();
//                        }
//                        Looper.loop();
//                    }   //findAllPOI
//                });
//
//            } catch (Exception e) {
//                Toast.makeText(this, "오류가 발생하였습니다!", Toast.LENGTH_LONG).show();
//            }
//        }//else
//    }//search_start

    //검색기능
    public void search_end(final TMapView tmapView) {

        tmapView.removeAllMarkerItem();            //이전에 있던 마커들 모두 지우기

        //출발지를 설정했다면 다시 그려주기
        if (start_name != null) {
            showMarker1(tmapView, "현재 내 위치", current_lat, current_lon);
        }
        //도착지를 설정했다면 다시 그려주기
        if (end_name != null) {
            showMarker2(tmapView, end_name, end_lat, end_lon);
        }



        //검색어가 있을 경우 text 가져오기
        if (end_edit.getText().length() > 0) {
            search_name = end_edit.getText().toString();
//            search_name = db.selectByDestination();
            Log.e("search_name", ""+search_name);
        }

        //검색어 없을 경우
        if (search_name == null) {
            Toast.makeText(this, "검색어를 입력해주세요!", Toast.LENGTH_LONG).show();
//            Log.e("aaa","검색어 없음");
        }
        //검색어 있을 경우
        else {
//            Toast.makeText(this, "검색 중...", Toast.LENGTH_LONG).show();
//            Log.e("aaa","검색중");
            //try - catch 문으로 오류 대비
            try {
                final ArrayList<TMapPOIItem> poiItems;

                tmapView.setZoomLevel(16);

                //통합검색 POI데이터를 검색개수만큼 요청!
                tmapdata.findAllPOI(search_name, 20, new TMapData.FindAllPOIListenerCallback() {

                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
//                        Log.e("aaa","poiItem.size(): "+poiItem.size());
                        Looper.prepare();

                        if (poiItem.size() != 0) {

                            for (int i = 0; i < poiItem.size(); i++) {

                                TMapPOIItem item = poiItem.get(i);

                                String name = item.getPOIName();
                                double lat = item.getPOIPoint().getLatitude();
                                double lon = item.getPOIPoint().getLongitude();


                                showMarker2(tmapView, name, lat, lon);        //마커 띄워주기
                            }   //for문

                            //제일 먼저 검색되는 결과 중심점으로 설정
                            tmapView.setCenterPoint(poiItem.get(0).getPOIPoint().getLongitude(), poiItem.get(0).getPOIPoint().getLatitude(), true);

                            end_name = poiItem.get(0).getPOIName();
                            end_lat = poiItem.get(0).getPOIPoint().getLatitude();
                            end_lon = poiItem.get(0).getPOIPoint().getLongitude();
//                            Log.e("aaa","end_name: "+end_name+" end_lat: "+end_lat+" end_lon: "+end_lon);
                            Toast.makeText(GoHomeActivity.this, "검색 완료", Toast.LENGTH_LONG).show();
                        }   //if문

                        else {
                            Toast.makeText(GoHomeActivity.this, "검색 결과 없음", Toast.LENGTH_LONG).show();
                        }
                        Looper.loop();
                    }   //findAllPOI
                });

            } catch (Exception e) {
                Toast.makeText(this, "오류가 발생하였습니다!", Toast.LENGTH_LONG).show();
            }
        }//else
    }//search_end

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onCalloutRightButton(TMapMarkerItem markerItem) {

    }

    @Override
    protected void onDestroy() {
//        mTimer.cancel();
        super.onDestroy();
    }

    // Toolbar
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Toast.makeText(this, "안심지도를 종료합니다", Toast.LENGTH_LONG).show();
//            mTimer.cancel();
            super.onBackPressed();
        }

        if (countDownTimer != null)
            countDownTimer.onFinish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Intent cctvIntent = new Intent(getApplicationContext(), FindCctvActivity.class);
            startActivity(cctvIntent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(getApplicationContext(), NaviActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(getApplicationContext(), NewsSubActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(getApplicationContext(), HotIssueActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }// end ToolBar

    public void cctvOn(TMapView tmapview) {
        ArrayList<DB.CCTV> cctvs = new ArrayList<>();
        DB.CCTV cctv = new DB.CCTV();

        DB.setDB().isCheckDB(GoHomeActivity.this,"/data/data/" + getPackageName() + "/databases","cctv.DB");

        tmapview.removeAllMarkerItem();
//        Log.e("aaa","current_lat: "+current_lat+" start_lon: "+current_lon+" end_lat: "+end_lat+" end_lon: "+end_lon);
        if (current_lat < end_lat) {
            if (current_lon < end_lon) {
                cctvs = DB.setDB().navirouteCCTV(current_lat - 0.001, end_lat + 0.001, current_lon - 0.001, end_lon + 0.001);
            } else {
                cctvs = DB.setDB().navirouteCCTV(current_lat - 0.001, end_lat + 0.001, end_lon - 0.001, current_lon + 0.001);
            }
        } else {
            if (current_lon < end_lon) {
                cctvs = DB.setDB().navirouteCCTV(end_lat - 0.001, current_lat + 0.001, current_lon - 0.001, end_lon + 0.001);
            } else {
                cctvs = DB.setDB().navirouteCCTV(end_lat - 0.001, current_lat + 0.001, end_lon - 0.001, current_lon + 0.001);
            }
        }
//        Log.e("aaa","사이즈:"+cctvs.size());
        for (int i = 0; i < cctvs.size(); i++) {
            cctv = cctvs.get(i);
            cctvMarker(tmapview, cctv.getAddr(), cctv.getManageOffice(), cctv.getTellNum(), cctv.getLatitude(), cctv.getLongitude());
        }
    }
}
