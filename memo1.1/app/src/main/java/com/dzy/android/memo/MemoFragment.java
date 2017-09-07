package com.dzy.android.memo;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.UUID;


/**
 * Created by dengz on 2016/12/1.
 */

public class MemoFragment extends Fragment {
    private static final String ARG_CRIME_ID="crime_id";
    private static final String DIALOG_DATE="DialogDate";
    private static final int REQUEST_DATE=0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO=2;
    private Memo mMemo;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mSuspectButton;
    private Button mReportButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private Callbacks mCallbacks;
//    private SwipeRefreshLayout swipeRefresh;
    public interface  Callbacks{
        void onCrimeUpdated(Memo memo);
    }

    public static MemoFragment newInstance(UUID crimeId)
    {
        Bundle args=new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);
        MemoFragment fragment=new MemoFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mCallbacks=(Callbacks)activity;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_crime:
                AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
                dialog.setTitle("注意！");
                dialog.setMessage("是否删除此备忘录？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UUID crimeId = mMemo.getId();
                MemoLab.get(getActivity()).deleteCrime(crimeId);
                Toast.makeText(getActivity(), R.string.toast_delete_crime, Toast.LENGTH_SHORT).show();
                getActivity().finish();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID crimeId=(UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mMemo = MemoLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile= MemoLab.get(getActivity()).getPhotoFile(mMemo);
    }
    public void onPause() {
        super.onPause();
        MemoLab.get(getActivity())
                .updateCrime(mMemo);
    }
   public void  onDetach(){
       super.onDetach();
       mCallbacks=null;
   }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_crime,container,false);

//        swipeRefresh=(SwipeRefreshLayout)v.findViewById(R.id.swipe_refresh);
//        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
//        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refreshlist();
//            }
//        });

        mTitleField=(EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mMemo.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mMemo.setTitle(s.toString());
                updateCrime();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDateButton=(Button)v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager=getFragmentManager();
                DatePickerFragment dialog=DatePickerFragment.newInstance(mMemo.getDate());
                dialog.setTargetFragment(MemoFragment.this,REQUEST_DATE);
                dialog.show(manager,DIALOG_DATE);
            }
        });
        mSolvedCheckBox=(CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mMemo.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mMemo.setSolved(isChecked);
                updateCrime();

            }
        });
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                i=Intent.createChooser(i,getString(R.string.send_report));
                startActivity(i);
            }
        });
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mMemo.getSuspect() != null) {
            mSuspectButton.setText(mMemo.getSuspect());
        }
        PackageManager packageManager=getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY)==null)
        {
            mSuspectButton.setEnabled(false);
        }
        mPhotoButton=(ImageButton)v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });


        mPhotoView=(ImageView)v.findViewById(R.id.crime_photo);
        updatePhotoView();
        return v;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode!= Activity.RESULT_OK)
        {
            return;
        }
        if (requestCode==REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mMemo.setDate(date);
            updateCrime();
            updateDate();
        }
        else if (requestCode==REQUEST_CONTACT && data != null)
        {
            Uri contactUri=data.getData();
            String[] queryFields=new String[]
                    {
                            ContactsContract.Contacts.DISPLAY_NAME
                    };
            Cursor c=getActivity().getContentResolver().query(contactUri,queryFields,null,null,null);
            try
            {
                if(c.getCount()==0)
                {
                    return;
                }
                c.moveToFirst();
                String suspect=c.getString(0);
                mMemo.setSuspect(suspect);
                updateCrime();
                mSuspectButton.setText(suspect);
            }
            finally {
                c.close();
            }
        }
        else if (requestCode==REQUEST_PHOTO)
        {
            updateCrime();
            updatePhotoView();
        }
    }
    private void updateCrime(){
        MemoLab.get(getActivity()).updateCrime(mMemo);
        mCallbacks.onCrimeUpdated(mMemo);
    }

    private void updateDate() {
        mDateButton.setText(mMemo.getDate().toString());
    }
    private String getCrimeReport()
    {
        String solvedString=null;
        if(mMemo.isSolved())
        {
            solvedString=getString(R.string.crime_report_solved);
        }else
        {
            solvedString=getString(R.string.crime_report_unsolved);
        }
        String dateFormat="EEE,MMM dd";
        String dateString = DateFormat.format(dateFormat, mMemo.getDate()).toString();
        String suspect= mMemo.getSuspect();
        if (suspect==null)
        {
            suspect=getString(R.string.crime_report_no_suspect);
        }
        else
        {
            suspect=getString(R.string.crime_report_suspect,suspect);
        }
        String report=getString(R.string.crime_report, mMemo.getTitle(),dateString,solvedString,suspect);
        return report;
    }
    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
//    private  void refreshlist(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                getActivity().runOnUiThread(new Runnable(){
//                    @Override
//                    public void run() {
//                        swipeRefresh.setRefreshing(false);
//                    }
//                });
//            }
//        }).start();
//    }
}
