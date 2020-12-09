package com.example.journal;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;

public class LessonActivity extends AppCompatActivity {

    TextView subject, teacher, console;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor studentCursor;
    Cursor groupsCursor;
    Cursor lessonCursor;
    ListView studentList;
    SimpleCursorAdapter studentAdapter;
    ProgressBar loadingStudents;

    Spinner groups;


    //SimpleCursorAdapter userAdapter;
    int subjectId=0, groupId=-1, userId=0;
    boolean isTeacher = false;
    String item="";
    long lessonId=-1;
    String selectedDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        databaseHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            subjectId = extras.getInt("id");
            groupId = extras.getInt("groupid");
            userId = extras.getInt("userid");
        }
        if (groupId == -1){
            isTeacher = true;
        }
        subject = (TextView)findViewById(R.id.subject);
        teacher = (TextView)findViewById(R.id.teacher);
        studentList = (ListView)findViewById(R.id.studentlist);
        loadingStudents = (ProgressBar)findViewById(R.id.prgLoading);
        groups = (Spinner)findViewById(R.id.groups);

        groups.setPrompt("Группа");

        FloatingActionButton addStudent =  findViewById(R.id.addstudentbtn);
        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LessonActivity.this, AddStudent.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("groupid", groupId);
                intent.putExtra("userid", userId);
                startActivity(intent);
            }
        });


            groups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    groupsCursor.moveToPosition(position);

                    String f = groupsCursor.getString(1);
                    //Toast.makeText(getApplicationContext(), f, Toast.LENGTH_LONG).show();
                    groupId = getGroupId(f);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        studentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Cursor presenceCursor =  db.rawQuery("select "+DatabaseHelper.STLESS +"."+DatabaseHelper.COLUMN_ID +" from "+
                        DatabaseHelper.STUDENT + " inner join " +DatabaseHelper.STLESS+" on "+
                        DatabaseHelper.STUDENT +"."+DatabaseHelper.COLUMN_ID + "="+DatabaseHelper.STLESS +"."+DatabaseHelper.COLUMN_IDSTUDENT +
                        " inner join " +DatabaseHelper.LESSON+" on "+
                        DatabaseHelper.LESSON +"."+DatabaseHelper.COLUMN_ID + "="+DatabaseHelper.STLESS +"."+DatabaseHelper.COLUMN_IDLESSON +
                        " where " + DatabaseHelper.STUDENT +"."+DatabaseHelper.COLUMN_IDGROUP +"="+ groupId +
                        " and " + DatabaseHelper.STLESS +"."+DatabaseHelper.COLUMN_IDLESSON +"="+ lessonId , null);
              int count = presenceCursor.getCount();
              presenceCursor.moveToPosition(position);
                int SelectedStLessId = Integer.parseInt(presenceCursor.getString(0));
                presenceCursor.close();

                if (SelectedStLessId > 0) {
                    // получаем элемент по id из бд
                    presenceCursor = db.rawQuery("select * from " + DatabaseHelper.STLESS + " where " +
                            DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(SelectedStLessId)});
                    presenceCursor.moveToFirst();
                    ContentValues cv = new ContentValues();
                    int idlesson = Integer.parseInt(presenceCursor.getString(1));
                    int idstudent = Integer.parseInt(presenceCursor.getString(2));
                    String markstr = presenceCursor.getString(3);
                    if (markstr!=null)
                    {int mark = Integer.parseInt(presenceCursor.getString(3));
                        cv.put(DatabaseHelper.COLUMN_MARK, mark);
                    }
                    int presence = Integer.parseInt(presenceCursor.getString(4));
                    presenceCursor.close();
                    if (presence == 1) {
                        presence=0;
                    } else {
                        presence=1;
                    }
                    cv.put(DatabaseHelper.COLUMN_IDLESSON, idlesson);
                    cv.put(DatabaseHelper.COLUMN_IDSTUDENT, idstudent);

                    cv.put(DatabaseHelper.COLUMN_PRESENCE, presence);
                    db.update(DatabaseHelper.STLESS, cv, DatabaseHelper.COLUMN_ID + "=" + SelectedStLessId, null);
                    getStudentCursor();
                }
            }
        });



        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {
                loadingStudents.setVisibility(View.VISIBLE);
                int mYear = year;
                int mMonth = month;
                int mDay = dayOfMonth;
                selectedDate = new StringBuilder().append(mMonth + 1).append("/").append(mDay).append("/").append(mYear).toString();
                getlesson(selectedDate);

            }
        });
    }

    private int getGroupId(String name){
        Cursor groupCurs =  db.rawQuery("select * from "+ DatabaseHelper.GROUP+ " where " +DatabaseHelper.COLUMN_NAME+"='"+name+"'", null);
        groupCurs.moveToFirst();
        int id = Integer.parseInt(groupCurs.getString(0));
        if (id>0)
            return id;
        else return 0;
    }

    private void setgroups(){

        groupsCursor =  db.rawQuery("select "+  DatabaseHelper.GROUP +"."+DatabaseHelper.COLUMN_ID +","+DatabaseHelper.GROUP +"."+DatabaseHelper.COLUMN_NAME
                       +" from "+ DatabaseHelper.GROUP+ " inner join " +DatabaseHelper.GRSUB+" on "+
               DatabaseHelper.GROUP +"."+DatabaseHelper.COLUMN_ID + "="+DatabaseHelper.GRSUB +"."+DatabaseHelper.COLUMN_IDGROUP +
               " inner join " +DatabaseHelper.SUBJECT +" on "+
               DatabaseHelper.SUBJECT +"."+DatabaseHelper.COLUMN_ID + "="+DatabaseHelper.GRSUB +"."+DatabaseHelper.COLUMN_IDSUBJECT +
               " where " +DatabaseHelper.SUBJECT +"."+DatabaseHelper.COLUMN_ID + " =" +subjectId , null);
        if (groupsCursor.getCount()>0) {
            String[] headers = new String[] {DatabaseHelper.COLUMN_NAME};
            SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, groupsCursor, headers, new int[]{android.R.id.text1});
            mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            groups.setVisibility(View.VISIBLE);
            groups.setAdapter(mAdapter);
        }
        else {
            Toast.makeText(getApplicationContext(), "Возникла ошибка!", Toast.LENGTH_LONG).show();
            groups.setVisibility(View.GONE);
        }
    }


    public void getlesson(String selectedDate){
        studentCursor =  db.rawQuery("select "+ DatabaseHelper.LESSON +"."+DatabaseHelper.COLUMN_ID
                        +" from "+ DatabaseHelper.LESSON + " inner join " +DatabaseHelper.STLESS+" on "+
                        DatabaseHelper.LESSON +"."+DatabaseHelper.COLUMN_ID + "="+DatabaseHelper.STLESS +"."+DatabaseHelper.COLUMN_IDLESSON +
                        " inner join " +DatabaseHelper.STUDENT +" on "+
                        DatabaseHelper.STUDENT +"."+DatabaseHelper.COLUMN_ID + "="+DatabaseHelper.STLESS +"."+DatabaseHelper.COLUMN_IDSTUDENT +
                        " where " + DatabaseHelper.STUDENT +"."+DatabaseHelper.COLUMN_IDGROUP +"="+ groupId + " and "+
                        DatabaseHelper.LESSON +"."+DatabaseHelper.COLUMN_DATE + " ='"+ selectedDate +"'"+ " and "+
                        DatabaseHelper.LESSON +"."+DatabaseHelper.COLUMN_IDSUBJECT + " =" +subjectId
                , null);

        if (studentCursor.getCount()>0)
        {
            studentCursor.moveToFirst();
            lessonId = Integer.parseInt(studentCursor.getString(0));
        } else{  //если на выбранный день уже есть пара, то открыть ее
            Toast.makeText(getApplicationContext(), selectedDate, Toast.LENGTH_LONG).show();
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COLUMN_DATE, selectedDate);
            cv.put(DatabaseHelper.COLUMN_IDSUBJECT,subjectId);
            lessonId = db.insert(DatabaseHelper.LESSON, null, cv);
            studentCursor =  db.rawQuery("select * from "+ DatabaseHelper.STUDENT +
                    " where " + DatabaseHelper.COLUMN_IDGROUP +"="+ groupId, null);

            while (studentCursor.moveToNext()) {
                ContentValues stless = new ContentValues();
                stless.put(DatabaseHelper.COLUMN_IDLESSON, lessonId);
                stless.put(DatabaseHelper.COLUMN_IDSTUDENT, studentCursor.getString(0));
                stless.put(DatabaseHelper.COLUMN_PRESENCE, true);
                long result = db.insert(DatabaseHelper.STLESS, null, stless);
                // действия
            }
        }
        getStudentCursor();
    }


    public void getStudentCursor() {
        studentCursor =  db.rawQuery("select * from "+
                DatabaseHelper.STUDENT + " inner join " +DatabaseHelper.STLESS+" on "+
                DatabaseHelper.STUDENT +"."+DatabaseHelper.COLUMN_ID + "="+DatabaseHelper.STLESS +"."+DatabaseHelper.COLUMN_IDSTUDENT +
                " inner join " +DatabaseHelper.LESSON+" on "+
                DatabaseHelper.LESSON +"."+DatabaseHelper.COLUMN_ID + "="+DatabaseHelper.STLESS +"."+DatabaseHelper.COLUMN_IDLESSON +
                " where " + DatabaseHelper.STUDENT +"."+DatabaseHelper.COLUMN_IDGROUP +"="+ groupId +
                " and " + DatabaseHelper.STLESS +"."+DatabaseHelper.COLUMN_IDLESSON +"="+ lessonId , null);

        if (studentCursor.getCount()>0) {
            loadingStudents.setVisibility(View.GONE);
        }
        else {
            Toast.makeText(getApplicationContext(), "Возникла ошибка!", Toast.LENGTH_LONG).show();
            loadingStudents.setVisibility(View.GONE);
        }
        // определяем, какие столбцы из курсора будут выводиться в ListView


        String[] headers = new String[] {DatabaseHelper.COLUMN_FIO, DatabaseHelper.COLUMN_PRESENCE};
        // создаем адаптер, передаем в него курсор
        studentAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                studentCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        studentList.setAdapter(studentAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // открываем подключение
        db = databaseHelper.getWritableDatabase();
        subject.setText("ID subject: " + subjectId+"\n");
        lessonCursor =  db.rawQuery("select "+ DatabaseHelper.SUBJECT+"."+DatabaseHelper.COLUMN_NAME +","+ DatabaseHelper.USER+"."+DatabaseHelper.COLUMN_FIO+
                " from "+ DatabaseHelper.SUBJECT +" inner join " +DatabaseHelper.USER +
                        " on "+DatabaseHelper.SUBJECT+"."+DatabaseHelper.COLUMN_IDTEACHER+"="+DatabaseHelper.USER+"."+ DatabaseHelper.COLUMN_ID +" where " +
                DatabaseHelper.SUBJECT+"."+DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(subjectId)});
        lessonCursor.moveToFirst();
     if  (lessonCursor.getCount()>0)  {
         subject.setText(lessonCursor.getString(0));
         teacher.setText(lessonCursor.getString(1));
         getSupportActionBar().setTitle(lessonCursor.getString(0));
     }
        lessonCursor.close();
        Date currentTime = Calendar.getInstance().getTime();
        int month = currentTime.getMonth();
        int date = currentTime.getDate();
        int year = currentTime.getYear();
        selectedDate = new StringBuilder().append(month+1).append("/").append(date).append("/").append(year).toString();

        if (isTeacher)
        {
            setgroups();
        } else {
            groups.setVisibility(View.GONE);
        }

      //  getlesson(selectedDate);
        //Date date = new Date();
       // db.delete(DatabaseHelper.LESSON, null, null);
        //db.delete(DatabaseHelper.STLESS, null, null);

        //получаем данные из бд в виде курсора
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        if (studentCursor != null) studentCursor.close();
        if (lessonCursor!= null) lessonCursor.close();
    }

}
