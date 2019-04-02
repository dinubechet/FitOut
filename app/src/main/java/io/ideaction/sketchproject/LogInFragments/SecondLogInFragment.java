package io.ideaction.sketchproject.LogInFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.HashMap;

import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.MainScreenFragments.MainScreenFragment;
import io.ideaction.sketchproject.R;
import io.ideaction.sketchproject.Validations.Validations;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("ValidFragment")
public class SecondLogInFragment extends Fragment {
   
   Button xBack;
   TextView welcomeText;
   TextView enterYourAccount;
   EditText emailEditText;
   EditText passwordEditText;
   EditText repeatPasswordEditText;
   TextView forgotPassword;
   Button nextButton;
   String viewType;
   
   String email;
   String password1;
   String password2;

   CheckBox signupcheckbox;
   
   public SecondLogInFragment(String viewType) {
      this.viewType = viewType;
   }
   
   public String getViewType() {
      return viewType;
   }
   
   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      final View view = inflater.inflate(R.layout.second_log_in_fragment, container, false);
      signupcheckbox = view.findViewById(R.id.signupcheckbox);
      xBack = view.findViewById(R.id.x);
      welcomeText = view.findViewById(R.id.welcome_text);
      enterYourAccount = view.findViewById(R.id.enter_your_account);
      emailEditText = view.findViewById(R.id.email_edit_text);
      passwordEditText = view.findViewById(R.id.password_edit_text);
      forgotPassword = view.findViewById(R.id.forgot_password);
      nextButton = view.findViewById(R.id.next_button);
      repeatPasswordEditText = view.findViewById(R.id.repeat_password_edit_text);
      repeatPasswordEditText.setVisibility(View.GONE);
      
      emailEditText.requestFocus();
      
