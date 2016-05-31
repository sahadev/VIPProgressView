package com.sahadev.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.sahadev.example.vipprogressview.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 会员达阵进度View
 * Created by admin on 2016/5/13.
 */
public class VipProgressView extends View {
    private String TAG = getClass().getSimpleName();

    //会员达阵总进度
    private int mTotleStage = 0;

    //当前进度
    private int mCurrState = 0;

    //未达阵示意图片

    //进度条颜色
    private ColorDrawable mProgressDrawable;

    //已达阵示意图片

    //连接线宽度
    private int mProgressDrawableWidth;

    //左内边距
    private int mPaddingLeft;

    //上内边距
    private int mPaddingTop;

    //连接线选中画笔
    private Paint mLineCheckedPaint;

    //连接线未选中画笔
    private Paint mLineUncheckedPaint;

    //数据
    private List<StateBean> mLists;

    /**
     * 图片的最大高度,用作内容的高度
     */
    private int mContentHeight;

    /**
     * 存放代表性的图片，选中或者未选中只取一张
     */
    private List<Drawable> mDrawables;

    private int[] drawableRes = {R.drawable.ic_vip_onprocess, R.drawable.ic_vip_onprocess_s,
            R.drawable.ic_vip_final, R.drawable.ic_vip_final_s};

    public VipProgressView(Context context) {
        super(context);
        init(context, null);
    }

    public VipProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public VipProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        initDrawableInfo();

        mLineCheckedPaint = new Paint();
        mLineCheckedPaint.setColor(getResources().getColor(R.color.color_ff2741));

        mLineUncheckedPaint = new Paint();
        mLineUncheckedPaint.setColor(getResources().getColor(R.color.color_1fff));

        //构造模拟数据
        mLists = new ArrayList<>();

        mTotleStage = mLists.size();


        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VipProgressView, 0, 0);

        try {
            int minStandardNum = typedArray.getInt(R.styleable.VipProgressView_minStandardNum, 4);
            int currentNum = typedArray.getInt(R.styleable.VipProgressView_currentNum, 0);

            setInterData(minStandardNum, currentNum);
        } finally {
            typedArray.recycle();
        }
    }

    /**
     * 初始化图片信息
     */
    private void initDrawableInfo() {
        mDrawables = new ArrayList<>();

        for (int i = 0; i < drawableRes.length; i++) {
            if (i % 2 == 0)//每套图片只取一张，所以在存放图片的时候要连续存放
                continue;
            Drawable drawable = getResources().getDrawable(drawableRes[i]);
            mDrawables.add(drawable);

            if (drawable.getIntrinsicHeight() > mContentHeight)
                mContentHeight = drawable.getIntrinsicHeight();
        }

    }


    /**
     * 设置进度数据，该方法对参数进行了一次包装。该方法适用于当当前单数等于最大单数时，直接完成达阵，这里的进度也表示了会员状态
     *
     * @param min  会员每月最小单数
     * @param curr 当前单数
     */
    public void setData(int min, int curr) {
        if (min == curr) {
            curr += 1;//
        }
        min += 1;
        setInterData(min, curr);
    }

    /**
     * 设置数据
     *
     * @param min  会员每月最小单数
     * @param curr 当前单数
     */
    private void setInterData(int min, int curr) {
        if (min > 0) {


            mLists.clear();
            for (int i = 0; i < min; i++) {
                if (curr <= i)
                    mLists.add(new StateBean(false));
                else
                    mLists.add(new StateBean(true));
            }
            mTotleStage = mLists.size();
            requestLayout();
            invalidate();
        }

    }

    /**
     * 设置线条绘制颜色
     *
     * @param frontColor  设置选中色
     * @param behindColor 设置未选中色
     */
    public void setPaintColor(int frontColor, int behindColor) {
        mLineCheckedPaint.setColor(frontColor);
        mLineUncheckedPaint.setColor(behindColor);
        invalidate();
    }

    /**
     * 设置状态图标
     *
     * @param icons 数组长度必须为4个
     */
    public void setIcons(int[] icons) {
        if (icons == null || icons.length != 4) {
            try {
                throw new Exception("the lenght of the array must be as 4.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        drawableRes = icons;
        initDrawableInfo();
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        mPaddingLeft = getPaddingLeft();

        mPaddingTop = getPaddingTop();

        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY) {
            //计算实际的内容区域宽度
            int contentWidth = widthSize - getPaddingLeft() - getPaddingRight();
            //内容区域的总长度 = mTotleStage * mOffDrawable.width + (mTotleStage - 1) * mProgressDrawable

            int drawableTotolWidth = 0;
            for (int i = 0; i < mTotleStage; i++) {
                if (i != mTotleStage - 1) {//如果不是最后一张图片，则使用前面的图片宽度进行计算
                    drawableTotolWidth += mDrawables.get(0).getIntrinsicWidth();
                } else {
                    drawableTotolWidth += mDrawables.get(1).getIntrinsicWidth();
                }
            }
            //计算进度条图像宽度
            mProgressDrawableWidth = (contentWidth - drawableTotolWidth) / (mTotleStage - 1);
        }

        int finalHeight = mContentHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(finalHeight, heightMode));
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        int sum = mPaddingLeft;

        int tempHalfHeight = mContentHeight / 2;

        int lineSelectOffset = tempHalfHeight - 2;//这里的2代表线条高度的一半，总高度=4
        int lineUnselectOffset = tempHalfHeight - 2;//这里的6代表线条高度的一半，总高度=12

        Drawable tempIconDrawable = null;
        Paint tempPiant = null;
        for (int i = 0; i < mTotleStage * 2 - 1; i++) {
            int saveCount = canvas.getSaveCount();
            canvas.save();

            StateBean stateBean = mLists.get(i / 2);

            if (i % 2 == 0) {
                //这种情况下是状态图标
                if (stateBean.isChecked) {
                    tempIconDrawable = getResources().getDrawable(drawableRes[i == mTotleStage * 2 - 2 ? 3 : 1]);
                } else {
                    tempIconDrawable = getResources().getDrawable(drawableRes[i == mTotleStage * 2 - 2 ? 2 : 0]);
                }
                int intrinsicWidth = tempIconDrawable.getIntrinsicWidth();
                int intrinsicHeight = tempIconDrawable.getIntrinsicHeight();
                int centerOffset = (mContentHeight - intrinsicHeight) / 2;
                //居中绘制方法
                tempIconDrawable.setBounds(sum, mPaddingTop + centerOffset, sum + intrinsicWidth, mPaddingTop + intrinsicHeight + centerOffset);
                tempIconDrawable.draw(canvas);
                sum += intrinsicWidth;
            } else {
                //这种情况下是状态线
                //绘制选中线
                if (stateBean.isChecked) {
                    tempPiant = mLineCheckedPaint;
                    canvas.drawRect(sum + 10, mPaddingTop + lineSelectOffset, sum + mProgressDrawableWidth - 10, mPaddingTop + mContentHeight - lineSelectOffset, tempPiant);
                } else {
                    //绘制背景线
                    tempPiant = mLineUncheckedPaint;
                    canvas.drawRect(sum + 10, mPaddingTop + lineUnselectOffset, sum + mProgressDrawableWidth - 10, mPaddingTop + mContentHeight - lineUnselectOffset, tempPiant);
                }
                sum += mProgressDrawableWidth;//这里的20是线于图标之间的间
            }
            canvas.restoreToCount(saveCount);
        }
    }

    class StateBean {
        boolean isChecked;

        public StateBean(boolean isChecked) {
            this.isChecked = isChecked;
        }
    }
}
