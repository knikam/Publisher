package com.swami.kalpesh.publisher.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.swami.kalpesh.publisher.Fragment.Attend_listFragment;
import com.swami.kalpesh.publisher.Fragment.Non_Attend_listFragment;
import com.swami.kalpesh.publisher.Fragment.Report_ChartFragment;

public class AdminReportPageAdapter extends FragmentPagerAdapter {

    public AdminReportPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
       if(position==0)
       {
            return new Report_ChartFragment();
       }
       else if(position==1)
       {
           return new Attend_listFragment();
       }
       else
       {
           return new Non_Attend_listFragment();
       }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
