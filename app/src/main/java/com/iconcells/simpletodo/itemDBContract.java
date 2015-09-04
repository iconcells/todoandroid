package com.iconcells.simpletodo;

import android.provider.BaseColumns;

/**
 * Contract class to implement base column interface so we can change in
 * Created by kng2 on 9/1/15.
 */
public final class itemDBContract {

    // Prevent someone from accidentally instantiating the contract class
    public itemDBContract(){}

    public static abstract class ItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "todoitems";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_TITLE = "name";
        public static final String COLUMN_NAME_DESC = "desc";
    }
}
