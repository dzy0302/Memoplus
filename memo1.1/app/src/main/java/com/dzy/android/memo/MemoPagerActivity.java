package com.dzy.android.memo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Created by dengz on 2016/12/12.
 */

public class MemoPagerActivity extends AppCompatActivity implements MemoFragment.Callbacks{
    private static final String EXTRA_CRIME_ID="com.bignerdranch.android.criminalintent.crime_id";
    private ViewPager mViewPager;
    private List<Memo> mMemos;
    public static Intent newIntent(Context packageContext, UUID crimeId)
    {
        Intent intent=new Intent(packageContext,MemoPagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,crimeId);
        return intent;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        UUID crimeId=(UUID)getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        mViewPager=(ViewPager)findViewById(R.id.activity_crime_pager_view_paper);
        mMemos = MemoLab.get(this).getCrimes();
        FragmentManager fragmentManager=getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Memo memo = mMemos.get(position);
                return MemoFragment.newInstance(memo.getId());
            }

            @Override
            public int getCount() {
                return mMemos.size();
            }
        });
        for (int i = 0; i< mMemos.size(); i++)
        {
            if (mMemos.get(i).getId().equals(crimeId))
            {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
    public void onCrimeUpdated(Memo memo){

    }

}
