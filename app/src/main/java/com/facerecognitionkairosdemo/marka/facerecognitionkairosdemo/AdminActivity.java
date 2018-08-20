package com.facerecognitionkairosdemo.marka.facerecognitionkairosdemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.kairos.Kairos;
import com.kairos.KairosListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {
    Button reg;
    Button remove;
    Button adminlogout;
    ListView userList;
    Kairos myKairos;
    KairosListener listener;
    JSONObject jo;
    JSONArray ja;
    String[] listItems;
    int counter;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        reg = (Button)findViewById(R.id.reg);
        remove = (Button)findViewById(R.id.remove);
        adminlogout = (Button)findViewById(R.id.adminlogout);
        userList = (ListView)findViewById(R.id.userList);
        myKairos = new Kairos();
        String app_id = "793a284b";
        String api_key = "d1ad8c7199a0ab9f700146b1065bbe95";
        myKairos.setAuthentication(AdminActivity.this, app_id, api_key);
        listener = new KairosListener() {
            @Override
            public void onSuccess(String s) {
                Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(String s) {

            }
        };


        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AdminActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Reading a file that already exists
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
                }catch(IOException e){
                    // There's no JSON file that exists, so don't
                    // show the list. But also don't worry about creating
                    // the file just yet, that takes place in AddText.

                    //Here, disable the list view
                    userList.setEnabled(false);
                    userList.setVisibility(View.INVISIBLE);

                }
                showEnterLabelDialog();
                /*JSONArray list = new JSONArray();
                int len = ja.length();
                if (ja != null) {
                    for (int i=0;i<len;i++) {
                        //Excluding the item at position
                        if (i != counter) {
                            try {
                                list.put(ja.get(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }*/

            }
        });
        adminlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AdminActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        try {
            myKairos.listSubjectsForGallery("Company", listener);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        jo = null;
        try {
            // Reading a file that already exists
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
            if(ja.length() == 0){
                userList.setEnabled(false);
                userList.setVisibility(View.INVISIBLE);
            }
            final ArrayList<String> aList = new ArrayList<String>();
            for(int i = 0; i < ja.length(); i++){
                String temp = "";
                try{
                    temp = ja.getJSONObject(i).getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                aList.add(temp);
            }

            listItems = new String[aList.size()];

            for(int i = 0; i < aList.size(); i++){
                String listD = aList.get(i);
                listItems[i] = listD;
            }

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);
            userList.setAdapter(adapter);
        }catch(IOException e){
            // There's no JSON file that exists, so don't
            // show the list. But also don't worry about creating
            // the file just yet, that takes place in AddText.

            //Here, disable the list view
            userList.setEnabled(false);
            userList.setVisibility(View.INVISIBLE);

        }
    }

    private void showEnterLabelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
        builder.setTitle("Please enter your name:");

        final EditText input = new EditText(AdminActivity.this);
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
                        String string = input.getText().toString().trim();
                        if (!string.isEmpty()) { // Make sure the input is valid
                            // If input is valid, dismiss the dialog and add the label to the array
                            /*counter = 0;
                            for(int i = 0; i < listItems.length; i++){
                                if(listItems[i].equals(string)){
                                    counter = i;
                                    break;
                                }
                            }*/
                            try {
                                myKairos.deleteSubject(string, "Company", listener);
                                //Toast.makeText(getBaseContext(),"User deleted",Toast.LENGTH_LONG).show();
                            }catch(JSONException e1){
                                e1.printStackTrace();
                            }catch(UnsupportedEncodingException e2){
                                e2.printStackTrace();
                            }

                            dialog.dismiss();

                            //addLabel(string);
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
