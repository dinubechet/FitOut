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
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.CustomCallBack;
import io.ideaction.sketchproject.MainScreenFragments.ForeignPartnersFragment;
import io.ideaction.sketchproject.Models.User;
import io.ideaction.sketchproject.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

public class ForeignPartnersAdapter extends BaseAdapter {
    private Context context;
    private boolean readyToCall = true;
    private int page = 1;
    private String id;
    SharedPreferences sharedPreferences;
    
    private List<User> partnersList = new ArrayList<>();
    ForeignPartnersFragment fragment;
    View view;
    
    public void setPartnersFeedList(List<User> partnersList, int page) {
        if (page == 1) {
            this.partnersList = partnersList;
            notifyDataSetChanged();
            return;
        }
        this.partnersList.addAll(partnersList);
        readyToCall = !partnersList.isEmpty();
        notifyDataSetChanged();
    }
    
    public ForeignPartnersAdapter(Context context, String id , ForeignPartnersFragment fragment , View view) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("my", MODE_PRIVATE);
        this.fragment = fragment;
        this.view = view;
        
    }
    
    @Override
    public int getCount() {
        return partnersList.size();
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.partner_cell, parent, false);
        
        CircleImageView profileImage = rowView.findViewById(R.id.profile_image);
        TextView nameTv = rowView.findViewById(R.id.name_tv);
        TextView usernameTv = rowView.findViewById(R.id.username_tv);
        TextView locationTv = rowView.findViewById(R.id.location_tv);
        final Button partnershipButton = rowView.findViewById(R.id.partnership_button);
        final LinearLayout messageButton = rowView.findViewById(R.id.message_button);
        LinearLayout userLocation = rowView.findViewById(R.id.location);
   
       if (partnersList.get(position).getEmail().equals(sharedPreferences.getString("email" , ""))){
          partnershipButton.setVisibility(GONE);
          messageButton.setVisibility(GONE);
       }
        
        messageButton.setOnClickListener(v -> {
            fetch(partnersList.get(position));
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
        
        if (position == partnersList.size() - 1 && readyToCall) {
            page++;
            fragment.requestPartnersPage(page,view);
        }
        
        if (partnersList.get(position).getFriend()) {
            partnershipButton.setText("End Partnership");
            partnershipButton.setTextColor(Color.parseColor("#FFFFFF"));
            partnershipButton.setBackgroundResource(R.drawable.bg_round_end_partnership);
            partnershipButton.setEnabled(true);
        }else if (partnersList.get(position).isPendingRequest()){
            partnershipButton.setText("Accept");
            partnershipButton.setTextColor(Color.parseColor("#FFFFFF"));
            partnershipButton.setBackgroundResource(R.drawable.bg_round_end_partnership);
            partnershipButton.setEnabled(true);
        }else if (partnersList.get(position).isSentRequest()){
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

        
        nameTv.setText(partnersList.get(position).getName());
        if (partnersList.get(position).getAvatar().isEmpty()) {
            Picasso.get().load(R.drawable.default_profile).into(profileImage);
        } else {
            Picasso.get().load(partnersList.get(position).getAvatar()).placeholder(R.drawable.default_profile).into(profileImage);
        }
        
        usernameTv.setText(partnersList.get(position).getUsername());
        if(partnersList.get(position).getCity().equals("") && partnersList.get(position).getCountry().equals("")){
            userLocation.setVisibility(GONE);
        }else {
            locationTv.setText(partnersList.get(position).getCity() + ", " + partnersList.get(position).getCountry());
        }
        
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<User> call = MainActivity.APIBuild().getPerson(partnersList.get(position).getId() + "", "Bearer " +
                        context.getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
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
        
        partnershipButton.setOnClickListener(v -> {
            partnershipButton.setEnabled(false);
            new CountDownTimer(500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }
                @Override
                public void onFinish() {
                    if (partnersList.get(position).getFriend()){
                        partnersList.get(position).setFriend(false);
                        partnershipButton.setEnabled(true);
                        partnershipButton.setText("Partner Up!");
                        partnershipButton.setTextColor(Color.parseColor("#FFFFFF"));
                        partnershipButton.setBackgroundResource(R.drawable.bg_round_partner_up_blue);
                        Call<HashMap> call = MainActivity.APIBuild().unFriend(partnersList.get(position).getId(), "Bearer " +
                                context.getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));

                        call.enqueue(new Callback<HashMap>() {
                            @Override
                            public void onResponse(Call<HashMap> call, Response<HashMap> response) {

                            }

                            @Override
                            public void onFailure(Call<HashMap> call, Throwable t) {
                            }
                        });

                    } else if (partnersList.get(position).isPendingRequest()){
                        partnersList.get(position).setPendingRequest(false);
                        partnershipButton.setEnabled(true);
                        partnershipButton.setText("End Partnership");
                        partnershipButton.setTextColor(Color.WHITE);
                        partnershipButton.setBackgroundResource(R.drawable.bg_round_end_partnership);
                        Call<HashMap> callNotifications = MainActivity.APIBuild().acceptFriendRequest(partnersList.get(position).getId(), "Bearer " +
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
                    } else if (!partnersList.get(position).getFriend() && !partnersList.get(position).isSentRequest()){
                        partnersList.get(position).setFriend(true);
                        partnersList.get(position).setSentRequest(true);
                        partnershipButton.setEnabled(false);
                        partnershipButton.setText("Request Sent");
                        partnershipButton.setTextColor(Color.WHITE);
                        partnershipButton.setBackgroundResource(R.drawable.bg_round_end_partnership);
                        Call<HashMap> call = MainActivity.APIBuild().friendRequest(partnersList.get(position).getId(), "Bearer " +
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