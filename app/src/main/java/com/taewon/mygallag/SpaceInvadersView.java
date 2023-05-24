package com.taewon.mygallag;


import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.taewon.mygallag.sprites.AlienSprite;
import com.taewon.mygallag.sprites.Sprite;
import com.taewon.mygallag.sprites.StarshipSprite;

import java.util.ArrayList;
import java.util.Random;

public class SpaceInvadersView extends SurfaceView implements Runnable, SurfaceHolder.Callback { // 게임 화면
    //SurfaceView는 스레드를 이용해 강제로 화면에 그려주므로 View보다 빠르다. 애니메이션, 영상 처리에 이용
    //SurfaceHolder.Callback Surface의 변화감지를 위해 필요. 지금처럼 SurfaceView와 거의 같이 사용한다.
    //SurfaveView는 View를 상속받는 클래스, 스레드를 이용해 강제로 화면에 그림으로써 일반 View의 onDraw 메소드 호출보다 빠르다.
    //SurfaceView에서 직접 내용을 뿌려주지는 않고, SurfaceHolder를 통해 데이터를 받아서 처리 (SurfaceHolder가 Surface에 접근하여 화면을 처리)

    private static int MAX_ENEMY_COUNT = 10;
    private Context context;
    private int characterId;
    private SurfaceHolder ourHolder; //화면에 그리는데 View보다 빠르게 그려준다
    private Paint paint; // canvas위에 paint로 좌표 잡아서 그림
    public int screenW, screenH;
    private Rect src, dst; //사각형 그리는 클래스 (원본, 사본)
    private ArrayList sprites = new ArrayList();
    private Sprite starship;
    private  int score, currEnemyCount;
    private Thread gameThread=null;
    private volatile boolean running; //휘발성 부울 함수

    private Canvas canvas;

    int mapBitmapY = 0;



    public SpaceInvadersView(Context context, int characterId, int x, int y){  // MainActivity, Intent(비행기 = player), point x,y가 넘어온다
        // Context : 현재 사용되고 있는 애플리케이션(또는 액티비티)에 대한 포괄적인 정보를 지니고 있는 객체
        super(context);
        this.context = context;
        this.characterId = characterId;
        ourHolder = getHolder();  // 현재 SurfaceView를 리턴받는다.
        paint = new Paint();
        screenW = x;
        screenH= y;  //받아온 x, y
        src = new Rect();  //원본 사각형
        dst = new Rect();  //사본 사각형
        dst.set(0,0,screenW,screenH); //시작 x,y와 끝 x,y
        startGame();
    }

    private void startGame(){
        sprites.clear();  // ArrayList지우기
        initSprites();  // ArrayList에 player아이템들(비행기) 추가 하기
        score =0;
    }

    public void endGame(){
        Log.e("GameOver", "GameOver");
        Intent intent = new Intent(context, ResultActivity.class); // Result Activity로 화면 전환
        intent.putExtra("score", score);
        context.startActivity(intent);
        gameThread.stop();
    }

    public void removeSprite(Sprite sprite) {sprites.remove(sprite);}

    private void initSprites(){  //sprite 초기화
        starship = new StarshipSprite(context, this, characterId,
                screenW / 2, screenH - 400, 1.5f);//StarshipSprite생성 아이템들 생성
        sprites.add(starship); //AllayList에 추가
        spawnEnemy();
        spawnEnemy();
    }  // Main에서 외계인, 비행기 처음 생성되는 부분
    public void spawnEnemy(){
        Random r = new Random();
        int x = r.nextInt(300)+ 100;
        int y = r.nextInt(300)+ 100;
        //외계인 아이템
        Sprite alien = new AlienSprite(context, this, R.drawable.ship_0002,
                100 + x, 100 + y);
        sprites.add(alien);
        currEnemyCount++;  //외계인 아이템 개수 증가
    }

    public ArrayList getSprites() {return sprites;}

    public void resume(){  //사용자가 만드는 resume()함수
        running = true;
        gameThread = new Thread(this);
        gameThread.start(); // run을 실행시킴
    }

    // Sprite 를 StarshipSprite로 형변환하여 리턴하기
    public StarshipSprite getPlayer(){return (StarshipSprite) starship;}
    public int getScore() { return score;}
    public void setScore(int score) {this.score = score;}

    public void setCurrEnemyCount(int currEnemyCount) { this.currEnemyCount = currEnemyCount;}

    public int getCurrEnemyCount() {return currEnemyCount;}
    // 숫자가 들어오면 넣어주고 빼주고 함

    public void pause() {
        running = false;
        try{
            gameThread.join(); // 쓰레드 종료 대기하기
        }catch (InterruptedException e){
        }
    }





    @Override
    public void run() {
        while (running) {
            Random r = new Random();
            boolean isEnemySpawn = r.nextInt(100) + 1 < (getPlayer().speed +
                    (int) (getPlayer().getPowerLevel() / 2)); // 1~100의 랜덤 수가 player의 스피드+player의 powerlever/2보다 작을 때 실행
            if(isEnemySpawn && currEnemyCount < MAX_ENEMY_COUNT) spawnEnemy(); // ??? 추가

            for(int i = 0; i < sprites.size(); i++) {
                Sprite sprite = (Sprite) sprites.get(i); //ArrayList에꺼 하나씩 가져와서 움직이기
                sprite.move();
            }

            for (int p = 0; p < sprites.size(); p++) {
                for (int s = p + 1; s <sprites.size(); s++) {
                    try {
                        Sprite me = (Sprite) sprites.get(p);
                        Sprite other = (Sprite) sprites.get(s);
                        //충돌체크
                        if (me.checkCollision(other)) {
                            me.handleCollision(other);
                            other.handleCollision(me);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            draw();
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                }
            }
        }

        public void draw(){
        if(ourHolder.getSurface().isValid()){
            canvas= ourHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            mapBitmapY++;
            if(mapBitmapY<0) mapBitmapY = 0; // Y값이 마이너스가 되면 화면에 안보여서 0보다 작으면 다시 0으로 가게 함
            paint.setColor(Color.BLUE);
            for(int i=0; i <sprites.size(); i++){
                Sprite sprite = (Sprite) sprites.get(i);
                sprite.draw(canvas, paint);
            }
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }




    // SurfaceView로부터 상속받을 경우 디폴트로 구현해야 할 메소드
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) { startGame(); }
    // View가 생성될 때 호출됨

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) { }
    // View가 종료될 때 호출됨

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {}
    // View가 변경될 때 호출됨
}
