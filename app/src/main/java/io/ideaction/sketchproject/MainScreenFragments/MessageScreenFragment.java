package io.ideaction.sketchproject.MainScreenFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.Adapters.MessagesAdapter;
import io.ideaction.sketchproject.R;

public class MessageScreenFragment extends Fragment {
   
   ListView messagesListView;
   SharedPreferences sharedPreferences;
   TextView noContent;
   MessagesAdapter adapter;
   List<HashMap> chatList =  new ArrayList<>();
   
   
   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.message_fragment, container, false);
      
      messagesListView = view.findViewById(R.id.messages_list_view);
      adapter = new MessagesAdapter(getActivity());
      messagesListView.setAdapter(adapter);
      noContent = view.findViewById(R.id.no_content);
      sharedPreferences = getActivity().getSharedPreferences("my" , Context.MODE_PRIVATE);
      return view;
   }
   @Override
   public void setUserVisibleHint(boolean isVisibleToUser) {
      super.setUserVisibleHint(isVisibleToUser);

      if (!isVisibleToUser) {
         Fragment currentFragment = MessageScreenFragment.this;
         FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
         fragmentTransaction.detach(currentFragment);
         fragmentTransaction.attach(currentFragment);
         fragmentTransaction.commit();
      }
   }
   @Override
   public void onResume() {
      super.onResume();
      fetch();
     
   }
   
   void fetch() {
      FirebaseDatabase database = FirebaseDatabase.getInstance();
      DatabaseReference myRef = database.getReference("message");
      myRef.addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
            chatList.clear();
            HashMap chats = (HashMap) dataSnapshot.getValue();
            for (final Object entry : chats.keySet()) {
               HashMap chat = (HashMap) chats.get(entry);
               if (chat.get("participants").toString().split(",")[0].equals(sharedPreferences.getString("email" , "xxx")))
                  chatList.add(chat);
               else
               if (chat.get("participants").toString().split(",")[1].equals(sharedPreferences.getString("email" , "xxx")))
                  chatList.add(chat);
            }
            
            Collections.sort(chatList, new Comparator<HashMap>() {
               public int compare(HashMap obj1, HashMap obj2) {
                  if (obj1.get("lastMessageTime").toString().equals(""))
                     return 0;
                  if (obj2.get("lastMessageTime").toString().equals(""))
                     return 0;
                  return (Double.valueOf(obj2.get("lastMessageTime").toString()).compareTo(Double.valueOf(obj1.get("lastMessageTime").toString())));
               }
            });
            
            adapter.setList(chatList);
            if(adapter.getCount() == 0){
               noContent.setVisibility(View.VISIBLE);
            } else {
               noContent.setVisibility(View.GONE);
            }
         }
         
         @Override
         public void onCancelled(DatabaseError error) {
         
         }
      });
      
      
   }
   
   
   
}
