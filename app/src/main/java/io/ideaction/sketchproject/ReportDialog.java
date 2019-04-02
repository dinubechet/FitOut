package io.ideaction.sketchproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class ReportDialog extends DialogFragment {


    String type;
    String id;

    public ReportDialog(String type, String id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View viewg = inflater.inflate(R.layout.dialog_report, null);
        builder.setView(viewg);


        TextView title = viewg.findViewById(R.id.title);
        EditText reason = viewg.findViewById(R.id.reason);
        Button submit = viewg.findViewById(R.id.submit);


        title.setText("Why do you want to report this "+type + "?");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO implement api

                getDialog().dismiss();


            }
        });


        return builder.create();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

}
