package faizan.com.islamichandbook.utilities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AlarmSettingService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    SharedPreferences sharedPrayerTiming;
    SharedPreferences sharedPrayerAlarm;
    public final String SHAREDPREFPRAYERALARM = "prayer_alarm";
    public final String SHAREDPREFPRAYERTIMING = "prayer_timing";

    AlarmManager alarmManager;
    Calendar calendar;
    Location source;
    GoogleApiClient mGoogleApiClient;
    String city, countryCode;
    RequestQueue requestQueue;
    Context context;

    public AlarmSettingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        context = this;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        sharedPrayerAlarm = this.getSharedPreferences(SHAREDPREFPRAYERALARM, MODE_PRIVATE);
        sharedPrayerTiming = this.getSharedPreferences(SHAREDPREFPRAYERTIMING, MODE_PRIVATE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        setFajr();
        setSaher();
        setSunrise();
        setDuhr();
        setAsr();
        setSunset();
        setMaghrib();
        setIftar();
        setIsha();

        return START_NOT_STICKY;
    }

    private void setIftar() {
        if(sharedPrayerAlarm.getBoolean("Iftar",false)){
            int hour = Integer.parseInt(sharedPrayerTiming.getString("Iftar","18:00").substring(0,2));
            int minute = Integer.parseInt(sharedPrayerTiming.getString("Iftar","18:00").substring(3,5));
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            Intent iftarIntent = new Intent(this,PrayerAlarmReceiver.class);
            iftarIntent.putExtra("prayer", "Iftar");
            PendingIntent pendingIntentIftar = PendingIntent.getBroadcast(this, 0, iftarIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntentIftar);
        }
    }

    private void setSaher() {
        if(sharedPrayerAlarm.getBoolean("Saher",false)){
            int hour = Integer.parseInt(sharedPrayerTiming.getString("Saher","04:00").substring(0,2));
            int minute = Integer.parseInt(sharedPrayerTiming.getString("Saher","04:00").substring(3,5));
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            Intent saherIntent = new Intent(this,PrayerAlarmReceiver.class);
            saherIntent.putExtra("prayer", "Saher");
            PendingIntent pendingIntentSaher = PendingIntent.getBroadcast(this, 0, saherIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntentSaher);
        }
    }

    private void setFajr() {
        if(sharedPrayerAlarm.getBoolean("Fajr",false)){
            int hour = Integer.parseInt(sharedPrayerTiming.getString("Fajr","04:00").substring(0,2));
            int minute = Integer.parseInt(sharedPrayerTiming.getString("Fajr","04:00").substring(3,5));
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            Intent fajrIntent = new Intent(this,PrayerAlarmReceiver.class);
            fajrIntent.putExtra("prayer", "Fajr");
            PendingIntent pendingIntentFajr = PendingIntent.getBroadcast(this, 0, fajrIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntentFajr);
        }
    }

    private void setSunrise() {

        if(sharedPrayerAlarm.getBoolean("Sunrise",false)){
            int hour = Integer.parseInt(sharedPrayerTiming.getString("Sunrise","06:00").substring(0,2));
            int minute = Integer.parseInt(sharedPrayerTiming.getString("Sunrise","06:00").substring(3,5));
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            Intent sunriseIntent = new Intent(this,PrayerAlarmReceiver.class);
            sunriseIntent.putExtra("prayer", "Sunrise");
            PendingIntent pendingIntentSunrise = PendingIntent.getBroadcast(this, 0, sunriseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntentSunrise);
        }
    }

    private void setDuhr() {
        if(sharedPrayerAlarm.getBoolean("Dhuhr",false)) {
            int hour = Integer.parseInt(sharedPrayerTiming.getString("Dhuhr", "12:00").substring(0, 2));
            int minute = Integer.parseInt(sharedPrayerTiming.getString("Dhuhr", "12:00").substring(3, 5));
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            Intent dhuhrIntent = new Intent(this, PrayerAlarmReceiver.class);
            dhuhrIntent.putExtra("prayer", "Dhuhr");
            PendingIntent pendingIntentDhuhr = PendingIntent.getBroadcast(this, 0, dhuhrIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentDhuhr);
        }
    }

    private void setAsr() {
        if(sharedPrayerAlarm.getBoolean("Asr",false)) {
            int hour = Integer.parseInt(sharedPrayerTiming.getString("Asr", "16:00").substring(0, 2));
            int minute = Integer.parseInt(sharedPrayerTiming.getString("Asr", "16:00").substring(3, 5));
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            Intent asrIntent = new Intent(this, PrayerAlarmReceiver.class);
            asrIntent.putExtra("prayer", "Asr");
            PendingIntent pendingIntentAsr = PendingIntent.getBroadcast(this, 0, asrIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentAsr);
        }
    }

    private void setSunset() {

        if(sharedPrayerAlarm.getBoolean("Sunset",false) && !sharedPrayerAlarm.getBoolean("Maghrib",false)) {
            int hour = Integer.parseInt(sharedPrayerTiming.getString("Sunset", "18:00").substring(0, 2));
            int minute = Integer.parseInt(sharedPrayerTiming.getString("Sunset", "18:00").substring(3, 5));
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            Intent sunsetIntent = new Intent(this, PrayerAlarmReceiver.class);
            sunsetIntent.putExtra("prayer", "Sunset");
            PendingIntent pendingIntentSunset = PendingIntent.getBroadcast(this, 0, sunsetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentSunset);
        }
    }

    private void setMaghrib() {

        if(sharedPrayerAlarm.getBoolean("Maghrib",false)) {
            int hour = Integer.parseInt(sharedPrayerTiming.getString("Maghrib", "18:00").substring(0, 2));
            int minute = Integer.parseInt(sharedPrayerTiming.getString("Maghrib", "18:00").substring(3, 5));
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            Intent maghribIntent = new Intent(this, PrayerAlarmReceiver.class);
            maghribIntent.putExtra("prayer", "Maghrib");
            PendingIntent pendingIntentMaghrib = PendingIntent.getBroadcast(this, 0, maghribIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentMaghrib);
        }
    }

    private void setIsha() {

        if(sharedPrayerAlarm.getBoolean("Isha",false)) {
            int hour = Integer.parseInt(sharedPrayerTiming.getString("Isha", "20:00").substring(0, 2));
            int minute = Integer.parseInt(sharedPrayerTiming.getString("Isha", "20:00").substring(3, 5));
            calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            Intent ishaIntent = new Intent(this, PrayerAlarmReceiver.class);
            ishaIntent.putExtra("prayer", "Isha");
            PendingIntent pendingIntentIsha = PendingIntent.getBroadcast(this, 0, ishaIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentIsha);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        source = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(source.getLatitude(), source.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0)
        {
            System.out.println("this "+addresses.get(0).getLocality());
            city = addresses.get(0).getLocality();
            countryCode = addresses.get(0).getCountryCode();
            getPrayerTimings();
        }
        else
        {
            Toast.makeText(this,"Unexpected error during finding location",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void getPrayerTimings() {
        Log.i("Abdul","getprayer called");
        requestQueue = VolleySingleton.getInstance().getmRequestQueue();

        final SharedPreferences.Editor editor = this.getSharedPreferences(SHAREDPREFPRAYERTIMING, MODE_PRIVATE).edit();
        editor.putString("location", city + ", " + countryCode);
        editor.commit();

        String url = "http://api.aladhan.com/timingsByCity?city=" + city + "&country=" + countryCode;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("Abdul","request initiated");
                            JSONObject jsonObject = response.getJSONObject("data");
                            JSONObject jsonObject2 = jsonObject.getJSONObject("date");
                            jsonObject = jsonObject.getJSONObject("timings");
                            Log.i("Abdul",jsonObject.getString("Fajr"));
                            editor.putString("Fajr", jsonObject.getString("Fajr"));
                            editor.putString("Saher", getSaher(jsonObject.getString("Fajr")));
                            editor.putString("Sunrise", jsonObject.getString("Sunrise"));
                            editor.putString("Dhuhr", jsonObject.getString("Dhuhr"));
                            editor.putString("Asr", jsonObject.getString("Asr"));
                            editor.putString("Sunset", jsonObject.getString("Sunset"));
                            editor.putString("Maghrib", jsonObject.getString("Maghrib"));
                            editor.putString("Iftar", getIftar(jsonObject.getString("Maghrib")));
                            editor.putString("Isha", jsonObject.getString("Isha"));
                            editor.putString("date", jsonObject2.getString("readable"));
                            editor.apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Unexpected error while fetching prayer timings", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private String getSaher(String fajr) {
        String time = null;
        int hour = Integer.parseInt(fajr.substring(0, 2));
        int minute = Integer.parseInt(fajr.substring(3, 5));
        int totalMinutes = 60;
        if(minute-10>=0){
            time = String.valueOf(hour) + ":" +String.valueOf(minute-10);
        }else{
            totalMinutes += minute - 10;
            hour--;
            time = "0" + String.valueOf(hour) + ":" +String.valueOf(totalMinutes);
        }
        return time;
    }

    private String getIftar(String maghrib) {

        String time = null;
        int hour = Integer.parseInt(maghrib.substring(0, 2));
        int minute = Integer.parseInt(maghrib.substring(3, 5));
        int totalMinutes = 60;
        if(minute-10>=0){
            time = String.valueOf(hour) + ":" +String.valueOf(minute-10);
        }else{
            totalMinutes += minute - 10;
            hour--;
            time = String.valueOf(hour) + ":" +String.valueOf(totalMinutes);
        }
        return time;
    }
}
