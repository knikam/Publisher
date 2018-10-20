package com.swami.kalpesh.publisher.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.swami.kalpesh.publisher.R;

public class MainActivity extends AppCompatActivity {


    TextView txt_pccoe,txt_workshop,txt_compterdept;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        txt_pccoe=findViewById(R.id.id_pccoe_txt);
        txt_workshop=findViewById(R.id.id_workshop_txt);
        txt_compterdept=findViewById(R.id.id_computer_txt);

        Animation fade_in= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        txt_pccoe.startAnimation(fade_in);
        txt_workshop.startAnimation(fade_in);
        txt_compterdept.startAnimation(fade_in);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {

                Boolean firsttime=getSharedPreferences("PREFERENCE",MODE_PRIVATE).getBoolean("first",true);
                if(!firsttime)
                {
                    startActivity(new Intent(getApplicationContext(),Teacher_ListActivity.class));
                    finish();
                }
                else
                {
                    startActivity(new Intent(MainActivity.this,IntroActivity.class));
                    finish();

                }
                getSharedPreferences("PREFERENCE",MODE_PRIVATE).edit().putBoolean("first",false).commit();

            }
        },3000);
    }

}
