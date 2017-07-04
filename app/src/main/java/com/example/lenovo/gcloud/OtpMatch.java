package com.example.lenovo.gcloud;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class OtpMatch extends AppCompatActivity {

    int chances = 3;
    String email = "";
    EditText editTextOtp;
    String otp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_match);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * 180);
                    Intent in = new Intent();
                    in.putExtra("message", "Your time has expired. Please try again.");
                    setResult(RESULT_OK, in);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Intent in=getIntent();
        email=in.getStringExtra("email");
        editTextOtp = (EditText) (findViewById(R.id.editTextOtp));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void submit(View view) {
        if (chances == 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_delete_otp");
                        URLConnection conn = url.openConnection();
                        conn.setRequestProperty("email", email);
                        conn.connect();
                        DataInputStream dis = new DataInputStream(conn.getInputStream());
                        String data = dis.readLine();
                        if (data.equals("success")) {
                            Intent in = new Intent();
                            in.putExtra("message", "You have exhausted your chances .");
                            setResult(RESULT_OK, in);
                            finish();
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            if (check()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {;
                        try {
                            URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_otp_match");
                            URLConnection conn = url.openConnection();
                            conn.setRequestProperty("email", email);
                            conn.setRequestProperty("otp",otp);
                            conn.connect();
                            DataInputStream dis = new DataInputStream(conn.getInputStream());
                            String data = dis.readLine();
                            if(data.equals("success")){
                                Intent in=new Intent(getApplicationContext(),NewPassword.class);
                                in.putExtra("email",email);
                                in.putExtra("otp",otp);
                                startActivityForResult(in,2);
                            }
                            else {
                                String msg="";
                                if(chances==1){
                                    msg=chances+" chance";
                                }
                                else{
                                    msg=chances+" chances";
                                }
                                Toast.makeText(OtpMatch.this, "Please enter correct otp. You have "+msg+" left.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
        chances--;
    }

    boolean check() {
        otp = editTextOtp.getText().toString();
        if (otp.trim().isEmpty()) {
            editTextOtp.setError("This field is required");
            return false;
        } else {
            return true;
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2&&resultCode==RESULT_OK) {
            String message = data.getStringExtra("message");
            Intent in=new Intent();
            in.putExtra("message",message);
            setResult(RESULT_OK,in);
            finish();
        }
    }
}
