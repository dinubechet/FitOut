package io.ideaction.sketchproject.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import io.ideaction.sketchproject.Activities.EventsActivity;
import io.ideaction.sketchproject.Activities.FeedActivity;
import io.ideaction.sketchproject.Activities.ForeignProfileActivity;
import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.Activities.SportActivity;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.CustomCallBack;
import io.ideaction.sketchproject.MainScreenFragments.CreateEventFragment;
import io.ideaction.sketchproject.Models.EventModel;
import io.ideaction.sketchproject.Models.SportFeedModel;
import io.ideaction.sketchproject.Models.User;
import io.ideaction.sketchproject.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class EventAdapter extends BaseAdapter {

    Context context;
    private boolean readyToCall = true;
    private int page = 1;
    SharedPreferences sharedPreferences;

    List<EventModel> eventList = new ArrayList<>();

    String[] items = new String[]{
            "Edit" , "Delete" , "Cancel"
    };
    ArrayAdapter<String> spinnerAdapter;

    public void setEventList(List<EventModel> eventList) {
        if (page == 1) {
            this.eventList = eventList;
            notifyDataSetChanged();
        }

        this.eventList.addAll(eventList);
        readyToCall = !eventList.isEmpty();
        notifyDataSetChanged();
    }

    public void setEventListClear(List<EventModel> eventList) {
        this.eventList = eventList;
        notifyDataSetChanged();
        page = 1;
    }

    public EventAdapter(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("my", MODE_PRIVATE);
        spinnerAdapter =new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, items);

    }


    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.events_cell, parent, false);

        final RelativeLayout saveButton = rowView.findViewById(R.id.save_button);
        final RelativeLayout attendButton = rowView.findViewById(R.id.attend_button);
        CircleImageView eventOwnerAvatar = rowView.findViewById(R.id.profile_image);
        TextView nameOfTheEventOwner = rowView.findViewById(R.id.name_of_event_owner);
        TextView timeLeft = rowView.findViewById(R.id.time_left);
        TextView when = rowView.findViewById(R.id.time_at);
        TextView where = rowView.findViewById(R.id.place_at);
        TextView description = rowView.findViewById(R.id.content_text);
        TextView title = rowView.findViewById(R.id.title);
        TextView noOneGoing = rowView.findViewById(R.id.no_one_going);
        TextView goingCounter = rowView.findViewById(R.id.going_others_number);
        final ImageView attendPicture = rowView.findViewById(R.id.attend_image_view);
        final ImageView savePicture = rowView.findViewById(R.id.save_image);
        final TextView saveTextView = rowView.findViewById(R.id.save_text_on_button);
        LinearLayout whoIsGoing = rowView.findViewById(R.id.linear_with_going);
        LinearLayout going1 = rowView.findViewById(R.id.going_1);
        LinearLayout going2 = rowView.findViewById(R.id.going_2);
        LinearLayout going3 = rowView.findViewById(R.id.going_3);
        LinearLayout going4 = rowView.findViewById(R.id.going_4);
        LinearLayout goingOthers = rowView.findViewById(R.id.going_others);
        CircleImageView profileImageGoing1 = rowView.findViewById(R.id.profile_image_going_1);
        CircleImageView profileImageGoing2 = rowView.findViewById(R.id.profile_image_going_2);
        CircleImageView profileImageGoing3 = rowView.findViewById(R.id.profile_image_going_3);
        CircleImageView profileImageGoing4 = rowView.findViewById(R.id.profile_image_going_4);
        Spinner deleteEventButton = rowView.findViewById(R.id.delete_event_button);
        deleteEventButton.setAdapter(spinnerAdapter);


        if (position == eventList.size() - 1 && readyToCall) {
            page++;
            ((EventsActivity) context).requestHostedEventsPage(page);
        }
        if (Integer.parseInt(eventList.get(position).getCountAttendedUsers()) > 4) {
            goingOthers.setVisibility(View.VISIBLE);
            goingCounter.setText("+" + (Integer.parseInt(eventList.get(position).getCountAttendedUsers()) - 4));
        }

        if (eventList.get(position).getUser().getEmail().equals
                (context.getSharedPreferences("my", MODE_PRIVATE)
                        .getString("email", ""))) {
            deleteEventButton.setVisibility(VISIBLE);
        } else {
            deleteEventButton.setVisibility(GONE);
        }
        deleteEventButton.setSelection(2,false);
        deleteEventButton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int positionx, long id) {
                if(positionx == 1){
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(context);
                    }
                    builder.setTitle("Delete event")
                            .setMessage("Are you sure you want to delete this event?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Call<EventModel> call = MainActivity.APIBuild().deleteEvent(eventList.get(position).getId(), "Bearer " +
                                            context.getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
                                    call.enqueue(new CustomCallBack<EventModel>(context) {
                                        @Override
                                        public void onResponse(Call<EventModel> call, Response<EventModel> response) {
                                            super.onResponse(call, response);
                                            if (response.isSuccessful()) {
                                                eventList.remove(position);
                                                notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<EventModel> call, Throwable t) {
                                            super.onFailure(call, t);
                                        }
                                    });
                                }
                            })
                            .setNegativeButton(android.R.string.no, (dialog, which) -> {

                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    deleteEventButton.setSelection(2,false);
                }
                if (positionx == 0){

                    if (context instanceof SportActivity) {
                        ((SportActivity) context).enterFromRight(new CreateEventFragment(eventList.get(position)));
                    }
                    else if (context instanceof FeedActivity) {
                        ((FeedActivity) context).enterFromRight(new CreateEventFragment(eventList.get(position)));
                    }else if (context instanceof EventsActivity) {
                        ((EventsActivity) context).enterFromRight(new CreateEventFragment(eventList.get(position)));
                    }
                    deleteEventButton.setSelection(2,false);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );

        setFields(position, eventOwnerAvatar, nameOfTheEventOwner, timeLeft, when, where, description, title, attendPicture, savePicture, saveTextView);

        attendButton.setOnClickListener(v -> {
            if (!eventList.get(position).getAttend()) {
                eventList.get(position).getAttendedUsers().add(0, new User(sharedPreferences.getString("name", ""), sharedPreferences.getString("img", "")));

            } else {
                for (User x : eventList.get(position).getAttendedUsers()) {
                    if (x.getName().equals(sharedPreferences.getString("name", ""))) {
                        eventList.get(position).getAttendedUsers().remove(x);
                        break;
                    }
                }
            }
            attendButtonFunctionality(position, attendPicture);
            attendButton.setEnabled(false);
            new CountDownTimer(500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    attendButton.setEnabled(true);
                }
            }.start();


        });

        saveButton.setOnClickListener(v -> {
            saveButtonFunctionality(position, savePicture, saveTextView);
            saveButton.setEnabled(false);
            new CountDownTimer(500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    saveButton.setEnabled(true);
                }
            }.start();
        });

        eventOwnerAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<User> call = MainActivity.APIBuild().getPerson(eventList.get(position).getUser().getId() + "", "Bearer " +
                        context.getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));
                call.enqueue(new CustomCallBack<User>(context) {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        super.onResponse(call, response);
                        if (response.isSuccessful()) {
                            Intent i = new Intent(context, ForeignProfileActivity.class);
                            i.putExtra("TYPE", "FOREIGN EVENTS");
                            i.putExtra("user", response.body());
                            context.startActivity(i);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        super.onFailure(call, t);
                    }
                });
            }
        });

        numberOfPeopleGoing(position, noOneGoing, whoIsGoing, going1, going2, going3, going4, goingOthers,
                profileImageGoing1, profileImageGoing2, profileImageGoing3, profileImageGoing4);
        return rowView;
    }

    private void attendButtonFunctionality(int position, ImageView attendPicture) {
        Call<HashMap> call = MainActivity.APIBuild().attentUnattend(eventList.get(position).getId(), "Bearer " +
                context.getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));

        call.enqueue(new Callback<HashMap>() {
            @Override
            public void onResponse(Call<HashMap> call, Response<HashMap> response) {
            }

            @Override
            public void onFailure(Call<HashMap> call, Throwable t) {
            }
        });
        if (!((EventsActivity) context).getAttendingButton().isEnabled()) {
            Picasso.get().load(R.drawable.attend).into(attendPicture);
            eventList.remove(position);
        } else {
            if (eventList.get(position).getAttend()) {
                Picasso.get().load(R.drawable.attend).into(attendPicture);
                eventList.get(position).setAttend(false);
            } else {
                Picasso.get().load(R.drawable.attend2).into(attendPicture);
                eventList.get(position).setAttend(true);
            }
        }
       notifyDataSetChanged();
    }

    private void saveButtonFunctionality(int position, ImageView savePicture, TextView saveTextView) {
        Call<HashMap> call = MainActivity.APIBuild().saveUnsave(eventList.get(position).getId(), "Bearer " +
                context.getSharedPreferences("my", Context.MODE_PRIVATE).getString("token", ""));

        call.enqueue(new Callback<HashMap>() {
            @Override
            public void onResponse(Call<HashMap> call, Response<HashMap> response) {
            }

            @Override
            public void onFailure(Call<HashMap> call, Throwable t) {
            }
        });
        if (!((EventsActivity) context).getSavedButton().isEnabled()) {
            Picasso.get().load(R.drawable.save).into(savePicture);
            saveTextView.setText("Save");
            eventList.remove(position);
        } else {
            if (eventList.get(position).getSaved()) {
                Picasso.get().load(R.drawable.save).into(savePicture);
                saveTextView.setText("Save");
                eventList.get(position).setSaved(false);
            } else {
                Picasso.get().load(R.drawable.save2).into(savePicture);
                saveTextView.setText("Unsave");
                eventList.get(position).setSaved(true);
            }
        }
        notifyDataSetChanged();
    }

    private void setFields(int position, CircleImageView eventOwnerAvatar, TextView nameOfTheEventOwner,
                           TextView timeLeft, TextView when, TextView where, TextView description,
                           TextView title, ImageView attendPicture, ImageView savePicture, TextView saveTextView) {
        if (eventList.get(position).getUser().getAvatar().isEmpty()) {
            Picasso.get().load(R.drawable.default_profile).into(eventOwnerAvatar);
        } else {
            Picasso.get().load(eventList.get(position).getUser().getAvatar()).placeholder(R.drawable.default_profile).into(eventOwnerAvatar);
        }
        nameOfTheEventOwner.setText(eventList.get(position).getUser().getName());
        timeLeft.setText(eventList.get(position).getWhenForHumans());
        when.setText(getDate(eventList.get(position).getWhenTimestamp()));
        where.setText(eventList.get(position).getAddress());
        description.setText(eventList.get(position).getDescription());
        title.setText(eventList.get(position).getName());

        if (eventList.get(position).getAttend()) {
            Picasso.get().load(R.drawable.attend2).into(attendPicture);
        } else {
            Picasso.get().load(R.drawable.attend).into(attendPicture);
        }
        if (eventList.get(position).getSaved()) {
            Picasso.get().load(R.drawable.save2).into(savePicture);
            saveTextView.setText("Saved");
        } else {
            Picasso.get().load(R.drawable.save).into(savePicture);
            saveTextView.setText("Save");
        }
    }
    private String getDate(long timeStamp){
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a EEEE - MMM dd, yyyy");
        try{
            java.sql.Date netDate = (new Date(timeStamp *1000));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }
    private void numberOfPeopleGoing(int position, TextView noOneGoing, LinearLayout whoIsGoing,
                                     LinearLayout going1, LinearLayout going2, LinearLayout going3,
                                     LinearLayout going4, LinearLayout goingOthers, CircleImageView profileImageGoing1,
                                     CircleImageView profileImageGoing2, CircleImageView profileImageGoing3,
                                     CircleImageView profileImageGoing4) {
        switch (eventList.get(position).getAttendedUsers().size()) {
            case 0:
                noOneGoing.setVisibility(View.VISIBLE);
                whoIsGoing.setVisibility(GONE);
                break;

            case 1:
                going1.setVisibility(View.VISIBLE);
                if (eventList.get(position).getAttendedUsers().get(0).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing1);
                } else {
                    Picasso.get().load(eventList.get(position).getAttendedUsers().get(0).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing1);
                }
                break;

            case 2:
                going1.setVisibility(View.VISIBLE);
                going2.setVisibility(View.VISIBLE);
                if (eventList.get(position).getAttendedUsers().get(0).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing1);
                } else {
                    Picasso.get().load(eventList.get(position).getAttendedUsers().get(0).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing1);
                }
                if (eventList.get(position).getAttendedUsers().get(1).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing2);
                } else {
                    Picasso.get().load(eventList.get(position).getAttendedUsers().get(1).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing2);
                }
                break;

            case 3:
                going1.setVisibility(View.VISIBLE);
                going2.setVisibility(View.VISIBLE);
                going3.setVisibility(View.VISIBLE);
                if (eventList.get(position).getAttendedUsers().get(0).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing1);
                } else {
                    Picasso.get().load(eventList.get(position).getAttendedUsers().get(0).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing1);
                }
                if (eventList.get(position).getAttendedUsers().get(1).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing2);
                } else {
                    Picasso.get().load(eventList.get(position).getAttendedUsers().get(1).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing2);
                }
                if (eventList.get(position).getAttendedUsers().get(2).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing3);
                } else {
                    Picasso.get().load(eventList.get(position).getAttendedUsers().get(2).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing3);
                }
                break;

            default:
                going1.setVisibility(View.VISIBLE);
                going2.setVisibility(View.VISIBLE);
                going3.setVisibility(View.VISIBLE);
                going4.setVisibility(View.VISIBLE);
                if (eventList.get(position).getAttendedUsers().get(0).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing1);
                } else {
                    Picasso.get().load(eventList.get(position).getAttendedUsers().get(0).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing1);
                }
                if (eventList.get(position).getAttendedUsers().get(1).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing2);
                } else {
                    Picasso.get().load(eventList.get(position).getAttendedUsers().get(1).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing2);
                }
                if (eventList.get(position).getAttendedUsers().get(2).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing3);
                } else {
                    Picasso.get().load(eventList.get(position).getAttendedUsers().get(2).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing3);
                }
                if (eventList.get(position).getAttendedUsers().get(3).getAvatar().isEmpty()) {
                    Picasso.get().load(R.drawable.default_profile).into(profileImageGoing4);
                } else {
                    Picasso.get().load(eventList.get(position).getAttendedUsers().get(3).getAvatar())
                            .placeholder(R.drawable.default_profile).into(profileImageGoing4);
                }
                break;
        }
    }
}