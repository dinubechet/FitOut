package io.ideaction.sketchproject.MainScreenFragments;

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
import io.ideaction.sketchproject.Activities.SportActivity;
import io.ideaction.sketchproject.Adapters.PartnersAdapter;
import io.ideaction.sketchproject.Models.User;
import io.ideaction.sketchproject.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class PartnersFragment extends Fragment {

    Button backButton;
    ListView partnersListView;
    TextView noContent;
    PartnersAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.partner_fragment, container, false);

        backButton = view.findViewById(R.id.back_arrow);
        partnersListView = view.findViewById(R.id.partners_list_view);
        adapter = new PartnersAdapter((MainActivity) getActivity(),PartnersFragment.this , view);
        partnersListView.setAdapter(adapter);
        noContent = view.findViewById(R.id.no_content);
        requestMyPartnersPage(1, view);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).draw(new ProfileScreenFragment());
            }
        });

        return view;
    }

    public void requestMyPartnersPage(final int page, final View view) {
        MainActivity.showLoadingIndicator(view);
        Call<List<User>> call = MainActivity.APIBuild().getMyPartners("", page + "", "Bearer " +
                getActivity().getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                MainActivity.hideLoadingIndicator(view);
                if (response.isSuccessful()) {
                    adapter.setPartnersFeedList(response.body(), page);
                    if(adapter.getCount() == 0){
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
