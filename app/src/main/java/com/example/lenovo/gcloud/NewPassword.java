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

public class NewPassword extends AppCompatActivity {

    String password,confirmPassword;
    EditText editTextNewPassword,editTextConfirmPassword;
    String email="",otp="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        editTextNewPassword=(EditText) (findViewById(R.id.editTextNewPassword));
        editTextConfirmPassword=(EditText) (findViewById(R.id.editTextConfirmPassword));
       Intent in=getIntent();
        email=in.getStringExtra("email");
        otp=in.getStringExtra("otp");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void submit(View view){
        if(check()){
            new Thread(new Runnable() {
                @Override
                public void run() {;
                    try {
                        URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_new_password");
                        URLConnection conn = url.openConnection();
                        conn.setRequestProperty("email", email);
                        conn.setRequestProperty("otp",otp);
                        conn.connect();
                        DataInputStream dis = new DataInputStream(conn.getInputStream());
                        String data = dis.readLine();
                        if(data.equals("success")){
                            Intent in=new Intent();
                            in.putExtra("message","New password set successfully");
                            setResult(RESULT_OK,in);
                            finish();
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
    boolean check(){
        password=editTextNewPassword.getText().toString();
        confirmPassword=editTextConfirmPassword.getText().toString();
        if(password.trim().length()<5|| confirmPassword.trim().length()<5){
            if(password.trim().length()<5) {
                editTextNewPassword.setError("This field must have 5 characters");
            }
            if(confirmPassword.trim().length()<5){
                editTextConfirmPassword.setError("This field must have 5 characters");
            }
            return false;
        }
        else{
            if(confirmPassword.equals(password)){
               return true;
            }
            else{
                Toast.makeText(this,"Confirm password must match with password", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

    }
}
