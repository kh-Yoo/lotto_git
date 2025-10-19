package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

// ⚠️ com.google.gson.annotations.SerializedName을 반드시 import 해야 합니다.

public class LottoResult {

    // 🎯 에러의 주범: API 호출 결과 상태 (성공 시 "success"여야 함)
    @SerializedName("returnValue")
    private String returnValue;

    // 🎯 회차 정보
    @SerializedName("drwNo")
    private int drwNo;
    @SerializedName("drwNoDate")
    private String drwNoDate;

    // 🎯 당첨 번호 6개
    @SerializedName("drwtNo1")
    private int drwtNo1;
    @SerializedName("drwtNo2")
    private int drwtNo2;
    @SerializedName("drwtNo3")
    private int drwtNo3;
    @SerializedName("drwtNo4")
    private int drwtNo4;
    @SerializedName("drwtNo5")
    private int drwtNo5;
    @SerializedName("drwtNo6")
    private int drwtNo6;

    // 🎯 보너스 번호
    @SerializedName("bnusNo")
    private int bnusNo;

    // -------------------------------------------
    // * Getter 메서드 (LottoEntity 생성을 위해 필요)
    // -------------------------------------------

    public String getReturnValue() {
        return returnValue;
    }

    public int getDrwNo() {
        return drwNo;
    }

    public String getDrwNoDate() {
        return drwNoDate;
    }

    public int getDrwtNo1() {
        return drwtNo1;
    }

    public int getDrwtNo2() {
        return drwtNo2;
    }

    public int getDrwtNo3() {
        return drwtNo3;
    }

    public int getDrwtNo4() {
        return drwtNo4;
    }

    public int getDrwtNo5() {
        return drwtNo5;
    }

    public int getDrwtNo6() {
        return drwtNo6;
    }

    public int getBnusNo() {
        return bnusNo;
    }
}