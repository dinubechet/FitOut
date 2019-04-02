package io.ideaction.sketchproject;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
   public static final String STRTRTR = "io.ideaction.sketchproject.Activities.MainActivity";
  public static final String MESSAGE = "io.ideaction.sketchproject.Activities.MainActivityx";


 @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
      if (remoteMessage.getData().get("message") != null)
     if (remoteMessage.getData().get("message").toString().equals("true")) {
      Intent intent = new Intent();
      intent.setAction(MESSAGE);
      sendBroadcast(intent);
      return;
     }

    Intent intent = new Intent();
    intent.setAction(STRTRTR);
    sendBroadcast(intent);
    }

}
