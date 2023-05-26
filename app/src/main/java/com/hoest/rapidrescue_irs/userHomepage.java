package com.hoest.rapidrescue_irs;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.hoest.rapidrescue_irs.databinding.FragmentUserHomepageBinding;


public class userHomepage extends Fragment {
      FragmentUserHomepageBinding binding;
      NavController mNavController;

      private String userSelectedNumber="";



      private ActivityResultLauncher<String> requestPhoneCallLauncher =
              registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
                    if (result) {
                          // Permission granted, start the phone call
                          makePhoneCall(userSelectedNumber);
                    } else {
                          // Permission denied
                          Toast.makeText(getActivity(), "Phone call permission denied", Toast.LENGTH_SHORT).show();
                    }
              });

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            binding=FragmentUserHomepageBinding.inflate(inflater, container, false);
            return binding.getRoot();
      }

      @Override
      public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mNavController= Navigation.findNavController(view);

            binding.reportIncidentButton.setOnClickListener(view1 -> {
                    mNavController.navigate(R.id.action_userHomepage_to_userReportIncident);
            });

            binding.viewIncidentStatusButton.setOnClickListener(view1 -> {
                  mNavController.navigate(R.id.action_userHomepage_to_userIncidentStatus);
            });

            binding.viewNotificationsButton.setOnClickListener(view1 -> {
                  mNavController.navigate(R.id.action_userHomepage_to_userNotifications);
            });

            binding.viewProfileButton.setOnClickListener(view1 -> {
                  mNavController.navigate(R.id.action_userHomepage_to_profileOptions);
            });


            getUserProfile();

            binding.roadAccidentHelplineButton.setOnClickListener(view1 -> {
                  makePhoneCall("1073");
            });

            binding.fireDepartmentButton.setOnClickListener(view1 -> {
                  makePhoneCall("101");
            });

            binding.policeButton.setOnClickListener(view1 -> {
                  makePhoneCall("100");
            });

            binding.ambulanceButton.setOnClickListener(view1 -> {
                  makePhoneCall("102");
            });

            binding.disasterManagementButton.setOnClickListener(view1 -> {
                  makePhoneCall("108");
            });

            binding.womenHelplineButton.setOnClickListener(view1 -> {
                  makePhoneCall("1091");
            });

            binding.childHelplineButton.setOnClickListener(view1 -> {
                  makePhoneCall("1098");
            });




      }

      private void makePhoneCall(String phoneNumber) {
            userSelectedNumber=phoneNumber;

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
            builder.setTitle("Call Emergency number");
            builder.setMessage("Are you sure you want to call this number?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                  Intent intent = new Intent(Intent.ACTION_CALL);
                  intent.setData(Uri.parse("tel:" + phoneNumber));

                  if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        startActivity(intent);
                  } else {
                        // Request permission to make a phone call
                        requestPhoneCallLauncher.launch(android.Manifest.permission.CALL_PHONE);
                  }

            });

            builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());

            builder.show();

      }

      private void getUserProfile() {
            if(!(FirebaseAuth.getInstance().getCurrentUser()==null)){
                  FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get(Source.CACHE).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                              DocumentSnapshot documentSnapshot=task.getResult();
                              binding.heyuserTextView.setText("Hey "+documentSnapshot.getString("name")+"!");
                              binding.userNameTopProfileOptions.setText(documentSnapshot.getString("name"));
                        }
                  });
            }


      }
}