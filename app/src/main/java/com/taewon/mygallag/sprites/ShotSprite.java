package com.taewon.mygallag.sprites;


import android.content.Context;

import com.taewon.mygallag.SpaceInvadersView;

public class ShotSprite extends Sprite{ // player 공격
    private SpaceInvadersView game;

    public ShotSprite(Context context, SpaceInvadersView game,
                      int resId, float x, float y, int dy){
        super(context, resId, x, y);
        this.game = game;
        setDy(dy);
    }
}
