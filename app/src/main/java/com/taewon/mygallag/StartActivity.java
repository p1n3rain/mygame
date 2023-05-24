package com.taewon.mygallag;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity { // activity_start의 화면 (맨 처음 시작 화면 )
    int characterId, effectId;
    ImageButton startBtn;
    TextView guideTv;
    MediaPlayer mediaPlayer;
    ImageView imgView[] = new ImageView[8];
    Integer img_id[] = {R.id.ship_001, R.id.ship_002, R.id.ship_003, R.id.ship_004, R.id.ship_005, R.id.ship_006, R.id.ship_007, R.id.ship_008};
    Integer img[] = {R.drawable.ship_0000, R.drawable.ship_0001, R.drawable.ship_0002, R.drawable.ship_0003,
            R.drawable.ship_0004, R.drawable.ship_0005, R.drawable.ship_0006, R.drawable.ship_0007};
    SoundPool soundPool;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Activity가 생성될 때 실행됨
        super.onCreate(savedInstanceState); // super 클래스 호출 (Activity를 구현하는 데 필요한 과정)
        setContentView(R.layout.activity_start); // setContentView() 메소드 (onCreate()안에서 activity_start.xml의 레이아웃을 전달하여 화면을 정의함)

        //mediaPlayer는 긴 사운드 재생에 효과적, soundPool은 짧은 사운드 재생에 효과적
        mediaPlayer = MediaPlayer.create(this, R.raw.robby_bgm); // 음원을 재생하는 mediaPlayer (raw/robby_bgm 을 재생함)
        mediaPlayer.setLooping(true); // 반복 설정 (MediaPlayer에서 제공하는 함수)
        mediaPlayer.start(); // 재생 시작
        soundPool = new SoundPool(5, AudioManager.USE_DEFAULT_STREAM_TYPE, 0);
        // soundPool : 음원 로딩 class, (총 플레이 가능한 사운드 개수 5, 스트림 타입(기본), 음질(기본, 5로 갈수록 좋아짐))
        effectId = soundPool.load(this, R.raw.reload_sound, 1);
        // soundPool에 사용할 오디오 파일을 load, (현재 앱 위치, load할 오디오 파일 주소 값, 우선순위)
        startBtn = findViewById(R.id.startBtn); // findViewById() 메소드 : 레이아웃에 있는 뷰를 리소스 id를 통해서 원하는 뷰 객체에 가져옴
        guideTv = findViewById(R.id.guideTv);

        for (int i = 0; i < imgView.length; i++) { // i가 초깃값 0부터 하나씩 증가시켜서 imgView의 길이보다 작을 때까지 수행
            imgView[i] = findViewById(img_id[i]); // 여러 개의 리소스를 불러오는
            int index = i; // 배열 값
            imgView[i].setOnClickListener(new View.OnClickListener() { // setOnClickListener()
                @Override
                public void onClick(View view) {
                    characterId = img[index];
                    startBtn.setVisibility(View.VISIBLE);
                    // setVisivility(), (VISIBLE:보이게 함, INVISIBLE:가림, GONE:가림(공간차지 X)
                    // 이미지(비행기)를 누르면 startBtn 이 보이게 함
                    startBtn.setEnabled(true);
                    // setEnabled : Button 객체를 활성화/비활성화 하는 함수, (true면 활성화, false면 비활성화)
                    startBtn.setImageResource(characterId); // setImageResource(), startBtn에 원하는 이미지 띄워줌
                    guideTv.setVisibility(View.INVISIBLE); // guideTv를 가림
                    soundPool.play(effectId, 1, 1, 0, 0, 1.0f);
                    // soundPool의 메소드 play, (load를 통해 받은 effectId, 좌측볼륨, 우측볼륨, 우선순위, 반복횟수, 재생속도)
                }
            });
        }
       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT); //화면 세로로 돌리기
        init(); // 화면 전환
    }

    private void init() {
        findViewById(R.id.startBtn).setVisibility(View.GONE); // startBtn 안보이게 함
        findViewById(R.id.startBtn).setEnabled(false); // startBtn 비활성화 함
        findViewById(R.id.startBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                // Intent : activity 전환이 일어날 때 호출이나 메세지를 전달하는 매개체
                // StartActivity -> MainActivity
                intent.putExtra("character", characterId);
                // intent로 activity 이동을 하면서 값을 전달 (전달할 변수명, 해당 변수를 통해 전달할 값)
                startActivity(intent); // 새 인스턴스(다른 activity)를 시작하기 위해 intent를 startActivity로 전달
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() { //onStop()이던 상태가 완전히 제거되는 단계 / Activity가 소멸되기 직전에 호출됨
        super.onDestroy();
        if (mediaPlayer != null) { // mediaPlayer가 비어있지 않으면
            mediaPlayer.release(); //미디어 플레이어를 소멸시키는 작업
            mediaPlayer = null;
        }
    }
}