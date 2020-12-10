package com.example.journal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Repository implements IRepository{
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    public Repository(Context cx){
        databaseHelper = new DatabaseHelper(cx);
        db = databaseHelper.getWritableDatabase();
    }

    public Cursor getSubjectsForTeacher(int UserId){
        Cursor userCursor =  db.rawQuery("select "+DatabaseHelper.SUBJECT + "." + DatabaseHelper.COLUMN_ID+",* from " + DatabaseHelper.USER+ " inner join " + DatabaseHelper.SUBJECT +
                " on " + DatabaseHelper.USER + "." + DatabaseHelper.COLUMN_ID + "=" + DatabaseHelper.SUBJECT + "." + DatabaseHelper.COLUMN_IDTEACHER +
                " where "+DatabaseHelper.SUBJECT+"."+DatabaseHelper.COLUMN_IDTEACHER+"="+UserId, null);
        return userCursor;
    }

    public Cursor getSubjectsForStudent(int Group){
        Cursor userCursor =  db.rawQuery("select "+DatabaseHelper.SUBJECT + "." + DatabaseHelper.COLUMN_ID+",* from " + DatabaseHelper.GRSUB
                + " inner join " + DatabaseHelper.SUBJECT +
                " on " + DatabaseHelper.GRSUB + "." + DatabaseHelper.COLUMN_IDSUBJECT + "=" + DatabaseHelper.SUBJECT + "." +DatabaseHelper.COLUMN_ID +
                " inner join " + DatabaseHelper.USER +
                " on " + DatabaseHelper.SUBJECT + "." + DatabaseHelper.COLUMN_IDTEACHER + "=" + DatabaseHelper.USER + "." +DatabaseHelper.COLUMN_ID +
                " where "+DatabaseHelper.GRSUB+"."+DatabaseHelper.COLUMN_IDGROUP+"="+Group, null);
        return userCursor;
    }

    public Cursor getAllStudents() {
        return db.rawQuery("select * from " + DatabaseHelper.STUDENT , null);
    }

    public Cursor GetStudentById(int id){
       return db.rawQuery("select * from " + DatabaseHelper.STUDENT + " where " +
                DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public long UpdateStudent(ContentValues cv, int Id){
        return db.update(DatabaseHelper.STUDENT, cv, DatabaseHelper.COLUMN_ID + "=" + String.valueOf(Id), null);
    }

    public long CreateStudent(ContentValues cv){
        return db.insert(DatabaseHelper.STUDENT, null, cv);
    }
    public Cursor getLessons(int groupId){
      return   db.rawQuery("select "+ DatabaseHelper.LESSON+"."+DatabaseHelper.COLUMN_ID+","+
                DatabaseHelper.STUDENT+"."+DatabaseHelper.COLUMN_ID +
                " from " + DatabaseHelper.LESSON
                +" inner join "+ DatabaseHelper.STLESS + " on "+
                DatabaseHelper.STLESS+"."+DatabaseHelper.COLUMN_IDLESSON +"="+ DatabaseHelper.LESSON+"."+DatabaseHelper.COLUMN_ID
                +" inner join "+ DatabaseHelper.STUDENT + " on "+
                DatabaseHelper.STLESS+"."+DatabaseHelper.COLUMN_IDSTUDENT +"="+ DatabaseHelper.STUDENT+"."+DatabaseHelper.COLUMN_ID
                +" inner join "+ DatabaseHelper.GROUP + " on "+
                DatabaseHelper.STUDENT+"."+DatabaseHelper.COLUMN_IDGROUP +"="+ DatabaseHelper.GROUP+"."+DatabaseHelper.COLUMN_ID
                + " where " + DatabaseHelper.GROUP+"."+DatabaseHelper.COLUMN_ID + "=" + groupId, null);
    }
    public long CreateSTLESS(ContentValues stless){
        return db.insert(DatabaseHelper.STLESS, null, stless);
    }

    public Cursor getAllGroups(){
        return  db.rawQuery("select * from "+ DatabaseHelper.GROUP, null);
    }

    public long CreateSubject(ContentValues cv){
        return db.insert(DatabaseHelper.SUBJECT, null, cv);
    }
    public long CreateGroupSubject(ContentValues cv){
        return db.insert(DatabaseHelper.GRSUB, null, cv);
    }
    public Cursor getStlessForGroupOnLesson(int groupId,int lessonId){
      return   db.rawQuery("select "+DatabaseHelper.STLESS +"."+DatabaseHelper.COLUMN_ID +" from "+
                DatabaseHelper.STUDENT + " inner join " +DatabaseHelper.STLESS+" on "+
                DatabaseHelper.STUDENT +"."+DatabaseHelper.COLUMN_ID + "="+DatabaseHelper.STLESS +"."+DatabaseHelper.COLUMN_IDSTUDENT +
                " inner join " +DatabaseHelper.LESSON+" on "+
                DatabaseHelper.LESSON +"."+DatabaseHelper.COLUMN_ID + "="+DatabaseHelper.STLESS +"."+DatabaseHelper.COLUMN_IDLESSON +
                " where " + DatabaseHelper.STUDENT +"."+DatabaseHelper.COLUMN_IDGROUP +"="+ groupId +
                " and " + DatabaseHelper.STLESS +"."+DatabaseHelper.COLUMN_IDLESSON +"="+ lessonId , null);
    }
    public Cursor getStLessById(int id){
      return  db.rawQuery("select * from " + DatabaseHelper.STLESS + " where " +
                DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }
    public  long updateStLess(ContentValues cv, int Id){
        return db.update(DatabaseHelper.STLESS, cv, DatabaseHelper.COLUMN_ID + "=" + Id, null);
    }
    public Cursor getGroupByName(String name){
       return db.rawQuery("select * from "+ DatabaseHelper.GROUP+ " where " +DatabaseHelper.COLUMN_NAME+"='"+name+"'", null);
    }
    public Cursor getGroupsBySubject(int subjectId){
        return db.rawQuery("select "+  DatabaseHelper.GROUP +"."+DatabaseHelper.COLUMN_ID +","+DatabaseHelper.GROUP +"."+DatabaseHelper.COLUMN_NAME
                +" from "+ DatabaseHelper.GROUP+ " inner join " +DatabaseHelper.GRSUB+" on "+
                DatabaseHelper.GROUP +"."+DatabaseHelper.COLUMN_ID + "="+DatabaseHelper.GRSUB +"."+DatabaseHelper.COLUMN_IDGROUP +
                " inner join " +DatabaseHelper.SUBJECT +" on "+
                DatabaseHelper.SUBJECT +"."+DatabaseHelper.COLUMN_ID + "="+DatabaseHelper.GRSUB +"."+DatabaseHelper.COLUMN_IDSUBJECT +
                " where " +DatabaseHelper.SUBJECT +"."+DatabaseHelper.COLUMN_ID + " =" +subjectId , null);
    }
    public  Cursor getLessonsForSubject(int groupId, int subjectId, String selectedDate){
      return   db.rawQuery("select "+ DatabaseHelper.LESSON +"."+DatabaseHelper.COLUMN_ID
                +" from "+ DatabaseHelper.LESSON + " inner join " +DatabaseHelper.STLESS+" on "+
                DatabaseHelper.LESSON +"."+DatabaseHelper.COLUMN_ID + "="+DatabaseHelper.STLESS +"."+DatabaseHelper.COLUMN_IDLESSON +
                " inner join " +DatabaseHelper.STUDENT +" on "+
                DatabaseHelper.STUDENT +"."+DatabaseHelper.COLUMN_ID + "="+DatabaseHelper.STLESS +"."+DatabaseHelper.COLUMN_IDSTUDENT +
                " where " + DatabaseHelper.STUDENT +"."+DatabaseHelper.COLUMN_IDGROUP +"="+ groupId + " and "+
                DatabaseHelper.LESSON +"."+DatabaseHelper.COLUMN_DATE + " ='"+ selectedDate +"'"+ " and "+
                DatabaseHelper.LESSON +"."+DatabaseHelper.COLUMN_IDSUBJECT + " =" +subjectId, null);
    }
    public Cursor getStudentsByGroup(int groupId){
        return db.rawQuery("select * from "+ DatabaseHelper.STUDENT +
                " where " + DatabaseHelper.COLUMN_IDGROUP +"="+ groupId, null);
    }
    public long CreateLesson(ContentValues cv){
        return db.insert(DatabaseHelper.LESSON, null, cv);
    }
    public  Cursor getAllStudentsOntheLesson(int groupId, int lessonId){
        return db.rawQuery("select * from "+
                DatabaseHelper.STUDENT + " inner join " +DatabaseHelper.STLESS+" on "+
                DatabaseHelper.STUDENT +"."+DatabaseHelper.COLUMN_ID + "="+DatabaseHelper.STLESS +"."+DatabaseHelper.COLUMN_IDSTUDENT +
                " inner join " +DatabaseHelper.LESSON+" on "+
                DatabaseHelper.LESSON +"."+DatabaseHelper.COLUMN_ID + "="+DatabaseHelper.STLESS +"."+DatabaseHelper.COLUMN_IDLESSON +
                " where " + DatabaseHelper.STUDENT +"."+DatabaseHelper.COLUMN_IDGROUP +"="+ groupId +
                " and " + DatabaseHelper.STLESS +"."+DatabaseHelper.COLUMN_IDLESSON +"="+ lessonId , null);
    }
    public Cursor getSubject(int subjectId){
        return db.rawQuery("select "+ DatabaseHelper.SUBJECT+"."+DatabaseHelper.COLUMN_NAME +","+ DatabaseHelper.USER+"."+DatabaseHelper.COLUMN_FIO+
                " from "+ DatabaseHelper.SUBJECT +" inner join " +DatabaseHelper.USER +
                " on "+DatabaseHelper.SUBJECT+"."+DatabaseHelper.COLUMN_IDTEACHER+"="+DatabaseHelper.USER+"."+ DatabaseHelper.COLUMN_ID +" where " +
                DatabaseHelper.SUBJECT+"."+DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(subjectId)});
    }

    public long CreateGroup(ContentValues groupcv){
        return db.insert(DatabaseHelper.GROUP, null, groupcv);
    }
        public  Cursor getUniversitiesByName(String university){
                return db.rawQuery("select * from " + DatabaseHelper.UNIVERSITY +
                        " where " +DatabaseHelper.COLUMN_NAME + "='"+university+"'", null);
        }
public  long CreateUniversity(ContentValues cv){
        return db.insert(DatabaseHelper.UNIVERSITY, null, cv);
}

    public long CreateUser(ContentValues cv){
        return db.insert(DatabaseHelper.USER, null, cv);
    }
}
