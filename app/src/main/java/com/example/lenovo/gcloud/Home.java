package com.example.lenovo.gcloud;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment {


    ProgressBar pb_u_sms,pb_u_contacts,pb_u_call_logs,pb_u_images,pb_u_audio,pb_u_video,pb_u_location,pb_d_sms,pb_d_contacts,pb_d_call_logs,pb_d_images,pb_d_audio,pb_d_video;
    TextView progress_u_sms,progress_u_contacts,progress_u_call_logs,progress_u_images,progress_u_audio,progress_u_video,progress_u_location,progress_d_sms,progress_d_contacts,progress_d_call_logs,progress_d_images,progress_d_audio,progress_d_video;
    LinearLayout linear_layout,linear_layout_u_sms,linear_layout_u_contacts,linear_layout_u_call_logs,linear_layout_u_images,linear_layout_u_audio,linear_layout_u_video,linear_layout_u_location,linear_layout_d_sms,linear_layout_d_contacts,linear_layout_d_call_logs,linear_layout_d_images,linear_layout_d_audio,linear_layout_d_video;
    mybroadcast mybr;
    ImageView close_u_sms,close_u_contacts,close_u_call_logs,close_u_images,close_u_audio,close_u_video,close_u_location,close_d_sms,close_d_contacts,close_d_call_logs,close_d_images,close_d_audio,close_d_video, img;
    TextView textViewMessage;
    public Home() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        img=(ImageView) (getActivity().findViewById(R.id.img));
        Picasso.with(getContext()).load("http://"+getResources().getString(R.string.ip_server)+":8084/gcloud/images/background2.png").into(img);
        pb_u_sms = (ProgressBar) (getActivity().findViewById(R.id.pb_u_sms));
        pb_u_contacts = (ProgressBar) (getActivity().findViewById(R.id.pb_u_contacts));
        pb_u_call_logs = (ProgressBar) (getActivity().findViewById(R.id.pb_u_call_logs));
        pb_u_images = (ProgressBar) (getActivity().findViewById(R.id.pb_u_images));
        pb_u_audio = (ProgressBar) (getActivity().findViewById(R.id.pb_u_audio));
        pb_u_video = (ProgressBar) (getActivity().findViewById(R.id.pb_u_video));
        pb_u_location = (ProgressBar) (getActivity().findViewById(R.id.pb_u_location));
        pb_d_sms = (ProgressBar) (getActivity().findViewById(R.id.pb_d_sms));
        pb_d_contacts = (ProgressBar) (getActivity().findViewById(R.id.pb_d_contacts));
        pb_d_call_logs = (ProgressBar) (getActivity().findViewById(R.id.pb_d_call_logs));
        pb_d_images = (ProgressBar) (getActivity().findViewById(R.id.pb_d_images));
        pb_d_audio = (ProgressBar) (getActivity().findViewById(R.id.pb_d_audio));
        pb_d_video = (ProgressBar) (getActivity().findViewById(R.id.pb_d_video));

        textViewMessage=(TextView) (getActivity().findViewById(R.id.textViewMessage));

        progress_u_sms=(TextView) (getActivity().findViewById(R.id.progress_u_sms));
        progress_u_contacts=(TextView) (getActivity().findViewById(R.id.progress_u_contacts));
        progress_u_call_logs=(TextView) (getActivity().findViewById(R.id.progress_u_call_logs));
        progress_u_images=(TextView) (getActivity().findViewById(R.id.progress_u_images));
        progress_u_audio=(TextView) (getActivity().findViewById(R.id.progress_u_audio));
        progress_u_video=(TextView) (getActivity().findViewById(R.id.progress_u_video));
        progress_u_location=(TextView) (getActivity().findViewById(R.id.progress_u_location));
        progress_d_sms=(TextView) (getActivity().findViewById(R.id.progress_d_sms));
        progress_d_contacts=(TextView) (getActivity().findViewById(R.id.progress_d_contacts));
        progress_d_call_logs=(TextView) (getActivity().findViewById(R.id.progress_d_call_logs));
        progress_d_images=(TextView) (getActivity().findViewById(R.id.progress_d_images));
        progress_d_audio=(TextView) (getActivity().findViewById(R.id.progress_d_audio));
        progress_d_video=(TextView) (getActivity().findViewById(R.id.progress_d_video));

        linear_layout=(LinearLayout) (getActivity().findViewById(R.id.linear_layout));
        linear_layout_u_sms=(LinearLayout) (getActivity().findViewById(R.id.linear_layout_u_sms));
        linear_layout_u_contacts=(LinearLayout) (getActivity().findViewById(R.id.linear_layout_u_contacts));
        linear_layout_u_call_logs=(LinearLayout) (getActivity().findViewById(R.id.linear_layout_u_call_logs));
        linear_layout_u_images=(LinearLayout) (getActivity().findViewById(R.id.linear_layout_u_images));
        linear_layout_u_audio=(LinearLayout) (getActivity().findViewById(R.id.linear_layout_u_audio));
        linear_layout_u_video=(LinearLayout) (getActivity().findViewById(R.id.linear_layout_u_video));
        linear_layout_u_location=(LinearLayout) (getActivity().findViewById(R.id.linear_layout_u_location));
        linear_layout_d_sms=(LinearLayout) (getActivity().findViewById(R.id.linear_layout_d_sms));
        linear_layout_d_contacts=(LinearLayout) (getActivity().findViewById(R.id.linear_layout_d_contacts));
        linear_layout_d_call_logs=(LinearLayout) (getActivity().findViewById(R.id.linear_layout_d_call_logs));
        linear_layout_d_images=(LinearLayout) (getActivity().findViewById(R.id.linear_layout_d_images));
        linear_layout_d_audio=(LinearLayout) (getActivity().findViewById(R.id.linear_layout_d_audio));
        linear_layout_d_video=(LinearLayout) (getActivity().findViewById(R.id.linear_layout_d_video));

        close_u_sms=(ImageView) (getActivity().findViewById(R.id.close_u_sms));
        close_u_contacts=(ImageView) (getActivity().findViewById(R.id.close_u_contacts));
        close_u_call_logs=(ImageView) (getActivity().findViewById(R.id.close_u_call_logs));
        close_u_images=(ImageView) (getActivity().findViewById(R.id.close_u_images));
        close_u_audio=(ImageView) (getActivity().findViewById(R.id.close_u_audio));
        close_u_video=(ImageView) (getActivity().findViewById(R.id.close_u_video));
        close_u_location=(ImageView) (getActivity().findViewById(R.id.close_u_location));
        close_d_sms=(ImageView) (getActivity().findViewById(R.id.close_d_sms));
        close_d_contacts=(ImageView) (getActivity().findViewById(R.id.close_d_contacts));
        close_d_call_logs=(ImageView) (getActivity().findViewById(R.id.close_d_call_logs));
        close_d_images=(ImageView) (getActivity().findViewById(R.id.close_d_images));
        close_d_audio=(ImageView) (getActivity().findViewById(R.id.close_d_audio));
        close_d_video=(ImageView) (getActivity().findViewById(R.id.close_d_video));

        close_u_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_u_sms.setVisibility(View.GONE);
                linear_layout_u_sms.setVisibility(View.GONE);
            }
        });
        close_u_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_u_contacts.setVisibility(View.GONE);
                linear_layout_u_contacts.setVisibility(View.GONE);
            }
        });
        close_u_call_logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_u_call_logs.setVisibility(View.GONE);
                linear_layout_u_call_logs.setVisibility(View.GONE);
            }
        });
        close_u_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_u_images.setVisibility(View.GONE);
                linear_layout_u_images.setVisibility(View.GONE);
            }
        });
        close_u_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_u_audio.setVisibility(View.GONE);
                linear_layout_u_audio.setVisibility(View.GONE);
            }
        });
        close_u_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_u_video.setVisibility(View.GONE);
                linear_layout_u_video.setVisibility(View.GONE);
            }
        });
        close_u_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_u_location.setVisibility(View.GONE);
                linear_layout_u_location.setVisibility(View.GONE);
            }
        });
        close_d_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_d_sms.setVisibility(View.GONE);
                linear_layout_d_sms.setVisibility(View.GONE);
            }
        });
        close_d_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_d_contacts.setVisibility(View.GONE);
                linear_layout_d_contacts.setVisibility(View.GONE);
            }
        });
        close_d_call_logs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_d_call_logs.setVisibility(View.GONE);
                linear_layout_d_call_logs.setVisibility(View.GONE);
            }
        });
        close_d_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_d_images.setVisibility(View.GONE);
                linear_layout_d_images.setVisibility(View.GONE);
            }
        });
        close_d_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_d_audio.setVisibility(View.GONE);
                linear_layout_d_audio.setVisibility(View.GONE);
            }
        });
        close_d_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_d_video.setVisibility(View.GONE);
                linear_layout_d_video.setVisibility(View.GONE);
            }
        });

        mybr = new mybroadcast();
    }
    @Override
     public void onResume() {
        super.onResume();

        IntentFilter inf =new IntentFilter();
        inf.addAction("uploading_sms");
        inf.addAction("uploading_contacts");
        inf.addAction("uploading_call_logs");
        inf.addAction("uploading_images");
        inf.addAction("uploading_audio");
        inf.addAction("uploading_video");
        inf.addAction("uploading_location");
        inf.addAction("downloading_sms");
        inf.addAction("downloading_contacts");
        inf.addAction("downloading_call_logs");
        inf.addAction("downloading_images");
        inf.addAction("downloading_audio");
        inf.addAction("downloading_video");

        getActivity().registerReceiver(mybr,inf);
//        Toast.makeText(this, "reciever registered", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mybr);
//        Toast.makeText(this, "reciever unregistered", Toast.LENGTH_SHORT).show();
    }

    class mybroadcast extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            img.setVisibility(View.GONE);
            textViewMessage.setText("Backup/Restore Status");
            if(intent.getAction().equals("uploading_location")) {
                boolean stop = intent.getBooleanExtra("stop",false);
                String text=intent.getStringExtra("text");
                if(stop){
                    close_u_location.setVisibility(View.VISIBLE);
                    pb_u_location.setVisibility(View.INVISIBLE);
                }
                else{
                    pb_u_location.setVisibility(View.VISIBLE);
                    pb_u_location.setIndeterminate(true);
                }
                progress_u_location.setText(text);
                linear_layout_u_location.setVisibility(View.VISIBLE);
            }
           else if(intent.getAction().equals("uploading_sms")) {
                int value = intent.getIntExtra("value", 0);
                int max=-1;
                if(value==0){
                    progress_u_sms.setText("Starting backup...");
                    pb_u_sms.setIndeterminate(true);
                }
                else{
                    max= intent.getIntExtra("max",0);
                    pb_u_sms.setProgress(value);
                    pb_u_sms.setMax(max);
                    progress_u_sms.setText(value+"/"+max);
                    pb_u_sms.setIndeterminate(false);
                }
                linear_layout_u_sms.setVisibility(View.VISIBLE);
                if(value==max){
                    close_u_sms.setVisibility(View.VISIBLE);
                    progress_u_sms.setText("Backup completed");
                }
            }
            else if(intent.getAction().equals("uploading_contacts")){
                int value = intent.getIntExtra("value", 0);
                int max=-1;
                if(value==0){
                    progress_u_contacts.setText("Starting backup...");
                    pb_u_contacts.setIndeterminate(true);
                }
                else{
                    max= intent.getIntExtra("max",0);
                    pb_u_contacts.setProgress(value);
                    pb_u_contacts.setMax(max);
                    progress_u_contacts.setText(value+"/"+max);
                    pb_u_contacts.setIndeterminate(false);
                }
                linear_layout_u_contacts.setVisibility(View.VISIBLE);
                if(value==max){
                    close_u_contacts.setVisibility(View.VISIBLE);
                    progress_u_contacts.setText("Backup completed");
                }
            }
            else if(intent.getAction().equals("uploading_call_logs")){
                int value = intent.getIntExtra("value", 0);
                int max=-1;
                if(value==0){
                    progress_u_call_logs.setText("Starting backup...");
                    pb_u_call_logs.setIndeterminate(true);
                }
                else{
                    max= intent.getIntExtra("max",0);
                    pb_u_call_logs.setProgress(value);
                    pb_u_call_logs.setMax(max);
                    progress_u_call_logs.setText(value+"/"+max);
                    pb_u_call_logs.setIndeterminate(false);
                }
                linear_layout_u_call_logs.setVisibility(View.VISIBLE);
                if(value==max){
                    close_u_call_logs.setVisibility(View.VISIBLE);
                    progress_u_call_logs.setText("Backup completed");
                }
            }
            else if(intent.getAction().equals("uploading_images")){
                int value = intent.getIntExtra("value", 0);
                int max=-1;
                if(value==0){
                    progress_u_images.setText("Starting backup...");
                    pb_u_images.setIndeterminate(true);
                }
                else{
                    max= intent.getIntExtra("max",0);
                    pb_u_images.setProgress(value);
                    pb_u_images.setMax(max);
                    progress_u_images.setText(value+"/"+max);
                    pb_u_images.setIndeterminate(false);
                }
                linear_layout_u_images.setVisibility(View.VISIBLE);
                if(value==max){
                    close_u_images.setVisibility(View.VISIBLE);
                    progress_u_images.setText("Backup completed");
                }
            }
            else if(intent.getAction().equals("uploading_audio")){
                int value = intent.getIntExtra("value", 0);
                int max=-1;
                if(value==0){
                    progress_u_audio.setText("Starting backup...");
                    pb_u_audio.setIndeterminate(true);
                }
                else{
                    max= intent.getIntExtra("max",0);
                    pb_u_audio.setProgress(value);
                    pb_u_audio.setMax(max);
                    progress_u_audio.setText(value+"/"+max);
                    pb_u_audio.setIndeterminate(false);
                }
                linear_layout_u_audio.setVisibility(View.VISIBLE);
                if(value==max){
                    close_u_audio.setVisibility(View.VISIBLE);
                    progress_u_audio.setText("Backup completed");
                }
            }
            else if(intent.getAction().equals("uploading_video")){
                int value = intent.getIntExtra("value", 0);
                int max=-1;
                if(value==0){
                    progress_u_video.setText("Starting backup...");
                    pb_u_video.setIndeterminate(true);
                }
                else{
                    max= intent.getIntExtra("max",0);
                    pb_u_video.setProgress(value);
                    pb_u_video.setMax(max);
                    progress_u_video.setText(value+"/"+max);
                    pb_u_video.setIndeterminate(false);
                }
                linear_layout_u_video.setVisibility(View.VISIBLE);
                if(value==max){
                    close_u_video.setVisibility(View.VISIBLE);
                    progress_u_video.setText("Backup completed");
                }
            }
            else if(intent.getAction().equals("downloading_sms")){
                Log.d("MYMSG","Inside broadcast");
                int value = intent.getIntExtra("value", 0);
                int max=intent.getIntExtra("max",0);
                if(value==0&&max==0){
                    progress_d_sms.setText("Starting restore...");
                    pb_d_sms.setIndeterminate(true);
                }
                else if(max==0){
                    progress_d_sms.setText("Fetching extra data...");
                    pb_d_sms.setIndeterminate(true);
                }
                else if(max==-1){
                    close_d_sms.setVisibility(View.VISIBLE);
                    pb_d_sms.setProgress(value);
                    pb_d_sms.setIndeterminate(false);
                    progress_d_sms.setText("Restore completed");
                }
                else{
                    pb_d_sms.setProgress(value);
                    pb_d_sms.setMax(max);
                    progress_d_sms.setText(value+"/"+max);
                    pb_d_sms.setIndeterminate(false);
                }
                linear_layout_d_sms.setVisibility(View.VISIBLE);
            }
            else if(intent.getAction().equals("downloading_contacts")){
                Log.d("MYMSG","Inside broadcast");
                int value = intent.getIntExtra("value", 0);
                int max=intent.getIntExtra("max",0);
                if(value==0&&max==0){
                    progress_d_contacts.setText("Starting restore...");
                    pb_d_contacts.setIndeterminate(true);
                }
                else if(max==0){
                    progress_d_contacts.setText("Fetching extra data...");
                    pb_d_contacts.setIndeterminate(true);
                }
                else if(max==-1){
                    close_d_contacts.setVisibility(View.VISIBLE);
                    pb_d_contacts.setProgress(value);
                    pb_d_contacts.setIndeterminate(false);
                    progress_d_contacts.setText("Restore completed");
                }
                else{
                    pb_d_contacts.setProgress(value);
                    pb_d_contacts.setMax(max);
                    progress_d_contacts.setText(value+"/"+max);
                    pb_d_contacts.setIndeterminate(false);
                }
                linear_layout_d_contacts.setVisibility(View.VISIBLE);
            }
            else if(intent.getAction().equals("downloading_call_logs")){
                Log.d("MYMSG","Inside broadcast");
                int value = intent.getIntExtra("value", 0);
                int max=intent.getIntExtra("max",0);
                if(value==0&&max==0){
                    progress_d_call_logs.setText("Starting restore...");
                    pb_d_call_logs.setIndeterminate(true);
                }
                else if(max==0){
                    progress_d_call_logs.setText("Fetching extra data...");
                    pb_d_call_logs.setIndeterminate(true);
                }
                else if(max==-1){
                    close_d_call_logs.setVisibility(View.VISIBLE);
                    pb_d_call_logs.setProgress(value);
                    pb_d_call_logs.setIndeterminate(false);
                    progress_d_call_logs.setText("Restore completed");
                }
                else{
                    pb_d_call_logs.setProgress(value);
                    pb_d_call_logs.setMax(max);
                    progress_d_call_logs.setText(value+"/"+max);
                    pb_d_call_logs.setIndeterminate(false);
                }
                linear_layout_d_call_logs.setVisibility(View.VISIBLE);
            }
            else if(intent.getAction().equals("downloading_images")){
                int value = intent.getIntExtra("value", 0);
                int max=-1;
                if(value==0){
                    progress_d_images.setText("Starting backup...");
                    pb_d_images.setIndeterminate(true);
                }
                else{
                    max= intent.getIntExtra("max",0);
                    pb_d_images.setProgress(value);
                    pb_d_images.setMax(max);
                    progress_d_images.setText(value+"/"+max);
                    pb_d_images.setIndeterminate(false);
                }
                linear_layout_d_images.setVisibility(View.VISIBLE);
                if(value==max){
                    close_d_images.setVisibility(View.VISIBLE);
                    progress_d_images.setText("Backup completed");
                }
            }
            else if(intent.getAction().equals("downloading_audio")){
                int value = intent.getIntExtra("value", 0);
                int max=-1;
                if(value==0){
                    progress_d_audio.setText("Starting backup...");
                    pb_d_audio.setIndeterminate(true);
                }
                else{
                    max= intent.getIntExtra("max",0);
                    pb_d_audio.setProgress(value);
                    pb_d_audio.setMax(max);
                    progress_d_audio.setText(value+"/"+max);
                    pb_d_audio.setIndeterminate(false);
                }
                linear_layout_d_audio.setVisibility(View.VISIBLE);
                if(value==max){
                    close_d_audio.setVisibility(View.VISIBLE);
                    progress_d_audio.setText("Backup completed");
                }
            }
            else if(intent.getAction().equals("downloading_video")){
                int value = intent.getIntExtra("value", 0);
                int max=-1;
                if(value==0){
                    progress_d_video.setText("Starting backup...");
                    pb_d_video.setIndeterminate(true);
                }
                else{
                    max= intent.getIntExtra("max",0);
                    pb_d_video.setProgress(value);
                    pb_d_video.setMax(max);
                    progress_d_video.setText(value+"/"+max);
                    pb_d_video.setIndeterminate(false);
                }
                linear_layout_d_video.setVisibility(View.VISIBLE);
                if(value==max){
                    close_d_video.setVisibility(View.VISIBLE);
                    progress_d_video.setText("Backup completed");
                }
            }

        }
    }
}
