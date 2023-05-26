package com.hoest.rapidrescue_irs;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hoest.rapidrescue_irs.NavGraphDirections;
import com.hoest.rapidrescue_irs.databinding.FragmentViewImageBinding;
import com.hoest.rapidrescue_irs.databinding.FragmentViewIncidentBinding;
import com.hoest.viewModels.incidentsViewModel;


public class viewIncident extends Fragment {
      FragmentViewIncidentBinding binding;
      incidentsViewModel model;


      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            binding = FragmentViewIncidentBinding.inflate(inflater, container, false);
            return binding.getRoot();
      }

      @Override
      public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            model=new ViewModelProvider(requireActivity()).get(incidentsViewModel.class);
            model.init();

            Glide.with(requireContext())
                    .load(model.selectedIncident.getIncidentPhotoUri())
                    .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade())
                    .listener(new RequestListener<Drawable>() {
                          @Override
                          public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                          }

                          @Override
                          public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                          }
                    })
                    .into(binding.imageViewLayoutUserIncidents);

            binding.imageViewLayoutUserIncidents.setOnClickListener(view1 -> {
                    com.hoest.rapidrescue_irs.NavGraphDirections.ActionGlobalViewImage action=NavGraphDirections.actionGlobalViewImage(model.selectedIncident.getIncidentPhotoUri());
                    action.setPhotoUrl(model.selectedIncident.getIncidentPhotoUri());
                    Navigation.findNavController(view1).navigate(action);
            });

            binding.categoryTextLayoutUserIncidents.setText(model.selectedIncident.getIncidentCategory());
            binding.descriptionText.setText(model.selectedIncident.getIncidentDescription());
            if(model.selectedIncident.isBroadcast()){
                  binding.broadcastingOrNot.setText("Broadcasting");
            }else {
                  binding.broadcastingOrNot.setText("Not Broadcasting");
            }




      }
}