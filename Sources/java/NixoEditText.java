package edittext.nixo.com.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.GridLayout;
import android.widget.Switch;


/**
 * @Author Nixo
 * @Date 2018/11/26
 * @FeedBack 该控件目前仅支持EditText的作用+了一个清除输入数据的作用。
 */

public class NixoEditText extends android.support.v7.widget.AppCompatEditText implements TextWatcher {

    private int mCancelIconInt; //清除文字图片
    private Paint mPaint; //画笔
    private String TAG = "Nixo";
    private String text;
    private StringBuilder mTextBuilder;
    private float mCancelBottom; //图片底坐标
    private float mCancelLeft;//图片左坐标
    private float mCancelRight;//图片右坐标
    private float mCancelTop;// 图片顶坐标
    private boolean isClearForDown;
    private boolean isClearForUP;
    private OnClearListener listener;

    public NixoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context,attrs);
        initPaint();
        initData();
    }

    private void initData() {
        mTextBuilder = new StringBuilder();
    }

    public NixoEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context,attrs);
        initPaint();
        initData();

    }

    private void initPaint() {
        mPaint = new Paint();
        //使用以下两个方法使得画出来的图案更清晰
        mPaint.setDither(true); //.setDither设置坑锯齿
        mPaint.setAntiAlias(true);//.setAntialias设置防抖动
        mPaint.setStyle(Paint.Style.FILL);
    }


    private void initAttr(Context context,AttributeSet attrs){
        //获取到自定义属性，属性定义在res/values/attrs.xml中
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.NixoEditText);
        mCancelIconInt = typedArray.getResourceId(R.styleable.NixoEditText_cancelIcon, R.drawable.cancel);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //自己测量大小，样板示代码
        int mSpecWith = MeasureSpec.getSize(widthMeasureSpec);
        int mSpecHeight = MeasureSpec.getSize(heightMeasureSpec);
        int mSpecWithMode = MeasureSpec.getMode(widthMeasureSpec);
        int mSpecHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        int mWith = 0;
        int mHeight = 0;
        int mMaxWith = 0;
        int mMaxHeight = 0;

        switch (mSpecWithMode){
            case MeasureSpec.EXACTLY: {
                //如果输入的android:layout_with= "具体数值"；
                //那么当前的模式为EXACTLY 精确模式；
                mWith = mSpecWith;
                break;
            }
            case MeasureSpec.UNSPECIFIED:{
                //MeasureSpec.UNSPECIFIED 也就是不受任何拘束，有多大就给多大，所以取最大值即可 == MatchParent;
                mMaxWith = mSpecWith+getPaddingLeft()+getPaddingRight();
                mWith = mMaxWith;
                break;
            }
            case MeasureSpec.AT_MOST: {
                // MeasureSpec.AT_MOST == WrapContent;
                //所以最宽的宽度应该为当前获取到的mSpecWith+PaddingLeft+PaddingRight的宽度；
                //getPaddingLeft()方法是View的方法，这里通过EditText继承来的。
                //这里只要取最小值即可，因为最大值肯定是整个屏幕，最小值就是刚刚好到的位置
                int mWrapWith = Math.min(mMaxWith,mSpecWith);
                mWith = mWrapWith;
                break;
            }
        }

        //高度跟宽度一样的样板代码

        switch (mSpecHeightMode){
            case MeasureSpec.EXACTLY: {
                mHeight = mSpecHeight;
                Log.e(TAG, "onMeasure: mSpecHeight"+mSpecHeight );
                break;
            }
            case MeasureSpec.UNSPECIFIED:{
                mMaxHeight = mSpecHeight+getPaddingTop()+getPaddingBottom();
                mHeight = mMaxWith;
                break;
            }
            case MeasureSpec.AT_MOST: {
                int mWrapHeight = Math.min(mMaxHeight,mSpecHeight);
                mHeight = mWrapHeight;
                break;
            }
        }
        Log.e(TAG, "onMeasure: 测算出的高度宽度"+mWith+"======"+mHeight );
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //在这里用Paint画出来取消按钮
        //先获取取消按钮的bitmap,这里直接强转就行.

        Bitmap mCancelBt = ((BitmapDrawable) (getResources().getDrawable(mCancelIconInt))).getBitmap();
        //这里的取消按钮的top 应为父View的高度-高度/2-图片的高度/2
        //这里的取消按钮的left应为父View的宽度 - 宽度/2 - 图片的宽度
        //通过上面两个公式可以让bitmap达到right|center效果
        mCancelBottom = getHeight() - (getHeight()/2) - (mCancelBt.getHeight()/2);
        mCancelLeft = getWidth()-(getHeight()/2)-(mCancelBt.getWidth()/2);
        //计算clear图标的坐标范围
        mCancelRight = mCancelLeft + (mCancelBt.getWidth());
        mCancelTop = mCancelBottom + (mCancelBt.getHeight());

//        Log.e(TAG, "onDraw: 父View宽度"+getWidth()
//                +"父控件高度"+getHeight()
//                +"\n"+"宽度:-->"+ mCancelLeft
//                +"\n"+"高度"+ this.mCancelTop);

        canvas.drawBitmap(mCancelBt, mCancelLeft, mCancelBottom,mPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{

                //如果按下的位置是图片的坐标范围内，标记为以按住，当触发ACTION_UP的位置也在这里，那么触发clear();
                //获取到点击坐标X
                float touchPointX = event.getX();
                float touchPointY = event.getY();
                //写同时会消耗性能(因为每一项都触发对比)，这里写或（只要有一项不满足，直接break）
                //横坐标小于bitmap左边界，大于右边界
                //纵坐标高于bitmap顶边界,纵坐标小于底坐标
                if(touchPointX>mCancelRight || touchPointX<mCancelLeft
                        || touchPointY > mCancelTop || touchPointY <mCancelBottom){
                isClearForDown = false;
                }
                    isClearForDown = true;
                    Log.e(TAG, "onTouchEvent: 满足点击" );
                break;
            }
            case MotionEvent.ACTION_UP:{
                float touchPointX = event.getX();
                float touchPointY = event.getY();
                Log.e(TAG, "onTouchEvent: 松开了X--->"+touchPointX+"Y--->"+touchPointY );
                if(touchPointX>mCancelRight || touchPointX<mCancelLeft
                        || touchPointY > mCancelTop || touchPointY <mCancelBottom){
                    isClearForUP = false;
                    break;
                }
                Log.e(TAG, "onTouchEvent: 满足松开" );
                //在触点抬起的时候判断两次是否都在范围内，如果是就clear；
                isClearForUP = true;
                if(isClearForDown == isClearForUP){
                    clear();
                }

                break;
            }

        }


        return super.onTouchEvent(event);
    }

    private  void clear(){
        text = "";
        setText(text);
        if(listener == null){
            return;
        }
        listener.onClear(getText().toString());
    }

    public void setOnClearListener(OnClearListener infs){
        listener = infs;
    }

    public interface OnClearListener{
       void onClear(String str);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.length() == 0){
            return;
        }
        mTextBuilder.append(s);
    }

    @Override
    public void afterTextChanged(Editable s) {
        text = mTextBuilder.toString();
    }
}










