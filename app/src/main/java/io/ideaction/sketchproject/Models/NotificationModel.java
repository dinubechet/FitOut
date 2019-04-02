package io.ideaction.sketchproject.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationModel {
      
      @SerializedName("id")
      @Expose
      private Integer id;
      @SerializedName("text")
      @Expose
      private String text;
      @SerializedName("type")
      @Expose
      private Integer type;
      @SerializedName("user")
      @Expose
      private User user;
      @SerializedName("model_id")
      @Expose
      private Integer modelId;
      @SerializedName("viewed")
      @Expose
      private Boolean viewed;
      @SerializedName("created_at")
      @Expose
      private String createdAt;
      @SerializedName("created_at_forHumans")
      @Expose
      private String createdAtForHumans;
      
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
      
      public Integer getType() {
         return type;
      }
      
      public void setType(Integer type) {
         this.type = type;
      }
      
      public User getUser() {
         return user;
      }
      
      public void setUser(User user) {
         this.user = user;
      }
      
      public Integer getModelId() {
         return modelId;
      }
      
      public void setModelId(Integer modelId) {
         this.modelId = modelId;
      }
      
      public Boolean getViewed() {
         return viewed;
      }
      
      public void setViewed(Boolean viewed) {
         this.viewed = viewed;
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
