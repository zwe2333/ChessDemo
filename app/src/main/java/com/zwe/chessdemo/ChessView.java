package com.zwe.chessdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asus on 2017/6/8.
 */

public class ChessView extends View{
    private int mPanelWidth;
    private float mLineHeight;
    private int maxLine=10;

    private Paint mPaint;
    private Bitmap mWhitePice;
    private Bitmap mBlackPice;
    private float ratioPieceOfLineHeight=3 * 1.0f / 4;

    private boolean isGameOver;
    public static int WHITE_WIN=0;
    public static int BLACK_WIN=1;
    private boolean isWhite=true;

    private List<Point> mWhiteArray=new ArrayList<>();
    private List<Point> mBlackArray=new ArrayList<>();

    private int mUnder;
    private onGameListener mListener;

    public ChessView(Context context) {
        this(context,null);
    }

    public ChessView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setOnGameListener(onGameListener listener){
        mListener=listener;
    }

    private void init() {
        mPaint=new Paint();
        mPaint.setColor(0X44ff0000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mWhitePice= BitmapFactory.decodeResource(getResources(),R.drawable.stone_w2);
        mBlackPice=BitmapFactory.decodeResource(getResources(),R.drawable.stone_b1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver){
            return false;
        }
        int action=event.getAction();
        if (action==MotionEvent.ACTION_UP){
            int x= (int) event.getX();
            int y= (int) event.getY();
            Point p=getVaLidPoint(x,y);

            if (mWhiteArray.contains(p)||mBlackArray.contains(p)){
                return false;
            }

            if (isWhite){
                mWhiteArray.add(p);
            }else {
                mBlackArray.add(p);
            }

            invalidate();

            isWhite=!isWhite;

        }
        return true;
    }

    private Point getVaLidPoint(int x, int y) {
        return new Point((int)(x/mLineHeight),(int) (y/mLineHeight));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);

        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int heightSize=MeasureSpec.getSize(heightMeasureSpec);

        int width=Math.min(widthSize,heightSize);

        if (widthMode==MeasureSpec.UNSPECIFIED){
            width=heightSize;
        }else if (heightMode==MeasureSpec.UNSPECIFIED){
            width=widthSize;
        }

        setMeasuredDimension(width,width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth=w;
        mLineHeight=mPanelWidth*1.0f/maxLine;
        mUnder=h-(h-mPanelWidth)/2;

        int pieceWidth= (int) (mLineHeight*ratioPieceOfLineHeight);

        mWhitePice=Bitmap.createScaledBitmap(mWhitePice,pieceWidth,pieceWidth,false);
        mBlackPice=Bitmap.createScaledBitmap(mBlackPice,pieceWidth,pieceWidth,false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBroad(canvas);
        drawPiece(canvas);
        checkGameOver();
    }

    private void checkGameOver() {
        boolean whiteWin=checkFiveInLine(mWhiteArray);
        boolean blackWin=checkFiveInLine(mBlackArray);

        if (whiteWin||blackWin){
            isGameOver=true;
            if (mListener!=null){
                mListener.onGameOver(whiteWin ? WHITE_WIN : BLACK_WIN);
            }
        }
    }

    public int getUnder(){
        return mUnder;
    }

    private boolean checkFiveInLine(List<Point> mArray) {
        for (Point p:mArray){
            int x=p.x;
            int y=p.y;

            boolean win_flag=checkHorizontal(x,y,mArray)||checkVertical(x,y,mArray)
                    ||checkLeftDiagonal(x,y,mArray)||checkRightDiagonal(x,y,mArray);

            if (win_flag){
                return true;
            }
        }


        return false;
    }

    private boolean checkRightDiagonal(int x, int y, List<Point> mArray) {
        int count=1;
        for (int i=1;i<5;i++){
            if (mArray.contains(new Point(x-i,y-i))){
                count++;
            }else {
                break;
            }
        }
        if (count==5){
            return true;
        }
        for (int i=1;i<5;i++){
            if (mArray.contains(new Point(x+i,y+i))){
                count++;
            }else {
                break;
            }
            if (count==5){
                return true;
            }
        }
        return false;
    }

    private boolean checkLeftDiagonal(int x, int y, List<Point> mArray) {
        int count=1;
        for (int i=1;i<5;i++){
            if (mArray.contains(new Point(x-i,y+i))){
                count++;
            }else {
                break;
            }
        }
        if (count==5){
            return true;
        }
        for (int i=1;i<5;i++){
            if (mArray.contains(new Point(x+i,y-i))){
                count++;
            }else {
                break;
            }
            if (count==5){
                return true;
            }
        }
        return false;
    }

    private boolean checkVertical(int x, int y, List<Point> mArray) {
        int count=1;
        for (int i=1;i<5;i++){
            if (mArray.contains(new Point(x,y+i))){
                count++;
            }else {
                break;
            }
        }
        if (count==5){
            return true;
        }
        for (int i=1;i<5;i++){
            if (mArray.contains(new Point(x,y-1))){
                count++;
            }else {
                break;
            }
            if (count==5){
                return true;
            }
        }
        return false;
    }

    private boolean checkHorizontal(int x, int y, List<Point> mArray) {
        int count=1;
        for (int i=1;i<5;i++){
            if (mArray.contains(new Point(x+i,y))){
                count++;
            }else {
                break;
            }
        }
        if (count==5){
            return true;
        }
        for (int i=1;i<5;i++){
            if (mArray.contains(new Point(x-i,y))){
                count++;
            }else {
                break;
            }
            if (count==5){
                return true;
            }
        }

        return false;
    }

    private void drawPiece(Canvas canvas) {
        int n1=mWhiteArray.size();
        int n2=mBlackArray.size();
        for (int i=0;i<n1;i++){
            Point whitePoint=mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePice,
                    (whitePoint.x+(1-ratioPieceOfLineHeight)/2)*mLineHeight,
                    (whitePoint.y+(1-ratioPieceOfLineHeight)/2)*mLineHeight,null);
        }
        for (int i=0;i<n2;i++){
            Point blackPoint=mBlackArray.get(i);
            canvas.drawBitmap(mBlackPice,
                    (blackPoint.x+(1-ratioPieceOfLineHeight)/2)*mLineHeight,
                    (blackPoint.y+(1-ratioPieceOfLineHeight)/2)*mLineHeight,null);
        }

    }

    private void drawBroad(Canvas canvas) {
        int w=mPanelWidth;
        float lineHeight=mLineHeight;
        int startX= (int) (lineHeight/2);
        int endX= (int) (w-lineHeight/2);
        for (int i=0;i<maxLine;i++){
            int y= (int) ((i+0.5)*lineHeight);

            canvas.drawLine(startX,y,endX,y,mPaint);
            canvas.drawLine(y,startX,y,endX,mPaint);
        }
    }
    protected void restartGame(){
        mWhiteArray.clear();
        mBlackArray.clear();
        isGameOver=false;
        isWhite=false;
        invalidate();
    }
}
