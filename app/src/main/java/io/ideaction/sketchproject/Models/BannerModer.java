package io.ideaction.sketchproject.Models;

import android.net.Uri;

import java.io.File;

public class BannerModer {
   Uri file;
   
   String url;
   String videoThimb;

   boolean video = false;

   public BannerModer(String url, boolean video , String videoThimb) {
      this.url = url;
      this.video = video;
      this.videoThimb = videoThimb;
   }

   public String getVideoThimb() {
      return videoThimb;
   }

   public boolean isVideo() {
      return video;
   }

   public BannerModer(Uri file) {
      this.file = file;
   }
   
   public BannerModer(String url) {
      this.url = url;
   }
   
   public Uri getFile() {
      return file;
   }
   
   public String getUrl() {
      return url;
   }
}
