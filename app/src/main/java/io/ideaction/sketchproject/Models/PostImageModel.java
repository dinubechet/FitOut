package io.ideaction.sketchproject.Models;

import com.google.gson.annotations.SerializedName;

public class PostImageModel {
   
   @SerializedName("id")
   String id;
   
   @SerializedName("pathThumb")
   String imagePathThumb;
   
   @SerializedName("path")
   String imagePath;
   
   public String getId() {
      return id;
   }
   
   public String getImagePathThumb() {
      return imagePathThumb;
   }
   
   public String getImagePath() {
      return imagePath;
   }
}
