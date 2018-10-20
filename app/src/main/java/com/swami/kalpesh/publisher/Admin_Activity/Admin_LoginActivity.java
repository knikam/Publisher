package com.swami.kalpesh.publisher.Admin_Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.swami.kalpesh.publisher.R;

public class Admin_LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button loginbtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        getSupportActionBar().setTitle("Control Authentication");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username=findViewById(R.id.id_admin_username);
        password=findViewById(R.id.id_admin_password);
        loginbtn=findViewById(R.id.id_admin_loginbtn);
        password.setTransformationMethod(new PasswordTransformationMethod());

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               startActivity(new Intent(getApplicationContext(),Admin_NavigationActivity.class));
            }
        });

    }

    public void Sign_in()
    {



    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onStart();
    }
}
