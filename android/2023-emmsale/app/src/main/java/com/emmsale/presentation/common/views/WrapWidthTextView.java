package com.emmsale.presentation.common.views;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WrapWidthTextView
        extends androidx.appcompat.widget.AppCompatTextView {
    public WrapWidthTextView(@NonNull Context context) {
        super(context);
    }

    public WrapWidthTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapWidthTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // TextView 가져오기
        Layout layout = getLayout();
        if (layout != null) {
            // getMaxLineWidth()를 통해 너비를 구하고 양쪽 패딩값을 더해 최종 width를 계산
            int width = (int) Math.ceil(getMaxLineWidth(layout))
                    + getCompoundPaddingLeft() + getCompoundPaddingRight();
            // 높이는 그냥 TextView의 높이
            int height = getMeasuredHeight();

            // 구한 너비와 높이를 토대로 View의 크기 조정
            setMeasuredDimension(width, height);
        }
    }

    // 텍스트 뷰의 모든 줄을 돌면서 최대 width을 구해 반환
    private float getMaxLineWidth(Layout layout) {
        float max_width = 0.0f;
        int lines = layout.getLineCount();
        for (int i = 0; i < lines; i++) {
            if (layout.getLineWidth(i) > max_width) {
                max_width = layout.getLineWidth(i);
            }
        }
        return max_width;
    }
}