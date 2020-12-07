package com.example.journal.data;

import android.content.Context;
import android.database.Cursor;

import com.example.journal.UserRepo;
import com.example.journal.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */

public class LoginDataSource {
    UserRepo ur;
    LoggedInUser User;
    public Result<LoggedInUser> login(String username, String password, Context cx) {

        try {
            ur= new UserRepo(cx);
            //получаем данные из бд в виде курсора
            Cursor userCursor =  ur.getUser(username, password);
            // определяем, какие столбцы из курсора будут выводиться в ListView
                    if (userCursor.getCount()>0){
                        userCursor.moveToFirst();
                        int id = Integer.parseInt(userCursor.getString(0));
                        String FIO = userCursor.getString(1);
                        String group = userCursor.getString(2);
                        int idGroup;
                        if (group==null) {
                            idGroup=-1;
                        } else
                        {
                            idGroup = Integer.parseInt(userCursor.getString(2));
                        }
                        int idRole = Integer.parseInt(userCursor.getString(3));
                        int idUni = Integer.parseInt(userCursor.getString(4));
                        String Username = userCursor.getString(5);
                        String email = userCursor.getString(6);
                        String Password = userCursor.getString(7);
                        User = new LoggedInUser(id, FIO,idGroup,idRole,idUni,Username,email,Password);
                        System.out.println("user "+User);
                        return new Result.Success<>(User);
                    } else {
                             return new Result.Error(new IOException("Error logging in"));
                    }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
