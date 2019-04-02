package io.ideaction.sketchproject.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.ideaction.sketchproject.Activities.ForeignProfileActivity;
import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.Activities.SingleNotificationActivity;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.CustomCallBack;
import io.ideaction.sketchproject.Models.EventModel;
import io.ideaction.sketchproject.Models.NotificationModel;
import io.ideaction.sketchproject.Models.SportFeedModel;
import io.ideaction.sketchproject.Models.User;
import io.ideaction.sketchproject.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class NotificationsAdapter extends BaseAdapter {

    List<NotificationModel> notificationList = new ArrayList<>();
    Context context;

    public NotificationsAdapter(Context context) {
        this.context = context;
    }

    public void setNotificationList(List<NotificationModel> notificationList) {
        this.notificationList = notificationList;
    }

    @Override
    public int getCount() {
        return notificationList.size();
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

        if (notificationList.get(position).getType() != 1) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.notification_cell, parent, false);

            CircleImageView notificationCellImage = rowView.findViewById(R.id.notification_image);
            TextView timePassed = rowView.findViewById(R.id.time_passed);
            final TextView notification = rowView.findViewById(R.id.notification);
            notification.setText(notificationList.get(position).getText());
            timePassed.setText(notificationList.get(position).getCreatedAtForHumans());
            if (notificationList.get(position).getUser().getAvatar().isEmpty()) {
                Picasso.get().load(R.drawable.default_profile).into(notificationCellImage);
            } else {
                Picasso.get().load(notificationList.get(position).getUser().getAvatar()).placeholder(R.drawable.default_profile).into(notificationCellImage);
            }

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowView.setEnabled(false);

                    new CountDownTimer(500, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            rowView.setEnabled(true);

                        }
                    }.start();
                    if (notificationList.get(position).getType() < 5) {
                        Call<User> call = MainActivity.APIBuild().getPerson(notificationList.get(position).getUser().getId() + "", "Bearer " +
                                context.getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
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
                    } else if (notificationList.get(position).getType() == 5 ||
                            notificationList.get(position).getType() == 6 ||
                            notificationList.get(position).getType() == 8) {
                        Call<SportFeedModel> call = MainActivity.APIBuild().getPost(notificationList.get(position).getModelId(), "Bearer " +
                                context.getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
                        call.enqueue(new Callback<SportFeedModel>() {
                            @Override
                            public void onResponse(Call<SportFeedModel> call, Response<SportFeedModel> response) {
                                if (response.isSuccessful()) {
                                    Intent i = new Intent(context, SingleNotificationActivity.class);
                                    i.putExtra("TYPE", "Post");
                                    i.putExtra("post", response.body());
                                    context.startActivity(i);

                                } else {
                                    {
                                        try {
                                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                                            Toast.makeText(context, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                                        } catch (Exception e) {
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<SportFeedModel> call, Throwable t) {

                            }


                        });

                    } else if (notificationList.get(position).getType() == 7 ||
                            notificationList.get(position).getType() == 9) {
                        Call<EventModel> call = MainActivity.APIBuild().getEvent(notificationList.get(position).getModelId(), "Bearer " +
                                context.getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
                        call.enqueue(new Callback<EventModel>() {
                            @Override
                            public void onResponse(Call<EventModel> call, Response<EventModel> response) {
                                if (response.isSuccessful()) {
                                    Intent i = new Intent(context, SingleNotificationActivity.class);
                                    i.putExtra("TYPE", "Event");
                                    i.putExtra("event", response.body());
                                    context.startActivity(i);

                                } else {
                                    {
                                        try {
                                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                                            Toast.makeText(context, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                                        } catch (Exception e) {
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<EventModel> call, Throwable t) {

                            }
                        });

                    }

                }
            });
            return rowView;
        } else {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.friend_request_cell, parent, false);

            CircleImageView notificationCellImage = rowView.findViewById(R.id.notification_image);
            TextView timePassed = rowView.findViewById(R.id.time_passed);
            final TextView notification = rowView.findViewById(R.id.notification);
            final ImageView accept = rowView.findViewById(R.id.accept_icon);
            final ImageView reject = rowView.findViewById(R.id.reject_icon);

            notification.setText(notificationList.get(position).getText());

            accept.setOnClickListener(v -> {
                acceptFriendRequest(position, notification, accept, reject);
                accept.setEnabled(false);
                new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        accept.setEnabled(true);
                    }
                }.start();
            });

            reject.setOnClickListener(v -> rejectFriendRequest(position, notification, accept, reject));

            timePassed.setText(notificationList.get(position).getCreatedAtForHumans());
            if (notificationList.get(position).getUser().getAvatar().isEmpty()) {
                Picasso.get().load(R.drawable.default_profile).into(notificationCellImage);
            } else {
                Picasso.get().load(notificationList.get(position).getUser().getAvatar()).placeholder(R.drawable.default_profile).into(notificationCellImage);
            }

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowView.setEnabled(false);
                    final ProgressDialog pd = new ProgressDialog(context);
                    pd.setMessage("loading...");
                    pd.setCancelable(false);
                    pd.show();
                    new CountDownTimer(500, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            rowView.setEnabled(true);
                            pd.dismiss();
                        }
                    }.start();
                    if (notificationList.get(position).getType() < 5) {
                        Call<User> call = MainActivity.APIBuild().getPerson(notificationList.get(position).getUser().getId() + "", "Bearer " +
                                context.getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
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
                }
            });

            return rowView;
        }
    }

    private void acceptFriendRequest(final int position, final TextView notification, final ImageView accept, final ImageView reject) {
        Call<HashMap> callNotifications = MainActivity.APIBuild().acceptFriendRequest(notificationList.get(position).getUser().getId(), "Bearer " +
                context.getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
        callNotifications.enqueue(new Callback<HashMap>() {
            @Override
            public void onResponse(Call<HashMap> call, Response<HashMap> response) {
                accept.setVisibility(View.GONE);
                reject.setVisibility(View.GONE);
                notification.setText("You have accepted " + notificationList.get(position).getUser().getName() + "'s partnership request.");
            }

            @Override
            public void onFailure(Call<HashMap> call, Throwable t) {
                Toast.makeText(context, "Your internet connection is bad or our server is down", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rejectFriendRequest(final int position, final TextView notification, final ImageView accept, final ImageView reject) {
        Call<HashMap> callNotifications = MainActivity.APIBuild().denyFriendRequest(notificationList.get(position).getUser().getId(), "Bearer " +
                context.getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
        callNotifications.enqueue(new Callback<HashMap>() {
            @Override
            public void onResponse(Call<HashMap> call, Response<HashMap> response) {
                accept.setVisibility(View.GONE);
                reject.setVisibility(View.GONE);
                notification.setText("You have denied " + notificationList.get(position).getUser().getName() + "'s partnership request.");
            }

            @Override
            public void onFailure(Call<HashMap> call, Throwable t) {
                Toast.makeText(context, "Your internet connection is bad or our server is down", Toast.LENGTH_SHORT).show();
            }
        });
    }
}