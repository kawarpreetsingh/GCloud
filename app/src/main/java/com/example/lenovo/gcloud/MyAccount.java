package com.example.lenovo.gcloud;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MyAccount extends AppCompatActivity {

    EditText editTextUsername, editTextPhone, editTextEmail;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    Button buttonEdit;
    String username, phone, email;
    AlertDialog.Builder msg_dialog;
    protected static Activity ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressBar=(ProgressBar) (findViewById(R.id.progressBar));
        linearLayout=(LinearLayout) (findViewById(R.id.linearLayout));
        editTextPhone = (EditText) (findViewById(R.id.editTextPhone));
        editTextUsername = (EditText) (findViewById(R.id.editTextUsername));
        editTextEmail = (EditText) (findViewById(R.id.editTextEmail));
        buttonEdit = (Button) (findViewById(R.id.buttonEdit));
        ref=this;

        SharedPreferences pref = getSharedPreferences("MyPref", MODE_PRIVATE);
        email = pref.getString("email", "email");

        new Thread(new Fetch()).start();

        msg_dialog = new AlertDialog.Builder(this);
        msg_dialog.setCancelable(false);
        msg_dialog.setTitle("Message");
        msg_dialog.setIcon(R.drawable.info_icon);
        msg_dialog.setMessage("Changes are saved");
        msg_dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    public void edit(View view) {
        int ready = 0;
        if (buttonEdit.getText().equals("edit")) {
            editTextPhone.setEnabled(true);
            editTextUsername.setEnabled(true);
            buttonEdit.setText("save changes");
        } else if (buttonEdit.getText().equals("save changes")) {
            username = editTextUsername.getText().toString();
            phone = editTextPhone.getText().toString();
            if (username.trim().isEmpty()) {
                editTextUsername.setError("This field is required");
            } else {
                if(isValidUsername()) {
                    ready++;
                }
                else{
                    editTextUsername.setError("Username should not contain any digit");
                }
            }
            if (phone.length() == 10) {
                ready++;
            } else {
                editTextPhone.setError("Phone no is invalid");
            }
            if (ready == 2) {
                new Thread(new Save()).start();
            }
        }
    }

    private boolean isValidUsername(){
        char c[]=username.toCharArray();
        for(int i=0;i<username.length();i++){
            if(Character.isDigit(c[i])){
                return false;
            }
        }
        return true;
    }

    class Save implements Runnable {

        @Override
        public void run() {
            try {
                URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_save_changes");
                URLConnection conn = url.openConnection();
                conn.setRequestProperty("username", username);
                conn.setRequestProperty("email", email);
                conn.setRequestProperty("phone", phone);
                conn.connect();
                DataInputStream dis = new DataInputStream(conn.getInputStream());
                String msg = dis.readLine();
                if (msg.equals("success")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msg_dialog.show();
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

    class Fetch implements Runnable {

        @Override
        public void run() {
            try {
                URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_account");
                URLConnection conn = url.openConnection();
                conn.setRequestProperty("email", email);
                conn.connect();
                DataInputStream dis = new DataInputStream(conn.getInputStream());
                JSONObject jsonObject = new JSONObject(dis.readLine());
                username = jsonObject.getString("username");
                phone = jsonObject.getString("phone");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editTextEmail.setText(email);
                        editTextUsername.setText(username);
                        editTextPhone.setText(phone);
                        linearLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void change_password(View view){
        startActivity(new Intent(this,ChangePassword.class));
    }
    public void delete_account(View view){
        AlertDialog.Builder confirm=new AlertDialog.Builder(this);
        confirm.setTitle("Confirmation");
        confirm.setCancelable(false);
        confirm.setIcon(R.drawable.alert_2_icon);
        confirm.setMessage("This will erase all your backed up data.\nAre you sure to delete it?");
        confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new DeleteAccount()).start();
            }
        });
        confirm.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        confirm.create();
        confirm.show();
    }
    class DeleteAccount implements Runnable{

        @Override
        public void run() {
            try {
                URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_delete_account");
                URLConnection conn = url.openConnection();
                conn.setRequestProperty("email", email);
                conn.connect();
                DataInputStream dis = new DataInputStream(conn.getInputStream());
                if(dis.readLine().equals("success")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msg_dialog.setMessage("Account Deleted Successfully");
                            msg_dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences pref=getSharedPreferences("MyPref",MODE_PRIVATE);
                                    SharedPreferences.Editor editor=pref.edit();
                                    editor.clear();
                                    editor.commit();
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    UserHome.user_home_ref.finish();
                                    finish();
                                }
                            });
                            msg_dialog.create();
                            msg_dialog.show();
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
}
