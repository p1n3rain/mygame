package com.taewon.mygallag;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

public class PauseDialog extends Dialog { // pause_dialog의 화면

    RadioGroup bgMusicOnOff;
    RadioGroup effectSoundOnOff;



public PauseDialog(@NonNull Context context){
    super (context);
    setContentView(R.layout.pause_dialog); // setContentView() 메소드, pause_dialog_xml 구현
    bgMusicOnOff = findViewById(R.id.bgMusicOnOff);
    effectSoundOnOff = findViewById(R.id.effectSoundOnOff);
    init(); // 화면 전환
}

public void init(){
    bgMusicOnOff.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // 배경음악
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch (checkedId){
                case R.id.bgMusicOn:
                    MainActivity.bgMusic.setVolume(1, 1);
                    break;
                case R.id.bgMusicOff:
                    MainActivity.bgMusic.setVolume(0, 0);
                    break;
            }
        }
    });
    effectSoundOnOff.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // 효과음
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch (checkedId) {
                case R.id.effectSoundOn:
                    MainActivity.effectVolumn = 1.0f;
                    break;
                case R.id.effectSoundOff:
                    MainActivity.effectVolumn = 0;
                    break;
            }
        }
    });
    findViewById(R.id.dialogCancelBtn).setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) { dismiss(); } // dismiss : dialog를 종료하는 메소드, cancel보다 안전하게 화면 종료
        // dismiss는 다이얼로그의 버튼을 클릭하여 종료된 경우에 호출된다.(즉, 안전하게 종료한 경우)
        // 하지만, cancel은 뒤로가기 버튼을 눌러서 다이얼로그가 종료된 경우에 호출된다.
        });

    findViewById(R.id.dialogOkBtn).setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) { dismiss();}
    });
    }

}
