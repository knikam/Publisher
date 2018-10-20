package com.swami.kalpesh.publisher.Activity;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.swami.kalpesh.publisher.R;

public class Selected_TeacherActivity extends AppCompatActivity {

    private Button workshop,publication;
    private PopupWindow popupWindow;

    String username;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_teacher);
        getSupportActionBar().setTitle("Workshop & publication");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        workshop=(Button)findViewById(R.id.id_workshop_btn);
        publication=(Button)findViewById(R.id.id_publication_btn);

        username=getIntent().getStringExtra("username");
        name=getIntent().getStringExtra("name");


        TextView user_name=(TextView)findViewById(R.id.id_user_name);
        user_name.setText(name);

        workshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup(view);
            }
        });
        publication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup(view);
            }
        });

        final TextView siteurl=(TextView)findViewById(R.id.id_siteurl);
        siteurl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(siteurl.getText().toString())));
            }
        });
    }

    /*-------------------------------------------------------------------------------------
    *   Add Data Or View data choose pop up window
    * ---------------------------------------------------------------------------------------*/

    public void popup(View view)
    {

        final BottomSheetDialog sheetDialog=new BottomSheetDialog(Selected_TeacherActivity.this);
        sheetDialog.setContentView(R.layout.workshop_publication_popup);
        sheetDialog.show();

        Button add=(Button)sheetDialog.findViewById(R.id.id_add_btn);
        Button show=(Button)sheetDialog.findViewById(R.id.id_show_btn);

        if(view.getId()==R.id.id_workshop_btn) {
            add.setText("Add Workshop Detail");
            show.setText("Show Workshop Detail");
        }
        else
        {
            add.setText("Add Publication Detail");
            show.setText("Show Publication Detail");

        }

        if(add.getText()=="Add Workshop Detail")
        {
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                    intent.putExtra("type","Workshop");
                    startActivity(intent);
                    sheetDialog.dismiss();
                }
            });
        }
        else{

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                    intent.putExtra("type","Publication");
                    intent.putExtra("username",username);
                    startActivity(intent);
                    sheetDialog.dismiss();
                }
            });
        }



        if(show.getText()=="Show Workshop Detail")
        {
            show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getApplicationContext(), ShowWorkshopActivity.class);
                    intent.putExtra("username",username);
                    startActivity(intent);
                    sheetDialog.dismiss();
                }
            });
        }
        else
        {
            show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getApplicationContext(), ShowPublicationActivity.class);
                    intent.putExtra("username",username);
                    startActivity(intent);
                    sheetDialog.dismiss();

                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
