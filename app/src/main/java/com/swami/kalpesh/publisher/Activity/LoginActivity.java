package com.swami.kalpesh.publisher.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.swami.kalpesh.publisher.R;

public class LoginActivity extends AppCompatActivity {

    EditText user,password;
    Button loginbtn;
    TextView forgotpassword;

    EditText email;

    Boolean flag=false;
    String username;

    PopupWindow popupWindow;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    FirebaseAuth firebaseAuth;
    String faculty_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        faculty_name=getIntent().getStringExtra("faculty name");

        user=(EditText)findViewById(R.id.id_Username);
        password=(EditText)findViewById(R.id.id_Password);
        password.setTransformationMethod(new PasswordTransformationMethod());

        sharedPreferences=getSharedPreferences("username",MODE_PRIVATE);
        editor=sharedPreferences.edit();

        final String Type=getIntent().getStringExtra("type");
        firebaseAuth=FirebaseAuth.getInstance();

        //Already Login Check
        if(firebaseAuth.getCurrentUser()!=null)
        {
            if (Type.equals("Workshop"))
            {
                Intent intent=new Intent(getApplicationContext(),Add_WorkshopActivity.class);
                intent.putExtra("username",sharedPreferences.getString("username",""));
                startActivity(intent);
                finish();
            }
            else
            {
                Intent intent=new Intent(getApplicationContext(),Add_PublicationActivity.class);
                intent.putExtra("username",sharedPreferences.getString("username",""));
                startActivity(intent);
                finish();
            }
        }
        else
        {
            editor.remove("username");
            editor.apply();
        }



        forgotpassword=(TextView)findViewById(R.id.id_login_forgotpass);
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(LoginActivity.this);
                bottomSheetDialog.setContentView(R.layout.forgot_password_layout);
                bottomSheetDialog.show();

                Button forgotbtn=bottomSheetDialog.findViewById(R.id.id_login_forgotpass_btn);
                email=bottomSheetDialog.findViewById(R.id.id_login_email);
                forgotbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(TextUtils.isEmpty(email.getText()))
                        {
                            Snackbar.make(bottomSheetDialog.getWindow().getDecorView(),"Enter Email Id",Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        else
                        {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "Email Send", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });

            }
        });


        loginbtn=(Button)findViewById(R.id.id_login_btn);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!Validation())
                {
                    return;
                }

                final ProgressDialog progressDialog=new ProgressDialog(LoginActivity.this);
                progressDialog.setTitle("Login");
                progressDialog.setMessage("Log In");
                progressDialog.setCancelable(false);
                progressDialog.show();


                    username=user.getText().toString();
                    firebaseAuth.signInWithEmailAndPassword(user.getText().toString(),password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        editor.putString("username",user.getText().toString());
                                        editor.commit();
                                        if(Type.equals("Workshop"))
                                        {
                                            Intent intent=new Intent(getApplicationContext(),Add_WorkshopActivity.class);
                                            intent.putExtra("username",user.getText().toString());
                                            startActivity(intent);
                                            finish();
                                        }
                                        else if(Type.equals("Publication"))
                                        {
                                            Intent intent=new Intent(getApplicationContext(),Add_PublicationActivity.class);
                                            intent.putExtra("username",user.getText().toString());
                                            startActivity(intent);
                                            finish();
                                        }
                                        else
                                        {
                                            finish();
                                        }
                                    }
                                    else
                                    {
                                        Snackbar.make(findViewById(R.id.login_activity),"Check Username password",Snackbar.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void onStart() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onStart();
    }


    public boolean Validation()
    {
        if(TextUtils.isEmpty(user.getText().toString()))
        {
            user.setError("UserName");
            return false;
        }
        else if(TextUtils.isEmpty(password.getText().toString()))
        {
            password.setError("Password");
            return false;
        }
            return true;
    }
}
