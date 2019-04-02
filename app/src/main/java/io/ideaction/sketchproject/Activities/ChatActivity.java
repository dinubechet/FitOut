package io.ideaction.sketchproject.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.ideaction.sketchproject.Adapters.ChatAdapter;
import io.ideaction.sketchproject.Models.ChatModel;
import io.ideaction.sketchproject.R;
import io.ideaction.sketchproject.Validations.Validations;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    List<HashMap> chatList = new ArrayList<>();

    ListView listView;
    ChatAdapter adapter;
    Button send_button;
    RelativeLayout backToMessages;
    EditText message_input_line;
    TextView name_of_the_partner;
    TextView noContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        message_input_line = findViewById(R.id.message_input_line);
        listView = findViewById(R.id.list_of_messages);
        backToMessages = findViewById(R.id.back_to_messages_arrow);
        send_button = findViewById(R.id.send_button);
        noContent = findViewById(R.id.no_content);
        name_of_the_partner = findViewById(R.id.name_of_the_partner);
        name_of_the_partner.setText(getIntent().getStringExtra("name"));
        adapter = new ChatAdapter(this, getIntent().getStringExtra("img") , getIntent().getStringExtra("key"));
        listView.setAdapter(adapter);

        backToMessages.setOnClickListener(v -> {
            finish();
            Validations.hideKeyboard(ChatActivity.this);
        });
        send_button.setEnabled(false);

        message_input_line.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int i;
                for (i = 0; i < s.length() && Character.isWhitespace(s.charAt(i)); i++) {
                }
                s.replace(0, i, "");
                if (s.toString().isEmpty()) {
                    send_button.setEnabled(false);
                    send_button.setTextColor(Color.parseColor("#B5B5B5"));
                } else {
                    send_button.setEnabled(true);
                    send_button.setTextColor(Color.parseColor("#000000"));
                }
            }
        });
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("message").child(getIntent().getStringExtra("key")).child("messages");

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap hashMap = new HashMap();
                hashMap.put("text", message_input_line.getText().toString());
                hashMap.put("userEmail", getSharedPreferences("my", MODE_PRIVATE).getString("email", ""));
                hashMap.put("time", System.currentTimeMillis() / 1000);

               //TODO added
                String mGroupId = myRef.push().getKey();
                hashMap.put("key", mGroupId);
                myRef.child(mGroupId).setValue(hashMap);



                DatabaseReference myRef2 = database.getReference("message").child(getIntent().getStringExtra("key")).child("lastMessage");
                myRef2.setValue(message_input_line.getText().toString());

                DatabaseReference myRef3 = database.getReference("message").child(getIntent().getStringExtra("key")).child("lastMessageTime");
                myRef3.setValue(System.currentTimeMillis() / 1000);

                Call<Void> sendNot = MainActivity.APIBuild().sendNotification("Bearer " + getSharedPreferences("my", MODE_PRIVATE).getString("token", ""), getIntent().getStringExtra("em"), message_input_line.getText().toString(), getSharedPreferences("my", MODE_PRIVATE).getString("name", ""));
                sendNot.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                    }
                });

                message_input_line.setText("");
            }
        });

        getChat(getIntent().getStringExtra("key"));
    }

    void getChat(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message").child(key).child("messages");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null)
                    return;
                chatList.clear();
                HashMap chats = (HashMap) dataSnapshot.getValue();
                for (final Object entry : chats.keySet()) {
                    HashMap chat = (HashMap) chats.get(entry);
                    chatList.add(chat);
                }

                Collections.sort(chatList, new Comparator<HashMap>() {
                    public int compare(HashMap obj1, HashMap obj2) {
                        return (Double.valueOf(obj1.get("time").toString()).compareTo(Double.valueOf(obj2.get("time").toString())));
                    }
                });
                adapter.setChatList(chatList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }
}
