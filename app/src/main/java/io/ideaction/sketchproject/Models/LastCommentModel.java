package io.ideaction.sketchproject.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LastCommentModel implements Serializable {
   
   @SerializedName("id")
   private String id;
   
   @SerializedName("message")
   private String message;
   
   @SerializedName("user")
   private User user;
   
   @SerializedName("created_at")
   private String created_at;
   
   public String getId() {
      return id;
   }
   
   public String getMessage() {
      return message;
   }
   
   public User getUser() {
      return user;
   }
   
   public String getCreated_at() {
      return created_at;
   }
}
