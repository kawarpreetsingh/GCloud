package com.example.lenovo.gcloud;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GoogleMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SharedPreferences pref;
    Polyline polyline;
    PolylineOptions po;
    float cameraZoom;
    int jsonArraySize=0;
    int count=0;
    boolean al_ready=false;
    ArrayList<Lat_Lng>al=new ArrayList<>();
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent in=getIntent();
        final String from=in.getStringExtra("from");
        final String to=in.getStringExtra("to");
        progressBar=(ProgressBar) (findViewById(R.id.progressBar));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url=new URL("http://"+getResources().getString(R.string.ip_server)+":8084/gcloud/mobile_location_download");
                    HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection","Keep-Alive");
                    pref=getSharedPreferences("MyPref",MODE_PRIVATE);
                    int device_id=pref.getInt("device_id",0);
                    conn.setRequestProperty("device_id",device_id+"");
                    conn.setRequestProperty("from",from);
                    conn.setRequestProperty("to",to);
                    conn.connect();
                    DataInputStream dis=new DataInputStream(conn.getInputStream());
                    JSONObject jsonObject=new JSONObject(dis.readLine());
                    JSONArray jsonArray=jsonObject.getJSONArray("lat_lng");
                    po=new PolylineOptions();
                    jsonArraySize=jsonArray.length();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject js=jsonArray.getJSONObject(i);
                        String lat=js.getString("lat");
                        String lng=js.getString("lng");
                        al.add(new Lat_Lng(lat,lng));
                    }
                    al_ready=true;
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        progressBar.setVisibility(View.GONE);
        mMap = googleMap;
//        LatLng amritsar=new LatLng(31.6547,74.7867);
//        MarkerOptions mo=new MarkerOptions();
//        mo.position(amritsar);
//        mo.title("Amritsar");
//        mMap.addMarker(mo);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(amritsar,15));
        while(true){
            if(al_ready){
                break;
            }
        }
        for(count=0;count<al.size();count++){
            showLocation(al.get(count).lat,al.get(count).lng);
        }
    }
    private void showLocation(String lat,String lng){
        double latitude=Double.parseDouble(lat);
        double longitude=Double.parseDouble(lng);
        LatLng currentLatLng=new LatLng(latitude,longitude);
        po.add(currentLatLng);
        po.width(5);
        po.color(Color.RED);
        MarkerOptions mo=new MarkerOptions();
        mo.position(currentLatLng);
        mo.title("My Location");
        if(count==0||count==jsonArraySize-1) {
            mMap.addMarker(mo);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,15));
        polyline=mMap.addPolyline(po);
    }
    class Lat_Lng{
        String lat;
        String lng;

        Lat_Lng(String lat, String lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }
}
