package com.example.lenovo.gcloud;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Download extends Fragment {


    SharedPreferences pref;
    ArrayList<Device> al;
    ListView listView;
    MyAdapter myAdapter;
    public Download() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_download, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        al=new ArrayList<>();
        listView=(ListView) (getActivity().findViewById(R.id.listView));

        pref=getActivity().getSharedPreferences("MyPref",getContext().MODE_PRIVATE);

        final String email=pref.getString("email","email");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url=new URL("http://"+getResources().getString(R.string.ip_server)+":8084/gcloud/mobile_view_devices");
                    URLConnection conn=url.openConnection();
                    conn.setRequestProperty("email",email);
                    conn.connect();
                    DataInputStream dis=new DataInputStream(conn.getInputStream());
                    JSONObject jsonObject=new JSONObject(dis.readLine());
                    JSONArray jsonArray=jsonObject.getJSONArray("devices");
                    al.clear();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject js=jsonArray.getJSONObject(i);
                        al.add(new Device(js.getString("manufacturer"),js.getString("model_no"),js.getInt("device_id")));
                    }
                    myAdapter=new MyAdapter();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listView.setAdapter(myAdapter);
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
        }).start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in=new Intent(getContext(),SyncDevice.class);
                in.putExtra("device_id",al.get(position).device_id);
                startActivity(in);
            }
        });
    }

    class Device{
        String manufacturer;
        String model_no;
        int device_id;

        Device(String manufacturer, String model_no,int device_id) {
            this.manufacturer = manufacturer;
            this.model_no = model_no;
            this.device_id=device_id;
        }
    }
    class MyAdapter extends BaseAdapter {

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
            LayoutInflater l=LayoutInflater.from(getContext());
            convertView =l.inflate(R.layout.view_devices_design,parent,false);
            TextView textViewManufacturer=(TextView) (convertView.findViewById(R.id.textViewManufacturer));
            TextView textViewModelNo=(TextView) (convertView.findViewById(R.id.textViewModelNo));
            ImageView imageView=(ImageView)(convertView.findViewById(R.id.imageView));
            textViewManufacturer.append(al.get(position).manufacturer);
            textViewModelNo.append(al.get(position).model_no);
            Picasso.with(getContext()).load("http://"+getResources().getString(R.string.ip_server)+":8084/gcloud/images/"+al.get(position).manufacturer.toLowerCase()+".png").into(imageView);
            return convertView;
        }
    }
}
