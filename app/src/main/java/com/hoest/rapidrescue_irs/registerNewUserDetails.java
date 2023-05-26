package com.hoest.rapidrescue_irs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hoest.rapidrescue_irs.databinding.FragmentRegisterNewUserDetailsBinding;
import com.hoest.viewModels.loginFlowViewModel;


public class registerNewUserDetails extends Fragment {
      FragmentRegisterNewUserDetailsBinding binding;
      loginFlowViewModel model;

      String name="", email="", bloodGroup="", state="", City="", address="";
      boolean nameOK=false, emailOK=false, bloodGroupOK=false, stateOK=false, CityOK=false, addressOK=false;

      NavController mNavController;

      FirebaseAuth mAuth;



      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            binding=FragmentRegisterNewUserDetailsBinding.inflate(inflater, container, false);
            return binding.getRoot();
      }


      @Override
      public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            model=new ViewModelProvider(requireActivity()).get(loginFlowViewModel.class);
            mNavController= Navigation.findNavController(view);
            addTextWatchers();
            mAuth=FirebaseAuth.getInstance();

            binding.continueButton.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                        if (!nameOK){
                              binding.fullNameLayout.setError("Invalid name");
                        }
                        if (!emailOK){
                              binding.emailLayout.setError("Invalid email");
                        }
                        if (!bloodGroupOK){
                              binding.bloodGroupLayout.setError("Invalid blood group");
                        }
                        if (!stateOK){
                              binding.stateLayout.setError("Invalid state");
                        }
                        if (!CityOK){
                              binding.cityLayout.setError("Invalid city");
                        }
                        if (!addressOK){
                              binding.fullAddressLayout .setError("Invalid address");
                        }
                        if(nameOK && emailOK && bloodGroupOK && stateOK && CityOK && addressOK){


                              model.setNewUserDetails(name, email, bloodGroup, state, City, address).observe(getViewLifecycleOwner(), new Observer<String>() {
                                    @Override
                                    public void onChanged(String s) {
                                          if(s.equals("success")){
                                                mNavController.navigate(R.id.action_registerNewUserDetails_to_userHomepage);
                                          } else if (s.equals("failed")){
                                                Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                                          }
                                    }
                              });

                  }
            }});
      }

      private void addTextWatchers() {
            binding.fullNameEditText.addTextChangedListener(new TextWatcher() {
                  @Override
                  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                  }

                  @Override
                  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        name=charSequence.toString();
                        nameOK= name.length() > 3;
                  }

                  @Override
                  public void afterTextChanged(Editable editable) {

                  }
            });

            binding.emailEditText.addTextChangedListener(new TextWatcher() {
                  @Override
                  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                  }

                  @Override
                  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        email=charSequence.toString();
                        emailOK= email.contains("@") && email.contains(".");
                  }

                  @Override
                  public void afterTextChanged(Editable editable) {

                  }
            });

            binding.bloodGroupEditText.addTextChangedListener(new TextWatcher() {
                  @Override
                  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                  }

                  @Override
                  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        bloodGroup=charSequence.toString();
                        bloodGroupOK= bloodGroup.equals("A+") || bloodGroup.equals("A-") || bloodGroup.equals("B+") || bloodGroup.equals("B-") || bloodGroup.equals("AB+") || bloodGroup.equals("AB-") || bloodGroup.equals("O+") || bloodGroup.equals("O-");
                  }

                  @Override
                  public void afterTextChanged(Editable editable) {

                  }
            });

            binding.stateEditText.addTextChangedListener(new TextWatcher() {
                  @Override
                  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                  }

                  @Override
                  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        state=charSequence.toString();
                        stateOK= state.length()>0;
                  }

                  @Override
                  public void afterTextChanged(Editable editable) {

                  }
            });

            binding.cityEditText.addTextChangedListener(new TextWatcher() {
                  @Override
                  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                  }

                  @Override
                  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        City=charSequence.toString();
                        CityOK= City.length()>0;
                  }

                  @Override
                  public void afterTextChanged(Editable editable) {

                  }
            });

            binding.fullAddressEditText.addTextChangedListener(new TextWatcher() {
                  @Override
                  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                  }

                  @Override
                  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        address=charSequence.toString();
                        addressOK= address.length()>0;
                  }

                  @Override
                  public void afterTextChanged(Editable editable) {

                  }
            });

      }
}