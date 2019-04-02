package io.ideaction.sketchproject.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.ideaction.sketchproject.Activities.ChatActivity;
import io.ideaction.sketchproject.Activities.ForeignProfileActivity;
import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.Activities.SportActivity;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.CustomCallBack;
import io.ideaction.sketchproject.Models.User;
import io.ideaction.sketchproject.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class FollowingFollowersAdapter extends BaseAdapter {

    private Context context;
    private boolean readyToCall = true;
    private int page = 1;
    private SharedPreferences sharedPreferences;

    private List<User> peopleList = new ArrayList<>();

    public void setPeopleFeedList(List<User> peopleList, int page) {
        if (page == 1) {
            this.peopleList = peopleList;
            notifyDataSetChanged();
            return;
        }
        this.peopleList.addAll(peopleList);
        readyToCall = !peopleList.isEmpty();
        notifyDataSetChanged();
    }

    public FollowingFollowersAdapter(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("my", Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return peopleList.size();
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
        View rowView = inflater.inflate(R.layout.people_feed_cell, parent, false);

        final CircleImageView profileImage = rowView.findViewById(R.id.profile_image);
        TextView name = rowView.findViewById(R.id.name_tv);
        TextView userName = rowView.findViewById(R.id.username_tv);
        TextView location = rowView.findViewById(R.id.location_tv);
        TextView description = rowView.findViewById(R.id.description_about);
        final Button partnershipButton = rowView.findViewById(R.id.partnership_button);
        final TextView followTxt = rowView.findViewById(R.id.follow_txt);
        final LinearLayout followButton = rowView.findViewById(R.id.follow_button);
        final LinearLayout messageButton = rowView.findViewById(R.id.message_button);
        final ImageView follow = rowView.findViewById(R.id.follow);
        LinearLayout userLocation = rowView.findViewById(R.id.location);

        name.setText(peopleList.get(position).getName());
        if (peopleList.get(position).getAvatar().isEmpty()) {
            Picasso.get().load(R.drawable.default_profile).into(profileImage);
        } else {
            Picasso.get().load(peopleList.get(position).getAvatar()).placeholder(R.drawable.default_profile).into(profileImage);
        }

        if (peopleList.get(position).getFriend()) {
            partnershipButton.setText("End Partnership");
            partnershipButton.setTextColor(Color.parseColor("#FFFFFF"));
            partnershipButton.setBackgroundResource(R.drawable.bg_round_end_partnership);
            partnershipButton.setEnabled(true);
        }else if (peopleList.get(position).isPendingRequest()){
            partnershipButton.setText("Accept");
            partnershipButton.setTextColor(Color.parseColor("#FFFFFF"));
            partnershipButton.setBackgroundResource(R.drawable.bg_round_end_partnership);
            partnershipButton.setEnabled(true);
        }else if (peopleList.get(position).isSentRequest()){
            partnershipButton.setText("Request Sent");
            partnershipButton.setTextColor(Color.parseColor("#FFFFFF"));
            partnershipButton.setBackgroundResource(R.drawable.bg_round_end_partnership);
            partnershipButton.setEnabled(false);
        }
        else{
            partnershipButton.setText("Partner Up!");
            partnershipButton.setTextColor(Color.parseColor("#FFFFFF"));
            partnershipButton.setBackgroundResource(R.drawable.bg_round_partner_up_blue);
            partnershipButton.setEnabled(true);
        }

        if (peopleList.get(position).getFollowing()) {
            followTxt.setText("Unfollow");
            followTxt.setTextColor(Color.parseColor("#CACACA"));
            follow.setBackgroundResource(R.drawable.username_icon);//!!
        } else {
            followTxt.setText("Follow");
            followTxt.setTextColor(Color.parseColor("#2CC9D8"));
            follow.setBackgroundResource(R.drawable.follow_icon);
        }

        partnershipButton.setOnClickListener(v -> {
            partnershipButton.setEnabled(false);
            new CountDownTimer(500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }
                @Override
                public void onFinish() {
                    if (peopleList.get(position).getFriend()){
                        peopleList.get(position).setFriend(false);
                        partnershipButton.setEnabled(true);
                        partnershipButton.setText("Partner Up!");
                        partnershipButton.setTextColor(Color.parseColor("#FFFFFF"));
                        partnershipButton.setBackgroundResource(R.drawable.bg_round_partner_up_blue);
                        Call<HashMap> call = MainActivity.APIBuild().unFriend(peopleList.get(position).getId(), "Bearer " +
                                context.getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));

                        call.enqueue(new Callback<HashMap>() {
                            @Override
                            public void onResponse(Call<HashMap> call, Response<HashMap> response) {

                            }

                            @Override
                            public void onFailure(Call<HashMap> call, Throwable t) {
                            }
                        });

                    } else if (peopleList.get(position).isPendingRequest()){
                        peopleList.get(position).setPendingRequest(false);
                        partnershipButton.setEnabled(true);
                        partnershipButton.setText("End Partnership");
                        partnershipButton.setTextColor(Color.WHITE);
                        partnershipButton.setBackgroundResource(R.drawable.bg_round_end_partnership);
                        Call<HashMap> callNotifications = MainActivity.APIBuild().acceptFriendRequest(peopleList.get(position).getId(), "Bearer " +
                                context.getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
                        callNotifications.enqueue(new Callback<HashMap>() {
                            @Override
                            public void onResponse(Call<HashMap> call, Response<HashMap> response) {

                            }

                            @Override
                            public void onFailure(Call<HashMap> call, Throwable t) {
                                Toast.makeText(context, "Your internet connection is bad or our server is down", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (!peopleList.get(position).getFriend() && !peopleList.get(position).isSentRequest()){
                        peopleList.get(position).setFriend(true);
                        peopleList.get(position).setSentRequest(true);
                        partnershipButton.setEnabled(false);
                        partnershipButton.setText("Request Sent");
                        partnershipButton.setTextColor(Color.WHITE);
                        partnershipButton.setBackgroundResource(R.drawable.bg_round_end_partnership);
                        Call<HashMap> call = MainActivity.APIBuild().friendRequest(peopleList.get(position).getId(), "Bearer " +
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
            }.start();


        });

        userName.setText(peopleList.get(position).getUsername());
        if(peopleList.get(position).getCity().equals("") && peopleList.get(position).getCountry().equals("")){
            userLocation.setVisibility(View.GONE);
        }else {
            location.setText(peopleList.get(position).getCity() + ", " + peopleList.get(position).getCountry());
        }
        description.setText(peopleList.get(position).getDescription());


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<User> call = MainActivity.APIBuild().getPerson(peopleList.get(position).getId() + "", "Bearer " +
                        context.getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));
                call.enqueue(new CustomCallBack<User>(context) {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        super.onResponse(call , response);
                        if (response.isSuccessful()) {
                            Intent i = new Intent(context, ForeignProfileActivity.class);
                            i.putExtra("TYPE", "FOREIGN EVENTS");
                            i.putExtra("user", response.body());
                            context.startActivity(i);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        super.onFailure(call , t);
                    }
                });
            }

        });


        followButton.setOnClickListener(v -> {

            if (!peopleList.get(position).getFollowing()) {
                peopleList.get(position).setFollowing(true);
                followTxt.setText("Unfollow");
                followTxt.setTextColor(Color.parseColor("#CACACA"));
                follow.setBackgroundResource(R.drawable.username_icon);
                Call<HashMap> call = MainActivity.APIBuild().follow(peopleList.get(position).getId(), "Bearer " +
                        context.getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));

                call.enqueue(new Callback<HashMap>() {
                    @Override
                    public void onResponse(Call<HashMap> call, Response<HashMap> response) {
                    }

                    @Override
                    public void onFailure(Call<HashMap> call, Throwable t) {
                    }
                });
            } else {
                peopleList.get(position).setFollowing(false);
                followTxt.setText("Follow");
                followTxt.setTextColor(Color.parseColor("#2CC9D8"));
                follow.setBackgroundResource(R.drawable.follow_icon);
                Call<HashMap> call = MainActivity.APIBuild().unFollow(peopleList.get(position).getId(), "Bearer " +
                        context.getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));

                call.enqueue(new Callback<HashMap>() {
                    @Override
                    public void onResponse(Call<HashMap> call, Response<HashMap> response) {

                    }

                    @Override
                    public void onFailure(Call<HashMap> call, Throwable t) {
                    }
                });
            }
            followButton.setEnabled(false);
            new CountDownTimer(500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }
                @Override
                public void onFinish() {
                    followButton.setEnabled(true);
                }
            }.start();
        });
        messageButton.setOnClickListener(v -> {
            fetch(peopleList.get(position));
            messageButton.setEnabled(false);
            new CountDownTimer(500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }
                @Override
                public void onFinish() {
                    messageButton.setEnabled(true);
                }
            }.start();

        });

        return rowView;
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
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("key", chat.get("key").toString());
                        intent.putExtra("name", user.getName());
                        intent.putExtra("img", user.getAvatar());
                        intent.putExtra("em", user.getEmail());

                        context.startActivity(intent);
                        return;
                    }

                    if (chat.get("participants").toString().equals(user.getEmail() + "," + sharedPreferences.getString("email", "xxx"))) {
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("key", chat.get("key").toString());
                        intent.putExtra("name", user.getName());
                        intent.putExtra("img", user.getAvatarThumb());
                        intent.putExtra("em", user.getEmail());

                        context.startActivity(intent);
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


                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("key", mGroupId);
                intent.putExtra("name", user.getName());
                intent.putExtra("img", user.getAvatarThumb());
                intent.putExtra("em", user.getEmail());

                context.startActivity(intent);

            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}
