package io.ideaction.sketchproject.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EventModel implements Serializable {

   @SerializedName("category_id")
   private String category_id;

   public String getCategory_id() {
      return category_id;
   }

   public void setCategory_id(String category_id) {
      this.category_id = category_id;
   }

   @SerializedName("id")
   private String id;
   
   @SerializedName("name")
   private String name;
   
   @SerializedName("description")
   private String description;
   
   @SerializedName("when")
   private String when;
   
   @SerializedName("whenForHumans")
   private String whenForHumans;

   @SerializedName("whenTimestamp")
   long whenTimestamp;

   public long getWhenTimestamp() {
      return whenTimestamp;
   }

   @SerializedName("created_at_forHumans")
   private String created_at_forHumans;
   
   @SerializedName("countAttendedUsers")
   private String countAttendedUsers;
   
   @SerializedName("attend")
   private boolean attend;
   
   @SerializedName("attendedUsers")
   private List<User> attendedUsers;
   
   @SerializedName("lastComment")
   private LastCommentModel lastComment;
   
   @SerializedName("commentsCount")
   private String commentsCount;
   
   @SerializedName("saved")
   private boolean saved;
   
   @SerializedName("user")
   private User user;
   
   @SerializedName("created_at")
   private String created_at;
   
   @SerializedName("address")
   private String address;
   
   public void setAttend(boolean attend) {
      this.attend = attend;
   }
   
   public void setSaved(boolean saved) {
      this.saved = saved;
   }
   
   public String getWhenForHumans() {
      return whenForHumans;
   }
   
   public String getCreated_at_forHumans() {
      return created_at_forHumans;
   }
   
   public String getAddress() {
      return address;
   }
   
   public String getWhen() {
      return when;
   }
   
   public String getId() {
      return id;
   }
   
   public String getName() {
      return name;
   }
   
   public String getDescription() {
      return description;
   }
   
   public String getCountAttendedUsers() {
      return countAttendedUsers;
   }
   
   public boolean getAttend() {
      return attend;
   }
   
   public List<User> getAttendedUsers() {
      return attendedUsers;
   }
   
   public LastCommentModel getLastComment() {
      return lastComment;
   }
   
   public String getCommentsCount() {
      return commentsCount;
   }
   
   public boolean getSaved() {
      return saved;
   }
   
   public User getUser() {
      return user;
   }
   
   public String getCreated_at() {
      return created_at;
   }
}
