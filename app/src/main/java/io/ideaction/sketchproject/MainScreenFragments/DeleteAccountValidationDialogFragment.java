package io.ideaction.sketchproject.MainScreenFragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.ApiRequests.UtilityClasses.CustomCallBack;
import io.ideaction.sketchproject.LogInFragments.MainLogInFragment;
import io.ideaction.sketchproject.Models.User;
import io.ideaction.sketchproject.R;
import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class DeleteAccountValidationDialogFragment extends DialogFragment {

Button yesButton;
Button noButton;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delete_account_validation_dialog, container, false);

        yesButton =  view.findViewById(R.id.yes_button);
        noButton = view.findViewById(R.id.no_button);


        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("message");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        HashMap chats = (HashMap) dataSnapshot.getValue();
                        for (final Object entry : chats.keySet()) {
                            HashMap chat = (HashMap) chats.get(entry);
                            if (chat.get("participants").toString().split(",")[0].equals(getActivity().getSharedPreferences("my" , MODE_PRIVATE).getString("email" , "xxx")))
                                myRef.child(chat.get("key").toString()).removeValue();
                            else
                            if (chat.get("participants").toString().split(",")[1].equals(getActivity().getSharedPreferences("my" , MODE_PRIVATE).getString("email" , "xxx")))
                                myRef.child(chat.get("key").toString()).removeValue();
                        }



                        Call<User> call = MainActivity.APIBuild().deleteAccount("Bearer " +
                                getActivity().getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
                        call.enqueue(new CustomCallBack<User>(getActivity()) {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                super.onResponse(call , response);
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                super.onFailure(call , t);
                            }
                        });
                        getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).edit().clear().apply();
                        ((MainActivity) getActivity()).draw(new MainLogInFragment());
                        dismiss();

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });


            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return view;

    }
}
