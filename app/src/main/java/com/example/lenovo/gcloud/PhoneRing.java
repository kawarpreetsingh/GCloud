package com.example.lenovo.gcloud;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class PhoneRing extends Service {
    int device_id ;
    Handler handler = new Handler();
    public PhoneRing() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences pref = getSharedPreferences("MyPref", MODE_PRIVATE);
        device_id=pref.getInt("device_id",0);
        task();
        return START_STICKY;
    }
    private void task(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        try {
                            Thread.sleep(4000);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        URL url=new URL("http://"+getResources().getString(R.string.ip_server)+":8084/gcloud/mobile_ringing_check");
                        URLConnection con =url.openConnection();
                        con.setRequestProperty("device_id",device_id+"");
                        con.connect();
                        DataInputStream dis=new DataInputStream(con.getInputStream());
                        String msg=dis.readLine();
                        if(msg.equals("success")){}
                        int startFrom = 1000;
                        int endAt = 21000;
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);


                        Runnable stopPlayerTask = new Runnable() {
                            @Override
                            public void run() {
                                mp.pause();
                            }
                        };

                        mp.seekTo(startFrom);
                        mp.start();

                        handler.postDelayed(stopPlayerTask, endAt);

                    }catch (Exception ex)
                    {
//                        ex.printStackTrace();
                    }

                }
            }

        }
        ).start();
    }
}
