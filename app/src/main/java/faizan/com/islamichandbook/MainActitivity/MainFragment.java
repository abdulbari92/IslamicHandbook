package faizan.com.islamichandbook.MainActitivity;

import android.Manifest;
import android.app.AlarmManager;
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
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import faizan.com.islamichandbook.GeoFence.GeoFenceActivity;
import faizan.com.islamichandbook.R;
import faizan.com.islamichandbook.announcement.AnnouncementFragment;
import faizan.com.islamichandbook.hadith.HadithFragment;
import faizan.com.islamichandbook.prayertiming.PrayerTimingFragment;
import faizan.com.islamichandbook.qibla.QiblaFragment;
import faizan.com.islamichandbook.quran.QuranFragment;
import faizan.com.islamichandbook.scholarlyArticles.ScholarlyArticleFragment;
import faizan.com.islamichandbook.utilities.AlarmReceiver;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    //for replacing fragments
    Fragment fragment;
    private FragmentManager fragmentManager;

    //for setting alarms
    AlarmManager alarmManager;
    Calendar calendar;

    //Shared preference file name for alaram state
    final String SHAREDALARMSTATE = "alarm_state";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        NavigationView navigationView = (NavigationView) view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //handling items on navigation menu
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_quran) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                fragment = new QuranFragment();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        } else if (id == R.id.nav_hadith) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                fragment = new HadithFragment();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.commit();
            }
        } else if (id == R.id.nav_prayer_timing) {

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
            }
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                fragment = new PrayerTimingFragment();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        } else if (id == R.id.nav_qibla) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 4);
            }
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                fragment = new QiblaFragment();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        } else if (id == R.id.nav_articles) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5);
            }
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                fragment = new ScholarlyArticleFragment();
                transaction.replace(R.id.frameLayout, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        } else if (id == R.id.nav_Announcement) {
            fragmentManager = getActivity().getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            fragment = new AnnouncementFragment();
            transaction.replace(R.id.frameLayout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        //Mark Masjid Geofence
        else if (id == R.id.nav_Mark_Masjid) {
            //gaining dnd access for devices with api>=24
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    && !notificationManager.isNotificationPolicyAccessGranted()) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Permission Required");
                alertDialog.setMessage("To Use Service Notification Manager is required");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            } else {
                Intent mark_masjid = new Intent(getActivity(), GeoFenceActivity.class);
                startActivity(mark_masjid);
            }
        }
        return true;
    }
    //handling permission responses
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    fragmentManager = getActivity().getFragmentManager();
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
                    fragmentManager = getActivity().getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    fragment = new HadithFragment();
                    transaction.replace(R.id.frameLayout, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                break;
            }
            case 3:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setAlarm();
                    fragmentManager = getActivity().getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    fragment = new PrayerTimingFragment();
                    transaction.replace(R.id.frameLayout, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                break;
            }
            case 4:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setAlarm();
                    fragmentManager = getActivity().getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    fragment = new QiblaFragment();
                    transaction.replace(R.id.frameLayout, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                break;
            }
            case 5:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fragmentManager = getActivity().getFragmentManager();
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

    //setting alarms
    private void setAlarm() {
        SharedPreferences prefs = getActivity().getSharedPreferences(SHAREDALARMSTATE, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (!prefs.getBoolean("state",false)){
            Log.i("Abdul","Alarms Set");
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,2);
            calendar.set(Calendar.MINUTE, 30);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);

            //setting our intent
            Intent intent = new Intent(getActivity(), AlarmReceiver.class);

            //specifying our pending intent, which is a delayed intent
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            //setting the alarmManager
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pendingIntent);

            //setting the shared preference alar state to true so that we dont set it again and again
            editor.putBoolean("state", true);
        }
    }
}

