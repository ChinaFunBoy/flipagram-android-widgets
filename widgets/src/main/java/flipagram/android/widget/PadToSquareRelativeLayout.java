/*
 * Copyright (C) 2015 Flipagram, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package flipagram.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import flipagram.android.widget.ondraw.PaddingColor;
import flipagram.android.widget.ondraw.PaddingFrame;
import flipagram.android.widget.onmeasure.PaddingSquare;
import flipagram.android.widgets.R;

/**
 * TODO: document your custom view class.
 */
public class PadToSquareRelativeLayout extends RelativeLayout {
    private Paint paddingPaint = new Paint(Color.TRANSPARENT);
    private Paint paddingFramePaint = new Paint(Color.TRANSPARENT);

    public PadToSquareRelativeLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public PadToSquareRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PadToSquareRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        setWillNotDraw(false);

        paddingPaint.setStyle(Paint.Style.FILL);
        paddingFramePaint.setStyle(Paint.Style.STROKE);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
            attrs, R.styleable.PadToSquareRelativeLayout, defStyle, 0);

        paddingPaint.setColor(a.getColor(
            R.styleable.PadToSquareRelativeLayout_paddingColor,
            paddingPaint.getColor()));

        paddingFramePaint.setColor(a.getColor(
            R.styleable.PadToSquareRelativeLayout_paddingFrameColor,
            paddingFramePaint.getColor()));

        paddingFramePaint.setStrokeWidth(a.getDimension(
            R.styleable.PadToSquareRelativeLayout_paddingFrameWidth,
            0f));

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        PaddingColor.draw(this, canvas, paddingPaint);
        PaddingFrame.draw(this, canvas, paddingFramePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        PaddingSquare.measure(this, widthMeasureSpec, heightMeasureSpec);
    }


    /**
     * @return The padding color
     */
    public int getPaddingColor() {
        return paddingPaint.getColor();
    }

    /**
     * Sets the view's padding color
     *
     * @param paddingColor The new Padding Color
     */
    public void setPaddingColor(int paddingColor) {
        paddingPaint.setColor(paddingColor);
    }

    /**
     * @return The padding frame color
     */
    public int getPaddingFrameColor() {
        return paddingFramePaint.getColor();
    }

    /**
     * Sets the view's padding frame color
     *
     * @param paddingFrameColor The new Padding Frame Color
     */
    public void setPaddingFrameColor(int paddingFrameColor) {
        paddingFramePaint.setColor(paddingFrameColor);
    }

    /**
     * Gets width of the padding frame
     *
     * @return The width of the padding frame
     */
    public float getPaddingFrameWidth() {
        return paddingFramePaint.getStrokeWidth();
    }

    /**
     * Sets the width of the padding frame
     *
     * @param paddingFrameWidth The new width of the padding frame
     */
    public void setPaddingFrameWidth(float paddingFrameWidth) {
        paddingFramePaint.setStrokeWidth(paddingFrameWidth);
    }

}
