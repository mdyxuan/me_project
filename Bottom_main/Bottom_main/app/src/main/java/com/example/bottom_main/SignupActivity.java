package com.example.bottom_main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {

    EditText signupName, signupEmail, signupUsername, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");
                String name = signupName.getText().toString();
                String email = signupEmail.getText().toString();
                String username = signupUsername.getText().toString();
                String password = signupPassword.getText().toString();

                // 針對四個欄位去做檢查
                if (!validateName() | !validateUserEmail() | !validateUsername() | !validatePassword()) {
                    return;
                }

                reference.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) { // 檢查users中，name是不是已經被註冊過了
                            Toast.makeText(SignupActivity.this, "帳號已被註冊", Toast.LENGTH_SHORT).show();
                        } else {
                            HelperClass helperClass = new HelperClass(name, email, username, password); // 建立HelperClass
                            reference.child(username).setValue(helperClass).addOnCompleteListener(task -> { // 新增資料
                                if (task.isSuccessful()) {
                                    Log.d("MainActivity", "新增資料成功.");
                                } else {
                                    Log.d("MainActivity", "新增資料失敗.", task.getException());
                                }
                            });
                            Toast.makeText(SignupActivity.this, "你註冊成功!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    public Boolean validateName()
    {
        String val = signupName.getText().toString();
        if(val.isEmpty())
        {
            signupName.setError("未輸入註冊名字!");
            return false;
        }
        else
        {
            signupName.setError(null);
            return true;
        }
    }
    public Boolean validateUserEmail()
    {
        String val = signupEmail.getText().toString();
        if(val.isEmpty())
        {
            signupEmail.setError("未輸入註冊信箱!");
            return false;
        }
        else
        {
            signupEmail.setError(null);
            return true;
        }
    }
    public Boolean validateUsername()
    {
        String val = signupUsername.getText().toString();
        if(val.isEmpty())
        {
            signupUsername.setError("未輸入註冊使用者名稱!");
            return false;
        }
        else
        {
            signupEmail.setError(null);
            return true;
        }
    }

    public Boolean validatePassword()
    {
        String val = signupPassword.getText().toString();
        if(val.isEmpty())
        {
            signupPassword.setError("未輸入註冊密碼!");
            return false;
        }
        else
        {
            signupPassword.setError(null);
            return true;
        }
    }


}