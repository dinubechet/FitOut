package io.ideaction.sketchproject.MainScreenFragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.ideaction.sketchproject.Activities.ChatActivity;
import io.ideaction.sketchproject.Activities.EventsActivity;
import io.ideaction.sketchproject.Activities.FeedActivity;
import io.ideaction.sketchproject.Activities.ForeignEventsActivity;
import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.Activities.SportActivity;
import io.ideaction.sketchproject.Models.User;
import io.ideaction.sketchproject.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ValidFragment")
public class ForeignProfileFragment extends Fragment {
   
   
   ImageView backArrow;
   CircleImageView profileImage;
   Button followButton;
   TextView profileName;
   TextView atSignProfileName;
   TextView userLocation;
   TextView followers;
   TextView motto;
   Button partnerUpButton;
   Button messageButton;
   RelativeLayout posts;
   RelativeLayout likes;
   RelativeLayout events;
   RelativeLayout partners;
   User user;
   SharedPreferences sharedPreferences;


   public ForeignProfileFragment(User user) {
      this.user = user;
   }
   
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.foreign_profile_fragment, container, false);

      backArrow = view.findViewById(R.id.back_arrow_foreign);
      profileImage = view.findViewById(R.id.profile_image);
      followButton = view.findViewById(R.id.follow_button);
      profileName = view.findViewById(R.id.profile_name);
      atSignProfileName = view.findViewById(R.id.at_sign_profile_name);
      userLocation = view.findViewById(R.id.user_location);
      followers = view.findViewById(R.id.followers);
      motto = view.findViewById(R.id.motto);
      partnerUpButton = view.findViewById(R.id.partner_up_button);
      messageButton = view.findViewById(R.id.message_button);
      posts = view.findViewById(R.id.posts);
      likes = view.findViewById(R.id.likes);
      events = view.findViewById(R.id.events);
      partners = view.findViewById(R.id.partners);
      sharedPreferences = getActivity().getSharedPreferences("my", Context.MODE_PRIVATE);
      
      messageButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            fetch(user);
         }
      });
      
      posts.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent i = new Intent(getActivity(), FeedActivity.class);
            i.putExtra("TYPE", "FOREIGN POST");
            i.putExtra("id", user.getId());
            getActivity().startActivity(i);
         }
      });
      
      likes.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent i = new Intent(getActivity(), FeedActivity.class);
            i.putExtra("TYPE", "FOREIGN LIKES");
            i.putExtra("id", user.getId());
            getActivity().startActivity(i);
         }
      });
      
      events.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent i = new Intent(getActivity(), ForeignEventsActivity.class);
            i.putExtra("TYPE", "FOREIGN EVENTS");
            i.putExtra("id", user.getId());
            getActivity().startActivity(i);
         }
      });
      
      if (user.getFriend()) {
         partnerUpButton.setText("End Partnership");
         partnerUpButton.setTextColor(Color.WHITE);
         partnerUpButton.setBackgroundResource(R.drawable.bg_round_fill_unfollow);
      } else {
         partnerUpButton.setText("Partner Up!");
         partnerUpButton.setTextColor(Color.parseColor("#2CC9D8"));
         partnerUpButton.setBackgroundResource(R.drawable.bg_round_fill);
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
      
      followers.setText("Followers: " + user.getFollowersCount().toString().replace(".0", "")
              + " | " + "Following: " + user.getFollowingCount().toString().replace(".0", ""));
      
      userLocation.setText(user.getCity() + ", " +
              user.getCountry() + " | " +
              user.getTrainingHours() + " | " +
              user.getTrainingPlace());
      
      backArrow.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            getActivity().onBackPressed();
         }
      });
      
      partnerUpButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (!user.getFriend()) {
               user.setFriend(true);
               partnerUpButton.setText("End Partnership");
               partnerUpButton.setTextColor(Color.WHITE);
               partnerUpButton.setBackgroundResource(R.drawable.bg_round_fill_unfollow);
               Call<HashMap> call = MainActivity.APIBuild().friendRequest(user.getId(), "Bearer " +
                       getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));
               
               call.enqueue(new Callback<HashMap>() {
                  @Override
                  public void onResponse(Call<HashMap> call, Response<HashMap> response) {
                  }
                  
                  @Override
                  public void onFailure(Call<HashMap> call, Throwable t) {
                  }
               });
               
            } else {
               user.setFriend(false);
               partnerUpButton.setText("Partner Up!");
               partnerUpButton.setTextColor(Color.parseColor("#2CC9D8"));
               partnerUpButton.setBackgroundResource(R.drawable.bg_round_fill);
               Call<HashMap> call = MainActivity.APIBuild().unFriend(user.getId(), "Bearer " +
                       getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));
               
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
      
      followButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (!user.getFollowing()) {
               user.setFollowing(true);
               followButton.setText("Unfollow");
               followButton.setTextColor(Color.WHITE);
               followButton.setBackgroundResource(R.drawable.bg_round_fill_unfollow);
               Call<HashMap> call = MainActivity.APIBuild().follow(user.getId(), "Bearer " +
                       getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));
               
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
                       getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));
               
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
            ((FeedActivity) getActivity()).enterFromRight(new ForeignPartnersFragment(user.getId() + ""));
         }
      });







      return view;
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
                  Intent intent = new Intent(getActivity(), ChatActivity.class);
                  intent.putExtra("key", chat.get("key").toString());
                  intent.putExtra("name", user.getName());
                  intent.putExtra("img", user.getAvatar());
                  intent.putExtra("em", user.getEmail());
                  getActivity().startActivity(intent);
                  return;
               }
               
               if (chat.get("participants").toString().equals(user.getEmail() + "," + sharedPreferences.getString("email", "xxx"))) {
                  Intent intent = new Intent(getActivity(), ChatActivity.class);
                  intent.putExtra("key", chat.get("key").toString());
                  intent.putExtra("name", user.getName());
                  intent.putExtra("img", user.getAvatarThumb());
                  intent.putExtra("em", user.getEmail());
                  getActivity().startActivity(intent);
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
            
            
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("key", mGroupId);
            intent.putExtra("name", user.getName());
            intent.putExtra("img", user.getAvatarThumb());
            intent.putExtra("em", user.getEmail());

            getActivity().startActivity(intent);
            
         }
         
         @Override
         public void onCancelled(DatabaseError error) {
            
         }
      });
      
      
   }
}