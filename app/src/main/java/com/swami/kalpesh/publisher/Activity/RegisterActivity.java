package com.swami.kalpesh.publisher.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.swami.kalpesh.publisher.Model.TeacherInfoModel;
import com.swami.kalpesh.publisher.R;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

   private EditText name,emailId,qualification,designation,contact_no,password;
   private Button signupbtn;
   private CircleImageView profile_image;

   String user;
   boolean flag;
   DatabaseReference databaseReference;
   FirebaseAuth firebaseAuth;

   Uri imageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        name=(EditText)findViewById(R.id.id_name);
        emailId=(EditText)findViewById(R.id.id_email);
        qualification=(EditText)findViewById(R.id.id_qualification);
        designation=(EditText)findViewById(R.id.id_Designation);
        contact_no=(EditText)findViewById(R.id.id_contact_no);
        password=(EditText)findViewById(R.id.id_password);
        profile_image=findViewById(R.id.id_profile_pic);
        password.setTransformationMethod(new PasswordTransformationMethod());

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Image"),1);
            }
        });

        signupbtn=(Button)findViewById(R.id.id_signup_btn);
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!Validation())
                {
                    return;
                }

                if(imageuri!=null)
                {
                    final ProgressDialog progressDialog=new ProgressDialog(RegisterActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setTitle("Register");
                    progressDialog.setMessage("please wait..");
                    progressDialog.show();

                    user=emailId.getText().toString().toLowerCase();
                    int index=user.indexOf("@");
                    final String childkey=user.substring(0,index);

                    databaseReference=FirebaseDatabase.getInstance().getReference("VerificationData").child(childkey);
                    StorageReference storageReference= FirebaseStorage.getInstance().getReference("User").child(contact_no.getText().toString());
                    storageReference.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            TeacherInfoModel teacherInfoModel=new TeacherInfoModel();
                            teacherInfoModel.setName(name.getText().toString());
                            teacherInfoModel.setEmail(emailId.getText().toString());
                            teacherInfoModel.setQualification(qualification.getText().toString());
                            teacherInfoModel.setDesignation(designation.getText().toString());
                            teacherInfoModel.setContact_no(contact_no.getText().toString());
                            teacherInfoModel.setImage(taskSnapshot.getDownloadUrl().toString());
                            teacherInfoModel.setPassword(password.getText().toString());
                            teacherInfoModel.setApproval("Approved");
                            teacherInfoModel.setWorkshop(false);
                            databaseReference.setValue(teacherInfoModel);
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Information Sent", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(findViewById(R.id.id_profile_layout),"Fail Profile Update", BaseTransientBottomBar.LENGTH_SHORT)
                                    .show();
                        }
                    }) ;




                }
                else
                {
                    final ProgressDialog progressDialog=new ProgressDialog(RegisterActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setTitle("Register");
                    progressDialog.setMessage("please wait..");
                    progressDialog.show();

                    user=emailId.getText().toString().toLowerCase();
                    int index=user.indexOf("@");
                    final String childkey=user.substring(0,index);

                    DatabaseReference rootref=FirebaseDatabase.getInstance().getReference("VerificationData");
                    final DatabaseReference childref=rootref.child(childkey);
                    childref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            TeacherInfoModel teacherInfoModel=new TeacherInfoModel();
                            teacherInfoModel.setName(name.getText().toString());
                            teacherInfoModel.setQualification(qualification.getText().toString());
                            teacherInfoModel.setDesignation(designation.getText().toString());
                            teacherInfoModel.setEmail(emailId.getText().toString());
                            teacherInfoModel.setContact_no(contact_no.getText().toString());
                            teacherInfoModel.setPassword(password.getText().toString());
                            teacherInfoModel.setApproval("Pending");
                            childref.setValue(teacherInfoModel);

                            Toast.makeText(RegisterActivity.this, "Information Sent", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK){
            imageuri=data.getData();
            profile_image.setImageURI(imageuri);
        }
    }


    //Email Validation
    public boolean isEmailvalid()
    {
        if(Patterns.EMAIL_ADDRESS.matcher(emailId.getText().toString()).matches()){
            return true;
        }
        else
        {
            return false;
        }
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

    public boolean Validation()
    {
        if(TextUtils.isEmpty(name.getText()))
        {
            name.setError("Your Name");
            return false;
        }
        else if(TextUtils.isEmpty(emailId.getText()))
        {
            emailId.setError("Email ID");
            return false;
        }
        else if(TextUtils.isEmpty(qualification.getText()))
        {
            qualification.setError("Qualification");
            return false;
        }
        else if(TextUtils.isEmpty(designation.getText()))
        {
            designation.setError("Designation");
            return false;
        }
        else if(TextUtils.isEmpty(contact_no.getText()))
        {
            contact_no.setError("Contact No");
            return false;
        }
        else if(TextUtils.isEmpty(password.getText()))
        {
            password.setError("Password");
            return false;
        }
        else if(!isEmailvalid())
        {
            Snackbar.make(findViewById(R.id.register_activity),"Email Id Invalid",Snackbar.LENGTH_SHORT).show();
            return false;
        }
        else if(contact_no.getText().toString().length()<10||contact_no.getText().toString().length()>10)
        {
            Snackbar.make(findViewById(R.id.register_activity),"Mobile No. Invalid",Snackbar.LENGTH_SHORT).show();
            return false;
        }

        else if(password.getText().toString().length()<6)
        {
            Snackbar.make(findViewById(R.id.register_activity),"Password To Short",Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
