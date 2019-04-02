package io.ideaction.sketchproject.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONObject;

import java.util.List;

import im.delight.android.location.SimpleLocation;
import io.ideaction.sketchproject.Adapters.EventAdapter2;
import io.ideaction.sketchproject.Adapters.PeopleFeedAdapter;
import io.ideaction.sketchproject.Adapters.SportFeedAdapter;
import io.ideaction.sketchproject.MainScreenFragments.CreateEventFragment;
import io.ideaction.sketchproject.MainScreenFragments.CreatePostFragment;
import io.ideaction.sketchproject.Models.EventModel;
import io.ideaction.sketchproject.Models.SportFeedModel;
import io.ideaction.sketchproject.Models.User;
import io.ideaction.sketchproject.R;
import io.ideaction.sketchproject.Validations.Validations;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SportActivity extends AppCompatActivity {

    final int FEED_RUNNING = 1;
    final int FEED_YOGA = 2;
    final int FEED_BODY_BUILDING = 3;
    final int FEED_WEIGHT_LOSS = 4;
    final int FEED_HIKING = 5;
    final int FEED_POWER_LIFTING = 6;

    RelativeLayout feedButton;
    RelativeLayout peopleButton;
    RelativeLayout eventsButton;
    RelativeLayout backButton;
    LinearLayout feedButtonIndicator;
    LinearLayout peopleButtonIndicator;
    LinearLayout eventsButtonIndicator;
    LinearLayout searchBar;
    ListView sportFeedListView;
    ImageView addPostButton;
    ImageView addEventButton;
    TextView title;
    TextView noContent;
    EditText searchForUsers;
    static int feedCategory = 0;
    SportFeedAdapter adapter;
    PeopleFeedAdapter adapter2;
    EventAdapter2 adapter3;
    String fragmentDrawer = "";
    ImageView changeFeedOrder;

    public FragmentManager getS() {
        return getSupportFragmentManager();
    }

    public void setFragmentDrawer(String fragmentDrawer) {
        this.fragmentDrawer = fragmentDrawer;
    }

    public static void setFeedCategory(int feedCategory) {
        SportActivity.feedCategory = feedCategory;
    }

    public static int getFeedCategory() {
        return feedCategory;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport);
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        changeFeedOrder = findViewById(R.id.changeFeedOrder);
        location = new SimpleLocation(this);
        changeFeedOrder.setOnClickListener(v -> changeOrder());

        feedButton = findViewById(R.id.feed_button);
        searchBar = findViewById(R.id.search_bar);
        peopleButton = findViewById(R.id.people_button);
        eventsButton = findViewById(R.id.events_button);
        backButton = findViewById(R.id.back_arrow);
        feedButtonIndicator = findViewById(R.id.feed_indicator);
        peopleButtonIndicator = findViewById(R.id.people_indicator);
        eventsButtonIndicator = findViewById(R.id.events_indicator);
        sportFeedListView = findViewById(R.id.sport_feed_list);
        addPostButton = findViewById(R.id.add_button);
        addEventButton = findViewById(R.id.add_event_button);
        title = findViewById(R.id.title);
        noContent = findViewById(R.id.no_content);
        searchForUsers = findViewById(R.id.search_edit_text);
        adapter = new SportFeedAdapter(this);
        adapter2 = new PeopleFeedAdapter(this);
        adapter3 = new EventAdapter2(this);
        sportFeedListView.setAdapter(adapter);
        Validations.hideKeyboard(this);

        orderBy = "nearest";
        changeFeedOrder.setImageResource(R.drawable.icon_location);
        Toast.makeText(SportActivity.this, "Sorted based on location", Toast.LENGTH_SHORT).show();

        initializationBasedOnButtonPressed();
        requestSportFeedPage(1, feedCategory);

        searchForUsers.setOnEditorActionListener(
                (v, actionId, event) -> {
                    Call<List<User>> call = MainActivity.APIBuild().getPeople(searchForUsers.getText().toString(), 1 + "", "Bearer "
                            + getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));

                    call.enqueue(new Callback<List<User>>() {
                        @Override
                        public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                            Validations.hideKeyboard(SportActivity.this);
                            searchForUsers.setText("");
                            adapter2.setPeopleModelList2(response.body());
                        }

                        @Override
                        public void onFailure(Call<List<User>> call, Throwable t) {

                        }
                    });
                    return false;
                }
        );

        peopleButton.setOnClickListener(v -> {
                sportFeedListView.setAdapter(adapter2);
                requestPeopleFeedPage(1);
                searchBar.setVisibility(View.VISIBLE);
                addPostButton.setVisibility(View.GONE);
                addEventButton.setVisibility(View.GONE);
                feedButtonIndicator.setVisibility(View.INVISIBLE);
                peopleButtonIndicator.setVisibility(View.VISIBLE);
                eventsButtonIndicator.setVisibility(View.INVISIBLE);
                noContent.setVisibility(View.GONE);
                feedButton.setEnabled(true);
                changeFeedOrder.setVisibility(View.GONE);
                peopleButton.setEnabled(false);
                eventsButton.setEnabled(true);
        });

        feedButton.setOnClickListener(v -> {
            sportFeedListView.setAdapter(adapter);
            searchBar.setVisibility(View.GONE);
            addEventButton.setVisibility(View.GONE);
            addPostButton.setVisibility(View.VISIBLE);
            feedButtonIndicator.setVisibility(View.VISIBLE);
            peopleButtonIndicator.setVisibility(View.INVISIBLE);
            eventsButtonIndicator.setVisibility(View.INVISIBLE);
            feedButton.setEnabled(false);
            peopleButton.setEnabled(true);
            eventsButton.setEnabled(true);
            noContent.setVisibility(View.GONE);
            changeFeedOrder.setVisibility(View.VISIBLE);
            requestSportFeedPage(1, getFeedCategory());
            changeFeedOrder.setOnClickListener(v1 -> changeOrder());
        });

        eventsButton.setOnClickListener(v -> {
            sportFeedListView.setAdapter(adapter);
            searchBar.setVisibility(View.GONE);
            addPostButton.setVisibility(View.GONE);
            addEventButton.setVisibility(View.VISIBLE);
            feedButtonIndicator.setVisibility(View.INVISIBLE);
            peopleButtonIndicator.setVisibility(View.INVISIBLE);
            eventsButtonIndicator.setVisibility(View.VISIBLE);
            feedButton.setEnabled(true);
            peopleButton.setEnabled(true);
            eventsButton.setEnabled(false);
            sportFeedListView.setAdapter(adapter3);
            noContent.setVisibility(View.GONE);
            changeFeedOrder.setVisibility(View.VISIBLE);
            requestEventsPage(1);
            changeFeedOrder.setOnClickListener(v12 -> changeEventOrder());
        });

        addPostButton.setOnClickListener(v -> {
            enterFromRight(new CreatePostFragment(feedCategory));
            noContent.setVisibility(View.GONE);
            addPostButton.setEnabled(false);
            new CountDownTimer(500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    addPostButton.setEnabled(true);
                }
            }.start();
        });

        addEventButton.setOnClickListener(v -> {
            enterFromRight(new CreateEventFragment(feedCategory));
            noContent.setVisibility(View.GONE);
            addEventButton.setEnabled(false);
            new CountDownTimer(500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    addEventButton.setEnabled(true);
                }
            }.start();
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void  changeOrder() {
        if (orderBy.equals("newest")) {
            orderBy = "nearest";
            changeFeedOrder.setImageResource(R.drawable.icon_location);
            Toast.makeText(SportActivity.this, "Sorted based on location", Toast.LENGTH_SHORT).show();
        } else {
            orderBy = "newest";
            changeFeedOrder.setImageResource(R.drawable.icon_latest);
            Toast.makeText(SportActivity.this, "Sorted chronologically", Toast.LENGTH_SHORT).show();
        }
        requestSportFeedPage(1, feedCategory);
    }

    private void changeEventOrder() {
        if (orderBy.equals("newest")) {
            orderBy = "nearest";
            changeFeedOrder.setImageResource(R.drawable.icon_location);
            Toast.makeText(SportActivity.this, "Sorted based on location", Toast.LENGTH_SHORT).show();
        } else {
            orderBy = "newest";
            changeFeedOrder.setImageResource(R.drawable.icon_latest);
            Toast.makeText(SportActivity.this, "Sorted chronologically", Toast.LENGTH_SHORT).show();
        }
        requestEventsPage(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addEventButton.setEnabled(true);
    }

    @Override
    protected void onPause() {
        location.endUpdates();
        super.onPause();
    }

    private void initializationBasedOnButtonPressed() {
        switch (getIntent().getStringExtra("TYPE")) {
            case "RUNNING":
                title.setText("Running");
                setFeedCategory(FEED_RUNNING);
                break;
            case "YOGA":
                title.setText("Yoga");
                setFeedCategory(FEED_YOGA);
                break;
            case "BODY BUILDING":
                title.setText("Body Building");
                setFeedCategory(FEED_BODY_BUILDING);
                break;
            case "WEIGHT LOSS":
                title.setText("Weight Loss");
                setFeedCategory(FEED_WEIGHT_LOSS);
                break;
            case "HIKING":
                title.setText("Hiking");
                setFeedCategory(FEED_HIKING);
                break;
            case "POWER LIFTING":
                title.setText("Power Lifting");
                setFeedCategory(FEED_POWER_LIFTING);
                break;
        }
    }

    private SimpleLocation location;
    String orderBy = "newest";

    public void requestSportFeedPage(final int page, final int category) {

        if (!orderBy.equals("newest")) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
//                    PackageManager.PERMISSION_GRANTED &&
//                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
//                            PackageManager.PERMISSION_GRANTED) {
//                location.beginUpdates();
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{
//                                Manifest.permission.ACCESS_FINE_LOCATION,
//                                Manifest.permission.ACCESS_COARSE_LOCATION},
//                        1);
//                return;
//            }
        }

        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();


        MainActivity.showLoadingIndicator(getWindow().getDecorView());
        Call<List<SportFeedModel>> call = MainActivity.APIBuild().getSportFeed(page + "", category + "", "Bearer " +
                getSharedPreferences("my", MODE_PRIVATE).getString("token", ""), orderBy, latitude + "", longitude + "");
        call.enqueue(new Callback<List<SportFeedModel>>() {
            @Override
            public void onResponse(Call<List<SportFeedModel>> call, Response<List<SportFeedModel>> response) {
                MainActivity.hideLoadingIndicator(getWindow().getDecorView());
                if (response.isSuccessful()) {
                    if (page == 1) {
                        adapter.setSportFeedListClear(response.body());
                    } else {
                        adapter.setSportFeedList(response.body(), page);
                    }
                    if (response.body().size() == 0 && page == 1) {
                        noContent.setVisibility(View.VISIBLE);
                    } else {
                        noContent.setVisibility(View.GONE);
                    }


                } else {
                    {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(SportActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(SportActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SportFeedModel>> call, Throwable t) {
                MainActivity.hideLoadingIndicator(getWindow().getDecorView());
                Toast.makeText(SportActivity.this, "Check your internet connection or our server is down!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestPeopleFeedPage(final int page2) {
        MainActivity.showLoadingIndicator(getWindow().getDecorView());
        Call<List<User>> call = MainActivity.APIBuild().getPeople("", page2 + "", "Bearer " +
                getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                MainActivity.hideLoadingIndicator(getWindow().getDecorView());
                if (response.isSuccessful()) {
                    adapter2.setPeopleFeedList(response.body(), page2);
                    adapter2.notifyDataSetChanged();
                    if (response.body().size() == 0 && page2 == 1) {
                        noContent.setVisibility(View.VISIBLE);
                    } else {
                        noContent.setVisibility(View.GONE);
                    }
                } else {
                    {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(SportActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(SportActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                MainActivity.hideLoadingIndicator(getWindow().getDecorView());
                Toast.makeText(SportActivity.this, "Check your internet connection or our server is down!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void enterFromRight(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left);
        transaction.replace(R.id.sport_container, fragment);
        changeFeedOrder.setVisibility(View.GONE);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {

        if (fragmentDrawer.equals("people")) {

            sportFeedListView.setAdapter(adapter2);
            requestPeopleFeedPage(1);
        }
        if (fragmentDrawer.equals("addEvent")) {
            sportFeedListView.setAdapter(adapter3);
            requestEventsPage(1);
        }
        if (fragmentDrawer.equals("addPost")) {
            sportFeedListView.setAdapter(adapter);
            requestSportFeedPage(1, getFeedCategory());
            changeFeedOrder.setVisibility(View.VISIBLE);
        }
        super.onBackPressed();
    }

    public void requestEventsPage(final int page) {
        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();

        MainActivity.showLoadingIndicator(getWindow().getDecorView());
        Call<List<EventModel>> call = MainActivity.APIBuild().getEvents(String.valueOf(page), String.valueOf(getFeedCategory()), "Bearer " +
                getSharedPreferences("my", MODE_PRIVATE).getString("token", ""),orderBy, latitude + "", longitude + "");
        call.enqueue(new Callback<List<EventModel>>() {
            @Override
            public void onResponse(Call<List<EventModel>> call, Response<List<EventModel>> response) {
                MainActivity.hideLoadingIndicator(getWindow().getDecorView());
                if (response.isSuccessful()) {
                    if (page == 1) {
                        adapter3.setEventListClear(response.body());
                        if (adapter3.getCount() == 0) {
                            noContent.setVisibility(View.VISIBLE);
                        } else {
                            noContent.setVisibility(View.GONE);
                        }
                    } else {
                        adapter3.setEventList(response.body(), page);
                    }
                } else {
                    {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(SportActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(SportActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<EventModel>> call, Throwable t) {
                MainActivity.hideLoadingIndicator(getWindow().getDecorView());
                Toast.makeText(SportActivity.this, "Check your internet connection or maybe our server is down", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void disableClickable() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void enableClickable() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}