package com.taewon.mygallag.sprites;


import android.content.Context;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.taewon.mygallag.MainActivity;
import com.taewon.mygallag.R;
import com.taewon.mygallag.SpaceInvadersView;
import com.taewon.mygallag.items.HealitemSprite;
import com.taewon.mygallag.items.PowerItemSprite;
import com.taewon.mygallag.items.SpeedItemSprite;

import java.util.ArrayList;

public class StarshipSprite extends Sprite{ // Player
    Context context;
    SpaceInvadersView game;  // 게임 객체. 내 비행기
    public float speed;
    private int bullets, life=3, powerLevel;
    private int specialShotCount;

    private boolean isSpacialShooting;
    private static ArrayList<Integer> bulletSprites = new ArrayList<Integer>();

    private final static float MAX_SPEED = 3.5f;

    private final static int MAX_HEART = 3;
    private RectF rectF; // 사각형 그리는 객체

    private boolean isReloading = false;


    public StarshipSprite(Context context, SpaceInvadersView game,
                          int resId, int x, int y, float speed){
        super(context, resId, x, y); // super class는 Sprite
        this.context = context; // context는 구현이 Android 시스템에 의해 제공되는 추상 클래스
        this.game = game;
        this.speed = speed;
        init();
    } // 생성자는 최소한으로 하고 나머지는 함수로 빼는 게 좋다

    public void init(){
        dx=dy=0; // 움직임 x
        bullets=30; // 총알 30
        life=3; // 생명 3
        specialShotCount = 3; // specialShotCount는 3번만 쓸 수 있다
        powerLevel=0; // powerLevel은 0
        Integer[] shots = {R.drawable.shot_001,R.drawable.shot_002,R.drawable.shot_003,R.drawable.shot_004,R.drawable.shot_005,R.drawable.shot_006,R.drawable.shot_007};
        for(int i =0; i<shots.length; i++){ // i가 초깃값 0부터 하나씩 증가시켜서 shots의 길이보다 작을 때까지 수행
            bulletSprites.add(shots[i]);
        }
    }

    @Override
    public void move() {
        // 벽에 부딪히면 못 가게 하기
        // dx와 dy 변수는 각자 수직 수평으로 스크롤이 얼마나 되었는지 그 양을 뜻함
        if((dx<0) && (x<120)) return;
        // dx가 0보다 작고(음수, 좌측 스크롤) x값이 120보다 작으면
        if((dx>0) && (x>game.screenW-120)) return;
        // dx가 0보다 크고(양수, 우측 스크롤) x값이 스크린 너비 - 120 보다 크면
        if((dy<0) && (y<120)) return;
        // dy가 0보다 작고(음수, 위로 스크롤) y값이 120보다 작으면
        if((dy>0) && (y>game.screenH-120)) return;
        // dy가 0보다 크고(양수, 아래로 스크롤) y값이 스크린 높이 - 120보다 크면
        super.move(); // 슈퍼클래스 가서 x, y 위치 다시 지정
    }

    //총알 개수 리턴
    public int getBulletsCount() {return bullets;}

    // 위, 아래, 오른쪽, 왼쪽 이동하기
    public void moveRight(double force) {setDx((float)(1*force*speed));} // dx가 양수, 오른쪽
    public void moveLeft(double force) {setDx((float)(-1*force*speed));} // dx가 음수, 왼쪽
    public void moveDown(double force) {setDy((float)(1*force*speed));} // dy가 양수, 아래쪽
    public void moveUp(double force) {setDy((float)(-1*force*speed));} // dy가 음수 위쪽
    public void resetDx() { setDx(0);} // dx값 0으로
    public void resetDy() { setDy(0);} // dy값 0으로

    //스피드 제어
    public void plusSpeed(float speed) {this.speed += speed;}

    //총알 발사
    public void fire(){
        if(isReloading | isSpacialShooting){return;} //
        MainActivity.effectSound(MainActivity.PLAYER_SHOT); // MainActivity의 effectSound에 PLAYER_SHOT 재생
        //ShotSprite 생성자구현
        ShotSprite shot = new ShotSprite(context, game, bulletSprites.get(powerLevel),
                getX()+10, getY()-30, -16);

        //SpaceInvadersView의 getSprites() 구현
        game.getSprites().add(shot);
        bullets--;

        // 확인
        MainActivity.bulletCount.setText(bullets+"/30"); // MainActivity의 bulletCount에 "총알수/30"
        Log.d("bullets", bullets+"/30");
        //Log.d(태그, 로그내용)-> 상태 확인용
        if(bullets ==0){ // 총알이 0이면
            reloadBullets(); // 재장전
            return;
        }
    }

    public void powerUp(){
        if(powerLevel >= bulletSprites.size() - 1){ // 파워 레벨이 총알 크기 - 1 보다 크거나 같으면
            game.setScore(game.getScore() +1); // 점수 추가
            MainActivity.scoreTv.setText(Integer.toString(game.getScore())); // MainActivity의 scoreTv에 점수 추가
            return;
        }
        powerLevel++; // 파워레벨 +
        MainActivity.fireBtn.setImageResource(bulletSprites.get(powerLevel));
        // MainActivity의 fireBtn에 powerLevel 이미지넣기
        MainActivity.fireBtn.setBackgroundResource(R.drawable.round_button_shape);
        //MainActivity의 fireBtn 배경사진 변경
    }

