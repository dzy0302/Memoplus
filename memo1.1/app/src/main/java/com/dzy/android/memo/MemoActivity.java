package com.dzy.android.memo;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class MemoActivity extends SingleFragmentActivity {
    private static final String EXTRA_CRIME_ID="com.bignerdranch.android.criminalintent.crime_id";
    public static Intent newIntent(Context packageContext, UUID crimeID)
    {
        Intent intent=new Intent(packageContext,MemoActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,crimeID);
        return intent;
    }
    protected Fragment createFragment()
    {
        UUID crimeId=(UUID)getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return MemoFragment.newInstance(crimeId);
    }


}
