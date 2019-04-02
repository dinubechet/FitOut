package io.ideaction.sketchproject.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LikedUser implements Serializable {
   
   @SerializedName("id")
   @Expose
   private Integer id;
   @SerializedName("name")
   @Expose
   private String name;
   @SerializedName("username")
   @Expose
   private String username;
   @SerializedName("email")
   @Expose
   private String email;
   @SerializedName("description")
   @Expose
   private String description;
   @SerializedName("city")
   @Expose
   private String city;
   @SerializedName("country")
   @Expose
   private String country;
   @SerializedName("training_place")
   @Expose
   private String trainingPlace;
   @SerializedName("training_hours")
   @Expose
   private String trainingHours;
   @SerializedName("avatar")
   @Expose
   private String avatar;
   @SerializedName("avatarThumb")
   @Expose
   private String avatarThumb;
   @SerializedName("following")
   @Expose
   private Boolean following;
   
   public Integer getId() {
      return id;
   }
   
   public void setId(Integer id) {
      this.id = id;
   }
   
   public String getName() {
      return name;
   }
   
   public void setName(String name) {
      this.name = name;
   }
   
   public String getUsername() {
      return username;
   }
   
   public void setUsername(String username) {
      this.username = username;
   }
   
   public String getEmail() {
      return email;
   }
   
   public void setEmail(String email) {
      this.email = email;
   }
   
   public String getDescription() {
      return description;
   }
   
   public void setDescription(String description) {
      this.description = description;
   }
   
   public String getCity() {
      return city;
   }
   
   public void setCity(String city) {
      this.city = city;
   }
   
   public String getCountry() {
      return country;
   }
   
   public void setCountry(String country) {
      this.country = country;
   }
   
   public String getTrainingPlace() {
      return trainingPlace;
   }
   
   public void setTrainingPlace(String trainingPlace) {
      this.trainingPlace = trainingPlace;
   }
   
   public String getTrainingHours() {
      return trainingHours;
   }
   
   public void setTrainingHours(String trainingHours) {
      this.trainingHours = trainingHours;
   }
   
   public String getAvatar() {
      return avatar;
   }
   
   public void setAvatar(String avatar) {
      this.avatar = avatar;
   }
   
   public String getAvatarThumb() {
      return avatarThumb;
   }
   
   public void setAvatarThumb(String avatarThumb) {
      this.avatarThumb = avatarThumb;
   }
   
   public Boolean getFollowing() {
      return following;
   }
   
   public void setFollowing(Boolean following) {
      this.following = following;
   }
}
