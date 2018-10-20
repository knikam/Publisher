package com.swami.kalpesh.publisher.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
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
import com.swami.kalpesh.publisher.Model.PublicationModel;
import com.swami.kalpesh.publisher.R;

import java.util.List;

public class PublicationAdapter extends RecyclerView.Adapter<PublicationAdapter.ViewHolder>{

    List<PublicationModel> publicationModel;
    Context context;

    public PublicationAdapter(List<PublicationModel> publicationModel, Context context) {
        this.publicationModel = publicationModel;
        this.context = context;
    }

    @NonNull
    @Override
    public PublicationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.publication_card_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicationAdapter.ViewHolder holder, final int position) {

        holder.faculty_name.setText(publicationModel.get(position).getName_of_faculy());
        holder.author_name.setText(publicationModel.get(position).getName_of_author());
        holder.publication_type.setText(publicationModel.get(position).getType_of_publication());
        holder.title_of_paper.setText(publicationModel.get(position).getTitle_of_paper());
        holder.conference_name.setText(publicationModel.get(position).getName_of_conference());
        holder.publication_date.setText(publicationModel.get(position).getDate_of_publication());
        holder.DOI.setText(publicationModel.get(position).getDOI());
        holder.publication_year.setText(publicationModel.get(position).getYear_of_publication());

        holder.publication_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(FirebaseAuth.getInstance().getCurrentUser()!=null)
                if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(publicationModel.get(position).getEmailid()))
                {
                    PopupMenu publication = new PopupMenu(context, view);
                    publication.getMenuInflater().inflate(R.menu.data_update_menu, publication.getMenu());
                    publication.show();

                    publication.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            String user = publicationModel.get(position).getEmailid();
                            int index = user.indexOf("@");
                            final String childkey = user.substring(0, index);

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            Query removeQuery = ref.child("Publication_Detail").child(childkey).child(publicationModel.get(position).getTitle_of_paper());

                            removeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    dataSnapshot.getRef().removeValue();
                                    publicationModel.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, publicationModel.size());
                                    Toast.makeText(context, "Detail Removed", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            return true;
                        }
                    });
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return publicationModel.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView faculty_name,author_name,publication_type,title_of_paper,conference_name,publication_date,DOI,publication_year;
        RelativeLayout publication_card;

        public ViewHolder(View itemView) {
            super(itemView);

            publication_card=itemView.findViewById(R.id.publication_show_card);
            faculty_name=(TextView)itemView.findViewById(R.id.id_publication_name);
            author_name=(TextView)itemView.findViewById(R.id.id_publication_author);
            publication_type=(TextView)itemView.findViewById(R.id.id_publication_type);
            title_of_paper=(TextView)itemView.findViewById(R.id.id_publication_paperTitle);
            conference_name=(TextView)itemView.findViewById(R.id.id_publication_conferenceName);
            publication_date=(TextView)itemView.findViewById(R.id.id_publication_publicationDate);
            DOI=(TextView)itemView.findViewById(R.id.id_publication_doi);
            publication_year=(TextView)itemView.findViewById(R.id.id_publication_publicationYear);
        }
    }
}
