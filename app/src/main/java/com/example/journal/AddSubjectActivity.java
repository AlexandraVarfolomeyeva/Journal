package com.example.journal;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.journal.ui.login.LoginViewModel;
import com.example.journal.ui.login.LoginViewModelFactory;

public class AddSubjectActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        final int UserId= loginViewModel.getUserId();


        final EditText SubjectName = findViewById(R.id.SubjectName);
        final Button addbtn = findViewById(R.id.addbtn);

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
                addbtn.setEnabled(SubjectName.getText().toString() != "");
            }
        };
        SubjectName.addTextChangedListener(afterTextChangedListener);
        databaseHelper = new DatabaseHelper(this);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = databaseHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.COLUMN_NAME, SubjectName.getText().toString());
                cv.put(DatabaseHelper.COLUMN_IDTEACHER, UserId);
                db.insert(DatabaseHelper.SUBJECT, null, cv);
                db.close();
               // Intent intent = new Intent(AddSubjectActivity.this, MainActivity.class);
               // startActivity(intent);
                //Complete and destroy activity once successful
                finish();
            }
        });
    }


}
