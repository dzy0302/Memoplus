package com.dzy.android.memo;

/**
 * Created by dengz on 2016/12/15.
 */

public class MemoDbSchema {
    public static final class CrimeTable
    {
        public static final String NAME="crimes";
        public static final class Cols
        {
            public static final String UUID="uuid";
            public static final String TITLE="title";
            public static final String DATE="date";
            public static final String SOLVED="solved";
            public static final String SUSPECT = "suspect";
        }
    }
}
