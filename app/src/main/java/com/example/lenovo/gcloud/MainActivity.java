package com.example.lenovo.gcloud;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends Activity {

    EditText editTextEmail,editTextPassword;
    String email,password;
    AlertDialog.Builder msg_dialog;
    boolean logged_in;
    View view;
    AlertDialog.Builder dialog;
    String otp_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref=getSharedPreferences("MyPref",MODE_PRIVATE);
        logged_in=pref.getBoolean("logged_in",false);

        if(logged_in){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            request_permission();
        }
        else{
            task_on_permission_granted();
        }
        }

        dialog=new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle("Message");
        dialog.setMessage("Check your email for one time password");
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent in=new Intent(getApplicationContext(),OtpMatch.class);
                in.putExtra("email",otp_email);
                startActivityForResult(in,2);
            }
        });
        dialog.create();

        editTextEmail=(EditText) (findViewById(R.id.editTextEmail));
        editTextPassword=(EditText) (findViewById(R.id.editTextPassword));

        msg_dialog=new AlertDialog.Builder(this);
        msg_dialog.setCancelable(false);
        msg_dialog.setTitle("Message");
        msg_dialog.setMessage("Login successful");
        msg_dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getApplicationContext(),UserHome.class));
                finish();
            }
        });
        msg_dialog.create();

    }
    public void login(View view){
        this.view=view;
        email=editTextEmail.getText().toString();
        password=editTextPassword.getText().toString();
        int ready=0;
        if(email.trim().isEmpty()){
            editTextEmail.setError("This field is required");
        }
        else{
            int index1=email.indexOf('@');
            int index2=email.indexOf('.');
            if((index1!=-1)&&(index2!=-1)&&(email.length()>=7)&&(email.substring(index1+1,index2).length()>=2)){
                ready++;
            }
            else{
                editTextEmail.setError("Invalid Email");
            }
        }
        if(password.trim().length()<5){
            editTextPassword.setError("This field must have at least 5 characters");
        }
        else{
            ready++;
        }
        if(ready==2){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                request_permission();
            }
            else{
                task_on_permission_granted();
            }
        }
    }
    class Connect implements Runnable{

        @Override
        public void run() {
            try {
                URL url = new URL("http://"+getResources().getString(R.string.ip_server)+":8084/gcloud/mobile_login");
//                Log.d("IP",getResources().getString(R.string.ip_server));
                URLConnection conn = url.openConnection();
                conn.setRequestProperty("email", email);
                conn.setRequestProperty("password", password);
                conn.connect();
                DataInputStream dis = new DataInputStream(conn.getInputStream());
                String msg = dis.readLine();
                if(msg.equals("success")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences pref=getSharedPreferences("MyPref",MODE_PRIVATE);
                            SharedPreferences.Editor editor=pref.edit();
                            editor.putString("email",email);
                            editor.putBoolean("logged_in",true);
                            editor.putBoolean("manual",true);
                            editor.commit();
                            msg_dialog.show();

                        }
                    });
                }
                else if(msg.equals("password")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "This email id is not registered in GCloud", Toast.LENGTH_SHORT).show();
//                            Snackbar.make(view,"This email id is not registered in GCloud", BaseTransientBottomBar.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private boolean check_phone_permission_granted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }
    private boolean check_contacts_permission_granted(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }
    private boolean check_sms_permission_granted(){
        return ContextCompat.checkSelfPermission(this,Manifest.permission.READ_SMS)== PackageManager.PERMISSION_GRANTED;
    }
    private boolean check_call_logs_permission_granted(){
        return ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_CALL_LOG)== PackageManager.PERMISSION_GRANTED;
    }
    private boolean check_external_storage_permission(){
        return ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
    }
    private boolean check_location_permission(){
        return ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED;
    }

    private void request_permission() {

        Log.d("MYMSG","Request permission");
        ArrayList<String> al = new ArrayList<>();
        if(!check_phone_permission_granted()){
            al.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!check_contacts_permission_granted()) {
            al.add(Manifest.permission.WRITE_CONTACTS);
        }
        if (!check_call_logs_permission_granted()) {
            al.add(Manifest.permission.WRITE_CALL_LOG);
        }
        if (!check_sms_permission_granted()) {
            al.add(Manifest.permission.READ_SMS);
        }
        if (!check_external_storage_permission()) {
            al.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!check_location_permission()) {
            al.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        Log.d("MYMSG","Al size="+al.size());
        if(al.size()>0) {
            String[] s = al.toArray(new String[al.size()]);
            ActivityCompat.requestPermissions(this, s, 1);
        }
        else{
            task_on_permission_granted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            Log.d("MYMSG","on request permission result");
            int granted=0;
            for(int i=0;i<permissions.length;i++){
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    granted++;
                }
            }
            if(granted==permissions.length){
                task_on_permission_granted();
            }
            else{
                task_on_permission_rejected();
            }
        }
    }
    private void task_on_permission_granted() {
        Log.d("MYMSG","Task on permission granted");
        if(haveNetworkConnection()){
            if(logged_in){
                startActivity(new Intent(this,UserHome.class));
                finish();
            }
            else {
                new Thread(new Connect()).start();
            }
        }
        else{
            AlertDialog.Builder dialog=new AlertDialog.Builder(this);
            dialog.setTitle("Internet Required");
            dialog.setMessage("Please turn on your data services for working of this app");
            dialog.setIcon(R.drawable.wifi_icon);
            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent in=new Intent(Settings.ACTION_SETTINGS);
                    startActivity(in);
                }
            });
            dialog.create();
            dialog.show();
        }
    }


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
    private void task_on_permission_rejected() {
        Log.d("MYMSG","Task on permission rejected");
        AlertDialog.Builder allow = new AlertDialog.Builder(this);
        allow.setCancelable(false);
        allow.setTitle("Permission Required");
        allow.setMessage("Please allow the permission(s) so that our app can perform its task properly\n\n*This app will not use your sensitive data without your permission");
        allow.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        allow.create();
        allow.show();
    }

    public void forgot_password(View view){
        startActivityForResult(new Intent(this,ForgotPassword.class),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK){
            String message=data.getStringExtra("message");
            if(message.equals("success")){
                otp_email=data.getStringExtra("email");
                dialog.show();
            }
            else{
                Toast.makeText(this, "Sorry!! you provided wrong information", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode==2&&resultCode==RESULT_OK){
            Toast.makeText(this, data.getStringExtra("message"), Toast.LENGTH_SHORT).show();
        }
    }
    public void signup(View view){
        startActivity(new Intent(this,SignUp.class));
    }
}
