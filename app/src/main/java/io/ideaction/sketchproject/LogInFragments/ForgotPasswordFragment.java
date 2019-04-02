package io.ideaction.sketchproject.LogInFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.R;
import io.ideaction.sketchproject.Validations.Validations;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

@SuppressLint("ValidFragment")
public class ForgotPasswordFragment extends Fragment {
   String viewType;
   
   public ForgotPasswordFragment(String viewType) {
      this.viewType = viewType;
   }
   
   public String getViewType() {
      return viewType;
   }
   
   Button back;
   Button next;
   EditText email;
   EditText password1;
   EditText password2;
   TextView boldText;
   TextView regularText;
   LinearLayout backGround;
   LinearLayout blankSpace;
   SharedPreferences sharedPreferences;
   
   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      final View view = inflater.inflate(R.layout.forgot_password, container, false);
      setUpVariables(view);
      setUpFragment();
      
      back.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            setUpBackButton();
         }
      });
      next.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            setUpNextButton(view);
         }
      });
      
      return view;
   }
   
   @Override
   public void onStart() {
      super.onStart();
      Validations.showKeyboard(getActivity());
   }
   
   void setUpVariables(View view){
      sharedPreferences = getActivity().getSharedPreferences("e-mail", Context.MODE_PRIVATE);
      email = view.findViewById(R.id.email_edit_text);
      back = view.findViewById(R.id.x);
      next = view.findViewById(R.id.next_button);
      boldText = view.findViewById(R.id.bold_text_view);
      regularText = view.findViewById(R.id.regular_text_view);
      backGround = view.findViewById(R.id.background_color_linear_layout);
      password1 = view.findViewById(R.id.password1);
      password2 = view.findViewById(R.id.password2);
      blankSpace = view.findViewById(R.id.blank_space);
      email.requestFocus();
   }
   
   void setUpFragment(){
      switch (getViewType()) {
         case "E-mail":
            forgotPassword1();
            break;
         case "EnterCode":
            forgotPassword2();
            break;
         case "EnterNewPassword":
            forgotPassword3();
            break;
         case "YourPasswordHasChanged":
            forgotPassword4();
            break;
      }
   }
   
   void setUpBackButton(){
      Validations.hideKeyboard(getActivity());
      switch (getViewType()) {
         case "FirstView":
            ((MainActivity) getActivity()).enterFromLeft(new SecondLogInFragment("LogIn"));
            break;
      
         case "E-mail":
            ((MainActivity) getActivity()).enterFromLeft(new ForgotPasswordFragment("FirstView"));
            break;
      
         case "EnterCode":
            ((MainActivity) getActivity()).enterFromLeft(new ForgotPasswordFragment("E-mail"));
            break;
      
         case "EnterNewPassword":
            ((MainActivity) getActivity()).enterFromLeft(new ForgotPasswordFragment("EnterCode"));
            break;
      }
   }
   
   void setUpNextButton(final View view){
      switch (getViewType()) {
         case "FirstView": {
            if (!Validations.isValidEmail(email.getText().toString())) {
               Toast.makeText(getActivity(), "You need to provide a valid E-mail", Toast.LENGTH_SHORT).show();
               return;
            }
            sharedPreferences.edit().putString("email", email.getText().toString()).apply();
            MainActivity.showLoadingIndicator(view);
            Validations.hideKeyboard(getActivity());
         
            Call<HashMap> forgotPassword1 = MainActivity.APIBuild().forgotPassword1(email.getText().toString());
            forgotPassword1.enqueue(new Callback<HashMap>() {
               @Override
               public void onResponse(Call<HashMap> call, Response<HashMap> response) {
                  MainActivity.hideLoadingIndicator(view);
                  if (response.code() == 200) {
                     ((MainActivity) getActivity()).enterFromRight(new ForgotPasswordFragment("EnterCode"));
                  } else {
                     Toast.makeText(getActivity(), "Invalid E-mail address", Toast.LENGTH_SHORT).show();
                     Validations.showKeyboard(getActivity());
                  }
               }
               @Override
               public void onFailure(Call<HashMap> call, Throwable t) {
                  MainActivity.hideLoadingIndicator(view);
                  Toast.makeText(getActivity(), "Check your internet connection or maybe our server is down.", Toast.LENGTH_SHORT).show();
               }
            });
            break;
         }
      
         case "E-mail":
            ((MainActivity) getActivity()).enterFromRight(new ForgotPasswordFragment("EnterCode"));
            break;
      
         case "EnterCode":
            if (email.getText().toString().isEmpty()) {
               Toast.makeText(getActivity(), "Input the confirmation code", Toast.LENGTH_SHORT).show();
               return;
            }
            MainActivity.showLoadingIndicator(view);
            sharedPreferences.edit().putString("token", email.getText().toString()).apply();
            Validations.hideKeyboard(getActivity());
         
            Call<HashMap> forgotPassword2 = MainActivity.APIBuild().forgotPassword2(
                    getActivity().getSharedPreferences("e-mail", Context.MODE_PRIVATE).getString("email", ""),
                    email.getText().toString());
            forgotPassword2.enqueue(new Callback<HashMap>() {
               @Override
               public void onResponse(Call<HashMap> call, Response<HashMap> response) {
                  MainActivity.hideLoadingIndicator(view);
                  if (response.code() == 200) {
                     ((MainActivity) getActivity()).enterFromRight(new ForgotPasswordFragment("EnterNewPassword"));
                  } else {
                     Toast.makeText(getActivity(), "Invalid confirmation code", Toast.LENGTH_SHORT).show();
                  }
               }
               @Override
               public void onFailure(Call<HashMap> call, Throwable t) {
                  MainActivity.hideLoadingIndicator(view);
                  Toast.makeText(getActivity(), "Check your internet connection or maybe our server is down.", Toast.LENGTH_SHORT).show();
               }
            });
            break;
      
         case "EnterNewPassword":
            if (password1.getText().toString().isEmpty() || password2.getText().toString().isEmpty()) {
               Toast.makeText(getActivity(), "Provide the new password in both fields", Toast.LENGTH_SHORT).show();
               return;
            } else if (!Validations.isPasswordMatching(password1, password2)) {
               Toast.makeText(getActivity(), "The passwords should match", Toast.LENGTH_SHORT).show();
               return;
            }
            MainActivity.showLoadingIndicator(view);
            Validations.hideKeyboard(getActivity());
         
            Call<HashMap> forgotPassword3 = MainActivity.APIBuild().forgotPassword3(
                    getActivity().getSharedPreferences("e-mail", Context.MODE_PRIVATE).getString("email", ""),
                    getActivity().getSharedPreferences("e-mail", Context.MODE_PRIVATE).getString("token", ""),
                    password1.getText().toString(),
                    password2.getText().toString()
            );
            forgotPassword3.enqueue(new Callback<HashMap>() {
               @Override
               public void onResponse(Call<HashMap> call, Response<HashMap> response) {
                  MainActivity.hideLoadingIndicator(view);
                  if (response.code() == 200) {
                     ((MainActivity) getActivity()).enterFromRight(new SecondLogInFragment("E-mail"));                  } else {
                     Toast.makeText(getActivity(), "Invalid confirmation code", Toast.LENGTH_SHORT).show();
                  }
               }
               @Override
               public void onFailure(Call<HashMap> call, Throwable t) {
                  MainActivity.hideLoadingIndicator(view);
                  Toast.makeText(getActivity(), "Check your internet connection or maybe our server is down.", Toast.LENGTH_SHORT).show();
               }
            });
            break;
      
         case "YourPasswordHasChanged":
            Validations.hideKeyboard(getActivity());
            sharedPreferences.edit().clear().apply();
            getActivity().finish();

        //    ((MainActivity) getActivity()).enterFromRight(new SecondLogInFragment("E-mail"));
            break;
         default:
            Validations.showKeyboard(getActivity());
            ((MainActivity) getActivity()).enterFromRight(new ForgotPasswordFragment("FirstView"));
      }
   }
   
   void forgotPassword4() {
      boldText.setText("Your password was changed");
      regularText.setText("Check your email and click on the link to reset your password. ");
      back.setVisibility(GONE);
      next.setText("Back to login");
      email.setVisibility(GONE);
      blankSpace.setVisibility(View.VISIBLE);
      password1.setVisibility(View.GONE);
      password2.setVisibility(View.GONE);
   }
   
   void forgotPassword3() {
      boldText.setText("Enter new password");
      regularText.setText("Pick one you will remember this time :)");
      email.setVisibility(GONE);
      password1.setVisibility(View.VISIBLE);
      password2.setVisibility(View.VISIBLE);
      password1.requestFocus();
      Validations.showKeyboard(getActivity());
   }
   
   void forgotPassword2() {
      backGround.setVisibility(View.VISIBLE);
      blankSpace.setVisibility(View.GONE);
      boldText.setText("Enter Code");
      regularText.setText("Enter the code from your email");
      email.setHint("Code");
      password1.setVisibility(View.GONE);
      password2.setVisibility(View.GONE);
   }
   
   void forgotPassword1() {
      backGround.setVisibility(GONE);
      blankSpace.setVisibility(View.VISIBLE);
      boldText.setText("Email has been sent!");
      regularText.setText("Check your email and click on the link to reset your password. ");
   }
}
