package io.ideaction.sketchproject;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import io.ideaction.sketchproject.ApiRequests.UtilityClasses.Util;
import io.ideaction.sketchproject.Models.BannerModer;


@SuppressLint("ValidFragment")
public class BannerFragment extends Fragment {
   BannerModer bannerModel;
   
   
   public BannerFragment(BannerModer bannerModel) {
      this.bannerModel = bannerModel;
   }
   
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_banner, container, false);
      ImageView img = view.findViewById(R.id.img);
     
   
      if (bannerModel.getFile() != null)
      Picasso.get().load(bannerModel.getFile()).fit().centerCrop().transform(Util.cropPosterTransformation).placeholder(R.drawable.default_image).into(img);
      else
         Picasso.get().load(bannerModel.getUrl()).fit().centerCrop().transform(Util.cropPosterTransformation).placeholder(R.drawable.default_image).into(img);
      return view;
   }
   
   @Override
   public void setUserVisibleHint(boolean isVisibleToUser) {
      super.setUserVisibleHint(isVisibleToUser);

      if (isVisibleToUser) {
         Fragment currentFragment = BannerFragment.this;
         FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
         fragmentTransaction.detach(currentFragment);
         fragmentTransaction.attach(currentFragment);
         fragmentTransaction.commit();
      }
   }
   
   
 
}