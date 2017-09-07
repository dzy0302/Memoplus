package com.dzy.android.memo;

import java.util.Date;
import java.util.UUID;

/**
 * Created by dengz on 2016/12/1.
 */

public class Memo {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }
    public String getSuspect()
    {
        return mSuspect;
    }
    public void setSuspect(String suspect)
    {
        mSuspect=suspect;
    }
    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    public Memo()
    {
        this(UUID.randomUUID());


    }
    public Memo(UUID id)
    {
        mId=id;
        mDate=new Date();
    }
    public UUID getId()
    {
        return mId;
    }

}
