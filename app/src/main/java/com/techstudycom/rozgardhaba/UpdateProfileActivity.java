package com.techstudycom.rozgardhaba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techstudycom.rozgardhaba.R.id;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText editTextUpdateName, editTextUpdateDoB, editTextUpdateMobile, editTextUpdateAadhaar, editTextUpdateAddress;
    private RadioGroup radioGroupUpdateGender;
    private RadioButton radioButtonUpdateGenderSelected;
    private  String textFullName, textEmail,textDOB, textMobileNo, textAadhaarNo, textAddress, textGender;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        getSupportActionBar().setTitle("Update Profile Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressbar);

        editTextUpdateName = findViewById(id.editText_update_profile_name);
        editTextUpdateDoB = findViewById(id.editText_update_profile_dob);
        editTextUpdateMobile = findViewById(id.editText_update_profile_mobile);
        editTextUpdateAadhaar = findViewById(id.editText_update_profile_aadhaar);
        editTextUpdateAddress = findViewById(id.editText_update_profile_address);

        radioGroupUpdateGender = findViewById(id.radio_group_update_gender);

        authProfile = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = authProfile.getCurrentUser();


        assert firebaseUser != null;
        showProfile(firebaseUser);

        Button buttonUploadProfilePic = findViewById(id.button_update_profile_pic);
        buttonUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this, UploadProfilePicActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button buttonUploadEmail = findViewById(id.button_profile_update_email);
        buttonUploadEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this, UpdateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });

        editTextUpdateDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String textSADoB[] = textDOB.split("/");

                int day = Integer.parseInt(textSADoB[0]);
                int month = Integer.parseInt(textSADoB[1])-1;
                int year = Integer.parseInt(textSADoB[2]);

                DatePickerDialog picker;


                picker = new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view , int year, int month, int dayOfMonth) {
                        editTextUpdateDoB.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                    }
                } ,year,month, day);
                picker.show();
            }
        });

        Button buttonUpdateProfile = findViewById(id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);

            }
        });



    }

    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID = radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected = findViewById(selectedGenderID);


        String mobileRegex = "[6-9][0-9]{9}";
        Matcher mobileMatcher ;
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        mobileMatcher = mobilePattern.matcher(textMobileNo);


        if (TextUtils.isEmpty(textFullName)){
            Toast.makeText(UpdateProfileActivity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            editTextUpdateName.setError("Full name is required");
            editTextUpdateName.requestFocus();
        }else if (TextUtils.isEmpty(textDOB)){
            Toast.makeText(UpdateProfileActivity.this, "Please enter your date of birth", Toast.LENGTH_SHORT).show();
            editTextUpdateDoB.setError("Date of birth is required");
            editTextUpdateDoB.requestFocus();
        } else if (TextUtils.isEmpty(textAddress)) {
            Toast.makeText(UpdateProfileActivity.this, "Please Write Your Full Address", Toast.LENGTH_SHORT).show();
            editTextUpdateAddress.setError("Address is required");
            editTextUpdateAddress.requestFocus();

        } else if (TextUtils.isEmpty(textAadhaarNo)){
            Toast.makeText(UpdateProfileActivity.this, "Please enter your aadhaar number", Toast.LENGTH_SHORT).show();
            editTextUpdateAadhaar.setError("Aadhaar number  is required");
            editTextUpdateAadhaar.requestFocus();
        }else if (textAadhaarNo.length()!=12) {
            Toast.makeText(UpdateProfileActivity.this, "Please re-enter your Aadhaar Number", Toast.LENGTH_SHORT).show();
            editTextUpdateAadhaar.setError("Aadhaar Number should be 12 digits");
            editTextUpdateAadhaar.requestFocus();
        } else if (TextUtils.isEmpty(textMobileNo)){
            Toast.makeText(UpdateProfileActivity.this, "Please enter your Mobile Number", Toast.LENGTH_SHORT).show();
            editTextUpdateMobile.setError("Mobile Number is required");
            editTextUpdateMobile.requestFocus();
        }else if (textMobileNo.length()!=10){
            Toast.makeText(UpdateProfileActivity.this, "Please re-enter your mobile number", Toast.LENGTH_SHORT).show();
            editTextUpdateMobile.setError("Mobile Number should be 10 digits");
            editTextUpdateMobile.requestFocus();
        }else if (! mobileMatcher.find()) {
            Toast.makeText(UpdateProfileActivity.this, "Please re-enter your mobile number", Toast.LENGTH_SHORT).show();
            editTextUpdateMobile.setError("Mobile Number is not validate");
            editTextUpdateMobile.requestFocus();
        }
        else if (TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())){
            Toast.makeText(UpdateProfileActivity.this, "Please enter your gender", Toast.LENGTH_SHORT).show();
            radioButtonUpdateGenderSelected.setError("Gender is required");
            radioButtonUpdateGenderSelected.requestFocus();
        }

        else {
            textGender = radioButtonUpdateGenderSelected.getText().toString();
            textFullName = editTextUpdateName.getText().toString();
            textDOB = editTextUpdateDoB.getText().toString();
            textMobileNo = editTextUpdateMobile.getText().toString();
            textAadhaarNo = editTextUpdateAadhaar.getText().toString();
            textAddress = editTextUpdateAddress.getText().toString();


            //ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDOB, textGender, textMobileNo, textAadhaarNo, textAddress);
            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails( textFullName,textEmail,textDOB, textGender, textMobileNo, textAadhaarNo, textAddress);

            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
            String userId  = firebaseUser.getUid();

            progressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userId).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                        firebaseUser.updateProfile(profileUpdate);

                        Toast.makeText(UpdateProfileActivity.this, "Updated Successfully !", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(UpdateProfileActivity.this, UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e){
                            Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });

        }
    }

    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();

        DatabaseReference referenceProfile  = FirebaseDatabase.getInstance().getReference("Registered Users");

        progressBar.setVisibility(View.VISIBLE);


        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null){
                    textFullName = firebaseUser.getDisplayName();
                    textDOB = readUserDetails.dOB;
                    textGender = readUserDetails.gender;
                    textMobileNo = readUserDetails.mobile;
                    textAadhaarNo = readUserDetails.aadhaarNumber;
                    textAddress = readUserDetails.address;


                    editTextUpdateName.setText(textFullName);
                    editTextUpdateDoB.setText(textDOB);
                    editTextUpdateMobile.setText(textMobileNo);
                    editTextUpdateAadhaar.setText(textAadhaarNo);
                    editTextUpdateAddress.setText(textAddress);

                    if (textGender.equals("Male")){
                        radioButtonUpdateGenderSelected= findViewById(id.radio_update_male);
                    } else if (textGender.equals("Female")){
                        radioButtonUpdateGenderSelected.findViewById(id.radio_update_female);
                    } else  {
                        radioButtonUpdateGenderSelected.findViewById(id.radio_update_other);
                    }
                    radioButtonUpdateGenderSelected.setChecked(true);
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            }
        });

       // referenceProfile.child(userIDofRegistered).addValueEventListener(new ValueEventListener() {
           /*
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null){
                    textFullName = firebaseUser.getDisplayName();
                    textDOB = readUserDetails.dOB;
                    textGender = readUserDetails.gender;
                    textMobileNo = readUserDetails.mobile;
                    textAadhaarNo = readUserDetails.aadhaarNumber;
                    textAddress = readUserDetails.address;


                    editTextUpdateName.setText(textFullName);
                    editTextUpdateDoB.setText(textDOB);
                    editTextUpdateMobile.setText(textMobileNo);
                    editTextUpdateAadhaar.setText(textAadhaarNo);
                    editTextUpdateAddress.setText(textAddress);

                    if (textGender.equals("Male")){
                        radioButtonUpdateGenderSelected= findViewById(id.radio_update_male);
                    } else if (textGender.equals("Female")){
                        radioButtonUpdateGenderSelected.findViewById(id.radio_update_female);
                    } else  {
                        radioButtonUpdateGenderSelected.findViewById(id.radio_update_other);
                    }
                    radioButtonUpdateGenderSelected.setChecked(true);
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            }
        });

            */

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(UpdateProfileActivity.this);
        }
        else if (id == R.id.menu_refresh){
            startActivity(getIntent());
            finish();

            overridePendingTransition(0, 0);

        } else if (id == R.id.menu_update_profile){
            Intent intent = new Intent(UpdateProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_update_email){
            Intent intent = new Intent(UpdateProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_setting){
            Toast.makeText(UpdateProfileActivity.this, "menu_setting", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UpdateProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UpdateProfileActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(UpdateProfileActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UpdateProfileActivity.this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();

        }else {
            Toast.makeText(UpdateProfileActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

}