package io.ideaction.sketchproject.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

import io.ideaction.sketchproject.Adapters.EventAdapter;
import io.ideaction.sketchproject.Adapters.ForeignEventAdapter;
import io.ideaction.sketchproject.Models.EventModel;
import io.ideaction.sketchproject.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForeignEventsActivity extends AppCompatActivity {
   
   
   RelativeLayout attendingButton;
   RelativeLayout savedButton;
   RelativeLayout hostedButton;
   RelativeLayout backButton;
   LinearLayout attendingIndicator;
   LinearLayout savedIndicator;
   LinearLayout hostedIndicator;
   TextView noContent;
   ListView eventsListView;
   String fragmentDrawer = "";
   int page = 1;
   int id = 0;
   
   ForeignEventAdapter adapter;
   public void setFragmentDrawer(String fragmentDrawer) {
      this.fragmentDrawer = fragmentDrawer;
   }
   
   public RelativeLayout getAttendingButton() {
      return attendingButton;
   }
   
   public RelativeLayout getSavedButton() {
      return savedButton;
   }
   
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_foreign_events);
      
      attendingButton = findViewById(R.id.attending);
      savedButton = findViewById(R.id.saved);
      hostedButton = findViewById(R.id.hosted);
      backButton = findViewById(R.id.back_arrow);
      attendingIndicator = findViewById(R.id.attending_indicator);
      savedIndicator = findViewById(R.id.saved_indicator);
      hostedIndicator = findViewById(R.id.hosted_indicator);
      noContent = findViewById(R.id.no_content);
      eventsListView = findViewById(R.id.events_list);
      id = getIntent().getIntExtra("id", 0);
      adapter = new ForeignEventAdapter(this);
      eventsListView.setAdapter(adapter);
      
      requestAttendingEventsPage(page);
      
      
      attendingButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            requestAttendingEventsPage(page);
            attendingIndicator.setVisibility(View.VISIBLE);
            savedIndicator.setVisibility(View.INVISIBLE);
            hostedIndicator.setVisibility(View.INVISIBLE);
            attendingButton.setEnabled(false);
            savedButton.setEnabled(true);
            hostedButton.setEnabled(true);
         }
      });
      
      savedButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            requestSavedEventsPage(page);
            attendingIndicator.setVisibility(View.INVISIBLE);
            savedIndicator.setVisibility(View.VISIBLE);
            hostedIndicator.setVisibility(View.INVISIBLE);
            attendingButton.setEnabled(true);
            savedButton.setEnabled(false);
            hostedButton.setEnabled(true);
         }
      });
      
      hostedButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            requestHostedEventsPage(page);
            attendingIndicator.setVisibility(View.INVISIBLE);
            savedIndicator.setVisibility(View.INVISIBLE);
            hostedIndicator.setVisibility(View.VISIBLE);
            attendingButton.setEnabled(true);
            savedButton.setEnabled(true);
            hostedButton.setEnabled(false);
         }
      });
      
      
      backButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            finish();
         }
      });
   }
   
   public void requestAttendingEventsPage(final int page) {
      MainActivity.showLoadingIndicator(getWindow().getDecorView());
      Call<List<EventModel>> call = MainActivity.APIBuild().getForeignAttendingEvents(id, page + "", "Bearer " +
              getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
      call.enqueue(new Callback<List<EventModel>>() {
         @Override
         public void onResponse(Call<List<EventModel>> call, Response<List<EventModel>> response) {
            MainActivity.hideLoadingIndicator(getWindow().getDecorView());
            if (response.isSuccessful()) {
               if (page == 1) {
                  adapter.setEventListClear(response.body());
                  if (adapter.getCount() == 0) {
                     noContent.setVisibility(View.VISIBLE);
                  } else {
                     noContent.setVisibility(View.GONE);
                  }
               } else {
                  adapter.setEventList(response.body());
                  if (adapter.getCount() == 0) {
                     noContent.setVisibility(View.VISIBLE);
                  } else {
                     noContent.setVisibility(View.GONE);
                  }
               }
            } else {
               {
                  try {
                     JSONObject jObjError = new JSONObject(response.errorBody().string());
                     Toast.makeText(ForeignEventsActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                  } catch (Exception e) {
                     Toast.makeText(ForeignEventsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                  }
               }
            }
         }
         
         @Override
         public void onFailure(Call<List<EventModel>> call, Throwable t) {
            MainActivity.hideLoadingIndicator(getWindow().getDecorView());
         }
      });
   }
   
   public void requestSavedEventsPage(final int page) {
      MainActivity.showLoadingIndicator(getWindow().getDecorView());
      Call<List<EventModel>> call = MainActivity.APIBuild().getForeignSavedEvents(id, page + "", "Bearer " +
              getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
      call.enqueue(new Callback<List<EventModel>>() {
         @Override
         public void onResponse(Call<List<EventModel>> call, Response<List<EventModel>> response) {
            MainActivity.hideLoadingIndicator(getWindow().getDecorView());
            if (response.isSuccessful()) {
               if (page == 1) {
                  adapter.setEventListClear(response.body());
                  if (adapter.getCount() == 0) {
                     noContent.setVisibility(View.VISIBLE);
                  } else {
                     noContent.setVisibility(View.GONE);
                  }
               }
               else {
                  adapter.setEventList(response.body());
                  if (adapter.getCount() == 0) {
                     noContent.setVisibility(View.VISIBLE);
                  } else {
                     noContent.setVisibility(View.GONE);
                  }
               }
            } else {
               {
                  try {
                     JSONObject jObjError = new JSONObject(response.errorBody().string());
                     Toast.makeText(ForeignEventsActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                  } catch (Exception e) {
                     Toast.makeText(ForeignEventsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                  }
               }
            }
         }
         
         @Override
         public void onFailure(Call<List<EventModel>> call, Throwable t) {
            MainActivity.hideLoadingIndicator(getWindow().getDecorView());
         }
      });
   }
   
   public void requestHostedEventsPage(final int page) {
      MainActivity.showLoadingIndicator(getWindow().getDecorView());
      Call<List<EventModel>> call = MainActivity.APIBuild().getForeignEvents(id, page + "", "Bearer " +
              getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
      call.enqueue(new Callback<List<EventModel>>() {
         @Override
         public void onResponse(Call<List<EventModel>> call, Response<List<EventModel>> response) {
            MainActivity.hideLoadingIndicator(getWindow().getDecorView());
            if (response.isSuccessful()) {
               if (page == 1) {
                  adapter.setEventListClear(response.body());
                  if (adapter.getCount() == 0) {
                     noContent.setVisibility(View.VISIBLE);
                  } else {
                     noContent.setVisibility(View.GONE);
                  }
               }
               else {
                  adapter.setEventList(response.body());
                  if (adapter.getCount() == 0) {
                     noContent.setVisibility(View.VISIBLE);
                  } else {
                     noContent.setVisibility(View.GONE);
                  }
               }
            } else {
               {
                  try {
                     JSONObject jObjError = new JSONObject(response.errorBody().string());
                     Toast.makeText(ForeignEventsActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                  } catch (Exception e) {
                     Toast.makeText(ForeignEventsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                  }
               }
            }
         }
         
         @Override
         public void onFailure(Call<List<EventModel>> call, Throwable t) {
            MainActivity.hideLoadingIndicator(getWindow().getDecorView());
         }
      });
   }

   public void enterFromRight(Fragment fragment) {
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left);
      transaction.replace(R.id.tutorial, fragment);
      transaction.addToBackStack(null);
      transaction.commit();
   }
}