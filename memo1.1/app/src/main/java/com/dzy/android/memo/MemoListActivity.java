package com.dzy.android.memo;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by dengz on 2016/12/6.
 */

public class MemoListActivity extends SingleFragmentActivity implements  MemoListFragment.Callbacks,MemoFragment.Callbacks{
    protected Fragment createFragment()
    {
        return new MemoListFragment();
    }
    protected int getLayoutResId()
    {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Memo memo) {
        if (findViewById(R.id.detail_fragment_container)==null)
        {
            Intent intent= MemoPagerActivity.newIntent(this, memo.getId());
            startActivity(intent);
        }else {
            Fragment newDetail= MemoFragment.newInstance(memo.getId());
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container,newDetail).commit();
        }
    }

    @Override
    public void onCrimeUpdated(Memo memo) {
        MemoListFragment listFragment=(MemoListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
