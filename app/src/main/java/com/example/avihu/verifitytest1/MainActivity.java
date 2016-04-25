package com.example.avihu.verifitytest1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.UserService;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.util.persistence.AbstractBackendlessDataObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    AsyncCallback<BackendlessUser> user;
    EditText etID,etPASS;
    List<String> categories;
    TextView hallTextView;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // setupToolbar();
        String appVersion = "v1";

        String APP_ID= "AD6A8602-D126-5352-FFA6-3FBA38B58E00";
        String SECRET_KEY = "58DB9BC3-F27B-F99C-FFC9-E0876FD80500";
        Backendless.initApp(this, APP_ID, SECRET_KEY, appVersion);
        final Button button = (Button) findViewById(R.id.button);
        etID = (EditText) findViewById(R.id.editTextPersonID);
        etPASS = (EditText) findViewById(R.id.editTextPassword);
        hallTextView = (TextView) findViewById(R.id.chooseHallTv);
        Backendless.Data.mapTableToClass( "Places", Places.class );
        //Spinner
        // Spinner element
        spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("IM HEREEEE!@#!@#!@#!@","HEEEEEEEEEEEEEEEEEEEEREEEEERRERE");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Spinner Drop down elements
        categories = new ArrayList<String>();



        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        //Spinner End/
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(MainActivity.this, SimpleScannerFragmentActivity.class);
                startActivity(intent);*/
               Backendless.UserService.login(etID.getText().toString(), etPASS.getText().toString(), user, true);
            }
        });

        user = new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                button.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
                hallTextView.setVisibility(View.VISIBLE);
                Places retrievedPlace = (Places) response.getProperty( "Places" );

               //Order the Spinner halls
                for (int i = 1 ; i<8;i++){
                    if (i==1){
                        if (retrievedPlace.getH1()!=null){
                            categories.add(retrievedPlace.getH1());
                        }
                    }
                    if (i==2){
                        if (retrievedPlace.getH2()!=null){
                            categories.add(retrievedPlace.getH2());
                        }
                    }
                    if (i==3){
                        if (retrievedPlace.getH3()!=null){
                            categories.add(retrievedPlace.getH3());
                        }
                    }
                    if (i==4){
                        if (retrievedPlace.getH4()!=null){
                            categories.add(retrievedPlace.getH4());
                        }
                    }
                    if (i==5){
                        if (retrievedPlace.getH5()!=null){
                            categories.add(retrievedPlace.getH5());
                        }
                    }
                    if (i==6){
                        if (retrievedPlace.getH6()!=null){
                            categories.add(retrievedPlace.getH6());
                        }
                    }

                }

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(MainActivity.this,"Failed: "+fault.getCode(), Toast.LENGTH_SHORT).show();
            }
        };


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
