package com.example.choicemart1;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ActivityResultLauncher<Intent> imagePickerLauncher;


    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        CircleImageView profileImageView = view.findViewById(R.id.settings_profile_image);
        TextView changeProfileImageBtn = view.findViewById(R.id.profile_image_change_btn);
        TextView phoneNumberTextView = view.findViewById(R.id.settings_phone_number);
        EditText fullNameEditText = view.findViewById(R.id.settings_full_name);
        EditText addressEditText = view.findViewById(R.id.settings_address);
        EditText countryEditText = view.findViewById(R.id.settings_country);
        TextView updateButton = view.findViewById(R.id.update_account_settings_btn);
        TextView closeButton = view.findViewById(R.id.close_settings_btn);

        String phone = UserData.getInstance().getUserId();
        String phoneNumberText = "Phone Number: " + phone;
        phoneNumberTextView.setText(phoneNumberText);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(UserData.getInstance().getUserId());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("name").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String country = snapshot.child("country").getValue(String.class);
                    String userImageUri = snapshot.child("imageUri").getValue(String.class);

                    fullNameEditText.setText(fullName);
                    addressEditText.setText(address);
                    countryEditText.setText(country);

                    if (userImageUri != null && !userImageUri.isEmpty()) {
                        Glide.with(requireActivity())
                                .load(userImageUri)
                                .apply(RequestOptions.circleCropTransform())
                                .into(profileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
            }
        });



        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();

                        // Display the selected image in the profileImageView
                        Glide.with(requireActivity())
                                .load(imageUri)
                                .apply(RequestOptions.circleCropTransform())
                                .into(profileImageView);
                    }
                });
        changeProfileImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = fullNameEditText.getText().toString();
                String address = addressEditText.getText().toString();
                String country = countryEditText.getText().toString();

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                        .child("Users")
                        .child(UserData.getInstance().getUserId());

                if (fullName.isEmpty()) {
                    userRef.child("name").removeValue();
                } else {
                    userRef.child("name").setValue(fullName);
                    UserData.getInstance().setUserName(fullName);
                }

                if (address.isEmpty()) {
                    userRef.child("address").removeValue();
                } else {
                    userRef.child("address").setValue(address);
                }

                if (country.isEmpty()) {
                    userRef.child("country").removeValue();
                } else {
                    userRef.child("country").setValue(country);
                }

                if (imageUri != null) {
                    StorageReference imageRef = storageRef.child("profile_images/" + UserData.getInstance().getUserId() + ".jpg");
                    UploadTask uploadTask = imageRef.putFile(imageUri);

                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUrlTask.addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            userRef.child("imageUri").setValue(imageUrl);
                            UserData.getInstance().setUserImageUri(imageUrl);
                            Glide.with(requireActivity())
                                    .load(imageUrl)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(profileImageView);

                            Toast.makeText(getActivity(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        });
                    });
                }

                Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = NavHostFragment.findNavController(SettingsFragment.this);
                navController.popBackStack();
            }
        });

        return view;
    }






}