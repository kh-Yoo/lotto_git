package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    // 로또 기능 관련 UI
    private LinearLayout lottoFeatureLayout;
    private TextView number1, number2, number3, number4, number5, number6;
    private Button generateButton, historyButton, logoutButton;

    // 로그인 기능 관련 UI
    private LinearLayout loginFormLayout;
    private EditText editUsername, editPassword;
    private Button buttonLogin, buttonRegister; // 회원가입 버튼

    // DB 및 비동기 처리
    private LottoDatabase db;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = LottoDatabase.getDatabase(this);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        initLottoUI();
        initLoginUI();

        checkLoginStatusAndSwitchUI();
    }

    private void initLottoUI() {
        lottoFeatureLayout = findViewById(R.id.lottoFeatureLayout);
        number1 = findViewById(R.id.number1);
        number2 = findViewById(R.id.number2);
        number3 = findViewById(R.id.number3);
        number4 = findViewById(R.id.number4);
        number5 = findViewById(R.id.number5);
        number6 = findViewById(R.id.number6);
        generateButton = findViewById(R.id.generateButton);
        historyButton = findViewById(R.id.historyButton);
        logoutButton = findViewById(R.id.logoutButton);

        generateButton.setOnClickListener(v -> {
            List<Integer> lottoNumbers = generateLottoNumbers();
            displayLottoNumbers(lottoNumbers);
        });

        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> handleLogout());
    }

    private void initLoginUI() {
        loginFormLayout = findViewById(R.id.loginFormLayout);
        editUsername = findViewById(R.id.edit_username);
        editPassword = findViewById(R.id.edit_password);
        buttonLogin = findViewById(R.id.button_login);
        buttonRegister = findViewById(R.id.button_register);

        buttonLogin.setOnClickListener(v -> handleLogin());

        // 🎯 회원가입 버튼 클릭 시 RegisterActivity로 이동
        buttonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void checkLoginStatusAndSwitchUI() {
        if (sharedPreferences.getBoolean("is_logged_in", false)) {
            loginFormLayout.setVisibility(View.GONE);
            lottoFeatureLayout.setVisibility(View.VISIBLE);
            String user = sharedPreferences.getString("current_user", "사용자");
            // Toast.makeText(this, user + "님, 환영합니다!", Toast.LENGTH_SHORT).show(); // onResume에서 발생 방지
        } else {
            lottoFeatureLayout.setVisibility(View.GONE);
            loginFormLayout.setVisibility(View.VISIBLE);
        }
    }

    // ===================================================
    // 로그인 / 로그아웃 로직
    // ===================================================
    private boolean validateInput(String username, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "아이디와 비밀번호를 모두 입력하세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (username.length() < 4 || password.length() < 4) {
            Toast.makeText(this, "아이디와 비밀번호는 4자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void handleLogin() {
        final String username = editUsername.getText().toString();
        final String password = editPassword.getText().toString();

        if (!validateInput(username, password)) return;

        executor.execute(() -> {
            UserEntity user = db.userDao().login(username, password);

            runOnUiThread(() -> {
                if (user != null) {
                    sharedPreferences.edit()
                            .putBoolean("is_logged_in", true)
                            .putString("current_user", username)
                            .apply();
                    Toast.makeText(this, username + "님, 환영합니다!", Toast.LENGTH_SHORT).show();
                    checkLoginStatusAndSwitchUI();
                } else {
                    Toast.makeText(this, "로그인 정보가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void handleLogout() {
        sharedPreferences.edit().clear().apply();
        checkLoginStatusAndSwitchUI();
        editUsername.setText("");
        editPassword.setText("");
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
    }

    // ===================================================
    // 로또 기능 로직 (기존 로직 유지)
    // ===================================================
    private List<Integer> generateLottoNumbers() {
        ArrayList<Integer> fullRange = new ArrayList<>();
        for (int i = 1; i <= 45; i++) { fullRange.add(i); }
        Collections.shuffle(fullRange);
        List<Integer> lottoNumbers = new ArrayList<>(fullRange.subList(0, 6));
        Collections.sort(lottoNumbers);
        return lottoNumbers;
    }

    private void displayLottoNumbers(List<Integer> numbers) {
        if (numbers.size() >= 6) {
            number1.setText(String.valueOf(numbers.get(0)));
            number2.setText(String.valueOf(numbers.get(1)));
            number3.setText(String.valueOf(numbers.get(2)));
            number4.setText(String.valueOf(numbers.get(3)));
            number5.setText(String.valueOf(numbers.get(4)));
            number6.setText(String.valueOf(numbers.get(5)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 로그인 성공 후 돌아올 때 UI를 업데이트
        checkLoginStatusAndSwitchUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}