package com.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.hoest.pojos.incident;
import com.hoest.rapidrescue_irs.NavGraphDirections;
import com.hoest.rapidrescue_irs.R;
import com.hoest.viewModels.incidentsViewModel;

import java.util.ArrayList;

public class userIncidentsAdapter extends RecyclerView.Adapter<userIncidentsAdapter.ViewHolder>{
      NavController navController;
      ArrayList<incident> userIncidentList;
      Context context;
      incidentsViewModel incidentsViewModel;



      public  userIncidentsAdapter(NavController navController, ArrayList<incident> userIncidentList, Context context, incidentsViewModel incidentsViewModel){
            this.navController = navController;
            this.userIncidentList = userIncidentList;
            this.context = context;
            this.incidentsViewModel = incidentsViewModel;
      }



      @NonNull
      @Override
      public userIncidentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_incidents_horizontal, parent, false);
            return new ViewHolder(view);
      }

      @Override
      public void onBindViewHolder(@NonNull userIncidentsAdapter.ViewHolder holder, int position) {
            incident mIncident = userIncidentList.get(position);
            holder.incidentCategory.setText(mIncident.getIncidentCategory());

            Glide.with(context)
                    .load(mIncident.getIncidentPhotoUri())
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
                    .into(holder.incidentImage);

            if(mIncident.isBroadcast()){
                  holder.broadcastingTextView.setText("Broadcasting");
            }else {
                  holder.broadcastingTextView.setText("Not Broadcasting");
            }

            holder.locationButton.setOnClickListener(v -> {
                  Intent intent;
                  intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=" + mIncident.getLatLang()));
                  context.startActivity(intent);
            });

            holder.viewButton.setOnClickListener(v ->{
                  incidentsViewModel.selectedIncident= mIncident;
                  navController.navigate(R.id.action_global_viewIncident) ;
            });

            holder.incidentImage.setOnClickListener(view -> {
                  com.hoest.rapidrescue_irs.NavGraphDirections.ActionGlobalViewImage action= com.hoest.rapidrescue_irs.NavGraphDirections.actionGlobalViewImage(mIncident.getIncidentPhotoUri());
                  action.setPhotoUrl(mIncident.getIncidentPhotoUri());
                  navController.navigate(action);
            });



      }

      @Override
      public int getItemCount() {
            Log.i("userIncidentList", String.valueOf(userIncidentList.size()));
            return userIncidentList.size();
      }

      public static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView incidentImage;
            TextView incidentCategory;
            TextView broadcastingTextView;
            MaterialButton locationButton,viewButton;

            public ViewHolder(@NonNull View itemView) {
                  super(itemView);
                    incidentImage = itemView.findViewById(R.id.imageViewLayoutUserIncidents);
                    incidentCategory = itemView.findViewById(R.id.categoryTextLayoutUserIncidents);
                    broadcastingTextView = itemView.findViewById(R.id.broadcastingOrNot);
                    locationButton = itemView.findViewById(R.id.viewLocationUserIncidents);
                    viewButton = itemView.findViewById(R.id.viewDetailsButtonLayoutUserIncident);
            }
      }

}
