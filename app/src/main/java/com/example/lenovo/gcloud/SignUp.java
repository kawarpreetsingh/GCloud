package com.example.lenovo.gcloud;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.IntegerRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SignUp extends AppCompatActivity {

    String[] que={"What is your nick name?","What is your favorite color?","What is your first school name?","What is your hobby?"};
    ArrayAdapter<String> ad;
    Spinner chooseSecurityQue;
    Button buttonSignUp;
    EditText editTextUsername,editTextEmail,editTextPassword,editTextConfirmPassword,editTextSecurityAns,editTextPhone,editTextCaptchaAns;
    String username,email,password,confirmPassword,securityQue,securityAns,phone;
    AlertDialog.Builder msg_dialog;
    TextView textViewCaptcha;
    int captcha_ans=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        buttonSignUp=(Button) (findViewById(R.id.buttonSignUp));
        editTextUsername=(EditText) (findViewById(R.id.editTextUsername));
        editTextEmail=(EditText) (findViewById(R.id.editTextEmail));
        editTextPassword=(EditText) (findViewById(R.id.editTextPassword));
        editTextConfirmPassword=(EditText) (findViewById(R.id.editTextConfirmPassword));
        editTextSecurityAns=(EditText) (findViewById(R.id.editTextSecurityAns));
        editTextPhone=(EditText) (findViewById(R.id.editTextPhone));
        editTextCaptchaAns=(EditText) (findViewById(R.id.editTextCaptchaAns));
        textViewCaptcha=(TextView) (findViewById(R.id.textViewCaptcha));

        ad=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,que);
        chooseSecurityQue=(Spinner) (findViewById(R.id.chooseSecurityQue));

        refresh();
        chooseSecurityQue.setAdapter(ad);

        msg_dialog=new AlertDialog.Builder(this);
        msg_dialog.setCancelable(false);
        msg_dialog.setTitle("Message");
        msg_dialog.setMessage("Sign up successful");
        msg_dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
    public void check(View view){
       username=editTextUsername.getText().toString();
        email=editTextEmail.getText().toString();
         password=editTextPassword.getText().toString();
        confirmPassword=editTextConfirmPassword.getText().toString();
        securityQue=chooseSecurityQue.getSelectedItem().toString();
        securityAns=editTextSecurityAns.getText().toString();
        phone=editTextPhone.getText().toString();
        int ready=0;

        if(username.trim().isEmpty()){
            editTextUsername.setError("This field is required");
        }
        else{
            if(isValidUsername()){
                ready++;
            }
            else {
                editTextUsername.setError("Username should not contain any digits");
            }
        }
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
        if(password.trim().length()<5|| confirmPassword.trim().length()<5){
            if(password.trim().length()<5) {
                editTextPassword.setError("This field must have 5 characters");
            }
            if(confirmPassword.trim().length()<5){
                editTextConfirmPassword.setError("This field must have 5 characters");
            }
        }
        else{
            if(confirmPassword.equals(password)){
                ready++;
            }
            else{
                Toast.makeText(this,"Confirm password must match with password", Toast.LENGTH_SHORT).show();
            }
        }

        if(securityAns.trim().isEmpty()){
            editTextSecurityAns.setError("This field is required");
        }
        else{
            ready++;
        }
        if(phone.length()==10){
            ready++;
        }
        else{
            editTextPhone.setError("Phone no is invalid");
        }
        if(editTextCaptchaAns.getText().toString().trim().isEmpty()){
            editTextCaptchaAns.setError("This field is required");
        }
        else{
            if(Integer.parseInt(editTextCaptchaAns.getText().toString())==captcha_ans){
                ready++;
            }
            else{
                Toast.makeText(this, "Enter correct answer of captcha", Toast.LENGTH_SHORT).show();
                refresh();
            }
        }
        if(ready==6) {
           new Thread(new Connect()).start();
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
    public void refresh_captcha(View view){
        refresh();
    }
    private  void refresh(){
        int a=(int)(Math.random()*10);
        int b=(int)(Math.random()*10);
        textViewCaptcha.setText("Enter the ans : "+a+"+"+b);
        editTextCaptchaAns.setText("");
        captcha_ans=a+b;
    }
    class Connect implements Runnable{

        @Override
        public void run() {
            try {
                URL url = new URL("http://"+getResources().getString(R.string.ip_server)+":8084/gcloud/mobile_signup");
                URLConnection conn = url.openConnection();
                conn.setRequestProperty("username", username);
                conn.setRequestProperty("email", email);
                conn.setRequestProperty("password", password);
                conn.setRequestProperty("phone", phone);
                conn.setRequestProperty("securityque", securityQue);
                conn.setRequestProperty("securityans", securityAns);
                conn.connect();
                DataInputStream dis = new DataInputStream(conn.getInputStream());
                String msg = dis.readLine();
                if(msg.equals("success")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msg_dialog.show();
                        }
                    });
                }
                else if(msg.equals("exists")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignUp.this, "This email already exists", Toast.LENGTH_SHORT).show();
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
