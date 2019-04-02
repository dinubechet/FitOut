package io.ideaction.sketchproject.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import io.ideaction.sketchproject.MainScreenFragments.PartnersFragment;
import io.ideaction.sketchproject.Models.User;
import io.ideaction.sketchproject.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class BlockedAdapter extends BaseAdapter {

   private Context context;

   SharedPreferences sharedPreferences;

   private List<User> partnersList = new ArrayList<>();



   public BlockedAdapter(Context context, List<User> partnersList) {
      this.context = context;
      sharedPreferences = context.getSharedPreferences("my", Context.MODE_PRIVATE);
      this.partnersList = partnersList;

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
   public View getView(final int position, View convertView, ViewGroup parent) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View rowView = inflater.inflate(R.layout.blockeduser_cell, parent, false);
      
      CircleImageView profileImage = rowView.findViewById(R.id.profile_image);
      TextView nameTv = rowView.findViewById(R.id.name_tv);
      TextView usernameTv = rowView.findViewById(R.id.username_tv);
      TextView locationTv = rowView.findViewById(R.id.location_tv);

      LinearLayout userLocation = rowView.findViewById(R.id.location);

      nameTv.setText(partnersList.get(position).getName());
      if (partnersList.get(position).getAvatar().isEmpty()) {
         Picasso.get().load(R.drawable.default_profile).into(profileImage);
      } else {
         Picasso.get().load(partnersList.get(position).getAvatar()).placeholder(R.drawable.default_profile).into(profileImage);
      }
      
      usernameTv.setText(partnersList.get(position).getUsername());
      if (partnersList.get(position).getCity().equals("") && partnersList.get(position).getCountry().equals("")) {
         userLocation.setVisibility(GONE);
      } else {
         locationTv.setText(partnersList.get(position).getCity() + ", " + partnersList.get(position).getCountry());
      }
      
      Button endBlock = rowView.findViewById(R.id.endblock);

      endBlock.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Call<Void> call = MainActivity.APIBuild().unblockuser("Bearer " + sharedPreferences.getString("token" , "") , partnersList.get(position).getId() + "");
            call.enqueue(new Callback<Void>() {
               @Override
               public void onResponse(Call<Void> call, Response<Void> response) {

               }

               @Override
               public void onFailure(Call<Void> call, Throwable t) {

               }
            });

            partnersList.remove(position);
            notifyDataSetChanged();

         }
      });

      
      return rowView;
   }
   

}