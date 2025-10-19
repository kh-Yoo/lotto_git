package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private Button loginButton;
    private ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 🚨 이 부분이 R.id.login_button을 찾습니다.
        // XML 파일에 해당 ID가 정의되어야 오류가 해결됩니다.
        loginButton = findViewById(R.id.button_login);

        initializeDatabase();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, HistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initializeDatabase() {
        databaseExecutor.execute(() -> {
            try {
                LottoDatabase.getDatabase(this);
                Log.i(TAG, "Database initialized successfully.");
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize database: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(
                        LoginActivity.this,
                        "DB 초기화 오류 발생. 앱을 재설치해주세요.",
                        Toast.LENGTH_LONG).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseExecutor.shutdown();
    }
}