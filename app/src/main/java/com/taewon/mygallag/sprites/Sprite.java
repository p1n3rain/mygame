package com.taewon.mygallag.sprites;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Sprite  { // Sprite 는 최상위 슈퍼클래스
    protected float x, y;
    protected int width, height;
    protected float dx, dy;
    private Bitmap bitmap;
    protected int id;
    private RectF rect;
    public Sprite(Context context,int resourceId, float x, float y){
        this.id=resourceId;
        this.x = x; this.y = y;
        bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        rect = new RectF();
    }
    public int getWidth(){return width;}
    public int getHeight(){return height;}
    public void draw(Canvas canvas, Paint paint){ canvas.drawBitmap(bitmap, x,y,paint);}
    public void move(){
        x=x+dx; y=y+dy;
        rect.left=x; rect.right = x+width;
        rect.top = y; rect.bottom = y + height;
    }
    public float getX() {return x;}
    public float getY() {return y;}
    public float getDx() {return dx;}
    public float getDy() {return dy;}
    public void setDx(float dx) { this.dx = dx;}
    public void setDy(float dy) { this.dy = dy;}
    public RectF getRect() {return rect;}
    public boolean checkCollision(Sprite other){ // 교차하는 점을 리턴해줌
        return RectF.intersects(this.getRect(), other.getRect());
    }
    public void handleCollision(Sprite other){ }//충돌처리 위한
    public Bitmap getBitmap(){ return bitmap; }
    public void setBitmap(Bitmap bitmap) {this.bitmap = bitmap;}

}
