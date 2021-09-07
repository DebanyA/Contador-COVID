package prueba.app.contadorcovid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import prueba.app.contadorcovid.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText mEditTestName;
    private EditText mEditTestEmail;
    private EditText mEditTestPassword;
    private Button   mButtonRegister;
    private Button mButtonSendToLogin;



    private String name ="";
    private String email ="";
    private String password ="";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mEditTestName = (EditText) findViewById(R.id.editTextName);
        mEditTestEmail = (EditText) findViewById(R.id.editTextEmail);
        mEditTestPassword = (EditText) findViewById(R.id.editTextPassword);
        mButtonRegister = (Button) findViewById(R.id.btnRegister);
        mButtonSendToLogin = (Button) findViewById(R.id.btnSendToLogin);



        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = mEditTestName.getText().toString();
                email = mEditTestEmail.getText().toString();
                password = mEditTestPassword.getText().toString();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {

                    if (password.length() >= 6){
                        registerUser();
                    }
                    else {
                        Toast.makeText( MainActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText( MainActivity.this, "Debe completar los campos", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mButtonSendToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });


    }

    private void registerUser(){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    Map<String, Object> map = new HashMap<>();
                    map.put("name",name);
                    map.put("email",email);
                    map.put("password",password);

                    String id = mAuth.getCurrentUser().getUid();

                    mDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()){
                                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                finish();
                            }
                            else {
                                Toast.makeText(MainActivity.this, "No se pudo crear el perfil correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
                else {
                    Toast.makeText(MainActivity.this, "No pudo registrarse este usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() !=null){
            startActivity(new Intent(MainActivity.this,ProfileActivity.class));
            finish();
        }
    }
}