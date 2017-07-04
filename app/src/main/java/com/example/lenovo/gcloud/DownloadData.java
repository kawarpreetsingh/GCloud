package com.example.lenovo.gcloud;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.IntDef;
import android.support.annotation.IntegerRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.LogRecord;

public class DownloadData extends Service {
    int device_id;
    SharedPreferences pref;
    String email,d_notification_content_text;
    boolean b[]=new boolean[6];
    boolean sms_upload_started, contacts_upload_started, call_logs_upload_started,manual, audio_upload_started, images_upload_started, video_upload_started,sms_download_started, contacts_download_started, call_logs_download_started, audio_download_started, images_download_started, video_download_started;
    int d_sms,d_contacts,d_call_logs,d_images,d_audio,d_video;
    SharedPreferences.Editor editor;
    NotificationCompat.Builder builder;
    NotificationManager nm;
    PendingIntent pendingIntent;
    Intent in_upload;
    public DownloadData() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        pref = getSharedPreferences("MyPref", MODE_PRIVATE);
        editor=pref.edit();
        email = pref.getString("email", "email");
        device_id=pref.getInt("device_id",0);
        b[0]=pref.getBoolean("b[0]",false);
        b[1]=pref.getBoolean("b[1]",false);
        b[2]=pref.getBoolean("b[2]",false);
        b[3]=pref.getBoolean("b[3]",false);
        b[4]=pref.getBoolean("b[4]",false);
        b[5]=pref.getBoolean("b[5]",false);

        in_upload = new Intent(this, UserHome.class);
        pendingIntent = PendingIntent.getActivity(this, 0, in_upload, 0);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        d_notification_content_text = pref.getString("d_notfication_content_text", "");
        manual = pref.getBoolean("manual", false);


        d_sms = pref.getInt("d_sms", 0);
        d_contacts = pref.getInt("d_contacts", 0);
        d_call_logs = pref.getInt("d_call_logs", 0);
        d_images = pref.getInt("d_images", 0);
        d_audio = pref.getInt("d_audio", 0);
        d_video = pref.getInt("d_video", 0);

        Log.d("MYMSG", "SMS_VALUE:" + b[0]);
        Log.d("MYMSG", "CONTACTS_VALUE:" + b[2]);
        Log.d("MYMSG", "CALL_LOGS_VALUE:" + b[1]);
        Log.d("MYMSG", "IMAGES_VALUE:" + b[3]);
        Log.d("MYMSG", "AUDIO_VALUE:" + b[4]);
        Log.d("MYMSG", "VIDEO_VALUE:" + b[5]);

        new Thread(new StartThreads()).start();

