package com.example.kccistc.parkingarea;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kccistc.parkingarea.adapter.NewsSubAdapter;
import com.example.kccistc.parkingarea.list.NewsList;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsSubActivity extends YouTubeBaseActivity implements NavigationView.OnNavigationItemSelectedListener, YouTubePlayer.OnInitializedListener  {
    ListView newsSubList;
    List<NewsList> list;
    YouTubePlayerView player;
    YouTubePlayer youTubePlayer;
    NewsList newsListVO = null;
    String videoId = "";

    Toolbar toolbar;
    Handler handler = new Handler();
    boolean isClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_sub_main);

        newsSubList = findViewById(R.id.newsSubList);

        // ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("CCTV 영상자료");
//        setActionBar(toolbar);


//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // end Toolbar

        player = findViewById(R.id.player);

        // start news list
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = Network.getData(
                        "https://www.googleapis.com/youtube/v3/search?part=snippet&"+
                                "key=AIzaSyBnaYEag0kdyXV7K3ZNE7fi5PPoonoAAog&q="+"" +
                                "cctv+%EB%B2%94%EC%A3%84+%EC%95%88%EC%A0%84&maxResults=50&order=date&order=viewCount&type=video");
                try {

                    JSONObject obj = new JSONObject(result);
                    JSONArray items = obj.getJSONArray("items");

                    list = new ArrayList<NewsList>();
                    for (int i=0 ; i<items.length(); i++){
                        JSONObject jobj = items.getJSONObject(i);
                        JSONObject snippet = jobj.getJSONObject("snippet");
                        JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                        JSONObject medium = thumbnails.getJSONObject("high");

                        JSONObject id = jobj.getJSONObject("id");


                        String urls = medium.getString("url");
                        String title = snippet.getString("title");
                        String description = snippet.getString("description");
                        String publishedAt = snippet.getString("publishedAt");
                        String channelTitle = snippet.getString("channelTitle");
                        String videoIds = id.getString("videoId");

                        newsListVO = new NewsList();
                        newsListVO.setUrl(urls);
                        newsListVO.setPublishedAt(publishedAt);
                        newsListVO.setTitle(title);
                        newsListVO.setDescription(description);
                        newsListVO.setChannelTitle(channelTitle);
                        newsListVO.setVideoId(videoIds);
                        list.add(newsListVO);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NewsSubAdapter adapter = new NewsSubAdapter(NewsSubActivity.this,R.layout.news_list_sub_item ,list);
                        newsSubList.setAdapter(adapter);

                        newsSubList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                                newsListVO = list.get(i);
                                videoId = newsListVO.getVideoId();

                                youTubePlayer.loadVideo(videoId);
                            }
                        });
                        // 유투브 영상 실행
                        newsListVO = list.get(0);
                        player.initialize("AIzaSyBnaYEag0kdyXV7K3ZNE7fi5PPoonoAAog", NewsSubActivity.this);

                    }
                });// end runonUiThread
            }
        }).start();// end Thread
        // end news list

    }

    // 유투브 영상 실행 impliment 메서드
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        this.youTubePlayer = youTubePlayer;

        videoId = newsListVO.getVideoId();
        if(newsListVO == null){
            Toast.makeText(NewsSubActivity.this,"잠시 후 실행됩니다.",Toast.LENGTH_SHORT).show();
        }


//        Log.e("videoId : ", videoId);
        if(videoId != null){
            Toast.makeText(NewsSubActivity.this,"play youtube",Toast.LENGTH_SHORT).show();
            youTubePlayer.loadVideo(videoId);
        }else{
            Toast.makeText(NewsSubActivity.this,"자료가 없습니다.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this,"다시 실행해주세요",Toast.LENGTH_SHORT).show();
    }
    // end youtube


    // Toolbar
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }


        // 처음 Back 눌렀을 때
//        if(!isClick){
//            Toast.makeText(this,"다시 누르면 종료",Toast.LENGTH_SHORT).show();
//            isClick = true;
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    isClick = false;
//                }
//            }, 3000);
//        } else {
//            finish();
//        }




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
            Intent intent = new Intent(getApplicationContext(), GoHomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(getApplicationContext(), HotIssueActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }// end ToolBar
}
