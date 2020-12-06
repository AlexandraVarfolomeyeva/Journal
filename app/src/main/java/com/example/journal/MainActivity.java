package com.example.journal;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    ListView subjectsList;
    TextView header;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    Button basket;
    ProgressBar prgLoading;
    EditText edtKeyword;
    ImageButton btnSearch;
    TextView txtAlert;
    String Keyword;

    Button goToBasketBtn;
    private EditText mMessageEditText;
    public static final String EXTRA_MESSAGE =
            "com.example.android.BookShop.extra.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        goToBasketBtn = (Button) findViewById(R.id.goToBasketBtn);
        //  goToBasketBtn.setOnClickListener(this);
        goToBasketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = mMessageEditText.getText().toString();
                // TODO Call second activity
                Intent intent = new Intent(MainActivity.this, AuthorizationActivity.class);
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        });

        mMessageEditText = findViewById(R.id.editText_main);
*/


        header = (TextView)findViewById(R.id.header);
        subjectsList = (ListView)findViewById(R.id.list);
        subjectsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), LessonActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        databaseHelper = new DatabaseHelper(getApplicationContext());

    }

    @Override
    public void onResume() {
        super.onResume();
        // открываем подключение
        db = databaseHelper.getReadableDatabase();

        //получаем данные из бд в виде курсора
        userCursor =  db.rawQuery("select * from "+ DatabaseHelper.USER
                + " inner join "+ DatabaseHelper.SUBJECT+
                               " on "+DatabaseHelper.USER+"."+DatabaseHelper.COLUMN_ID+"="+DatabaseHelper.SUBJECT+"."+DatabaseHelper.COLUMN_IDTEACHER, null);
        //
        // определяем, какие столбцы из курсора будут выводиться в ListView
        String[] headers = new String[] {DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_FIO};
        // создаем адаптер, передаем в него курсор
        userAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        header.setText("Найдено предметов: " + String.valueOf(userCursor.getCount()));
        subjectsList.setAdapter(userAdapter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        userCursor.close();
    }
}
