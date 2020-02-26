package com.example.cannongamesample3;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cannongamesample3.data.BulletManager;
import com.example.cannongamesample3.data.Cannon;
import com.example.cannongamesample3.data.GameArea;
import com.example.cannongamesample3.data.bullet.Bullet;
import com.example.cannongamesample3.data.bullet.StraightBullet;
import com.example.cannongamesample3.util.TaskRepeater;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainActivity extends AppCompatActivity {
    public static final int MID_DEGREE = 90;

    // 뷰 필드
    private SeekBar mSeekBar;
    private Button mFireButton;

    private FrameLayout mAreaView;
    private ImageView mCannonView;
    private ConcurrentHashMap<Integer, View> mBulletViews = new ConcurrentHashMap<>();

    // 데이터 필드
    private GameArea mArea = new GameArea();
    private Cannon mCannon = new Cannon();
    private BulletManager mBulletManager = new BulletManager();


    private TaskRepeater mTaskRepeater = new TaskRepeater();

    /**
     * 뷰 초기화, 리스너 설정, 데이터 초기값 설정
     *
     * @param savedInstanceState 사용하지 않음
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 뷰 초기화
        mAreaView = findViewById(R.id.gamescreen_background);
        mSeekBar = findViewById(R.id.controller_seekbar);
        mFireButton = findViewById(R.id.fire_button);

        mCannonView = findViewById(R.id.cannon_imageview);

        // 각도 초기값 설정, 각도 조절기 상태유지 해제 ( 액티비티 Destroy 시에도 상태 유지 해제 )
        mSeekBar.setProgress(MID_DEGREE);
        mSeekBar.setSaveEnabled(false);
        mCannonView.setRotation(MID_DEGREE);

        // SeekBar 리스너 정의
        // SeekBar를 움직이면 캐논뷰와 컨트롤러에도 영향을 미친다
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!fromUser) return;
                changeDegree(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // FireButton 리스너 정의
        // 연사 기능은 간략하게 안에서 구현
        // 손가락을 눌렀다 떼는 사이에 연속 발사
        mFireButton.setOnTouchListener(new View.OnTouchListener() {
            private TaskRepeater mFireRepeater = new TaskRepeater();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:{
                        if(mFireRepeater.isRunning()) mFireRepeater.stopRepeater();
                        mFireRepeater.setDelay(128);
                        mFireRepeater.setTask(new Runnable() {
                            @Override
                            public void run() {
                                shoot();
                            }
                        });
                        mFireRepeater.startRepeater();
                    }break;

                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:{
                        mFireRepeater.stopRepeater();
                    }break;
                }
                return false;
            }
        });

        // 루트뷰가 그려진 뒤에
        // 캐논게임 데이터를 초기화
        mAreaView.getRootView().post(new Runnable() {
            @Override
            public void run() {
                changeDegree(MID_DEGREE);

                mArea.topLeft = new PointF(mAreaView.getX(), mAreaView.getY());
                mArea.bottomRight = new PointF(mAreaView.getX() + mAreaView.getWidth(), mAreaView.getY() + mAreaView.getHeight());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Resume 때 타이머를 실행
        if(mTaskRepeater.isRunning()) mTaskRepeater.stopRepeater();

        mTaskRepeater.setTask(mTask);
        mTaskRepeater.startRepeater();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Pause 때 타이머를 정지
        mTaskRepeater.stopRepeater();
    }


    /**
     * 캐논의 각도를 변경한다
     * @param degree 각도 변경값
     */
    public void changeDegree(final int degree) {
        mCannon.degree = degree;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCannonView.setRotation(degree);
            }
        });
    }

    /**
     * 총알 발사 메서드
     */
    public void shoot() { createBullet(); }

    /**
     * 총알 생성 메서드
     * 총알의 각도는 캐논의 각도와 동일
     */
    public void createBullet() {
        // 총알의 위치는 캐논의 정중앙
        final float bulletPosX = mCannonView.getX() + mCannonView.getWidth()/2f;
        final float bulletPosY = mCannonView.getY() + mCannonView.getHeight()/2f;

        // 매니저에 총알 추가
        StraightBullet bullet = new StraightBullet();
        bullet.setDegree(mCannon.degree);
        bullet.coord.x = bulletPosX;
        bullet.coord.y = bulletPosY;

        final int id = mBulletManager.makeBullet(bullet);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // xml 에 정의된 총알 이미지뷰를 로드 및 레이아웃에 등록
                final View bulletView = LayoutInflater
                        .from(MainActivity.this)
                        .inflate(R.layout.bullet_imageview, mAreaView, false);
                mAreaView.addView(bulletView);

                bulletView.setX(bulletPosX);
                bulletView.setY(bulletPosY);
                bulletView.setVisibility(View.INVISIBLE);

                // 뷰 관리 컬렉션에 추가
                mBulletViews.put(id, bulletView);
            }
        });

    }

    /**
     * 해당하는 id의 총알을 움직인다
     * @param id 움직이려는 총알 id
     */
    public void moveBullet(final int id) {
        final PointF coord = mBulletManager.moveBullets(id);
        if(coord == null) return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View bulletView = mBulletViews.get(id);
                if(bulletView == null) throw new NullPointerException("Target Bullet View is Null");

                bulletView.setX(coord.x);
                bulletView.setY(coord.y);
            }
        });
    }

    /**
     * 해당하는 id의 총알을 삭제한다
     * @param id 삭제할 총알 id
     */
    public void deleteBullet(final int id) {
        // 매니저로 데이터 모델에서 제거
        mBulletManager.deleteBullet(id);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 레이아웃에서 제거
                mAreaView.removeView( mBulletViews.get(id) );
                // 총알뷰 컬렉션에서 제거
                mBulletViews.remove(id);
            }
        });
    }

    /**
     * 해당하는 id의 총알을 보이게 할건지 말건지 여부를 정한다
     * @param id        여부를 정할 총알 id
     * @param visible   보이게 할 여부
     */
    public void setVisibleBullet(final int id, final boolean visible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View bulletView = mBulletViews.get(id);
                if(bulletView == null) throw new NullPointerException("Target Bullet View is Null");

                if(visible) bulletView.setVisibility(View.VISIBLE);
            }
        });
    }

    // 메인 로직
    // 태스크 한번 실행될때 마다 모든 총알을 감시하고 움직인다
    private final Runnable mTask = new Runnable() {
        @Override
        public void run() {
            for (Map.Entry<Integer, View> entry : mBulletViews.entrySet()) {
                int id = entry.getKey();
                Bullet bullet = mBulletManager.getBullet(id);

                float minX = mArea.topLeft.x - mArea.margin;
                float minY = mArea.topLeft.y - mArea.margin;
                float maxX = mArea.bottomRight.x + mArea.margin;
                float maxY = mArea.bottomRight.y + mArea.margin;

                // 총알이 영역을 빠져나갔으면 삭제
                if( !(minX < bullet.coord.x && minY < bullet.coord.y
                        && bullet.coord.x < maxX && bullet.coord.y < maxY) ) {
                    deleteBullet(id);
                    continue;
                }

                // 총알이 충분히 움직였을때 보이게 한다
                if(bullet.visibleDicisionNumber == bullet.moveCount) {
                    setVisibleBullet(id, true);
                }

                // 총알 한번 움직이기
                moveBullet(id);
            }
        }
    };
}
