package com.example.cannongamesample3.data.bullet;

import android.graphics.PointF;

import androidx.annotation.NonNull;

/**
 * 총알의 대한 데이터 클래스
 */
public abstract class Bullet {

    // 위치
    public PointF coord = new PointF();
    // 각도
    public int degree = 0;
    // 속도
    public int speed = 0;
    // 언제부터 총알이 보일지 나타내는 값
    public int visibleDicisionNumber = 0;
    // 총알이 움직인 횟수
    public long moveCount = 0;

    /**
     * 총알의 움직임을 표현하는 메서드
     */
    abstract public void move();

    @NonNull
    @Override
    public String toString() {
        return "Bullet Coord: " + coord
                + " Degree: " + degree
                + " Speed: " + speed
                + " moveCount: " + moveCount;
    }
}
