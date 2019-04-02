package io.ideaction.sketchproject.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

import io.ideaction.sketchproject.Adapters.BlockedAdapter;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.CustomCallBack;
import io.ideaction.sketchproject.Models.User;
import io.ideaction.sketchproject.R;
import retrofit2.Call;
import retrofit2.Response;

public class BlackList extends AppCompatActivity {

    private ListView       blackList;
    private TextView       no_content;
    private RelativeLayout back_arrow;
    private BlockedAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
        blackList   = findViewById(R.id.black_list);
        no_content  = findViewById(R.id.no_content);
        back_arrow  = findViewById(R.id.back_arrow);
        fetchData();
        setCallBack();
    }


    private void setCallBack() {
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fetchData() {

        Call<List<User>> fetch = MainActivity.APIBuild().getBlockedUsers("Bearer " + getSharedPreferences("my" , MODE_PRIVATE).getString("token" , ""));
        fetch.enqueue(new CustomCallBack<List<User>>(this) {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                super.onResponse(call , response);
                adapter = new BlockedAdapter(BlackList.this , response.body());
                blackList.setAdapter(adapter);
                adapter.notifyDataSetChanged();


                if (response.body().size() == 0) {
                    no_content.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                super.onFailure(call , t);
            }
        });


    }
}
