package io.ideaction.sketchproject.MainScreenFragments;

import android.annotation.SuppressLint;
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

import org.json.JSONObject;

import java.util.List;

import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.Adapters.FollowingFollowersAdapter;
import io.ideaction.sketchproject.Models.User;
import io.ideaction.sketchproject.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

@SuppressLint("ValidFragment")
public class FollowersFollowingFragment extends Fragment {

    Button backButton;
    ListView followingFollowersListView;
    TextView noContent;
    TextView title;
    FollowingFollowersAdapter adapter;
    int page = 1;
    int id;
    int from;
    String type;

    public FollowersFollowingFragment(int id, String type, int from) {
        this.id = id;
        this.type = type;
        this.from = from;
    }

    public int getMyId() {
        return id;
    }

    public String getType() {
        return type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.following_followers_fragment, container, false);

        backButton = view.findViewById(R.id.back_arrow);
        title = view.findViewById(R.id.title);
        followingFollowersListView = view.findViewById(R.id.following_followers_list_view);
        adapter = new FollowingFollowersAdapter(getActivity());
        followingFollowersListView.setAdapter(adapter);
        noContent = view.findViewById(R.id.no_content);

        if (getType().equals("following")) {
            title.setText("Following");
            requestMyFollowingList(getMyId(), view);
        } else {
            title.setText("Followers");
            requestMyFollowersList(getMyId(), view);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(from == 1) {
                    getActivity().onBackPressed();
                } else {
                    ((MainActivity)getActivity()).enterFromLeft(new ProfileScreenFragment());
                }
            }
        });

        return view;
    }

    public void requestMyFollowersList(final int id, final View view) {
        MainActivity.showLoadingIndicator(view);
        Call<List<User>> call = MainActivity.APIBuild().getFollowersList(id, "Bearer " +
                getActivity().getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                MainActivity.hideLoadingIndicator(view);
                if (response.isSuccessful()) {
                    adapter.setPeopleFeedList(response.body(), page);
                    if (adapter.getCount() == 0) {
                        noContent.setVisibility(View.VISIBLE);
                    } else {
                        noContent.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
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
            public void onFailure(Call<List<User>> call, Throwable t) {
                MainActivity.hideLoadingIndicator(view);
                Toast.makeText(getActivity(), "Check your internet connection or our server is down!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void requestMyFollowingList(final int id, final View view) {
        MainActivity.showLoadingIndicator(view);
        Call<List<User>> call = MainActivity.APIBuild().getFollowingsList(id, "Bearer " +
                getActivity().getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                MainActivity.hideLoadingIndicator(view);
                if (response.isSuccessful()) {
                    adapter.setPeopleFeedList(response.body(), page);
                    if (adapter.getCount() == 0) {
                        noContent.setVisibility(View.VISIBLE);
                    } else {
                        noContent.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
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
            public void onFailure(Call<List<User>> call, Throwable t) {
                MainActivity.hideLoadingIndicator(view);
                Toast.makeText(getActivity(), "Check your internet connection or our server is down!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
