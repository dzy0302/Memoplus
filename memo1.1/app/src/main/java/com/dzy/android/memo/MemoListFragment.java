package com.dzy.android.memo;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;


import java.util.List;

/**
 * Created by dengz on 2016/12/6.
 */

public class MemoListFragment extends Fragment {
    private static final String SAVED_SUBTITLE_VISIBLE="subtitle";
    private RecyclerView mCrimeRecyclerView;
    private  CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks;
//    int position;
    public interface Callbacks
    {
        void onCrimeSelected(Memo memo);
    }
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mCallbacks=(Callbacks) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_crime_list,container,false);


        mCrimeRecyclerView=(RecyclerView)view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (savedInstanceState!=null)
        {
            mSubtitleVisible=savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        updateUI();
        return view;
    }
    public void onResume()
    {
        super.onResume();
        updateUI();
    }
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);
    }
    public void onDetach()
    {
        super.onDetach();
        mCallbacks=null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);
        MenuItem subtitleItem=menu.findItem(R.id.menu_item_show_subtitle);
        MenuItem notificationItem=menu.findItem(R.id.alwaysinbar);
        if (mSubtitleVisible)
        {
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else
        {
            subtitleItem.setTitle(R.string.show_subtitle);
        }

    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_item_new_crime:
                Memo memo =new Memo();
                MemoLab.get(getActivity()).addCrime(memo);
                updateUI();
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible=!mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            case R.id.about:
                Intent intent = new Intent(getActivity(), About.class);
                startActivity(intent);
                return true;
            case R.id.alwaysinbar:
                Intent intent1=new Intent(getActivity(),MemoListActivity.class);
                PendingIntent pi =PendingIntent.getActivity(getActivity(), 0,intent1,0);
                NotificationManager manager=(NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification=new NotificationCompat.Builder(getActivity())
                        .setContentTitle("Memoplus Service")
                        .setContentText("点击打开备忘录")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.memoic)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.memoplus))
                        .setContentIntent(pi)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(),R.drawable.barbar)))
                        .build();
                notification.flags|=Notification.FLAG_ONGOING_EVENT;
                notification.flags|=Notification.FLAG_NO_CLEAR;
                manager.notify(1,notification);

                Intent intent2 = new Intent(Intent.ACTION_MAIN);
                intent2.addCategory(Intent.CATEGORY_HOME);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
               return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void updateSubtitle()
    {
        MemoLab memoLab = MemoLab.get(getActivity());
        int crimeCount= memoLab.getCrimes().size();
        String subtitle=getString(R.string.subtitle_format,crimeCount);
        if (!mSubtitleVisible)
        {
            subtitle=null;
        }
        AppCompatActivity activity=(AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }


    public void updateUI()
    {
        MemoLab memoLab = MemoLab.get(getActivity());
        List<Memo> memos = memoLab.getCrimes();
        if (mAdapter==null)
        {
            mAdapter=new CrimeAdapter(memos);
            mCrimeRecyclerView.setAdapter(mAdapter);
            mCrimeRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                    DividerItemDecoration.VERTICAL_LIST));//ITEM添加下划线
//            mCrimeRecyclerView.setItemAnimator(new DefaultItemAnimator());无效，待解决
        }
       else
        {
            mAdapter.setMemos(memos);
//            mAdapter.notifyItemChanged(position);
            mAdapter.notifyDataSetChanged();///,,k
        }
        updateSubtitle();
    }
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private Memo mMemo;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;
        public CrimeHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView=(TextView)itemView.findViewById(R.id.List_item_crime_title_text_view);
            mDateTextView=(TextView)itemView.findViewById(R.id.List_item_crime_date_text_view);
            mSolvedCheckBox=(CheckBox)itemView.findViewById(R.id.List_item_crime_solved_check_box);
        }
        public void bindCrime(Memo memo)
        {
            mMemo = memo;
            mTitleTextView.setText(mMemo.getTitle());
            mDateTextView.setText(mMemo.getDate().toString());
            mSolvedCheckBox.setChecked(mMemo.isSolved());
        }

        @Override
        public void onClick(View v) {
//          position = mCrimeRecyclerView.getChildAdapterPosition(v); //将获取到的位置赋值给之前定义的变量
          mCallbacks.onCrimeSelected(mMemo);
        }
    }
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>
    {
        private List<Memo> mMemos;
        public CrimeAdapter(List<Memo> memos)
        {
            mMemos = memos;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            View view=layoutInflater.inflate(R.layout.list_item_crime,parent,false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Memo memo = mMemos.get(position);
            holder.bindCrime(memo);
        }
        public int getItemCount()
        {
            return mMemos.size();
        }
        public void setMemos(List<Memo> memos)
        {
            mMemos = memos;
        }
    }


}
