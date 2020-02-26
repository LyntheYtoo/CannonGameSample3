package com.example.cannongamesample3.data;

import android.graphics.PointF;

import androidx.annotation.NonNull;

/**
 * 게임 화면을 의미하는 데이터 클래스
 * 게임 화면은 사각형으로 되있음
 */
public class GameArea {

    // 총알이 에어리어에 오래 남아있게 하는 마진
    public int margin = 32;
    // 위왼쪽 점 좌표
    public PointF topLeft = new PointF();
    // 아래오른쪽 점 좌표
    public PointF bottomRight = new PointF();

    @NonNull
    @Override
    public String toString() {
        return "GameArea TopLeft: " + topLeft
                + " BottomRight: " + bottomRight
                + " Margin: " + margin;
    }
}
