package com.example.telforv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LoginActivity extends AppCompatActivity {

  private EditText userEmail, userPassword;
  private Button loginBtn;
  private TextView forgetPassword;
  private FirebaseAuth mAuth;
  private FirebaseAuth.AuthStateListener authStateListener;

  public static final String PREFERENCES = "prefKey";
  public static final String Name = "nameKey";
  public static final String Email = "emailKey";
  public static final String Password = "passwordKey";
  public static final String Matricula = "matriculaKey";

  SharedPreferences sharedPreferences;

  StorageReference reference;
  FirebaseFirestore firebaseFirestore;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
    reference = FirebaseStorage.getInstance().getReference();
    firebaseFirestore = FirebaseFirestore.getInstance();

    mAuth = FirebaseAuth.getInstance();
    authStateListener =
        new FirebaseAuth.AuthStateListener() {
          @Override
          public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
              Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
              startActivity(intent);
              finish();
            }
          }
        };

    findViewById(R.id.no_tienes_acceso)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
              }
            });

    userEmail = findViewById(R.id.user_email);
    userPassword = findViewById(R.id.user_password);
    loginBtn = findViewById(R.id.login_btn);
    forgetPassword = findViewById(R.id.forget_password);
    mAuth = FirebaseAuth.getInstance();

    loginBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {

            String email = userEmail.getText().toString().trim();
            String password = userPassword.getText().toString().trim();
            mAuth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                      @Override
                      public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                          startActivity((new Intent(LoginActivity.this, HomeActivity.class)));
                          verifyEmail();
                        } else {
                          Toast.makeText(
                                  LoginActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT)
                              .show();
                        }
                      }
                    });
          }
        });

    forgetPassword
        .findViewById(R.id.forget_password)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
              }
            });
  }

  private void verifyEmail() {

    FirebaseUser user = mAuth.getCurrentUser();
    assert user != null;
    if (user.isEmailVerified()) {

      String name = sharedPreferences.getString(Name, null);
      String password = sharedPreferences.getString(Password, null);
      String email = sharedPreferences.getString(Email, null);
      String matricula = sharedPreferences.getString(Matricula, null);

      if (name != null && password != null && email != null && matricula != null) {}

      startActivity(new Intent(LoginActivity.this, HomeActivity.class));
      finish();

    } else {
      mAuth.signOut();
      Toast.makeText(LoginActivity.this, "Por favor, verifica tu correo", Toast.LENGTH_SHORT)
          .show();
    }
  }
}
