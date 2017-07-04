package com.example.lenovo.gcloud;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ForgotPassword extends AppCompatActivity {

    EditText editTextEmail;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        editTextEmail=(EditText) (findViewById(R.id.editTextEmail));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void go(View view){
        if(check()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url=new URL("http://"+getResources().getString(R.string.ip_server)+":8084/gcloud/mobile_forgot_password");
                        URLConnection conn=url.openConnection();
                        conn.setRequestProperty("email",email);
                        conn.connect();
                        DataInputStream dis=new DataInputStream(conn.getInputStream());
                        String data=dis.readLine();
                        if(data.equals("failure")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ForgotPassword.this, "Sorry!! This email is not registered in GCloud", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            JSONObject json=new JSONObject(data);
                            String securityQue= json.getString("securityque");
                            String securityAns=json.getString("securityans");
                            Intent in=new Intent(getApplicationContext(),ForgotPassword1.class);
                            in.putExtra("securityque",securityQue);
                            in.putExtra("securityans",securityAns);
                            in.putExtra("email",email);
                            startActivityForResult(in,1);
                        }
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
    }
    private boolean check(){
        email=editTextEmail.getText().toString();
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
        if(ready==1){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK){
            String message=data.getStringExtra("message");
            Intent in=new Intent();
            in.putExtra("message",message);
             setResult(RESULT_OK,in);
            finish();
        }
    }
}
