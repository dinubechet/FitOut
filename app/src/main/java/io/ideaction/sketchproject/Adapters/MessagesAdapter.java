package io.ideaction.sketchproject.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.ideaction.sketchproject.Activities.ChatActivity;
import io.ideaction.sketchproject.R;
import io.ideaction.sketchproject.Validations.TimeAgo;

public class MessagesAdapter extends BaseAdapter {
   
   Context context;
   List<HashMap> list = new ArrayList<>();
   private SharedPreferences sharedPreferences;
   
   public void setList(List<HashMap> list) {
      if (this.list.containsAll(list))
         return;
      this.list = list;
      notifyDataSetChanged();
   }
   
   public MessagesAdapter(Context context) {
      this.context = context;
      sharedPreferences = context.getSharedPreferences("my" , Context.MODE_PRIVATE);
   }
   
   @Override
   public int getCount() {
      return list.size();
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
      final View rowView = inflater.inflate(R.layout.message_cell, parent, false);
      
      CircleImageView sendersPicture = rowView.findViewById(R.id.profile_image);
      final TextView sendersName = rowView.findViewById(R.id.senders_name);
      TextView timePassed = rowView.findViewById(R.id.time_passed);
      TextView message = rowView.findViewById(R.id.message);
      String img;
      String email;
      if (list.get(position).get("participants").toString().split(",")[0].equals(sharedPreferences.getString("email" , "xxx"))) {
         sendersName.setText(list.get(position).get("participantsName").toString().split(",")[1]);
            if (!list.get(position).get("participantsImg").toString().split(",")[1].equals("no"))
            Picasso.get().load(list.get(position).get("participantsImg").toString().split(",")[1]).placeholder(R.drawable.default_profile).into(sendersPicture);
            else
               sendersPicture.setImageResource(R.drawable.default_profile);
         
         img = list.get(position).get("participantsImg").toString().split(",")[1];
         email = list.get(position).get("participants").toString().split(",")[1];

      }
      else {
         sendersName.setText(list.get(position).get("participantsName").toString().split(",")[0]);
         if (!list.get(position).get("participantsImg").toString().split(",")[0].equals("no"))
            Picasso.get().load(list.get(position).get("participantsImg").toString().split(",")[0]).placeholder(R.drawable.default_profile).into(sendersPicture);
         else
            sendersPicture.setImageResource(R.drawable.default_profile);
   
   
         img = list.get(position).get("participantsImg").toString().split(",")[0];
         email = list.get(position).get("participants").toString().split(",")[0];

      }
      
      message.setText(list.get(position).get("lastMessage").toString());
   
      if (!list.get(position).get("lastMessageTime").toString().equals(""))
         timePassed.setText(TimeAgo.getTimeAgo(Long.valueOf(list.get(position).get("lastMessageTime").toString())));
   
      final String finalImg = img;
      final String finalEmail = email;

      rowView.setOnClickListener(v -> {
         rowView.setEnabled(false);
         Intent intent = new Intent(context , ChatActivity.class);
         intent.putExtra("key" , list.get(position).get("key").toString());
         intent.putExtra("name" , sendersName.getText().toString());
         intent.putExtra("img" , finalImg);
         intent.putExtra("em", finalEmail);

         context.startActivity(intent);

         new CountDownTimer(500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
               rowView.setEnabled(true);
            }
         }.start();
      });
      
      return rowView;
   }
}
