package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// 테이블 이름 정의
@Entity(tableName = "lotto_history")
public class LottoEntity {

    // 🚨 중요: drwNo를 기본 키로 설정하여 중복 데이터 저장을 방지하고 대체(REPLACE)할 수 있게 합니다.
    @PrimaryKey
    public int drwNo;

    public String drwNoDate;
    public int drwtNo1;
    public int drwtNo2;
    public int drwtNo3;
    public int drwtNo4;
    public int drwtNo5;
    public int drwtNo6;
    public int bnusNo; // 보너스 번호
    public String returnValue; // API 응답 상태 (success 확인용)

    // 💡 오류 해결: Room이 DB에서 데이터를 읽어와 객체를 생성할 때 사용하는 기본 생성자
    public LottoEntity() {
        // Room이 필드를 로드하여 초기화하므로 내용은 비워둡니다.
    }

    // LottoResult DTO를 LottoEntity로 변환하는 생성자 (API 호출 시 사용)
    public LottoEntity(LottoResult result) {
        this.drwNo = result.getDrwNo();
        this.drwNoDate = result.getDrwNoDate();
        this.drwtNo1 = result.getDrwtNo1();
        this.drwtNo2 = result.getDrwtNo2();
        this.drwtNo3 = result.getDrwtNo3();
        this.drwtNo4 = result.getDrwtNo4();
        this.drwtNo5 = result.getDrwtNo5();
        this.drwtNo6 = result.getDrwtNo6();
        this.bnusNo = result.getBnusNo();
        this.returnValue = result.getReturnValue();
    }
}