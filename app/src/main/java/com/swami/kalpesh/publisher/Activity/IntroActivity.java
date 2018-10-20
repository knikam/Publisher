package com.swami.kalpesh.publisher.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.swami.kalpesh.publisher.Adapter.SliderAdapter;
import com.swami.kalpesh.publisher.R;

public class IntroActivity extends AppCompatActivity{

    private ViewPager slide_view;
    private SliderAdapter SliderAdapter;
    private int item;
    private LinearLayout linearLayout;
    private TextView[] mdot;

    private Button contineu_btn;
    private ImageView left_arrow, right_arrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        getSupportActionBar().hide();

        slide_view = (ViewPager) findViewById(R.id.slidepager);
        SliderAdapter = new SliderAdapter(this);
        slide_view.setAdapter(SliderAdapter);
        slide_view.addOnPageChangeListener(ViewListener);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        adddots(0);

        right_arrow = (ImageView) findViewById(R.id.id_right_arrow);
        left_arrow = (ImageView) findViewById(R.id.id_left_arrow);
        left_arrow.setVisibility(View.INVISIBLE);

        contineu_btn = findViewById(R.id.id_continue_btn);
        contineu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Teacher_ListActivity.class));
                finish();
            }
        });


        left_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slide_view.setCurrentItem(item - 1);
            }
        });

        right_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slide_view.setCurrentItem(item + 1);
            }
        });
    }

    public void adddots(int position) {
        mdot = new TextView[3];
        linearLayout.removeAllViews();
        for (int i = 0; i < mdot.length; i++) {
            mdot[i] = new TextView(this);
            mdot[i].setText(Html.fromHtml("&#8226;"));
            mdot[i].setTextSize(35);
            mdot[i].setTextColor(getResources().getColor(R.color.grey));

            linearLayout.addView(mdot[i]);

        }
        if (mdot.length > 0) {
            mdot[position].setTextColor(getResources().getColor(R.color.white));
        }
    }

        ViewPager.OnPageChangeListener ViewListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {

            adddots(i);
            item=i;
            if(item==0)
            {
                left_arrow.setVisibility(View.INVISIBLE);
            }
            else if(item==mdot.length-1)
            {
                right_arrow.setVisibility(View.INVISIBLE);
            }
            else
            {
                left_arrow.setVisibility(View.VISIBLE);
                right_arrow.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }
}
