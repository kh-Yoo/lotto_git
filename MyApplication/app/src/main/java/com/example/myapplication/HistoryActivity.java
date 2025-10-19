package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HistoryActivity extends AppCompatActivity {

    // 화면에 표시되는 항목 수는 여전히 300개로 제한하지만,
    // DB 저장 기준은 '최신 회차 - 300' 회차까지로 변경됩니다.
    private static final int DISPLAY_HISTORY_COUNT = 300;
    private static final int MIN_DRWNO_OFFSET = 300; // 최신 회차 - 300 (301회차를 포함하는 기준점)
    private static final String TAG = "HistoryActivity";

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private LottoApiService lottoService;
    private LottoDatabase db;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private boolean isDataLoading = false;

    private List<LottoEntity> currentHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("최신 로또 당첨 번호 기록");
        }

        // 1. DB 및 Retrofit 초기화
        db = LottoDatabase.getDatabase(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(LottoApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        lottoService = retrofit.create(LottoApiService.class);


        // 2. 뷰 초기화 및 어댑터 설정
        recyclerView = findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HistoryAdapter(currentHistoryList);
        recyclerView.setAdapter(adapter);

        // 3. 데이터 로딩 시작
        loadHistoryOptimized();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * DB에서 가져온 데이터를 최신순으로 정렬하여 최대 DISPLAY_HISTORY_COUNT(300)개만 화면에 표시합니다.
     */
    private void displayLocalHistory(List<LottoEntity> entities) {
        currentHistoryList.clear();

        if (!entities.isEmpty()) {
            // 최신 회차(drwNo)가 맨 위로 오도록 내림차순 정렬
            Collections.sort(entities, (a, b) -> Integer.compare(b.drwNo, a.drwNo));

            // 최대 300개까지 UI 리스트에 추가합니다.
            int count = Math.min(entities.size(), DISPLAY_HISTORY_COUNT);
            currentHistoryList.addAll(entities.subList(0, count));
        }

        adapter.notifyDataSetChanged();

        if (currentHistoryList.isEmpty()) {
            Toast.makeText(this, "저장된 당첨 번호가 없습니다. 로딩을 시작합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadHistoryOptimized() {
        if (isDataLoading) return;
        isDataLoading = true;
        runOnUiThread(() -> Toast.makeText(this, "데이터 로딩 시작...", Toast.LENGTH_SHORT).show());

        executor.execute(() -> {
            try {
                // DB에서 모든 데이터를 로딩
                List<LottoEntity> localHistory = db.lottoDao().getAllHistory();

                // UI 갱신 (최대 300개 표시)
                runOnUiThread(() -> displayLocalHistory(localHistory));

                // 최신 데이터 여부 확인 및 필요한 경우 API 호출
                fetchLatestDrwNoAndSync();

            } catch (Exception e) {
                // DB 로딩 오류 발생 시 DB를 초기화하고 앱 재시작을 유도합니다.
                Log.e(TAG, "DB 로딩 실패 또는 데이터 처리 오류: " + e.getMessage());

                // 오류 복구 시도: DB 초기화 후 재시작
                try {
                    db.lottoDao().deleteAll();
                } catch (Exception deleteException) {
                    Log.e(TAG, "DB 초기화 실패: " + deleteException.getMessage());
                }

                isDataLoading = false;
                runOnUiThread(() -> {
                    Toast.makeText(HistoryActivity.this,
                            "⚠️ DB 오류 발생. 데이터를 초기화하고 다시 시작합니다.",
                            Toast.LENGTH_LONG).show();
                    recreate(); // 액티비티 재시작
                });
            }
        });
    }

    // 날짜 기반으로 최신 회차를 확인하고 누락된 회차를 역순으로 동기화합니다.
    private void fetchLatestDrwNoAndSync() {

        final int currentLatestDrwNo = LottoUpdateManager.getCalculatedLatestDrwNo();

        executor.execute(() -> {
            try {
                Integer latestLocalDrwNo = db.lottoDao().getLatestDrwNo();

                // 🚨🚨 수정된 로직: 최신 회차 - 300 회차부터 유지합니다. 🚨🚨
                final int minDrwNoToKeep = currentLatestDrwNo - MIN_DRWNO_OFFSET; // currentLatestDrwNo - 300

                int startDrwNo;
                if (latestLocalDrwNo == null) {
                    // DB가 비어있다면, minDrwNoToKeep부터 시작
                    startDrwNo = minDrwNoToKeep;
                } else if (latestLocalDrwNo < currentLatestDrwNo) {
                    // 최신 회차보다 DB가 낮으면, DB 최신 회차의 다음 회차부터 시작
                    startDrwNo = latestLocalDrwNo + 1;
                } else {
                    isDataLoading = false;
                    runOnUiThread(() -> Toast.makeText(HistoryActivity.this,
                            String.format("✅ 최신 데이터(%d회)가 있습니다.", currentLatestDrwNo), Toast.LENGTH_SHORT).show());
                    return;
                }

                // API로 가져와야 할 최종 시작 회차 (1회차 미만으로 내려가지 않도록 보장)
                final int finalStartDrwNo = Math.max(startDrwNo, 1);

                // 실제 동기화를 시작할 최소 회차 (최소 유지 회차와 실제 시작 회차 중 큰 값)
                final int finalMinDrwNo = Math.max(finalStartDrwNo, minDrwNoToKeep);

                if (finalMinDrwNo <= currentLatestDrwNo) {
                    runOnUiThread(() -> Toast.makeText(HistoryActivity.this,
                            String.format("📢 %d회차부터 %d회차까지 업데이트를 시작합니다.",
                                    currentLatestDrwNo, finalMinDrwNo),
                            Toast.LENGTH_LONG).show());

                    // 순차 업데이트 루틴 시작 (최신 회차부터 시작하여 역순으로 진행)
                    syncMissingHistory(finalMinDrwNo, currentLatestDrwNo);

                } else {
                    isDataLoading = false;
                    runOnUiThread(() -> Toast.makeText(HistoryActivity.this,
                            String.format("✅ 데이터 범위가 이미 충분합니다.(%d회차 이상)", MIN_DRWNO_OFFSET), Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "DB 확인 중 오류: " + e.getMessage());
                isDataLoading = false;
            }
        });
    }

    // 누락된 회차들을 역순으로 (최신부터) 순차적으로 API에서 가져와 저장 및 UI에 실시간 추가합니다.
    private void syncMissingHistory(int minDrwNo, int maxDrwNo) {
        executor.execute(new Runnable() {
            int currentDrwNo = maxDrwNo;

            @Override
            public void run() {
                if (currentDrwNo >= minDrwNo) {

                    try {
                        Thread.sleep(1000); // 1초 지연 (부하 방지)
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        isDataLoading = false;
                        runOnUiThread(() -> Toast.makeText(HistoryActivity.this, "업데이트 중단됨.", Toast.LENGTH_LONG).show());
                        return;
                    }

                    fetchAndSaveSingleDrwNo(currentDrwNo, new Runnable() {
                        @Override
                        public void run() {
                            currentDrwNo--;
                            executor.execute(this); // 다음 회차 로딩 시작
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            isDataLoading = false;
                            runOnUiThread(() -> Toast.makeText(HistoryActivity.this,
                                    String.format("%d회차 로딩 실패 후 업데이트 중단.", currentDrwNo), Toast.LENGTH_LONG).show());
                        }
                    });
                } else {
                    isDataLoading = false;
                    runOnUiThread(() -> {
                        Toast.makeText(HistoryActivity.this, "🎉 업데이트 완료!", Toast.LENGTH_LONG).show();
                    });

                    // 업데이트 완료 후, 오래된 데이터 정리
                    executor.execute(() -> cleanUpOldData(maxDrwNo));
                }
            }
        });
    }

    /**
     * 특정 회차(drwNo) 하나를 가져와 DB에 저장하고, UI에 추가하며 다음 작업을 알립니다.
     */
    private void fetchAndSaveSingleDrwNo(int drwNo, Runnable onSuccess, Runnable onFailure) {
        lottoService.getLottoNumber("getLottoNumber", drwNo).enqueue(new Callback<LottoResult>() {
            @Override
            public void onResponse(Call<LottoResult> call, Response<LottoResult> response) {
                LottoResult body = response.body();
                boolean isSuccess = response.isSuccessful() &&
                        body != null &&
                        "success".equals(body.getReturnValue());

                if (isSuccess) {
                    LottoEntity newEntity = new LottoEntity(body);

                    executor.execute(() -> {
                        try {
                            db.lottoDao().insert(newEntity);

                            runOnUiThread(() -> addLottoEntityToUIAndNotify(newEntity));

                            runOnUiThread(onSuccess);
                        } catch (Exception e) {
                            Log.e(TAG, "DB 저장 실패: " + e.getMessage());
                            runOnUiThread(onFailure);
                        }
                    });
                } else {
                    runOnUiThread(onFailure);
                }
            }

            @Override
            public void onFailure(Call<LottoResult> call, Throwable t) {
                Log.e(TAG, String.format("%d회차 네트워크 오류: %s", drwNo, t.getMessage()));
                runOnUiThread(onFailure);
            }
        });
    }

    /**
     * 새로운 LottoEntity 1개를 UI 목록 맨 위에 추가하고 어댑터를 갱신합니다.
     */
    private void addLottoEntityToUIAndNotify(LottoEntity entity) {
        currentHistoryList.add(0, entity);

        // DISPLAY_HISTORY_COUNT(300)개 초과 시 가장 오래된 항목(맨 뒤) 제거
        if (currentHistoryList.size() > DISPLAY_HISTORY_COUNT) {
            currentHistoryList.remove(DISPLAY_HISTORY_COUNT);
            adapter.notifyItemRemoved(DISPLAY_HISTORY_COUNT);
        }

        adapter.notifyItemInserted(0);

        Toast.makeText(this,
                String.format("🔄 %d회차 화면에 추가 완료.", entity.drwNo), Toast.LENGTH_SHORT).show();
    }

    /**
     * 업데이트 완료 후, DB에 '최신 회차 - 300'보다 오래된 데이터가 남아있다면 삭제합니다.
     */
    private void cleanUpOldData(int latestDrwNo) {
        // 🚨🚨 수정된 로직: 최신 회차 - 300 보다 작은 회차는 정리합니다. 🚨🚨
        final int thresholdDrwNo = latestDrwNo - MIN_DRWNO_OFFSET; // latestDrwNo - 300

        try {
            // LottoDao.java에 정의된 deleteOldHistory 메서드를 사용합니다.
            int deletedCount = db.lottoDao().deleteOldHistory(thresholdDrwNo);
            if (deletedCount > 0) {
                Log.i(TAG, deletedCount + "개의 오래된 DB 항목 삭제 완료 (회차 < " + thresholdDrwNo + ")");
            }
        } catch (Exception e) {
            Log.e(TAG, "오래된 데이터 정리 실패: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}