package com.facerecognitionkairosdemo.marka.facerecognitionkairosdemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.kairos.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

public class RegisterActivity extends AppCompatActivity {
    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    Kairos myKairos;
    KairosListener listener;
    Boolean cancel = true;
    String subjectId;
    String encodedImage;
    String galleryId;
    JSONObject jo;
    JSONArray ja;
    Boolean exists = false;
    String string;
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
                if(!exists) {
                    JSONObject temp = new JSONObject();
                    try {
                        temp.put("name", string);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ja.put(temp);

                    try{
                        File f = new File(getFilesDir(), "file.ser");
                        FileOutputStream fo = new FileOutputStream(f);
                        ObjectOutputStream o = new ObjectOutputStream(fo);
                        String j = jo.toString();
                        o.writeObject(j);
                        o.close();
                        fo.close();
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
                Intent i = new Intent(RegisterActivity.this, AdminActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }

            @Override
            public void onFail(String s) {
                Toast.makeText(getBaseContext(),"Cannot recognize face",Toast.LENGTH_LONG).show();
            }
        };

        try {
            File f = new File(getFilesDir(), "file.ser");
            FileInputStream fi = new FileInputStream(f);
            ObjectInputStream o = new ObjectInputStream(fi);
            // Notice here that we are de-serializing a String object (instead of
            // a JSONObject object) and passing the String to the JSONObject’s
            // constructor. That’s because String is serializable and
            // JSONObject is not. To convert a JSONObject back to a String, simply
            // call the JSONObject’s toString method.
            String j = null;
            try {
                j = (String) o.readObject();
            } catch (ClassNotFoundException c) {
                c.printStackTrace();
            }
            try {
                jo = new JSONObject(j);
                ja = jo.getJSONArray("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch(IOException e){
            jo = new JSONObject();
            ja = new JSONArray();
            try{
                jo.put("data", ja);
            }
            catch(JSONException j){
                j.printStackTrace();
            }
        }

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
            encodedImage = Base64.encodeToString(data, Base64.DEFAULT);
            galleryId = "Company";
            showEnterLabelDialog();
            camera.startPreview();
        }
    };

    public void captureImage(View v){
        if(camera != null){
            camera.takePicture(null, null, mPictureCallback);
        }
    }

    private void showEnterLabelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Please enter your name:");

        final EditText input = new EditText(RegisterActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Submit", null); // Set up positive button, but do not provide a listener, so we can check the string before dismissing the dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false); // User has to input a name
        AlertDialog dialog = builder.create();

        // Source: http://stackoverflow.com/a/7636468/2175837
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button mButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        string = input.getText().toString().trim();
                        if (!string.isEmpty()) { // Make sure the input is valid
                            // If input is valid, dismiss the dialog and add the label to the array
                            subjectId = string;
                            dialog.dismiss();
                            try {
                                myKairos.enroll(encodedImage, subjectId, galleryId, null, null, null, listener);
                            } catch (JSONException e1) {

                            } catch (UnsupportedEncodingException e2) {

                            }
                            //addLabel(string);
                            //check if the name entered is already registered
                            for(int i = 0; i < ja.length(); i++){
                                try {
                                    if(string.equals(ja.getJSONObject(i).getString("name"))){
                                        exists = true;
                                        break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
        });

        // Show keyboard, so the user can start typing straight away
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();
    }
}
