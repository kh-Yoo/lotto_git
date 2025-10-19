package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText editUsername, editPassword;
    private Button buttonConfirmRegister;
    private LottoDatabase db;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 뒤로가기 버튼 표시
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("회원가입");
        }

        db = LottoDatabase.getDatabase(this);
        editUsername = findViewById(R.id.reg_edit_username);
        editPassword = findViewById(R.id.reg_edit_password);
        buttonConfirmRegister = findViewById(R.id.button_confirm_register);

        buttonConfirmRegister.setOnClickListener(v -> handleRegister());
    }

    // 뒤로가기 버튼 처리
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private boolean validateInput(String username, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            runOnUiThread(() -> Toast.makeText(this, "아이디와 비밀번호를 모두 입력하세요.", Toast.LENGTH_SHORT).show());
            return false;
        }
        if (username.length() < 4 || password.length() < 4) {
            runOnUiThread(() -> Toast.makeText(this, "아이디와 비밀번호는 4자 이상이어야 합니다.", Toast.LENGTH_SHORT).show());
            return false;
        }
        return true;
    }

    private void handleRegister() {
        final String username = editUsername.getText().toString();
        final String password = editPassword.getText().toString();

        if (!validateInput(username, password)) return;

        executor.execute(() -> {
            // 1. 아이디 중복 확인 (백그라운드)
            UserEntity existingUser = db.userDao().getUserByUsername(username);

            if (existingUser != null) {
                runOnUiThread(() -> Toast.makeText(this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show());
            } else {
                // 2. 중복이 없으면 DB에 저장 (백그라운드)
                db.userDao().insertUser(new UserEntity(username, password));

                // 3. UI 업데이트 및 액티비티 종료 (메인 스레드)
                runOnUiThread(() -> {
                    Toast.makeText(this, "회원가입 성공! 로그인해주세요.", Toast.LENGTH_LONG).show();
                    finish(); // 회원가입 완료 후 로그인 화면으로 돌아감
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}