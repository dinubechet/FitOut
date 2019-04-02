package io.ideaction.sketchproject.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.ideaction.sketchproject.Activities.ForeignProfileActivity;
import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.CustomCallBack;
import io.ideaction.sketchproject.Models.ChatModel;
import io.ideaction.sketchproject.Models.CommentModel;
import io.ideaction.sketchproject.Models.User;
import io.ideaction.sketchproject.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends BaseAdapter {
   
   List<CommentModel> commentList = new ArrayList<>();
   Context context;
   
   public CommentAdapter(Context context) {
      this.context = context;
   }
   
   public void setCommentList(List<CommentModel> chatList) {
      this.commentList = chatList;
   }
   
   public void add(CommentModel item) {
      commentList.add(0 ,item);
      notifyDataSetChanged();
   }
   
   @Override
   public int getCount() {
      return commentList.size();
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
      View rowView = inflater.inflate(R.layout.comment_cell, parent, false);
      
      TextView commentatorsName = rowView.findViewById(R.id.senders_name);
      TextView actualMessage = rowView.findViewById(R.id.message);
      TextView timePassed = rowView.findViewById(R.id.time_passed);
      CircleImageView sendersPicture = rowView.findViewById(R.id.profile_image);
      
      commentatorsName.setText(commentList.get(position).getUser().getName());
      actualMessage.setText(commentList.get(position).getMessage());
      timePassed.setText(commentList.get(position).getCreatedAtForHumans());
      if (commentList.get(position).getUser().getAvatar().isEmpty()) {
         Picasso.get().load(R.drawable.default_profile).into(sendersPicture);
      } else {
         Picasso.get().load(commentList.get(position).getUser().getAvatar()).placeholder(R.drawable.default_profile).into(sendersPicture);
      }
      
      sendersPicture.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Call<User> call = MainActivity.APIBuild().getPerson(commentList.get(position).getUser().getId() + "", "Bearer " +
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
