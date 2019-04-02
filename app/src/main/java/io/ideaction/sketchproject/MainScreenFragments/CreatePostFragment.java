package io.ideaction.sketchproject.MainScreenFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.google.firebase.database.core.Context;

import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
import com.zfdang.multiple_images_selector.SelectorSettings;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.Activities.MediaPlayerAc;
import io.ideaction.sketchproject.Activities.SportActivity;
import io.ideaction.sketchproject.Adapters.GridImgAdapter;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.FileUtils;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.Util;
import io.ideaction.sketchproject.Models.BannerModer;
import io.ideaction.sketchproject.Models.Image;
import io.ideaction.sketchproject.Models.SportFeedModel;
import io.ideaction.sketchproject.R;
import io.ideaction.sketchproject.TrimmerActivity;
import io.ideaction.sketchproject.Validations.Validations;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

@SuppressLint("ValidFragment")
public class CreatePostFragment extends Fragment {
   
   ImageView backButton;
   RelativeLayout postButton;
   EditText postMessage;
   RelativeLayout addMedia;
   List<File> imgList = new ArrayList<>();
   int feedCategory = 1;
   RelativeLayout add_video;
   File videoFile;
   GridView girdView;
   SportFeedModel post;

   boolean edit = false;

   View mView;




   public CreatePostFragment(SportFeedModel post) {
      this.post = post;
      edit = true;
   }

   public CreatePostFragment(int feedCategory) {
      this.feedCategory = feedCategory;
   }
   
   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      final View view = inflater.inflate(R.layout.create_post_layout, container, false);




