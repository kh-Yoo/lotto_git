package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LottoDao {

    // 🚨 중요: 회차 번호(drwNo)가 중복될 경우 기존 데이터를 새 데이터로 대체합니다.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LottoEntity entity);

    // DB에 저장된 모든 로또 기록을 가져옵니다.
    @Query("SELECT * FROM lotto_history")
    List<LottoEntity> getAllHistory();

    // 현재 DB에 저장된 데이터 중 가장 최신 회차 번호(MAX)를 가져옵니다.
    @Query("SELECT MAX(drwNo) FROM lotto_history")
    Integer getLatestDrwNo();

    /**
     * 특정 회차 번호(threshold) 미만의 오래된 데이터를 삭제합니다.
     * HistoryActivity에서 300회차 초과 데이터 정리용으로 사용됩니다.
     * @return 삭제된 행의 개수
     */
    @Query("DELETE FROM lotto_history WHERE drwNo < :threshold")
    int deleteOldHistory(int threshold);

    // 모든 데이터를 삭제하는 쿼리 (DB 오류 시 초기화용)
    @Query("DELETE FROM lotto_history")
    void deleteAll();
}