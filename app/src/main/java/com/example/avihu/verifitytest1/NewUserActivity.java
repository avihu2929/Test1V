package com.example.avihu.verifitytest1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.Files;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.ArrayList;
import java.util.List;

public class NewUserActivity extends AppCompatActivity {

    ImageView imageView;
    int count;
    String objectId,content,picurl;
    BackendlessDataQuery dataQuery;
    Button buttonPicture,buttonSave;
    EditText editTextF,editTextL;
    Bitmap imageBitmap;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        Intent intent = getIntent();
        objectId = intent.getStringExtra("OBJECTID");
        content = intent.getStringExtra("CONTENT");
        count=6;
        Toast.makeText(this,intent.getStringExtra("CONTENT"),Toast.LENGTH_LONG).show();
        imageView = (ImageView) findViewById(R.id.imageView);
        buttonPicture = (Button) findViewById(R.id.buttonPicture);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        editTextF = (EditText) findViewById(R.id.editTextFname);
        editTextL = (EditText) findViewById(R.id.editTextLname);
        spinner= (Spinner) findViewById(R.id.spinner2);
        List<String> categories = new ArrayList<String>();
        categories.add("6");
        categories.add("12");
        categories.add("24");
        categories.add("36");
        categories.add(getResources().getString(R.string.freeyear));


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if (item.equals(getResources().getString(R.string.freeyear))){
                    count = 9999;
                }else{
                    count = Integer.valueOf(item);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {///

                Backendless.Files.Android.upload(imageBitmap, Bitmap.CompressFormat.PNG, 100, objectId + ".png", "pics",true, new AsyncCallback<BackendlessFile>() {
                    @Override
                    public void handleResponse(BackendlessFile response) {
                        Toast.makeText(NewUserActivity.this, "Pic Saved", Toast.LENGTH_SHORT).show();
                        picurl = response.getFileURL();

                        VaildCodes update = new VaildCodes();
                        update.setFname(editTextF.getText().toString());
                        update.setLname(editTextL.getText().toString());
                        update.setCount(count);
                        update.setContent(content);
                        update.setPic(picurl);
                        update.setVaild(1);
                        update.setObjectId(objectId);
                        Backendless.Persistence.save(update, new AsyncCallback<VaildCodes>() {
                            @Override
                            public void handleResponse(VaildCodes response) {
                                Toast.makeText(NewUserActivity.this, "Person Saved", Toast.LENGTH_LONG).show();
                                Intent intent1 = new Intent(NewUserActivity.this, SimpleScannerFragmentActivity.class);
                                startActivity(intent1);
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(NewUserActivity.this, "ERROR", Toast.LENGTH_LONG).show();

                            }
                        });

                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Toast.makeText(NewUserActivity.this, "Picture Error", Toast.LENGTH_LONG).show();
                    }
                });

            }///
        });
        buttonPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            buttonPicture.setVisibility(View.GONE);
            buttonSave.setVisibility(View.VISIBLE);
        }
    }
}
