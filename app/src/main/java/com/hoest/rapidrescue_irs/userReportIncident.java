package com.hoest.rapidrescue_irs;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hoest.rapidrescue_irs.databinding.FragmentUserReportIncidentBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class userReportIncident extends Fragment {
      FragmentUserReportIncidentBinding binding;
      NavController mNavController;

      ArrayList<String> incidentCategories;


      private ActivityResultLauncher<String> requestPermissionLauncher;

      private FusedLocationProviderClient fusedLocationProviderClient;

      double latitude = 0f, longitude = 0f;

      private boolean locationFetched;

      private String incidentCategory = "", incidentDescription = "";


      private ActivityResultLauncher<String> filePickerLauncher;

      private static final String FILE_TYPE = "image/*";
      private static final int REQUEST_CODE_FILE = 1;

      private Uri selectedFileUri;

      private boolean broadcast = false, declaration = false;
      private String photoUrl="";
      private boolean fileUploaded=false;


      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            binding = FragmentUserReportIncidentBinding.inflate(inflater, container, false);
            /*binding.mapView.onCreate(savedInstanceState);
            binding.mapView.getMapAsync(this);*/
            return binding.getRoot();
      }

      @Override
      public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mNavController = Navigation.findNavController(view);
            locationFetched = false;

            setArrayLists();
            addTextWatchers();
            addButtonListeners();
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

            filePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                    result -> {
                          if (result != null) {
                                selectedFileUri = result;
                                binding.selectedPhotoName.setText(getFileName(selectedFileUri));
                                binding.selectPhoto.setText("Change Photo");
                                uploadPhoto(selectedFileUri);
                          }
                    });


            requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                          if (isGranted) {
                                checkLocationService();
                          } else {
                                Toast.makeText(requireContext(), "Location permission denied.", Toast.LENGTH_SHORT).show();
                          }
                    });

            fetchUserLocation();

      }


      private void fetchUserLocation() {
            checkLocationPermissions();
      }

      private void checkLocationPermissions() {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                  requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                  checkLocationService();
            }
      }


      private void checkLocationService() {
            LocationManager locationManager = (LocationManager) requireContext().getSystemService(LOCATION_SERVICE);
            if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                  startActivity(intent);
            } else {
                  fetchLocation();
            }
      }

      @SuppressLint("MissingPermission")
      private void fetchLocation() {
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(5000); // Update interval in milliseconds

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                  @Override
                  public void onLocationResult(@NonNull LocationResult locationResult) {
                        Location location = locationResult.getLastLocation();
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        locationFetched = true;
                  }
            }, null);
      }


      private void addButtonListeners() {
            binding.fetchLocationButton.setOnClickListener(view -> {
                  if (locationFetched) {
                        Toast.makeText(requireContext(), "Location Already Fetched.", Toast.LENGTH_SHORT).show();
                  } else {
                        fetchUserLocation();
                  }
            });

            binding.selectPhoto.setOnClickListener(view -> {
                  selectFile();
            });

            binding.broadcastCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                  @Override
                  public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        broadcast = b;
                  }
            });

            binding.declarationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                  @Override
                  public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        declaration = b;
                  }
            });

            binding.submitIncident.setOnClickListener(view -> {
                  if(fileUploaded){
                        if (declaration) {
                              submitIncident(incidentCategory, incidentDescription, latitude + "," + longitude, photoUrl, broadcast, declaration);
                        }else{
                              Toast.makeText(requireContext(), "Please agree to the declaration.", Toast.LENGTH_SHORT).show();
                        }
                  }else{
                        new AlertDialog.Builder(requireContext())
                                .setTitle("File Not Uploaded")
                                .setMessage("Please wait for the file to upload.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                      }
                                })
                                .show();
                  }

            });
      }

      private void submitIncident(String incidentCategory, String incidentDescription, String latLang, String incidentPhotoUri, boolean broadcast, boolean declaration){
            Map<String, Object> incident = new HashMap<>();
            DocumentReference mDocRef = FirebaseFirestore.getInstance().collection("incidents").document();
            incident.put("incidentCategory", incidentCategory);
            incident.put("incidentDescription", incidentDescription);
            incident.put("latLang", latLang);
            incident.put("incidentPhotoUri", incidentPhotoUri);
            incident.put("broadcast", broadcast);
            incident.put("declaration", declaration);
            incident.put("incidentId", mDocRef.getId());
            incident.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            incident.put("submittedDate", FieldValue.serverTimestamp());
            incident.put("isActive", true);

            mDocRef.set(incident).addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void unused) {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Incident Reported")
                                .setMessage("Incident Reported Successfully, You can find your incident in the incident list from homepage.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            mNavController.popBackStack();
                                      }
                                }).show();
                  }
            }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Incident Report Failed", Toast.LENGTH_SHORT).show();
                  }
            });
      }


      private void selectFile() {
            filePickerLauncher.launch(FILE_TYPE);
      }

      private void uploadPhoto(Uri photoUri) {
            // Create a storage reference
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            // Create a reference to the file in storage
            StorageReference photoRef = storageRef.child("photos/" + System.currentTimeMillis() + ".jpg");

            // Upload the file to Firebase Storage
            UploadTask uploadTask = photoRef.putFile(photoUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                  // File upload success
                  // Get the file URL
                  photoRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        photoUrl= uri.toString();
                        fileUploaded = true;
                        // Use the file URL as needed (e.g., save it to a database, display it in an ImageView)
                        // ...
                  }).addOnFailureListener(exception -> {
                        // File URL retrieval failed
                        exception.printStackTrace();
                  });
            }).addOnFailureListener(exception -> {
                  // File upload failed
                  exception.printStackTrace();
            });
      }

      private String getFileName(Uri uri) {
            String fileName = null;
            if (uri.getScheme().equals("content")) {
                  ContentResolver contentResolver = requireActivity().getContentResolver();
                  Cursor cursor = contentResolver.query(uri, null, null, null, null);
                  if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (nameIndex != -1) {
                              fileName = cursor.getString(nameIndex);
                        }
                        cursor.close();
                  }
            }
            if (fileName == null) {
                  fileName = uri.getLastPathSegment();
            }
            return fileName;
      }


      private void addTextWatchers() {
            binding.incidentCategoriesTextView.addTextChangedListener(new TextWatcher() {
                  @Override
                  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                  }

                  @Override
                  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        incidentCategory = charSequence.toString();
                  }

                  @Override
                  public void afterTextChanged(Editable editable) {

                  }
            });

            binding.descriptionTextEditText.addTextChangedListener(new TextWatcher() {
                  @Override
                  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                  }

                  @Override
                  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        incidentDescription = charSequence.toString();
                  }

                  @Override
                  public void afterTextChanged(Editable editable) {

                  }
            });
      }

      private void setArrayLists() {
            incidentCategories = new ArrayList<>();

            incidentCategories.add("Natural Disasters");
            incidentCategories.add("Earthquakes");
            incidentCategories.add("Floods");
            incidentCategories.add("Hurricanes/Cyclones");
            incidentCategories.add("Tornadoes");
            incidentCategories.add("Tsunamis");
            incidentCategories.add("Wildfires");
            incidentCategories.add("Landslides");

            incidentCategories.add("Accidents");
            incidentCategories.add("Traffic accidents");
            incidentCategories.add("Industrial accidents");
            incidentCategories.add("Aviation accidents");
            incidentCategories.add("Maritime accidents");
            incidentCategories.add("Train derailments");
            incidentCategories.add("Building collapses");

            incidentCategories.add("Medical Emergencies");
            incidentCategories.add("Heart attacks");
            incidentCategories.add("Stroke");
            incidentCategories.add("Respiratory emergencies");
            incidentCategories.add("Severe injuries");
            incidentCategories.add("Poisonings");
            incidentCategories.add("Allergic reactions");

            incidentCategories.add("Criminal Incidents");
            incidentCategories.add("Robbery");
            incidentCategories.add("Burglary");
            incidentCategories.add("Assault");
            incidentCategories.add("Kidnapping");
            incidentCategories.add("Homicide");
            incidentCategories.add("Domestic violence");

            incidentCategories.add("Fire and Explosions");
            incidentCategories.add("Building fires");
            incidentCategories.add("Wildfires");
            incidentCategories.add("Industrial fires");
            incidentCategories.add("Explosions");
            incidentCategories.add("Gas leaks");

            incidentCategories.add("Environmental Incidents");
            incidentCategories.add("Oil spills");
            incidentCategories.add("Chemical spills");
            incidentCategories.add("Hazardous material incidents");
            incidentCategories.add("Pollution incidents");
            incidentCategories.add("Ecological disasters");

            incidentCategories.add("Public Health Emergencies");
            incidentCategories.add("Disease outbreaks (e.g., pandemics)");
            incidentCategories.add("Epidemics");
            incidentCategories.add("Biological incidents");
            incidentCategories.add("Foodborne illnesses");
            incidentCategories.add("Public health crises");

            incidentCategories.add("Cybersecurity Incidents");
            incidentCategories.add("Data breaches");
            incidentCategories.add("Hacking incidents");
            incidentCategories.add("Malware attacks");
            incidentCategories.add("Ransomware attacks");
            incidentCategories.add("Network disruptions");

            incidentCategories.add("Civil Unrest and Terrorism");
            incidentCategories.add("Riots");
            incidentCategories.add("Protests");
            incidentCategories.add("Bombings");
            incidentCategories.add("Hostage situations");
            incidentCategories.add("Terrorist attacks");

            incidentCategories.add("Search and Rescue Operations");
            incidentCategories.add("Missing persons");
            incidentCategories.add("Stranded or lost individuals");
            incidentCategories.add("Mountain or wilderness rescues");
            incidentCategories.add("Water rescues");

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_dropdown_item_1line, incidentCategories);
            binding.incidentCategoriesTextView.setAdapter(adapter);
      }
}