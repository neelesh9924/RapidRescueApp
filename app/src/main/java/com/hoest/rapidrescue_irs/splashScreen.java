package com.hoest.rapidrescue_irs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class splashScreen extends Fragment {

      FirebaseAuth mAuth;
      NavController mNavController;
      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_splash_screen, container, false);
      }

      @Override
      public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mNavController= Navigation.findNavController(view);
            mAuth= FirebaseAuth.getInstance();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                        if(mAuth.getCurrentUser()!=null){
                              mNavController.navigate(R.id.action_splashScreen_to_userHomepage);
                        }else{
                              mNavController.navigate(R.id.action_splashScreen_to_enterPhoneNumber);
                        }
                  }
            }, 2000);


      }
}