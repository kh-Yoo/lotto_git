package com.example.myapplication;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// 🚨🚨🚨 1. UserEntity를 entities 목록에 추가하고, DB 버전을 1 증가시킵니다. 🚨🚨🚨
@Database(entities = {LottoEntity.class, UserEntity.class}, version = 3)
public abstract class LottoDatabase extends RoomDatabase {

    public abstract LottoDao lottoDao();

    // 🚨 2. UserDao를 추가합니다. 이 메서드가 없어서 오류가 발생했습니다. 🚨
    public abstract UserDao userDao();

    private static volatile LottoDatabase INSTANCE;
    private static final String DATABASE_NAME = "lotto_db";

    public static LottoDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LottoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    LottoDatabase.class,
                                    DATABASE_NAME
                            )
                            // 스키마 변경 시 기존 데이터를 버리고 새로 만듭니다. (개발 단계에서 편리)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}