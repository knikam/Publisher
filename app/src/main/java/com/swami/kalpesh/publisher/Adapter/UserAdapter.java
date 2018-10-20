package com.swami.kalpesh.publisher.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.swami.kalpesh.publisher.Model.TeacherInfoModel;
import com.swami.kalpesh.publisher.R;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> implements Filterable{

    List<TeacherInfoModel> teacherInfoModelList;
    List<TeacherInfoModel> filterlist;
    Context context;

    DatabaseReference databaseReference;

    public UserAdapter(List<TeacherInfoModel> teacherInfoModelList, Context context) {
        this.teacherInfoModelList = teacherInfoModelList;
        this.filterlist=teacherInfoModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.user_card_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserAdapter.ViewHolder holder, final int position) {

        holder.name.setText(filterlist.get(position).getName());
        holder.qualification.setText(filterlist.get(position).getQualification());
        holder.designation.setText(filterlist.get(position).getDesignation());
        holder.email.setText(filterlist.get(position).getEmail());
        holder.contact.setText(filterlist.get(position).getContact_no());
        Glide.with(context).load(filterlist.get(position).getImage()).error(R.drawable.placeholder).into(holder.profilepic);
        holder.acceptbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                databaseReference= FirebaseDatabase.getInstance().getReference("Active_users");
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            final  ProgressDialog progressDialog=new ProgressDialog(context);
                            progressDialog.setMessage("wait..");
                            progressDialog.show();
                            FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                            firebaseAuth.createUserWithEmailAndPassword(teacherInfoModelList.get(position).getEmail(),teacherInfoModelList.get(position).getPassword())
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    TeacherInfoModel teacherInfoModel = new TeacherInfoModel();
                                    teacherInfoModel.setName(teacherInfoModelList.get(position).getName());
                                    teacherInfoModel.setQualification(teacherInfoModelList.get(position).getQualification());
                                    teacherInfoModel.setDesignation(teacherInfoModelList.get(position).getDesignation());
                                    teacherInfoModel.setEmail(teacherInfoModelList.get(position).getEmail());
                                    teacherInfoModel.setContact_no(teacherInfoModelList.get(position).getContact_no());
                                    teacherInfoModel.setImage(teacherInfoModelList.get(position).getImage());
                                    teacherInfoModel.setWorkshop(false);
                                    teacherInfoModel.setApproval("Approved");
                                    String keyname=teacherInfoModelList.get(position).getEmail().toString().toLowerCase();
                                    int index=keyname.indexOf("@");
                                    final String childkey=keyname.substring(0,index);
                                    DatabaseReference ref = databaseReference.child(childkey);
                                    ref.setValue(teacherInfoModel);

                                    Toast.makeText(context, "User Approved", Toast.LENGTH_SHORT).show();
                                    Delete_user(position,teacherInfoModel.getEmail());
                                    progressDialog.dismiss();
                                    FirebaseAuth.getInstance().signOut();

                                }
                            });

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

            }
        });

        holder.deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Delete_user(position,teacherInfoModelList.get(position).getEmail());
                Toast.makeText(context, "Profile Discard ", Toast.LENGTH_SHORT).show();
//                FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
//                AuthCredential authCredential= EmailAuthProvider.getCredential(teacherInfoModelList.get(position).getEmail(),teacherInfoModelList.get(position).getPassword());
//
//                firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(context, "Discart User", Toast.LENGTH_SHORT).show();
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
            }
        });
    }

    public void Delete_user(int position,String email)
    {
        String user=email.toLowerCase();
        int index=user.indexOf("@");
        final String childkey=user.substring(0,index);

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("VerificationData");
        DatabaseReference ref=databaseReference.child(childkey);
        ref.setValue(null);
    }

    @Override
    public int getItemCount() {
        return filterlist.size();
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String keyword=charSequence.toString();

                if(keyword.isEmpty())
                {
                    filterlist=teacherInfoModelList;
                }
                else
                {
                    ArrayList<TeacherInfoModel> filterdata=new ArrayList<>();

                    for (TeacherInfoModel teacherInfoModel:teacherInfoModelList)
                    {
                        if(teacherInfoModel.getName().toLowerCase().contains(keyword))
                        {
                            filterdata.add(teacherInfoModel);
                        }
                    }
                    filterlist=filterdata;
                }

                FilterResults filterResults=new FilterResults();
                filterResults.values=filterlist;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    filterlist= (List<TeacherInfoModel>) filterResults.values;
                    notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView name,qualification,designation,email,contact;
        ImageView profilepic;
        Button acceptbtn,deletebtn;
        public ViewHolder(View itemView) {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.id_usercard_name);
            qualification=(TextView)itemView.findViewById(R.id.id_usercard_qualifiaction);
            designation=(TextView)itemView.findViewById(R.id.id_usercard_designation);
            email=(TextView)itemView.findViewById(R.id.id_usercard_email);
            contact=(TextView)itemView.findViewById(R.id.id_usercard_contact);
            profilepic=(ImageView) itemView.findViewById(R.id.id_usercard_image);
            acceptbtn=(Button)itemView.findViewById(R.id.id_approve_btn);
            deletebtn=(Button)itemView.findViewById(R.id.id_delete_btn);
        }
    }
}
