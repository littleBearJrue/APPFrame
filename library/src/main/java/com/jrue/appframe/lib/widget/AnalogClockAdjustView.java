package com.jrue.appframe.lib.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jrue.appframe.lib.R;
import com.jrue.appframe.lib.event.StartAdjustClockEvent;
import com.jrue.appframe.lib.util.MEvent;
import com.jrue.appframe.lib.util.MLog;
import com.jrue.appframe.lib.util.MUtils;


/**
 * Created by jrue on 17/2/10.
 */
public class AnalogClockAdjustView extends View {
    private static final String TAG = AnalogClockAdjustView.class.getSimpleName();
    private Time mCalendar;
    private final float ROTATE_STEP = 1f;
    private final float CYCLE_DEGREE = 360f;
    private final float MAX_OFFSET_DEGREE = 5f;
    private final int HAND_OFFSET = MUtils.dip2px(getContext(), 12);
    public final static int HOUR_HAND = 0;
    public final static int MINUTE_HAND = 1;
    public final static long UPDATE_INTERVAL = 20L;
    private Drawable mHourHand;// 时针
    private Drawable mMinuteHand;// 分针
    private Drawable mHourHandSel;// 时针
    private Drawable mMinuteHandSel;// 分针
    private Drawable mTouchUnsel;// 分针
    private Drawable mTouchSel;// 分针
    private Drawable mTouchPressed;// 分针
    private Drawable mCenter;// 分针
    private Drawable mDial;// 表盘
    private int mYCenter;
    private int mXCenter;

    private int mDialWidth;// 表盘宽度
    private int mDialHeight;// 表盘高度
    private boolean isPressed;// 是否有指针被按下
    private float mHour;// 时针值
    private float mHourDegree;// 指针旋转度数
    private float mDstHourDegree;// 指针旋转度数
    private float mMinutes;// 分针值
    private float mMinuteDegree;// 指针旋转度数
    private float mScale;  // 表盘缩放比例
    private int mViewWidth;// view可用宽度，通过右坐标减去左坐标
    private int mViewHeight;// view可用高度，通过下坐标减去上坐标

    private boolean mIsInited;
    private boolean mIsStartMove;
    private int mAdjustHand = HOUR_HAND;
    private boolean mIsHourHandNeedAdjust = false;
    public AnalogClockAdjustView(Context context) {
        this(context, null);
    }

