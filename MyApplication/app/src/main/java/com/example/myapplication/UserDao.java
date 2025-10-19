package com.example.myapplication;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {

    /** 🎯 로그인: 아이디와 비밀번호가 모두 일치하는 사용자를 조회 */
    @Query("SELECT * FROM user_table WHERE username = :username AND password = :password LIMIT 1")
    UserEntity login(String username, String password);

    /** 🎯 중복 확인: 아이디로 사용자가 존재하는지 조회 */
    @Query("SELECT * FROM user_table WHERE username = :username LIMIT 1")
    UserEntity getUserByUsername(String username);

    /** 🎯 회원가입: 새 사용자를 DB에 삽입 */
    @Insert
    void insertUser(UserEntity user);

    /** 🎯 테스트용: 모든 사용자 삭제 */
    @Query("DELETE FROM user_table")
    void deleteAll();
}