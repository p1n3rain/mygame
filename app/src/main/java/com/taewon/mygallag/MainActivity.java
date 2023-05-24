package com.taewon.mygallag;



import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity { // activity_main 의 화면

    private Intent userIntent;
    ArrayList<Integer> bgMusicList;
    public static SoundPool effectSound;
    public static float effectVolumn;
    ImageButton specialShotBtn;
    public static ImageButton fireBtn, reloadBtn;
    JoystickView joyStick;
    public static TextView scoreTv;
    LinearLayout gameFrame;

    ImageView pauseBtn;
    public static LinearLayout lifeFrame;
    SpaceInvadersView spaceInvadersView;
    public static MediaPlayer bgMusic;
    int bgMusicIndex;
    public static TextView bulletCount;
    private static ArrayList<Integer> effectSoundList;
    public static final int PLAYER_SHOT =0;
    public static final int PLAYER_HURT =1;
    public static final int PLAYER_RELOAD =2;
    public static final int PLAYER_GET_ITEM =3;



    @Override
    protected void onCreate(Bundle savedInstanceState) { // Activity가 생성될 때 실행됨, onCreate()가 완료되면 -> onStart()가 호출됨
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // setContentView() 메소드는 activity 클래스에서 onCreate()안에 들어감, 레이아웃을 보여주기 위해서 사용


        userIntent = getIntent(); // 화면 전환을 위한 intent
        bgMusicIndex = 0;
        bgMusicList = new ArrayList<Integer>(); // 음악 넣을 arrylist생성
        bgMusicList.add(R.raw.main_game_bgm1);
        bgMusicList.add(R.raw.main_game_bgm2);
        bgMusicList.add(R.raw.main_game_bgm3);

        effectSound = new SoundPool(5, AudioManager.USE_DEFAULT_STREAM_TYPE, 0); // SoundPool : 음원 로딩 class, 총 플레이 가능한 사운드 개수 5
        effectVolumn = 1;

        specialShotBtn = findViewById(R.id.specialShotBtn); //번개
        joyStick = findViewById(R.id.joyStick);
        scoreTv = findViewById(R.id.score); //TextView
        fireBtn = findViewById(R.id.fireBtn); //ImageButton
        reloadBtn = findViewById(R.id.reloadBtn);//ImageButton
        gameFrame = findViewById(R.id.gameFrame); //첫번째 LinearLayout
        pauseBtn = findViewById(R.id.pauseBtn);//ImageButton
        lifeFrame = findViewById(R.id.lifeFrame);//윗부분 LinearLayout

        init(); // 생성자를 통해 인스턴스가 만들어질 때 호출되는 함수, 매개변수가 없고 반환되는 값이 없음
        setBtnBehavior(); // 조이스틱 작동함수 실행, Behavior : 자식 view들 간의 상호작용을 위해 사용

    }

    @Override
    protected void onResume() { // 사용자와 상호작용 하는 단계 / Activity 스택의 Top에 위치 / 주로 어플 기능이 onResume()에 설정됨
        super.onResume();
        bgMusic.start();
        spaceInvadersView.resume();
    }

    private void init(){
        Display display = getWindowManager().getDefaultDisplay();  // getWindowManger / view의 display를 얻어온다.
        Point size = new Point(); // 화면 사이즈 구하기
        display.getSize(size);

        spaceInvadersView = new SpaceInvadersView(this,
                userIntent.getIntExtra("character", R.drawable.ship_0000),size.x, size.y); // 데이터를 받는 getIntExtra(받는 변수 명, 기본 값)
        gameFrame.addView(spaceInvadersView); // addView : 액티비티가 호출하는 xml 레이아웃에 소스코드로 직접 view를 만들어서 add한다. / 프레임에 만든 아이템들 넣기

        //음악 바꾸기
        changeBgMusic();
        bgMusic.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // setOnCompletionListener : 미디어가 모두 재생되었을 때 설정하는 이벤트 / 음악이 끝나면
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                changeBgMusic();
            }
        });

        bulletCount = findViewById(R.id.bulletCount);   //총알 개수

        // spaceInvadersView의 getPlayer() 구현
        bulletCount.setText(spaceInvadersView.getPlayer().getBulletsCount() + "/30");  // 30/30표시
        scoreTv.setText(Integer.toString(spaceInvadersView.getScore())); //score:0 표시

        effectSoundList = new ArrayList<>();
        effectSoundList.add(PLAYER_SHOT, effectSound.load(MainActivity.this, R.raw.player_shot_sound, 1));
        effectSoundList.add(PLAYER_HURT, effectSound.load(MainActivity.this, R.raw.player_hurt_sound, 1));
        effectSoundList.add(PLAYER_RELOAD, effectSound.load(MainActivity.this, R.raw.reload_sound, 1));
        effectSoundList.add(PLAYER_GET_ITEM, effectSound.load(MainActivity.this, R.raw.player_get_item_sound, 1));
        bgMusic.start();  //음악이 바뀌면서 재생
    }
    private void changeBgMusic(){
        bgMusic = MediaPlayer.create(this, bgMusicList.get(bgMusicIndex)); // MediaPlayer : 미디어 파일을 재생해줌 / 음악 하나 가져와 만들기
        bgMusic.start();
        bgMusicIndex++; //음악 바꾸기 위해 증가해 놓기
        bgMusicIndex = bgMusicIndex % bgMusicList.size(); //음악 개수만큼만 바뀌게 하기 위해
    }
    @Override
    protected void onPause() { // 일시 정지시
        super.onPause();
        bgMusic.pause();
        spaceInvadersView.pause();
    }

    public static void effectSound(int flag){ // Activity의 중복문제나 흐름을 제어해주고 싶을 때 Flag를 사용
    effectSound.play(effectSoundList.get(flag), effectVolumn, effectVolumn,
            0, 0, 1.0f);
    //Soundpool 실행
    }

