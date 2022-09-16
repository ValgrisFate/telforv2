package com.example.telforv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

  private EditText username, user_email, user_matricula, user_password;
  private Button registro_btn;
  private Uri mainUri = null;
  private FirebaseAuth mAuth;

  public static final String PREFERENCES = "prefKey";
  public static final String Name = "nameKey";
  public static final String Email = "emailKey";
  public static final String Password = "passwordKey";
  public static final String Matricula = "matriculaKey";

  SharedPreferences sharedPreferences;

  String name, password, email, matricula;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_up);

    sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);

    findViewById(R.id.tienes_acceso)
        .setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                onBackPressed();
              }
            });

    username = findViewById(R.id.username);
    user_password = findViewById(R.id.user_password);
    user_email = findViewById(R.id.user_email);
    user_matricula = findViewById(R.id.user_matricula);
    registro_btn = findViewById(R.id.registro_btn);

    mAuth = FirebaseAuth.getInstance();

    registro_btn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            name = username.getText().toString().trim();
            password = user_password.getText().toString().trim();
            email = user_email.getText().toString().trim();
            matricula = user_matricula.getText().toString().trim();

            if (!TextUtils.isEmpty(name)
                && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(email)
                && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                && !TextUtils.isEmpty(matricula)) {

              createUser(email, password);

            } else {
              Toast.makeText(
                      SignUpActivity.this, "Favor de llenar todos los campos", Toast.LENGTH_SHORT)
                  .show();
            }
          }
        });
  }

  private void createUser(String email, String password) {
    mAuth
        .createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(
            new OnCompleteListener<AuthResult>() {
              @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                  Toast.makeText(SignUpActivity.this, "Usuario creado", Toast.LENGTH_SHORT).show();

                  verifyEmail();

                } else {
                  Toast.makeText(
                          SignUpActivity.this, "Fallo, intentelo de nuevo", Toast.LENGTH_SHORT)
                      .show();
                }
              }
            })
        .addOnFailureListener(
            new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "Algo salio mal", Toast.LENGTH_SHORT).show();
              }
            });
  }

  private void verifyEmail() {
    FirebaseUser user = mAuth.getCurrentUser();
    if (user != null) {
      user.sendEmailVerification()
          .addOnCompleteListener(
              new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                  if (task.isSuccessful()) {

                    SharedPreferences.Editor pref = sharedPreferences.edit();
                    pref.putString(Name, name);
                    pref.putString(Password, password);
                    pref.putString(Email, email);
                    pref.putString(Matricula, matricula);
                    pref.commit();

                    Toast.makeText(
                            SignUpActivity.this, "Correo electronico enviado", Toast.LENGTH_SHORT)
                        .show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    finish();

                  } else {

                  }
                }
              });
    }
  }

  private void cropImage() {}
}
