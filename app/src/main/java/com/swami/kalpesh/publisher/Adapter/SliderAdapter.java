package com.swami.kalpesh.publisher.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.swami.kalpesh.publisher.R;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context=context;
    }
    //image array
    public int[] slide_image={
            R.drawable.ic_welcomeicon,
            R.drawable.ic_technical_support,
            R.drawable.ic_notebook
    };
    //header array
    public String[] slide_header={

            "Welcome !",
            "Workshop Report",
            "Publication Report"
    };

    //content
    public String[] slide_content= {

            "Information of Workshop And Publication of Computer Department.",
            "Add the attended workshop detail as well view the workshop detail.",
            "Put the Publication detail as well view the all Publication detail."
    };


    @Override
    public int getCount() {
        return slide_header.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==(RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.slider_layout, container,false);

        ImageView imageView=(ImageView)view.findViewById(R.id.id_slide_image);
        TextView textView=(TextView)view.findViewById(R.id.id_title_txt);
        TextView textView1=(TextView)view.findViewById(R.id.id_desc_txt);   //set contact with layout


        imageView.setImageResource(slide_image[position]);
        textView.setText(slide_header[position]);
        textView1.setText(slide_content[position]);


        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
