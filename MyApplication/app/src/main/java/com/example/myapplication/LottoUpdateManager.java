package com.example.myapplication;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class LottoUpdateManager {

    // 1194회 추첨일 기준 (2025년 10월 18일 토요일 20:45)
    private static final int BASE_DRW_NO = 1194;
    private static final long BASE_DATE_MILLIS;

    static {
        // 기준 날짜 설정: 2025년 10월 18일 20:45 (로또 추첨 마감 시간)
        Calendar baseCal = Calendar.getInstance();
        baseCal.set(2025, Calendar.OCTOBER, 18, 20, 45, 0);
        baseCal.set(Calendar.MILLISECOND, 0);
        BASE_DATE_MILLIS = baseCal.getTimeInMillis();
    }

    /**
     * 현재 시간을 기준으로 최신 로또 회차 번호를 계산합니다.
     * @return 현재 최신 회차 번호
     */
    public static int getCalculatedLatestDrwNo() {
        long currentMillis = Calendar.getInstance().getTimeInMillis();

        // 현재 시간이 기준 날짜보다 이전이라면, 1194회를 반환
        if (currentMillis < BASE_DATE_MILLIS) {
            return BASE_DRW_NO;
        }

        // 밀리초 차이 계산 후 주(Week) 단위 차이 계산 (1주 = 7일)
        long diffMillis = currentMillis - BASE_DATE_MILLIS;
        long diffWeeks = TimeUnit.MILLISECONDS.toDays(diffMillis) / 7;

        // 현재 회차 번호 = 기준 회차 + 주 단위 차이
        int latestDrwNo = BASE_DRW_NO + (int) diffWeeks;

        return latestDrwNo;
    }
}