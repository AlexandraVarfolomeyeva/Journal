package com.example.journal;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class AddStudent extends AppCompatActivity {
    EditText nameBox;
   // EditText yearBox;
  //  Button delButton;
    Button saveButton;

    Repository repository;
    Cursor userCursor;
    int Id=0, groupId=0;
            long userId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        nameBox = (EditText) findViewById(R.id.SubjectName);
        saveButton = (Button) findViewById(R.id.addbtn);

        repository = new Repository(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            groupId = extras.getInt("groupid");
            userId = extras.getLong("userid");
        }
        // если 0, то добавление
        if (Id > 0) {
            // получаем элемент по id из бд
            userCursor = repository.GetStudentById(Id);
            userCursor.moveToFirst();
            nameBox.setText(userCursor.getString(1));
            userCursor.close();
        } else {
            // скрываем кнопку удаления
           // delButton.setVisibility(View.GONE);
        }

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
                saveButton.setEnabled(nameBox.getText().toString() != "");
            }
        };
        nameBox.addTextChangedListener(afterTextChangedListener);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               save(view);
            }
        });

    }

    public void save(View view){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_FIO, nameBox.getText().toString());
        cv.put(DatabaseHelper.COLUMN_IDGROUP, groupId);
        cv.put(DatabaseHelper.COLUMN_ISDELETED, 0);
        if (Id > 0) {
            repository.UpdateStudent(cv,Id);
        } else {
            long stId =repository.CreateStudent(cv);
            Cursor lessons = repository.getLessons(groupId);
            lessons.moveToFirst();
           if (lessons.getCount()>0) {
               int studentId=Integer.parseInt(lessons.getString(1));
            do  {
                if (Integer.parseInt(lessons.getString(1)) == studentId) {
                    ContentValues stless = new ContentValues();
                    stless.put(DatabaseHelper.COLUMN_IDSTUDENT, stId);
                    stless.put(DatabaseHelper.COLUMN_PRESENCE, 0);
                    String lessonId = lessons.getString(0);
                    int lesson = Integer.parseInt(lessonId);
                    stless.put(DatabaseHelper.COLUMN_IDLESSON, lesson);
                    repository.CreateSTLESS(stless);
                }
            } while (lessons.moveToNext());
            }
        }
        goHome();
    }

    public void delete(View view){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_FIO, nameBox.getText().toString());
        cv.put(DatabaseHelper.COLUMN_IDGROUP, groupId);
        cv.put(DatabaseHelper.COLUMN_ISDELETED, 1);
        repository.UpdateStudent(cv,Id);
        goHome();
    }
    private void goHome(){
        // переход к главной activity
        Intent intent = new Intent(this, LessonActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
