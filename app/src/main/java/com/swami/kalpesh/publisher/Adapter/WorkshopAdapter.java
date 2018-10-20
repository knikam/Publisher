package com.swami.kalpesh.publisher.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.swami.kalpesh.publisher.Model.WorkshopModel;
import com.swami.kalpesh.publisher.R;

import java.util.List;

public class WorkshopAdapter extends RecyclerView.Adapter<WorkshopAdapter.ViewHolder>{
    List<WorkshopModel> workshopModelList;
    Context context;

    public WorkshopAdapter(List<WorkshopModel> workshopModelList, Context context) {
        this.workshopModelList = workshopModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public WorkshopAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.workshop_card_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final WorkshopAdapter.ViewHolder holder, final int position) {

        holder.workshop_name.setText(workshopModelList.get(position).getName_of_workshop());
        holder.organize_by.setText(workshopModelList.get(position).getOrganized_by());
        holder.duration.setText(workshopModelList.get(position).getDuration());
        holder.start_date.setText(workshopModelList.get(position).getStart_date());
        holder.end_date.setText(workshopModelList.get(position).getEnd_date());
        holder.acadmic_year.setText(workshopModelList.get(position).getAcadmic_year());

        holder.workshop_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                PopupMenu workshop_data=new PopupMenu(context,view);
                workshop_data.getMenuInflater().inflate(R.menu.data_update_menu,workshop_data.getMenu());
                workshop_data.show();

                workshop_data.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        String user = workshopModelList.get(position).getEmailid();
                        int index = user.indexOf("@");
                        final String childkey = user.substring(0, index);


                        final DatabaseReference rootref=FirebaseDatabase.getInstance().getReference("Workshop_Detail").child(childkey)
                                .child(workshopModelList.get(position).getName_of_workshop());
                        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                rootref.getRef().removeValue();
                                workshopModelList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, workshopModelList.size());
                                Toast.makeText(context, "Detail Remove", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        return false;
                    }

                });

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
       return workshopModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView workshop_name,organize_by,duration,start_date,end_date,acadmic_year;
        RelativeLayout workshop_card;
        public ViewHolder(View itemView) {
            super(itemView);
            workshop_card=itemView.findViewById(R.id.workshop_show_card);
            workshop_name=(TextView)itemView.findViewById(R.id.id_workshop_name);
            organize_by=(TextView)itemView.findViewById(R.id.id_workshop_oraganizer);
            duration=(TextView)itemView.findViewById(R.id.id_workshop_duration);
            start_date=(TextView)itemView.findViewById(R.id.id_workshop_startdate);
            end_date=(TextView)itemView.findViewById(R.id.id_workshop_enddate);
            acadmic_year=(TextView)itemView.findViewById(R.id.id_workshop_acadmicyear);
        }
    }
}
