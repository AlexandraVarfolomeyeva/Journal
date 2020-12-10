package com.example.journal;

import android.content.ContentValues;
import android.database.Cursor;

interface IRepository {
     Cursor getAllStudents();
     Cursor GetStudentById(int id);
     long CreateStudent(ContentValues cv);
     long UpdateStudent(ContentValues cv, int Id);

     Cursor getLessons(int groupId);
     long CreateLesson(ContentValues cv);

     long CreateSTLESS(ContentValues stless);
     Cursor getStLessById(int id);
      long updateStLess(ContentValues cv, int Id);

     Cursor getAllGroups();
     long CreateGroup(ContentValues groupcv);

     long CreateSubject(ContentValues cv);
     Cursor getSubject(int subjectId);

     long CreateGroupSubject(ContentValues cv);

      long CreateUniversity(ContentValues cv);

     long CreateUser(ContentValues cv);
}
