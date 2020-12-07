package com.example.journal;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.journal.ui.login.LoginViewModel;
import com.example.journal.ui.login.LoginViewModelFactory;

public class RegisterActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText FIO = findViewById(R.id.FIO);
        final EditText university = findViewById(R.id.university);
        final EditText username = findViewById(R.id.username);
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);
        final EditText group = findViewById(R.id.group);
        final CheckBox teacher = findViewById(R.id.teacher);

        final Button registerbtn = findViewById(R.id.registerbtn);
        databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean r = FIO.getText().toString() != "" && university.getText().toString() != "" && username.getText().toString() != "" &&
                        email.getText().toString() != "" && password.getText().toString().length()>5 && group.getText().toString() != "";
                registerbtn.setEnabled(r);
            }
        };

        FIO.addTextChangedListener(afterTextChangedListener);
        university.addTextChangedListener(afterTextChangedListener);
        username.addTextChangedListener(afterTextChangedListener);
        email.addTextChangedListener(afterTextChangedListener);
        password.addTextChangedListener(afterTextChangedListener);
        group.addTextChangedListener(afterTextChangedListener);


        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                ContentValues groupcv = new ContentValues();
                groupcv.put(DatabaseHelper.COLUMN_NAME, group.getText().toString());
                long groupid = db.insert(DatabaseHelper.GROUP, null, groupcv);
                if (teacher.isChecked()){
                    cv.put(DatabaseHelper.COLUMN_IDROLE, 1);
                } else{
                    cv.put(DatabaseHelper.COLUMN_IDROLE, 2);
                    int groupidint = (int)groupid;
                    cv.put(DatabaseHelper.COLUMN_IDGROUP, groupidint);
                }
               Cursor universitiesCursor = db.rawQuery("select * from " + DatabaseHelper.UNIVERSITY +
                       " where " +DatabaseHelper.COLUMN_NAME + "='"+university.getText().toString()+"'", null);
                if (universitiesCursor.getCount() >0) {
                    universitiesCursor.moveToFirst();
                    cv.put(DatabaseHelper.COLUMN_IDUNIVERSITY,  Integer.parseInt(universitiesCursor.getString(0)));
                } else {
                    ContentValues unicv = new ContentValues();
                    unicv.put(DatabaseHelper.COLUMN_NAME, university.getText().toString());
                    long id = db.insert(DatabaseHelper.UNIVERSITY, null, unicv);
                    int idint = (int)id;
                    cv.put(DatabaseHelper.COLUMN_IDUNIVERSITY, idint);
                }

                cv.put(DatabaseHelper.COLUMN_FIO, FIO.getText().toString());
                cv.put(DatabaseHelper.COLUMN_USERNAME, username.getText().toString());
                cv.put(DatabaseHelper.COLUMN_EMAIL, email.getText().toString());
                cv.put(DatabaseHelper.COLUMN_PASSWORD, password.getText().toString());


               long userId =  db.insert(DatabaseHelper.USER, null, cv);
                Toast.makeText(getApplicationContext(), "created user "+String.valueOf(userId), Toast.LENGTH_LONG).show();
                db.close();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                //Complete and destroy activity once successful
                finish();
            }
        });

    }
}
