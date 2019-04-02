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

import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.LogInFragments.MainLogInFragment;
import io.ideaction.sketchproject.R;

public class LogOutValidationDialogFragment extends DialogFragment {

Button yesButton;
Button noButton;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log_out_validation_dialog, container, false);

        yesButton =  view.findViewById(R.id.yes_button);
        noButton = view.findViewById(R.id.no_button);


        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSharedPreferences("my", Context.MODE_PRIVATE).edit().clear().apply();
                ((MainActivity) getActivity()).draw(new MainLogInFragment());
                dismiss();
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
