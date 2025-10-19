package com.example.myapplication;

import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;

public class LottoDataFetcher {

    private static final String BASE_URL = "https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo=";

    public static LottoResult fetchLottoNumber(int drwNo) throws IOException {
        String url = BASE_URL + drwNo;

        try {
            // Jsoup을 사용하여 HTML 문서를 가져옴
            Document doc = Jsoup.connect(url)
                    .timeout(10000) // 10초 타임아웃
                    .ignoreContentType(true)
                    .get();

            // 🎯 Jsoup은 기본적으로 HTML 응답을 기대하지만,
            // 로또 API URL은 JSON을 반환합니다. Jsoup을 Retrofit 대신 사용하여
            // JSON 응답을 String으로 읽어와 수동으로 파싱하거나 (비효율적),
            // 아니면 아예 JSON이 아닌, 웹사이트의 결과를 크롤링해야 합니다.

            // ⚠️ 잠시: 로또 API는 JSON을 반환하는 URL을 사용하고 있습니다.
            // Jsoup 크롤링으로 완전히 바꾸려면 로또 당첨 페이지 URL을 사용해야 합니다.
            // (예: https://www.dhlottery.co.kr/gameResult.do?method=byWin)

            // ➡️ 기존 JSON URL 대신 Jsoup이 작동하도록 **JSON 문자열을 수동 파싱**하는 코드로 변경하거나,
            // ➡️ JSON 대신 HTML 크롤링으로 전환해야 합니다.

            // ----------------------------------------------------
            // 💡 대안: 기존 URL이 JSON을 반환하므로, Jsoup이 아닌 HttpURLConnection이나 OkHttp를 사용해
            // JSON 문자열을 받고, Gson 대신 **JSONObject로 수동 파싱**하는 것이 더 안정적일 수 있습니다.
            // ----------------------------------------------------

            // 하지만 현재 가장 큰 문제는 '데이터 오류' 토스트이므로,
            // LottoResult의 Getter/Setter 문제를 우회하기 위해 **JSON 수동 파싱**을 시도해 보겠습니다.

            return null; // Jsoup을 통한 JSON 파싱은 복잡하므로, Retrofit 구조를 최대한 살리겠습니다.

        } catch (IOException e) {
            Log.e("LottoFetcher", "네트워크 오류: " + e.getMessage());
            throw e;
        }
    }
}