package com.facerecognitionkairosdemo.marka.facerecognitionkairosdemo;


import android.content.Intent;
import android.hardware.Camera;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

public class RecognitionActivity extends AppCompatActivity {
    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    Kairos myKairos;
    KairosListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        frameLayout = (FrameLayout)findViewById(R.id.frameLayout);
        myKairos = new Kairos();
        String app_id = "793a284b";
        String api_key = "d1ad8c7199a0ab9f700146b1065bbe95";
        myKairos.setAuthentication(this, app_id, api_key);
        listener = new KairosListener() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(getBaseContext(),s,Toast.LENGTH_LONG).show();
                Intent i = new Intent(RecognitionActivity.this, LogOutActivity.class);
                startActivity(i);
            }

            @Override
            public void onFail(String s) {
                Toast.makeText(getBaseContext(),"Cannot recognize face",Toast.LENGTH_LONG).show();
            }
        };

        //open camera
        camera = Camera.open(1);

        showCamera = new ShowCamera(this, camera);
        frameLayout.addView(showCamera);

    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            /*Bitmap capturedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();*/
            String encodedImage = Base64.encodeToString(data, Base64.DEFAULT);
            String subjectId = "Wally";
            String galleryId = "Company";
            try {
                myKairos.recognize(encodedImage, galleryId, null, null, null, null, listener);
            }catch(JSONException e1){

            }catch(UnsupportedEncodingException e2){

            }
            camera.startPreview();
        }
    };

    public void captureImage(View v){
        if(camera != null){
            camera.takePicture(null,null, mPictureCallback);
        }
    }
}
