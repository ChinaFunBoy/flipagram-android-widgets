package flipagram.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import flipagram.android.widgets.R;

/**
 * A layout that lets you specify locations (x/y coordinates) of its
 * children as a float that ranges between 0 and 1 where 0 is the top/left and 1 is bottom/right.
 */
public class PercentLayout extends ViewGroup {
    public PercentLayout(Context context) {
        this(context, null);
    }

    public PercentLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PercentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
//        int count = getChildCount();
//
//        int maxHeight = 0;
//        int maxWidth = 0;
//
//        // Find out how big everyone wants to be
//        measureChildren(widthMeasureSpec, heightMeasureSpec);
//
//        // Find rightmost and bottom-most child
//        for (int i = 0; i < count; i++) {
//            View child = getChildAt(i);
//            if (child.getVisibility() != GONE) {
//                int childRight;
//                int childBottom;
//
//                PercentLayout.LayoutParams lp
//                    = (PercentLayout.LayoutParams) child.getLayoutParams();
//
//                childRight = (int)(lp.x *getMeasuredWidth())+ child.getMeasuredWidth();
//                childBottom = (int)(lp.y*getMeasuredHeight()) + child.getMeasuredHeight();
//
//                maxWidth = Math.max(maxWidth, childRight);
//                maxHeight = Math.max(maxHeight, childBottom);
//            }
//        }
//
//        // Account for padding too
//        maxWidth += getPaddingLeft() + getPaddingRight();
//        maxHeight += getPaddingTop() + getPaddingBottom();
//
//        // Check against minimum height and width
//        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
//        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
//
//        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
//            resolveSizeAndState(maxHeight, heightMeasureSpec, 0));

        measureChildren(widthSpec, heightSpec);
        int lockedWidth=MeasureSpec.getSize(widthSpec);
        int lockedHeight=MeasureSpec.getSize(heightSpec);

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(lockedWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(lockedHeight, MeasureSpec.EXACTLY));

    }

    protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    protected void measureChild(View child, int parentWidthSpec, int parentHeightSpec) {
        PercentLayout.LayoutParams lp = (PercentLayout.LayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthSpec,
            getPaddingLeft() + getPaddingRight(), lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightSpec,
            getPaddingTop() + getPaddingBottom(), lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();

        int childLeft, childTop, childRight, childBottom;

        int vpad = getPaddingTop() + getPaddingBottom();
        int hpad = getPaddingLeft() + getPaddingRight();

        int vpix = getMeasuredHeight() - vpad;
        int hpix = getMeasuredWidth() - hpad;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {

                PercentLayout.LayoutParams lp = (PercentLayout.LayoutParams) child.getLayoutParams();
                childLeft = getPaddingLeft() + (int) (lp.x * hpix);
                childTop = getPaddingTop() + (int) (lp.y * vpix);
                if (child.getMeasuredWidth()!=0){
                    childRight = childLeft + child.getMeasuredWidth();
                } else {
                    childRight = childLeft + (int)(hpix * lp.wide);
                }
                if (child.getMeasuredWidth()!=0){
                    childBottom = childTop + child.getMeasuredHeight();
                } else {
                    childBottom = childTop + (int)(vpix * lp.high);
                }
                Rect rect = new Rect(childLeft,childTop,childRight,childBottom);
                Log.i("jl", "PercentLayout.onLayout: "+rect.flattenToString());

                child.layout(childLeft, childTop, childRight, childBottom);
            }
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new PercentLayout.LayoutParams(getContext(), attrs);
    }

    // Override to allow type-checking of LayoutParams.
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof PercentLayout.LayoutParams;
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
         * The horizontal width percentage
         */
        public float wide;
        /**
         * The vertical height percentage
         */
        public float high;
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
            super(width, height);
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
            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.PercentLayout);
            x = a.getFloat(R.styleable.PercentLayout_x, 0);
            y = a.getFloat(R.styleable.PercentLayout_y, 0);
            wide = a.getFloat(R.styleable.PercentLayout_wide, 0);
            high = a.getFloat(R.styleable.PercentLayout_high, 0);
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