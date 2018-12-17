package com.example.kccistc.parkingarea;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.kccistc.parkingarea.adapter.NewsAdapter;
import com.example.kccistc.parkingarea.db.MessageDB;
import com.example.kccistc.parkingarea.list.NewsList;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.Inflater;

// implements NavigationView.OnNavigationItemSelectedListener
public class ContentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    LinearLayout CctvBtn1;
    LinearLayout CctvBtn2;
    LinearLayout CctvBtn3;
    LinearLayout CctvBtn4;
    LinearLayout CctvBtn5;
    Button RankBtn;
    Button newsSubMoveTo;
    TextView dateTime;
    ListView newsView;
    ScrollView scrollview;
    int year, month, date;

    Handler handler = new Handler();
    boolean isClick = false;

    List<NewsList> list;

    //카메라 접근
    private Camera camera;
    boolean isOnCamera = false;

    boolean isRegist = false;

    private String name;
    private String phoneNo;
    private String sms;

    private int isCount;

    MessageDB db;

    // T weather
    private String city;
    private String county;
    private String village;
    private String time;

    //오늘날씨
    private String today_sky;
    private String today_maxtemp;
    private String today_mintemp;

    //내일날씨
    private String tomorrow_sky;
    private String tomorrow_maxtemp;
    private String tomorrow_mintemp;

    //체감온도
    private String current_temp;

    //불쾌지수
    private String discomfort_index;
    private double comfareIndex;
    private String discomfort_text;

    //현재 내위치 위도, 경도 값
    private static double lat;
    private static double lng;

    public static double getLat() {
        return lat;
    }

    public static double getLng() {
        return lng;
    }

    Context context;
    Thread weatherTr;
    LocationListener locationListener;
    String point_address = null; // 현재 주소 얻어오기
    String[] temp = null; // 주소 자르기
    TextView current_adr;
    String adr = null;

    TMapView tmapview;

    NewsAdapter adapter;

    Boolean isLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_main);

        tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey("ab19368d-24d3-4c4a-9348-a5e4ec1834af");

        CctvBtn1 = findViewById(R.id.CctvBtn1);
        CctvBtn2 = findViewById(R.id.CctvBtn2);
        CctvBtn3 = findViewById(R.id.CctvBtn3);
        CctvBtn4 = findViewById(R.id.CctvBtn4);
        CctvBtn5 = findViewById(R.id.CctvBtn5);
//        dateTime = findViewById(R.id.dateTime);
        RankBtn = findViewById(R.id.rankBtn);
        newsSubMoveTo = findViewById(R.id.newsSubMoveTo);
        newsView = findViewById(R.id.newsView);
        scrollview = findViewById(R.id.scrollview);
        current_adr = findViewById(R.id.current_adr); // 현재 주소 얻기


        scrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isLoad = true;
                return false;
            }
        });

        scrollview.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if(!isLoad) {
                    scrollview.setScrollX(0);
                    scrollview.setScrollY(0);
                }
            }
        });


        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        date = calendar.get(Calendar.DAY_OF_MONTH) + 1;
//        dateTime.setText(String.format("%04d년 %02d월 %02d일", year, month, date));


        // ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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


        setTitle("SafeNavi");

        CctvBtn1.setOnClickListener(new View.OnClickListener() {
            // 전국 <CCTV 위치찾기>버튼 클릭시
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FindCctvActivity.class);
                startActivity(intent);
            }
        });

        CctvBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NaviActivity.class);
                startActivity(intent);
            }
        });

        CctvBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentActivity.this, GoHomeActivity.class);
                startActivity(intent);

            }
        });


        // 긴급버튼 클릭
        CctvBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DB생성
                db = new MessageDB(ContentActivity.this);
//        db.delete();
                isCount = db.seletAll();
                // DB생성 유무확인
//                Log.e("isCount", isCount + "");

                // DB에 내용이있으면 확인하고 Popup으로 띄우기
                if (isCount > 0) {
                    name = db.selectGetName();

//                    Log.e("update_name", db.selectGetName());
//                    Log.e("update_phoneNo", db.selectGetPhoneNo());
//                    Log.e("update_sms", db.selectGetSms());
                }

                if (name != null)
                    isRegist = true;

                if (isRegist) {
                    Intent intent = new Intent(ContentActivity.this, PopupActivity.class);
                    startActivity(intent);

                    isRegist = true;
                } else {
                    Intent intent = new Intent(ContentActivity.this, SendMessage.class);
                    startActivity(intent);

                    isRegist = false;

                }
            }
        });

        CctvBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (adr != null) {
                    Intent intent = new Intent(ContentActivity.this, SafeHelp.class);
                    intent.putExtra("locationName", adr.split(" ")[1]);
                    startActivity(intent);
                } else
                    Toast.makeText(ContentActivity.this, "현재 위치를 검색 중입니다", Toast.LENGTH_SHORT).show();
            }
        });

        // 실검으로 가기 버튼
        RankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HotIssueActivity.class);
                startActivity(intent);
            }
        });

        // newsSubList로 가기 버튼
        newsSubMoveTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewsSubActivity.class);
                startActivity(intent);
            }
        });


        //GpsInfo
        //현재 내위치 위도 경도 알아내기
        LocationManager location = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean isGps = location.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetwork = location.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isPassive = location.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

