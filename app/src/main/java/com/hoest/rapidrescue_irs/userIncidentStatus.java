package com.hoest.rapidrescue_irs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adapters.userIncidentsAdapter;
import com.hoest.rapidrescue_irs.databinding.FragmentUserIncidentStatusBinding;
import com.hoest.viewModels.incidentsViewModel;


public class userIncidentStatus extends Fragment {
      FragmentUserIncidentStatusBinding binding;
      incidentsViewModel model;

      LinearLayoutManager mLinearLayoutManager;

       userIncidentsAdapter adapter;

       NavController mNavController;



      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
            binding = FragmentUserIncidentStatusBinding.inflate(inflater, container, false);
            return binding.getRoot();
      }

      @Override
      public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mNavController = Navigation.findNavController(view);
            mLinearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);

            model=new ViewModelProvider(requireActivity()).get(incidentsViewModel.class);
            model.init();

            adapter = new userIncidentsAdapter(mNavController,model.allUserIncidents,requireContext(),model);
            binding.userIncidentsrecyclerView.setLayoutManager(mLinearLayoutManager);
            binding.userIncidentsrecyclerView.setAdapter(adapter);


            model.getIncidents().observe(getViewLifecycleOwner(), incident -> {
                  Log.i("incidents","size:"+incident.size());
                  binding.userIncidentsrecyclerView.getAdapter().notifyDataSetChanged();
            });



      }
}