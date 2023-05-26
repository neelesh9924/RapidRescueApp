package com.hoest.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class loginFlowViewModel extends ViewModel {
      public String phoneNumberWithCountryCode = "";

      MutableLiveData<String> saveUserData = new MutableLiveData<>();

      public LiveData<String> setNewUserDetails(String name, String email, String bloodGroup, String state, String City, String address) {
            Map<String,Object> data=new HashMap<>();
            data.put("name",name);
            data.put("email",email);
            data.put("bloodGroup",bloodGroup);
            data.put("state",state);
            data.put("City",City);
            data.put("address",address);
            data.put("phoneNumber",phoneNumberWithCountryCode);
            data.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());

            if(saveUserData==null){
                  saveUserData=new MutableLiveData<>();
            }
            saveUserDataToServer(data);
            return saveUserData;
      }

      private void saveUserDataToServer(Map<String, Object> data) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(data).addOnCompleteListener(task -> {
                  if(task.isSuccessful()){
                        saveUserData.postValue("success");
                  }else{
                        saveUserData.postValue("failed");
                  }
            });
      }


}
