package com.amirmohammed.seniorstepsfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    private final String PROFILE_IMAGES = "UsersProfileImages";

    FirebaseAuth auth = FirebaseAuth.getInstance();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    EditText editTextEmail, editTextPassword, editTextRePassword, editTextUsername, editTextPhoneNumber;

    CircleImageView profileImage;

    Uri profileImageUri = null;

    User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextEmail = findViewById(R.id.sign_up_email);
        editTextPassword = findViewById(R.id.sign_up_password);
        editTextRePassword = findViewById(R.id.sign_up_re_password);
        editTextUsername = findViewById(R.id.sign_up_username);
        editTextPhoneNumber = findViewById(R.id.sign_up_phone_number);

        profileImage = findViewById(R.id.profile_image);

        print("onCreate");
    }

    public void signUp(View view) {
        print("Button clicked");

        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String rePassword = editTextRePassword.getText().toString();
        String username = editTextUsername.getText().toString();
        String phoneNumber = editTextPhoneNumber.getText().toString();

        if (profileImageUri == null) {
            Toast.makeText(this, "Please select profile image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty() || password.isEmpty() || rePassword.isEmpty() || username.isEmpty()
                || phoneNumber.isEmpty()) {

            Toast.makeText(this, "Please fill all data", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isEmailValid(email)) {
            editTextEmail.setError("Please enter valid email");
        }

        if (password.length() < 8) {
            editTextPassword.setError("Password must be bigger than seven characters");
            return;
        }

        if (rePassword.length() < 8) {
            editTextPassword.setError("Password must be bigger than seven characters");
            return;
        }

        if (!password.equals(rePassword)) {
            editTextPassword.setError("Password not matching");
            editTextRePassword.setError("Password not matching");
            return;
        }

        user = new User();
        user.setUsername(username);
        user.setPhoneNumber(phoneNumber);
//        if (!isPasswordValid(password)) {
//            editTextPassword.setError("Please enter valid password");
//            return;
//        }
        print(user.toString());

        createAccountByEmailAndPassword(email, password);
    }

    private void createAccountByEmailAndPassword(String email, String password) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            uploadImageProfile();
                            print("Account create");

                        } else {
                            showExceptions(Objects.requireNonNull(task.getException()));
                        }

                    }
                });

    }

    private boolean isEmailValid(String email) {
        final Pattern EMAIL_REGEX =
                Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
        return EMAIL_REGEX.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        final Pattern PASSWORD_REGEX =
                Pattern.compile("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\\\S+$).{8,}", Pattern.CASE_INSENSITIVE);
        return PASSWORD_REGEX.matcher(password).matches();
    }

    public void openGallery(View view) {
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        startActivityForResult(intent, 1);
        print("Open Gallery");
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profileImageUri = result.getUri();
                profileImage.setImageURI(profileImageUri);

                print("Image Uri : " + profileImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageProfile() {
        storageReference.child(PROFILE_IMAGES)
                .child(auth.getCurrentUser().getUid())
                .putFile(profileImageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            downloadImageProfileUrl();
                            print("Upload success");

                        } else {
                            showExceptions(Objects.requireNonNull(task.getException()));
                        }
                    }
                });
    }

    private void downloadImageProfileUrl() {
        storageReference.child(PROFILE_IMAGES)
                .child(auth.getCurrentUser().getUid())
                .getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String imageProfileUrl = task.getResult().toString();
                            user.setProfileImageUrl(imageProfileUrl);

                            print("Download success : " + imageProfileUrl);
                            print(user.toString());

                            setUserDataToCloud();
                        } else {
                            showExceptions(Objects.requireNonNull(task.getException()));
                        }
                    }
                });
    }

    private void setUserDataToCloud() {
        firestore.collection("users")
                .document(auth.getCurrentUser().getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SignUpActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                        print("Finish");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showExceptions(e);
                    }
                });
    }

    private void showExceptions(Exception e) {
        String errorMessage = e.getMessage();
        Toast.makeText(SignUpActivity.this,
                errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void print(String message) {
        Log.i("CreateAccount", "print: " + message);
    }

}