    // 총알 다시 셋팅
    public void reloadBullets(){
        isReloading = true; //
        MainActivity.effectSound(MainActivity.PLAYER_RELOAD); // MainActivity의 effectSound를 PLATER_RELOAD로
        MainActivity.fireBtn.setEnabled(false); // 총알 발사 버튼 비활성화
        MainActivity.reloadBtn.setEnabled(false); // 총알 재장전 버튼 비활성화
        // Thread sleep 사용하지 않고 지연시키는 클래스
        new Handler().postDelayed(new Runnable(){
            // 약간의 시간을 두고 실행시키고 싶을때, 딜레이를 주고 싶을때 Handler를 사용, 몇 초 뒤에 실행시키는 postDelayed
            @Override
            public void run(){
                bullets=30; // 총알 30발
                MainActivity.fireBtn.setEnabled(true); // 총알 발사 버튼 활성화
                MainActivity.reloadBtn.setEnabled(true); // 총알 재장전 버튼 활성화
                MainActivity.bulletCount.setText(bullets+"/30"); // "총알수/30"
                MainActivity.bulletCount.invalidate(); //화면 새로고침
                isReloading=false; //
            }
        }, 2000); // 2초 딜레이
    }

    //필살기
    public void specialShot(){
        specialShotCount--;
        //SpecialshotSprite 구현
        SpecialshotSprite shot = new SpecialshotSprite(context, game, R.drawable.laser,
                getRect().right - getRect().left, 0);

        //game -> SpaceInvadersView의 getSprites() : sprite에 shot 추가하기
        game.getSprites().add(shot);
    }

    public int getSpecialShotCount() {return  specialShotCount;}
    public boolean isSpacialShooting() {return  isSpacialShooting;}
    public void setSpecialShooting(boolean specialShooting)
    { isSpacialShooting=specialShooting;}

    //생명 잃었을 때
    public int getLife() { return life;} // 생명
   public void hurt(){
        life--;
        if(life<=0) { // 생명이 0보다 작거나 같으면
            ((ImageView) MainActivity.lifeFrame.getChildAt(life)).setImageResource(
                    (R.drawable.ic_baseline_favorite_border_24)); // MainActivity의 lifeFrame에 빈 하트
            game.endGame(); // SpaceInvadersView의 endGame() 에서 game종료시키기
            return;
        }
        Log.d("hurt", Integer.toString(life));  //생명 확인하기
        ((ImageView) MainActivity.lifeFrame.getChildAt(life)).setImageResource(
                R.drawable.ic_baseline_favorite_border_24); // MainActivity의 lifeFrame에 빈 하트

    }

    //생명 얻었을 때
    public void heal(){
        Log.d("heal", Integer.toString(life));
        if(life +1 >MAX_HEART){ // 생명+1이 최대 생명보다 높으면
            game.setScore(game.getScore() +1); // 1점 추가
            MainActivity.scoreTv.setText(Integer.toString(game.getScore())); // MainActivity의 scoreTv에 점수 추가
            return;
        }
        ((ImageView)MainActivity.lifeFrame.getChildAt(life)).setImageResource(
                (R.drawable.ic_baseline_favorite_24)); // MainActivity의 lifeFrame에 찬 하트 넣기
        life++;
    }

    //속도 올리기
    private void speedUp(){
        if(MAX_SPEED >= speed +0.2f)plusSpeed(0.2f); // 최대 스피드가 스피드+0.2보다 크거나 같으면 스피드 0.2 증가
        else{
            game.setScore(game.getScore()+1); // 1점 추가
            MainActivity.scoreTv.setText(Integer.toString(game.getScore())); // MainActivity의 scoreTv에 점수 추가
        }
    }

    //Sprite의 handleCollision()-> 충돌처리
    @Override
    public void handleCollision(Sprite other) {
        if (other instanceof AlienSprite) {
            // Alien을 맞으면
            game.removeSprite(other); // Alien을 지움
            MainActivity.effectSound(MainActivity.PLAYER_HURT); // MainActvity의 PLAYER_HURT 음원 재생
            hurt();
        }
        if (other instanceof SpeedItemSprite) {
            // 스피드 아이템을 맞으면
            game.removeSprite(other); // SpeedItem을 지움
            MainActivity.effectSound(MainActivity.PLAYER_GET_ITEM); // MainActvity의 PLAYER_GET_ITEM 음원 재생
            speedUp();
        }
        if (other instanceof AlienShotSprite) {
            // 총알 맞으면
            game.removeSprite(other); // AlienShot을 지움
            MainActivity.effectSound(MainActivity.PLAYER_HURT); // MainActvity의 PLAYER_HURT 음원 재생
            hurt();
        }
        if (other instanceof PowerItemSprite) {
            // Power 아이템을 맞으면
            game.removeSprite(other); // PowerItem을 지움
            MainActivity.effectSound(MainActivity.PLAYER_GET_ITEM); // MainActvity의 PLAYER_GET_ITEM 음원 재생
            powerUp();
        }
        if (other instanceof HealitemSprite) {
            // 생명 아이템을 맞으면
            game.removeSprite(other); // HealItem을 지움
            MainActivity.effectSound(MainActivity.PLAYER_GET_ITEM); // MainActvity의 PLAYER_GET_ITEM 음원 재생
            heal();

        }
    }
    public int getPowerLevel(){ return powerLevel; }

}