//        Log.e("isGps", isGps + "");
//        Log.e("isNetwork", isNetwork + "");
//        Log.e("isPassive", isPassive + "");


        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();

                TMapData tmapdata = new TMapData();

                try {
                    tmapdata.convertGpsToAddress(lat, lng, new TMapData.ConvertGPSToAddressListenerCallback() {

                        @Override
                        public void onConvertToGPSToAddress(String addr) {
                            temp = addr.split(" ");
                            adr = temp[0] + " " + temp[1] + " " + temp[2];
                        }
                    });

                } catch (Exception e) {
//                    Log.d("error", "*** Exception: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }

                //수정부분
                if (lat > 0 && lng > 0 && ((weatherTr.getState() + "").equals("NEW")
                        || (weatherTr.getState() + "").equals("TERMINATE")))
                    weatherTr.start();

                if (point_address != null)
                    current_adr.setText(adr); // <== 변경

//                Log.e("aaa", weatherTr.getState() + "");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (isGps)
            if (ActivityCompat.checkSelfPermission(ContentActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ContentActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        location.requestLocationUpdates
                (LocationManager.GPS_PROVIDER, 100, 1, locationListener);

        if (isNetwork)
            location.requestLocationUpdates
                    (LocationManager.NETWORK_PROVIDER, 100, 1, locationListener);

        //수정부분
        //날씨 JSON 파싱
        weatherTr = new Thread(new Runnable() {
            @Override
            public void run() {
//                Log.e("check", "위도경도보다 먼저뜬다");
                //오늘,내일 날씨
                String result = Network.getData(
                        "https://api2.sktelecom.com/weather/summary?version=" +
                                "&lat=" + lat + "&lon=" + lng + "&appkey=ab19368d-24d3-4c4a-9348-a5e4ec1834af");
                //체감온도
                String result2 = Network.getData("https://api2.sktelecom.com/weather/index/wct?version=" +
                        "&lat=" + lat + "&lon=" + lng + "&appkey=ab19368d-24d3-4c4a-9348-a5e4ec1834af");
                //불쾌지수
                String result3 = Network.getData("https://api2.sktelecom.com/weather/index/th?version=" +
                        "&lat=" + lat + "&lon=" + lng + "&appkey=ab19368d-24d3-4c4a-9348-a5e4ec1834af");

                try {
                    //오늘,내일 날씨
                    JSONObject obj = new JSONObject(result);
                    JSONObject weather = obj.getJSONObject("weather");
                    JSONArray summary = weather.getJSONArray("summary");
                    JSONObject objs = summary.getJSONObject(0);
                    JSONObject grid = objs.getJSONObject("grid"); //지역 이름
                    city = grid.getString("city");
                    county = grid.getString("county");
                    village = grid.getString("village");
                    time = objs.getString("timeRelease");

                    JSONObject today = objs.getJSONObject("today");
                    JSONObject sky = today.getJSONObject("sky");
                    JSONObject temperature = today.getJSONObject("temperature");
                    today_sky = sky.getString("name"); //현재 날씨 상태
                    today_maxtemp = temperature.getString("tmax");
                    today_mintemp = temperature.getString("tmin");

                    JSONObject tomorrow = objs.getJSONObject("tomorrow");
                    JSONObject tsky = tomorrow.getJSONObject("sky");
                    JSONObject ttemperature = tomorrow.getJSONObject("temperature");
                    tomorrow_sky = tsky.getString("name"); //현재 날씨 상태
                    tomorrow_maxtemp = ttemperature.getString("tmax");
                    tomorrow_mintemp = ttemperature.getString("tmin");

                    //체감온도
                    JSONObject jobj = new JSONObject(result2);
                    JSONObject jweather = jobj.getJSONObject("weather");
                    JSONObject wIndex = jweather.getJSONObject("wIndex");
                    JSONArray wctIndex = wIndex.getJSONArray("wctIndex");
                    JSONObject jsbjs = wctIndex.getJSONObject(0);
                    JSONObject current = jsbjs.getJSONObject("current");
                    current_temp = current.getString("index");

                    //불쾌지수
                    JSONObject jobj2 = new JSONObject(result3);
                    JSONObject weather2 = jobj2.getJSONObject("weather");
                    JSONObject wIndex2 = weather2.getJSONObject("wIndex");
                    JSONArray thIndex2 = wIndex2.getJSONArray("thIndex");
                    JSONObject jobjs2 = thIndex2.getJSONObject(0);
                    JSONObject current2 = jobjs2.getJSONObject("current");
                    discomfort_index = current2.getString("index");


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (adr == null)
                                current_adr.setText(city + " " + county + " " + village);
                            else
                                current_adr.setText(adr); // <== 변경

                            TextView date = findViewById(R.id.date);
                            date.setText(time);

                            //오늘날씨
                            TextView today_weather = findViewById(R.id.today_weather);
                            today_weather.setText(today_sky + "  " + today_maxtemp + "˚ / " + today_mintemp + "˚ ");
                            ImageView todayImg = findViewById(R.id.todayImg);
//                            Log.e("today_sky", today_sky);
                            if (today_sky.equals("흐림"))
                                todayImg.setBackgroundResource(R.drawable.cloud);
                            else if (today_sky.equals("비"))
                                todayImg.setBackgroundResource(R.drawable.rain);
                            else if (today_sky.equals("눈"))
                                todayImg.setBackgroundResource(R.drawable.snow);
                            else
                                todayImg.setBackgroundResource(R.drawable.sunny);


                            //체감온도
                            TextView current_temp_view = findViewById(R.id.current_temp_view);
                            current_temp_view.setText("체감 온도 " + current_temp + "˚ ");

                            //내일날씨
                            TextView tomorrow_weather = findViewById(R.id.tomorrow_weather);
                            tomorrow_weather.setText(tomorrow_sky + "  " + tomorrow_maxtemp + "˚ / " + tomorrow_mintemp + "˚ ");

                            //수정부분
                            //불쾌지수
                            if (discomfort_index.length() > 0)
                                comfareIndex = Double.parseDouble(discomfort_index);
                            else
                                comfareIndex = -1;

                            //Log.e("discomfort_index", comfareIndex + "");
                            if (comfareIndex >= 80)
                                discomfort_text = "매우높음";
                            else if (comfareIndex >= 75)
                                discomfort_text = "높음";
                            else if (comfareIndex >= 68)
                                discomfort_text = "보통";
                            else
                                discomfort_text = "낮음";

                            if (comfareIndex == -1) discomfort_text = "정보없음";

                            TextView discomfort_view = findViewById(R.id.discomfort_view);
                            discomfort_view.setText("불쾌 지수 " + discomfort_index + " (" + discomfort_text + ")");

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        // NewsTask 실행
        new NewsTask().execute();

    }// end main

    // NewsTask
    class NewsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // youtube 뉴스(cctv관련) CustomerAdapter 써서 ListView에 뿌리기
            try {
                URL url =
                        new URL("https://www.googleapis.com/youtube/v3/search?part=snippet&" +
                                "key=AIzaSyBnaYEag0kdyXV7K3ZNE7fi5PPoonoAAog&q=" +
                                "cctv+%EB%B2%94%EC%A3%84+%EC%95%88%EC%A0%84&maxResults=50&order=date&order=viewCount&type=video");
                URLConnection conn = url.openConnection();
                InputStream is = conn.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader reader = new BufferedReader(isr);

                String result = "";
                while (true) {
                    String data = reader.readLine();
                    if (data == null) break;
                    result += data;
                }

                JSONObject obj = new JSONObject(result);
                JSONArray arr = obj.getJSONArray("items");

                list = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jobj = arr.getJSONObject(i);
                    JSONObject snippet = jobj.getJSONObject("snippet");
                    JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                    JSONObject medium = thumbnails.getJSONObject("medium");

                    String title = snippet.getString("title");
                    String publishedAt = snippet.getString("publishedAt");
                    String channelTitle = snippet.getString("channelTitle");
                    String imgUrl = medium.getString("url");

                    NewsList newsList = new NewsList();
                    newsList.setPublishedAt(publishedAt);
                    newsList.setTitle(title);
                    newsList.setChannelTitle(channelTitle);
                    newsList.setUrl(imgUrl);
                    list.add(newsList);
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            adapter = new NewsAdapter(ContentActivity.this, R.layout.news_list_item, list);
            try {
                newsView.setAdapter(adapter);

                setListViewHeightBasedOnChildren(newsView);
            }catch (Exception e){
                Log.e("nullException","null Exception error");
            }
        }
    }// end Newstask


    // ScrollView 안의 ListView를 나타내기 위한 메서드
    public void setListViewHeightBasedOnChildren(ListView listView) {
        NewsAdapter listAdapter = (NewsAdapter) listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight() + 100) * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        //scrollView.fullScroll(ScrollView.FOCUS_UP);
        listView.requestLayout();
    }


    // Toolbar
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

        }
        // 처음 종료버튼 누르면
        if (!isClick) {
            Toast.makeText(this, "'뒤로'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            isClick = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isClick = false;
                }
            }, 3000);
        } else {
            finish();
        }

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
            Intent intent = new Intent(ContentActivity.this, GoHomeActivity.class);
            startActivity(intent);
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

}
