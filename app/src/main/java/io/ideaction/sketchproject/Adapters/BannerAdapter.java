package io.ideaction.sketchproject.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import io.ideaction.sketchproject.BannerFragment;
import io.ideaction.sketchproject.Models.BannerModer;

public class BannerAdapter extends FragmentPagerAdapter {
   
   
   
   
   
   List<BannerModer> list;
   
   
   public BannerAdapter(FragmentManager fm, List<BannerModer> list) {
      super(fm);
      this.list = list;
   }
   
   @Override
   public int getCount() {
      return list.size();
   }
   
   @Override
   public Fragment getItem(int position) {
      
      return  new BannerFragment(list.get(position));
      
   }
   
   @Override
   public CharSequence getPageTitle(int position) {
      return "Page " + position;
   }
   
   @Override
   public int getItemPosition(Object object) {
      return POSITION_NONE;
   }
   
}
