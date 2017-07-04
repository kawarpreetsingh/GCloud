package com.example.lenovo.gcloud;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SyncDevice extends AppCompatActivity {

    ListView listView;
    ArrayList<Sync> al;
    MyAdapter myAdapter;
    int device_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_device);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent in=getIntent();
        device_id=in.getIntExtra("device_id",0);
        Log.d("device_id",device_id+"");

        al=new ArrayList<>();
        al.add(new Sync("SMS"));
        al.add(new Sync("Call logs"));
        al.add(new Sync("Contacts"));
        al.add(new Sync("Images"));
        al.add(new Sync("Music"));
        al.add(new Sync("Video"));

        listView=(ListView) (findViewById(R.id.listView));
        myAdapter=new MyAdapter();
        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ColorDrawable d=(ColorDrawable)view.getBackground();
                int color=d.getColor();
                if(color==Color.WHITE) {
                    view.setBackgroundColor(getResources().getColor(R.color.lightblue));
                    al.get(position).value = true;
                }
                else{
                    view.setBackgroundColor(Color.WHITE);
                    al.get(position).value=false;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return al.size();
        }

        @Override
        public Object getItem(int position) {
            return al.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater l=LayoutInflater.from(getApplicationContext());
            convertView=l.inflate(R.layout.sync_device_design,parent,false);
            TextView textView=(TextView) (convertView.findViewById(R.id.textView));
            textView.setText(al.get(position).text);
            Log.d("text",al.get(position).text);
            convertView.setBackgroundColor(Color.WHITE);
            return convertView;
        }
    }
    public void sync(View view){

        if(check()) {
            SharedPreferences pref = getSharedPreferences("MyPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            for (int i = 0; i < al.size(); i++) {
                editor.putBoolean("b[" + i + "]", al.get(i).value);
            }
            editor.commit();

//        if(!isMyServiceRunning(DownloadData.class)) {
            Intent in = new Intent(this, DownloadData.class);
            startService(in);
            UserHome.user_home_ref.finish();
            startActivity(new Intent(this, UserHome.class));
            finish();
//        }
        }
        else{
            Snackbar.make(view,"Select at least one option to be restored",Snackbar.LENGTH_SHORT).show();
        }
    }
    private boolean check(){
        for(int i=0;i<al.size();i++){
            if(al.get(i).value==true){
                return true;
            }
        }
        return false;
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
    class Sync{
        String text;
        boolean value=false;
        Sync(String text) {
            this.text = text;
        }
    }
    public void location(View view){
        startActivity(new Intent(this,LocationToFrom.class));
    }
}
