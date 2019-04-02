package io.ideaction.sketchproject.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentModel {
   @SerializedName("id")
   @Expose
   private Integer id;
   @SerializedName("message")
   @Expose
   private String message;
   @SerializedName("model_type")
   @Expose
   private String modelType;
   @SerializedName("model_id")
   @Expose
   private Integer modelId;
   @SerializedName("user")
   @Expose
   private User user;
   @SerializedName("created_at")
   @Expose
   private String createdAt;
   
   public CommentModel(String message, User user, String createdAtForHumans) {
      this.message = message;
      this.user = user;
      this.createdAtForHumans = createdAtForHumans;
   }
   
   @SerializedName("created_at_forHumans")
   @Expose
   
   
   
   private String createdAtForHumans;
   
   public Integer getId() {
      return id;
   }
   
   public void setId(Integer id) {
      this.id = id;
   }
   
   public String getMessage() {
      return message;
   }
   
   public void setMessage(String message) {
      this.message = message;
   }
   
   public String getModelType() {
      return modelType;
   }
   
   public void setModelType(String modelType) {
      this.modelType = modelType;
   }
   
   public Integer getModelId() {
      return modelId;
   }
   
   public void setModelId(Integer modelId) {
      this.modelId = modelId;
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
   
   public String getCreatedAtForHumans() {
      return createdAtForHumans;
   }
   
   public void setCreatedAtForHumans(String createdAtForHumans) {
      this.createdAtForHumans = createdAtForHumans;
   }
}
