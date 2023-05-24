package com.taewon.mygallag;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity { // game_over_dialog의 화면

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over_dialog); // setContentView() 메소드
        init(); // 화면 전환
    }

    @Override
    public void onBackPressed() {

    }

    private void init(){
        findViewById(R.id.goMainBtn).setOnClickListener(new View.OnClickListener() { // 처음으로 < 버튼
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultActivity.this,
                        StartActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ((TextView)findViewById(R.id.userFinalScoreText)).setText(
                getIntent().getIntExtra("score", 0)+"");
    }
}
