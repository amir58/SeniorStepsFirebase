package com.amirmohammed.seniorstepsfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    CircleImageView profileCircle;
    TextView textViewUsername, textViewPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileCircle = findViewById(R.id.profile_circle_iv);
        textViewUsername = findViewById(R.id.profile_tv_username);
        textViewPhoneNumber = findViewById(R.id.profile_tv_phone_number);

        getUserData();
    }

    private void getUserData() {
        final String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        firestore.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = Objects.requireNonNull(task.getResult()).toObject(User.class);

                            textViewUsername.setText("Username : " + user.getUsername());
                            textViewPhoneNumber.setText("Phone : " + user.getPhoneNumber());

                            Glide.with(ProfileActivity.this)
                                    .load(user.getProfileImageUrl())
                                    .into(profileCircle);
                        }

                        else {

                        }
                    }
                });

    }


}
