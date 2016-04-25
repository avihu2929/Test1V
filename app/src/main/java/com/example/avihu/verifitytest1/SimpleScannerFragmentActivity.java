package com.example.avihu.verifitytest1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.io.InputStream;
import java.net.URL;

public class SimpleScannerFragmentActivity extends BaseScannerActivity implements SimpleScannerFragment.FragmentListener{

    TextView textView;
    Button buttonVaild , buttonNew;
    BackendlessDataQuery dataQuery;
    VaildCodes vaildCodes;
    String content1;
    ProgressDialog dlg;
    ProgressDialog dlg2;
    ImageView img;
    Bitmap bitmap;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_simple_scanner_fragment);
        setupToolbar();
        dlg = new ProgressDialog(SimpleScannerFragmentActivity.this);
        dlg.setTitle(getResources().getString(R.string.pls_wait));
        dlg.setCancelable(false);
        dlg.setCanceledOnTouchOutside(false);
        dlg2 = new ProgressDialog(SimpleScannerFragmentActivity.this);
        dlg2.setTitle(getResources().getString(R.string.load_pic));
        dlg2.setCancelable(false);
        dlg2.setCanceledOnTouchOutside(false);
        img = (ImageView) findViewById(R.id.personImage);
        textView = (TextView) findViewById(R.id.textView);
        buttonNew = (Button) findViewById(R.id.buttonNew);
        buttonVaild = (Button) findViewById(R.id.buttonVaild);
        buttonVaild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vaildCodes.setCount(vaildCodes.count - 1);
                dlg.show();
                img.setImageDrawable(null);
                img.setVisibility(View.GONE);
                Backendless.Persistence.save(vaildCodes, new AsyncCallback<VaildCodes>() {
                    @Override
                    public void handleResponse(VaildCodes response) {
                        dlg.dismiss();

                        Toast.makeText(SimpleScannerFragmentActivity.this,"SAVED",Toast.LENGTH_LONG).show();
                        buttonVaild.setVisibility(View.GONE);
                        textView.setText(getResources().getString(R.string.scancode));
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        dlg.dismiss();
                    }
                });
            }
        });
        buttonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SimpleScannerFragmentActivity.this,NewUserActivity.class);
                intent.putExtra("OBJECTID",vaildCodes.getObjectId());
                intent.putExtra("CONTENT",content1);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onReceive(String contents) {
        dlg.show();
        img.setImageDrawable(null);
        content1=contents;
        String whereClause = "content = '"+contents+"'";
        dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of( VaildCodes.class ).find(dataQuery, new AsyncCallback<BackendlessCollection<VaildCodes>>() {
            @Override
            public void handleResponse(BackendlessCollection<VaildCodes> response) {
                dlg.dismiss();

//                VaildCodes code = response.getCurrentPage().get(0);
                if (response.getCurrentPage().size()>0) {

                    if (response.getCurrentPage().get(0).vaild == 1) {

                        new LoadImage().execute(response.getCurrentPage().get(0).pic);
                        vaildCodes=response.getCurrentPage().get(0);
                        buttonVaild.setVisibility(View.VISIBLE);
                        buttonNew.setVisibility(View.GONE);
                        if (response.getCurrentPage().get(0).count>300){
                            textView.setText(response.getCurrentPage().get(0).fname + " " + response.getCurrentPage().get(0).lname + "\n " + getResources().getString(R.string.freeyear));
                        }else if(response.getCurrentPage().get(0).count<=0){
                            textView.setText(response.getCurrentPage().get(0).fname + " " + response.getCurrentPage().get(0).lname + "\n " + getResources().getString(R.string.nomore));
                        }else{
                            textView.setText(response.getCurrentPage().get(0).fname + " " + response.getCurrentPage().get(0).lname + "\n " + response.getCurrentPage().get(0).count +" "+getResources().getString(R.string.points) );
                        }

                    } else {
                        img.setVisibility(View.GONE);
                        vaildCodes=response.getCurrentPage().get(0);
                        textView.setText(getResources().getString(R.string.unregisteredcode));
                        buttonVaild.setVisibility(View.GONE);
                        buttonNew.setVisibility(View.VISIBLE);
                    }
                }else{
                    textView.setText(getResources().getString(R.string.unvaildcode));
                    buttonVaild.setVisibility(View.GONE);
                    img.setVisibility(View.GONE);
                    buttonNew.setVisibility(View.GONE);
                }

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                dlg.dismiss();
                textView.setText(getResources().getString(R.string.unvaildcode));
                buttonVaild.setVisibility(View.GONE);
                buttonNew.setVisibility(View.GONE);

            }
        });

        //BackendlessCollection<VaildCodes> result = Backendless.Persistence.of( VaildCodes.class ).find( dataQuery );
       // BackendlessCollection<VaildCodes> result = Backendless.Persistence.of(VaildCodes.class).f





    }



    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dlg2.show();
        }
        protected Bitmap doInBackground(String... args) {
            bitmap=null;
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            dlg2.dismiss();
            if(image != null){

                img.setImageBitmap(image);
                img.setVisibility(View.VISIBLE);

            }else{
                Toast.makeText(SimpleScannerFragmentActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }
}