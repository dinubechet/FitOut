package io.ideaction.sketchproject.Models;

import com.google.gson.annotations.SerializedName;

public class UserModel {
   @SerializedName("name")
   String name;
   @SerializedName("avatarThumb")
   String avatar;
   
   public UserModel(String name, String avatar) {
      this.name = name;
      this.avatar = avatar;
   }
   
   public String getName() {
      return name;
   }
   
   public String getAvatar() {
      return avatar;
   }
}
