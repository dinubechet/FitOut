package io.ideaction.sketchproject.Activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.ads.MobileAds;

import io.ideaction.sketchproject.ApiRequests.API;
import io.ideaction.sketchproject.LogInFragments.MainLogInFragment;
import io.ideaction.sketchproject.LogInFragments.TutorialFragment;
import io.ideaction.sketchproject.MainScreenFragments.MainScreenFragment;
import io.ideaction.sketchproject.MainScreenFragments.NotificationsScreenFragment;
import io.ideaction.sketchproject.R;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static void showLoadingIndicator(View view) {
        view.findViewById(R.id.loading).setVisibility(View.VISIBLE);
    }

    public static void hideLoadingIndicator(View view) {
        view.findViewById(R.id.loading).setVisibility(View.GONE);
    }

    public static API APIBuild() {
        return new Retrofit.Builder()
                .baseUrl("http://ec2-18-223-224-68.us-east-2.compute.amazonaws.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(API.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        Fresco.initialize(this);
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~1458002511");


        if (getIntent().getStringExtra("TYPE") != null) {

            draw(new NotificationsScreenFragment());
            return;
        }

        if (getIntent().getStringExtra("message") != null) {
            draw(new MainScreenFragment(true));
            return;
        }

        if (!getSharedPreferences("tutorial", MODE_PRIVATE).getString("pass", "").equals("true")) {
            new CountDownTimer(500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    draw(new TutorialFragment());
                }
            }.start();
        } else if (!getSharedPreferences("my", MODE_PRIVATE).getString("token", "").equals("")) {
            new CountDownTimer(500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    draw(new MainScreenFragment());

                }
            }.start();
        } else {
            new CountDownTimer(500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    draw(new MainLogInFragment());
                }
            }.start();
        }
    }

    public void draw(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.tutorial, fragment);
        fragmentTransaction.commit();
    }

    public void enterFromLeft(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right);
        transaction.replace(R.id.tutorial, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void enterFromRight(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left);
        transaction.replace(R.id.tutorial, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
