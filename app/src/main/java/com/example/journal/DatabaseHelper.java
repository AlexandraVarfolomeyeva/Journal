package com.example.journal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "journal.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных
    static final String USER = "users"; // название таблицы в бд
    static final String ROLE = "roles";
    static final String STUDENT = "students";
    static final String LESSON = "lessons";
    static final String STLESS = "student_lesson";
    static final String GROUP = "groups";
    static final String SUBJECT = "subjects";
    static final String GRSUB = "group_subject";
    static final String UNIVERSITY = "universities";
    // названия столбцов
    public static final String COLUMN_ID = "_id";
    //users
    public static final String COLUMN_IDROLE = "idrole";
    public static final String COLUMN_FIO = "FIO";
    public static final String COLUMN_IDGROUP = "idgroup";
    public static final String COLUMN_IDUNIVERSITY= "iduniversity";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    //Roles
    public static final String COLUMN_NAME = "name";
    //Student

    public static final String COLUMN_ISDELETED = "isdeleted";
    //stless
    public static final String COLUMN_PRESENCE = "presence";
    public static final String COLUMN_MARK = "mark";
    public static final String COLUMN_IDSTUDENT = "idstudent";
    public static final String COLUMN_IDLESSON = "idlesson";
    //subject +NAME
    public static final String COLUMN_IDTEACHER = "idteacher";
    //lesson +DATE
    public static final String COLUMN_IDSUBJECT = "idsubject";
    public static final String COLUMN_DATE = "date";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//user
        db.execSQL("CREATE TABLE "+USER +" (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_FIO+ " TEXT NOT NULL, " +
                COLUMN_IDGROUP + " INTEGER, " +
                COLUMN_IDROLE + " INTEGER NOT NULL," +
                COLUMN_IDUNIVERSITY + " INTEGER NOT NULL,"+
                COLUMN_USERNAME + " text NOT NULL,"+
                COLUMN_EMAIL + " text NOT NULL,"+
                COLUMN_PASSWORD + " text NOT NULL,"+

                "    FOREIGN KEY ("+COLUMN_IDROLE+")" +
                "       REFERENCES "+ ROLE +" ("+COLUMN_ID + ")," +

                "    FOREIGN KEY ("+COLUMN_IDGROUP+")" +
                "       REFERENCES "+ GROUP +" ("+COLUMN_ID + "),"+

                "    FOREIGN KEY ("+COLUMN_IDUNIVERSITY+")" +
                "       REFERENCES "+ UNIVERSITY +" ("+COLUMN_ID + "))");

//student
        db.execSQL("CREATE TABLE "+STUDENT +" (" +
                COLUMN_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_FIO+ " TEXT NOT NULL, "+
                COLUMN_ISDELETED +" INTEGER NOT NULL,"+
                COLUMN_IDGROUP +" INTEGER NOT NULL,"+
                " FOREIGN KEY ("+ COLUMN_IDGROUP +")" +
                " REFERENCES "+ GROUP +" ("+COLUMN_ID + "));");

//stless
        db.execSQL("CREATE TABLE "+STLESS +" (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_IDLESSON + " INTEGER NOT NULL, "+
                COLUMN_IDSTUDENT +" INTEGER NOT NULL, "+
                COLUMN_MARK+" INTEGER,"+
                COLUMN_PRESENCE+" INTEGER ,"+

                " FOREIGN KEY ("+COLUMN_IDSTUDENT+")" +
                " REFERENCES "+ STUDENT +" ("+COLUMN_ID + ")," +

                " FOREIGN KEY ("+ COLUMN_IDLESSON +")" +
                " REFERENCES "+ LESSON +" ("+COLUMN_ID + "));");

 //subject
        db.execSQL("CREATE TABLE "+SUBJECT +" (" +
                COLUMN_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_IDTEACHER+ " INTEGER NOT NULL, "+
                COLUMN_NAME + " TEXT NOT NULL,"+

                " FOREIGN KEY ("+COLUMN_IDTEACHER +")" +
                " REFERENCES "+ USER +" ("+COLUMN_ID + "));");

//lesson
        db.execSQL("CREATE TABLE "+LESSON+" (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_IDSUBJECT + " INTEGER NOT NULL, "+
                COLUMN_DATE +" TEXT NOT NULL, " +

                " FOREIGN KEY ("+COLUMN_IDSUBJECT +")" +
                " REFERENCES "+ SUBJECT +" ("+COLUMN_ID + "));");

//role
        db.execSQL("CREATE TABLE "+ROLE+" (" +
                COLUMN_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT NOT NULL);");

 //role
        db.execSQL("CREATE TABLE "+ GROUP +" (" +
                COLUMN_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT NOT NULL);");

//university
        db.execSQL("CREATE TABLE "+ UNIVERSITY +" (" +
                COLUMN_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT NOT NULL);");

