package com.amirmohammed.seniorstepsfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    EditText editTextEmail, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        editTextEmail = findViewById(R.id.sign_in_et_email);
        editTextPassword = findViewById(R.id.sign_in_et_password);

    }

    public void signIn(View view) {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();


        if (email.isEmpty() || password.isEmpty()) {
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

        signInWithEmailAndPassword(email, password);
    }

    private void signInWithEmailAndPassword(String email, String password) {

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        }
                        else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(SignInActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    public void signUp(View view) {
        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
    }

    private boolean isEmailValid(String email) {
        final Pattern EMAIL_REGEX =
                Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
        return EMAIL_REGEX.matcher(email).matches();
    }
}
