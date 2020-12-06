package com.example.journal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class LessonActivity extends AppCompatActivity {

    TextView subject, teacher, console;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor studentCursor;
    ListView studentList;
    SimpleCursorAdapter studentAdapter;
    ProgressBar loadingStudents;
    Cursor lessonCursor;
    //SimpleCursorAdapter userAdapter;
    long subjectId=0, groupId=1;
    String item="";
    long lessonId=-1;
    String selectedDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        subject = (TextView)findViewById(R.id.subject);
        teacher = (TextView)findViewById(R.id.teacher);
        console = (TextView)findViewById(R.id.console);
        studentList = (ListView)findViewById(R.id.studentlist);
        loadingStudents = (ProgressBar)findViewById(R.id.prgLoading);

        databaseHelper = new DatabaseHelper(this);


        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {
                int mYear = year;
                int mMonth = month;
                int mDay = dayOfMonth;
                selectedDate = new StringBuilder().append(mMonth + 1)
                        .append("-").append(mDay).append("/").append(mYear)
                        .append(" ").toString();
                view.setBackgroundColor(Color.parseColor("#00BCD4"));
                Toast.makeText(getApplicationContext(), selectedDate, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        // открываем подключение
        db = databaseHelper.getWritableDatabase();
       Bundle extras = getIntent().getExtras();
        if (extras != null) {
            subjectId = extras.getLong("id");
        }
        subject.setText("ID subject: " + subjectId+"\n");
        lessonCursor =  db.rawQuery("select "+ DatabaseHelper.SUBJECT+"."+DatabaseHelper.COLUMN_NAME +","+ DatabaseHelper.USER+"."+DatabaseHelper.COLUMN_FIO+
                " from "+ DatabaseHelper.SUBJECT +" inner join " +DatabaseHelper.USER +
                        " on "+DatabaseHelper.SUBJECT+"."+DatabaseHelper.COLUMN_IDTEACHER+"="+DatabaseHelper.USER+"."+ DatabaseHelper.COLUMN_ID +" where " +
                DatabaseHelper.SUBJECT+"."+DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(subjectId)});
        lessonCursor.moveToFirst();
     if  (lessonCursor.getCount()>0)  {
         subject.setText(lessonCursor.getString(0));
         teacher.setText(lessonCursor.getString(1));
     }
        lessonCursor.close();
        Date date = new Date();
        ContentValues cv = new ContentValues();

        db.delete(DatabaseHelper.LESSON, null, null);
        db.delete(DatabaseHelper.STLESS, null, null);

        cv.put(DatabaseHelper.COLUMN_DATE, date.toString());
        cv.put(DatabaseHelper.COLUMN_IDSUBJECT,subjectId);
        //lessonId = db.insert(DatabaseHelper.LESSON, null, cv);
        if (lessonId>0){
            console.append("Console: result: "+ lessonId+"\n date:" + date.toString());
            // действия
        }
        //получаем данные из бд в виде курсора
        studentCursor =  db.rawQuery("select * from "+ DatabaseHelper.STUDENT + " where " + DatabaseHelper.COLUMN_IDGROUP +"="+ groupId, null);

        if (studentCursor.getCount()>0) {
            loadingStudents.setVisibility(View.GONE);
        }
        else {
            Toast.makeText(getApplicationContext(), "Возникла ошибка!", Toast.LENGTH_LONG).show();
            loadingStudents.setVisibility(View.GONE);
        }
        // определяем, какие столбцы из курсора будут выводиться в ListView
     /*   while (studentCursor.moveToNext()) {
            ContentValues stless = new ContentValues();
            stless.put(DatabaseHelper.COLUMN_IDLESSON, lessonId);
            stless.put(DatabaseHelper.COLUMN_IDSTUDENT, studentCursor.getString(0));
            stless.put(DatabaseHelper.COLUMN_PRESENCE, true);
            long result = db.insert(DatabaseHelper.STLESS, null, stless);
            console.append("Console: result stless: "+ result);
            // действия
        }*/

        String[] headers = new String[] {DatabaseHelper.COLUMN_FIO, DatabaseHelper.COLUMN_IDGROUP};
        // создаем адаптер, передаем в него курсор
        studentAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                studentCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        studentList.setAdapter(studentAdapter);

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        lessonCursor.close();
        studentCursor.close();
    }

}
