package io.ideaction.sketchproject.LogInFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.ideaction.sketchproject.Activities.MainActivity;
import io.ideaction.sketchproject.LogInFragments.MainLogInFragment;
import io.ideaction.sketchproject.R;

public class TutorialFragment extends Fragment {
   
   TextView textView;
   TextView smallerTextView;
   Button tutorialButton;
   int viewCounter = 0;
   boolean tutorial = false;
   
   
   @Nullable
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.intro_tutorial_screen, container, false);
      
      textView = view.findViewById(R.id.text_view1_tutorial_screen);
      smallerTextView = view.findViewById(R.id.text_view2_tutorial_screen);
      tutorialButton = view.findViewById(R.id.tutorial_button);
      
      tutorialButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            
            switch (viewCounter) {
               case 0:
                  textView.setText("Stay on top of the latest information");
                  smallerTextView.setText("Each category has a feed of people sharing information, diets, articles, and other important information regarding your preferred workouts.");
                  break;
               case 1:
                  textView.setText("Connect with like-minded healthy peers");
                  smallerTextView.setText("We provide a list of users that are interested in the same workouts as you for an easier way to connect and exchange information.");
                  break;
               case 2:
                  textView.setText("Workout better by doing\n" + " it together");
                  smallerTextView.setText("Our platform allows people to create events and invite other users to join them when on their upcoming workouts and physical acitivities.");
                  break;
               case 3:
                  tutorial = true;
                  getActivity().getSharedPreferences("tutorial", Context.MODE_PRIVATE).edit()
                          .putString("pass", String.valueOf(tutorial)).apply();
                  ((MainActivity)getActivity()).draw(new MainLogInFragment());
            }
            viewCounter++;
         }
      });
      
      return view;
   }
}
