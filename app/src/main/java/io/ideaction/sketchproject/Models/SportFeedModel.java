package io.ideaction.sketchproject.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SportFeedModel implements Serializable {
   @SerializedName("id")
   @Expose
   private Integer id;
   @SerializedName("text")
   @Expose
   private String text;
   @SerializedName("images")
   @Expose
   private List<Image> images = null;
   @SerializedName("videos")
   @Expose
   private List<Image> video = null;
   @SerializedName("likes")
   @Expose
   private Integer likes;
   @SerializedName("liked")
   @Expose
   private Boolean liked;
   @SerializedName("likedUsers")
   @Expose
   private List<LikedUser> likedUsers = null;
   @SerializedName("lastComment")
   @Expose
   private LastCommentModel lastComment;
   @SerializedName("commentsCount")
   @Expose
   private Integer commentsCount;
   @SerializedName("user")
   @Expose
   private User user;
   @SerializedName("created_at")
   @Expose
   private String createdAt;
   
   public List<Image> getVideo() {
      return video;
   }
   public Integer getId() {
      return id;
   }
   
   public void setId(Integer id) {
      this.id = id;
   }
   
   public String getText() {
      return text;
   }
   
   public void setText(String text) {
      this.text = text;
   }
   
   public List<Image> getImages() {
      return images;
   }
   
   public void setImages(List<Image> images) {
      this.images = images;
   }
   
   public Integer getLikes() {
      return likes;
   }
   
   public void setLikes(Integer likes) {
      this.likes = likes;
   }
   
   public Boolean getLiked() {
      return liked;
   }
   
   public void setLiked(Boolean liked) {
      this.liked = liked;
   }
   
   public List<LikedUser> getLikedUsers() {
      return likedUsers;
   }
   
   public void setLikedUsers(List<LikedUser> likedUsers) {
      this.likedUsers = likedUsers;
   }
   
   public LastCommentModel getLastComment() {
      return lastComment;
   }
   
   public void setLastComment(LastCommentModel lastComment) {
      this.lastComment = lastComment;
   }
   
   public Integer getCommentsCount() {
      return commentsCount;
   }
   
   public void setCommentsCount(Integer commentsCount) {
      this.commentsCount = commentsCount;
   }
   
   public User getUser() {
      return user;
   }
   
   public void setUser(User user) {
      this.user = user;
   }
   
   public String getCreatedAt() {
      return createdAt;
   }
   
   public void setCreatedAt(String createdAt) {
      this.createdAt = createdAt;
   }
}
