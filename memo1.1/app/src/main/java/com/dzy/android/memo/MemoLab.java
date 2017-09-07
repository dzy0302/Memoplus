package com.dzy.android.memo;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.dzy.android.memo.MemoDbSchema.CrimeTable;


/**
 * Created by dengz on 2016/12/3.
 */

public class MemoLab {
    private static MemoLab sMemoLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    public static MemoLab get(Context context)
    {
        if (sMemoLab ==null)
        {
            sMemoLab =new MemoLab(context);
        }
        return sMemoLab;
    }
    private MemoLab(Context context)
    {
        mContext=context.getApplicationContext();
        mDatabase=new MemoBaseHelper(mContext).getWritableDatabase();

    }
    public void addCrime(Memo c)
    {
        ContentValues values=getContentValues(c);
        mDatabase.insert(CrimeTable.NAME,null,values);

    }
    public void deleteCrime(UUID crimeId)
    {
        String uuidString = crimeId.toString();
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + " = ?", new String[] {uuidString});
    }
    public List<Memo>getCrimes()
    {
       List<Memo> memos =new ArrayList<>();
        MemoCursorWrapper cursor=queryCrimes(null,null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                memos.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return memos;
    }
    public Memo getCrime(UUID id)
    {

        MemoCursorWrapper cursor=queryCrimes(CrimeTable.Cols.UUID+"=?",new String[]
                {
                        id.toString()
                });
        try {
            if (cursor.getCount()==0)
            {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        }finally {
            cursor.close();
        }
    }
    public File getPhotoFile(Memo memo) {
        File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir == null) {
            return null;
        }
        return new File(externalFilesDir, memo.getPhotoFilename());
    }
    public void updateCrime(Memo memo)
    {
        String uuidString = memo.getId().toString();
        ContentValues values = getContentValues(memo);
        mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private static ContentValues getContentValues(Memo memo) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, memo.getId().toString());
        values.put(CrimeTable.Cols.TITLE, memo.getTitle());
        values.put(CrimeTable.Cols.DATE, memo.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, memo.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, memo.getSuspect());
        return values;
    }
    private MemoCursorWrapper queryCrimes(String whereClause, String[] whereArgs)
    {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new MemoCursorWrapper(cursor);
    }
}