      mView = view;
      add_video = view.findViewById(R.id.add_video);
      add_video.setOnClickListener(v -> {
         Validations.hideKeyboard(getActivity());
         if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);

         } else {

            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Video"),11);

         }



      });
      girdView = view.findViewById(R.id.girdView);
      backButton = view.findViewById(R.id.back_button);
      postButton = view.findViewById(R.id.post_button);
      postMessage = view.findViewById(R.id.message_edit_text);
      addMedia = view.findViewById(R.id.add_media);
      postMessage.requestFocus();

      if (getActivity() instanceof  SportActivity)
      ((SportActivity)getActivity()).setFragmentDrawer("addPost");

      addMedia.setOnClickListener(v -> openGallery());

      postButton.setOnClickListener(v -> {
         if (postMessage.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "The post body is empty.", Toast.LENGTH_SHORT).show();
         } else {
            postRequest(view, feedCategory);
         }
      });
      
      backButton.setOnClickListener(v -> {
         Validations.hideKeyboard(getActivity());
         getActivity().onBackPressed();
      });


      if (edit) {
         postMessage.setText(post.getText());
         postMessage.setSelection(post.getText().length());
         List<BannerModer> list = new ArrayList<>();
         for (Image x : post
                 .getImages())
            list.add(new BannerModer(x.getPath(), false, ""));

         if (post.getVideo().size() > 0)
            list.add(new BannerModer(post.getVideo().get(0).getPath(), true, post.getVideo().get(0).getThumb()));

         girdView.setAdapter(new GridImgAdapter(list , getActivity()));

      }

      return view;
   }
   
   private void postRequest(final View view, int feedCategory) {


      if (edit) {

         MainActivity.showLoadingIndicator(view);
         List<MultipartBody.Part> files = new ArrayList<>();
         if (!imgList.isEmpty()) {
            for (int index = 0; index < imgList.size(); index++) {
               MultipartBody.Part filePart = MultipartBody.Part.createFormData("images[" + index + "]",
                       imgList.get(index).getName(), RequestBody.create(MediaType.parse("image/*"), imgList.get(index)));
               files.add(filePart);
            }
         }

         if (videoFile != null) {
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("videos[0]",
                    videoFile.getName(),  RequestBody.create(MediaType.parse("video/mp4"), videoFile));
            files.add(filePart);

         }

         Call<HashMap> call = MainActivity.APIBuild().updatePost(RequestBody.create(MediaType.parse("text/plain"),
                 postMessage.getText().toString()),
                 files, "Bearer " + getActivity().getSharedPreferences("my", MODE_PRIVATE).getString("token", "") , post.getId()+"");
         call.enqueue(new Callback<HashMap>() {
            @Override
            public void onResponse(Call<HashMap> call, Response<HashMap> response) {
               MainActivity.hideLoadingIndicator(view);
               if (response.isSuccessful()) {
                  Validations.hideKeyboard(getActivity());
                  getActivity().onBackPressed();
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
            public void onFailure(Call<HashMap> call, Throwable t) {
               Validations.hideKeyboard(getActivity());
               MainActivity.hideLoadingIndicator(view);
            }
         });
         return;
      }

      MainActivity.showLoadingIndicator(view);

      List<MultipartBody.Part> files = new ArrayList<>();
      if (!imgList.isEmpty()) {
         for (int index = 0; index < imgList.size(); index++) {
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("images[" + index + "]",
                    imgList.get(index).getName(), RequestBody.create(MediaType.parse("image/*"), imgList.get(index)));
            files.add(filePart);
         }
      }

      if (videoFile != null) {
         MultipartBody.Part filePart = MultipartBody.Part.createFormData("videos[0]",
                 videoFile.getName(),  RequestBody.create(MediaType.parse("video/mp4"), videoFile));
         files.add(filePart);

      }
      
      Call<HashMap> call = MainActivity.APIBuild().createNewPost(RequestBody.create(MediaType.parse("text/plain"),
              postMessage.getText().toString()),RequestBody.create(MediaType.parse("text/plain"), String.valueOf(feedCategory)),
              files, "Bearer " + getActivity().getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
      call.enqueue(new Callback<HashMap>() {
         @Override
         public void onResponse(Call<HashMap> call, Response<HashMap> response) {
            MainActivity.hideLoadingIndicator(view);
            if (response.isSuccessful()) {
               Validations.hideKeyboard(getActivity());
               getActivity().onBackPressed();
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
         public void onFailure(Call<HashMap> call, Throwable t) {
            Validations.hideKeyboard(getActivity());
            MainActivity.hideLoadingIndicator(view);
         }
      });
   }
   
   @Override
   public void onStart() {
      super.onStart();
   }
   private static final int REQUEST_CODE = 123;
   private ArrayList<String> mResults = new ArrayList<>();
   
   private void openGallery() {
      Validations.hideKeyboard(getActivity());
      if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
         ActivityCompat.requestPermissions(getActivity(),
                 new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
         
      } else {
         Intent intent = new Intent(getActivity(), ImagesSelectorActivity.class);
         intent.putExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, 4);
         intent.putExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, 100000);
         intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, true);
         intent.putStringArrayListExtra(SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST, mResults);
         startActivityForResult(intent, REQUEST_CODE);
      }
      
   }



   
   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      if (requestCode == 2) {
         if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
         } else {
            Toast.makeText(getActivity(), "Permission DENIED", Toast.LENGTH_SHORT).show();
         }
      }
   }
   
   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);


      if(requestCode == REQUEST_CODE) {
         if(resultCode == RESULT_OK) {
            List<BannerModer> newList = new ArrayList<>();
            mResults = data.getStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS);
            assert mResults != null;
            imgList.clear();
            for(String result : mResults) {
               try {
                  imgList.add(Util.getCompressed(getActivity() , result , 700 , 700));
                  newList.add(new BannerModer(result , false , result));

               } catch (IOException e) {
                  e.printStackTrace();
               }
            }


            if (newList.size() == 0 && edit) {
               for (Image x : post
                       .getImages())
                  newList.add(new BannerModer(x.getPath(), false, x.getPathThumb()));
            }


            if (videoFile != null)
               newList.add(new BannerModer(videoFile.getAbsolutePath() , true , ""));
            else
            if (edit && post.getVideo().size() > 0)
               newList.add(new BannerModer(post.getVideo().get(0).getPath(), true, post.getVideo().get(0).getThumb()));

            girdView.setAdapter(new GridImgAdapter(newList , getActivity()));

         }
      }



      if (requestCode == 11 && resultCode == RESULT_OK) {
         Uri selectedImageUri = data.getData();
         startTrimActivity(selectedImageUri);

      }



      if (requestCode == 22 && resultCode == RESULT_OK) {



         videoFile = new File(data.getStringExtra("path"));
         imgList.clear();
         List<BannerModer> newList = new ArrayList<>();
         assert mResults != null;
         for(String result : mResults) {
            imgList.add(new File(result));
            newList.add(new BannerModer(result , false , ""));
         }

         if (newList.size() == 0 && edit) {
            for (Image x : post
                    .getImages())
               newList.add(new BannerModer(x.getPath(), false, ""));
         }

         newList.add(new BannerModer(videoFile.getPath() , true , ""));
         girdView.setAdapter(new GridImgAdapter(newList , getActivity()));

      }
   }

   private void startTrimActivity(@NonNull Uri uri) {
      Intent intent = new Intent(getActivity(), TrimmerActivity.class);
      intent.putExtra("path", FileUtils.getPath(getActivity(), uri));
      startActivityForResult(intent , 22);
   }






}