//grsub
        db.execSQL("CREATE TABLE "+ GRSUB +" (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_IDGROUP+ " INTEGER NOT NULL, "+
                COLUMN_IDSUBJECT +" INTEGER NOT NULL, "+

                " FOREIGN KEY ("+COLUMN_IDGROUP+")" +
                " REFERENCES "+ GROUP +" ("+COLUMN_ID + ")," +

                " FOREIGN KEY ("+ COLUMN_IDSUBJECT +")" +
                " REFERENCES "+ SUBJECT +" ("+COLUMN_ID + "));");

        // добавление начальных данных
        db.execSQL("INSERT INTO "+ ROLE +" (" + COLUMN_NAME +") VALUES ('Преподаватель');");
        db.execSQL("INSERT INTO "+ ROLE +" (" + COLUMN_NAME +") VALUES ('Староста');");

        db.execSQL("INSERT INTO "+ UNIVERSITY +" (" + COLUMN_NAME +") VALUES ('ИГЭУ');");
        db.execSQL("INSERT INTO "+ UNIVERSITY +" (" + COLUMN_NAME +") VALUES ('СПбГЭУ');");

        db.execSQL("INSERT INTO "+ GROUP +" (" + COLUMN_NAME +") VALUES ('1-41м');");
        db.execSQL("INSERT INTO "+ GROUP +" (" + COLUMN_NAME +") VALUES ('1-41');");

        db.execSQL("INSERT INTO "+ USER +" (" + COLUMN_FIO + ", " + COLUMN_IDGROUP + ", " + COLUMN_IDROLE + ", "
                + COLUMN_IDUNIVERSITY + ", " + COLUMN_USERNAME +", " + COLUMN_EMAIL +", " + COLUMN_PASSWORD +") " +
                "VALUES ('Садыков Артур Мунавирович',NULL , 1,1,'sadykov','sadykov@gapps.ispu.ru','123456');");
        db.execSQL("INSERT INTO "+ USER +" (" + COLUMN_FIO+ ", " + COLUMN_IDGROUP + ", " + COLUMN_IDROLE + ", "
                + COLUMN_IDUNIVERSITY + ", " + COLUMN_USERNAME +", " + COLUMN_EMAIL +", " + COLUMN_PASSWORD +") " +
                "VALUES ('Косяков Сергей Витальевич',NULL , 1, 1,'kosyakov','kosyakov@gapps.ispu.ru','123456');");
        db.execSQL("INSERT INTO "+ USER +" (" + COLUMN_FIO + ", " + COLUMN_IDGROUP + ", " + COLUMN_IDROLE + ", "
                + COLUMN_IDUNIVERSITY + ", " + COLUMN_USERNAME +", " + COLUMN_EMAIL +", " + COLUMN_PASSWORD +") " +
                "VALUES ('Варфоломеева Александра Александровна', 1, 2 , 1,'varfolomeyeva','16490@gapps.ispu.ru','123456');");

        db.execSQL("INSERT INTO "+ SUBJECT +" (" + COLUMN_NAME+ ", " + COLUMN_IDTEACHER + ") " +
                "VALUES ('Жизненный цикл программного обеспечения',1);");
        db.execSQL("INSERT INTO "+ SUBJECT +" (" + COLUMN_NAME + ", " + COLUMN_IDTEACHER + ") " +
                "VALUES ('Разработка веб-приложений',1);");
        db.execSQL("INSERT INTO "+ SUBJECT +" (" + COLUMN_NAME + ", " + COLUMN_IDTEACHER + ") " +
                "VALUES ('ГИС',2);");

        db.execSQL("INSERT INTO "+ STUDENT +" (" + COLUMN_FIO + ", " + COLUMN_IDGROUP +", " + COLUMN_ISDELETED + ") " +
                "VALUES ('Ананьин Дмитрий',1,0);");
        db.execSQL("INSERT INTO "+ STUDENT +" (" + COLUMN_FIO + ", " + COLUMN_IDGROUP +", " + COLUMN_ISDELETED +  ") " +
                "VALUES ('Барменков Никита',1,0);");
        db.execSQL("INSERT INTO "+ STUDENT +" (" + COLUMN_FIO+ ", " + COLUMN_IDGROUP +", " + COLUMN_ISDELETED +  ") " +
                "VALUES ('Бакшеев Алесандр',1,0);");
        db.execSQL("INSERT INTO "+ STUDENT +" (" + COLUMN_FIO+ ", " + COLUMN_IDGROUP +", " + COLUMN_ISDELETED +  ") " +
                "VALUES ('Барышев Алексей',1,0);");

        db.execSQL("INSERT INTO "+ GRSUB +" (" + COLUMN_IDGROUP+ ", " + COLUMN_IDSUBJECT + ") " +
                "VALUES (1,1);");
        db.execSQL("INSERT INTO "+ GRSUB +" (" + COLUMN_IDGROUP+ ", " + COLUMN_IDSUBJECT + ") " +
                "VALUES (1,3);");
        db.execSQL("INSERT INTO "+ GRSUB +" (" + COLUMN_IDGROUP+ ", " + COLUMN_IDSUBJECT + ") " +
                "VALUES (2,1);");
        db.execSQL("INSERT INTO "+ GRSUB +" (" + COLUMN_IDGROUP+ ", " + COLUMN_IDSUBJECT + ") " +
                "VALUES (2,2);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ USER);
        onCreate(db);
    }
}
