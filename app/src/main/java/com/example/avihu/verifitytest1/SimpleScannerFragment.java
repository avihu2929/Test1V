package com.example.avihu.verifitytest1;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.persistence.BackendlessDataQuery;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class SimpleScannerFragment extends Fragment implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    String result;
    public Context context;
    FragmentListener listener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mScannerView = new ZBarScannerView(getActivity());
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
     //   Toast.makeText(getActivity(), "Contents = " + rawResult.getContents() +
       //         ", Format = " + rawResult.getBarcodeFormat().getName(), Toast.LENGTH_SHORT).show();

        result=rawResult.getContents();


        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listener.onReceive(result);
                mScannerView.resumeCameraPreview(SimpleScannerFragment.this);
            }
        }, 2000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
    public interface FragmentListener {
        void onReceive(String contents);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context=getActivity();
        listener =(FragmentListener)context;
    }
}