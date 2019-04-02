package io.ideaction.sketchproject.Activities;

import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import io.ideaction.sketchproject.R;

public class MediaPlayerAc extends AppCompatActivity implements SurfaceHolder.Callback {


    MediaPlayer mediaPlayer;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean pausing = false;;
    Button buttonPlayVideo;
    String stringPath = "http://ec2-18-223-224-68.us-east-2.compute.amazonaws.com/api/v1/getVideo/283";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        if (getIntent().getStringExtra("path") !=  null) {
            stringPath = getIntent().getStringExtra("path");
        }
         buttonPlayVideo = (Button)findViewById(R.id.playvideoplayer);
        Button buttonPauseVideo = (Button)findViewById(R.id.pausevideoplayer);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView)findViewById(R.id.surfaceview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFixedSize(176, 144);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mediaPlayer = new MediaPlayer();

        buttonPlayVideo.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                pausing = false;

                if(mediaPlayer.isPlaying()){
                    mediaPlayer.reset();
                }

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDisplay(surfaceHolder);

                try {
                    mediaPlayer.setDataSource(stringPath);
                    mediaPlayer.prepare();
                } catch (IllegalArgumentException e) {

                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();


            }});

        buttonPauseVideo.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(pausing){
                    pausing = false;
                    mediaPlayer.start();
                }
                else{
                    pausing = true;
                    mediaPlayer.pause();
                }
            }});



    }



    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
// TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        buttonPlayVideo.callOnClick();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        mediaPlayer.release();

    }


    @Override
    protected void onDestroy() {
        mediaPlayer.release();
        super.onDestroy();
    }
}
