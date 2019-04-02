package io.ideaction.sketchproject.MainScreenFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import io.ideaction.sketchproject.Activities.FeedActivity;
import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.Adapters.CommentAdapter;
import io.ideaction.sketchproject.Models.ChatModel;
import io.ideaction.sketchproject.Models.CommentModel;
import io.ideaction.sketchproject.Models.User;
import io.ideaction.sketchproject.R;
import io.ideaction.sketchproject.Validations.Validations;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ValidFragment")
public class ViewAllCommentsFragment extends Fragment {
   
   int postID;
   int page = 1;
   CommentAdapter adapter;
   ListView listView;
   ImageView backButton;
   EditText messageLine;
   Button sendButton;
   SharedPreferences sharedPreferences;
   
   
   
   
   public int getPostID() {
      return postID;
   }
   
   public ViewAllCommentsFragment(int postID) {
      this.postID = postID;
   }
   
   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      final View view = inflater.inflate(R.layout.add_comment_fragment, container, false);
      listView = view.findViewById(R.id.comments_list_view);
      backButton = view.findViewById(R.id.back_button);
      sendButton = view.findViewById(R.id.send_button);
      messageLine = view.findViewById(R.id.message_input_line);
      sharedPreferences = getActivity().getSharedPreferences("my", Context.MODE_PRIVATE);
      adapter = new CommentAdapter(getActivity());
      listView.setAdapter(adapter);
      getComments();
      
      backButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Validations.hideKeyboard(getActivity());
            getActivity().onBackPressed();
         }
      });
      messageLine.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
         }
         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {
         }
         @Override
         public void afterTextChanged(Editable s) {
            int i;
            for (i = 0; i < s.length() && Character.isWhitespace(s.charAt(i)); i++) {  }
            s.replace(0, i, "");
            if (s.toString().isEmpty()) {
               sendButton.setEnabled(false);
               sendButton.setTextColor(Color.parseColor("#B5B5B5"));
            }
            else {
               sendButton.setEnabled(true);
               sendButton.setTextColor(Color.parseColor("#000000"));
            }
         }
      });
      
      sendButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            
            Call<HashMap> addComment = MainActivity.APIBuild().addComment(getPostID(),messageLine.getText().toString(), "Bearer " +
                    getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));
            addComment.enqueue(new Callback<HashMap>() {
               @Override
               public void onResponse(Call<HashMap> call, Response<HashMap> response) {
               }
               
               @Override
               public void onFailure(Call<HashMap> call, Throwable t) {
                  Toast.makeText(getActivity(),"Your internet connection is bad or our server is down",Toast.LENGTH_SHORT);
               }
            });
            adapter.add(new CommentModel(messageLine.getText().toString(),
                    new User(sharedPreferences.getString("name",""),sharedPreferences.getString("img", "")),
                    "now"));
            messageLine.setText("");
            
         }
      });
      
      return view;
   }
   
   void getComments(){
      Call<List<CommentModel>> getComments = MainActivity.APIBuild().getComments(getPostID(),page,"Bearer " +
              getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));
      getComments.enqueue(new Callback<List<CommentModel>>() {
         @Override
         public void onResponse(Call<List<CommentModel>> call, Response<List<CommentModel>> response) {
            if (response.isSuccessful()) {
               adapter.setCommentList(response.body());
               adapter.notifyDataSetChanged();
            } else {
               {
                  try {
                     JSONObject jObjError = new JSONObject(response.errorBody().string());
                     Toast.makeText(getActivity(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                  } catch (Exception e) {
                     Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                  }
               }
            }
         }
         
         @Override
         public void onFailure(Call<List<CommentModel>> call, Throwable t) {
         }
      });
   }
}