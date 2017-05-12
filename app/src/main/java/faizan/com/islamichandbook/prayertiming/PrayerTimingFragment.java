package faizan.com.islamichandbook.prayertiming;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import faizan.com.islamichandbook.R;
import faizan.com.islamichandbook.utilities.PrayerAlarmReceiver;
import faizan.com.islamichandbook.utilities.VolleySingleton;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;


public class PrayerTimingFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    TextView textViewFajr,textViewSunrise, textViewDhuhr, textViewAsr, textViewMaghrib,textViewIsha, textViewSaher, textViewIftar;
    TextView textViewLocationName;
    EditText editTextSunset;

    Calendar calendar;

    CheckBox checkBoxFajr, checkBoxSunrise, checkBoxDhuhr, checkBoxAsr, checkBoxSunset, checkBoxMaghrib, checkBoxIsha, checkBoxSaher, checkBoxIftar;

    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    String city, countryCode;

    Location source;
    GoogleApiClient mGoogleApiClient;

    public final String SHAREDPREFPRAYERTIMING = "prayer_timing";
    public final String SHAREDPREFPRAYERALARM = "prayer_alarm";

    public static PrayerTimingFragment newInstance(String param1, String param2) {
        PrayerTimingFragment fragment = new PrayerTimingFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
        //remove this later
        calendar = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prayer_timing, container, false);
        textViewFajr = (TextView) view.findViewById(R.id.textViewFajr);
        textViewSunrise = (TextView) view.findViewById(R.id.textViewSunrise);
        textViewDhuhr = (TextView) view.findViewById(R.id.textViewDhuhr);
        textViewAsr = (TextView) view.findViewById(R.id.textViewAsr);
        editTextSunset = (EditText) view.findViewById(R.id.editTextSunset);
        textViewMaghrib = (TextView) view.findViewById(R.id.textViewMaghrib);
        textViewIsha = (TextView) view.findViewById(R.id.textViewIsha);
        textViewSaher = (TextView) view.findViewById(R.id.textViewSaher);
        textViewIftar = (TextView) view.findViewById(R.id.textViewIftar);
        textViewLocationName = (TextView) view.findViewById(R.id.textViewLocationName);
        checkBoxFajr = (CheckBox) view.findViewById(R.id.checkBoxFajr);
        checkBoxSunrise = (CheckBox) view.findViewById(R.id.checkBoxSunrise);
        checkBoxDhuhr = (CheckBox) view.findViewById(R.id.checkBoxDhuhr);
        checkBoxAsr = (CheckBox) view.findViewById(R.id.checkBoxAsr);
        checkBoxSunset = (CheckBox) view.findViewById(R.id.checkBoxSunset);
        checkBoxMaghrib = (CheckBox) view.findViewById(R.id.checkBoxMaghrib);
        checkBoxIsha = (CheckBox) view.findViewById(R.id.checkBoxIsha);
        checkBoxSaher = (CheckBox) view.findViewById(R.id.checkBoxSaher);
        checkBoxIftar = (CheckBox) view.findViewById(R.id.checkBoxIftar);
        checkBoxFajr.setOnClickListener(this);
        checkBoxSunrise.setOnClickListener(this);
        checkBoxDhuhr.setOnClickListener(this);
        checkBoxAsr.setOnClickListener(this);
        checkBoxSunset.setOnClickListener(this);
        checkBoxMaghrib.setOnClickListener(this);
        checkBoxIsha.setOnClickListener(this);
        checkBoxSaher.setOnClickListener(this);
        checkBoxIftar.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        publishPrayerTimings();
        showUserAlarms();
    }

    private void showUserAlarms() {
        SharedPreferences prefs = this.getActivity().getSharedPreferences(SHAREDPREFPRAYERALARM, MODE_PRIVATE);
        checkBoxFajr.setChecked(prefs.getBoolean("Fajr",false));
        checkBoxSunrise.setChecked(prefs.getBoolean("Sunrise",false));
        checkBoxDhuhr.setChecked(prefs.getBoolean("Dhuhr",false));
        checkBoxAsr.setChecked(prefs.getBoolean("Asr",false));
        checkBoxSunset.setChecked(prefs.getBoolean("Sunset",false));
        checkBoxMaghrib.setChecked(prefs.getBoolean("Maghrib",false));
        checkBoxIsha.setChecked(prefs.getBoolean("Isha",false));
        checkBoxIsha.setChecked(prefs.getBoolean("Saher",false));
        checkBoxIsha.setChecked(prefs.getBoolean("Iftar",false));
    }

    // to put on
    private void publishPrayerTimings() {

        SharedPreferences prefs = this.getActivity().getSharedPreferences(SHAREDPREFPRAYERTIMING, MODE_PRIVATE);
        textViewLocationName.setText("Prayer Timings at "+prefs.getString("location", " - "));
        textViewFajr.setText(prefs.getString("Fajr", " - "));
        textViewSunrise.setText(prefs.getString("Sunrise", " - "));
        textViewDhuhr.setText(prefs.getString("Dhuhr"," - "));
        textViewAsr.setText(prefs.getString("Asr"," - "));
        editTextSunset.setText(prefs.getString("Sunset"," - "));
        textViewMaghrib.setText(prefs.getString("Maghrib"," - "));
        textViewIsha.setText(prefs.getString("Isha"," - "));
        textViewSaher.setText(prefs.getString("Saher"," - "));
        textViewIftar.setText(prefs.getString("Iftar"," - "));
    }

    private void getPrayerTimings() {
        requestQueue = VolleySingleton.getInstance().getmRequestQueue();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        final SharedPreferences.Editor editor = this.getActivity().getSharedPreferences(SHAREDPREFPRAYERTIMING, MODE_PRIVATE).edit();
        editor.putString("location", city+", "+countryCode);
        editor.commit();

        String url = "http://api.aladhan.com/timingsByCity?city="+city+"&country="+countryCode;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("data");
                            JSONObject jsonObject2 = jsonObject.getJSONObject("date");
                            jsonObject = jsonObject.getJSONObject("timings");
                            progressDialog.hide();
                            editor.putString("Fajr",jsonObject.getString("Fajr"));
                            editor.putString("Saher", getSaher(jsonObject.getString("Fajr")));
                            editor.putString("Sunrise",jsonObject.getString("Sunrise"));
                            editor.putString("Dhuhr",jsonObject.getString("Dhuhr"));
                            editor.putString("Asr",jsonObject.getString("Asr"));
                            editor.putString("Sunset",jsonObject.getString("Sunset"));
                            editor.putString("Maghrib",jsonObject.getString("Maghrib"));
                            editor.putString("Iftar", getIftar(jsonObject.getString("Maghrib")));
                            editor.putString("Isha",jsonObject.getString("Isha"));
                            editor.putString("date",jsonObject2.getString("readable"));
                            editor.apply();
                            publishPrayerTimings();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Toast.makeText(getActivity(),"Unexpected error while fetching prayer timings",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        progressDialog.dismiss();
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
            time = "0"+String.valueOf(hour) + ":" +String.valueOf(totalMinutes);
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

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        source = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(source.getLatitude(), source.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0)
        {
            System.out.println(addresses.get(0).getLocality());
            city = addresses.get(0).getLocality();
            countryCode = addresses.get(0).getCountryCode();
            verifyPrayerTimings();
        }
        else
        {
            Toast.makeText(getActivity(),"Unexpected error during finding location",Toast.LENGTH_SHORT).show();
        }

    }

    private void verifyPrayerTimings() {
        SharedPreferences prefs = this.getActivity().getSharedPreferences(SHAREDPREFPRAYERTIMING, MODE_PRIVATE);
        String date = prefs.getString("date","");
        String currentDate = parseDate(DateFormat.getDateInstance().format(new Date()));
        String location = prefs.getString("location","");
        if(date.compareTo(currentDate)!=0 || location.compareTo(city+", "+countryCode)!=0){
            getPrayerTimings();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onClick(View v) {
        final SharedPreferences.Editor editor = this.getActivity().getSharedPreferences(SHAREDPREFPRAYERALARM, MODE_PRIVATE).edit();
        Toast.makeText(getActivity(),"All Alarms selected/unselected will start from the next day",Toast.LENGTH_SHORT).show();
        switch(v.getId()){
            case R.id.checkBoxFajr:
                if(checkBoxFajr.isChecked()){
                    editor.putBoolean("Fajr",true);
                    Toast.makeText(getActivity(),"Fajr Alarm Set",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),"Fajr Alarm Removed",Toast.LENGTH_SHORT).show();
                    editor.putBoolean("Fajr",false);
                }
                break;
            case R.id.checkBoxSaher:
                if(checkBoxSaher.isChecked()){
                    editor.putBoolean("Saher",true);
                    Toast.makeText(getActivity(),"Saher Alarm Set",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),"Saher Alarm Removed",Toast.LENGTH_SHORT).show();
                    editor.putBoolean("Saher",false);
                }
                break;
            case R.id.checkBoxSunrise:
                if(checkBoxSunrise.isChecked()){
                    editor.putBoolean("Sunrise",true);
                    Toast.makeText(getActivity(),"Sunrise Alarm Set",Toast.LENGTH_SHORT).show();
                }else{
                    editor.putBoolean("Sunrise",false);
                    Toast.makeText(getActivity(),"Sunrise Alarm Removed",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.checkBoxDhuhr:
                if(checkBoxDhuhr.isChecked()){
                    editor.putBoolean("Dhuhr",true);
                    Toast.makeText(getActivity(),"Dhuhr Alarm Set",Toast.LENGTH_SHORT).show();
                }else{
                    editor.putBoolean("Dhuhr",false);
                    Toast.makeText(getActivity(),"Dhuhr Alarm Removed",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.checkBoxAsr:

                if(checkBoxAsr.isChecked()){
                    editor.putBoolean("Asr",true);
                    Toast.makeText(getActivity(),"Asr Alarm Set",Toast.LENGTH_SHORT).show();
                }else{
                    editor.putBoolean("Asr",false);
                    Toast.makeText(getActivity(),"Asr Alarm Removed",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.checkBoxSunset:
                //for demo
                int hour = Integer.parseInt(editTextSunset.getText().toString().substring(0, 2));
                int minute = Integer.parseInt(editTextSunset.getText().toString().substring(3, 5));
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);
                if(checkBoxSunset.isChecked()){
                    editor.putBoolean("Sunset",true);
                    Toast.makeText(getActivity(),"Sunset Alarm Set",Toast.LENGTH_SHORT).show();
                    AlarmManager  alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                    Intent sunsetIntent = new Intent(getActivity(), PrayerAlarmReceiver.class);
                    sunsetIntent.putExtra("prayer", "Sunset");
                    PendingIntent pendingIntentSunset = PendingIntent.getBroadcast(getActivity(), 0, sunsetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Log.i("Abdul",hour+" "+minute+" "+calendar.getTimeInMillis());
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntentSunset);
                }else{
                    editor.putBoolean("Sunset",false);
                    Toast.makeText(getActivity(),"Sunset Alarm Removed",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.checkBoxIftar:
                if(checkBoxIftar.isChecked()){
                    editor.putBoolean("Iftar",true);
                    Toast.makeText(getActivity(),"Iftar Alarm Set",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),"Iftar Alarm Removed",Toast.LENGTH_SHORT).show();
                    editor.putBoolean("Iftar",false);
                }
                break;
            case R.id.checkBoxMaghrib:
                if(checkBoxMaghrib.isChecked()){
                    editor.putBoolean("Maghrib",true);
                    Toast.makeText(getActivity(),"Maghrib Alarm Set",Toast.LENGTH_SHORT).show();
                }else{
                    editor.putBoolean("Maghrib",false);
                    Toast.makeText(getActivity(),"Maghrib Alarm Removed",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.checkBoxIsha:

                if(checkBoxIsha.isChecked()){
                    editor.putBoolean("Isha",true);
                    Toast.makeText(getActivity(),"Isha Alarm Set",Toast.LENGTH_SHORT).show();
                }else{
                    editor.putBoolean("Isha",false);
                    Toast.makeText(getActivity(),"Isha Alarm Removed",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        editor.commit();

    }
    //for demo
    // parsing system date in the required format
    public String parseDate(String time) {
        String inputPattern = "MMM dd, yyyy";
        String outputPattern = "dd MMM yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}
