package io.ideaction.sketchproject.Models;

import com.google.gson.annotations.SerializedName;

public class LikedPostsModel {
   
   @SerializedName("id")
   String id;
   
   @SerializedName("name")
   String name;
   
   @SerializedName("text")
   String text;
   
   @SerializedName("likes")
   String likes;
   
   @SerializedName("liked")
   String liked;
   
   @SerializedName("images")
   String images;
   
   @SerializedName("created_at")
   String created_at;
   
   public String getId() {
      return id;
   }
   
   public String getName() {
      return name;
   }
   
   public String getText() {
      return text;
   }
   
   public String getLikes() {
      return likes;
   }
   
   public String getLiked() {
      return liked;
   }
   
   public String getImages() {
      return images;
   }
   
   public String getCreated_at() {
      return created_at;
   }
}
