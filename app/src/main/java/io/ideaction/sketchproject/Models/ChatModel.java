package io.ideaction.sketchproject.Models;

public class ChatModel {
   private String message;
   private boolean owner;
   
   public ChatModel(String message, boolean owner) {
      this.message = message;
      this.owner = owner;
   }
   
   public String getMessage() {
      return message;
   }
   
   public void setMessage(String message) {
      this.message = message;
   }
   
   public boolean isOwner() {
      return owner;
   }
   
   public void setOwner(boolean owner) {
      this.owner = owner;
   }
}
