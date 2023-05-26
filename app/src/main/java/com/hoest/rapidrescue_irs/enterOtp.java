package com.hoest.rapidrescue_irs;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hoest.rapidrescue_irs.databinding.FragmentEnterOtpBinding;
import com.hoest.viewModels.loginFlowViewModel;

import java.util.concurrent.TimeUnit;


public class enterOtp extends Fragment {

      FragmentEnterOtpBinding binding;
      loginFlowViewModel mLoginFlowViewModel;

      String phoneNumber;


      private static final String TAG = "enterOtpFragment";

      // [START declare_auth]
      private FirebaseAuth mAuth;
      // [END declare_auth]

      private String mVerificationId;
      private PhoneAuthProvider.ForceResendingToken mResendToken;
      private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
      private NavController navController;
      private View rootView;
      private boolean otpSent;
      private String userEnteredOtp;


      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            binding = FragmentEnterOtpBinding.inflate(inflater, container, false);
            return binding.getRoot();
      }

      @Override
      public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mLoginFlowViewModel = new ViewModelProvider(requireActivity()).get(loginFlowViewModel.class);
            mAuth = FirebaseAuth.getInstance();

            binding.resendOtpButton.setEnabled(false);
            navController = Navigation.findNavController(view);

            rootView = view;


            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                  @Override
                  public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        Log.d(TAG, "onVerificationCompleted:" + credential);
                        signInWithPhoneAuthCredential(credential);
                  }

                  @Override
                  public void onVerificationFailed(@NonNull FirebaseException e) {

                        Log.w(TAG, "onVerificationFailed", e);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                              // Invalid request
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                              // The SMS quota for the project has been exceeded
                        } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                              // reCAPTCHA verification attempted with null Activity
                        }

                  }

                  @Override
                  public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        // The SMS verification code has been sent to the provided phone number, we
                        // now need to ask the user to enter the code and then construct a credential
                        // by combining the code with a verification ID.

                        Log.d(TAG, "onCodeSent:" + verificationId);
                        try {
                              Snackbar snackbar = Snackbar
                                      .make(rootView, "Code Sent!", Snackbar.LENGTH_SHORT);
                              snackbar.show();
                        } catch (IllegalArgumentException ignored) {
                        }
                        startClock();
                        // Save verification ID and resending token so we can use them later
                        mVerificationId = verificationId;
                        mResendToken = token;

                        binding.enterOTPLayout.setEnabled(true);

                        otpSent = true;
                  }
            };

            phoneNumber = mLoginFlowViewModel.phoneNumberWithCountryCode;

            sendOTP(phoneNumber);

            setListeners();
      }

      private void startClock() {

            new CountDownTimer(60000, 1000) {

                  public void onTick(long millisUntilFinished) {
                        binding.resendOtpButton.setText("Resend after " + millisUntilFinished / 1000);
                  }

                  public void onFinish() {
                        binding.resendOtpButton.setText("Resend OTP");
                        binding.resendOtpButton.setEnabled(true);
                  }

            }.start();
      }


      private void setListeners() {
            binding.resendOtpButton.setOnClickListener(view1 -> {
                  resendVerificationCode(phoneNumber, mResendToken);
            });


            binding.otpPageContinueButton.setOnClickListener(view1 -> {
                  if (otpSent) {
                        if (userEnteredOtp.length() == 6) {
                              //showProgressIndicator();
                              verifyPhoneNumberWithCode(mVerificationId, userEnteredOtp);
                        } else {
                              binding.enterOTPLayout.setError("Invalid OTP");
                        }
                  } else {
                        binding.enterOTPEditText.setText("");
                        try {
                              Snackbar snackbar = Snackbar
                                      .make(rootView, "Please wait while we send you the OTP.", Snackbar.LENGTH_SHORT);
                              snackbar.show();
                        } catch (IllegalArgumentException | IllegalStateException ignored) {
                        }
                  }


            });

            /*mBindings.textView5s.setOnClickListener(view1 -> {
                  Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://pandaal.in/t&c.html"));
                  startActivity(browserIntent);
            });*/

            binding.enterOTPEditText.addTextChangedListener(new TextWatcher() {
                  @Override
                  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                  }

                  @Override
                  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        binding.enterOTPLayout.setError("");
                        userEnteredOtp = charSequence.toString();
                  }

                  @Override
                  public void afterTextChanged(Editable editable) {

                  }
            });
      }


      private void verifyPhoneNumberWithCode(String verificationId, String code) {

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithPhoneAuthCredential(credential);
      }

      private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber(phoneNumber)       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(requireActivity())                 // Activity (for callback binding)
                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                    .setForceResendingToken(token)     // ForceResendingToken from callbacks
                    .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
      }

      private void sendOTP(String phoneNumber) {
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber(phoneNumber)       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(requireActivity())                 // (optional) Activity for callback binding

                    .setCallbacks(mCallbacks).build();
            PhoneAuthProvider.verifyPhoneNumber(options);
      }

      private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

            mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                              //update the UI
                              //Sign-in Successful
                              //showProgressIndicator();
                              FirebaseUser user = task.getResult().getUser();

                              if (user != null) userLoggedIn(user.getUid());

                        } else {
                              //hideProgressIndicator();
                              // Sign in failed, display a message and update the UI
                              if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    // The verification code entered was invalid
                                    binding.enterOTPEditText.setText("");
                                    binding.enterOTPLayout.setError("Wrong OTP");

                              } else if (task.getException() instanceof FirebaseTooManyRequestsException) {
                                    try {
                                          Snackbar snackbar = Snackbar.make(rootView, "OTP request Limit exceeded,\nPlease try again later..", Snackbar.LENGTH_SHORT);
                                          snackbar.show();
                                    } catch (IllegalArgumentException |
                                             IllegalStateException ignored) {
                                    }

                              }
                        }
                  }
            });
      }

      private void userLoggedIn(String uid) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                              DocumentSnapshot document = task.getResult();
                              if(document.exists()){
                                    navController.navigate(R.id.action_enterOtp_to_userHomepage);
                              }else{
                                    navController.navigate(R.id.action_enterOtp_to_registerNewUserDetails);
                              }
                        }else{
                              task.getException().printStackTrace();
                        }
                  }
            });
      }
}