package com.example.lenovo.gcloud;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.*;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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


public class UploadData extends Service {
    Handler mainhandler;
    boolean sms_value, call_logs_value, contacts_value, images_value, audio_value, video_value, location_value, sms_upload_started, contacts_upload_started, call_logs_upload_started, audio_upload_started, images_upload_started, video_upload_started,sms_download_started, contacts_download_started, call_logs_download_started, audio_download_started, images_download_started, video_download_started;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String email, notification_content_text = "";
    int device_id;
    Intent intent;
    boolean manual;
    int i_sms, i_contacts, i_call_logs, i_images, i_audio, i_video;
    NotificationCompat.Builder builder;
    NotificationManager nm;
    PendingIntent pendingIntent;
    Intent in_upload;

    public UploadData() {

        mainhandler = new Handler();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.intent = intent;
        pref = getSharedPreferences("MyPref", MODE_PRIVATE);

        i_sms = pref.getInt("i_sms", 0);
        i_contacts = pref.getInt("i_contacts", 0);
        i_call_logs = pref.getInt("i_call_logs", 0);
        i_images = pref.getInt("i_images", 0);
        i_audio = pref.getInt("i_audio", 0);
        i_video = pref.getInt("i_video", 0);

        in_upload = new Intent(this, UserHome.class);
        pendingIntent = PendingIntent.getActivity(this, 0, in_upload, 0);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notification_content_text = pref.getString("notfication_content_text", "");
        manual = pref.getBoolean("manual", false);
        editor = pref.edit();
        email = pref.getString("email", "email");
        device_id = pref.getInt("device_id", 0);

        Log.d("MYMSG", "Inside on start command");
        Log.d("MYMSG", "Manual:" + manual);

        sms_value = pref.getBoolean("sms", false);
        call_logs_value = pref.getBoolean("call_logs", false);
        contacts_value = pref.getBoolean("contacts", false);
        images_value = pref.getBoolean("images", false);
        audio_value = pref.getBoolean("audio", false);
        video_value = pref.getBoolean("video", false);
        location_value = pref.getBoolean("location", false);

        Log.d("MYMSG", "SMS_VALUE:" + sms_value);
        Log.d("MYMSG", "CONTACTS_VALUE:" + contacts_value);
        Log.d("MYMSG", "CALL_LOGS_VALUE:" + call_logs_value);
        Log.d("MYMSG", "IMAGES_VALUE:" + images_value);
        Log.d("MYMSG", "AUDIO_VALUE:" + audio_value);
        Log.d("MYMSG", "VIDEO_VALUE:" + video_value);
        Log.d("MYMSG", "LOCATION_VALUE:" + location_value);

        new Thread(new StartThreads()).start();
        return START_STICKY;
    }

    class StartThreads implements Runnable {
        boolean started_sms, started_contacts, started_call_logs, started_images, started_audio, started_video;

