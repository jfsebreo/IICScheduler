package com.utilities;

import android.provider.BaseColumns;

/**
 * Created by Jace on 10/14/2016.
 */
public class UserContract {

    private UserContract(){

    }

    public static class UserDetails implements BaseColumns {

        public static final String TABLE_NAME = "USERS";
        public static final String EMAIL_COLUMN = "EMAIL";
        public static final String PASSWORD_COLUMN = "PASSWORD";
        public static final String NAME_COLUMN = "NAME";
        public static final String POSITION_COLUMN = "POSITION";
        public static final String DEPTSECT_COLUMN = "DEPT";
    }
}
