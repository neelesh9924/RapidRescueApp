package com.hoest.pojos;

import com.google.firebase.Timestamp;

public class incident {

      private String incidentCategory;
      private String incidentDescription;
      private String latLang;
      private String incidentPhotoUri;
      private boolean broadcast;
      private boolean declaration;
      private String incidentId;
      private String uid;
      private Timestamp submittedDate;
      private boolean isActive;

      public incident() {
      }

      public String getIncidentCategory() {
            return incidentCategory;
      }

      public void setIncidentCategory(String incidentCategory) {
            this.incidentCategory = incidentCategory;
      }

      public String getIncidentDescription() {
            return incidentDescription;
      }

      public void setIncidentDescription(String incidentDescription) {
            this.incidentDescription = incidentDescription;
      }

      public String getLatLang() {
            return latLang;
      }

      public void setLatLang(String latLang) {
            this.latLang = latLang;
      }

      public String getIncidentPhotoUri() {
            return incidentPhotoUri;
      }

      public void setIncidentPhotoUri(String incidentPhotoUri) {
            this.incidentPhotoUri = incidentPhotoUri;
      }

      public boolean isBroadcast() {
            return broadcast;
      }

      public void setBroadcast(boolean broadcast) {
            this.broadcast = broadcast;
      }

      public boolean isDeclaration() {
            return declaration;
      }

      public void setDeclaration(boolean declaration) {
            this.declaration = declaration;
      }

      public String getIncidentId() {
            return incidentId;
      }

      public void setIncidentId(String incidentId) {
            this.incidentId = incidentId;
      }

      public String getUid() {
            return uid;
      }

      public void setUid(String uid) {
            this.uid = uid;
      }

      public Timestamp getSubmittedDate() {
            return submittedDate;
      }

      public void setSubmittedDate(Timestamp submittedDate) {
            this.submittedDate = submittedDate;
      }

      public boolean isActive() {
            return isActive;
      }

      public void setIsActive(boolean isActive) {
            isActive = isActive;
      }
}
