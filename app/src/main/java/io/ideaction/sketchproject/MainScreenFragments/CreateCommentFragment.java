package io.ideaction.sketchproject.MainScreenFragments;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import io.ideaction.sketchproject.Activities.FeedActivity;
import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.Activities.SingleNotificationActivity;
import io.ideaction.sketchproject.Activities.SportActivity;
import io.ideaction.sketchproject.R;
import io.ideaction.sketchproject.Validations.Validations;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ValidFragment")
public class CreateCommentFragment extends Fragment {
   
   TextView addComment;
   ImageView xButton;
   EditText comment;
   int postID;
   
   public CreateCommentFragment(int postID) {
      this.postID = postID;
   }
   
   public int getPostID() {
      return postID;
   }
   
   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      final View view = inflater.inflate(R.layout.create_comment, container, false);
      
      addComment = view.findViewById(R.id.post_button);
      xButton = view.findViewById(R.id.back_button);
      comment = view.findViewById(R.id.et_comment);
      comment.requestFocus();
      
      
      if (getActivity() instanceof  SportActivity)
         ((SportActivity)getActivity()).setFragmentDrawer("addPost");
      
      xButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Validations.hideKeyboard(getActivity());
            getActivity().onBackPressed();
         }
      });
      
      comment.addTextChangedListener(new TextWatcher() {
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
         }
      });
      
      addComment.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(final View v) {
            if (!comment.getText().toString().isEmpty()) {
               MainActivity.showLoadingIndicator(view);
               Call<HashMap> addComment = MainActivity.APIBuild().addComment(getPostID(), comment.getText().toString(), "Bearer " +
                       getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));
               addComment.enqueue(new Callback<HashMap>() {
                  @Override
                  public void onResponse(Call<HashMap> call, Response<HashMap> response) {
                     MainActivity.hideLoadingIndicator(view);
                     Validations.hideKeyboard(getActivity());
                     getActivity().onBackPressed();
                  }
                  
                  @Override
                  public void onFailure(Call<HashMap> call, Throwable t) {
                     MainActivity.hideLoadingIndicator(view);
                     Validations.hideKeyboard(getActivity());
                     Toast.makeText(getActivity(), "Your internet connection is bad or our server is down", Toast.LENGTH_SHORT);
                  }
               });
            }else{
               Toast.makeText(getActivity(), "Please add your comment", Toast.LENGTH_SHORT);
            }
         }
      });
      
      return view;
   }
}