package com.taewon.mygallag.sprites;

import android.content.Context;

import com.taewon.mygallag.R;
import com.taewon.mygallag.SpaceInvadersView;

public class AlienShotSprite extends Sprite{ // 외계인 공격
    private Context context;
    private SpaceInvadersView game;
    public AlienShotSprite(Context context, SpaceInvadersView game, float x, float y, int dy){
        super(context, R.drawable.shot_001, x, y);
        this.game = game;
        this.context = context;
        setDy(dy);
    }
}