private void setBtnBehavior() {
    joyStick.setAutoReCenterButton(true);
    joyStick.setOnKeyListener(new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            Log.d("keycode", Integer.toString(i));
            return false;
        }
    });

    // 조이스틱 이동 방향으로 비행기가 이동하게 한다
    joyStick.setOnMoveListener(new JoystickView.OnMoveListener() {
        @Override
        public void onMove(int angle, int strength) {
            Log.d("angle", Integer.toString(angle)); // 각도
            Log.d("force", Integer.toString(strength)); // 세기 값
            if (angle > 67.5 && angle < 112.5) {
                //위
                spaceInvadersView.getPlayer().moveUp(strength / 10);
                spaceInvadersView.getPlayer().resetDx();
            } else if (angle > 247.5 && angle < 292.5) {
                //아래
                spaceInvadersView.getPlayer().moveDown(strength / 10);
                spaceInvadersView.getPlayer().resetDx();
            } else if (angle > 112.5 && angle < 157.5) {
                //왼쪽 대각선 위
                spaceInvadersView.getPlayer().moveUp(strength / 10 * 0.5);
                spaceInvadersView.getPlayer().moveLeft(strength / 10 * 0.5);
            } else if (angle > 157.5 && angle < 202.5) {
                // 왼쪽
                spaceInvadersView.getPlayer().moveLeft(strength / 10);
                spaceInvadersView.getPlayer().resetDy();
            } else if (angle > 202.5 && angle < 247.5) {
                //왼쪽 대각선 아래
                spaceInvadersView.getPlayer().moveLeft(strength / 10 * 0.5);
                spaceInvadersView.getPlayer().moveDown(strength / 10 * 0.5);
            } else if (angle > 22.5 && angle < 67.5) {
                //오른쪽 대각선 위
                spaceInvadersView.getPlayer().moveUp(strength / 10 * 0.5);
                spaceInvadersView.getPlayer().moveRight(strength / 10 * 0.5);
            } else if (angle > 337.5 || angle < 22.5) {
                //오른쪽
                spaceInvadersView.getPlayer().moveRight(strength / 10);
                spaceInvadersView.getPlayer().resetDy();
            } else if (angle > 292.5 && angle < 337.5) {
                //오른쪽 아래
                spaceInvadersView.getPlayer().moveRight(strength / 10 * 0.5);
                spaceInvadersView.getPlayer().moveDown(strength / 10 * 0.5);
            }
        }});


            //총알 버튼 눌렀을 때
            fireBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spaceInvadersView.getPlayer().fire();
                }
                //SpaceInvadersView -> StarshipSprite -> fire()
            });

            //재장전 버튼 눌렀을 때
            reloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spaceInvadersView.getPlayer().reloadBullets();
                    //SpaceInvadersView -> StarshipSprite -> reloadBullets()
                }
            });

            pauseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spaceInvadersView.pause(); // spaceInvadersView 일시정지
                    PauseDialog pauseDialog = new PauseDialog(MainActivity.this);
                    pauseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            spaceInvadersView.resume(); // spaceInvadersView 종료
                        }
                    });
                    pauseDialog.show(); // pauseDialog 띄움
                }
            });

            specialShotBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (spaceInvadersView.getPlayer().getSpecialShotCount() >= 0)
                        spaceInvadersView.getPlayer().specialShot();
                }

            });
        }
    }