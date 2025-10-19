package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class UserEntity {

    // 🎯 고유 ID를 자동으로 생성
    @PrimaryKey(autoGenerate = true)
    public int id;

    // 사용자 아이디 (로그인에 사용)
    public String username;

    // 사용자 비밀번호
    public String password;

    // Room이 요구하는 기본 생성자
    public UserEntity() {}

    /**
     * 사용자명과 비밀번호로 엔티티를 생성하는 생성자
     */
    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }
}