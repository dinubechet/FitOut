package io.ideaction.sketchproject.MainScreenFragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.ideaction.sketchproject.Activities.EventsActivity;
import io.ideaction.sketchproject.Activities.FeedActivity;
import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.ImageFilePath;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.Util;
import io.ideaction.sketchproject.LogInFragments.MainLogInFragment;
import io.ideaction.sketchproject.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class ProfileScreenFragment extends Fragment {
    private static final int PICK_IMAGE = 100;
    Button forwardButton;
    CircleImageView profileImage;
    TextView profileName;
    TextView atSignProfileName;
    TextView followers;
    TextView following;
    TextView motto;
    LinearLayout locationLayout;
    LinearLayout timeLayout;
    LinearLayout gymLayout;
    TextView userLocation;
    TextView preferredTime;
    TextView preferredGym;
    RelativeLayout profilePictureButton;
    RelativeLayout settings;
    RelativeLayout postsButton;
    RelativeLayout likesButton;
    RelativeLayout eventsButton;
    RelativeLayout partnersButton;
    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_screen_fragment, container, false);
        sharedPreferences = getActivity().getSharedPreferences("my", Context.MODE_PRIVATE);
        forwardButton = view.findViewById(R.id.forward_button);
        postsButton = view.findViewById(R.id.posts);
        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        atSignProfileName = view.findViewById(R.id.at_sign_profile_name);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        motto = view.findViewById(R.id.motto);
        eventsButton = view.findViewById(R.id.events);
        profilePictureButton = view.findViewById(R.id.profile_image_button);
        settings = view.findViewById(R.id.settings);
        likesButton = view.findViewById(R.id.likes);
        partnersButton = view.findViewById(R.id.partners);
        locationLayout = view.findViewById(R.id.location_layout);
        timeLayout = view.findViewById(R.id.time_layout);
        gymLayout = view.findViewById(R.id.gym_layout);
        userLocation = view.findViewById(R.id.location_text);
        preferredTime = view.findViewById(R.id.time_text);
        preferredGym = view.findViewById(R.id.preferred_gym_text);


        if (!getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("img", "").equals(""))
            Picasso.get().load(getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("img", ""))
                    .placeholder(R.drawable.default_image).into(profileImage);

        profileName.setText(getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("name", ""));
        atSignProfileName.setText("@" + getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("username", ""));
        motto.setText(sharedPreferences.getString("description", ""));
        followers.setText("Followers: " + sharedPreferences.getString("foll", "").replace(".0", "") + " | ");
        following.setText("Following: " + sharedPreferences.getString("folling", "").replace(".0", ""));

        followers.setOnClickListener(v -> ((MainActivity)getActivity()).draw(new FollowersFollowingFragment(sharedPreferences.getInt("id",1), "followers", 2)));

        following.setOnClickListener(v -> ((MainActivity)getActivity()).draw(new FollowersFollowingFragment(sharedPreferences.getInt("id",1), "following", 2)));

        profilePictureButton.setOnClickListener(v -> openGallery());

        settings.setOnClickListener(v -> {
            ((MainActivity) getActivity()).enterFromRight(new SettingsFragment());
            settings.setEnabled(false);
            new CountDownTimer(500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    settings.setEnabled(true);
                }
            }.start();
        });

        forwardButton.setOnClickListener(v -> {
            ((MainActivity) getActivity()).enterFromRight(new MainScreenFragment());
            forwardButton.setEnabled(false);
            new CountDownTimer(500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    forwardButton.setEnabled(true);
                }
            }.start();
        });

        postsButton.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), FeedActivity.class);
            i.putExtra("TYPE", "POST");
            getActivity().startActivity(i);

            postsButton.setEnabled(false);
            new CountDownTimer(500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    postsButton.setEnabled(true);
                }
            }.start();
        });

        likesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), FeedActivity.class);
                i.putExtra("TYPE", "LIKES");
                getActivity().startActivity(i);

                likesButton.setEnabled(false);
                new CountDownTimer(500, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        likesButton.setEnabled(true);
                    }
                }.start();
            }
        });

        eventsButton.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), EventsActivity.class);
            getActivity().startActivity(i);
            eventsButton.setEnabled(false);
            new CountDownTimer(500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    eventsButton.setEnabled(true);
                }
            }.start();
        });
        partnersButton.setOnClickListener(v -> {
            ((MainActivity) getActivity()).draw(new PartnersFragment());

            partnersButton.setEnabled(false);
            new CountDownTimer(500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    partnersButton.setEnabled(true);
                }
            }.start();
        });

        if (!(sharedPreferences.getString("city", "").equals("") ||
                sharedPreferences.getString("country", "").isEmpty())) {
            userLocation.setText(sharedPreferences.getString("city", "") + ", " +
                    sharedPreferences.getString("country", ""));
        } else {
            locationLayout.setVisibility(View.GONE);
        }
        if (!(sharedPreferences.getString("training_hours", "").isEmpty())) {
            preferredTime.setText(sharedPreferences.getString("training_hours", ""));
        } else {
            timeLayout.setVisibility(View.GONE);
        }
        if (!(sharedPreferences.getString("training_place", "").isEmpty())) {
            preferredGym.setText(sharedPreferences.getString("training_place", ""));
        } else {
            gymLayout.setVisibility(View.GONE);
        }

        return view;
    }

    private void openGallery() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        } else {
            Intent gallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(getActivity(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri imageUri = data.getData();
            profileImage.setImageURI(imageUri);
            uploadFile(imageUri);
        }
    }

    void uploadFile(Uri fileUri) {
        String path = ImageFilePath.getPath(getActivity(), fileUri);

        try {
            File file = Util.getCompressed(getActivity(), path , 400 , 400);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("avatar", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
            Call<HashMap> call = MainActivity.APIBuild().upload(filePart, "Bearer " +
                    getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));
            call.enqueue(new Callback<HashMap>() {
                @Override
                public void onResponse(Call<HashMap> call, Response<HashMap> response) {
                }

                @Override
                public void onFailure(Call<HashMap> call, Throwable t) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
