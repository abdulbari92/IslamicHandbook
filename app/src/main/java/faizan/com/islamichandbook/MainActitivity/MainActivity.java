package faizan.com.islamichandbook.MainActitivity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.crashlytics.android.Crashlytics;

import faizan.com.islamichandbook.GeoFence.GeoFenceActivity;
import faizan.com.islamichandbook.R;
import faizan.com.islamichandbook.announcement.AnnouncementFragment;
import faizan.com.islamichandbook.hadith.HadithFragment;
import faizan.com.islamichandbook.prayertiming.PrayerTimingFragment;
import faizan.com.islamichandbook.qibla.QiblaFragment;
import faizan.com.islamichandbook.quran.QuranFragment;
import faizan.com.islamichandbook.scholarlyArticles.ScholarlyArticleFragment;
import faizan.com.islamichandbook.utilities.AlarmReceiver;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    //for fragment transactions
    Fragment fragment;
    private FragmentManager fragmentManager;

    //for setting alarms
    AlarmManager alarmManager;
    Calendar calendar;

    //shared preference filename to check if values are already set for alarms
    //meaning alarms are set
    final String SHAREDALARMSTATE = "alarm_state";

    //initializing
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //instantiate the alarmManager
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //loading the initial MainFragment
        fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragment = new MainFragment();
        transaction.add(R.id.frameLayout, fragment);
        transaction.commit();

        // Webview on the bottom for advertisements
        WebView webView;
        webView = (WebView) findViewById(R.id.webView1);
        webView.loadUrl("http://rjtmobile.com/ansari/adv/mytest.gif");
        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        //if clicked on any link it should load in the same webView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    private void setAlarm() {
        SharedPreferences prefs = this.getSharedPreferences(SHAREDALARMSTATE, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (!prefs.getBoolean("state",false)){
            Log.i("Abdul","Alarms Set");
            // setting the calendar the calendar time
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,2);
            calendar.set(Calendar.MINUTE, 30);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);

            //setting our intent
            Intent intent = new Intent(this, AlarmReceiver.class);

            //specifying our pending intent, which is a delayed intent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            //setting the alarmManager
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pendingIntent);

            //setting the shared preference alar state to true so that we dont set it again and again
            editor.putBoolean("state", true);
        }
    }

    //back button for drawer action
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //handling clicks on nav bar items
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_quran) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                fragmentManager = getFragmentManager();
                fragment = new QuranFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.animator.slide_in_up, R.animator.slide_out_down);
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

        } else if (id == R.id.nav_hadith) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
            }
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                fragment = new HadithFragment();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

        } else if (id == R.id.nav_prayer_timing) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},3);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                fragment = new PrayerTimingFragment();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

        } else if (id == R.id.nav_qibla) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 4);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                fragment = new QiblaFragment();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

        } else if (id == R.id.nav_articles) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},5);
            }
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                fragment = new ScholarlyArticleFragment();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

        }else if (id == R.id.nav_Announcement) {
            fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            fragment = new AnnouncementFragment();
            transaction.replace(R.id.frameLayout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        //Mark Masjid Geofence
        else if (id == R.id.nav_Mark_Masjid){
            //for taking dnd permission from phone with api>=24
            NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    && !notificationManager.isNotificationPolicyAccessGranted()) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Permission Required");
                alertDialog.setMessage("To Use this Service Notification Manager is required");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            else{
                Intent mark_masjid = new Intent(this, GeoFenceActivity.class);
                startActivity(mark_masjid);
            }
        }
        //close the drawer after the selection
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //handling permission responses
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setAlarm();
                }
                break;
            }
            case 1:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    fragment = new QuranFragment();
                    transaction.replace(R.id.frameLayout, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                break;
            }
            case 2:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    fragment = new HadithFragment();
                    transaction.replace(R.id.frameLayout, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    break;
                }
            }
            case 3:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setAlarm();
                    fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    fragment = new PrayerTimingFragment();
                    transaction.replace(R.id.frameLayout, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                break;
            }
            case 4: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setAlarm();
                    fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    fragment = new QiblaFragment();
                    transaction.replace(R.id.frameLayout, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    break;
                }
            }
            case 5:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    fragment = new ScholarlyArticleFragment();
                    transaction.replace(R.id.frameLayout, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                break;
            }
        }
        return;
    }

    @Override
    public void onClick(View v) {
        v.getId();
    }
}
