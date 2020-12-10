package com.example.journal;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.journal.ui.login.LoginViewModel;
import com.example.journal.ui.login.LoginViewModelFactory;

import java.util.ArrayList;

public class AddSubjectActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    Repository repository;
    String[] listItem;
    boolean[] checkedItems;
    ArrayList<Integer> mGroupItems = new ArrayList<>();
    EditText gr;

    private void getGroups(){
        Cursor groupsCursor = repository.getAllGroups();
        if (groupsCursor.getCount()>0) {
            String[] headers = new String[] {DatabaseHelper.COLUMN_NAME};
            SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, groupsCursor, headers, new int[]{android.R.id.text1});
            mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          groupsCursor.moveToFirst();
            ArrayList<String> list  = new ArrayList<>();
          do {
              list.add(groupsCursor.getString(1));
          }while (groupsCursor.moveToNext());
            listItem = new String[list.size()];
            for (int i=0; i<list.size();i++) {
                listItem[i] = list.get(i);
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Возникла ошибка!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        final int UserId= loginViewModel.getUserId();
        repository= new Repository(this);
        getGroups();

        checkedItems = new boolean[listItem.length];

        final EditText SubjectName = findViewById(R.id.SubjectName);
        final Button addbtn = findViewById(R.id.addbtn);
        gr=(EditText)findViewById(R.id.groupNums);

        gr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(AddSubjectActivity.this);
                mBuilder.setTitle(R.string.prompt_groupNums);
                mBuilder.setMultiChoiceItems(listItem, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            mGroupItems.add(position);
                        }else{
                            mGroupItems.remove((Integer.valueOf(position)));
                        }

                    }});
                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < mGroupItems.size(); i++) {
                            item = item + listItem[mGroupItems.get(i)];
                            if (i != mGroupItems.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        gr.setText(item);
                    }
                });

                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            mGroupItems.clear();
                            gr.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
        }});




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
                addbtn.setEnabled(SubjectName.getText().toString() != "" && gr.getText().toString() !="");
            }
        };
        SubjectName.addTextChangedListener(afterTextChangedListener);

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.COLUMN_NAME, SubjectName.getText().toString());
                cv.put(DatabaseHelper.COLUMN_IDTEACHER, UserId);
                long subId=repository.CreateSubject(cv);

                for (Integer pos:mGroupItems
                     ) {
                    Cursor groupsCursor =  repository.getAllGroups();
                    if (groupsCursor.getCount()>0) {
                        groupsCursor.moveToPosition(pos);
                        ContentValues grsub = new ContentValues();
                        grsub.put(DatabaseHelper.COLUMN_IDGROUP,groupsCursor.getString(0));
                        grsub.put(DatabaseHelper.COLUMN_IDSUBJECT,subId);
                        long result = repository.CreateGroupSubject(grsub);
                       // Toast.makeText(getApplicationContext(), String.valueOf(result), Toast.LENGTH_LONG).show();
                    }
                }
                goHome();
            }
        });
    }

    private void goHome(){
        // переход к главной activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }


}
