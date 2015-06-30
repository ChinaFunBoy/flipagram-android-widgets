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
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import flipagram.android.widgets.R;

/**
 * A layout that lets you specify <ul>
 * <li>The location (x/y coordinates) of its children as a float that ranges between 0 and 1
 * where 0 is the top/left and 1 is bottom/right.</li>
 * <li>A ratio that relates the size of each child to it's parent's size</li>
 */
public class RatioDynamicLayout extends ViewGroup {
    private Point childPixels = new Point();

    public RatioDynamicLayout(Context context) {
        this(context, null);
    }

    public RatioDynamicLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioDynamicLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Returns a set of layout parameters with a width of
     * {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT},
     * a height of {@link android.view.ViewGroup.LayoutParams#WRAP_CONTENT}
     * and with the coordinates (0, 0).
     */
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        final int width = MeasureSpec.getSize(widthSpec);
        final int height= MeasureSpec.getSize(heightSpec);
        final int widthAtMostSpec = MeasureSpec.makeMeasureSpec(width,  MeasureSpec.AT_MOST);
        final int heightAtMostSpec = MeasureSpec.makeMeasureSpec(height,  MeasureSpec.AT_MOST);

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.forceLayout();
                child.measure(widthAtMostSpec,heightAtMostSpec);
            }
        }

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();

        float childLeft=0;
        float childTop=0;
        float childRight=0;
        float childBottom=0;

        final float paddingTop = getPaddingTop();
        final float paddingLeft = getPaddingLeft();
        final float verticalPadding = paddingTop + getPaddingBottom();
        final float horizontalPadding = getPaddingLeft() + getPaddingRight();

        final float containerVerticalPixels = getMeasuredHeight() - verticalPadding;
        final float containerHorizontalPixels = getMeasuredWidth() - horizontalPadding;
        final int widthAtMostSpec = MeasureSpec.makeMeasureSpec((int)containerHorizontalPixels,  MeasureSpec.AT_MOST);
        final int heightAtMostSpec = MeasureSpec.makeMeasureSpec((int)containerVerticalPixels,  MeasureSpec.AT_MOST);


        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                RatioDynamicLayout.LayoutParams lp = (RatioDynamicLayout.LayoutParams) child.getLayoutParams();

                child.forceLayout();
                child.measure(widthAtMostSpec,heightAtMostSpec);
                childPixels.y = child.getMeasuredHeight();
                childPixels.x = child.getMeasuredWidth();

                if (lp.centerInViewOnNextLayout) {
                    childTop = containerVerticalPixels/2 - childPixels.y/2;
                    childBottom = childTop + childPixels.y;
                    lp.y = calculateY(Gravity.TOP, // This centers multi-line text vertically
                            childTop, childBottom, paddingTop, containerVerticalPixels);
                } else if (lp.centerPoint!=null){
                    childTop = lp.centerPoint.y - childPixels.y/2;
                    childBottom = childTop + childPixels.y;
                    lp.y = calculateY(lp.gravity & Gravity.VERTICAL_GRAVITY_MASK,
                            childTop, childBottom, paddingTop, containerVerticalPixels);
                } else {
                    final int y = (int) (lp.y * containerVerticalPixels);
                    switch (lp.gravity & Gravity.VERTICAL_GRAVITY_MASK) {
                        case Gravity.BOTTOM:
                            childTop = paddingTop + y - childPixels.y;
                            break;
                        case Gravity.CENTER_VERTICAL:
                            childTop = paddingTop + y - childPixels.y / 2;
                            break;
                        case Gravity.TOP:
                        default:
                            childTop = paddingTop + y;
                    }
                    childBottom = childTop + childPixels.y;
                }

                if (lp.centerInViewOnNextLayout) {
                    childLeft = containerHorizontalPixels/2 - childPixels.x/2;
                    childRight = childLeft + childPixels.x;
                    lp.x = calculateX(lp.gravity & Gravity.HORIZONTAL_GRAVITY_MASK,
                            childLeft, childRight, paddingLeft, containerHorizontalPixels);
                } else if (lp.centerPoint!=null) {
                    childLeft = lp.centerPoint.x - childPixels.x/2;
                    childRight = childLeft + childPixels.x;
                    lp.x = calculateX(lp.gravity & Gravity.HORIZONTAL_GRAVITY_MASK,
                            childLeft, childRight, paddingLeft, containerHorizontalPixels);
                } else {
                    final int x = (int) (lp.x * containerHorizontalPixels);
                    switch (lp.gravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                        case Gravity.RIGHT:
                            childLeft = paddingLeft + x - childPixels.x;
                            break;
                        case Gravity.CENTER_HORIZONTAL:
                            childLeft = paddingLeft + x - childPixels.x / 2;
                            break;
                        case Gravity.LEFT:
                        default:
                            childLeft = paddingLeft + x;
                    }
                    childRight = childLeft + childPixels.x;
                }
                lp.centerPoint = null;
                lp.centerInViewOnNextLayout = false;

                child.layout((int)childLeft, (int)childTop, (int)childRight, (int)childBottom);
            }
        }
    }

    /**
     * Calculate lp.x based on the child's position and gravity
     */
    private float calculateX(
            int gravity,
            float childLeft,
            float childRight,
            float paddingLeft,
            float containerHorizontalPixels
    ){
        switch(gravity){
            case Gravity.RIGHT:
                return (childRight-paddingLeft) / containerHorizontalPixels;
            case Gravity.CENTER_HORIZONTAL:
                return (childLeft+(childRight-childLeft)/2-paddingLeft) / containerHorizontalPixels;
            case Gravity.LEFT:
            default:
                return (childLeft-paddingLeft) / containerHorizontalPixels;
        }
    }

    /**
     * Calculate lp.y based on the child's position and gravity
     */
    private float calculateY(
            int gravity,
            float childTop,
            float childBottom,
            float paddingTop,
            float containerVerticalPixels
    ){
        switch (gravity) {
            case Gravity.BOTTOM:
                return (childBottom-paddingTop) / containerVerticalPixels;
            case Gravity.CENTER_VERTICAL:
                return (childTop+(childBottom-childTop)/2-paddingTop) / containerVerticalPixels;
            case Gravity.TOP:
            default:
                return (childTop-paddingTop) / containerVerticalPixels;
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new RatioDynamicLayout.LayoutParams(getContext(), attrs);
    }

    // Override to allow type-checking of LayoutParams.
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof RatioDynamicLayout.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    /**
     * Per-child layout information associated with PercentLayout.
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        /**
         * The horizontal (X) percentage of the left side of the child
         */
        public float x;
        /**
         * The vertical (Y) percentage of the top of the child
         */
        public float y;
        /**
         * The ratio of container percentage
         */
        public float ratio;
        /**
         * The {@link android.view.Gravity} from which the x,y eminates
         */
        public int gravity;
        /**
         * When the centerPoint is not null, the view is laid out around this point next time it's
         * laid out. In this case the x,y are ignored for the purposes of laying out the View.
         * However, x,y are recalculated based on the centerPoint.
         */
        public Point centerPoint = null;
        /**
         * When the centerInViewOnNextLayout is true, the view is laid out around the exact center
         * of the container
         * x,y are recalculated based on the calculated center.
         */
        public boolean centerInViewOnNextLayout = false;

        public LayoutParams(int width, int height) {
            super(width, height);
        }
        /**
         * Creates a new set of layout parameters with the specified width,
         * height and location.
         *
         * @param width the width, either {@link #MATCH_PARENT},
        {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param height the height, either {@link #MATCH_PARENT},
        {@link #WRAP_CONTENT} or a fixed size in pixels
         * @param x the X location of the child
         * @param y the Y location of the child
         */
        public LayoutParams(int width, int height, float x, float y) {
            this(width, height);
            this.x = x;
            this.y = y;
        }

        /**
         * Creates a new set of layout parameters. The values are extracted from
         * the supplied attributes set and context. The XML attributes mapped
         * to this set of layout parameters are:
         *
         * <ul>
         *   <li><code>layout_x</code>: the X location of the child</li>
         *   <li><code>layout_y</code>: the Y location of the child</li>
         *   <li>All the XML attributes from
         *   {@link android.view.ViewGroup.LayoutParams}</li>
         * </ul>
         *
         * @param c the application environment
         * @param attrs the set of attributes from which to extract the layout
         *              parameters values
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.RatioDynamicLayout);
            x = a.getFloat(R.styleable.RatioDynamicLayout_x, 0);
            y = a.getFloat(R.styleable.RatioDynamicLayout_y, 0);
            ratio = a.getFloat(R.styleable.RatioDynamicLayout_ratio, 0);
            gravity = a.getInt(R.styleable.PercentLayout_android_layout_gravity, -1);
            a.recycle();
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
