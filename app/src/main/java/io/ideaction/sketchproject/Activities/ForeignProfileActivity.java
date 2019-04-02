package io.ideaction.sketchproject.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.ideaction.sketchproject.MainScreenFragments.FollowersFollowingFragment;
import io.ideaction.sketchproject.MainScreenFragments.ForeignPartnersFragment;
import io.ideaction.sketchproject.Models.User;
import io.ideaction.sketchproject.R;
import io.ideaction.sketchproject.ReportDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForeignProfileActivity extends AppCompatActivity {

    Button backArrow;
    CircleImageView profileImage;
    Button followButton;
    TextView profileName;
    TextView atSignProfileName;
    TextView userLocation;
    TextView preferredTime;
    TextView preferredGym;
    TextView followers;
    TextView following;
    TextView motto;
    Button partnerUpButton;
    Button messageButton;
    RelativeLayout posts;
    RelativeLayout likes;
    RelativeLayout events;
    RelativeLayout partners;
    LinearLayout locationLayout;
    LinearLayout timeLayout;
    LinearLayout gymLayout;
    SharedPreferences sharedPreferences;
    User user;
    String[] items2 = new String[]{
            "Report" , "Block" , "Cancel"
    };
    ArrayAdapter<String> spinnerAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreign_profile);

        backArrow = findViewById(R.id.back_arrow_foreign);
        profileImage = findViewById(R.id.profile_image);
        followButton = findViewById(R.id.follow_button);
        profileName = findViewById(R.id.profile_name);
        atSignProfileName = findViewById(R.id.at_sign_profile_name);
        userLocation = findViewById(R.id.location_text);
        preferredTime = findViewById(R.id.time_text);
        preferredGym = findViewById(R.id.preferred_gym_text);
        locationLayout = findViewById(R.id.location_layout);
        timeLayout = findViewById(R.id.time_layout);
        gymLayout = findViewById(R.id.gym_layout);
        followers = findViewById(R.id.followers);
        following = findViewById(R.id.following);
        motto = findViewById(R.id.motto);
        partnerUpButton = findViewById(R.id.partner_up_button);
        messageButton = findViewById(R.id.message_button);
        posts = findViewById(R.id.posts);
        likes = findViewById(R.id.likes);
        events = findViewById(R.id.events);
        partners = findViewById(R.id.partners);
        if (getIntent().getSerializableExtra("user") instanceof String) {
           user =  new Gson().fromJson(getIntent().getStringExtra("user"), User.class);
        } else {
            user = (User) getIntent().getSerializableExtra("user");
        }
        sharedPreferences = getSharedPreferences("my", Context.MODE_PRIVATE);

        if (user.getEmail().equals(sharedPreferences.getString("email", ""))) {
            partnerUpButton.setVisibility(View.GONE);
            messageButton.setVisibility(View.GONE);
            followButton.setVisibility(View.GONE);
        }

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageButton.setEnabled(false);
                new CountDownTimer(500,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        messageButton.setEnabled(true);
                    }
                }.start();
                fetch(user);

            }
        });

        posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForeignProfileActivity.this, FeedActivity.class);
                i.putExtra("TYPE", "FOREIGN POST");
                i.putExtra("id", user.getId());
                ForeignProfileActivity.this.startActivity(i);
            }
        });

        likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForeignProfileActivity.this, FeedActivity.class);
                i.putExtra("TYPE", "FOREIGN LIKES");
                i.putExtra("id", user.getId());
                ForeignProfileActivity.this.startActivity(i);
            }
        });

        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForeignProfileActivity.this, ForeignEventsActivity.class);
                i.putExtra("TYPE", "FOREIGN EVENTS");
                i.putExtra("id", user.getId());
                ForeignProfileActivity.this.startActivity(i);
            }
        });

        if (user.getFriend()) {
            partnerUpButton.setText("End Partnership");
            partnerUpButton.setTextColor(Color.WHITE);
            partnerUpButton.setBackgroundResource(R.drawable.bg_round_fill_unfollow);
            partnerUpButton.setEnabled(true);
        } else if (user.isPendingRequest()){
            partnerUpButton.setText("Accept");
            partnerUpButton.setTextColor(Color.parseColor("#2CC9D8"));
            partnerUpButton.setBackgroundResource(R.drawable.bg_round_fill);
            partnerUpButton.setEnabled(true);
        }else if (user.isSentRequest()){
            partnerUpButton.setText("Request Sent");
            partnerUpButton.setTextColor(Color.WHITE);
            partnerUpButton.setBackgroundResource(R.drawable.bg_round_fill_unfollow);
            partnerUpButton.setEnabled(false);
        } else {
            partnerUpButton.setText("Partner Up!");
            partnerUpButton.setTextColor(Color.parseColor("#2CC9D8"));
            partnerUpButton.setBackgroundResource(R.drawable.bg_round_fill);
            partnerUpButton.setEnabled(true);
        }

        if (user.getAvatar().isEmpty()) {
            Picasso.get().load(R.drawable.default_profile).into(profileImage);
        } else {
            Picasso.get().load(user.getAvatar())
                    .placeholder(R.drawable.default_image).into(profileImage);
        }

        if (user.getFollowing()) {
            followButton.setText("Unfollow");
            followButton.setTextColor(Color.WHITE);
            followButton.setBackgroundResource(R.drawable.bg_round_fill_unfollow);
        } else {
            followButton.setText("Follow +");
            followButton.setTextColor(Color.parseColor("#2CC9D8"));
            followButton.setBackgroundResource(R.drawable.bg_round_fill);
        }

        profileName.setText(user.getName());

        atSignProfileName.setText("@" + user.getUsername());

        motto.setText(user.getDescription());

        followers.setText("Followers: " + user.getFollowersCount().replace(".0", "") + " | ");
        following.setText("Following: " + user.getFollowingCount().replace(".0", ""));


        if (!(user.getCity().isEmpty() || user.getCity().isEmpty())) {
            userLocation.setText(user.getCity() + ", " +
                    user.getCountry());
        } else {
            locationLayout.setVisibility(View.GONE);
        }
        if (!(user.getTrainingHours().isEmpty())) {
            preferredTime.setText(user.getTrainingHours());
        } else {
            timeLayout.setVisibility(View.GONE);
        }
        if (!(user.getTrainingPlace().isEmpty())) {
            preferredGym.setText(user.getTrainingPlace());
        } else {
            gymLayout.setVisibility(View.GONE);
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForeignProfileActivity.this.draw(new FollowersFollowingFragment(user.getId(), "followers", 1));
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForeignProfileActivity.this.draw(new FollowersFollowingFragment(user.getId(), "following", 1));
            }
        });

        partnerUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partnerUpButton.setEnabled(false);
                new CountDownTimer(500,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        if (user.getFriend()) {
                            user.setFriend(false);
                            partnerUpButton.setText("Partner Up!");
                            partnerUpButton.setTextColor(Color.parseColor("#2CC9D8"));
                            partnerUpButton.setBackgroundResource(R.drawable.bg_round_fill);
                            Call<HashMap> call = MainActivity.APIBuild().unFriend(user.getId(), "Bearer " +
                                    ForeignProfileActivity.this.getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));

                            call.enqueue(new Callback<HashMap>() {
                                @Override
                                public void onResponse(Call<HashMap> call, Response<HashMap> response) {

                                }

                                @Override
                                public void onFailure(Call<HashMap> call, Throwable t) {
                                }
                            });

                        } else if(user.isPendingRequest()){
                            user.setPendingRequest(false);
                            partnerUpButton.setText("End Partnership");
                            partnerUpButton.setTextColor(Color.WHITE);
                            partnerUpButton.setBackgroundResource(R.drawable.bg_round_fill_unfollow);
                            Call<HashMap> callNotifications = MainActivity.APIBuild().acceptFriendRequest(user.getId(), "Bearer " +
                                    ForeignProfileActivity.this.getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
                            callNotifications.enqueue(new Callback<HashMap>() {
                                @Override
                                public void onResponse(Call<HashMap> call, Response<HashMap> response) {

                                }

                                @Override
                                public void onFailure(Call<HashMap> call, Throwable t) {
                                    Toast.makeText(ForeignProfileActivity.this, "Your internet connection is bad or our server is down", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (!user.getFriend() && !user.isSentRequest()) {
                            user.setFriend(true);
                            user.setSentRequest(true);
                            partnerUpButton.setText("Request Sent");
                            partnerUpButton.setEnabled(false);
                            partnerUpButton.setTextColor(Color.WHITE);
                            partnerUpButton.setBackgroundResource(R.drawable.bg_round_fill_unfollow);
                            Call<HashMap> call = MainActivity.APIBuild().friendRequest(user.getId(), "Bearer " +
                                    ForeignProfileActivity.this.getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));

                            call.enqueue(new Callback<HashMap>() {
                                @Override
                                public void onResponse(Call<HashMap> call, Response<HashMap> response) {
                                }

                                @Override
                                public void onFailure(Call<HashMap> call, Throwable t) {
                                }
                            });

                        }
                    }
                }.start();

            }
        });

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followButton.setEnabled(false);
                new CountDownTimer(500,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        followButton.setEnabled(true);
                    }
                }.start();
                if (!user.getFollowing()) {
                    user.setFollowing(true);
                    followButton.setText("Unfollow");
                    followButton.setTextColor(Color.WHITE);
                    followButton.setBackgroundResource(R.drawable.bg_round_fill_unfollow);
                    Call<HashMap> call = MainActivity.APIBuild().follow(user.getId(), "Bearer " +
                            ForeignProfileActivity.this.getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));

                    call.enqueue(new Callback<HashMap>() {
                        @Override
                        public void onResponse(Call<HashMap> call, Response<HashMap> response) {
                        }

                        @Override
                        public void onFailure(Call<HashMap> call, Throwable t) {
                        }
                    });

                } else {
                    user.setFollowing(false);
                    followButton.setText("Follow +");
                    followButton.setTextColor(Color.parseColor("#2CC9D8"));
                    followButton.setBackgroundResource(R.drawable.bg_round_fill);
                    Call<HashMap> call = MainActivity.APIBuild().unFollow(user.getId(), "Bearer " +
                            ForeignProfileActivity.this.getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));

                    call.enqueue(new Callback<HashMap>() {
                        @Override
                        public void onResponse(Call<HashMap> call, Response<HashMap> response) {

                        }

                        @Override
                        public void onFailure(Call<HashMap> call, Throwable t) {
                        }
                    });
                }
            }
        });
        partners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                draw(new ForeignPartnersFragment(user.getId() + ""));
            }
        });

        spinnerAdapter2 =new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, items2);


        Spinner deletePostButton = findViewById(R.id.user_options);
        deletePostButton.setAdapter(spinnerAdapter2);
        deletePostButton.setSelection(2,false);
        //TODO implement apis

        deletePostButton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int positionx, long id) {
                if (positionx == 1){
                    //Block user

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(ForeignProfileActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(ForeignProfileActivity.this);
                    }
                    builder.setTitle("Block user")
                            .setMessage("Are you sure you want to block this user?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Call<Void> call = MainActivity.APIBuild().blockuser("Bearer " + sharedPreferences.getString("token" , "") , user.getId() + "");
                                    call.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {

                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {

                                        }
                                    });

                                  finish();

                                }
                            })
                            .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    deletePostButton.setSelection(2,false);

                }

                if (positionx == 0){
                    //Report user
                    ReportDialog dialog = new ReportDialog("user" , user.getId()+"");
                    dialog.show(getSupportFragmentManager(), "report");


                    deletePostButton.setSelection(2,false);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        } );



    }

    void fetch(final User user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap chats = (HashMap) dataSnapshot.getValue();
                for (final Object entry : chats.keySet()) {
                    HashMap chat = (HashMap) chats.get(entry);
                    if (chat.get("participants").toString().equals(sharedPreferences.getString("email", "xxx") + "," + user.getEmail())) {
                        Intent intent = new Intent(ForeignProfileActivity.this, ChatActivity.class);
                        intent.putExtra("key", chat.get("key").toString());
                        intent.putExtra("name", user.getName());
                        intent.putExtra("img", user.getAvatar());
                        intent.putExtra("em", user.getEmail());

                        ForeignProfileActivity.this.startActivity(intent);
                        return;
                    }

                    if (chat.get("participants").toString().equals(user.getEmail() + "," + sharedPreferences.getString("email", "xxx"))) {
                        Intent intent = new Intent(ForeignProfileActivity.this, ChatActivity.class);
                        intent.putExtra("key", chat.get("key").toString());
                        intent.putExtra("name", user.getName());
                        intent.putExtra("img", user.getAvatarThumb());
                        intent.putExtra("em", user.getEmail());

                        ForeignProfileActivity.this.startActivity(intent);
                        return;
                    }
                }
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference("message");

                HashMap hashMap = new HashMap();
                String mGroupId = myRef.push().getKey();
                hashMap.put("key", mGroupId);
                hashMap.put("lastMessage", "");
                hashMap.put("lastMessageTime", "");
                hashMap.put("participants", sharedPreferences.getString("email", "xxx") + "," + user.getEmail());
                if (user.getAvatarThumb().equals("")) {
                    if (user.getAvatar().equals(""))
                        hashMap.put("participantsImg", sharedPreferences.getString("img", "no") + ",no");
                    else
                        hashMap.put("participantsImg", sharedPreferences.getString("img", "no") + "," + user.getAvatar());
                } else
                    hashMap.put("participantsImg", sharedPreferences.getString("img", "no") + "," + user.getAvatarThumb());


                hashMap.put("participantsName", sharedPreferences.getString("name", "xxx") + "," + user.getName());
                myRef.child(mGroupId).setValue(hashMap);


                Intent intent = new Intent(ForeignProfileActivity.this, ChatActivity.class);
                intent.putExtra("key", mGroupId);
                intent.putExtra("name", user.getName());
                intent.putExtra("img", user.getAvatarThumb());
                intent.putExtra("em", user.getEmail());
                ForeignProfileActivity.this.startActivity(intent);

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


    }

    public void draw(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.tutorial, fragment);
        fragmentTransaction.commit();
    }
}