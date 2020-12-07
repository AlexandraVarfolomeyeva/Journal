package com.example.journal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserRepo {
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

   public UserRepo(Context cx){
       databaseHelper = new DatabaseHelper(cx);
       db = databaseHelper.getReadableDatabase();

   }

   public Cursor getUser(String username, String password){
       Cursor userCursor =  db.rawQuery("select * from "+ DatabaseHelper.USER
               + " where "+DatabaseHelper.COLUMN_USERNAME +"='"+username +"' AND "+ DatabaseHelper.COLUMN_PASSWORD +"='"+password+"'", null);
       if (userCursor.getCount()==0) {
           userCursor =  db.rawQuery("select * from "+ DatabaseHelper.USER
                   + " where "+DatabaseHelper.COLUMN_EMAIL +"='"+username +"' AND "+ DatabaseHelper.COLUMN_PASSWORD +"='"+password+"'", null);
       }
        return userCursor;
   }
}