        return START_STICKY;
    }

    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        editor.putInt("d_sms", d_sms);
        editor.putInt("d_contacts", d_contacts);
        editor.putInt("d_call_logs", d_call_logs);
        editor.putInt("d_images", d_images);
        editor.putInt("d_audio", d_audio);
        editor.putInt("d_video", d_video);
        editor.putBoolean("sms_download_started", false);
        editor.putBoolean("contacts_download_started", false);
        editor.putBoolean("call_logs_download_started", false);
        editor.putBoolean("images_download_started", false);
        editor.putBoolean("audio_download_started", false);
        editor.putBoolean("video_download_started", false);
        editor.commit();

        Log.d("MYMSG", "On task removed");
    }
    class StartThreads implements Runnable {
        boolean started_sms, started_contacts, started_call_logs, started_images, started_audio, started_video;

        @Override
        public void run() {
            Log.d("MYMSG", "inside Start threads");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (b[0]) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (!started_sms) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent("downloading_sms");
                                    intent.putExtra("value", d_sms);
                                    sendBroadcast(intent);
                                }
                            }
                        }).start();
                    }
                    if (b[2]) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (!started_contacts) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent("downloading_contacts");
                                    intent.putExtra("value", d_contacts);
                                    sendBroadcast(intent);
                                }
                            }
                        }).start();
                    }
                    if (b[1]) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (!started_call_logs) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent("downloading_call_logs");
                                    intent.putExtra("value", d_call_logs);
                                    sendBroadcast(intent);
                                }
                            }
                        }).start();
                    }
                    if (b[3]) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (!started_images) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent("downloading_images");
                                    intent.putExtra("value", d_images);
                                    sendBroadcast(intent);
                                }
                            }
                        }).start();
                    }
                    if (b[4]) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (!started_audio) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent("downloading_audio");
                                    intent.putExtra("value", d_audio);
                                    sendBroadcast(intent);
                                }
                            }
                        }).start();
                    }
                    if (b[5]) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (!started_video) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent("downloading_video");
                                    intent.putExtra("value", d_video);
                                    sendBroadcast(intent);
                                }
                            }
                        }).start();
                    }
                }
            }).start();

            if (b[0]) {
                started();
                started_sms = true;
                editor.putBoolean("sms_download_started", true);
                editor.commit();
                download_sms();
            }
            if (b[2]) {
                started();
                started_contacts = true;
                editor.putBoolean("contacts_download_started", true);
                editor.commit();
                download_contacts();
            }
            if (b[1]) {
                started();
                started_call_logs = true;
                editor.putBoolean("call_logs_download_started", true);
                editor.commit();
                download_call_logs();
            }
            if (b[3]) {
                started();
                started_images = true;
                editor.putBoolean("images_download_started", true);
                editor.commit();
                download_images();
            }
            if (b[4]) {
                Log.d("MYMSG", "in audio");
                started();
                started_audio = true;
                Log.d("MYMSG", "After started");
                editor.putBoolean("audio_download_started", true);
                editor.commit();
                download_audio();
                Log.d("MYMSG", "After task started");
            }
            if (b[5]) {
                Log.d("MYMSG", "in video");
                started();
                started_video = true;
                editor.putBoolean("video_download_started", true);
                editor.commit();
                download_video();
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
    private void download_sms() {
        new Thread(new Runnable() {
            ArrayList<SMS> db_sms, phone_sms;

            @Override
            public void run() {
                try {
                    Log.d("MSG_SMS", "download started");
                    if (d_sms == 0) {
                        showProgressNotification("Starting...", 0, d_sms, true,"Restore in progress");
                        Intent intent = new Intent("downloading_sms");
                        intent.putExtra("value", d_sms);
                        intent.putExtra("max", 0);
                        sendBroadcast(intent);
                        d_sms = 1;
                    }
                    URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_sms_download");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("device_id", device_id + "");
                    conn.connect();
                    DataInputStream dis = new DataInputStream(conn.getInputStream());
                    JSONObject json = new JSONObject(dis.readLine());
                    String manufacturer = json.getString("manufacturer");
                    String model_no = json.getString("model_no");
                    JSONObject jsonObject = json.getJSONObject("sms");
                    JSONArray jsonArray = jsonObject.getJSONArray("sms_array");
                    db_sms = new ArrayList<>();
                    phone_sms = new ArrayList<>();
                    for (; d_sms <=jsonArray.length(); d_sms++) {
                        JSONObject js = jsonArray.getJSONObject(d_sms-1);
                        String sender = js.getString("sender");
                        String message = js.getString("message");
                        String time = js.getString("time");
                        db_sms.add(new SMS(sender, message, time));
                        showProgressNotification(d_sms + "/" + jsonArray.length(), jsonArray.length(), d_sms, false,"Restore in progress");
                        Intent intent = new Intent("downloading_sms");
                        intent.putExtra("value", d_sms);
                        intent.putExtra("max", jsonArray.length());
                        sendBroadcast(intent);
                        Log.d("MSG_SMS", "I:" + d_sms);
                    }
                    dis.close();
                    Log.d("MSG_SMS", "download finished");
                    nm.cancel(2);

                    showProgressNotification("Fetching extra data", 0, d_sms, true,"Restore in progress");
                    Intent intent = new Intent("downloading_sms");
                    intent.putExtra("value", d_sms);
                    intent.putExtra("max", 0);
                    sendBroadcast(intent);

                    Log.d("MSG_SMS", "phone fetch started");
                    Uri inboxuri = Telephony.Sms.CONTENT_URI;
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

                        phone_sms.add(new SMS(sender, messagebody, smsDate));
                    }
                    Log.d("MSG_SMS", "Phone fetch finished");
                    for (int i = db_sms.size() - 1; i > 0; i--) {
                        for (int j = 0; j < phone_sms.size(); j++) {
                            if (db_sms.get(i).sender.equals(phone_sms.get(j).sender) && db_sms.get(i).time.equals(phone_sms.get(j).time)) {
                                db_sms.remove(i);
                                break;
                            }
                        }
                    }
                    nm.cancel(2);
                    showProgressNotification("Writing into file", 0, d_sms, true,"Restore in progress");
                    intent.putExtra("value", d_sms);
                    intent.putExtra("max", 0);
                    sendBroadcast(intent);

                    Log.d("MSG_SMS", "Extra sms got");
                    Log.d("MSG_SMS", "write into file started");
                    write_sms_into_file(db_sms, manufacturer, model_no);
                    Log.d("MSG_SMS", "write into file finished");

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void write_sms_into_file(ArrayList<SMS> db_sms, String manufacturer, String model_no) {
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "GCloud" + File.separator + "SMS" + File.separator + manufacturer + File.separator + model_no);
        Log.d("MSG_SMS", "Folders created" + folder.mkdirs());
        try {
            PrintWriter pw = new PrintWriter(folder + File.separator + device_id + ".txt");
            for (int i = 0; i < db_sms.size(); i++) {
                pw.println("SMS : " + (i + 1));
                pw.println("Sender : " + db_sms.get(i).sender);
                pw.println("Message : " + db_sms.get(i).message);
                pw.println("Time : " + db_sms.get(i).time);
                pw.println("----------------------------");
                pw.flush();
            }
            nm.cancel(2);
            if (d_notification_content_text.equals("")) {
                d_notification_content_text = "SMS";
            } else {
                d_notification_content_text = d_notification_content_text + ", SMS";
            }
            if (manual) {
                editor.putBoolean("b[0]", false);
            }
            Intent intent = new Intent("downloading_sms");
            intent.putExtra("value", d_sms);
            intent.putExtra("max", -1);
            sendBroadcast(intent);
            showNotification(d_notification_content_text);

            editor.putString("d_notification_content_text", d_notification_content_text);
            editor.putInt("d_sms", 0);
            editor.putBoolean("sms_download_started", false);
            editor.commit();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void download_images() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("MSG_IMAGE", "image download started");
                    if (d_images == 0) {
                        showProgressNotification("Starting...", 0, d_images, true,"Restore in progress");
                        Intent intent = new Intent("downloading_images");
                        intent.putExtra("value", d_images);
                        intent.putExtra("max", 0);
                        sendBroadcast(intent);
                        d_images = 1;
                    }
                    URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_images_download");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("device_id", device_id + "");
                    conn.connect();
                    DataInputStream dis = new DataInputStream(conn.getInputStream());
                    JSONObject jsonObject = new JSONObject(dis.readLine());
                    JSONArray jsonArray = jsonObject.getJSONArray("image_ids");
                    URL url1 = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_images_download1");
                    for (;d_images<= jsonArray.length(); d_images++) {
                        JSONObject js = jsonArray.getJSONObject(d_images-1);
                        int image_id = js.getInt("image_id");
                        HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Connection", "Keep-Alive");
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        byte b[] = new byte[200000];
                        connection.setChunkedStreamingMode(b.length);
                        connection.setUseCaches(false);
                        connection.setRequestProperty("Content-Type", "multipart/form-data");
                        connection.setRequestProperty("image_id", image_id + "");
                        connection.connect();
                        String image_name = connection.getHeaderField("image_name");
                        DataInputStream dis1 = new DataInputStream(connection.getInputStream());
                        File f = new File(getExternalCacheDir(), "GCloud" + File.separator + "Images");
                        //      Log.d("Folder",getExternalCacheDir()+"");
                        f.mkdirs();
                        FileOutputStream fos = new FileOutputStream(f + File.separator + image_name);
//                        byte b1[]=new byte[200000];
                        while (true) {
                            int r = dis1.read(b, 0, b.length);
//                            Log.d("r",r+"");
                            if (r == -1) {
                                break;
                            }
                            fos.write(b, 0, r);
                        }
                        fos.close();
                        dis1.close();
//                        DataInputStream dataInputStream=new DataInputStream(connection.getInputStream());
                        Log.d("MSG_IMAGE", "Image_" + d_images);
                        showProgressNotification(d_images + "/" + jsonArray.length(), jsonArray.length(), d_images, false,image_name);
                        Intent intent = new Intent("downloading_images");
                        intent.putExtra("value", d_images);
                        intent.putExtra("max", jsonArray.length());
                        sendBroadcast(intent);
//                        dataInputStream.close();
                    }
                    Log.d("MSG_IMAGE", "Done");
                    nm.cancel(2);
                    if (d_notification_content_text.equals("")) {
                        d_notification_content_text = "Images";
                    } else {
                        d_notification_content_text = d_notification_content_text + ", Images";
                    }
                    if (manual) {
                        editor.putBoolean("b[3]", false);
                    }
                    showNotification(d_notification_content_text);
                    editor.putString("d_notification_content_text", d_notification_content_text);
                    editor.putInt("d_images", 0);
                    editor.putBoolean("images_download_started", false);
                    editor.commit();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void download_audio() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("MSG_AUDIO", "audio download started");
                    if (d_audio == 0) {
                        showProgressNotification("Starting...", 0, d_audio, true,"Restore in progress");
                        Intent intent = new Intent("downloading_audio");
                        intent.putExtra("value", d_audio);
                        intent.putExtra("max", 0);
                        sendBroadcast(intent);
                        d_audio = 1;
                    }
                    URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_audio_download");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("device_id", device_id + "");
                    conn.connect();
                    DataInputStream dis = new DataInputStream(conn.getInputStream());
                    JSONObject jsonObject = new JSONObject(dis.readLine());
                    JSONArray jsonArray = jsonObject.getJSONArray("audio_ids");
                    URL url1 = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_audio_download1");
                    for (;d_audio<= jsonArray.length(); d_audio++) {
                        JSONObject js = jsonArray.getJSONObject(d_audio-1);
                        int audio_id = js.getInt("audio_id");
                        HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Connection", "Keep-Alive");
                        connection.setRequestProperty("audio_id", audio_id + "");
                        connection.setDoOutput(true);
                        connection.setDoInput(true);
                        byte b[] = new byte[200000];
                        connection.setChunkedStreamingMode(b.length);
                        connection.setUseCaches(false);
                        connection.setRequestProperty("Content-Type", "multipart/form-data");
                        connection.connect();
                        DataInputStream dataInputStream = new DataInputStream(connection.getInputStream());
                        File f = new File(getExternalCacheDir(), "GCloud" + File.separator + "Audio");
                        f.mkdirs();
                        String audio_name = connection.getHeaderField("audio_name");
                        FileOutputStream fileOutputStream = new FileOutputStream(f + File.separator + audio_name);
                        while (true) {
                            int r = dataInputStream.read(b, 0, b.length);
                            if (r == -1) {
                                break;
                            }
                            fileOutputStream.write(b, 0, r);
                        }
                        fileOutputStream.close();
                        dataInputStream.close();
//                        DataInputStream dataInputStream1=new DataInputStream(connection.getInputStream());
                        Log.d("MSG_AUDIO", "Audio_" + d_audio);
                        showProgressNotification(d_audio + "/" + jsonArray.length(), jsonArray.length(), d_audio, false,audio_name);
                        Intent intent = new Intent("downloading_audio");
                        intent.putExtra("value", d_audio);
                        intent.putExtra("max", jsonArray.length());
                        sendBroadcast(intent);
                    }
                    Log.d("MSG_AUDIO", "Done");
                    nm.cancel(2);
                    if (d_notification_content_text.equals("")) {
                        d_notification_content_text = "Audio";
                    } else {
                        d_notification_content_text = d_notification_content_text + ", Audio";
                    }
                    if (manual) {
                        editor.putBoolean("b[4]", false);
                    }
                    showNotification(d_notification_content_text);
                    editor.putString("d_notification_content_text", d_notification_content_text);
                    editor.putInt("d_audio", 0);
                    editor.putBoolean("audio_download_started", false);
                    editor.commit();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void download_video() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("MSG_VIDEO", "video download started");
                    if (d_video == 0) {
                        showProgressNotification("Starting...", 0, d_video, true,"Restore in progress");
                        Intent intent = new Intent("downloading_video");
                        intent.putExtra("value", d_video);
                        intent.putExtra("max", 0);
                        sendBroadcast(intent);
                        d_video = 1;
                    }
                    URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_video_download");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("device_id", device_id + "");
                    conn.connect();
                    DataInputStream dis = new DataInputStream(conn.getInputStream());
                    JSONObject jsonObject = new JSONObject(dis.readLine());
                    JSONArray jsonArray = jsonObject.getJSONArray("video_ids");
                    URL url1 = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_video_download1");
                    for (; d_video<= jsonArray.length(); d_video++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(d_video-1);
                        int video_id = jsonObject1.getInt("video_id");
                        HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Connection", "Keep-Alive");
                        connection.setDoOutput(true);
                        connection.setDoInput(true);
                        connection.setRequestProperty("Content-Type", "multipart/form-data");
                        byte b[] = new byte[200000];
                        connection.setChunkedStreamingMode(b.length);
                        connection.setUseCaches(false);
                        connection.setRequestProperty("video_id", video_id + "");
                        connection.connect();
                        DataInputStream dataInputStream = new DataInputStream(connection.getInputStream());
                        File f = new File(getExternalCacheDir(), "GCloud" + File.separator + "Video");
                        f.mkdirs();
                        String video_name = connection.getHeaderField("video_name");
                        FileOutputStream fileOutputStream = new FileOutputStream(f + File.separator + video_name);
                        while (true) {
                            int r = dataInputStream.read(b, 0, b.length);
                            if (r == -1) {
                                break;
                            }
                            fileOutputStream.write(b, 0, r);
                        }
                        dataInputStream.close();
                        fileOutputStream.close();
//                        DataInputStream dataInputStream1=new DataInputStream(connection.getInputStream());
                        Log.d("MSG_VIDEO", "Video_" + d_video);

                        showProgressNotification(d_video + "/" + jsonArray.length(), jsonArray.length(), d_video, false,video_name);
                        Intent intent = new Intent("downloading_video");
                        intent.putExtra("value", d_video);
                        intent.putExtra("max", jsonArray.length());
                        sendBroadcast(intent);
                    }
                    Log.d("MSG_VIDEO", "Done");
                    nm.cancel(2);
                    if (d_notification_content_text.equals("")) {
                        d_notification_content_text = "Video";
                    } else {
                        d_notification_content_text = d_notification_content_text + ", Video";
                    }
                    if (manual) {
                        editor.putBoolean("b[5]", false);
                    }
                    showNotification(d_notification_content_text);
                    editor.putString("d_notification_content_text", d_notification_content_text);
                    editor.putInt("d_video", 0);
                    editor.putBoolean("video_download_started", false);
                    editor.commit();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void download_call_logs() {
        new Thread(new Runnable() {
            ArrayList<CallLogs> db_call_logs = new ArrayList<CallLogs>();
            ArrayList<CallLogs> phone_call_logs = new ArrayList<CallLogs>();

            @Override
            public void run() {
                try {
                    Log.d("MSG_CALL_LOGS", "download started");
                    if (d_call_logs == 0) {
                        showProgressNotification("Starting...", 0, d_call_logs, true,"Restore in progress");
                        Intent intent = new Intent("downloading_call_logs");
                        intent.putExtra("value", d_call_logs);
                        intent.putExtra("max", 0);
                        sendBroadcast(intent);
                        d_call_logs = 1;
                    }
                    URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_call_logs_download");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("device_id", device_id + "");
                    conn.connect();
                    DataInputStream dis = new DataInputStream(conn.getInputStream());
                    JSONObject json = new JSONObject(dis.readLine());
                    String manufacturer = json.getString("manufacturer");
                    String model_no = json.getString("model_no");
                    JSONObject jsonObject = json.getJSONObject("call_logs");
                    JSONArray jsonArray = jsonObject.getJSONArray("call_logs_array");
                    for (; d_call_logs<= jsonArray.length(); d_call_logs++) {
                        JSONObject js = jsonArray.getJSONObject(d_call_logs-1);
                        String caller_name = js.getString("caller_name");
                        String caller_number = js.getString("caller_number");
                        String call_duration = js.getString("call_duration");
                        String call_time = js.getString("call_time");
                        String call_type = js.getString("call_type");
//                        Log.d("CALL_LOGS", "Caller_name : " + caller_name);
//                        Log.d("CALL_LOGS", "Caller_number : " + caller_number);
//                        Log.d("CALL_LOGS", "Call_duration : " + call_duration);
//                        Log.d("CALL_LOGS", "Call_time : " + call_time);
//                        Log.d("CALL_LOGS", "Caller_type : " + call_type);
                        db_call_logs.add(new CallLogs(caller_name, caller_number, call_duration, call_time, call_type));
                        Log.d("CALL_LOGS", "I:" + d_call_logs);
                        showProgressNotification(d_call_logs + "/" + jsonArray.length(), jsonArray.length(), d_call_logs, false,"Restore in progress");
                        Intent intent = new Intent("downloading_call_logs");
                        intent.putExtra("value", d_call_logs);
                        intent.putExtra("max", jsonArray.length());
                        sendBroadcast(intent);
                    }
                    dis.close();
                    Log.d("MSG_CALL_LOGS", "download finished");

                    nm.cancel(2);
                    showProgressNotification("Fetching extra data", 0, d_call_logs, true,"Restore in progress");
                    Intent intent = new Intent("downloading_call_logs");
                    intent.putExtra("value", d_call_logs);
                    intent.putExtra("max", 0);
                    sendBroadcast(intent);

                    Log.d("MSG_CALL_LOGS", "Fetching from phone call logs started");

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
                    String caller_name = "", duration = "", number = "", call_type1 = "", dateString = "";
                    while (cursor.moveToNext()) {
                        number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                        duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));
                        String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                        int call_type = Integer.parseInt(cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE)));
                        String date = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));

                        Long timestamp = Long.parseLong(date);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(timestamp);
                        Date finaldate = calendar.getTime();
                        dateString = finaldate.toString();

                        caller_name = "";
                        if (name == null) {
                            caller_name = "No name available";
                        } else {
                            caller_name = name;
                        }
//                            Log.d("CALLLOGS", "Number : " + number);
//                            Log.d("CALLLOGS", "Duration : " + duration);
//                            Log.d("CALLLOGS", "Name : " + name);
                        call_type1 = "";
                        if (call_type == CallLog.Calls.MISSED_TYPE) {
                            call_type1 = "Missed Call";
//                                Log.d("CALLLOGS", "Type : Missed");
                        } else if (call_type == CallLog.Calls.INCOMING_TYPE) {
                            call_type1 = "Incoming Call";
//                                Log.d("CALLLOGS", "Type : Incoming");
                        } else if (call_type == CallLog.Calls.OUTGOING_TYPE) {
                            call_type1 = "Outgoing Call";
//                                Log.d("CALLLOGS", "Type : Outgoing");
                        }
//                    cursor.close();
                        phone_call_logs.add(new CallLogs(caller_name, number, duration, dateString, call_type1));
                    }

                    Log.d("MSG_CALL_LOGS", "Fetching from phone call logs ended");

                    for (int i = db_call_logs.size() - 1; i >= 0; i--) {
                        for (int j = 0; j < phone_call_logs.size(); j++) {
                            if (db_call_logs.get(i).caller_number.equals(phone_call_logs.get(j).caller_number) && db_call_logs.get(i).call_time.equals(phone_call_logs.get(j).call_time)) {
                                db_call_logs.remove(i);
                                break;
                            }
                        }
                    }
                    nm.cancel(2);
                    showProgressNotification("Writing into file", 0, d_call_logs, true,"Restore in progress");
                    intent.putExtra("value", d_call_logs);
                    intent.putExtra("max", 0);
                    sendBroadcast(intent);

                    Log.d("MSG_CALL_LOGS", "Adding call_logs started");
//                    if(write) {
//                        if (inFile) {
                    write_call_logs_into_file(db_call_logs, manufacturer, model_no);
//                        }
//                        else{
//                            write_call_logs_into_phone(db_call_logs);
//                        }
//                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void write_call_logs_into_file(ArrayList<CallLogs> db_call_logs, String manufacturer, String model_no) {
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "GCloud" + File.separator + "Call Logs" + File.separator + manufacturer + File.separator + model_no);
        Log.d("MKDIR", f.mkdirs() + "");
        File file = new File(f.getAbsolutePath() + File.separator + device_id + ".txt");
        Log.d("Path", file.getAbsolutePath());
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(file));

            for (int i = 0; i < db_call_logs.size(); i++) {
                pw.println("Call : " + (i + 1));
                pw.println("Caller Name : " + db_call_logs.get(i).caller_name);
                pw.println("Caller No : " + db_call_logs.get(i).caller_number);
                pw.println("Call duration : " + db_call_logs.get(i).call_duration);
                pw.println("Call time : " + db_call_logs.get(i).call_time);
                pw.println("Call type : " + db_call_logs.get(i).call_type);
                pw.println("------------------------------");
                pw.flush();
            }
            nm.cancel(2);
            if (d_notification_content_text.equals("")) {
                d_notification_content_text = "Call logs";
            } else {
                d_notification_content_text = d_notification_content_text + ", Call logs";
            }
            if (manual) {
                editor.putBoolean("b[1]", false);
            }
            Intent intent = new Intent("downloading_call_logs");
            intent.putExtra("value", d_call_logs);
            intent.putExtra("max", -1);
            sendBroadcast(intent);
            showNotification(d_notification_content_text);

            editor.putString("d_notification_content_text", d_notification_content_text);
            editor.putInt("d_call_logs", 0);
            editor.putBoolean("call_logs_download_started", false);
            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write_call_logs_into_phone(ArrayList<CallLogs> db_call_logs) {
        for (int i = 0; i < db_call_logs.size(); i++) {
            long timeInMilliseconds = 0;
            int call_type = 0;
            if (db_call_logs.get(i).call_type.equals("Incoming Call")) {
                call_type = CallLog.Calls.INCOMING_TYPE;
            } else if (db_call_logs.get(i).call_type.equals("Outgoing Call")) {
                call_type = CallLog.Calls.OUTGOING_TYPE;
            } else if (db_call_logs.get(i).call_type.equals("Missed Call")) {
                call_type = CallLog.Calls.MISSED_TYPE;
            } else if (db_call_logs.get(i).call_type.equals("Voice Mail")) {
                call_type = CallLog.Calls.VOICEMAIL_TYPE;
            } else if (db_call_logs.get(i).call_type.equals("Rejected")) {
                call_type = CallLog.Calls.REJECTED_TYPE;
            } else if (db_call_logs.get(i).call_type.equals("Blocked")) {
                call_type = CallLog.Calls.BLOCKED_TYPE;
            }
            Date d = new Date();
            String givenDateString = db_call_logs.get(i).call_time;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                Date mDate = sdf.parse(givenDateString);
                timeInMilliseconds = mDate.getTime();
                //  Log.d("Time in ms",timeInMilliseconds+"");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            d.getTime();
            ContentValues values = new ContentValues();
            values.put(CallLog.Calls.NUMBER, db_call_logs.get(i).caller_number);
            values.put(CallLog.Calls.DATE, timeInMilliseconds);
            values.put(CallLog.Calls.DURATION, db_call_logs.get(i).call_duration);
            values.put(CallLog.Calls.TYPE, call_type);
            values.put(CallLog.Calls.NEW, 1);
            values.put(CallLog.Calls.CACHED_NAME, "");
            values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
            values.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
        }
    }

    private void download_contacts() {

        new Thread(new Runnable() {
            ArrayList<Contacts> db_contacts = new ArrayList<>();
            ArrayList<Contacts> phone_contacts = new ArrayList<>();

            @Override
            public void run() {
                try {
                    Log.d("MSG_CONTACTS", "download started");
                    if (d_contacts == 0) {
                        showProgressNotification("Starting...", 0, d_contacts, true,"Restore in progress");
                        Intent intent = new Intent("downloading_contacts");
                        intent.putExtra("value", d_contacts);
                        intent.putExtra("max", 0);
                        sendBroadcast(intent);
                        d_contacts = 1;
                    }
                    URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_contacts_download");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("device_id", device_id + "");
                    conn.connect();
                    DataInputStream dis = new DataInputStream(conn.getInputStream());
                    JSONObject jsonObject = new JSONObject(dis.readLine());
                    JSONArray jsonArray = jsonObject.getJSONArray("contacts");
                    for (; d_contacts <= jsonArray.length(); d_contacts++) {
                        JSONObject js = jsonArray.getJSONObject(d_contacts-1);
                        String contact_name = js.getString("contact_name");
                        String contact_no = js.getString("contact_no");
                        db_contacts.add(new Contacts(contact_name, contact_no));
//                        Log.d("CONTACTS", "Contact_name : " + contact_name);
//                        Log.d("CONTACTS", "Contact_no : " + contact_no);
//                        Log.d("CONTACTS","I:"+i);
                        showProgressNotification(d_contacts + "/" + jsonArray.length(), jsonArray.length(), d_contacts, false,"Restore in progress");
                        Intent intent = new Intent("downloading_contacts");
                        intent.putExtra("value", d_contacts);
                        intent.putExtra("max", jsonArray.length());
                        sendBroadcast(intent);
                    }
                    dis.close();
                    Log.d("MSG_CONTACTS", "download finished");
                    nm.cancel(2);
                    showProgressNotification("Fetching extra data", 0, d_contacts, true,"Restore in progress");
                    Intent intent = new Intent("downloading_contacts");
                    intent.putExtra("value", d_contacts);
                    intent.putExtra("max", 0);
                    sendBroadcast(intent);

                    Log.d("MSG_CONTACTS", "compairing to existing contacts");

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
                                    phone_contacts.add(new Contacts(displayName, pno));
                                }
                            }
                            cnew.close();
                        } else {
                            if (pname.equals(displayName)) {
                            } else {
                                phone_contacts.add(new Contacts(displayName, "No Phone no"));
                                pname = displayName;
                            }
                        }
                    }
                    c.close();
                    for (int i = db_contacts.size() - 1; i >= 0; i--) {
                        for (int j = 0; j < phone_contacts.size(); j++) {
//                            Log.d("I",i+"");
//                            Log.d("J",j+"");
                            if (db_contacts.get(i).contact_no.equals(phone_contacts.get(j).contact_no)) {
                                db_contacts.remove(i);
                                break;
                            }
                        }
                    }
//                    Log.d("MSG_CONTACTS","----------------------------------------------------");
//                    Log.d("MSG_CONTACTS","DB_CONTACTS");
//                    for(int i=0;i<db_contacts.size();i++){
//                        Log.d("MSG_CONTACTS","Contact_Name:"+db_contacts.get(i).contact_name);
//                        Log.d("MSG_CONTACTS","Contact_No:"+db_contacts.get(i).contact_no);
//                        Log.d("MSG_CONTACTS","I:"+i);
//                    }
//                    Log.d("MSG_CONTACTS","----------------------------------------------------");
//
//                    Log.d("MSG_CONTACTS","----------------------------------------------------");
//                    Log.d("MSG_CONTACTS","PHONE_CONTACTS");
//                    for(int i=0;i<phone_contacts.size();i++){
//                        Log.d("MSG_CONTACTS","Contact_Name:"+phone_contacts.get(i).contact_name);
//                        Log.d("MSG_CONTACTS","Contact_No:"+phone_contacts.get(i).contact_no);
//                        Log.d("MSG_CONTACTS","I:"+i);
//                    }
//                    Log.d("MSG_CONTACTS","----------------------------------------------------");

                    nm.cancel(2);
                    showProgressNotification("Writing into phone", 0, d_contacts, true,"Restore in progress");
                    intent.putExtra("value", d_contacts);
                    intent.putExtra("max", 0);
                    sendBroadcast(intent);

                    for (int i = 0; i < db_contacts.size(); i++) {
                        Log.d("ADDED_CONTACT", db_contacts.get(i).contact_name);
                        write_into_contacts(getApplicationContext(), db_contacts.get(i).contact_name, db_contacts.get(i).contact_no);
                    }
                    nm.cancel(2);
                    if (d_notification_content_text.equals("")) {
                        d_notification_content_text = "Contacts";
                    } else {
                        d_notification_content_text = d_notification_content_text + ", Contacts";
                    }
                    if (manual) {
                        editor.putBoolean("b[2]", false);
                    }
                    intent.putExtra("value", d_contacts);
                    intent.putExtra("max", -1);
                    sendBroadcast(intent);
                    showNotification(d_notification_content_text);
                    editor.putString("d_notification_content_text", d_notification_content_text);
                    editor.putInt("d_contacts", 0);
                    editor.putBoolean("contacts_download_started", false);
                    editor.commit();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void write_into_contacts(Context con, String displayName, String number) {
        Context context = con; //Application's context or Activity's context
        String strDisplayName = displayName; // Name of the Person to add
        String strNumber = number; //number of the person to add with the Contact

        ArrayList<ContentProviderOperation> cntProOper = new ArrayList<ContentProviderOperation>();
        int contactIndex = cntProOper.size();//ContactSize

        //Newly Inserted contact
        // A raw contact will be inserted ContactsContract.RawContacts table in contacts database.
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)//Step1
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        //Display name will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step2
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, strDisplayName) // Name of the contact
                .build());
        //Mobile number will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)//Step 3
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, strNumber) // Number to be added
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build()); //Type like HOME, MOBILE etc
        try {
            // We will do batch operation to insert all above data
            //Contains the output of the app of a ContentProviderOperation.
            //It is sure to have exactly one of uri or count set
            ContentProviderResult[] contentProresult = null;
            contentProresult = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, cntProOper); //apply above data insertion into contacts list
        } catch (RemoteException exp) {
            //logs;
        } catch (OperationApplicationException exp) {
            //logs
        }
    }

    void showProgressNotification(String content_text, int max, int value, boolean indeterminate,String content_title) {
        builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.gcloud_logo));
        builder.setSmallIcon(R.drawable.cloud_download_icon);
        builder.setContentTitle(content_title);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);
        builder.setProgress(max, value, indeterminate);
        builder.setContentText(content_text);
        Notification n = builder.build();
        nm.notify(2, n);
    }

    void showNotification(String notification_content_text) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setProgress(0, 0, false);
        builder.setContentTitle("Restore completed");
        builder.setContentText(notification_content_text);
        builder.setSound(uri);
        builder.setOngoing(false);
        builder.setAutoCancel(true);
        Notification n = builder.build();
        nm.notify(3, n);
    }

    class Contacts {
        String contact_name;
        String contact_no;

        Contacts(String contact_name, String contact_no) {
            this.contact_name = contact_name;
            this.contact_no = contact_no;
        }
    }

    class CallLogs {
        String caller_name;
        String caller_number;
        String call_duration;
        String call_time;
        String call_type;

        CallLogs(String caller_name, String caller_number, String call_duration, String call_time, String call_type) {
            this.caller_name = caller_name;
            this.caller_number = caller_number;
            this.call_duration = call_duration;
            this.call_time = call_time;
            this.call_type = call_type;
        }
    }

    class SMS {
        String sender;
        String message;
        String time;

        SMS(String sender, String message, String time) {
            this.sender = sender;
            this.message = message;
            this.time = time;
        }
    }
}
