package io.ideaction.sketchproject.MainScreenFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.ideaction.sketchproject.Activities.BlackList;
import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.Models.User;
import io.ideaction.sketchproject.R;
import io.ideaction.sketchproject.Validations.Validations;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends Fragment {

    TextView saveButton;
    TextView email;
    TextView numberOfCharacters;
    Button backArrow;
    Button suspendButton;
    Button logoutButton;
    LinearLayout passwordButton;
    String[] items = new String[]{
            "Morning person",
            "Afternoon person",
            "Evening person"
    };
    ArrayAdapter<String> spinnerAdapter;
    Spinner preferredTimeToWorkOut;
    EditText name;
    EditText username;
    TextView city;
    TextView prefGym;
    EditText description;
    SharedPreferences sharedPreferences;
    String cityStr = "";
    String countryStr = "";
    String prefGymStr = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.settings_screen, container, false);

        sharedPreferences = getActivity().getSharedPreferences("my", Context.MODE_PRIVATE);
        saveButton = view.findViewById(R.id.save_button);
        suspendButton = view.findViewById(R.id.suspend_button);
        logoutButton = view.findViewById(R.id.logout_button);
        backArrow = view.findViewById(R.id.back_arrow);
        numberOfCharacters = view.findViewById(R.id.number_of_characters);
        preferredTimeToWorkOut = view.findViewById(R.id.spinner);
        spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, items);
        preferredTimeToWorkOut.setAdapter(spinnerAdapter);
        name = view.findViewById(R.id.name);
        username = view.findViewById(R.id.user_name);
        city = view.findViewById(R.id.city);
        prefGym = view.findViewById(R.id.preferred_gym);
        description = view.findViewById(R.id.description);
        passwordButton = view.findViewById(R.id.password);
        email = view.findViewById(R.id.email);
        prefGym.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                preferredTimeToWorkOut.setFocusable(true);
                preferredTimeToWorkOut.setFocusableInTouchMode(true);
                return false;
            }
        });

        name.setText(sharedPreferences.getString("name", ""));
        username.setText(sharedPreferences.getString("username", ""));
        cityStr = sharedPreferences.getString("city", "");
        countryStr = sharedPreferences.getString("country", "");

        if (!cityStr.equals(""))
            city.setText(cityStr + ", " + countryStr);
        prefGym.setText(sharedPreferences.getString("training_place", ""));
        description.setText(sharedPreferences.getString("description", ""));
        email.setText(sharedPreferences.getString("email", ""));
        name.requestFocus();
        name.setSelection(name.getText().length());
        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                username.requestFocus();
                username.setSelection(username.getText().length());
                return false;
            }
        });
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (description.getText().length() > 189) {
                    Validations.hideKeyboard(getActivity());
                    Toast.makeText(getActivity(), "The description length should be not more than 190 characters", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                numberOfCharacters.setText(description.getText().length() + "/190");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        username.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                getPLaces();
                return false;
            }
        });

        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPLaces();
            }
        });

        prefGym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPLaces();
            }
        });

        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordDialogFragment editNameDialogFragment = new ChangePasswordDialogFragment();
                editNameDialogFragment.show(getFragmentManager(), "change_fragment_dialog");
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).enterFromLeft(new ProfileScreenFragment());
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                saveButton.setEnabled(false);
                Call<User> sendSettings = MainActivity.APIBuild().sendSettings(name.getText().toString(),
                        username.getText().toString(), description.getText().toString(), cityStr,
                        countryStr, prefGym.getText().toString(), preferredTimeToWorkOut.getSelectedItem().toString(),
                        "Bearer " + getActivity().getSharedPreferences("my", MODE_PRIVATE).getString("token", ""));
                sendSettings.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            sharedPreferences.edit().putString("name", name.getText().toString()).apply();
                            sharedPreferences.edit().putString("username", username.getText().toString()).apply();
                            sharedPreferences.edit().putString("city", cityStr).apply();
                            sharedPreferences.edit().putString("country", countryStr).apply();
                            sharedPreferences.edit().putString("description", description.getText().toString()).apply();
                            sharedPreferences.edit().putString("training_place", prefGym.getText().toString()).apply();
                            sharedPreferences.edit().putString("training_hours", preferredTimeToWorkOut.getSelectedItem().toString()).apply();
                            Validations.hideKeyboard(getActivity());
                            new CountDownTimer(500,1000){

                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {
                                    saveButton.setEnabled(true);
                                }
                            }.start();
                            ((MainActivity) getActivity()).enterFromLeft(new ProfileScreenFragment());

                        } else {
                            saveButton.setEnabled(true);
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
                    public void onFailure(Call<User> call, Throwable t) {
                        Validations.hideKeyboard(getActivity());
                        saveButton.setEnabled(true);
                    }
                });
            }
        });

        suspendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteAccountValidationDialogFragment deleteAccountValidationDialogFragment = new DeleteAccountValidationDialogFragment();
                deleteAccountValidationDialogFragment.show(getFragmentManager(), "change_fragment_dialog");
                suspendButton.setEnabled(false);
                new CountDownTimer(500, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        suspendButton.setEnabled(true);
                    }
                }
                        .start();

            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOutValidationDialogFragment logOutValidationDialogFragment = new LogOutValidationDialogFragment();
                logOutValidationDialogFragment.show(getFragmentManager(), "change_fragment_dialog");
                logoutButton.setEnabled(false);
                new CountDownTimer(500, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        logoutButton.setEnabled(true);
                    }
                }
                        .start();
            }
        });





        Button gotoblacklist = view.findViewById(R.id.gotoblacklist);
        gotoblacklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity() , BlackList.class));
            }
        });


        return view;
    }

    int PLACE_PICKER_REQUEST = 1;

    void getPLaces() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());


                try {
                    Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
                    List<Address> addresses = null;
                    addresses = gcd.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    if (addresses.size() > 0) {
                        System.out.println(addresses.get(0).getLocality());
                        cityStr = addresses.get(0).getLocality();
                        countryStr = addresses.get(0).getCountryName();
                        prefGymStr = place.getName().toString();
                        city.setText(cityStr + ", " + countryStr);
                        prefGym.setText(prefGymStr);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                prefGym.requestFocus();
                //  prefGym.setSelection(prefGym.getText().length());
            }
        }
    }
}