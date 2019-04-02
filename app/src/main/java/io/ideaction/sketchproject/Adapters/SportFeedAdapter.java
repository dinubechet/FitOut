package io.ideaction.sketchproject.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import com.khizar1556.mkvideoplayer.MKPlayerActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.ideaction.sketchproject.Activities.FeedActivity;
import io.ideaction.sketchproject.Activities.ForeignProfileActivity;
import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.Activities.MediaPlayerAc;
import io.ideaction.sketchproject.Activities.SportActivity;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.CustomCallBack;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.Util;
import io.ideaction.sketchproject.MainScreenFragments.CreateCommentFragment;
import io.ideaction.sketchproject.MainScreenFragments.CreatePostFragment;
import io.ideaction.sketchproject.MainScreenFragments.ViewAllCommentsFragment;
import io.ideaction.sketchproject.Models.BannerModer;
import io.ideaction.sketchproject.Models.Image;
import io.ideaction.sketchproject.Models.SportFeedModel;
import io.ideaction.sketchproject.Models.User;
import io.ideaction.sketchproject.R;
import io.ideaction.sketchproject.ReportDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SportFeedAdapter extends BaseAdapter {


    private Context context;
    private boolean readyToCall = true;
    private int page = 1;

    private List<SportFeedModel> sportFeedList = new ArrayList<>();

    String[] items = new String[]{
            "Edit" , "Delete" , "Cancel"
    };
    String[] items2 = new String[]{
            "Report" , "Block" , "Cancel"
    };
    ArrayAdapter<String> spinnerAdapter;
    ArrayAdapter<String> spinnerAdapter2;


    public void setSportFeedList(List<SportFeedModel> sportFeedList, int page) {
        if (page == 1) {
            this.sportFeedList = sportFeedList;
            this.page = 1;
            notifyDataSetChanged();
        } else {
            this.sportFeedList.addAll(sportFeedList);
            readyToCall = !sportFeedList.isEmpty();
            notifyDataSetChanged();
        }
    }

    public void setSportFeedListClear(List<SportFeedModel> sportFeedList) {
        this.sportFeedList = sportFeedList;
        notifyDataSetChanged();
        page = 1;
    }

    public SportFeedAdapter(Context context) {
        this.context = context;
        spinnerAdapter =new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, items);
        spinnerAdapter2 =new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, items2);

    }

    @Override
    public int getCount() {
        return sportFeedList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.sport_feed_cell, parent, false);
        CircleImageView profileImage = rowView.findViewById(R.id.profile_image_sportfeed);
        TextView name = rowView.findViewById(R.id.name_sport_feed);
        TextView timePassed = rowView.findViewById(R.id.time_passed);
        Spinner deletePostButton = rowView.findViewById(R.id.delete_post_button);
        final TextView numberOfLikes = rowView.findViewById(R.id.number_of_likes);
        final ImageView heartIcon = rowView.findViewById(R.id.heart);
        TextView contentText = rowView.findViewById(R.id.text_content);
        final LinearLayout likeButton = rowView.findViewById(R.id.press_on_like_button);
        TextView comment = rowView.findViewById(R.id.comment);
        TextView addComment = rowView.findViewById(R.id.add_comment);
        TextView commentsCount = rowView.findViewById(R.id.comments_count);
        TextView addNewComment = rowView.findViewById(R.id.add_new_comment);
        TextView viewAll = rowView.findViewById(R.id.view_all);
        TextView noComments = rowView.findViewById(R.id.no_coments);
        LinearLayout shareButton = rowView.findViewById(R.id.press_on_share_button);

        commentsCount.setText("Comments(" + sportFeedList.get(position).getCommentsCount() + ")");
        if (sportFeedList.get(position).getLastComment() != null) {
            addNewComment.setVisibility(GONE);
            noComments.setVisibility(GONE);
            viewAll.setVisibility(VISIBLE);
            commentsCount.setVisibility(VISIBLE);
            addComment.setVisibility(VISIBLE);
            comment.setVisibility(VISIBLE);
            comment.setText(Html.fromHtml("<b>" + sportFeedList.get(position).getLastComment().getUser().getName()
                    + "</b>" + " " + sportFeedList.get(position).getLastComment().getMessage()));
        } else {
            addNewComment.setVisibility(VISIBLE);
            viewAll.setVisibility(View.GONE);
            commentsCount.setVisibility(GONE);
            addComment.setVisibility(GONE);
            comment.setVisibility(GONE);
            noComments.setVisibility(VISIBLE);
        }

        if (sportFeedList.get(position).getUser().getEmail().equals
                (context.getSharedPreferences("my", MODE_PRIVATE)
                        .getString("email" , ""))){

            deletePostButton.setAdapter(spinnerAdapter);
            deletePostButton.setVisibility(VISIBLE);
            deletePostButton.setSelection(2,false);

            deletePostButton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int positionx, long id) {
                    if (positionx == 1){
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(context);
                        }
                        builder.setTitle("Delete post")
                                .setMessage("Are you sure you want to delete this post?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Call<SportFeedModel> call = MainActivity.APIBuild().deletePost(sportFeedList.get(position).getId(), "Bearer " +
                                                context.getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
                                        call.enqueue(new CustomCallBack<SportFeedModel>(context){
                                            @Override
                                            public void onResponse(Call<SportFeedModel> call, Response<SportFeedModel> response) {
                                                super.onResponse(call , response);
                                                if (response.isSuccessful()) {
                                                    sportFeedList.remove(position);
                                                    notifyDataSetChanged();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<SportFeedModel> call, Throwable t) {
                                                super.onFailure(call , t);
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        deletePostButton.setSelection(2,false);

                    }

                    if (positionx == 0){

                        if (context instanceof SportActivity) {
                            ((SportActivity) context).enterFromRight(new CreatePostFragment(sportFeedList.get(position)));
                        }
                        else if (context instanceof FeedActivity) {
                            ((FeedActivity) context).enterFromRight(new CreatePostFragment(sportFeedList.get(position)));
                        }
                        deletePostButton.setSelection(2,false);


                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            } );


        }else{
            deletePostButton.setVisibility(VISIBLE);
            deletePostButton.setAdapter(spinnerAdapter2);
            deletePostButton.setSelection(2,false);
            //TODO implement apis

            deletePostButton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int positionx, long id) {
                    if (positionx == 1){
                        //Block content

                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(context);
                        }
                        builder.setTitle("Block content")
                                .setMessage("Are you sure you want to block this post?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                            //block content api

                                        sportFeedList.remove(position);
                                        notifyDataSetChanged();

                                    }
                                })
                                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        deletePostButton.setSelection(2,false);

                    }

                    if (positionx == 0){
                        if (context instanceof SportActivity) {
                            ReportDialog dialog = new ReportDialog("content" , sportFeedList.get(position).getId() + "");
                            dialog.show(((SportActivity) context).getSupportFragmentManager(), "report");
                        }
                        else if (context instanceof FeedActivity) {
                            ReportDialog dialog = new ReportDialog("content" , sportFeedList.get(position).getId() + "");
                            dialog.show(((FeedActivity) context).getSupportFragmentManager(), "report");
                        }

                        deletePostButton.setSelection(2,false);

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            } );


        }

        setupSportFeedAdapter(position, profileImage, name, timePassed, numberOfLikes, heartIcon, rowView);

        addComment.setOnClickListener(v -> {
            if (context instanceof SportActivity)
                ((SportActivity) context).enterFromRight(new CreateCommentFragment(sportFeedList.get(position).getId()));
            else if (context instanceof FeedActivity)
                ((FeedActivity) context).enterFromRight(new CreateCommentFragment(sportFeedList.get(position).getId()));
        });

        addNewComment.setOnClickListener(v -> {
            if (context instanceof SportActivity)
                ((SportActivity) context).enterFromRight(new CreateCommentFragment(sportFeedList.get(position).getId()));
            else if (context instanceof FeedActivity)
                ((FeedActivity) context).enterFromRight(new CreateCommentFragment(sportFeedList.get(position).getId()));
        });

        likeButton.setOnClickListener(v -> {
            setupLikeButton(position, heartIcon, numberOfLikes);
            likeButton.setEnabled(false);
            new CountDownTimer(500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    likeButton.setEnabled(true);
                }
            }.start();
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<User> call = MainActivity.APIBuild().getPerson(sportFeedList.get(position).getUser().getId() + "", "Bearer " +
                        context.getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
                call.enqueue(new CustomCallBack<User>(context) {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        super.onResponse(call, response);
                        if (response.isSuccessful()) {
                            Intent i = new Intent(context, ForeignProfileActivity.class);
                            i.putExtra("TYPE", "FOREIGN EVENTS");
                            i.putExtra("user", response.body());
                            context.startActivity(i);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        super.onFailure(call, t);
                    }
                });
            }
        });

        viewAll.setOnClickListener(v -> {
            if (context instanceof SportActivity)
                ((SportActivity) context).enterFromRight(new ViewAllCommentsFragment(sportFeedList.get(position).getId()));
            else if (context instanceof FeedActivity)
                ((FeedActivity) context).enterFromRight(new ViewAllCommentsFragment(sportFeedList.get(position).getId()));
        });

        shareButton.setOnClickListener(v -> {
            if (sportFeedList.get(position).getImages().isEmpty()) {
                share(sportFeedList.get(position).getText());
                return;
            }
            Picasso.get().load(sportFeedList.get(position).getImages().get(0).getPath()).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    SharePhoto photo = new SharePhoto.Builder().setBitmap(bitmap).build();
                    SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
                    new ShareDialog((Activity) context).show(content);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            });


        });
        contentText.setText(sportFeedList.get(position).getText());

        return rowView;
    }

    private void share(String message) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);
        context.startActivity(Intent.createChooser(share, "Share on"));
    }

    private void setupSportFeedAdapter(final int position, CircleImageView profileImage, TextView name,
                                       TextView timePassed, TextView numberOfLikes, ImageView heartIcon, View feedView) {
        name.setText(sportFeedList.get(position).getUser().getName());
        if (sportFeedList.get(position).getUser().getAvatar().isEmpty()) {
            Picasso.get().load(R.drawable.default_profile).into(profileImage);
        } else {
            Picasso.get().load(sportFeedList.get(position).getUser().getAvatar()).placeholder(R.drawable.default_profile).into(profileImage);
        }
        timePassed.setText(sportFeedList.get(position).getCreatedAt());
        numberOfLikes.setText(sportFeedList.get(position).getLikes().toString());
        ImageView postImg = feedView.findViewById(R.id.postImg);
        GridView gridView = feedView.findViewById(R.id.gridView);

        RelativeLayout videoFrame = feedView.findViewById(R.id.videoFrame);
        ImageView videoimg = feedView.findViewById(R.id.videoimg);


        postImg.setAdjustViewBounds(true);
        postImg.setOnClickListener(v -> {
            ArrayList<String> list = new ArrayList<>();
            for (Image x : sportFeedList.get(position).getImages())
                list.add(x.getPath());

            new ImageViewer.Builder(context, list)
                    .setStartPosition(0)
                    .show();
        });

        videoFrame.setVisibility(GONE);

        if (!sportFeedList.get(position).getImages().isEmpty()) {
            if (sportFeedList.get(position).getImages().size() == 1 && sportFeedList.get(position).getVideo().isEmpty()) {
                postImg.setVisibility(VISIBLE);
                gridView.setVisibility(View.GONE);
                Picasso.get().load(sportFeedList.get(position).getImages().get(0).getPathThumb()).placeholder(R.drawable.default_image).into(postImg);
            } else {

                postImg.setVisibility(GONE);
                gridView.setVisibility(View.VISIBLE);
                List<BannerModer> list = new ArrayList<>();
                for (Image x : sportFeedList.get(position)
                        .getImages())
                    list.add(new BannerModer(x.getPath(), false, x.getPathThumb()));

                if (sportFeedList.get(position).getVideo().size() > 0)
                    list.add(new BannerModer(sportFeedList.get(position).getVideo().get(0).getPath(), true, sportFeedList.get(position).getVideo().get(0).getThumb()));

                gridView.setAdapter(new GridImgAdapter(list, context));
            }

        } else {

            if (sportFeedList.get(position).getVideo().size() > 0) {
                    videoFrame.setVisibility(VISIBLE);
                    gridView.setVisibility(GONE);
                postImg.setVisibility(GONE);

                Picasso.get().load(sportFeedList.get(position).getVideo().get(0).getThumb()).placeholder(R.drawable.default_image).into(videoimg);
                    videoFrame.setOnClickListener(v ->
                            {

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent inten = new Intent(context, MediaPlayerAc.class);
                                        inten.putExtra("path", sportFeedList.get(position).getVideo().get(0).getPath());
                                        context.startActivity(inten);
                                    }
                                }).start();


                            }

                    );
            } else {
                gridView.setVisibility(View.GONE);
                postImg.setVisibility(GONE);
            }
        }

        if (sportFeedList.get(position).getLiked()) {
            Picasso.get().load(R.drawable.feed_heart_filled).into(heartIcon);
        } else {
            Picasso.get().load(R.drawable.feed_heart_empty).into(heartIcon);
        }

        if (position == sportFeedList.size() - 1 && readyToCall) {
            page++;
            if (context instanceof SportActivity)
                ((SportActivity) context).requestSportFeedPage(page, SportActivity.getFeedCategory());
            else if (context instanceof FeedActivity)
                ((FeedActivity) context).requestPage(page);
        }
    }

    private void setupLikeButton(int position, ImageView heartIcon, TextView numberOfLikes) {
        int x = sportFeedList.get(position).getLikes();
        if (sportFeedList.get(position).getLiked()) {
            sportFeedList.get(position).setLiked(false);
            Picasso.get().load(R.drawable.feed_heart_empty).into(heartIcon);
            x--;
            sportFeedList.get(position).setLikes(x);
        } else {
            sportFeedList.get(position).setLiked(true);
            Picasso.get().load(R.drawable.feed_heart_filled).into(heartIcon);
            x++;
            sportFeedList.get(position).setLikes(x);
        }
        numberOfLikes.setText(sportFeedList.get(position).getLikes().toString());

        Call<HashMap> call = MainActivity.APIBuild().likeUnlike(sportFeedList.get(position).getId().toString(), "Bearer " +
                context.getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
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