package io.ideaction.sketchproject.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.khizar1556.mkvideoplayer.MKPlayerActivity;
import com.pixelcan.inkpageindicator.InkPageIndicator;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.ideaction.sketchproject.Adapters.BannerAdapter;
import io.ideaction.sketchproject.Adapters.GridImgAdapter;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.CustomCallBack;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.Util;
import io.ideaction.sketchproject.MainScreenFragments.CreateCommentFragment;
import io.ideaction.sketchproject.MainScreenFragments.ViewAllCommentsFragment;
import io.ideaction.sketchproject.Models.BannerModer;
import io.ideaction.sketchproject.Models.EventModel;
import io.ideaction.sketchproject.Models.Image;
import io.ideaction.sketchproject.Models.SportFeedModel;
import io.ideaction.sketchproject.Models.User;
import io.ideaction.sketchproject.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SingleNotificationActivity extends AppCompatActivity {

    Button backArrow;

    RelativeLayout postView;
    RelativeLayout eventView;
    RelativeLayout attendButton;
    RelativeLayout saveButton;

    ImageView postImg;
    ImageView profileImagePost;
    ImageView profileImageEvent;
    ImageView heart;
    ImageView profileImageGoing1;
    ImageView profileImageGoing2;
    ImageView profileImageGoing3;
    ImageView profileImageGoing4;
    ImageView attendImageView;
    ImageView saveImage;

    LinearLayout linearWithGoing;
    LinearLayout likeBtn;
    LinearLayout shareBtn;
    LinearLayout going1St;
    LinearLayout going2nd;
    LinearLayout going3rd;
    LinearLayout going4th;
    LinearLayout goingOthers;

    TextView textContentPost;
    TextView timePassed;
    TextView nameFeed;
    TextView numberOfLikes;
    TextView commentsCount;
    TextView noComments;
    TextView viewAll;
    TextView addNewComment;
    TextView comment;
    TextView addComment;
    TextView nameOfEventOwner;
    TextView timeLeftEvent;
    TextView eventTitle;
    TextView timeAtEvent;
    TextView placeAtEvent;
    TextView textContentEvent;
    TextView noOneGoing;
    TextView goingOthersNumber;
    TextView attendTextView;
    TextView saveTextView;

    SportFeedModel post;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_notification);
        setParameters();
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent().getStringExtra("TYPE").equals("Post")) {

            if (getIntent().getSerializableExtra("post") instanceof String) {
                setPostField(new Gson().fromJson(getIntent().getStringExtra("post"), SportFeedModel.class));
            } else {
                setPostField((SportFeedModel) getIntent().getSerializableExtra("post"));
            }

        } else if (getIntent().getStringExtra("TYPE").equals("Event")) {

            if (getIntent().getSerializableExtra("event") instanceof String) {
                setEventFields(new Gson().fromJson(getIntent().getStringExtra("event"), EventModel.class));
            } else {
                setEventFields((EventModel) getIntent().getSerializableExtra("event"));
            }

        }
    }

    private void setPostField(final SportFeedModel sportFeedModel) {
        post = (SportFeedModel) getIntent().getSerializableExtra("post");
        postView.setVisibility(VISIBLE);
        eventView.setVisibility(GONE);
        commentsCount.setText("Comments(" + sportFeedModel.getCommentsCount() + ")");
        if (sportFeedModel.getLastComment() != null) {
            addNewComment.setVisibility(GONE);
            noComments.setVisibility(GONE);
            viewAll.setVisibility(VISIBLE);
            commentsCount.setVisibility(VISIBLE);
            addComment.setVisibility(VISIBLE);
            comment.setVisibility(VISIBLE);
            comment.setText(Html.fromHtml("<b>" + sportFeedModel.getLastComment().getUser().getName()
                    + "</b>" + " " + sportFeedModel.getLastComment().getMessage()));
        } else {
            addNewComment.setVisibility(VISIBLE);
            viewAll.setVisibility(View.GONE);
            commentsCount.setVisibility(GONE);
            addComment.setVisibility(GONE);
            comment.setVisibility(GONE);
            noComments.setVisibility(VISIBLE);
        }

        profileImagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<User> call = MainActivity.APIBuild().getPerson(sportFeedModel.getUser().getId() + "", "Bearer " +
                        SingleNotificationActivity.this.getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));
                call.enqueue(new CustomCallBack<User>(SingleNotificationActivity.this) {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        super.onResponse(call, response);
                        if (response.isSuccessful()) {
                            Intent i = new Intent(SingleNotificationActivity.this, ForeignProfileActivity.class);
                            i.putExtra("TYPE", "FOREIGN EVENTS");
                            i.putExtra("user", response.body());
                            SingleNotificationActivity.this.startActivity(i);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        super.onFailure(call, t);
                    }
                });
            }
        });

        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleNotificationActivity.this.draw(new ViewAllCommentsFragment(sportFeedModel.getId()));
            }
        });

        textContentPost.setText(sportFeedModel.getText());

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleNotificationActivity.this.draw(new CreateCommentFragment(sportFeedModel.getId()));
            }
        });

        addNewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleNotificationActivity.this.draw(new CreateCommentFragment(sportFeedModel.getId()));
            }
        });


        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupLikeButton(heart, numberOfLikes);
                likeBtn.setEnabled(false);
                new CountDownTimer(500, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        likeBtn.setEnabled(true);
                    }
                }.start();
            }
        });

        nameFeed.setText(sportFeedModel.getUser().getName());
        if (sportFeedModel.getUser().getAvatar().isEmpty()) {
            Picasso.get().load(R.drawable.default_profile).into(profileImagePost);
        } else {
            Picasso.get().load(sportFeedModel.getUser().getAvatar()).placeholder(R.drawable.default_profile).into(profileImagePost);
        }
        timePassed.setText(sportFeedModel.getCreatedAt());
        numberOfLikes.setText(sportFeedModel.getLikes().toString());

        ImageView postImg = findViewById(R.id.postImg);
        GridView gridView = findViewById(R.id.gridView);

        postImg.setAdjustViewBounds(true);
        postImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list = new ArrayList<>();
                for (Image x : sportFeedModel.getImages())
                    list.add(x.getPath());

                new ImageViewer.Builder(SingleNotificationActivity.this, list)
                        .setStartPosition(0)
                        .show();
            }
        });

        RelativeLayout videoFrame = findViewById(R.id.videoFrame);
        ImageView videoimg = findViewById(R.id.videoimg);
        videoFrame.setVisibility(GONE);

        if (!sportFeedModel.getImages().isEmpty()) {
            if (sportFeedModel.getImages().size() == 1 && sportFeedModel.getVideo().isEmpty()) {
                postImg.setVisibility(VISIBLE);
                gridView.setVisibility(View.GONE);
                Picasso.get().load(sportFeedModel.getImages().get(0).getPath()).transform(Util.cropPosterTransformation).placeholder(R.drawable.default_image).into(postImg);
            } else {

                postImg.setVisibility(GONE);
                gridView.setVisibility(View.VISIBLE);
                List<BannerModer> list = new ArrayList<>();
                for (Image x : sportFeedModel
                        .getImages())
                    list.add(new BannerModer(x.getPath(), false, ""));

                if (sportFeedModel.getVideo().size() > 0)
                    list.add(new BannerModer(sportFeedModel.getVideo().get(0).getPath(), true, sportFeedModel.getVideo().get(0).getThumb()));

                gridView.setAdapter(new GridImgAdapter(list, SingleNotificationActivity.this));
            }

        } else {

            if (sportFeedModel.getVideo().size() > 0) {
                videoFrame.setVisibility(VISIBLE);
                gridView.setVisibility(GONE);
                postImg.setVisibility(GONE);

                Picasso.get().load(sportFeedModel.getVideo().get(0).getThumb()).placeholder(R.drawable.default_image).into(videoimg);
                videoFrame.setOnClickListener(v -> MKPlayerActivity.configPlayer(this).play(sportFeedModel.getVideo().get(0).getPath()));
            } else {
                gridView.setVisibility(View.GONE);
                postImg.setVisibility(GONE);
            }
        }

        if (sportFeedModel.getLiked()) {
            Picasso.get().load(R.drawable.feed_heart_filled).into(heart);
        } else {
            Picasso.get().load(R.drawable.feed_heart_empty).into(heart);
        }
    }

    private void setEventFields(final EventModel eventModel) {
        postView.setVisibility(GONE);
        eventView.setVisibility(VISIBLE);
        if (Integer.parseInt(eventModel.getCountAttendedUsers()) > 4) {
            goingOthers.setVisibility(View.VISIBLE);
            goingOthersNumber.setText("+" + (Integer.parseInt(eventModel.getCountAttendedUsers()) - 4));
        }

        if (eventModel.getUser().getAvatar().isEmpty()) {
            Picasso.get().load(R.drawable.default_profile).into(profileImageEvent);
        } else {
            Picasso.get().load(eventModel.getUser().getAvatar()).placeholder(R.drawable.default_profile).into(profileImageEvent);
        }

        nameOfEventOwner.setText(eventModel.getUser().getName());
        timeLeftEvent.setText(eventModel.getCreated_at_forHumans());
        timeAtEvent.setText(eventModel.getWhen());
        placeAtEvent.setText(eventModel.getAddress());
        textContentEvent.setText(eventModel.getDescription());
        eventTitle.setText(eventModel.getName());

        if (eventModel.getAttend()) {
            Picasso.get().load(R.drawable.attend2).into(attendImageView);
        } else {
            Picasso.get().load(R.drawable.attend).into(attendImageView);
        }
        if (eventModel.getSaved()) {
            Picasso.get().load(R.drawable.save2).into(saveImage);
            saveTextView.setText("Saved");
        } else {
            Picasso.get().load(R.drawable.save).into(saveImage);
            saveTextView.setText("Save");
        }
        attendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attendButtonFunctionality(attendImageView, eventModel);
                attendButton.setEnabled(false);
                new CountDownTimer(500, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        attendButton.setEnabled(true);
                    }
                }.start();
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveButtonFunctionality(saveImage, saveTextView, eventModel);
                saveButton.setEnabled(false);
                new CountDownTimer(500, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        saveButton.setEnabled(true);
                    }
                }.start();
            }
        });

        profileImageEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<User> call = MainActivity.APIBuild().getPerson(eventModel.getUser().getId() + "", "Bearer " +
                        SingleNotificationActivity.this.getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));
                call.enqueue(new CustomCallBack<User>(SingleNotificationActivity.this) {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        super.onResponse(call, response);
                        if (response.isSuccessful()) {
                            Intent i = new Intent(SingleNotificationActivity.this, ForeignProfileActivity.class);
                            i.putExtra("TYPE", "FOREIGN EVENTS");
                            i.putExtra("user", response.body());
                            SingleNotificationActivity.this.startActivity(i);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        super.onFailure(call, t);
                    }
                });
            }
        });

        switch (eventModel.getAttendedUsers().size()) {
            case 0:
                noOneGoing.setVisibility(View.VISIBLE);
                linearWithGoing.setVisibility(GONE);
                break;

            case 1:
                going1St.setVisibility(View.VISIBLE);
                if (eventModel.getAttendedUsers().get(0).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing1);
                } else {
                    Picasso.get().load(eventModel.getAttendedUsers().get(0).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing1);
                }
                break;

            case 2:
                going1St.setVisibility(View.VISIBLE);
                going2nd.setVisibility(View.VISIBLE);
                if (eventModel.getAttendedUsers().get(0).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing1);
                } else {
                    Picasso.get().load(eventModel.getAttendedUsers().get(0).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing1);
                }
                if (eventModel.getAttendedUsers().get(1).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing2);
                } else {
                    Picasso.get().load(eventModel.getAttendedUsers().get(1).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing2);
                }
                break;

            case 3:
                going1St.setVisibility(View.VISIBLE);
                going2nd.setVisibility(View.VISIBLE);
                going3rd.setVisibility(View.VISIBLE);
                if (eventModel.getAttendedUsers().get(0).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing1);
                } else {
                    Picasso.get().load(eventModel.getAttendedUsers().get(0).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing1);
                }
                if (eventModel.getAttendedUsers().get(1).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing2);
                } else {
                    Picasso.get().load(eventModel.getAttendedUsers().get(1).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing2);
                }
                if (eventModel.getAttendedUsers().get(2).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing3);
                } else {
                    Picasso.get().load(eventModel.getAttendedUsers().get(2).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing3);
                }
                break;

            default:
                going1St.setVisibility(View.VISIBLE);
                going2nd.setVisibility(View.VISIBLE);
                going3rd.setVisibility(View.VISIBLE);
                going4th.setVisibility(View.VISIBLE);
                if (eventModel.getAttendedUsers().get(0).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing1);
                } else {
                    Picasso.get().load(eventModel.getAttendedUsers().get(0).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing1);
                }
                if (eventModel.getAttendedUsers().get(1).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing2);
                } else {
                    Picasso.get().load(eventModel.getAttendedUsers().get(1).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing2);
                }
                if (eventModel.getAttendedUsers().get(2).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing3);
                } else {
                    Picasso.get().load(eventModel.getAttendedUsers().get(2).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing3);
                }
                if (eventModel.getAttendedUsers().get(3).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing4);
                } else {
                    Picasso.get().load(eventModel.getAttendedUsers().get(3).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing4);
                }
                break;
        }
    }

    private void attendButtonFunctionality(ImageView attendPicture, EventModel event) {
        Call<HashMap> call = MainActivity.APIBuild().attentUnattend(event.getId(), "Bearer " +
                this.getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));

        call.enqueue(new Callback<HashMap>() {
            @Override
            public void onResponse(Call<HashMap> call, Response<HashMap> response) {
            }

            @Override
            public void onFailure(Call<HashMap> call, Throwable t) {
            }
        });
        if (event.getAttend()) {
            Picasso.get().load(R.drawable.attend).into(attendPicture);
            event.setAttend(false);
        } else {
            Picasso.get().load(R.drawable.attend2).into(attendPicture);
            event.setAttend(true);
        }
    }

    private void saveButtonFunctionality(ImageView savePicture, TextView saveTextView, EventModel event) {
        Call<HashMap> call = MainActivity.APIBuild().saveUnsave(event.getId(), "Bearer " +
                this.getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));

        call.enqueue(new Callback<HashMap>() {
            @Override
            public void onResponse(Call<HashMap> call, Response<HashMap> response) {
            }

            @Override
            public void onFailure(Call<HashMap> call, Throwable t) {
            }
        });
        if (event.getSaved()) {
            Picasso.get().load(R.drawable.save).into(savePicture);
            saveTextView.setText("Save");
            event.setSaved(false);
        } else {
            Picasso.get().load(R.drawable.save2).into(savePicture);
            saveTextView.setText("Unsave");
            event.setSaved(true);
        }

    }

    private void setupLikeButton(ImageView heartIcon, TextView numberOfLikes) {
        int x = post.getLikes();
        if (post.getLiked()) {
            post.setLiked(false);
            Picasso.get().load(R.drawable.feed_heart_empty).into(heartIcon);
            x--;
            post.setLikes(x);
        } else {
            post.setLiked(true);
            Picasso.get().load(R.drawable.feed_heart_filled).into(heartIcon);
            x++;
            post.setLikes(x);
        }
        numberOfLikes.setText(post.getLikes().toString());

        Call<HashMap> call = MainActivity.APIBuild().likeUnlike(post.getId().toString(), "Bearer " +
                this.getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));
        call.enqueue(new Callback<HashMap>() {
            @Override
            public void onResponse(Call<HashMap> call, Response<HashMap> response) {
            }

            @Override
            public void onFailure(Call<HashMap> call, Throwable t) {
            }
        });
    }

    private void setParameters() {
        backArrow = findViewById(R.id.back_arrow);
        postView = findViewById(R.id.postView);
        profileImagePost = findViewById(R.id.profile_image_sportfeed);
        nameFeed = findViewById(R.id.name_sport_feed);
        timePassed = findViewById(R.id.time_passed);
        textContentPost = findViewById(R.id.text_content);
        postImg = findViewById(R.id.postImg);
        likeBtn = findViewById(R.id.press_on_like_button);
        heart = findViewById(R.id.heart);
        numberOfLikes = findViewById(R.id.number_of_likes);
        shareBtn = findViewById(R.id.press_on_share_button);
        commentsCount = findViewById(R.id.comments_count);
        noComments = findViewById(R.id.no_coments);
        viewAll = findViewById(R.id.view_all);
        addNewComment = findViewById(R.id.add_new_comment);
        comment = findViewById(R.id.comment);
        addComment = findViewById(R.id.add_comment);
        eventView = findViewById(R.id.eventView);
        profileImageEvent = findViewById(R.id.profile_image_event);
        nameOfEventOwner = findViewById(R.id.name_of_event_owner);
        timeLeftEvent = findViewById(R.id.time_left_event);
        eventTitle = findViewById(R.id.title_event);
        timeAtEvent = findViewById(R.id.time_at_event);
        placeAtEvent = findViewById(R.id.place_at_event);
        textContentEvent = findViewById(R.id.content_text_event);
        noOneGoing = findViewById(R.id.no_one_going);
        linearWithGoing = findViewById(R.id.linear_with_going);
        going1St = findViewById(R.id.going_1);
        going2nd = findViewById(R.id.going_2);
        going3rd = findViewById(R.id.going_3);
        going4th = findViewById(R.id.going_4);
        goingOthers = findViewById(R.id.going_others);
        profileImageGoing1 = findViewById(R.id.profile_image_going_1);
        profileImageGoing2 = findViewById(R.id.profile_image_going_2);
        profileImageGoing3 = findViewById(R.id.profile_image_going_3);
        profileImageGoing4 = findViewById(R.id.profile_image_going_4);
        goingOthersNumber = findViewById(R.id.going_others_number);
        attendButton = findViewById(R.id.attend_button);
        saveButton = findViewById(R.id.save_button);
        attendImageView = findViewById(R.id.attend_image_view);
        attendTextView = findViewById(R.id.attend_text_view);
        saveTextView = findViewById(R.id.save_text_on_button);
        saveImage = findViewById(R.id.save_image);
        sharedPreferences = getSharedPreferences("my", Context.MODE_PRIVATE);
    }

    public void draw(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.tutorial, fragment);
        fragmentTransaction.commit();
    }
}