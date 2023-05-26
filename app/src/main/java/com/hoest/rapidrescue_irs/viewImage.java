package com.hoest.rapidrescue_irs;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hoest.rapidrescue_irs.databinding.FragmentViewImageBinding;


public class viewImage extends Fragment {
      FragmentViewImageBinding binding;

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            binding = FragmentViewImageBinding.inflate(inflater, container, false);
            return binding.getRoot();
      }

      @Override
      public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            String imageUrl = viewImageArgs.fromBundle(getArguments()).getPhotoUrl();

            Glide.with(requireContext())
                    .load(imageUrl)
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
                    .into(binding.imageView);

      }
}