package io.ideaction.sketchproject.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Image implements Serializable {
   
   @SerializedName("id")
   @Expose
   private Integer id;
   @SerializedName("pathThumb")
   @Expose
   private String pathThumb;
   @SerializedName("thumb")
   @Expose
   private String thumb;
   @SerializedName("path")
   @Expose
   private String path;

   public String getThumb() {
      return thumb;
   }

   public Integer getId() {
      return id;
   }
   
   public void setId(Integer id) {
      this.id = id;
   }
   
   public String getPathThumb() {
      return pathThumb;
   }
   
   public void setPathThumb(String pathThumb) {
      this.pathThumb = pathThumb;
   }
   
   public String getPath() {
      return path;
   }
   
   public void setPath(String path) {
      this.path = path;
   }
   
   
}
