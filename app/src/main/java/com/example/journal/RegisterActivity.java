package com.example.journal;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
   Repository repository;

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
        repository = new Repository(this);


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

                if (teacher.isChecked()){
                    cv.put(DatabaseHelper.COLUMN_IDROLE, 1);
                } else{
                    cv.put(DatabaseHelper.COLUMN_IDROLE, 2);
                    long groupid = repository.CreateGroup(groupcv);
                    int groupidint = (int)groupid;
                    cv.put(DatabaseHelper.COLUMN_IDGROUP, groupidint);
                }
               Cursor universitiesCursor = repository.getUniversitiesByName(university.getText().toString());
                if (universitiesCursor.getCount() >0) {
                    universitiesCursor.moveToFirst();
                    cv.put(DatabaseHelper.COLUMN_IDUNIVERSITY,  Integer.parseInt(universitiesCursor.getString(0)));
                } else {
                    ContentValues unicv = new ContentValues();
                    unicv.put(DatabaseHelper.COLUMN_NAME, university.getText().toString());
                    long id =repository.CreateUniversity(unicv);
                    int idint = (int)id;
                    cv.put(DatabaseHelper.COLUMN_IDUNIVERSITY, idint);
                }

                cv.put(DatabaseHelper.COLUMN_FIO, FIO.getText().toString());
                cv.put(DatabaseHelper.COLUMN_USERNAME, username.getText().toString());
                cv.put(DatabaseHelper.COLUMN_EMAIL, email.getText().toString());
                cv.put(DatabaseHelper.COLUMN_PASSWORD, password.getText().toString());


               long userId = repository.CreateUser(cv);
                Toast.makeText(getApplicationContext(), "created user "+String.valueOf(userId), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                //Complete and destroy activity once successful
                finish();
            }
        });

    }
}
