package com.hoest.rapidrescue_irs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoest.rapidrescue_irs.databinding.FragmentEnterPhoneNumberBinding;
import com.hoest.viewModels.loginFlowViewModel;


public class enterPhoneNumber extends Fragment {

      String phoneNumber="";

      FragmentEnterPhoneNumberBinding mBinding;

      loginFlowViewModel mLoginFlowViewModel;

      NavController mNavController;

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            mBinding= FragmentEnterPhoneNumberBinding.inflate(inflater, container, false);
            return mBinding.getRoot();
      }

      @Override
      public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mNavController= Navigation.findNavController(view);
            mLoginFlowViewModel=new ViewModelProvider(requireActivity()).get(loginFlowViewModel.class);
            addTextWatcher();
            mBinding.sentOtpButton.setEnabled(phoneNumber.length()==10);
            mBinding.sentOtpButton.setOnClickListener(view1 -> {

                  if(phoneNumber.length()==10){
                        mLoginFlowViewModel.phoneNumberWithCountryCode ="+91"+phoneNumber;
                        mNavController.navigate(R.id.action_enterPhoneNumber_to_enterOtp);
                  }else{
                        mBinding.phoneNumberInputLayout.setError("Please enter a valid phone number");
                  }
            });


      }

      private void addTextWatcher() {
            mBinding.phoneNumberEditText.addTextChangedListener(new TextWatcher() {
                  @Override
                  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                  }

                  @Override
                  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        phoneNumber=charSequence.toString();
                        mBinding.phoneNumberInputLayout.setError("");
                        mBinding.sentOtpButton.setEnabled(phoneNumber.length() == 10);
                  }

                  @Override
                  public void afterTextChanged(Editable editable) {

                  }
            });
      }
}