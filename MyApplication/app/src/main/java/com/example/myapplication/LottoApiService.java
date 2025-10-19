package com.example.myapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LottoApiService {

    // 🎯 동행복권 API의 기본 URL
    String BASE_URL = "https://www.dhlottery.co.kr/";

    /**
     * 최신 로또 당첨 번호를 가져옵니다.
     * 최종 URL: https://www.dhlottery.co.kr/common.do?method=getLottoNumber
     */
    @GET("common.do")
    Call<LottoResult> getLatestLottoNumber(@Query("method") String method);

    /**
     * 특정 회차(drwNo)의 로또 당첨 번호를 가져옵니다.
     * 최종 URL: https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo=XXXX
     *
     * @param method API 호출 방식 ("getLottoNumber"로 고정)
     * @param drwNo 조회할 로또 회차 번호
     */
    @GET("common.do")
    Call<LottoResult> getLottoNumber(
            @Query("method") String method,
            @Query("drwNo") int drwNo
    );
}