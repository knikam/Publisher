package com.swami.kalpesh.publisher.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.swami.kalpesh.publisher.Activity.Selected_TeacherActivity;
import com.swami.kalpesh.publisher.Interfaces.OnLoadMoreListener;
import com.swami.kalpesh.publisher.Model.TeacherInfoModel;
import com.swami.kalpesh.publisher.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherListAdapter extends RecyclerView.Adapter<TeacherListAdapter.ViewHolder> implements Filterable{

    List<TeacherInfoModel> teacherLists;
    List<TeacherInfoModel> filterList;
    Context context;
    RecyclerView recyclerView;


    public TeacherListAdapter(List<TeacherInfoModel> teacherLists, Context context,RecyclerView recyclerView) {
        this.teacherLists = teacherLists;
        this.filterList=teacherLists;
        this.context = context;
        this.recyclerView=recyclerView;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView  name,qualification,designation,email,contact_no;
        CircleImageView profile_pic;
        LinearLayout user_card;

        public ViewHolder(View itemView) {
            super(itemView);
            user_card=(LinearLayout) itemView.findViewById(R.id.id_teacher_list_card);
            name=(TextView)itemView.findViewById(R.id.id_card_name);
            qualification=(TextView)itemView.findViewById(R.id.id_card_qualifiaction);
            designation=(TextView)itemView.findViewById(R.id.id_card_designation);
            email=(TextView)itemView.findViewById(R.id.id_card_email);
            contact_no=(TextView)itemView.findViewById(R.id.id_card_contact);
            profile_pic=(CircleImageView)itemView.findViewById(R.id.id_card_image);
        }
    }


    @NonNull
    @Override
    public TeacherListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.teacherlist_card_layout, parent, false);
            return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewholder, final int position) {

        viewholder.name.setText(filterList.get(position).getName());
        viewholder.qualification.setText(filterList.get(position).getQualification());
        viewholder.designation.setText(filterList.get(position).getDesignation());
        viewholder.email.setText(filterList.get(position).getEmail());
        viewholder.contact_no.setText(filterList.get(position).getContact_no());
        Glide.with(context)
                .load(filterList
                .get(position)
                .getImage()).fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.placeholder)
                .into(viewholder.profile_pic);

        viewholder.user_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Selected_TeacherActivity.class);
                intent.putExtra("username", teacherLists.get(position).getEmail());
                intent.putExtra("name", teacherLists.get(position).getName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
       return filterList.size();
    }


    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String keyword=charSequence.toString();

                if(keyword.isEmpty())
                {
                    filterList=teacherLists;
                }
                else
                {
                    ArrayList<TeacherInfoModel> filter=new ArrayList<>();

                    for (TeacherInfoModel model:teacherLists)
                    {

                        if(model.getName().toLowerCase().contains(keyword))
                        {
                            filter.add(model);
                        }
                    }
                    filterList=filter;
                }

                FilterResults filterResults=new FilterResults();
                filterResults.values=filterList;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                filterList=(ArrayList<TeacherInfoModel>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
