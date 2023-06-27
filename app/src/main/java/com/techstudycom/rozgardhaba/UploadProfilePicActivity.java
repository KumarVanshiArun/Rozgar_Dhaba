package com.techstudycom.rozgardhaba;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class UploadProfilePicActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private  ImageView imageViewUploadPic;
    private FirebaseAuth authProfile;

    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private  FirebaseStorage storage;
    private  static final int PICK_IMAGE_REQUEST = 22;

    private Uri filePath;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_pic);

        getSupportActionBar().setTitle("Upload Profile Picture");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button buttonUploadPicChoose =  findViewById(R.id.upload_pic_choose_button);
        Button buttonUploadPic = findViewById(R.id.upload_pic_button);
        progressBar = findViewById(R.id.progressbar);

        imageViewUploadPic = findViewById(R.id.imageView_profile_dp);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();
        storage = FirebaseStorage.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference("DisplayPic's");

        Uri uri = firebaseUser.getPhotoUrl();

        Picasso.with(UploadProfilePicActivity.this).load(uri).into(imageViewUploadPic);

        // Picasso.with(UploadProfilePicActivity.this).load(uri.get(position).getUrl()).error(R.drawable.demo_image).into(imageViewUploadPic.pic);

        buttonUploadPicChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        buttonUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UploadPic();
            }
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void UploadPic() {
        ProgressDialog progressDialog = null;
        if (filePath != null) {


            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setMessage("Please wait, while we are uploading your data");
            progressDialog.show();

            StorageReference fileReference = storageReference.child(
                    "images/"
                            + UUID.randomUUID().toString() + "." + getFileExtension(filePath));


            ProgressDialog progressDialog1 = new ProgressDialog(UploadProfilePicActivity.this);
            progressDialog1.setTitle("Uploading....");
            progressDialog1.show();
            fileReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            firebaseUser = authProfile.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                            firebaseUser.updateProfile(profileUpdates);


                        }
                    });

                    Toast.makeText(UploadProfilePicActivity.this, "Image Uploaded Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UploadProfilePicActivity.this, UserProfileActivity.class);

                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog1.dismiss();
                    Toast.makeText(UploadProfilePicActivity.this,"Failed.." + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    double progress
                            = (100.0
                            * snapshot.getBytesTransferred()
                            / snapshot.getTotalByteCount());
                    progressDialog1.setMessage(
                            "Uploaded "
                                    + (int)progress + "%");

                }
            });

        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(UploadProfilePicActivity.this, "No file is selected", Toast.LENGTH_SHORT).show();


        }
    }

    private  String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null & data.getData() !=null) {
            filePath = data.getData();
            imageViewUploadPic.setImageURI(filePath);

            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                imageViewUploadPic.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(UploadProfilePicActivity.this);
        }
        else if (id == R.id.menu_refresh){
            startActivity(getIntent());
            finish();

            overridePendingTransition(0, 0);

        } else if (id == R.id.menu_update_profile){
            Intent intent = new Intent(UploadProfilePicActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_update_email){
            Intent intent = new Intent(UploadProfilePicActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        }else if (id == R.id.menu_setting){
            Toast.makeText(UploadProfilePicActivity.this, "menu_setting", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UploadProfilePicActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UploadProfilePicActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(UploadProfilePicActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(UploadProfilePicActivity.this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            finish();

        }else {
            Toast.makeText(UploadProfilePicActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}

