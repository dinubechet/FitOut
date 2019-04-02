package io.ideaction.sketchproject.MainScreenFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.ideaction.sketchproject.Adapters.NotificationsAdapter;
import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.Models.NotificationModel;
import io.ideaction.sketchproject.R;
import io.ideaction.sketchproject.Validations.Validations;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsScreenFragment extends Fragment {
   
   Button backButton;
   ListView notificationListView;
   NotificationsAdapter adapter;
   TextView noContent;
   
   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.notifications_layout,container,false);
      
      backButton = view.findViewById(R.id.back_arrow);
      notificationListView = view.findViewById(R.id.notification_list_view);
      adapter = new NotificationsAdapter(getActivity());
      notificationListView.setAdapter(adapter);
      noContent = view.findViewById(R.id.no_content);
      callNotificationList(view);
      
      backButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            ((MainActivity)getActivity()).enterFromLeft(new MainScreenFragment());
         }
      });
      
      return view;
   }
   
   void callNotificationList(final View view){
      MainActivity.showLoadingIndicator(view);
      Call<List<NotificationModel>> callNotifications = MainActivity.APIBuild().getNotifications("Bearer " +
              getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));
      callNotifications.enqueue(new Callback<List<NotificationModel>>() {
         @Override
         public void onResponse(Call<List<NotificationModel>> call, Response<List<NotificationModel>> response) {
            MainActivity.hideLoadingIndicator(view);
            adapter.setNotificationList(response.body());
            adapter.notifyDataSetChanged();
               if(adapter.getCount() == 0){
                  noContent.setVisibility(View.VISIBLE);
               } else {
                  noContent.setVisibility(View.GONE);
               }
         }
      
         @Override
         public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
            MainActivity.hideLoadingIndicator(view);
            Toast.makeText(getActivity(),"Your internet connection is bad or our server is down",Toast.LENGTH_SHORT).show();
         }
      });
   }
}
