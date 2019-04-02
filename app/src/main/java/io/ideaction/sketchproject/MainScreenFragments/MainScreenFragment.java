package io.ideaction.sketchproject.MainScreenFragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import io.ideaction.sketchproject.Adapters.MyPagerAdapter;
import io.ideaction.sketchproject.MyFirebaseMessagingService;
import io.ideaction.sketchproject.R;

public class MainScreenFragment extends Fragment {
   ViewPager viewPager;
   LinearLayout homeIndicator;
   LinearLayout messageIndicator;
   RelativeLayout homeButton;
   RelativeLayout messageButton;
   BroadcastReceiver broadcastReceiver2;
   ImageView messageImg;

   boolean x = false;

   public MainScreenFragment() {
   }

   @SuppressLint("ValidFragment")
   public MainScreenFragment(boolean x) {
      this.x = x;


   }

   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_main, container, false);
      viewPager = view.findViewById(R.id.pageView);
      messageImg = view.findViewById(R.id.messageImg);
      homeIndicator = view.findViewById(R.id.home_indicator);
      messageIndicator = view.findViewById(R.id.message_indicator);
      homeButton = view.findViewById(R.id.home);
      messageButton = view.findViewById(R.id.messages);
      viewPager.setAdapter(new MyPagerAdapter(getFragmentManager()));
      
      viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         @Override
         public void onPageScrolled(int i, float v, int i1) {
         }
         
         @Override
         public void onPageSelected(int i) {
            switch (i) {
               case 0:
                  homeIndicator.setVisibility(View.VISIBLE);
                  messageIndicator.setVisibility(View.INVISIBLE);
                  break;
               case 1:
                  homeIndicator.setVisibility(View.INVISIBLE);
                  messageIndicator.setVisibility(View.VISIBLE);
                  messageImg.setImageResource(R.drawable.message);
                  break;
               default:
                  break;
            }
         }
         
         @Override
         public void onPageScrollStateChanged(int i) {
         }
      });
      homeButton.setOnClickListener(v -> viewPager.setCurrentItem(0, true));
      messageButton.setOnClickListener(v -> viewPager.setCurrentItem(1, true));
      
      IntentFilter intentFilter2 = new IntentFilter(MyFirebaseMessagingService.MESSAGE);
      broadcastReceiver2 = new BroadcastReceiver() {
         @Override
         public void onReceive(Context context, Intent intent) {
            messageImg.setImageResource(R.drawable.message_not);

         }
      };

      getActivity().registerReceiver(broadcastReceiver2 , intentFilter2);


      if (x)
         messageButton.callOnClick();

      return view;
   }
}
