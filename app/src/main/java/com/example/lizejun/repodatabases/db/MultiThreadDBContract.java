package com.example.lizejun.repodatabases.db;


public class MultiThreadDBContract {

    public static final String DATABASE_NAME = "multi.db";
    public static final int DATABASE_VERSION = 1;

    public static class TABLE_KEY_VALUE {
        public static final String TABLE_NAME = "key_value";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_VALUE = "value";
        public static final String CREATE = "create table " + TABLE_NAME + "(_id integer primary key autoincrement, " + COLUMN_KEY + " text, " + COLUMN_VALUE + " text)";
    }
}
