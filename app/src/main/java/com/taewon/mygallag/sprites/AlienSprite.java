package com.taewon.mygallag.sprites;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.taewon.mygallag.MainActivity;
import com.taewon.mygallag.SpaceInvadersView;
import com.taewon.mygallag.items.HealitemSprite;
import com.taewon.mygallag.items.SpeedItemSprite;

import java.util.ArrayList;
import java.util.Random;

public class AlienSprite extends Sprite { // 외계인
    private Context context;
    private SpaceInvadersView game;
    ArrayList<AlienShotSprite> alienShotSprites; //하나가 아니라 ArrayList에 넣었다
    Handler fireHandler = null;
    boolean isDestroyed = false;


    public AlienSprite(Context context, SpaceInvadersView game, int resId, int x, int y) {
        //외계인 만들기
        super(context, resId, x, y);
        this.context = context;
        this.game = game;
        alienShotSprites = new ArrayList<>();
        Random r = new Random();
        int randomDx = r.nextInt(5);
        int randomDy = r.nextInt(5);
        if (randomDy <= 0) dy = 1;
        dx = randomDx;
        dy = randomDy;

        //스레드 충돌방지를 위해 main 스레드로 전달하기 위한 Handler 생성
        fireHandler = new Handler(Looper.getMainLooper());
        fireHandler.postDelayed(
                //delay주는 함수
                new Runnable() {
                    @Override
                    public void run() {
                        Log.d("run", "동작");
                        Random r = new Random();
                        boolean isFire = r.nextInt(100) + 1 <= 30;
                        if (isFire && !isDestroyed) {
                            fire(); // 총알을 계속 뿌려주는 거
                            fireHandler.postDelayed(this, 1000);
                        }
                    }
                }, 1000);
    }

    @Override
    public void move(){

        super.move();
        if(((dx<0) && (x<10)) || ((dx>0) && (x>800))){
            dx = -dx;
            if(y > game.screenH){game.removeSprite(this);} // SpaceInvadersView
        }
    }



    @Override
    public  void handleCollision(Sprite other) {
        if(other instanceof ShotSprite){
            game.removeSprite(other);
            game.removeSprite(this);
            destroyAlien();
            return;
        }
        if(other instanceof  SpecialshotSprite){
            game.removeSprite(this);
            destroyAlien();
            return;
        }
    }

    private void destroyAlien(){
        isDestroyed = true;
        game.setCurrEnemyCount(game.getCurrEnemyCount()-1);
        for(int i =0; i<alienShotSprites.size(); i++)
            game.removeSprite(alienShotSprites.get(i));
        spawnHealItem();
        spawnPowerItem();
        spawnSpeedItem();
        game.setScore(game.getScore() + 1);
        MainActivity.scoreTv.setText(Integer.toString(game.getScore()));
    }

    private void fire(){
        AlienShotSprite alienShotSprite= new AlienShotSprite(context, game, getX(), getY()+30, 16);
        alienShotSprites.add(alienShotSprite); // AlienShotSprite 를 위한 ArrayList에 추가
        game.getSprites().add(alienShotSprite); // SpaceInvadersView 의 ArrayList에 추가
    }

    private void spawnSpeedItem(){
        Random r = new Random();
        int speedItemDrop = r.nextInt(100) +1;
        if(speedItemDrop <=5){
            int dx = r.nextInt(10) +1;
            int dy = r.nextInt(10) +5;
            game.getSprites().add(new SpeedItemSprite(context, game,
                    (int)this.getX(), (int)this.getY(), dx, dy));
        }
    }

    private void spawnPowerItem(){
        Random r = new Random();
        int powerItemDrop = r.nextInt(100) +1;
        if(powerItemDrop <=3){
            int dx = r.nextInt(10) +1;
            int dy = r.nextInt(10) +10;
            game.getSprites().add(new SpeedItemSprite(context, game,
                    (int)this.getX(), (int)this.getY(), dx, dy));
        }

    }

    private void spawnHealItem(){
        Random r = new Random();
        int powerItemDrop = r.nextInt(100) +1;
        if(powerItemDrop <=5){
            int dx = r.nextInt(10) +1;
            int dy = r.nextInt(10) +10;
            game.getSprites().add(new HealitemSprite(context, game,
                    (int)this.getX(), (int)this.getY(), dx, dy));
        }

    }

}


