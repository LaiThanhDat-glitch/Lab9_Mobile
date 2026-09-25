package com.example.lab9;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etId, etName, etEmail, etTel;
    private Button btnSavePref, btnLoadPref, btnSaveSqlite, btnLoadSqlite;

    private static final String PREF_NAME = "StudentPrefs";
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ View
        etId = findViewById(R.id.etId);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etTel = findViewById(R.id.etTel);

        btnSavePref = findViewById(R.id.btnSavePref);
        btnLoadPref = findViewById(R.id.btnLoadPref);
        btnSaveSqlite = findViewById(R.id.btnSaveSqlite);
        btnLoadSqlite = findViewById(R.id.btnLoadSqlite);

        dbHelper = new DatabaseHelper(this);

        // 1. SharedPreferences: Save
        btnSavePref.setOnClickListener(v -> {
            SharedPreferences pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("id", etId.getText().toString().trim());
            editor.putString("name", etName.getText().toString().trim());
            editor.putString("email", etEmail.getText().toString().trim());
            editor.putString("tel", etTel.getText().toString().trim());
            editor.apply();

            Toast.makeText(this, "Đã lưu vào SharedPreferences!", Toast.LENGTH_SHORT).show();
        });

        // 1. SharedPreferences: Load
        btnLoadPref.setOnClickListener(v -> {
            SharedPreferences pref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
            etId.setText(pref.getString("id", ""));
            etName.setText(pref.getString("name", ""));
            etEmail.setText(pref.getString("email", ""));
            etTel.setText(pref.getString("tel", ""));

            Toast.makeText(this, "Đã tải từ SharedPreferences!", Toast.LENGTH_SHORT).show();
        });

        // 2. SQLite: Save
        btnSaveSqlite.setOnClickListener(v -> {
            String id = etId.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String tel = etTel.getText().toString().trim();

            if (id.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập ID để lưu SQLite!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isInserted = dbHelper.saveStudent(id, name, email, tel);
            if (isInserted) {
                Toast.makeText(this, "Đã lưu vào SQLite!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lưu vào SQLite thất bại!", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. SQLite: Load (tìm theo ID đang nhập ở ô etId)
        btnLoadSqlite.setOnClickListener(v -> {
            String id = etId.getText().toString().trim();
            if (id.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập ID cần tìm từ SQLite!", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor cursor = dbHelper.getStudentById(id);
            if (cursor != null && cursor.moveToFirst()) {
                etName.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)));
                etEmail.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL)));
                etTel.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TEL)));
                cursor.close();
                Toast.makeText(this, "Đã tải dữ liệu từ SQLite!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Không tìm thấy sinh viên có ID: " + id, Toast.LENGTH_SHORT).show();
            }
        });
    }
}