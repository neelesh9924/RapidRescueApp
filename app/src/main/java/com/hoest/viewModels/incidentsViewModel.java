package com.hoest.viewModels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hoest.pojos.incident;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class incidentsViewModel extends ViewModel {
      private MutableLiveData<ArrayList<incident>> incidentMutableLiveData;
      public ArrayList<incident> allUserIncidents;


      private String uid;

      public incident selectedIncident;

      public void init(){
            uid= FirebaseAuth.getInstance().getUid();
            if(incidentMutableLiveData!=null){
                  return;
            }
            allUserIncidents=new ArrayList<>();
            incidentMutableLiveData=new MutableLiveData<>();
      }


      public LiveData<ArrayList<incident>> getIncidents(){

            FirebaseFirestore db=FirebaseFirestore.getInstance();
            db.collection("incidents").whereEqualTo("uid",uid).get().addOnCompleteListener(task -> {
                  if(task.isSuccessful()){
                        Log.i("incidents","size:"+task.getResult().size());
                        allUserIncidents.clear();
                        allUserIncidents.addAll(task.getResult().toObjects(incident.class));
                        incidentMutableLiveData.setValue(allUserIncidents);
                  }
            });
            return incidentMutableLiveData;
      }
}
