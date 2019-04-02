package io.ideaction.sketchproject.ApiRequests.UtilityClasses;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import io.ideaction.sketchproject.R;

public class LoadingDialog extends DialogFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_loading, container, false);
        ProgressBar loading_indicator = v.findViewById(R.id.loading_indicator);
        //Set the color programatecly if you want
        loading_indicator.getIndeterminateDrawable()
                .setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
       setCancelable(false);
        return v;
    }



}
