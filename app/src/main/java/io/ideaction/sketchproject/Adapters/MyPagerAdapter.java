package io.ideaction.sketchproject.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.ideaction.sketchproject.MainScreenFragments.HomeScreenFragment;
import io.ideaction.sketchproject.MainScreenFragments.MessageScreenFragment;

public  class MyPagerAdapter extends FragmentPagerAdapter {

   
   public MyPagerAdapter(FragmentManager fragmentManager) {
      super(fragmentManager);
   }
   
   @Override
   public int getCount() {
      return 2;
   }
   
   @Override
   public Fragment getItem(int position) {
      switch (position) {
         case 0:
            return new HomeScreenFragment();
         case 1:
            return new MessageScreenFragment();
         default:
            return null;
      }
   }
   @Override
   public CharSequence getPageTitle(int position) {
      return "Page " + position;
   }
   
}