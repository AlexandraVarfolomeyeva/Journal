package com.example.journal;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.example.journal.ui.login.LoginViewModel;
import com.example.journal.ui.login.LoginViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    ListView subjectsList;
    TextView header;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    int Group, Role,UserId;

    private LoginViewModel loginViewModel;

    Repository repository;

    public static final String EXTRA_MESSAGE =
            "com.example.android.BookShop.extra.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        repository = new Repository(getApplicationContext());

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        Role = loginViewModel.getRoleId();
        Group = loginViewModel.getGroupId();
        UserId= loginViewModel.getUserId();
        FloatingActionButton AddSubjectBtn = findViewById(R.id.addsubjectbtn);
        header = (TextView)findViewById(R.id.header);
        subjectsList = (ListView)findViewById(R.id.list);


        subjectsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int subjectId=setSubjectId(position);

                Intent intent = new Intent(MainActivity.this, LessonActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                intent.putExtra("id", subjectId);
                intent.putExtra("groupid", Group);
                intent.putExtra("userid", UserId);
                startActivity(intent);
            }
        });
        if (Role!=1){
            AddSubjectBtn.setVisibility(View.GONE);
        }
        AddSubjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddSubjectActivity.class);
                startActivity(intent);
            }
        });

    }

    public int setSubjectId(int position){
        Cursor subjectCursor;
        subjectCursor = getSubjects();
        if (subjectCursor.getCount()>=position)
        {subjectCursor.moveToPosition(position);
        int subjectId = Integer.parseInt(subjectCursor.getString(0));
        subjectCursor.close();
        return subjectId;} else
            {
                return -1;
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        // открываем подключение
            userCursor = getSubjects();
        //
        // определяем, какие столбцы из курсора будут выводиться в ListView
        String[] headers = new String[] {DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_FIO};
        // создаем адаптер, передаем в него курсор
        userAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        header.setText("Найдено предметов: " + String.valueOf(userCursor.getCount()));
        subjectsList.setAdapter(userAdapter);
    }

    public Cursor getSubjects(){
        Cursor result;
        if (Group == -1) {
            //получаем данные из бд в виде курсора
            result = repository.getSubjectsForTeacher(UserId);
        } else {
            result = repository.getSubjectsForStudent(Group);
        }
        return result;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        loginViewModel.logout();
        if (userCursor != null) {userCursor.close();}
    }
}
