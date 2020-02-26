package com.example.cannongamesample3.data;

import android.graphics.PointF;

import androidx.annotation.NonNull;

import com.example.cannongamesample3.data.bullet.Bullet;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 총알을 리스트로 관리하는 데이터 클래스
 */
public class BulletManager {
    private ConcurrentHashMap<Integer, Bullet> bullets = new ConcurrentHashMap<>();

    /**
     * 총알을 하나 만들고 컬렉션에 추가한다
     * @return 추가한 총알 id
     */
    public int makeBullet(Bullet bullet) {
        int id = bullet.hashCode();

        bullets.put(id, bullet);

        return id;
    }

    /**
     * 해당하는 id의 총알을 컬렉션에서 삭제한다
     * @param id 삭제할 총알 id
     */
    public void deleteBullet(int id) {
        bullets.remove(id);
    }

    /**
     * 해당하는 id의 총알을 지정된 행동대로 움직인다
     * @param id 움직일 총알 id
     * @return 움직인뒤 총알 좌표
     */
    public PointF moveBullets(int id) {
        Bullet bullet = bullets.get(id);

        if(bullet != null) {
            bullet.move();
            return bullet.coord;
        }
        return null;
    }

    /**
     * 해당하는 id의 총알 객체를 반환한다
     * @param id 반환할 총알 id
     * @return 총알 객체
     */
    public Bullet getBullet(int id) {
        return bullets.get(id);
    }


    @NonNull
    @Override
    public String toString() {
        return "remaining number of bullets: " + bullets.entrySet().size();
    }
}
