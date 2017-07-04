package com.example.lenovo.gcloud;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class UserHome extends AppCompatActivity {

    String imei, model_no, manufacturer, email;
    SharedPreferences pref;
    ViewPager viewPager;
    TabLayout tabLayout;
    SharedPreferences.Editor editor;
    public static Activity user_home_ref;
    AlertDialog.Builder msg_dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        boolean startedAgain=savedInstanceState.getBoolean("startedAgain",false);

//        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        user_home_ref=this;

        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        imei = tm.getDeviceId();
        model_no = Build.MODEL;
        manufacturer = Build.MANUFACTURER;
        pref = getSharedPreferences("MyPref", MODE_PRIVATE);
        editor=pref.edit();
        email = pref.getString("email", "email");

        viewPager=(ViewPager) (findViewById(R.id.viewPager));
        tabLayout=(TabLayout) (findViewById(R.id.tabLayout));
        tabLayout.setupWithViewPager(viewPager);
        MyFragmentPagerAdapter mfpa=new MyFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mfpa);
        viewPager.setCurrentItem(0,true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://"+getResources().getString(R.string.ip_server)+":8084/gcloud/mobile_add_device");
                    URLConnection conn = url.openConnection();
                    conn.setRequestProperty("email", email);
                    conn.setRequestProperty("imei", imei);
                    conn.setRequestProperty("model_no", model_no);
                    conn.setRequestProperty("manufacturer", manufacturer);
                    conn.connect();
                    Log.d("MYMSG", "device recognised");
                    DataInputStream dis = new DataInputStream(conn.getInputStream());
                    String msg = dis.readLine();
                    int device_id = Integer.parseInt(dis.readLine());
                    Log.d("MYMSG", "Message : " + msg);
                    Log.d("MYMSG", "Device id : " + device_id);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("device_id", device_id);
                    editor.commit();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

            startService(new Intent(this, PhoneRing.class));

    }



    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        String names[]={"HOME","BACKUP","RESTORE"};
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0: return new Home();
                case 1: return new Upload();
                default:return new Download();
            }
        }

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return names[position];
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.myAccount){
            startActivity(new Intent(this,MyAccount.class));
        }
        else if(item.getItemId()==R.id.about_us){
            startActivity(new Intent(this,AboutUs.class));
        }
        else if(item.getItemId()==R.id.logout){
            msg_dialog=new AlertDialog.Builder(this);
            msg_dialog.setCancelable(false);
            msg_dialog.setTitle("Confirmation");
            msg_dialog.setIcon(R.drawable.alert_2_icon);
            msg_dialog.setMessage("Are you sure to logout?");
            MyListener ml=new MyListener();
            msg_dialog.setPositiveButton("Yes",ml);
            msg_dialog.setNegativeButton("No", ml);
            msg_dialog.create();
            msg_dialog.show();
        }
        return true;
    }
    class MyListener implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which==DialogInterface.BUTTON_POSITIVE){
//                ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.INVISIBLE);
                msg_dialog.setIcon(R.drawable.info_icon);
                msg_dialog.setTitle("Message");
                msg_dialog.setMessage("Logout Successful");
                msg_dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.clear();
                        editor.commit();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                });
                msg_dialog.setNegativeButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                msg_dialog.create();
                msg_dialog.show();
            }
        }
    }
}
