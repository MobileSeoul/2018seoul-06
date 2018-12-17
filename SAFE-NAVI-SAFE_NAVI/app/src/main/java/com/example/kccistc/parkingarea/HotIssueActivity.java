package com.example.kccistc.parkingarea;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.kccistc.parkingarea.adapter.HotIssueAdapter;
import com.example.kccistc.parkingarea.fragment.Fragment_First;
import com.example.kccistc.parkingarea.fragment.Fragment_Second;
import com.example.kccistc.parkingarea.fragment.Fragment_Third;

public class HotIssueActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotissue_main);

        ViewPager viewPager = findViewById(R.id.view_pager);
        HotIssueAdapter adapter =
                new HotIssueAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        //Tab layout
        TabLayout tab = findViewById(R.id.tab);
//        tab.setTabMode(TabLayout.MODE_SCROLLABLE);
        tab.setupWithViewPager(viewPager);
        tab.getTabAt(0).setIcon(R.drawable.ic_menu_gallery); // 버튼에 이미지 넣기
        tab.getTabAt(1).setIcon(R.drawable.ic_menu_share); // 버튼에 이미지 넣기
        tab.getTabAt(2).setIcon(R.drawable.ic_menu_send); // 버튼에 이미지 넣기


        // ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // end Toolbar

        setTitle("실시간 핫이슈");
    }// end main

    // Toolbar
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
            Intent intent = new Intent(getApplicationContext(), GoHomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(getApplicationContext(), NewsSubActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }// end ToolBar
}
