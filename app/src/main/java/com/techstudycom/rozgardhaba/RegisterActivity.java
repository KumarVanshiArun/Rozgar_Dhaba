package com.techstudycom.rozgardhaba;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private String selectedState, selectedDistricts, selectedEducation, selectedSkills;

    private Spinner stateSpinner, districtSpinner, educationSpinner, skillsSpinner;
    private ArrayAdapter<CharSequence> stateAdapter, districtAdapter, educationAdapter, skillsAdapter;

    private EditText editTextRegisterFullName, editTextRegisterEmail,editTextRegisterDOB, editTextRegisterMobile,editTextRegisterPwd, editTextRegisterConfirmPwd,
    editTextRegisterFatherName,editTextRegisterAddress,editTextRegisterPINCode, editTextAadhaarNumber, editTextRegisterExperience;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender, radioGroupRegisterDisability, radioGroupRegisterReadyWork;
    private RadioButton radioButtonRegisterGenderSelected, radioButtonRegisterDisabilitySelected, radioButtonReadyWorkSelected;
    private Button buttonRegister;
    private static final String TAG = "RegisterActivity";
    private  DatePickerDialog picker;
    private  FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Register");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toast.makeText(RegisterActivity.this, "You can register now", Toast.LENGTH_SHORT).show();

        progressBar = findViewById(R.id.progressbar);

        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterFatherName = findViewById(R.id.editText_register_father_name);
        editTextRegisterDOB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterAddress = findViewById(R.id.editText_register_address);
        editTextRegisterPINCode = findViewById(R.id.editText_register_pin);
        editTextRegisterEmail= findViewById(R.id.editText_register_email);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);
        editTextAadhaarNumber = findViewById(R.id.editText_register_aadhaar_number);
        editTextRegisterExperience = findViewById(R.id.editText_register_experience);


        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();
        radioGroupRegisterDisability = findViewById(R.id.radio_group_register_disability);
        radioGroupRegisterDisability.clearCheck();
        radioGroupRegisterReadyWork = findViewById(R.id.radio_group_register_ready_work);
        radioGroupRegisterReadyWork.clearCheck();


        editTextRegisterDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);


                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        editTextRegisterDOB.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                    }
                } ,year,month, day);
                picker.show();
            }
        });


        buttonRegister = findViewById(R.id.register_button);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

                int selectedDisabilityId = radioGroupRegisterDisability.getCheckedRadioButtonId();
                radioButtonRegisterDisabilitySelected = findViewById(selectedDisabilityId);

                int selectedReadyWorkId = radioGroupRegisterReadyWork.getCheckedRadioButtonId();
                radioButtonReadyWorkSelected = findViewById(selectedReadyWorkId);

                String textFullName = editTextRegisterFullName.getText().toString();
                String textFatherName = editTextRegisterFatherName.getText().toString();
                String textDOB = editTextRegisterDOB.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textMobileNo = editTextRegisterMobile.getText().toString();
                String textAddress = editTextRegisterAddress.getText().toString();
                String textPINCode = editTextRegisterPINCode.getText().toString();
                String textPwd = editTextRegisterPwd.getText().toString();
                String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString();
                String textAadhaarNo = editTextAadhaarNumber.getText().toString();
                String textGender;


                String mobileRegex = "[6-9][0-9]{9}";
                Matcher mobileMatcher ;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobileNo);


                if (TextUtils.isEmpty(textFullName)){
                    Toast.makeText(RegisterActivity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                    editTextRegisterFullName.setError("Full name is required");
                    editTextRegisterFullName.requestFocus();
                }else if (TextUtils.isEmpty(textEmail)){
                    Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(RegisterActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.setError("Valid email is required");
                    editTextRegisterEmail.requestFocus();
                }else if (TextUtils.isEmpty(textDOB)){
                    Toast.makeText(RegisterActivity.this, "Please enter your date of birth", Toast.LENGTH_SHORT).show();
                    editTextRegisterDOB.setError("Date of birth is required");
                    editTextRegisterDOB.requestFocus();
                } else if (TextUtils.isEmpty(textAddress)) {
                    Toast.makeText(RegisterActivity.this, "Please Write Your Full Address", Toast.LENGTH_SHORT).show();
                    editTextRegisterAddress.setError("Address is required");
                    editTextRegisterAddress.requestFocus();

                } else if (TextUtils.isEmpty(textAadhaarNo)){
                    Toast.makeText(RegisterActivity.this, "Please enter your aadhaar number", Toast.LENGTH_SHORT).show();
                    editTextAadhaarNumber.setError("Aadhaar number  is required");
                    editTextAadhaarNumber.requestFocus();
                }else if (textAadhaarNo.length()!=12) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your Aadhaar Number", Toast.LENGTH_SHORT).show();
                    editTextAadhaarNumber.setError("Aadhaar Number should be 12 digits");
                    editTextAadhaarNumber.requestFocus();
                } else if (TextUtils.isEmpty(textMobileNo)){
                    Toast.makeText(RegisterActivity.this, "Please enter your Mobile Number", Toast.LENGTH_SHORT).show();
                    editTextRegisterMobile.setError("Mobile Number is required");
                    editTextRegisterMobile.requestFocus();
                }else if (textMobileNo.length()!=10){
                    Toast.makeText(RegisterActivity.this, "Please re-enter your mobile number", Toast.LENGTH_SHORT).show();
                    editTextRegisterMobile.setError("Mobile Number should be 10 digits");
                    editTextRegisterMobile.requestFocus();
                }else if (! mobileMatcher.find()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your mobile number", Toast.LENGTH_SHORT).show();
                    editTextRegisterMobile.setError("Mobile Number is not validate");
                    editTextRegisterMobile.requestFocus();
                }
                else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1){
                    Toast.makeText(RegisterActivity.this, "Please enter your gender", Toast.LENGTH_SHORT).show();
                    radioButtonRegisterGenderSelected.setError("Gender is required");
                    radioButtonRegisterGenderSelected.requestFocus();
                }
                else if (TextUtils.isEmpty(textPwd)){
                    Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    editTextRegisterPwd.setError("Password is required");
                    editTextRegisterPwd.requestFocus();
                }else if (textPwd.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password should be at least 6 digits", Toast.LENGTH_SHORT).show();
                    editTextRegisterPwd.setError("Password is too weak");
                    editTextRegisterPwd.requestFocus();
                }else if (TextUtils.isEmpty(textConfirmPwd)){
                    Toast.makeText(RegisterActivity.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                    editTextRegisterConfirmPwd.setError("Password confirmation is required");
                    editTextRegisterConfirmPwd.requestFocus();
                }else if (! textPwd.equals(textConfirmPwd)) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter your password", Toast.LENGTH_SHORT).show();
                    editTextRegisterConfirmPwd.setError("Please confirmation is required");
                    editTextRegisterConfirmPwd.requestFocus();

                    editTextRegisterPwd.clearComposingText();
                    editTextRegisterConfirmPwd.clearComposingText();
                }else {
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDOB, textGender, textMobileNo, textPwd, textAadhaarNo, textAddress);

                }


            }

            private void registerUser(String textFullName, String textEmail, String textDOB, String textGender, String textMobileNo, String textPwd, String textAadhaarNo, String textAddress) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                           // Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                            assert firebaseUser != null;
                            firebaseUser.updateProfile(profileChangeRequest);

                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textEmail,textFullName,textDOB, textGender, textMobileNo, textAadhaarNo,  textAddress);

                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        firebaseUser.sendEmailVerification();
                                        auth.getCurrentUser();

                                        Toast.makeText(RegisterActivity.this, "User registered successfully. Please verify your email to login.", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                         | Intent.FLAG_ACTIVITY_NEW_TASK);
                                          startActivity(intent);
                                         finish();


                                    }else {
                                        Toast.makeText(RegisterActivity.this, "User Registration Failed. Please try again after sometime", Toast.LENGTH_SHORT).show();
                                    }
                                    progressBar.setVisibility(View.GONE);

                                }
                            });

                        } else {
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e) {
                                editTextRegisterPwd.setError("Your password is too weak. Kindly use a mix of alphabets, numbers and special characters");
                                editTextRegisterPwd.requestFocus();
                            }catch (FirebaseAuthInvalidCredentialsException e) {
                                editTextRegisterPwd.setError("Your email is invalid or already in use, Kindly Re-enter your email");
                                editTextRegisterPwd.requestFocus();
                            }catch (FirebaseAuthUserCollisionException e ){
                                editTextRegisterPwd.setError("Already registered with this email, Please use another email to register ");
                                editTextRegisterPwd.requestFocus();
                            }catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(RegisterActivity.this,  e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }
                });
            }
        });







        stateSpinner = findViewById(R.id.spinner_indian_states);
        districtSpinner = findViewById(R.id.spinner_indian_districts);
        educationSpinner = findViewById(R.id.spinner_education);
        skillsSpinner = findViewById(R.id.spinner_skills);

        skillsAdapter = ArrayAdapter.createFromResource(this, R.array.array_skills, R.layout.spinner_layout);
        skillsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skillsSpinner.setAdapter(skillsAdapter);
        skillsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSkills = skillsSpinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        educationAdapter = ArrayAdapter.createFromResource(this, R.array.array_education, R.layout.spinner_layout);
        educationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        educationSpinner.setAdapter(educationAdapter);
        educationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedEducation = educationSpinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        stateAdapter = ArrayAdapter.createFromResource(this, R.array.array_indian_states, R.layout.spinner_layout);

        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        stateSpinner.setAdapter(stateAdapter);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                districtSpinner = findViewById(R.id.spinner_indian_districts);

                selectedState = stateSpinner.getSelectedItem().toString();

                int parentId = parent.getId();
                if (parentId == R.id.spinner_indian_states){
                    switch (selectedState){
                        case "Select Your State": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_defaults_districts, R.layout.spinner_layout);
                        break;
                        case "Andhra Pradesh" : districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_andhra_pradesh_districts, R.layout.spinner_layout);
                        break;
                        case "Arunachal Pradesh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_arunachal_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "Assam": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_assam_districts, R.layout.spinner_layout);
                            break;
                        case "Bihar": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_bihar_districts, R.layout.spinner_layout);
                            break;
                        case "Chhattisgarh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_chhattisgarh_districts, R.layout.spinner_layout);
                            break;
                        case "Goa": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_goa_districts, R.layout.spinner_layout);
                            break;
                        case "Gujarat": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_gujarat_districts, R.layout.spinner_layout);
                            break;
                        case "Haryana": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_haryana_districts, R.layout.spinner_layout);
                            break;
                        case "Himachal Pradesh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_himachal_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "Jharkhand": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_jharkhand_districts, R.layout.spinner_layout);
                            break;
                        case "Karnataka": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_karnataka_districts, R.layout.spinner_layout);
                            break;
                        case "Kerala": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_kerala_districts, R.layout.spinner_layout);
                            break;
                        case "Madhya Pradesh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_madhya_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "Maharashtra": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_maharashtra_districts, R.layout.spinner_layout);
                            break;
                        case "Manipur": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_manipur_districts, R.layout.spinner_layout);
                            break;
                        case "Meghalaya": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_meghalaya_districts, R.layout.spinner_layout);
                            break;
                        case "Mizoram": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_mizoram_districts, R.layout.spinner_layout);
                            break;
                        case "Nagaland": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_nagaland_districts, R.layout.spinner_layout);
                            break;
                        case "Odisha": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_odisha_districts, R.layout.spinner_layout);
                            break;
                        case "Punjab": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_punjab_districts, R.layout.spinner_layout);
                            break;
                        case "Rajasthan": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_rajasthan_districts, R.layout.spinner_layout);
                            break;
                        case "Sikkim": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_sikkim_districts, R.layout.spinner_layout);
                            break;
                        case "Tamil Nadu": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_tamil_nadu_districts, R.layout.spinner_layout);
                            break;
                        case "Telangana": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_telangana_districts, R.layout.spinner_layout);
                            break;
                        case "Tripura": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_tripura_districts, R.layout.spinner_layout);
                            break;
                        case "Uttar Pradesh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_uttar_pradesh_districts, R.layout.spinner_layout);
                            break;
                        case "Uttarakhand": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_uttarakhand_districts, R.layout.spinner_layout);
                            break;
                        case "West Bengal": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_west_bengal_districts, R.layout.spinner_layout);
                            break;
                        case "Andaman and Nicobar Islands": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_andaman_nicobar_districts, R.layout.spinner_layout);
                            break;
                        case "Chandigarh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_chandigarh_districts, R.layout.spinner_layout);
                            break;
                        case "Dadra and Nagar Haveli": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_dadra_nagar_haveli_districts, R.layout.spinner_layout);
                            break;
                        case "Daman and Diu": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_daman_diu_districts, R.layout.spinner_layout);
                            break;
                        case "Delhi": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_delhi_districts, R.layout.spinner_layout);
                            break;
                        case "Jammu and Kashmir": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_jammu_kashmir_districts, R.layout.spinner_layout);
                            break;
                        case "Lakshadweep": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_lakshadweep_districts, R.layout.spinner_layout);
                            break;
                        case "Ladakh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_ladakh_districts, R.layout.spinner_layout);
                            break;
                        case "Puducherry": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                                R.array.array_puducherry_districts, R.layout.spinner_layout);
                            break;

                        default: break;
                    }
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    districtSpinner.setAdapter(districtAdapter);

                    districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            selectedDistricts = districtSpinner.getSelectedItem().toString();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
}