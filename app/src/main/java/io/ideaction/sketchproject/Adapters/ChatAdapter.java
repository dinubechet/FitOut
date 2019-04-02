package io.ideaction.sketchproject.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.ideaction.sketchproject.Activities.ChatActivity;
import io.ideaction.sketchproject.Activities.ForeignProfileActivity;
import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.CustomCallBack;
import io.ideaction.sketchproject.Models.ChatModel;
import io.ideaction.sketchproject.Models.User;
import io.ideaction.sketchproject.R;
import io.ideaction.sketchproject.Validations.TimeAgo;
import retrofit2.Call;
import retrofit2.Response;

public class ChatAdapter extends BaseAdapter {

   List<HashMap> chatList = new ArrayList<>();

   Context context;
   SharedPreferences sharedPreferences;
   String partenerImg;
   String key;

   public ChatAdapter(Context context , String partenerImg , String key) {
      this.context = context;
      sharedPreferences = context.getSharedPreferences("my" , Context.MODE_PRIVATE);
      this.partenerImg = partenerImg;
      this.key = key;
   }

   public void setChatList(List<HashMap> chatList) {
      this.chatList = chatList;
      notifyDataSetChanged();
   }


   @Override
   public int getCount() {
      return chatList.size();
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
      if (chatList.get(position).get("userEmail").toString().equals(sharedPreferences.getString("email" , "xxx"))) {
         LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         View rowView = inflater.inflate(R.layout.activity_chat_to_partner, parent, false);
         TextView actualMessage = rowView.findViewById(R.id.users_message);
         actualMessage.setText(chatList.get(position).get("text").toString());

         rowView.setOnLongClickListener(v -> {
               if (chatList.get(position).get("key") == null)
                  return false;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete message")
                    .setMessage("Are you sure you want to delete this message ?")
                    .setCancelable(false)
                    .setNegativeButton("NO",
                            (dialog, id) -> dialog.cancel()).setPositiveButton("YES",
                    (dialog, id) -> {
                       FirebaseDatabase.getInstance().getReference("message").child(key).child("messages").child(chatList.get(position).get("key").toString()).removeValue();

                       if (chatList.size() == 1) {
                          FirebaseDatabase.getInstance().getReference("message").child(key).removeValue();
                          ((ChatActivity)context).finish();
                       }
                       dialog.cancel();
                    });
            AlertDialog alert = builder.create();
            alert.show();


            return false;
         });

         return rowView;
      } else {
         LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         View rowView = inflater.inflate(R.layout.activity_chat_cell_from_partner, parent, false);
         CircleImageView sendersPicture = rowView.findViewById(R.id.profile_image);
         TextView actualMessage = rowView.findViewById(R.id.senders_message);
         actualMessage.setText(chatList.get(position).get("text").toString());

         if (!partenerImg.isEmpty())
            Picasso.get().load(partenerImg).placeholder(R.drawable.default_profile).into(sendersPicture);
         else
            sendersPicture.setImageResource(R.drawable.default_profile);

         TextView timeAgo = rowView.findViewById(R.id.timeAgo);
         if (!chatList.get(position).get("time").toString().equals(""))
            timeAgo.setText(TimeAgo.getTimeAgo(Long.valueOf(chatList.get(position).get("time").toString())));

         sendersPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Call<User> call = MainActivity.APIBuild().getPersonByEmail(chatList.get(position).get("userEmail") + "", "Bearer " +
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

         return rowView;
      }
   }
}