    public AnalogClockAdjustView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnalogClockAdjustView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Resources r = getContext().getResources();
        // 下面是从layout文件中读取所使用的图片资源，如果没有则使用默认的
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.AnalogClock, defStyle, 0);
        mDial = a.getDrawable(R.styleable.AnalogClockAdjust_dial);
        mHourHand = a.getDrawable(R.styleable.AnalogClockAdjust_hand_hour);
        mMinuteHand = a.getDrawable(R.styleable.AnalogClockAdjust_hand_minute);
        mHourHandSel = a.getDrawable(R.styleable.AnalogClockAdjust_hand_hour_sel);
        mMinuteHandSel = a.getDrawable(R.styleable.AnalogClockAdjust_hand_minute_sel);
        mTouchUnsel = a.getDrawable(R.styleable.AnalogClockAdjust_touch_unsel);
        mTouchSel = a.getDrawable(R.styleable.AnalogClockAdjust_touch_sel);
        mTouchPressed = a.getDrawable(R.styleable.AnalogClockAdjust_touch_pressed);
        mCenter = a.getDrawable(R.styleable.AnalogClockAdjust_clock_center);

        // 为了整体美观性，只要缺少一张图片，我们就用默认的那套图片
        if (mDial == null || mHourHand == null || mMinuteHand == null
                || mHourHandSel == null || mMinuteHandSel == null || mCenter == null) {
            mDial = r.getDrawable(R.drawable.clock_adjust_dial);
            mHourHand = r.getDrawable(R.drawable.clock_adjust_hour);
            mMinuteHand = r.getDrawable(R.drawable.clock_adjust_minute);
            mHourHandSel = r.getDrawable(R.drawable.clock_adjust_hour_sel);
            mMinuteHandSel = r.getDrawable(R.drawable.clock_adjust_minute_sel);
            mTouchUnsel = r.getDrawable(R.drawable.clock_touch_unsel);
            mTouchSel = r.getDrawable(R.drawable.clock_touch_sel);
            mTouchPressed = r.getDrawable(R.drawable.clock_touch_pressed);
            mCenter = r.getDrawable(R.drawable.clock_adjust_center);
        }
        a.recycle();// 不调用这个函数，则上面的都是白费功夫

        mIsInited = false;
        mIsStartMove = false;
        // 初始化Time对象
        if (mCalendar == null) {
            mCalendar = new Time();
            mCalendar.setToNow();
        }
        int hour = mCalendar.hour;
        int minute = mCalendar.minute;
        mMinutes = minute/* + second / 60.0f*/;// 分钟值，加上秒，也是为了使效果逼真
        mMinuteDegree = (mMinutes / 60.0f * CYCLE_DEGREE)%CYCLE_DEGREE;
        mHour = hour + mMinutes / 60.0f/* + mSecond / 3600.0f*/;// 小时值，加上分和秒，效果会更加逼真
        mHourDegree = (mHour / 12.0f * CYCLE_DEGREE)%CYCLE_DEGREE;
        initView();

    }
    public void setHand(int hand){
        mAdjustHand = hand;
        invalidate();
    }
    public int getTimeMinute(){
        mMinutes = (mMinuteDegree%CYCLE_DEGREE)*60f/CYCLE_DEGREE;
        return (int)mMinutes;
    }

    public int getTimeHour(){
        mHour = (mHourDegree%CYCLE_DEGREE)*12f/CYCLE_DEGREE;
        return (int)mHour;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 模式： UNSPECIFIED(未指定),父元素不对子元素施加任何束缚，子元素可以得到任意想要的大小；
        // EXACTLY(完全)，父元素决定自元素的确切大小，子元素将被限定在给定的边界里而忽略它本身大小；
        // AT_MOST(至多)，子元素至多达到指定大小的值。
        // 根据提供的测量值(格式)提取模式(上述三个模式之一)
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // 根据提供的测量值(格式)提取大小值(这个大小也就是我们通常所说的大小)
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        // 高度与宽度类似
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f;// 缩放值
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = (float) widthSize / (float) mDialWidth;// 如果父元素提供的宽度比图片宽度小，就需要压缩一下子元素的宽度
        }

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = (float) heightSize / (float) mDialHeight;// 同上
        }

        float scale = Math.min(hScale, vScale);// 取最小的压缩值，值越小，压缩越厉害
        // 最后保存一下，这个函数一定要调用
        setMeasuredDimension(
                resolveSizeAndState((int) (mDialWidth * scale),
                        widthMeasureSpec, 0),
                resolveSizeAndState((int) (mDialHeight * scale),
                        heightMeasureSpec, 0));
    }

    private void initView(){
        if(mIsInited){
            return;
        }
        // 获取表盘的宽度和高度
        mDialWidth = mDial.getIntrinsicWidth();
        mDialHeight = mDial.getIntrinsicHeight();
        mXCenter = (getRight() - getLeft())/2;// view可用宽度，通过右坐标减去左坐标
        mYCenter = (getBottom() - getTop())/2;// view可用高度，通过下坐标减去上坐标
        if(mXCenter != 0 && mYCenter != 0){
            mIsInited = true;
        }
        mViewWidth = getRight() - getLeft();// view可用宽度，通过右坐标减去左坐标
        mViewHeight = getBottom() - getTop();// view可用高度，通过下坐标减去上坐标
        //dial = mDial;// 表盘图片

        if(MLog.DEBUG) MLog.out.w(TAG, "availableWidth = " + mViewWidth + ", availableHeight = " + mViewHeight + "w = " + mDialWidth + ", h = " + mDialHeight);
        // 最先画表盘，最底层的要先画上画板
        mScale = 1f;
        if (mViewWidth < mDialWidth || mViewHeight < mDialHeight) {// 如果view的可用宽高小于表盘图片，就要缩小图片
            mScale = Math.min((float) mViewWidth / (float) mDialWidth,
                    (float) mViewHeight / (float) mDialHeight);// 计算缩小值
        }

        mDial.setBounds(mXCenter - (mDialWidth / 2), mYCenter - (mDialHeight / 2), mXCenter + (mDialWidth / 2), mYCenter + (mDialHeight / 2));
        int w = mMinuteHand.getIntrinsicWidth();// 表盘宽度
        int h = mMinuteHand.getIntrinsicHeight();
        mMinuteHand.setBounds(mXCenter - (w / 2), mYCenter - h + HAND_OFFSET, mXCenter + (w / 2), mYCenter + HAND_OFFSET);
        w = mMinuteHandSel.getIntrinsicWidth();
        h = mMinuteHandSel.getIntrinsicHeight();
        mMinuteHandSel.setBounds(mXCenter - (w / 2), mYCenter - h + HAND_OFFSET, mXCenter + (w / 2), mYCenter + HAND_OFFSET);
        w = mHourHand.getIntrinsicWidth();
        h = mHourHand.getIntrinsicHeight();
        mHourHand.setBounds(mXCenter - (w / 2), mYCenter - h + HAND_OFFSET, mXCenter + (w / 2), mYCenter + HAND_OFFSET);
        w = mHourHandSel.getIntrinsicWidth();
        h = mHourHandSel.getIntrinsicHeight();
        mHourHandSel.setBounds(mXCenter - (w / 2), mYCenter - h + HAND_OFFSET, mXCenter + (w / 2), mYCenter + HAND_OFFSET);
        w = mTouchPressed.getIntrinsicWidth();
        h = mTouchPressed.getIntrinsicHeight();
        mTouchPressed.setBounds(mXCenter - (w / 2), mYCenter - mDialHeight/2, mXCenter + (w / 2), mYCenter - mDialHeight/2 + h);
        w = mTouchUnsel.getIntrinsicWidth();
        h = mTouchUnsel.getIntrinsicHeight();
        mTouchUnsel.setBounds(mXCenter - (w / 2), mYCenter - mDialHeight/2, mXCenter + (w / 2), mYCenter - mDialHeight/2 + h);
        w = mTouchSel.getIntrinsicWidth();
        h = mTouchSel.getIntrinsicHeight();
        mTouchSel.setBounds(mXCenter - (w / 2), mYCenter - mDialHeight/2, mXCenter + (w / 2), mYCenter - mDialHeight/2 + h);
        w = mCenter.getIntrinsicWidth();
        h = mCenter.getIntrinsicHeight();
        mCenter.setBounds(mXCenter - (w / 2), mYCenter - (h / 2), mXCenter + (w / 2), mYCenter + (h / 2));
        //postInvalidateDelayed(100l);
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final float eventX = event.getX();
        final float eventY = event.getY();
        if(MLog.DEBUG)MLog.out.w(TAG, "eventX = " + eventX + ", eventY = " + eventY);
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setHandFromCoords(eventX, eventY);
                return true;
            case MotionEvent.ACTION_MOVE:
                updateHandPosition(eventX, eventY);
                return true;
            case MotionEvent.ACTION_UP:
                adjustHourHand();
                return true;
            default:
                break;
        }


        return super.onTouchEvent(event);
    }

    public void setHandFromCoords(float pointX, float pointY) {
        float opposite = Math.abs(pointY - mYCenter);

        float opposite2 = Math.abs(pointX - mXCenter);
        double radians = Math.atan(opposite / opposite2);
        int degrees = (int) (radians * 180 / Math.PI);

        // Now we have to translate to the correct quadrant.
        boolean rightSide = (pointX > mXCenter);
        boolean topSide = (pointY < mYCenter);
        if (rightSide && topSide) {
            degrees = 90 - degrees;
        } else if (rightSide && !topSide) {
            degrees = 90 + degrees;
        } else if (!rightSide && !topSide) {
            degrees = 270 - degrees;
        } else if (!rightSide && topSide) {
            degrees = 270 + degrees;
        }
        if(MLog.DEBUG)MLog.out.w(TAG, "mAdjustHand = " + mAdjustHand + ", degrees = " + degrees + ", mHourDegree = " + mHourDegree + ", mMinuteDegree = " + mMinuteDegree);
        if(mAdjustHand == HOUR_HAND && isTouchDown(mHourDegree, degrees)){
            isPressed = true;
        }else if(mAdjustHand == MINUTE_HAND && isTouchDown(mMinuteDegree, degrees)){
            isPressed = true;
        }else if(mAdjustHand != HOUR_HAND && isTouchDown(mHourDegree, degrees)){
            mAdjustHand = HOUR_HAND;
            isPressed = true;
        }else if(mAdjustHand != MINUTE_HAND && isTouchDown(mMinuteDegree, degrees)){
            mAdjustHand = MINUTE_HAND;
            isPressed = true;
        }else{
            isPressed = false;
        }
        if(isPressed){
            if(!mIsStartMove){
                mIsStartMove = true;
                MEvent.post(new StartAdjustClockEvent());
            }
        }
        invalidate();
    }

    private boolean isTouchDown(float handDegree, float pressedDegree){
        if((Math.abs(handDegree - pressedDegree) < MAX_OFFSET_DEGREE)
                || (CYCLE_DEGREE - Math.abs(handDegree - pressedDegree) < MAX_OFFSET_DEGREE)){
            return true;
        }else{
            return false;
        }
    }
    public void updateHandPosition(float pointX, float pointY) {
        if(!isPressed){
            return;
        }
        float opposite = Math.abs(pointY - mYCenter);

        float opposite2 = Math.abs(pointX - mXCenter);
        double radians = Math.atan(opposite / opposite2);
        int degrees = (int) (radians * 180 / Math.PI);

        // Now we have to translate to the correct quadrant.
        boolean rightSide = (pointX > mXCenter);
        boolean topSide = (pointY < mYCenter);
        if (rightSide && topSide) {
            degrees = 90 - degrees;
        } else if (rightSide && !topSide) {
            degrees = 90 + degrees;
        } else if (!rightSide && !topSide) {
            degrees = 270 - degrees;
        } else if (!rightSide && topSide) {
            degrees = 270 + degrees;
        }
        switch(mAdjustHand){
            case HOUR_HAND:
                mHourDegree = degrees%CYCLE_DEGREE;
                break;
            case MINUTE_HAND:
                degrees = degrees - degrees%6;
                mMinuteDegree = degrees%CYCLE_DEGREE;
                if(mMinuteDegree < 2 || mMinuteDegree > 358){
                    mMinuteDegree = 0;
                }else if(mMinuteDegree > 178 && mMinuteDegree < 182){
                    mMinuteDegree = 180;
                }
                break;
        }
        invalidate();
    }

    public void adjustHourHand() {
        mIsStartMove = false;
        if(isPressed){
            mMinuteDegree = mMinuteDegree%CYCLE_DEGREE;
            mHourDegree = mHourDegree%CYCLE_DEGREE;
            mMinutes = mMinuteDegree/CYCLE_DEGREE * 60f;
            mHour = (int)(mHourDegree/CYCLE_DEGREE * 12f);
            mHour = mHour + mMinutes / 60.0f;// 小时值，加上分和秒，效果会更加逼真
            mDstHourDegree = mHour / 12.0f * CYCLE_DEGREE;
            mDstHourDegree = mDstHourDegree%CYCLE_DEGREE;
            if(MLog.DEBUG)MLog.out.w(TAG, "mHourDegree = " + mHourDegree + ", mDstHourDegree = " + mDstHourDegree);
            if(Math.abs(mHourDegree - mDstHourDegree) > ROTATE_STEP){
                mIsHourHandNeedAdjust = true;
                if(mHourDegree < mDstHourDegree){
                    mHourDegree = mHourDegree + ROTATE_STEP;
                }else{
                    mHourDegree = mHourDegree - ROTATE_STEP;
                }

            }else{
                mHourDegree = mDstHourDegree;
            }
            isPressed = false;
            invalidate();
        }
        // Check if we're outside the range

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initView();

        // 对画板进行缩放
        if(MLog.DEBUG)MLog.out.w(TAG, "availableWidth = " + mViewWidth + ", availableHeight = " + mViewHeight
                + "， dialWidth = " + mDialWidth + ", dialHeight = " + mDialHeight + ", scale = " + mScale);
        if (mScale < 1f) {// 如果view的可用宽高小于表盘图片，就要缩小图片
            canvas.save();
            canvas.scale(mScale, mScale, mXCenter, mYCenter);// 实际上是缩小的画板
        }

        mDial.draw(canvas);// 这里才是真正把表盘图片画在画板上
        canvas.save();// 一定要保存一下

        //center = mCenter;
        if(mAdjustHand == HOUR_HAND){
            // 画分针
            canvas.rotate(mMinuteDegree, mXCenter, mYCenter);
            mMinuteHand.draw(canvas);
            mTouchUnsel.draw(canvas);
            canvas.restore();
            canvas.save();
            // 画时针
            canvas.rotate(mHourDegree, mXCenter, mYCenter);// 旋转画板，第一个参数为旋转角度，第二、三个参数为旋转坐标点
            if(isPressed){
                mTouchPressed.draw(canvas);
            }else{
                mTouchSel.draw(canvas);
            }
            mHourHandSel.draw(canvas);// 把时针画在画板上
            mCenter.draw(canvas);// 这里才是真正把表盘图片画在画板上
            canvas.restore();// 恢复画板到最初状态
        }else{

            // 画时针
            canvas.rotate(mHourDegree, mXCenter, mYCenter);// 旋转画板，第一个参数为旋转角度，第二、三个参数为旋转坐标点
            mHourHand.draw(canvas);// 把时针画在画板上
            mTouchUnsel.draw(canvas);
            canvas.restore();// 恢复画板到最初状态
            canvas.save();// 恢复画板到最初状态
            // 画分针
            canvas.rotate(mMinuteDegree, mXCenter, mYCenter);

            if(isPressed){
                mTouchPressed.draw(canvas);
            }else{
                mTouchSel.draw(canvas);
            }
            mMinuteHandSel.draw(canvas);
            mCenter.draw(canvas);// 这里才是真正把表盘图片画在画板上
            canvas.restore();
        }
        canvas.save();// 一定要保存一下
        canvas.restore();


        if(mIsHourHandNeedAdjust){
            if(MLog.DEBUG)MLog.out.w(TAG, "mHourDegree 2= " + mHourDegree + ", mDstHourDegree = " + mDstHourDegree);
            if(Math.abs(mHourDegree - mDstHourDegree) > ROTATE_STEP){
                if(mHourDegree < mDstHourDegree){
                    mHourDegree = mHourDegree + ROTATE_STEP;
                }else{
                    mHourDegree = mHourDegree - ROTATE_STEP;
                }

            }else{
                mHourDegree = mDstHourDegree;
                mIsHourHandNeedAdjust = false;
            }
            postInvalidateDelayed(UPDATE_INTERVAL);
        }

    }

}
