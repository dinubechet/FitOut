package io.ideaction.sketchproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import java.io.File;

import io.ideaction.sketchproject.videoTrimmer.HgLVideoTrimmer;
import io.ideaction.sketchproject.videoTrimmer.interfaces.OnHgLVideoListener;
import io.ideaction.sketchproject.videoTrimmer.interfaces.OnTrimVideoListener;

public class TrimmerActivity extends AppCompatActivity implements OnTrimVideoListener, OnHgLVideoListener {

    private HgLVideoTrimmer mVideoTrimmer;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimmer);

        Intent extraIntent = getIntent();
        String path = "";
        int maxDuration = 25000;

        if (extraIntent != null) {
            path = extraIntent.getStringExtra("path");
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getString(R.string.trimming_progress));

        mVideoTrimmer = ((HgLVideoTrimmer) findViewById(R.id.timeLine));
        if (mVideoTrimmer != null) {

            /**
             * get total duration of video file
             */
            Log.e("tg", "maxDuration = " + maxDuration);
             //mVideoTrimmer.setMaxDuration(maxDuration);
            mVideoTrimmer.setMaxDuration(maxDuration);
            mVideoTrimmer.setOnTrimVideoListener(this);
            mVideoTrimmer.setOnHgLVideoListener(this);
            mVideoTrimmer.setDestinationPath("/storage/emulated/0/DCIM/CameraCustom/");
            mVideoTrimmer.setVideoURI(Uri.parse(path));
            mVideoTrimmer.setVideoInformationVisibility(true);
        }
    }

    @Override
    public void onTrimStarted() {
        mProgressDialog.show();
    }

    @Override
    public void getResult(final Uri contentUri) {
        mProgressDialog.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });

        try {
            String path = contentUri.getPath();
            setResult(Activity.RESULT_OK,
                    new Intent().putExtra("path", path));
            finish();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("tg", "resultCode = " + resultCode + " data " + data);
    }

    @Override
    public void cancelAction() {
        mProgressDialog.cancel();
        mVideoTrimmer.destroy();
        finish();
    }

    @Override
    public void onError(final String message) {
        mProgressDialog.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    @Override
    public void onVideoPrepared() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }
}
