package com.example.lenovo.gcloud;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ChangePassword extends AppCompatActivity {

    EditText editTextOldPassword,editTextNewPassword,editTextConfirmPassword;
    AlertDialog.Builder msg_dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        editTextOldPassword=(EditText) (findViewById(R.id.editTextOldPassword));
        editTextNewPassword=(EditText) (findViewById(R.id.editTextNewPassword));
        editTextConfirmPassword=(EditText) (findViewById(R.id.editTextConfirmPassword));
        msg_dialog=new AlertDialog.Builder(this);
        msg_dialog.setTitle("Message");
        msg_dialog.setMessage("Password Changed Successfully");
        msg_dialog.setCancelable(false);
        msg_dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyAccount.ref.finish();
                startActivity(new Intent(getApplicationContext(),MyAccount.class));
                finish();
            }
        });
        msg_dialog.create();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public void change_password(View view){
          if(check()){
              new Thread(new Runnable() {
                  @Override
                  public void run() {
                      SharedPreferences pref=getSharedPreferences("MyPref",MODE_PRIVATE);
                      String email=pref.getString("email","email");
                      try {
                          URL url=new URL("http://"+getResources().getString(R.string.ip_server)+":8084/gcloud/mobile_password_change");
                          URLConnection conn=url.openConnection();
                          conn.setRequestProperty("email",email);
                          conn.setRequestProperty("old_password",editTextOldPassword.getText().toString());
                          conn.setRequestProperty("new_password",editTextNewPassword.getText().toString());
                          conn.connect();
                          DataInputStream dis=new DataInputStream(conn.getInputStream());
                          final String msg=dis.readLine();
                                  if(msg.equals("success")){
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              msg_dialog.show();
                                          }
                                      });

                                  }
                                  else if(msg.equals("failure")){
                                      runOnUiThread(new Runnable() {
                                          @Override
                                          public void run() {
                                              Toast.makeText(ChangePassword.this, "Sorry!! You entered wrong old password", Toast.LENGTH_SHORT).show();
                                          }
                                      });
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
    private boolean check(){
        int ready=0;
        if(editTextOldPassword.getText().toString().isEmpty()){
            editTextOldPassword.setError("Field Required");
        }
        else{
            ready++;
        }
        if(editTextNewPassword.getText().toString().isEmpty()){
            editTextNewPassword.setError("Field Required");
        }
        else{
            if(editTextNewPassword.getText().toString().equals(editTextOldPassword.getText().toString())){
                Toast.makeText(this, "You have entered same old and new password", Toast.LENGTH_SHORT).show();
            }
            else{
                ready++;
            }
        }
        if(editTextConfirmPassword.getText().toString().isEmpty()){
            editTextConfirmPassword.setError("Field Required");
        }
        else{
           if(editTextNewPassword.getText().toString().equals(editTextConfirmPassword.getText().toString())){
                ready++;
            }
            else{
               Toast.makeText(this, "Confirm passoword must match with new password", Toast.LENGTH_SHORT).show();
            }
        }
        if(ready==3){
            return true;
        }
        else{
            return false;
        }
    }
}