        @Override
        public void run() {
            Log.d("MYMSG", "inside Start threads");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (sms_value) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (!started_sms) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent("uploading_sms");
                                intent.putExtra("value", i_sms);
                                sendBroadcast(intent);
                                }
                            }
                        }).start();
                    }
                    if (contacts_value) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (!started_contacts) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent("uploading_contacts");
                                    intent.putExtra("value", i_contacts);
                                    sendBroadcast(intent);
                                }
                            }
                        }).start();
                    }
                    if (call_logs_value) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (!started_call_logs) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent("uploading_call_logs");
                                    intent.putExtra("value", i_call_logs);
                                    sendBroadcast(intent);
                                }
                            }
                        }).start();
                    }
                    if (images_value) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (!started_images) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent("uploading_images");
                                    intent.putExtra("value", i_images);
                                    sendBroadcast(intent);
                                }
                            }
                        }).start();
                    }
                    if (audio_value) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (!started_audio) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent("uploading_audio");
                                    intent.putExtra("value", i_audio);
                                    sendBroadcast(intent);
                                }
                            }
                        }).start();
                    }
                    if (video_value) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (!started_video) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent("uploading_video");
                                    intent.putExtra("value", i_video);
                                    sendBroadcast(intent);
                                }
                            }
                        }).start();
                    }
                }
            }).start();
            if (location_value) {
                new Thread(new TaskSyncLocation()).start();
            }
            if (sms_value) {
                started();
                started_sms = true;
                editor.putBoolean("sms_upload_started", true);
                editor.commit();
                new Thread(new TaskSyncSms()).start();
            }
            if (contacts_value) {
                started();
                started_contacts = true;
                editor.putBoolean("contacts_upload_started", true);
                editor.commit();
                new Thread(new TaskSyncContacts()).start();
            }
            if (call_logs_value) {
                started();
                started_call_logs = true;
                editor.putBoolean("call_logs_upload_started", true);
                editor.commit();
                new Thread(new TaskSyncCallLogs()).start();
            }
            if (images_value) {
                started();
                started_images = true;
                editor.putBoolean("images_upload_started", true);
                editor.commit();
                new Thread(new TaskSyncImages()).start();
            }
            if (audio_value) {
                Log.d("MYMSG", "in audio");
                started();
                started_audio = true;
                Log.d("MYMSG", "After started");
                editor.putBoolean("audio_upload_started", true);
                editor.commit();
                new Thread(new TaskSyncAudio()).start();
                Log.d("MYMSG", "After task started");
            }
            if (video_value) {
                Log.d("MYMSG", "in video");
                started();
                started_video = true;
                editor.putBoolean("video_upload_started", true);
                editor.commit();
                new Thread(new TaskSyncVideo()).start();
            }
        }
    }

    private void started() {
        while (true) {
            SharedPreferences pref = getSharedPreferences("MyPref", MODE_PRIVATE);
            sms_upload_started = pref.getBoolean("sms_upload_started", false);
            contacts_upload_started = pref.getBoolean("contacts_upload_started", false);
            call_logs_upload_started = pref.getBoolean("call_logs_upload_started", false);
            audio_upload_started = pref.getBoolean("audio_upload_started", false);
            video_upload_started = pref.getBoolean("video_upload_started", false);
            images_upload_started = pref.getBoolean("images_upload_started", false);
            sms_download_started = pref.getBoolean("sms_download_started", false);
            contacts_download_started = pref.getBoolean("contacts_download_started", false);
            call_logs_download_started = pref.getBoolean("call_logs_download_started", false);
            images_download_started = pref.getBoolean("images_download_started", false);
            audio_download_started = pref.getBoolean("audio_download_started", false);
            video_download_started = pref.getBoolean("video_download_started", false);
            if (!sms_upload_started && !contacts_upload_started && !call_logs_upload_started && !audio_upload_started && !video_upload_started && !images_upload_started&&!sms_download_started && !contacts_download_started && !call_logs_download_started && !audio_download_started && !video_download_started && !images_download_started) {
                break;
            }
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        editor.putInt("i_sms", i_sms);
        editor.putInt("i_contacts", i_contacts);
        editor.putInt("i_call_logs", i_call_logs);
        editor.putInt("i_images", i_images);
        editor.putInt("i_audio", i_audio);
        editor.putInt("i_video", i_video);
        editor.putBoolean("sms_upload_started", false);
        editor.putBoolean("contacts_upload_started", false);
        editor.putBoolean("call_logs_upload_started", false);
        editor.putBoolean("images_upload_started", false);
        editor.putBoolean("audio_upload_started", false);
        editor.putBoolean("video_upload_started", false);
        editor.commit();

        Log.d("MYMSG", "On task removed");
    }



    class TaskSyncLocation implements Runnable {

        @Override
        public void run() {

            Log.d("MSG_LOCATION", "LOCATION upload started");
            final LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

            final mylistener ml = new mylistener();

            mainhandler.post(new Runnable() {
                @Override
                public void run() {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ml);
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ml);
                }
            });

                Intent intent = new Intent("uploading_location");
                intent.putExtra("text","Starting restore...");
                sendBroadcast(intent);
        }
    }

    public class mylistener implements LocationListener {

        Location loc;

        @Override
        public void onLocationChanged(Location location) {
//            Log.d("LOCATION","Latitude="+location.getLatitude()+",Longitude="+location.getLongitude());
            loc = location;
            Long tsLong = System.currentTimeMillis();
//            Long timestamp = Long.parseLong(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(tsLong);
            final Date finaldate = calendar.getTime();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_location_fetch");
                        URLConnection conn = url.openConnection();
                        conn.setRequestProperty("email", email);
                        conn.setRequestProperty("device_id", device_id + "");
                        conn.setRequestProperty("lat", loc.getLatitude() + "");
                        conn.setRequestProperty("lng", loc.getLongitude() + "");
                        conn.setRequestProperty("date", finaldate + "");
                        conn.connect();
                        DataInputStream dis = new DataInputStream(conn.getInputStream());
                        String msg = dis.readLine();
                        Intent intent = new Intent("uploading_location");
                        intent.putExtra("text","Location backup in progress");
                        sendBroadcast(intent);
                        Log.d("MSG_LOCATION", msg);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
//            Log.d("LOCATION","Time="+smsDate);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            Intent intent = new Intent("uploading_location");
            intent.putExtra("text","Provider is turned off");
            intent.putExtra("stop",true);
            sendBroadcast(intent);
            editor.putBoolean("location",false);
        }
    }

    class TaskSyncSms implements Runnable {
        ArrayList<Sms> al;

        @Override
        public void run() {
            Log.d("MYMSG", "SMS upload started");
            al = new ArrayList<>();
            if (i_sms == 0) {
                showProgressNotification("Starting...", 0, i_sms, true,"Backup in progress");
                Intent intent = new Intent("uploading_sms");
                intent.putExtra("value", i_sms);
                intent.putExtra("max", 0);
                sendBroadcast(intent);
                i_sms = 1;
            }
            Uri inboxuri = Telephony.Sms.CONTENT_URI;
//          Uri inboxuri=Uri.parse("content://sms/inbox");
            Cursor c = getContentResolver().query(inboxuri, null, null, null, null);

            final int COLUMNFORBODY = c.getColumnIndex("body");
            final int COLUMNFORSENDER = c.getColumnIndex("address");
            final int COLUMNFORDATE = c.getColumnIndex("date");
            while (c.moveToNext()) {
                String messagebody = c.getString(COLUMNFORBODY);
                String sender = c.getString(COLUMNFORSENDER);
                String date = c.getString(COLUMNFORDATE);

                Long timestamp = Long.parseLong(date);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
                Date finaldate = calendar.getTime();
                String smsDate = finaldate.toString();

                al.add(new Sms(sender, messagebody, smsDate));
            }
            c.close();

            try {
                URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_sms_fetch");
                for (; i_sms <= al.size(); i_sms++) {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("email", email);
                    conn.setRequestProperty("device_id", device_id + "");
                    conn.connect();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("sender", al.get(i_sms - 1).sender);
                    jsonObject.put("message", al.get(i_sms - 1).message);
                    jsonObject.put("time", al.get(i_sms - 1).time);
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(jsonObject.toString());
                    dos.flush();
                    DataInputStream dis = new DataInputStream(conn.getInputStream());
                    String msg = dis.readLine();
                    Log.d("MSG_SMS", i_sms + ":" + msg);
                    Log.d("MSG_SMS", "Sender:"+al.get(i_sms - 1).sender);
                    Log.d("MSG_SMS", "Message:"+al.get(i_sms - 1).message);
                    Log.d("MSG_SMS", "time:"+al.get(i_sms - 1).time);
                    showProgressNotification(i_sms + "/" + al.size(), al.size(), i_sms, false,"Backup in progress");
                    Intent intent = new Intent("uploading_sms");
                    intent.putExtra("value", i_sms);
                    intent.putExtra("max", al.size());
                    sendBroadcast(intent);
                }
                nm.cancel(1);
                if (notification_content_text.equals("")) {
                    notification_content_text = "SMS";
                } else {
                    notification_content_text = notification_content_text + ", SMS";
                }
                if (manual) {
                    editor.putBoolean("sms", false);
                }
                showNotification(notification_content_text);
                editor.putString("notification_content_text", notification_content_text);
                editor.putInt("i_sms", 0);
                editor.putBoolean("sms_upload_started", false);
                editor.commit();
                Log.d("MSG_SMS", "SMS upload finished");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class TaskSyncContacts implements Runnable {
        ArrayList<Contacts> al;

        public void run() {
            Log.d("MSG_CONTACTS", "CONTACTS upload started");
            al = new ArrayList<>();
            Log.d("MYMSG", "I_CONTACTS:" + i_contacts);
            if (i_contacts == 0) {
                showProgressNotification("Starting...", 0, i_contacts, true,"Backup in progress");
                Intent intent = new Intent("uploading_contacts");
                intent.putExtra("value", i_contacts);
                intent.putExtra("max", 0);
                sendBroadcast(intent);
                i_contacts = 1;
            }

            Uri contactsuri = ContactsContract.Contacts.CONTENT_URI;
            Cursor c = getContentResolver().query(contactsuri, null, null, null, null);

            final int COLUMNFORNAME = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            final int COLUMNFORID = c.getColumnIndex(ContactsContract.Contacts._ID);
            final int COLUMNFORHASPHONE = c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
            String pname = "";
            while (c.moveToNext()) {

                int id = c.getInt(COLUMNFORID);
                String displayName = c.getString(COLUMNFORNAME);
                int hasPhoneNum = c.getInt(COLUMNFORHASPHONE);
//               Log.d("CONTACTS", "Name : " + displayName);

                if (hasPhoneNum == 1) // --- Contact Has a Phone Number
                {
                    Uri uri2 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                    Cursor cnew = getContentResolver().query(uri2, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
                            null, null);
                    String pno = "";

                    while (cnew.moveToNext()) {
                        String phoneNumber = cnew.getString(cnew.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String p_no = "";
                        StringTokenizer st = new StringTokenizer(phoneNumber, " -()");
                        while (st.hasMoreTokens()) {
                            p_no += st.nextToken();
                        }
                        if (pno.equals(p_no)) {
                        } else {
                            pno = p_no;
                            al.add(new Contacts(displayName, pno));
                        }
                    }
                    cnew.close();
                } else {
                    if (pname.equals(displayName)) {
                    } else {
                        al.add(new Contacts(displayName, "No Phone no"));
                        pname = displayName;
                    }
                }
            }
            c.close();
            try {
                URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_contacts_fetch");
                for (; i_contacts <= al.size(); i_contacts++) {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("email", email);
                    conn.setRequestProperty("device_id", device_id + "");
                    conn.connect();
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("contact_name", al.get(i_contacts - 1).contact_name);
                    jsonObject.put("contact_no", al.get(i_contacts - 1).contact_no);
                    Log.d("MYMSG", "CONTACT_NAME:" + al.get(i_contacts - 1).contact_name);
                    Log.d("MYMSG", "CONTACT_NO:" + al.get(i_contacts - 1).contact_no);
                    dos.writeBytes(jsonObject.toString());
                    dos.flush();
                    DataInputStream dis = new DataInputStream(conn.getInputStream());
                    String msg = dis.readLine();
                    Log.d("MSG_CONTACTS", i_contacts + ":" + msg);
                    showProgressNotification(i_contacts + "/" + al.size(), al.size(), i_contacts, false,"Backup in progress");
                    Intent intent = new Intent("uploading_contacts");
                    intent.putExtra("value", i_contacts);
                    intent.putExtra("max", al.size());
                    sendBroadcast(intent);
                }
                nm.cancel(1);
                if (notification_content_text.equals("")) {
                    notification_content_text = "Contacts";
                } else {
                    notification_content_text = notification_content_text + ", Contacts";
                }
                if (manual) {
                    editor.putBoolean("contacts", false);
                }
                showNotification(notification_content_text);
                editor.putString("notification_content_text", notification_content_text);
                editor.putBoolean("contacts_upload_started", false);
                editor.putInt("i_contacts", 0);
                editor.commit();
                Log.d("MSG_CONTACTS", "CONTACTS upload finished");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    class TaskSyncCallLogs implements Runnable {
        ArrayList<Calllogs> al;
        public void run() {
            Log.d("MSG_CALL_LOGS", "CALL_LOGS upload started");
            al=new ArrayList<>();
            if (i_call_logs == 0) {
                showProgressNotification("Starting...", 0, i_call_logs, true,"Backup in progress");
                Intent intent = new Intent("uploading_call_logs");
                intent.putExtra("value", i_call_logs);
                intent.putExtra("max", 0);
                sendBroadcast(intent);
                i_call_logs = 1;
            }
            Uri callUri = CallLog.Calls.CONTENT_URI;
            String[] projection =
                    {
                            CallLog.Calls.NUMBER,
                            CallLog.Calls.DURATION,
                            CallLog.Calls.CACHED_NAME,
                            CallLog.Calls._ID,
                            CallLog.Calls.TYPE,
                            CallLog.Calls.DATE
                    };

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Cursor cursor = getContentResolver().query(callUri, projection, null, null, CallLog.Calls._ID + " Desc");

                while (cursor.moveToNext()) {
                    String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                    String duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
                    String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                    int call_type = Integer.parseInt(cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE)));
                    String c_type="";
                    String date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
                    Long timestamp = Long.parseLong(date);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(timestamp);
                    Date finaldate = calendar.getTime();
                    String dateString = finaldate.toString();
                    if (name == null) {
                        name="No name available";
                    }

                    Log.d("CALLLOGS", "Number : " + number);
                    Log.d("CALLLOGS", "Duration : " + duration);
                    Log.d("CALLLOGS", "Name : " + name);
                    if (call_type == CallLog.Calls.MISSED_TYPE) {
                        c_type="Missed Call";
                        Log.d("CALLLOGS", "Type : Missed");
                    } else if (call_type == CallLog.Calls.INCOMING_TYPE) {
                        c_type="Incoming Call";
                        Log.d("CALLLOGS", "Type : Incoming");
                    } else if (call_type == CallLog.Calls.OUTGOING_TYPE) {
                        c_type="Outgoing Call";
                        Log.d("CALLLOGS", "Type : Outgoing");
                    }
                    al.add(new Calllogs(name,number,duration,dateString,c_type));
                    Log.d("CALLLOGS", "------------------------------------");
                }
                cursor.close();
            try {
                URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_call_logs_fetch");
                for (; i_call_logs <= al.size(); i_call_logs++) {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("email", email);
                    conn.setRequestProperty("device_id", device_id + "");
                    conn.connect();
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("caller_name", al.get(i_call_logs - 1).caller_name);
                    jsonObject.put("caller_number", al.get(i_call_logs - 1).caller_number);
                    jsonObject.put("call_duration", al.get(i_call_logs - 1).call_duration);
                    jsonObject.put("call_time", al.get(i_call_logs - 1).call_time);
                    jsonObject.put("call_type", al.get(i_call_logs - 1).call_type);
                    dos.writeBytes(jsonObject.toString());
                    dos.flush();
                    DataInputStream dis = new DataInputStream(conn.getInputStream());
                    String msg = dis.readLine();
                    Log.d("MSG_CALLLOG", i_call_logs + ":" + msg);
                    showProgressNotification(i_call_logs + "/" + al.size(), al.size(), i_call_logs, false,"Backup in progress");
                    Intent intent = new Intent("uploading_call_logs");
                    intent.putExtra("value", i_call_logs);
                    intent.putExtra("max", al.size());
                    sendBroadcast(intent);
                }
                nm.cancel(1);
                if (notification_content_text.equals("")) {
                    notification_content_text = "Call logs";
                } else {
                    notification_content_text = notification_content_text + ", Call logs";
                }
                if (manual) {
                    editor.putBoolean("call_logs", false);
                }
                showNotification(notification_content_text);
                editor.putString("notification_content_text", notification_content_text);
                editor.putBoolean("call_logs_upload_started", false);
                editor.commit();
                Log.d("MSG_CALL_LOGS", "CALL_LOGS upload finished");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class TaskSyncImages implements Runnable {
        ArrayList<Image> al;

        @Override
        public void run() {
            Log.d("MSG_IMAGE", "Images upload started");
            al = new ArrayList<>();
            if (i_images == 0) {
                showProgressNotification("Starting...", 0, i_images, true,"Backup in progress");
                Intent intent = new Intent("uploading_images");
                intent.putExtra("value", i_images);
                intent.putExtra("max", 0);
                sendBroadcast(intent);
                i_images = 1;
            }
            Uri imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = new String[]
                    {
                            MediaStore.Images.Media.TITLE,
                            MediaStore.Images.Media.DATA
                    };

            Cursor cursor = getContentResolver().query(imagesUri, projection, null, null, null);

            while (cursor.moveToNext()) {
                String f_path = cursor.getString(1);
                String f_name = f_path.substring(f_path.lastIndexOf('/') + 1);
                al.add(new Image(f_name, f_path));
            }
            cursor.close();
            try {
                URL url1 = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_images_fetch");
                URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_images_fetch_1");
                for (; i_images<=al.size(); i_images++) {
                    String f_name = al.get(i_images-1).f_name;
                    String f_path = al.get(i_images-1).f_path;
                    File f = new File(f_path);
                    long f_length = f.length();

                    HttpURLConnection conn1 = (HttpURLConnection) url1.openConnection();
                    conn1.setRequestProperty("device_id", device_id + "");
                    conn1.setRequestProperty("f_name", f_name);
                    conn1.setRequestProperty("f_length", f_length + "");
                    conn1.connect();
                    DataInputStream dataInputStream1 = new DataInputStream(conn1.getInputStream());
                    String msg1 = dataInputStream1.readLine();
                    Log.d("MSG_IMAGES", "First msg : " + i_images + " : " + msg1);
                    if (msg1.equals("required")) {
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        //       conn.setDoOutput(true);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        byte b[] = new byte[200000];
                        conn.setChunkedStreamingMode(b.length);
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setUseCaches(false);
                        conn.setRequestProperty("Content-Type", "multipart/form-data");
                        conn.setRequestProperty("email", email);
                        conn.setRequestProperty("device_id", device_id + "");
                        conn.setRequestProperty("f_name", f_name);
                        conn.setRequestProperty("f_path", f_path);
                        conn.setRequestProperty("f_length", f_length + "");
                        conn.connect();
                        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                        DataInputStream dis = new DataInputStream(new FileInputStream(f));
                        while (true) {
                            int r = dis.read(b, 0, b.length);
                            if (r == -1) {
                                break;
                            }
                            dos.write(b, 0, r);
                        }
                        dis.close();
                        DataInputStream dataInputStream = new DataInputStream(conn.getInputStream());
                        String msg = dataInputStream.readLine();
                        Log.d("MSG_IMAGES", "Second msg : " + i_images + " : " + msg);
                    }
                    showProgressNotification(i_images + "/" + al.size(), al.size(), i_images, false,f_name);
                    Intent intent = new Intent("uploading_images");
                    intent.putExtra("value", i_images);
                    intent.putExtra("max", al.size());
                    sendBroadcast(intent);
                }
                nm.cancel(1);
                if (notification_content_text.equals("")) {
                    notification_content_text = "Images";
                } else {
                    notification_content_text = notification_content_text + ", Images";
                }
                if (manual) {
                    editor.putBoolean("images", false);
                }
                showNotification(notification_content_text);
                editor.putString("notification_content_text", notification_content_text);
                editor.putBoolean("images_upload_started", false);
                editor.commit();
                Log.d("MSG_IMAGES", "IMAGES upload finished");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    class TaskSyncAudio implements Runnable {

        ArrayList<Audio> al;

        @Override
        public void run() {
            Log.d("MSG_AUDIO", "AUDIO upload started");
            al = new ArrayList<>();
            if (i_audio == 0) {
                showProgressNotification("Starting...", 0, i_audio, true,"Backup in progress");
                Intent intent = new Intent("uploading_audio");
                intent.putExtra("value", i_audio);
                intent.putExtra("max", 0);
                sendBroadcast(intent);
                i_audio = 1;
            }
            Uri audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

            String[] projection =
                    {
                            MediaStore.Audio.Media.DATA,
                            MediaStore.Audio.Media.TITLE,
                            MediaStore.Audio.Media.ARTIST,
                            MediaStore.Audio.Media.ALBUM,};

            Cursor cursor = getContentResolver().query(audioUri, projection, selection, null, null);

            while (cursor.moveToNext()) {
                String title = cursor.getString(1);
                String artist = cursor.getString(2);
                String album = cursor.getString(3);
                String path = cursor.getString(0);
                String name = path.substring(path.lastIndexOf('/') + 1);
                al.add(new Audio(name, album, path, artist));
//                Log.d("AUDIO","Title : "+title);
//                Log.d("AUDIO","Name : "+name);
//                Log.d("AUDIO","Artist : "+artist);
//                Log.d("AUDIO","Album : "+album);
//                Log.d("AUDIO","Path : "+path);
//                Log.d("AUDIO","------------------");
//                tv1.append(" Title : " + cursor.getString(1) + "\n");
//                tv1.append(" Artist : " + cursor.getString(2) + "\n");
//                tv1.append(" Album : " + cursor.getString(3) + "\n");
//                tv1.append(" Path : " + cursor.getString(0) + "\n");
//                tv1.append("----------------------------------------\n");
            }
            cursor.close();
            try {
                URL url1 = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_audio_fetch");
                URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_audio_fetch_1");
                for (; i_audio <= al.size(); i_audio++) {
                    HttpURLConnection conn1 = (HttpURLConnection) url1.openConnection();
                    conn1.setRequestProperty("device_id", device_id + "");
                    File f = new File(al.get(i_audio-1).path);
                    conn1.setRequestProperty("f_length", f.length() + "");
                    conn1.setRequestProperty("name", al.get(i_audio-1).name);
                    conn1.setRequestProperty("album", al.get(i_audio-1).album);
                    conn1.setRequestProperty("artist", al.get(i_audio-1).artist);
                    conn1.connect();
                    DataInputStream dataInputStream1 = new DataInputStream(conn1.getInputStream());
                    String msg1 = dataInputStream1.readLine();
                    Log.d("MSG_AUDIO", "First msg : " + i_audio + " : " + msg1);
                    if (msg1.equals("required")) {
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        byte b[] = new byte[200000];
                        conn.setChunkedStreamingMode(b.length);
                        conn.setUseCaches(false);
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setRequestProperty("Content-Type", "multipart/form-data");
                        conn.setRequestProperty("email", email);
                        conn.setRequestProperty("device_id", device_id + "");
                        conn.setRequestProperty("f_length", f.length() + "");
                        conn.setRequestProperty("name", al.get(i_audio-1).name);
                        conn.setRequestProperty("album", al.get(i_audio-1).album);
                        conn.setRequestProperty("artist", al.get(i_audio-1).artist);
                        conn.setRequestProperty("path", al.get(i_audio-1).path);
                        conn.connect();
                        DataInputStream dis = new DataInputStream(new FileInputStream(f));
                        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                        while (true) {
                            int r = dis.read(b, 0, b.length);
                            if (r == -1) {
                                break;
                            }
                            dos.write(b, 0, r);
                        }
                        dis.close();
                        DataInputStream dataInputStream = new DataInputStream(conn.getInputStream());
                        String msg = dataInputStream.readLine();
                        Log.d("MSG_AUDIO", "Second msg : " + i_audio + " : " + msg);
                    }
                    showProgressNotification(i_audio + "/" + al.size(), al.size(), i_audio, false,al.get(i_audio-1).name);
                    Intent intent = new Intent("uploading_audio");
                    intent.putExtra("value", i_audio);
                    intent.putExtra("max", al.size());
                    sendBroadcast(intent);
                }
                nm.cancel(1);
                if (notification_content_text.equals("")) {
                    notification_content_text = "Audio";
                } else {
                    notification_content_text = notification_content_text + ", Audio";
                }
                if (manual) {
                    editor.putBoolean("audio", false);
                }
                showNotification(notification_content_text);
                editor.putString("notification_content_text", notification_content_text);
                editor.putBoolean("audio_upload_started", false);
                editor.commit();
                Log.d("MSG_AUDIO", "AUDIO upload finished");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class TaskSyncVideo implements Runnable {

        ArrayList<Video> al;

        @Override
        public void run() {
            Log.d("MSG_VIDEO", "VIDEO upload started");
            al = new ArrayList<>();
            if (i_video == 0) {
                showProgressNotification("Starting...", 0, i_video, true,"Backup in progress");
                Intent intent = new Intent("uploading_video");
                intent.putExtra("value", i_video);
                intent.putExtra("max", 0);
                sendBroadcast(intent);
                i_video = 1;
            }
            Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String[] projection =
                    {
                            MediaStore.Video.Media.DISPLAY_NAME,
                            MediaStore.Video.Media.SIZE,
                            MediaStore.Video.Media.DATA,
                            MediaStore.Video.Media.DURATION

                    };

            Cursor cursor = getContentResolver().query(videoUri, projection, null, null, null);

            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                int length = cursor.getInt(1);
                String path = cursor.getString(2);
                int duration = cursor.getInt(3);
                al.add(new Video(name, duration, length, path));
//                Log.d("VIDEO","Name : "+name);
//                Log.d("VIDEO","Length : "+length);
//                Log.d("VIDEO","Path : "+path);
//                Log.d("VIDEO","Duration : "+duration);
//                Log.d("VIDEO","----------------------");
//                tv1.append(" Name : " + cursor.getString(0) + "\n");
//                tv1.append(" Size (bytes): " + cursor.getString(1) + "\n");
//                tv1.append(" Path : " + cursor.getString(2) + "\n");
//                tv1.append("----------------------------------------\n");
            }
            cursor.close();
            try {
                URL url1 = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_video_fetch");
                URL url2 = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_video_fetch_1");
                for (; i_video <= al.size(); i_video++) {
                    URLConnection conn1 = url1.openConnection();
                    conn1.setRequestProperty("device_id", device_id + "");
                    conn1.setRequestProperty("name", al.get(i_video-1).name);
                    conn1.setRequestProperty("f_length", al.get(i_video-1).length + "");
                    conn1.setRequestProperty("duration", al.get(i_video-1).duration + "");
                    conn1.connect();
                    DataInputStream dataInputStream = new DataInputStream(conn1.getInputStream());
                    String msg = dataInputStream.readLine();
                    Log.d("MSG_VIDEO", "First msg : " + i_video + " : " + msg);
                    if (msg.equals("required")) {
                        HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setUseCaches(false);
                        conn.setRequestProperty("Content-Type", "multipart/form-data");
                        byte b[] = new byte[200000];
                        conn.setChunkedStreamingMode(b.length);
//                    conn.setDefaultUseCaches(false);
                        conn.setRequestProperty("email", email);
                        conn.setRequestProperty("device_id", device_id + "");
                        conn.setRequestProperty("name", al.get(i_video-1).name);
                        conn.setRequestProperty("f_length", al.get(i_video-1).length + "");
                        conn.setRequestProperty("path", al.get(i_video-1).path);
                        conn.setRequestProperty("duration", al.get(i_video-1).duration + "");
                        conn.connect();

                        File f = new File(al.get(i_video-1).path);
                        DataInputStream dis = new DataInputStream(new FileInputStream(f));
                        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                        while (true) {
                            int r = dis.read(b, 0, b.length);
                            if (r == -1) {
                                break;
                            }
                            dos.write(b, 0, r);
                        }
                        dis.close();
                        DataInputStream dataInputStream1 = new DataInputStream(conn.getInputStream());
                        Log.d("MSG_VIDEO", "Second msg : " + i_video + " : " + dataInputStream1.readLine());
                    }
                    showProgressNotification(i_video + "/" + al.size(), al.size(), i_video, false,al.get(i_video-1).name);
                    Intent intent = new Intent("uploading_video");
                    intent.putExtra("value", i_video);
                    intent.putExtra("max", al.size());
                    sendBroadcast(intent);
                }
                nm.cancel(1);
                if (notification_content_text.equals("")) {
                    notification_content_text = "Video";
                } else {
                    notification_content_text = notification_content_text + ", Video";
                }
                if (manual) {
                    editor.putBoolean("video", false);
                }
                showNotification(notification_content_text);
                editor.putString("notification_content_text", notification_content_text);
                editor.putBoolean("video_upload_started", false);
                editor.commit();
                Log.d("MSG_VIDEO", "VIDEO upload finished");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void showProgressNotification(String content_text, int max, int value, boolean indeterminate,String content_title) {
        builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.gcloud_logo));
        builder.setSmallIcon(R.drawable.cloud_upload_icon);
        builder.setContentTitle(content_title);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setProgress(max, value, indeterminate);
        builder.setContentText(content_text);
        Notification n = builder.build();
        nm.notify(1, n);
    }

    void showNotification(String notification_content_text) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setProgress(0, 0, false);
        builder.setContentTitle("Backup completed");
        builder.setContentText(notification_content_text);
        builder.setSound(uri);
        builder.setOngoing(false);
        builder.setAutoCancel(true);
        Notification n = builder.build();
        nm.notify(0, n);
    }

    class Image {
        String f_name;
        String f_path;

        Image(String f_name, String f_path) {
            this.f_name = f_name;
            this.f_path = f_path;
        }
    }

    class Audio {
        String name;
        String album;
        String path;
        String artist;

        Audio(String name, String album, String path, String artist) {
            this.name = name;
            this.path = path;
            this.album = album;
            this.artist = artist;
        }
    }

    class Video {
        String name;
        int duration;
        int length;
        String path;

        Video(String name, int duration, int length, String path) {
            this.name = name;
            this.duration = duration;
            this.length = length;
            this.path = path;
        }
    }

    class Contacts {
        String contact_name;
        String contact_no;

        Contacts(String contact_name, String contact_no) {
            this.contact_name = contact_name;
            this.contact_no = contact_no;
        }
    }

    class Sms {
        String sender, message, time;

        Sms(String sender, String message, String time) {
            this.sender = sender;
            this.message = message;
            this.time = time;
        }
    }

    class Calllogs{
        String caller_name,caller_number,call_duration,call_time,call_type;

        Calllogs(String caller_name, String caller_number, String call_duration, String call_time, String call_type) {
            this.caller_name = caller_name;
            this.caller_number = caller_number;
            this.call_duration = call_duration;
            this.call_time = call_time;
            this.call_type = call_type;
        }
    }

}