      forgotPassword.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            ((MainActivity) getActivity()).enterFromRight(new ForgotPasswordFragment("FirstView"));
   
         }
      });
      
      nextButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            
            if (emailEditText.getText().toString().isEmpty() || passwordEditText.getText().toString().isEmpty()) {
               Toast.makeText((getActivity()), "E-mail or Password is empty!", Toast.LENGTH_SHORT).show();
            } else {
   
               View view1 = getActivity().getCurrentFocus();
               if (view1 != null) {
                  InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                  imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);
               }
               
               MainActivity.showLoadingIndicator(view);
               
               Call<HashMap> login = MainActivity.APIBuild().getToken(emailEditText.getText().toString(),
                       passwordEditText.getText().toString());
               login.enqueue(new Callback<HashMap>() {
                  @Override
                  public void onResponse(Call<HashMap> call, Response<HashMap> response) {
                     MainActivity.hideLoadingIndicator(view);
                     
                     if (response.isSuccessful()) {
                        getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).edit().putString(
                                "token", response.headers().get("Authorization")
                        ).apply();
                        Call<Void> addToken = MainActivity.APIBuild().addToken(FirebaseInstanceId.getInstance().getToken() , "Bearer " + response.headers().get("Authorization"));
                        addToken.enqueue(new Callback<Void>() {
                           @Override
                           public void onResponse(Call<Void> call, Response<Void> response) {
         
                           }
      
                           @Override
                           public void onFailure(Call<Void> call, Throwable t) {
         
                           }
                        });
                        ((MainActivity) getActivity()).draw(new MainScreenFragment());
                     } else {
                        try {
                           JSONObject jObjError = new JSONObject(response.errorBody().string());
                           Toast.makeText(getContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                           Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                     }
                  }
                  
                  @Override
                  public void onFailure(Call<HashMap> call, Throwable t) {
                     MainActivity.hideLoadingIndicator(view);
                     Toast.makeText((getActivity()), "Server down!", Toast.LENGTH_SHORT).show();
                     Log.i("SecondLogInFragment", t.getMessage(), t);
                  }
               });
            }
         }
      });
      
      xBack.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
   
            Validations.hideKeyboard(getActivity());
            
            ((MainActivity) getActivity()).enterFromLeft(new MainLogInFragment());
         }
      });
      
      if (getViewType().equals("SignUp")) {
         signUpView();
      }
      
      if (getViewType().equals("Create")) {
         createView(view);
      }
      
      return view;
   }
   
   void createView(final View view) {
      xBack.setBackgroundResource(R.drawable.ic_keyboard_arrow_left_black_24dp);
      repeatPasswordEditText.setVisibility(View.GONE);
      forgotPassword.setVisibility(View.INVISIBLE);
      forgotPassword.setEnabled(false);
      emailEditText.setText("");
      passwordEditText.setText("");
      nextButton.setText("Done");
      welcomeText.setText("Set up your profile");
      enterYourAccount.setText("Enter your name and select a username");
      emailEditText.setHint("Name");
      emailEditText.setInputType(EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES);
      passwordEditText.setHint("Username");
      passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
      
      xBack.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            signUpView();
         }
      });
      signupcheckbox.setChecked(true);
      
      nextButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Validations.hideKeyboard(getActivity());

            if (!signupcheckbox.isChecked()) {
               Toast.makeText((getActivity()), "Agree with terms and conditions to continue", Toast.LENGTH_SHORT).show();
               return;
            }


            if (emailEditText.getText().toString().isEmpty() || passwordEditText.getText().toString().isEmpty()) {
               Toast.makeText((getActivity()), "Make sure there are no empty fields.", Toast.LENGTH_SHORT).show();
            } else {
               MainActivity.showLoadingIndicator(view);
               
               Call<HashMap> login = MainActivity.APIBuild().register(email, password1, password2,
                       emailEditText.getText().toString(), passwordEditText.getText().toString());
               login.enqueue(new Callback<HashMap>() {
                  @Override
                  public void onResponse(Call<HashMap> call, Response<HashMap> response) {
                     MainActivity.hideLoadingIndicator(view);
                     
                     if (response.isSuccessful()) {
                        getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).edit()
                                .putString("token", response.headers().get("Authorization"))
                                .apply();
                        Call<Void> addToken = MainActivity.APIBuild().addToken(FirebaseInstanceId.getInstance().getToken() ,"Bearer " + response.headers().get("Authorization"));
                        addToken.enqueue(new Callback<Void>() {
                           @Override
                           public void onResponse(Call<Void> call, Response<Void> response) {
         
                           }
      
                           @Override
                           public void onFailure(Call<Void> call, Throwable t) {
         
                           }
                        });
                        ((MainActivity) getActivity()).draw(new MainScreenFragment());
                     } else {
                        try {
                           JSONObject jObjError = new JSONObject(response.errorBody().string());
                           Toast.makeText(getContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                           Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                     }
                  }
                  
                  @Override
                  public void onFailure(Call<HashMap> call, Throwable t) {
                     MainActivity.hideLoadingIndicator(view);
                     if (t.getMessage().contains("Use JsonReader.setLenient(true)"))
                        Toast.makeText((getActivity()), "User with this email already exists", Toast.LENGTH_SHORT).show();
                     else
                        Toast.makeText((getActivity()), "Check your internet connection or our server is down!", Toast.LENGTH_SHORT).show();
                  }
               });
            }
         }
      });
   }
   
   void signUpView() {
      xBack.setBackgroundResource(R.drawable.ic_keyboard_arrow_left_black_24dp);
      forgotPassword.setVisibility(View.INVISIBLE);
      repeatPasswordEditText.setVisibility(View.VISIBLE);
      forgotPassword.setEnabled(false);
      nextButton.setText("Create");
      emailEditText.setText("");
      passwordEditText.setText("");
      welcomeText.setText("Create your account");
      emailEditText.setHint("E-mail");
      emailEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
      passwordEditText.setHint("Password");
      passwordEditText.setTextSize(16);
      passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
      repeatPasswordEditText.setTextSize(16);
      repeatPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
      enterYourAccount.setText("Another account… you know the drill :)");
      
      xBack.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Validations.hideKeyboard(getActivity());
            ((MainActivity) getActivity()).enterFromLeft(new MainLogInFragment());
         }
      });
      
      nextButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (!signupcheckbox.isChecked()) {
               Toast.makeText((getActivity()), "Agree with terms and conditions to continue", Toast.LENGTH_SHORT).show();
               return;
            }
            if (emailEditText.getText().toString().isEmpty() || passwordEditText.getText().toString().isEmpty()) {
               Toast.makeText((getActivity()), "E-mail or Password is empty!", Toast.LENGTH_SHORT).show();
            } else if (passwordEditText.getText().toString().length() < 6) {
               Toast.makeText((getActivity()), "The password should contain at least 6 alphanumerics.", Toast.LENGTH_SHORT).show();
            } else if (!Validations.isValidPassword(passwordEditText.getText().toString())) {
               Toast.makeText((getActivity()), "The password should contain at least 1 upper case letter, 1 lower case letter, 1 number and 1 symbol",
                       Toast.LENGTH_SHORT).show();
            } else if (emailEditText.getText().toString().isEmpty() || passwordEditText.getText().toString().isEmpty() ||
                    repeatPasswordEditText.getText().toString().isEmpty()) {
               Toast.makeText((getActivity()), "Make sure there are no empty fields.", Toast.LENGTH_SHORT).show();
            } else if (!Validations.isPasswordMatching(passwordEditText, repeatPasswordEditText)) {
               Toast.makeText((getActivity()), "Passwords do not match.", Toast.LENGTH_SHORT).show();
            } else {
               SecondLogInFragment newFragment = new SecondLogInFragment("Create");
               newFragment.email = emailEditText.getText().toString();
               newFragment.password1 = passwordEditText.getText().toString();
               newFragment.password2 = repeatPasswordEditText.getText().toString();
               
               ((MainActivity) getActivity()).draw(newFragment);
            }
         }
      });
   }
   @Override
   public void onStart() {
      super.onStart();
      
      View view2 = getActivity().getCurrentFocus();
      if (view2 != null) {
         InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
         imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
      }
   }
}
