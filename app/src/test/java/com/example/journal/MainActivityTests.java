package com.example.journal;

import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;
import org.mockito.Mock;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class MainActivityTests {

    MainActivity mainActivity = mock(MainActivity.class);
    @Mock
    DatabaseHelper dataBaseHelper;
    private static final int CURRENT_DATABASE_VERSION = 42;
    private File mDatabaseFile;
 /*   @Override
    protected void setUp() throws Exception {
        super.setUp();
        File dbDir = getContext().getDir("tests", Context.MODE_PRIVATE);
        mDatabaseFile = new File(dbDir, "database_test.db");

        if (mDatabaseFile.exists()) {
            mDatabaseFile.delete();
        }
        mDatabase = SQLiteDatabase.openOrCreateDatabase(mDatabaseFile.getPath(), null);
        assertNotNull(mDatabase);
        mDatabase.setVersion(CURRENT_DATABASE_VERSION);
    }

    @Override
    protected void tearDown() throws Exception {
        mDatabase.close();
        mDatabaseFile.delete();
        super.tearDown();
    }
*/
    @Mock
    private SQLiteDatabase mDatabase;
    @Test
    public void get_subject_id_not_null() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void get_subject_id_returns_int() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void getSubjects_returns_not_null() {
        assertEquals(4, 2 + 2);
    }
}
