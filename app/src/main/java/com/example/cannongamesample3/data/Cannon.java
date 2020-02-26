package com.example.cannongamesample3.data;

import androidx.annotation.NonNull;

/**
 * 대포를 의미하는 데이터 클래스
 */
public class Cannon {

    // 각도
    public int degree = 0;

    @NonNull
    @Override
    public String toString() {
        return "Cannon Degree: " + degree;
    }
}
