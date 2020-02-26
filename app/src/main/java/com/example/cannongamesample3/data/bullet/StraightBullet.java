package com.example.cannongamesample3.data.bullet;

import android.graphics.PointF;

/**
 * 직선으로 나가는 총알 객체
 */
public class StraightBullet extends Bullet {

    // 총알이 한번 움직일때 증가치
    private PointF increment = new PointF();

    public StraightBullet() {
        speed = 12;
        visibleDicisionNumber = 8;
    }

    public void setDegree(int d) {
        degree = d;

        calculateBulletMovement(degree, speed);
    }

    /**
     * 총알이 한 move 당 움직일 거리 x,y 계산
     * @param d 총알 각도
     * @param s 총알 스피드
     * @return 움직일 거리를 담은 Point 객체
     */
    private PointF calculateBulletMovement(int d, int s) {
        double radian = d * Math.PI / 180;
        radian += Math.PI;

        increment.x = (float) (Math.cos(radian) * s);
        increment.y = (float) (Math.sin(radian) * s);

        return increment;
    }

    @Override
    public void move() {
        coord.x += increment.x;
        coord.y += increment.y;
        moveCount++;
    }
}
