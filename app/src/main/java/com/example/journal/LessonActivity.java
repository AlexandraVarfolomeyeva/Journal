package com.example.journal;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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

    Repository repository;
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
        repository = new Repository(this);
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
               Cursor presenceCursor = repository.getStlessForGroupOnLesson(groupId,(int)lessonId);
              int count = presenceCursor.getCount();
              presenceCursor.moveToPosition(position);
                int SelectedStLessId = Integer.parseInt(presenceCursor.getString(0));
                presenceCursor.close();

                if (SelectedStLessId > 0) {
                    // получаем элемент по id из бд
                    presenceCursor = repository.getStLessById(SelectedStLessId);
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
                    repository.updateStLess(cv,  SelectedStLessId);
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
        Cursor groupCurs =repository.getGroupByName(name);
        groupCurs.moveToFirst();
        int id = Integer.parseInt(groupCurs.getString(0));
        if (id>0)
            return id;
        else return 0;
    }

    private void setgroups(){

        groupsCursor = repository.getGroupsBySubject(subjectId);
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
        studentCursor = repository.getLessonsForSubject(groupId, subjectId, selectedDate);

        if (studentCursor.getCount()>0)
        {
            studentCursor.moveToFirst();
            lessonId = Integer.parseInt(studentCursor.getString(0));
        } else{  //если на выбранный день уже есть пара, то открыть ее
            Toast.makeText(getApplicationContext(), selectedDate, Toast.LENGTH_LONG).show();
            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.COLUMN_DATE, selectedDate);
            cv.put(DatabaseHelper.COLUMN_IDSUBJECT,subjectId);
            lessonId =repository.CreateLesson(cv);
            studentCursor = repository.getStudentsByGroup(groupId);

            while (studentCursor.moveToNext()) {
                ContentValues stless = new ContentValues();
                stless.put(DatabaseHelper.COLUMN_IDLESSON, lessonId);
                stless.put(DatabaseHelper.COLUMN_IDSTUDENT, studentCursor.getString(0));
                stless.put(DatabaseHelper.COLUMN_PRESENCE, true);
                long result = repository.CreateSTLESS(stless);
                // действия
            }
        }
        getStudentCursor();
    }


    public void getStudentCursor() {
        studentCursor =repository.getAllStudentsOntheLesson(groupId, (int)lessonId);

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
        subject.setText("ID subject: " + subjectId+"\n");
        lessonCursor = repository.getSubject(subjectId);
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
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        if (studentCursor != null) studentCursor.close();
        if (lessonCursor!= null) lessonCursor.close();
    }

}
