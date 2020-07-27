package com.india.rapchik.Video_Recording;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.india.rapchik.Main_Menu.MainMenuActivity;
import com.india.rapchik.R;
import com.india.rapchik.Services.ServiceCallback;
import com.india.rapchik.Services.Upload_Service;
import com.india.rapchik.SimpleClasses.Functions;
import com.india.rapchik.SimpleClasses.Variables;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Post_Video_A extends AppCompatActivity implements ServiceCallback,View.OnClickListener {


    ImageView video_thumbnail;
    String video_path;

    ServiceCallback serviceCallback;
    EditText description_edit;

    String draft_file;

    TextView privcy_type_txt;
    Switch comment_switch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_video);

        Intent intent=getIntent();
        if(intent!=null){
            draft_file=intent.getStringExtra("draft_file");
        }


        video_path = Variables.output_filter_file;
        video_thumbnail = findViewById(R.id.video_thumbnail);


        description_edit=findViewById(R.id.description_edit);

        // this will get the thumbnail of video and show them in imageview
        Bitmap bmThumbnail;
        bmThumbnail = ThumbnailUtils.createVideoThumbnail(video_path,
                MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

        if (bmThumbnail != null) {
            video_thumbnail.setImageBitmap(bmThumbnail);
        } else {
        }






      privcy_type_txt=findViewById(R.id.privcy_type_txt);
      comment_switch=findViewById(R.id.comment_switch);

      comment_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

      findViewById(R.id.Goback).setOnClickListener(this);

      findViewById(R.id.privacy_type_layout).setOnClickListener(this);
      findViewById(R.id.post_btn).setOnClickListener(this);
      findViewById(R.id.save_draft_btn).setOnClickListener(this);

}


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.Goback:
                onBackPressed();
                break;

            case R.id.privacy_type_layout:
                Privacy_dialog();
                break;

            case R.id.save_draft_btn:
                Save_file_in_draft();
                break;

            case R.id.post_btn:
                Start_Service();
                break;
        }
    }



    private void Privacy_dialog() {
        final CharSequence[] options = new CharSequence[]{"Public","Friend","Private"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);

        builder.setTitle(null);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {
                privcy_type_txt.setText(options[item]);

            }

        });

        builder.show();

    }







    // this will start the service for uploading the video into database
    public void Start_Service(){

        serviceCallback=this;

        Upload_Service mService = new Upload_Service(serviceCallback);
        if (!Functions.isMyServiceRunning(this,mService.getClass())) {
            Intent mServiceIntent = new Intent(this.getApplicationContext(), mService.getClass());
            mServiceIntent.setAction("startservice");
            mServiceIntent.putExtra("draft_file",draft_file);
            mServiceIntent.putExtra("uri",""+ video_path);
            mServiceIntent.putExtra("desc",""+description_edit.getText().toString());
            mServiceIntent.putExtra("privacy_type",privcy_type_txt.getText().toString());

            if(comment_switch.isChecked())
              mServiceIntent.putExtra("allow_comment","true");
             else
                mServiceIntent.putExtra("allow_comment","false");

            startService(mServiceIntent);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Post_Video_A.this, MainMenuActivity.class));
                }
            },1000);


        }
        else {
            Toast.makeText(this, "Please wait video already in uploading progress", Toast.LENGTH_LONG).show();
        }


    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }




    // when the video is uploading successfully it will restart the appliaction
    @Override
    public void ShowResponce(final String responce) {

        if(mConnection!=null)
        unbindService(mConnection);
        Toast.makeText(this, responce, Toast.LENGTH_SHORT).show();

    }




    // this is importance for binding the service to the activity
    Upload_Service mService;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

           Upload_Service.LocalBinder binder = (Upload_Service.LocalBinder) service;
            mService = binder.getService();

            mService.setCallbacks(Post_Video_A.this);



        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




    public void Save_file_in_draft(){
       File source = new File(video_path);
       File destination = new File(Variables.draft_app_folder+Functions.getRandomString()+".mp4");
        try
        {
            if(source.exists()){

                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(destination);

                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();

                Toast.makeText(Post_Video_A.this, "File saved in Draft", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Post_Video_A.this, MainMenuActivity.class));

            }else{
                Toast.makeText(Post_Video_A.this, "File failed to saved in Draft", Toast.LENGTH_SHORT).show();

            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }






 /*

  private void UploadVideo() {
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, Variables.uploadVideo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String resp) {
                        Log.d(Variables.tag,resp);
                        Functions.cancel_loader();
                        try {
                            JSONObject jsonObject = new JSONObject(resp);
                            String code = jsonObject.optString("code");
                            if (code.equalsIgnoreCase("200")) {
                                Upload_responce(resp);
                            } else
                                Toast.makeText(Post_Video_A.this, resp, Toast.LENGTH_SHORT).show();
                        }catch (JSONException e){
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){

            @Override
            public String getBodyContentType() {
                return "multipart/form-data; charset=utf-8";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "multipart/form-data");
                headers.put("fb-id",Variables.sharedPreferences.getString(Variables.u_id,"0"));
                headers.put("version", getResources().getString(R.string.version));
                headers.put("device", getResources().getString(R.string.device));
                headers.put("tokon", Variables.sharedPreferences.getString(Variables.api_token,""));
                headers.put("deviceid", Variables.sharedPreferences.getString(Variables.device_id,""));
                Log.d(Variables.tag,headers.toString());
                return headers;
            }
        };

      *//*  Map map=new HashMap();
        map.put("fb_id", Variables.user_id);
        map.put("sound_id", Variables.Selected_sound_id);
        map.put("description", description_edit.getText().toString());
        map.put("privacy_type", privcy_type_txt.getText().toString());
        if(comment_switch.isChecked())
            map.put("allow_comment","true");
        else
            map.put("allow_comment","false");

        smr.setParams(map);*//*
        smr.addStringParam("fb_id", Variables.user_id);
        smr.addStringParam("sound_id", Variables.Selected_sound_id);
        smr.addStringParam("description", description_edit.getText().toString());
        smr.addStringParam("privacy_type", privcy_type_txt.getText().toString());
        if(comment_switch.isChecked())
            smr.addStringParam("allow_comment","true");
        else
            smr.addStringParam("allow_comment","false");

        smr.addMultipartParam("uploaded_file","video/mp4",video_path+Functions.getRandomString()+".mp4");
        Log.d(Variables.tag,smr.toString());
       // Log.d(Variables.tag,map.toString());
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mRequestQueue.add(smr);
    }


*/



}
