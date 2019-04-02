package io.ideaction.sketchproject.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

import io.ideaction.sketchproject.Adapters.EventAdapter;
import io.ideaction.sketchproject.Adapters.SportFeedAdapter;
import io.ideaction.sketchproject.MainScreenFragments.CreateCommentFragment;
import io.ideaction.sketchproject.Models.EventModel;
import io.ideaction.sketchproject.Models.SportFeedModel;
import io.ideaction.sketchproject.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedActivity extends AppCompatActivity {

    RelativeLayout backArrow;
    SportFeedAdapter adapter;
    EventAdapter eventAdapter;
    int page = 1;
    ListView feedListView;
    TextView title;
    TextView noContent;

    public FragmentManager getS() {
        return getSupportFragmentManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        feedListView = findViewById(R.id.feed_list);
        title = findViewById(R.id.title);
        backArrow = findViewById(R.id.back_arrow);
        noContent = findViewById(R.id.no_content);
        adapter = new SportFeedAdapter(this);
        eventAdapter = new EventAdapter(this);
        feedListView.setAdapter(adapter);

        initialisationByType();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initialisationByType() {
        switch (getIntent().getStringExtra("TYPE")) {
            case "POST":
                title.setText("Posts");
                requestPage(page);
                break;
            case "LIKES":
                title.setText("Likes");
                requestLikedPage(page);
                break;
            case "FOREIGN EVENTS":
                title.setText("Events");
                requestForeignEvents(page, getIntent().getIntExtra("id", 0));
                break;
            case "FOREIGN POST":
                title.setText("Posts");
                requestForeignPosts(page, getIntent().getIntExtra("id", 0));
                break;
            case "FOREIGN LIKES":
                title.setText("Likes");
                requestForeignLikedPage(page, getIntent().getIntExtra("id", 0));
                break;
        }
    }

    public void requestPage(final int page) {
        Call<List<SportFeedModel>> call = MainActivity.APIBuild().getMyPosts(page + "", "Bearer " +
                getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
        call.enqueue(new Callback<List<SportFeedModel>>() {
            @Override
            public void onResponse(Call<List<SportFeedModel>> call, Response<List<SportFeedModel>> response) {
                if (response.isSuccessful()) {
                    adapter.setSportFeedList(response.body(), page);
                    adapter.notifyDataSetChanged();
                    if (response.body().size() == 0 && page == 1) {
                        noContent.setVisibility(View.VISIBLE);
                    } else {
                        noContent.setVisibility(View.GONE);
                    }
                } else {
                    {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(FeedActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(FeedActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SportFeedModel>> call, Throwable t) {
            }
        });
    }

    public void requestForeignPosts(final int page, Integer id) {
        Call<List<SportFeedModel>> call = MainActivity.APIBuild().getForeignPosts(id, page + "", "Bearer " +
                getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
        call.enqueue(new Callback<List<SportFeedModel>>() {
            @Override
            public void onResponse(Call<List<SportFeedModel>> call, Response<List<SportFeedModel>> response) {
                if (response.isSuccessful()) {
                    adapter.setSportFeedList(response.body(), page);
                    adapter.notifyDataSetChanged();
                    if (response.body().size() == 0 && page == 1) {
                        noContent.setVisibility(View.VISIBLE);
                    } else {
                        noContent.setVisibility(View.GONE);
                    }
                } else {
                    {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(FeedActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(FeedActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SportFeedModel>> call, Throwable t) {
            }
        });
    }

    public void requestLikedPage(final int page) {
        Call<List<SportFeedModel>> call = MainActivity.APIBuild().getMyLikedPosts(page + "", "Bearer " +
                getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
        call.enqueue(new Callback<List<SportFeedModel>>() {
            @Override
            public void onResponse(Call<List<SportFeedModel>> call, Response<List<SportFeedModel>> response) {
                if (response.isSuccessful()) {
                    adapter.setSportFeedList(response.body(), page);
                    adapter.notifyDataSetChanged();
                    if (response.body().size() == 0 && page == 1) {
                        noContent.setVisibility(View.VISIBLE);
                    } else {
                        noContent.setVisibility(View.GONE);
                    }
                } else {
                    {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(FeedActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(FeedActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SportFeedModel>> call, Throwable t) {
            }
        });
    }

    public void requestForeignLikedPage(final int page, Integer id) {
        Call<List<SportFeedModel>> call = MainActivity.APIBuild().getForeignLikedPosts(id, page + "", "Bearer " +
                getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
        call.enqueue(new Callback<List<SportFeedModel>>() {
            @Override
            public void onResponse(Call<List<SportFeedModel>> call, Response<List<SportFeedModel>> response) {
                if (response.isSuccessful()) {
                    adapter.setSportFeedList(response.body(), page);
                    adapter.notifyDataSetChanged();
                    if (response.body().size() == 0 && page == 1) {
                        noContent.setVisibility(View.VISIBLE);
                    } else {
                        noContent.setVisibility(View.GONE);
                    }
                } else {
                    {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(FeedActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(FeedActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SportFeedModel>> call, Throwable t) {
            }
        });
    }

    public void requestForeignEvents(final int page, Integer id) {
        MainActivity.showLoadingIndicator(getWindow().getDecorView());
        Call<List<EventModel>> call = MainActivity.APIBuild().getForeignEvents(id, page + "", "Bearer " +
                getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
        call.enqueue(new Callback<List<EventModel>>() {
            @Override
            public void onResponse(Call<List<EventModel>> call, Response<List<EventModel>> response) {
                MainActivity.hideLoadingIndicator(getWindow().getDecorView());
                if (response.isSuccessful()) {
                    eventAdapter.setEventList(response.body());
                    if (response.body().size() == 0 && page == 1) {
                        noContent.setVisibility(View.VISIBLE);
                    } else {
                        noContent.setVisibility(View.GONE);
                    }
                } else {
                    {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(FeedActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(FeedActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        initialisationByType();
    }

    public void draw(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.tutorial, fragment);
        fragmentTransaction.commit();
    }

    public void enterFromRight(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left);
        transaction.replace(R.id.tutorial, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}