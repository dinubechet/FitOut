package io.ideaction.sketchproject.LogInFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;

import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.ImageFilePath;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.Util;
import io.ideaction.sketchproject.MainScreenFragments.MainScreenFragment;
import io.ideaction.sketchproject.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainLogInFragment extends Fragment {
   
   private static final String EMAIL = "email";
   private static final String PROFILE = "public_profile";
   TextView title;
   TextView description1;
   TextView description2;
   Button logIn;
   Button loginButton2;
   LoginButton loginButton;
   Button signUp;
   String email, name;
   SharedPreferences sharedPreferences;
   CallbackManager callbackManager = CallbackManager.Factory.create();

   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      final View view = inflater.inflate(R.layout.log_in_fragment, container, false);
      title = view.findViewById(R.id.title);
      description1 = view.findViewById(R.id.description1);
      description2 = view.findViewById(R.id.description2);
      loginButton2 = view.findViewById(R.id.login_button2);
      loginButton = view.findViewById(R.id.login_button);
      loginButton.setReadPermissions(Arrays.asList(EMAIL, PROFILE));
      loginButton.setFragment(this);
      logIn = view.findViewById(R.id.log_in);
      signUp = view.findViewById(R.id.sine_up_button);
      sharedPreferences = getActivity().getSharedPreferences("my", Context.MODE_PRIVATE);


      description2.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://ec2-18-223-224-68.us-east-2.compute.amazonaws.com/privacy_policy.html"));
            startActivity(browserIntent);
         }
      });

      loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
         @Override
         public void onSuccess(final LoginResult loginResult) {
            MainActivity.showLoadingIndicator(view);
            GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
               @Override
               public void onCompleted(JSONObject object, GraphResponse response) {
                  Log.d("JSON", "" + response.getJSONObject().toString());
                  try {
                     email = object.getString("email");
                     name = object.getString("name");
                     
                     sharedPreferences.edit().putString("img", "https://graph.facebook.com/" + object.getString("id") + "/picture?type=large").apply();
                     sharedPreferences.edit().putString("name", name).apply();
                     
                     Call<HashMap> login = MainActivity.APIBuild().social_auth(name, email, "facebook", object.getString("id"));
                     login.enqueue(new Callback<HashMap>() {
                        @Override
                        public void onResponse(Call<HashMap> call, Response<HashMap> response) {
                           if (response.isSuccessful()) {
                              getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).edit().putString(
                                      "token", response.headers().get("Authorization")
                              ).apply();
                              Call<Void> addToken = MainActivity.APIBuild().addToken(FirebaseInstanceId.getInstance().getToken(), "Bearer " + response.headers().get("Authorization"));
                              addToken.enqueue(new Callback<Void>() {
                                 @Override
                                 public void onResponse(Call<Void> call, Response<Void> response) {
                                 }
                                 @Override
                                 public void onFailure(Call<Void> call, Throwable t) {
                                 }
                              });
                              new Thread(new Runnable() {
                                 @Override
                                 public void run() {
                                    File f = new File(getActivity().getCacheDir(), "img");
                                    try {
                                       f.createNewFile();
                                       Bitmap bitmap = getBitmapFromURL(getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("img", ""));
                                       ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                       bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                                       byte[] bitmapdata = bos.toByteArray();
                                       FileOutputStream fos = new FileOutputStream(f);
                                       fos.write(bitmapdata);
                                       fos.flush();
                                       fos.close();
                                       MultipartBody.Part filePart = MultipartBody.Part.createFormData("avatar", f.getName(), RequestBody.create(MediaType.parse("image/*"), f));
                                       Call<HashMap> call2 = MainActivity.APIBuild().upload(filePart, "Bearer " +
                                               getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));
                                       call2.enqueue(new Callback<HashMap>() {
                                          @Override
                                          public void onResponse(Call<HashMap> call, Response<HashMap> response) {
                                             MainActivity.hideLoadingIndicator(view);
                                             ((MainActivity) getActivity()).draw(new MainScreenFragment());
                                          }
                                          @Override
                                          public void onFailure(Call<HashMap> call, Throwable t) {
                                             MainActivity.hideLoadingIndicator(view);
                                          }
                                       });
                                    } catch (IOException e) {
                                       e.printStackTrace();
                                    }
                                 }
                              }).start();
                           } else {
                              MainActivity.hideLoadingIndicator(view);
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
                           Toast.makeText((getActivity()), "Check your internet connection or our server is down!", Toast.LENGTH_SHORT).show();
                        }
                     });
                     LoginManager.getInstance().logOut();
                  } catch (JSONException e) {
                     e.printStackTrace();
                     MainActivity.hideLoadingIndicator(view);
                  }
               }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,first_name,last_name,email");
            graphRequest.setParameters(parameters);
            graphRequest.executeAsync();
         }
         
         @Override
         public void onCancel() {
         }
         
         @Override
         public void onError(FacebookException exception) {
         }
      });
      
      loginButton2.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            loginButton.callOnClick();
         }
      });
      
      logIn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            ((MainActivity) getActivity()).enterFromRight(new SecondLogInFragment("LogIn"));
         }
      });
      
      signUp.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            ((MainActivity) getActivity()).draw(new SecondLogInFragment("SignUp"));
         }
      });
      
      return view;
   }
   
   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      callbackManager.onActivityResult(requestCode, resultCode, data);
      super.onActivityResult(requestCode, resultCode, data);
   }
   
   public Bitmap getBitmapFromURL(String src) {
      try {
         java.net.URL url = new java.net.URL(src);
         HttpURLConnection connection = (HttpURLConnection) url
                 .openConnection();
         connection.setDoInput(true);
         connection.connect();
         InputStream input = connection.getInputStream();
         Bitmap myBitmap = BitmapFactory.decodeStream(input);
         return myBitmap;
      } catch (IOException e) {
         e.printStackTrace();
         return null;
      }
   }
}
