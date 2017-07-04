package com.example.lenovo.gcloud;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Upload extends Fragment {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    AlertDialog.Builder dialog;
    boolean sms_value, call_logs_value, contacts_value, images_value, audio_value, video_value, location_value;
    ListView listViewOption;
    ArrayList<Options> al;
    FloatingActionButton fab;
    MyAdapter myAdapter;
    ViewPager viewPager;
    ProgressBar progressBar;
    boolean done = false;
    LinearLayout data;

    public Upload() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        fab = (FloatingActionButton) (getActivity().findViewById(R.id.fab));
        pref = getActivity().getSharedPreferences("MyPref", getContext().MODE_PRIVATE);
        viewPager = (ViewPager) (getActivity().findViewById(R.id.viewPager));
        progressBar = (ProgressBar) (getActivity().findViewById(R.id.progressBar));
        data = (LinearLayout) (getActivity().findViewById(R.id.data));
        Log.d("MYMSG", "On start finished");
    }

    @Override
    public void onResume() {
        Log.d("MYMSG", "On resume");
        super.onResume();
        listViewOption = (ListView) (getActivity().findViewById(R.id.listViewOption));
        al = new ArrayList<>();
        done = false;
        data.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        new Thread(new CheckOptionsReady()).start();
        new Thread(new ShowOptions()).start();
    }

    class CheckOptionsReady implements Runnable {

        @Override
        public void run() {
            while (!done) {
            }
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    data.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    class ShowOptions implements Runnable {

        @Override
        public void run() {
            sms_value = pref.getBoolean("sms", false);
            call_logs_value = pref.getBoolean("call_logs", false);
            contacts_value = pref.getBoolean("contacts", false);
            images_value = pref.getBoolean("images", false);
            audio_value = pref.getBoolean("audio", false);
            video_value = pref.getBoolean("video", false);
            location_value = pref.getBoolean("location", false);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!contacts_value && !sms_value && !call_logs_value && !audio_value && !images_value && !video_value && !location_value) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Please select at least one option for backup", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        editor.commit();
                        Intent in = new Intent(getContext(), UploadData.class);
                        getContext().startService(in);
                        viewPager.setCurrentItem(0, true);
                    }

                }
            });


            al.add(new Options("SMS", R.drawable.message_icon, sms_message(), sms_value));
            al.add(new Options("Contacts", R.drawable.user_icon, contacts_message(), contacts_value));
            al.add(new Options("Call logs", R.drawable.calling_phone_icon, call_logs_message(), call_logs_value));
            al.add(new Options("Images", R.drawable.image_icon, image_message(), images_value));
            al.add(new Options("Music", R.drawable.music_icon, music_message(), audio_value));
            al.add(new Options("Video", R.drawable.video_icon, video_message(), video_value));
            al.add(new Options("Location", R.drawable.location_icon, location_message(), location_value));

            myAdapter = new MyAdapter();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listViewOption.setAdapter(myAdapter);
                }
            });
            editor = pref.edit();
            done = true;
        }
    }

    private String location_message() {
        boolean b_gps, b_nw;
        LocationManager lm = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
        b_gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        b_nw = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (b_gps || b_nw) {
            return "Location Service is ON";
        } else {
            return "Location Service is OFF";
        }
    }

    private String sms_message() {
        Uri inboxuri = Telephony.Sms.CONTENT_URI;
        Cursor c = getActivity().getContentResolver().query(inboxuri, null, null, null, null);
        int count = c.getCount();
        c.close();
        return count + " SMS";
    }

    private String contacts_message() {

        Uri contactsuri = ContactsContract.Contacts.CONTENT_URI;
        Cursor c = getActivity().getContentResolver().query(contactsuri, null, null, null, null);

        final int COLUMNFORNAME = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        final int COLUMNFORID = c.getColumnIndex(ContactsContract.Contacts._ID);
        final int COLUMNFORHASPHONE = c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        String pname = "";
        int count=0;
        while (c.moveToNext()) {

            int id = c.getInt(COLUMNFORID);
            String displayName = c.getString(COLUMNFORNAME);
            int hasPhoneNum = c.getInt(COLUMNFORHASPHONE);
//               Log.d("CONTACTS", "Name : " + displayName);
            if (hasPhoneNum == 1) // --- Contact Has a Phone Number
            {
                Uri uri2 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                Cursor cnew =getActivity().getContentResolver().query(uri2, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
                        null, null);
                String pno = "";

                while (cnew.moveToNext()) {
                    String phoneNumber = cnew.getString(cnew.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String p_no = "";
                    StringTokenizer st = new StringTokenizer(phoneNumber, " -()");
                    while (st.hasMoreTokens()) {
                        p_no += st.nextToken();
                    }
                    if (pno.equals(p_no)) {
                    } else {
                        pno = p_no;
                        count++;
                    }
                }
                cnew.close();
            } else {
                if (pname.equals(displayName)) {
                } else {
                    pname = displayName;
                    count++;
                }
            }
        }
        c.close();
        return count + " Record(s)";
    }

    private String call_logs_message() {
        Uri callUri = CallLog.Calls.CONTENT_URI;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        Cursor cursor = getActivity().getContentResolver().query(callUri, null, null, null, CallLog.Calls._ID + " Desc");
        int count=cursor.getCount();
        cursor.close();
        return count+" Record(s)";
    }
    private  String music_message(){
        Uri audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection =
                {
                        MediaStore.Audio.Media.SIZE
                };

        Cursor cursor = getActivity().getContentResolver().query(audioUri, projection, selection, null, null);
        long audio_size=0;
        int count=0;
        while(cursor.moveToNext()){
            audio_size+=Long.parseLong(cursor.getString(0));
            count++;
        }
        cursor.close();
        double size=audio_size/1024;
        double m = size / 1024.0;
        double g = size / 1048576.0;
        double t = size / 1073741824.0;
        DecimalFormat dec = new DecimalFormat("0.00");

        String hrSize="";
        if (t > 1) {
            hrSize = dec.format(t).concat("TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat("GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat("MB");
        } else {
            hrSize = dec.format(size).concat("KB");
        }
        return count+" File(s)  Size: "+hrSize;
    }
    private String image_message(){
        Uri imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]
                {
                        MediaStore.Images.Media.SIZE
                };

        Cursor cursor = getActivity().getContentResolver().query(imagesUri, projection, null, null, null);
        long image_size=0;
        int count=0;
        while(cursor.moveToNext()){
            image_size+=Long.parseLong(cursor.getString(0));
            count++;
        }
        cursor.close();
        double size=image_size/1024;
        double m = size / 1024.0;
        double g = size / 1048576.0;
        double t = size / 1073741824.0;
        DecimalFormat dec = new DecimalFormat("0.00");

        String hrSize="";
        if (t > 1) {
            hrSize = dec.format(t).concat("TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat("GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat("MB");
        } else {
            hrSize = dec.format(size).concat("KB");
        }
        return count+" File(s)  Size: "+hrSize;
    }
    private String video_message(){
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection =
                {
                        MediaStore.Video.Media.SIZE,
                };

        Cursor cursor = getActivity().getContentResolver().query(videoUri, projection, null, null, null);
        long video_size=0;
        int count=0;
        while(cursor.moveToNext()){
            video_size+=Long.parseLong(cursor.getString(0));
            count++;
        }
        cursor.close();
        double size=video_size/1024;
        double m = size / 1024.0;
        double g = size / 1048576.0;
        double t = size / 1073741824.0;
        DecimalFormat dec = new DecimalFormat("0.00");

        String hrSize="";
        if (t > 1) {
            hrSize = dec.format(t).concat("TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat("GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat("MB");
        } else {
            hrSize = dec.format(size).concat("KB");
        }
        return count+" File(s)  Size: "+hrSize;
    }


    class MyAdapter extends BaseAdapter{

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
        public View getView(final int position, View convertView, ViewGroup parent) {
//            Log.d("MYMSG","In conver view");
//            Log.d("MYMSG","Al size="+al.size());
            LayoutInflater l=LayoutInflater.from(getContext());
            convertView=l.inflate(R.layout.design_options,parent,false);
            TextView textViewName=(TextView) (convertView.findViewById(R.id.textViewName));
            TextView textViewMessage=(TextView) (convertView.findViewById(R.id.textViewMessage));
            ImageView imageView=(ImageView) (convertView.findViewById(R.id.imageView));
            final Switch button=(Switch)(convertView.findViewById(R.id.button));
                textViewName.setText(al.get(position).name);
                textViewMessage.setText(al.get(position).message);
                imageView.setImageResource(al.get(position).icon);
                button.setChecked(al.get(position).selected);
                button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        switch (position) {
                            case 0:
                                sms_value = isChecked;
                                editor.putBoolean("sms", isChecked);
                                break;
                            case 1:
                                contacts_value = isChecked;
                                editor.putBoolean("contacts", isChecked);
                                break;
                            case 2:
                                call_logs_value = isChecked;
                                editor.putBoolean("call_logs", isChecked);
                                break;
                            case 3:
                                images_value = isChecked;
                                editor.putBoolean("images", isChecked);
                                break;
                            case 4:
                                audio_value = isChecked;
                                editor.putBoolean("audio", isChecked);
                                break;
                            case 5:
                                video_value = isChecked;
                                editor.putBoolean("video", isChecked);
                                break;
                            case 6:
                                if(isChecked) {
                                    dialog();
                                    button.setChecked(location);
                                    location_value = location;
                                    editor.putBoolean("location", location);
                                }
                                break;
                        }

                    }
                });
            return convertView;
        }
    }
    boolean location=false;
    private void dialog(){
        LocationManager lm= (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean nw=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(!gps&&!nw){
            AlertDialog.Builder msg=new AlertDialog.Builder(getContext());
            msg.setTitle("Location");
            msg.setMessage("Please turn on your location for the backup of it");
            msg.setCancelable(false);
            msg.setIcon(R.drawable.location_icon);
            msg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent in=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(in);
                }
            });
            msg.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            msg.create();
            msg.show();
        }
        else{
            location=true;
        }
    }
    private class Options{
        String name;
        int icon;
        String message;
        boolean selected;

        Options(String name, int icon, String message, boolean selected) {
            this.name = name;
            this.icon = icon;
            this.message = message;
            this.selected = selected;
        }
    }
}
