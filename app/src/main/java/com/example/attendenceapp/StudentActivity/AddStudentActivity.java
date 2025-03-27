package com.example.attendenceapp.StudentActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;

import com.example.attendenceapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AddStudentActivity extends AppCompatActivity {

    private TextInputEditText studentIdEditText;
    private Spinner classSpinner, semSpinner;
    private AppCompatButton captureImageButton, submitButton;
    private ImageView capturedImage;
    private Bitmap studentImageBitmap;

    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference().child("StudentImages");

        studentIdEditText = findViewById(R.id.stdId);
        classSpinner = findViewById(R.id.classSpinner);
        semSpinner = findViewById(R.id.semSpinner);
        captureImageButton = findViewById(R.id.captureButton);
        capturedImage = findViewById(R.id.capturedImage);
        submitButton = findViewById(R.id.submitData);

        // Setup the classSpinner
        ArrayAdapter<CharSequence> classAdapter = ArrayAdapter.createFromResource(this,
                R.array.class_name, android.R.layout.simple_spinner_item);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(classAdapter);

        // Setup the semSpinner
        ArrayAdapter<CharSequence> semAdapter = ArrayAdapter.createFromResource(this,
                R.array.semesterName, android.R.layout.simple_spinner_item);
        semAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semSpinner.setAdapter(semAdapter);

        // Check for camera permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        // Capture Image Button Click
        captureImageButton.setOnClickListener(v -> dispatchTakePictureIntent());

        // Submit Data Button Click
        submitButton.setOnClickListener(v -> {
            if (isInternetAvailable()) {
                checkIfStudentAuthorized();
            } else {
                Toast.makeText(AddStudentActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "Camera not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            studentImageBitmap = (Bitmap) extras.get("data");
            capturedImage.setImageBitmap(studentImageBitmap);
        }
    }

    private void checkIfStudentAuthorized() {
        String studentId = studentIdEditText.getText().toString().trim();

        if (studentId.isEmpty() || studentImageBitmap == null) {
            Toast.makeText(this, "Please enter student ID and capture image", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("student_attendence_details").document(studentId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Student is authorized, proceed with image upload
                            showProgressDialog();
                            uploadStudentImage(studentId);
                        } else {
                            // Student is not authorized
                            Toast.makeText(AddStudentActivity.this, "User is not registered", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddStudentActivity.this, "Error checking Firestore: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void uploadStudentImage(String studentId) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        studentImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        StorageReference imageRef = storageReference.child(studentId + ".jpg");

        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                submitStudentData(studentId, uri.toString());
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(AddStudentActivity.this, "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(AddStudentActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(snapshot -> {
            long bytesTransferred = snapshot.getBytesTransferred();
            long totalByteCount = snapshot.getTotalByteCount();
            int progress = (int) (100 * bytesTransferred / totalByteCount);
            progressDialog.setProgress(progress);
        }).addOnCompleteListener(task -> {
            progressDialog.dismiss();
        });
    }

    private void submitStudentData(String studentId, String imageUrl) {
        String className = classSpinner.getSelectedItem().toString();
        String semester = semSpinner.getSelectedItem().toString();

        // Creating a map to store student data in Firestore
        Map<String, Object> student = new HashMap<>();
        student.put("studentId", studentId);
        student.put("className", className);
        student.put("semester", semester);
        student.put("imageUrl", imageUrl);

        // Storing student details in Firestore
        firestore.collection("student_attendence_details").document(studentId)
                .set(student, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddStudentActivity.this, "Student registered successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddStudentActivity.this, "Failed to save student data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
