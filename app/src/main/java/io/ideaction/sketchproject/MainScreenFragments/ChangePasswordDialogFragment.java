package io.ideaction.sketchproject.MainScreenFragments;

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
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import java.util.HashMap;
import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.R;
import io.ideaction.sketchproject.Validations.Validations;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ChangePasswordDialogFragment extends DialogFragment {
   
   Button saveButton;
   EditText oldPassword;
   EditText newPassword;
   EditText repeatNewPassword;
   
   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.change_password_dialog, container, false);
      
      saveButton = view.findViewById(R.id.save_new_password);
      oldPassword = view.findViewById(R.id.old_password);
      newPassword = view.findViewById(R.id.new_password);
      repeatNewPassword = view.findViewById(R.id.repeat_new_password);
      
      saveButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Call<HashMap> changePassword = MainActivity.APIBuild().changePassword(
                    oldPassword.getText().toString(),newPassword.getText().toString(),
                    repeatNewPassword.getText().toString(), "Bearer " + getActivity()
                            .getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
            changePassword.enqueue(new Callback<HashMap>() {
               @Override
               public void onResponse(Call<HashMap> call, Response<HashMap> response) {
                  if (response.isSuccessful()) {
                     Validations.hideKeyboard(getActivity());
                     Toast.makeText(getActivity(), "The password has been changed", Toast.LENGTH_LONG).show();
                     dismiss();
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
               public void onFailure(Call<HashMap> call, Throwable t) {
                  Validations.hideKeyboard(getActivity());
               }
            });
         }
      });
      getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      return view;
   }
   
   @Override
   public void onDetach() {
      super.onDetach();
      Validations.hideKeyboard(getActivity());
   }
}
