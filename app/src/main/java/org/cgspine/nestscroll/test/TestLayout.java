package org.cgspine.nestscroll.test;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * Created by Yale on 2017/4/15.
 */

public class TestLayout extends ViewGroup {

    private static final String TAG="TestLayout";
    ListView mListView;
    int OFFSET = 0;
    int startY = 0;
    boolean mCanDragging =false;
    int mOffset = -1;
    int SLOT = 20;
    ArrayAdapter mArrayAdapter;
    LinearLayout mLinearLayout;

    Scroller mScroller;

    public void setArrayAdapter(ArrayAdapter arrayAdapter){
        mArrayAdapter = arrayAdapter;
    }
    public TestLayout(Context context) {
        super(context);
        init();
    }

    public TestLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private int dp2px(Context context ,int dpValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    public int px2dp(Context context,int px){
        return (int) (px/context.getResources().getDisplayMetrics().density + 0.5);
    }
    private void init(){
        mScroller = new Scroller(this.getContext());
    }


    @Override
    protected void onFinishInflate() {
        //mLinearLayout = (LinearLayout) getChildAt(0);
        mListView = (ListView) getChildAt(0);
        OFFSET = dp2px(this.getContext(),300);
        final ViewConfiguration vc = ViewConfiguration.get(getContext());
        SLOT = px2dp(this.getContext(),vc.getScaledTouchSlop());
        mOffset = OFFSET;
        super.onFinishInflate();
    }

    private void dragging(int y){
        Log.d(TAG,"y = "+  y+" startY = "+startY+" mOffset "+mOffset+" SLOT ="+SLOT );
        if(y>startY||mOffset>0){
            int delta = Math.abs(startY - y);
            if(delta>SLOT&&!mCanDragging){
                startY = y;
                mCanDragging = true;
            }
        }


    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int scrollMeasureWidthSpec = MeasureSpec.makeMeasureSpec(
                getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
        int scrollMeasureHeightSpec = MeasureSpec.makeMeasureSpec(
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
        mListView.measure(scrollMeasureWidthSpec, scrollMeasureHeightSpec);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mListView.layout(0,OFFSET,getMeasuredWidth(),getMeasuredHeight()+OFFSET);
    }
    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // 去掉默认行为，使得每个事件都会经过这个Layout
    }
    public boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mListView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mListView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return mListView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mListView, -1);
        }
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        Log.d(TAG,"onTouchEvent "+mCanDragging+" "+canChildScrollUp()+" "+ev.getAction());
        if(canChildScrollUp()){
            return false;
        }

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:{
                startY = (int) ev.getY();
                mCanDragging = false;
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                dragging((int) ev.getY());
                break;
            }
            case MotionEvent.ACTION_UP:{

                break;
            }


        }

        return mCanDragging;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d(TAG,"onTouchEvent "+mCanDragging+" "+canChildScrollUp()+" "+ev.getAction());
        if(canChildScrollUp()){
            return false;
        }
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:{
                mCanDragging = false;
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                if(mCanDragging){
                    int d = (int) (ev.getY()-startY);
                    mOffset += d;
                    mListView.offsetTopAndBottom(d);
                    if(mListView.getY()<0){
                        ViewCompat.offsetTopAndBottom(mListView, (int) (-1*(mListView.getY())));
                    }

                    if (mOffset <= 0&&d<0){
                        mOffset = 0;
                        int oldAction = ev.getAction();
                        ev.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(ev);
                        ev.setAction(oldAction);
                        return true;
                    }
                    startY = (int) ev.getY();
                }
                break;
            }
            case MotionEvent.ACTION_UP:{
                startY = (int) ev.getY();
                if(mCanDragging){
                    if(mListView.getY()>OFFSET){
                        int d = (int) (mListView.getY()-OFFSET);
                        //mListView.offsetTopAndBottom(-d);
                        Log.d(TAG,"--------- "+d);
                        smoothScrollTo(-d);
                    }else{
                        int d = (int) (mListView.getY()-0);
                        //mListView.offsetTopAndBottom(-d);
                        Log.d(TAG,"--------- "+d);
                        smoothScrollTo(-d);
                        mOffset = 0;

                        //int oldAction = ev.getAction();
                        //ev.setAction(MotionEvent.ACTION_DOWN);
                       // dispatchTouchEvent(ev);
                       // ev.setAction(oldAction);
                       // return true;
                    }

                }
                break;
            }


        }
        return mCanDragging;
    }
    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()){

            int offsetY = mScroller.getCurrY();
            Log.d(TAG,"computeScroll offsetY "+offsetY);
            mListView.offsetTopAndBottom(offsetY-(int) (mListView.getY()));
            invalidate();
        }
    }
    public void smoothScrollTo(int deltaY){
        int startX= (int) mListView.getX();
        int startY = (int) mListView.getY();
        mScroller.startScroll(startX, startY, 0, deltaY, 800);
        invalidate();
    }
}
