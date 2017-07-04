package com.example.lenovo.gcloud;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ForgotPassword1 extends AppCompatActivity {

    String securityQue,securityAns,email;
    TextView textViewSecurityQue;
    EditText editTextSecurityAns;
    Intent in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password1);

        in=new Intent();
        Intent i=getIntent();
        securityQue=i.getStringExtra("securityque");
        securityAns=i.getStringExtra("securityans");
        email=i.getStringExtra("email");

        textViewSecurityQue=(TextView) (findViewById(R.id.textViewSecurityQue));
        editTextSecurityAns=(EditText) (findViewById(R.id.editTextSecurityAns));

        textViewSecurityQue.setText(securityQue);

        this.setFinishOnTouchOutside(false);
    }
    public void submit(View view){
        String security_ans=editTextSecurityAns.getText().toString();
        if(security_ans.isEmpty()){
            editTextSecurityAns.setError("This field is required");
        }
        else{
            if(securityAns.equals(security_ans)){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://" + getResources().getString(R.string.ip_server) + ":8084/gcloud/mobile_generate_otp");
                            URLConnection conn = url.openConnection();
                            conn.setRequestProperty("email",email);
                            conn.connect();
                            DataInputStream dis = new DataInputStream(conn.getInputStream());
                            String data = dis.readLine();
                            if(data.equals("success")){
                                in.putExtra("email", email);
                                in.putExtra("message","success");
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
            else{
                in.putExtra("message","fail");
                setResult(RESULT_OK,in);
                finish();
            }
        }
    }
}
