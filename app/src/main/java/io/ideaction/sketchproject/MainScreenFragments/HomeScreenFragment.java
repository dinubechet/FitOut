package io.ideaction.sketchproject.MainScreenFragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.ideaction.sketchproject.Activities.EventsActivity;
import io.ideaction.sketchproject.Activities.FeedActivity;
import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.Activities.SportActivity;
import io.ideaction.sketchproject.Models.NotificationModel;
import io.ideaction.sketchproject.MyFirebaseMessagingService;
import io.ideaction.sketchproject.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeScreenFragment extends Fragment {

    CircleImageView profilePicture;
    TextView helloText;
    TextView helloText2;
    RelativeLayout button1;
    RelativeLayout button2;
    RelativeLayout button3;
    RelativeLayout button4;
    RelativeLayout button5;
    RelativeLayout button6;
    RelativeLayout bell;
    RelativeLayout pictureButton;
    SharedPreferences sharedPreferences;
    BroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            return;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_home_screen, container, false);
        sharedPreferences = getActivity().getSharedPreferences("my", Context.MODE_PRIVATE);
        profilePicture = view.findViewById(R.id.profile_image);
        bell = view.findViewById(R.id.bell);
        helloText = view.findViewById(R.id.hello_text);
        if (!getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("name", "").equals("")) {
            if (getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("name", "").toCharArray().length < 20) {
                helloText.setText("Hello, " + getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("name", ""));
            } else {
                helloText.setText("Hello, " + getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("name", "").substring(0, 20) + "...");
            }
        }

        pictureButton = view.findViewById(R.id.picture_button);
        helloText2 = view.findViewById(R.id.hello_text2);
        button1 = view.findViewById(R.id.button_1);
        button2 = view.findViewById(R.id.button_2);
        button3 = view.findViewById(R.id.button_3);
        button4 = view.findViewById(R.id.button_4);
        button5 = view.findViewById(R.id.button_5);
        button6 = view.findViewById(R.id.button_6);

        button1.setOnClickListener(v -> {
            button1.setEnabled(false);
            Intent i = new Intent(getActivity(), SportActivity.class);
            i.putExtra("TYPE", "RUNNING");
            getActivity().startActivity(i);
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button2.setEnabled(false);
                Intent i = new Intent(getActivity(), SportActivity.class);
                i.putExtra("TYPE", "YOGA");
                getActivity().startActivity(i);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button3.setEnabled(false);
                Intent i = new Intent(getActivity(), SportActivity.class);
                i.putExtra("TYPE", "BODY BUILDING");
                getActivity().startActivity(i);
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button4.setEnabled(false);
                Intent i = new Intent(getActivity(), SportActivity.class);
                i.putExtra("TYPE", "WEIGHT LOSS");
                getActivity().startActivity(i);
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button5.setEnabled(false);
                Intent i = new Intent(getActivity(), SportActivity.class);
                i.putExtra("TYPE", "HIKING");
                getActivity().startActivity(i);
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button6.setEnabled(false);
                Intent i = new Intent(getActivity(), SportActivity.class);
                i.putExtra("TYPE", "POWER LIFTING");
                getActivity().startActivity(i);
            }
        });

        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setEnabled(false);
                ((MainActivity) getActivity()).enterFromLeft(new ProfileScreenFragment());
            }
        });

        final ImageView bellImg = view.findViewById(R.id.bellImg);
        IntentFilter intentFilter = new IntentFilter(MyFirebaseMessagingService.STRTRTR);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(500);
                }
                bellImg.setImageResource(R.drawable.bell_new);
            }
        };
        getActivity().registerReceiver(broadcastReceiver, intentFilter);


        bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1.setEnabled(false);
                bellImg.setImageResource(R.drawable.bell);
                ((MainActivity) getActivity()).enterFromRight(new NotificationsScreenFragment());
            }
        });

        if (!getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("img", "").equals("")) {
            Picasso.get().load(getActivity().getSharedPreferences("my", Context.MODE_PRIVATE)
                    .getString("img", "").replace("large", "small"))
                    .placeholder(R.drawable.default_image).into(profilePicture);
        }
        getMe();

        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (!isVisibleToUser) {
            Fragment currentFragment = HomeScreenFragment.this;
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.detach(currentFragment);
            fragmentTransaction.attach(currentFragment);
            fragmentTransaction.commit();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        button1.setEnabled(true);
        button2.setEnabled(true);
        button3.setEnabled(true);
        button4.setEnabled(true);
        button5.setEnabled(true);
        button6.setEnabled(true);
        pictureButton.setEnabled(true);
        bell.setEnabled(true);
    }


    void getMe() {
        Call<HashMap> call = MainActivity.APIBuild().getMe("Bearer " +
                getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));
        call.enqueue(new Callback<HashMap>() {
            @Override
            public void onResponse(Call<HashMap> call, Response<HashMap> response) {
                if (response.isSuccessful()) {
                    sharedPreferences.edit().putString("name", response.body().get("name").toString()).apply();
                    if (!response.body().get("avatarThumb").equals("")) {
                        sharedPreferences.edit().putString("img", response.body().get("avatarThumb").toString()).apply();
                        Picasso.get().load(response.body().get("avatarThumb").toString())
                                .placeholder(R.drawable.default_profile).into(profilePicture);
                    }
                    sharedPreferences.edit().putString("username", response.body().get("username").toString()).apply();
                    sharedPreferences.edit().putInt("id", (int) Double.parseDouble(response.body().get("id").toString())).apply();
                    sharedPreferences.edit().putString("foll", response.body().get("followersCount").toString()).apply();
                    sharedPreferences.edit().putString("folling", response.body().get("followingCount").toString()).apply();
                    sharedPreferences.edit().putString("description", response.body().get("description").toString()).apply();
                    sharedPreferences.edit().putString("city", response.body().get("city").toString()).apply();
                    sharedPreferences.edit().putString("country", response.body().get("country").toString()).apply();
                    sharedPreferences.edit().putString("training_place", response.body().get("training_place").toString()).apply();
                    sharedPreferences.edit().putString("training_hours", response.body().get("training_hours").toString()).apply();
                    sharedPreferences.edit().putString("email", response.body().get("email").toString()).apply();

                    if(response.body().get("name").toString().toCharArray().length < 20) {
                        helloText.setText("Hello, " + response.body().get("name").toString());
                    } else {
                        helloText.setText("Hello, " + response.body().get("name").toString().substring(0, 20) + "...");
                    }

                }
            }

            @Override
            public void onFailure(Call<HashMap> call, Throwable t) {
            }
        });
    }
}
