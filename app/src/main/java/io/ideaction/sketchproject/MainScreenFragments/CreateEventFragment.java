package io.ideaction.sketchproject.MainScreenFragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONObject;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import io.ideaction.sketchproject.Activities.EventsActivity;
import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.Activities.SportActivity;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.Util;
import io.ideaction.sketchproject.Models.EventModel;
import io.ideaction.sketchproject.R;
import io.ideaction.sketchproject.Validations.Validations;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

@SuppressLint("ValidFragment")
public class CreateEventFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

   ImageView back_button;
   TextView createButton;
   RelativeLayout allLayout;
   EditText event_name;
   TextView when_edit_text;
   TextView where_edit_text;
   EditText description_edit_text;
   String realDate;
   EventModel event;
   int feedCategory;

   String name= "";
   String address = "";
   String longAndLat = "";

   boolean edit = false;
   boolean dateChange = false;

   public CreateEventFragment(int feedCategory) {
      this.feedCategory = feedCategory;
   }

   public CreateEventFragment(EventModel event) {
      this.event = event;
      edit = true;
   }

   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      final View view = inflater.inflate(R.layout.create_event_fragment, container, false);

      back_button = view.findViewById(R.id.back_button);
      createButton = view.findViewById(R.id.create_button);
      event_name = view.findViewById(R.id.event_name);
      when_edit_text = view.findViewById(R.id.when_edit_text);
      where_edit_text = view.findViewById(R.id.where_edit_text);
      description_edit_text = view.findViewById(R.id.description_edit_text);
      allLayout = view.findViewById(R.id.event_fragment);
      allLayout.setClickable(true);
      event_name.requestFocus();
      event_name.setOnEditorActionListener((v, actionId, event) -> {
         when_edit_text.callOnClick();
         return false;

      });

      if (edit){
         event_name.setText(event.getName());
         where_edit_text.setText(event.getAddress());
         description_edit_text.setText(event.getDescription());
         when_edit_text.setText(getDate(event.getWhenTimestamp()));
         realDate = when_edit_text.getText().toString();
      }
      if (getContext() instanceof SportActivity) {
         ((SportActivity)getActivity()).setFragmentDrawer("addEvent");
      } else if (getContext() instanceof EventsActivity) {
         ((EventsActivity)getActivity()).setFragmentDrawer("addEvent");
      }


      back_button.setOnClickListener(v -> {
         Validations.hideKeyboard(getActivity());
         getActivity().onBackPressed();
      });
      createButton.setOnClickListener(v -> {


         if (event_name.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Event name is empty", Toast.LENGTH_SHORT).show();
         } else if (when_edit_text.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Event time is missing", Toast.LENGTH_SHORT).show();
         } else if (where_edit_text.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Event location is missing", Toast.LENGTH_SHORT).show();
         } else if (description_edit_text.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Event description is missing", Toast.LENGTH_SHORT).show();
         } else {
            Validations.hideKeyboard(getActivity());
            requestingEventCreation(view);
         }
      });


      when_edit_text.setOnClickListener(v -> {
         Validations.hideKeyboard(getActivity());
         DatePickerDialog dpd;
         Calendar now = Calendar.getInstance();
         dpd = DatePickerDialog.newInstance(
                 CreateEventFragment.this,
                 now.get(Calendar.YEAR),
                 now.get(Calendar.MONTH),
                 now.get(Calendar.DAY_OF_MONTH));
         dpd.setThemeDark(true);
         dpd.setMinDate(Calendar.getInstance());
         dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
      });

      where_edit_text.setOnClickListener(v -> {
         where_edit_text.setEnabled(false);
         new CountDownTimer(500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
               where_edit_text.setEnabled(true);
            }
         }.start();
         getPLaces();
      });
      return view;
   }



   @Override
   public void onStart() {
      super.onStart();
      Validations.showKeyboard(getActivity());
   }

   @Override
   public boolean getUserVisibleHint() {
      return super.getUserVisibleHint();

   }

   @Override
   public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
      String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
      when_edit_text.setText(date);
      TimePickerDialog pickerDialog = TimePickerDialog.newInstance(CreateEventFragment.this, false);
      pickerDialog.setThemeDark(true);
      pickerDialog.show(getActivity().getFragmentManager(), "Datepickerdialog2");
   }

   @Override
   public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
      String date;
      if (minute < 10)
         date = hourOfDay + ":0" + minute;
      else
         date = hourOfDay + ":" + minute;
      when_edit_text.setText(when_edit_text.getText().toString() + " " + date);
      realDate = when_edit_text.getText().toString();
      dateChange = true;
   }

   int PLACE_PICKER_REQUEST = 1;

   void getPLaces() {
      PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

      try {
         startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
      } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
         e.printStackTrace();
      }
   }


   private String getDate(long timeStamp){

      try{
         SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
         Date netDate = (new Date(timeStamp));
         return sdf.format(netDate);
      }
      catch(Exception ex){
         return "xx";
      }
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == PLACE_PICKER_REQUEST) {
         if (resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(data, getActivity());

            name = place.getName()+"";
            longAndLat = place.getLatLng() +"";
            where_edit_text.setText("at " + name);

         }
      }
   }

   private void requestingEventCreation(final View view) {

      if(edit){
         String time = "";

         if (dateChange)
            time = Util.convertStringToTimestamp(realDate) + "";
         else
            time = event.getWhenTimestamp() + "";

         MainActivity.showLoadingIndicator(view);
         Call <EventModel> updateEvent = MainActivity.APIBuild()
                 .updateEvent(
                         event.getId()
                         ,event_name.getText().toString()
                         ,where_edit_text.getText().toString()
                         ,description_edit_text.getText().toString()
                         , time
                         , "Bearer " +
                                 getActivity().getSharedPreferences("my", MODE_PRIVATE)
                                         .getString("token", ""));

         updateEvent.enqueue(new Callback<EventModel>() {
            @Override
            public void onResponse(Call<EventModel> call, Response<EventModel> response) {
               MainActivity.hideLoadingIndicator(view);
               if (response.isSuccessful()) {
                  Validations.hideKeyboard(getActivity());
                  getActivity().onBackPressed();
               } else {
                  {
                     try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getActivity(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                     } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                     }
                  }
               }
            }

            @Override
            public void onFailure(Call<EventModel> call, Throwable t) {
               Validations.hideKeyboard(getActivity());
               MainActivity.hideLoadingIndicator(view);
            }
         });
      }else {


         MainActivity.showLoadingIndicator(view);
         Call<EventModel> createEvent = MainActivity.APIBuild().createNewEvent(event_name.getText().toString(), where_edit_text.getText().toString(),
                 description_edit_text.getText().toString(), String.valueOf(feedCategory), Util.convertStringToTimestamp(realDate) + "", "Bearer " +
                         getActivity().getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
         createEvent.enqueue(new Callback<EventModel>() {
            @Override
            public void onResponse(Call<EventModel> call, Response<EventModel> response) {
               MainActivity.hideLoadingIndicator(view);
               if (response.isSuccessful()) {
                  Validations.hideKeyboard(getActivity());
                  getActivity().onBackPressed();
               } else {
                  {
                     try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getActivity(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                     } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                     }
                  }
               }
            }

            @Override
            public void onFailure(Call<EventModel> call, Throwable t) {
               Validations.hideKeyboard(getActivity());
               MainActivity.hideLoadingIndicator(view);
            }
         });

      }
   }
}