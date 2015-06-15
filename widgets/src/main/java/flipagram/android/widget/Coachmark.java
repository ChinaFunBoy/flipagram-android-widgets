package flipagram.android.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class Coachmark {
    public static final String PREFS_NAME = "Coachmark";
    private static final int TRIANGLE_BASE = 20; // dips
    private static final int TRIANGLE_CENTROID = 12; // dips
    private static final int MARGIN = 16; //dips
    private static final int COACHMARK_CORNER_RADIUS = 5; // dips
    private static final int BOUNCE = 10; // dips

    private final DisplayMetrics displayMetrics;
    private final Activity activity;
    private final View activityContent;
    private final List<Target> targets = new ArrayList<Target>();
    private final int backgroundColor;
    private final int textColor;
    private float textSize = 0;
    private boolean showCoachmarks = true;

    public static class Target {
        static final String YAXIS = "TranslationY";
        static final String XAXIS = "TranslationX";

        public enum Direction {
            North(YAXIS,-1),
            South(YAXIS,1),
            East(XAXIS,1),
            West(XAXIS,-1),
            ;
            String axis;
            int grows;
            Direction(String axis, int grows){
                this.axis = axis;
                this.grows = grows;
            }
        }

        private final View view;
        private Direction points = Direction.North;
        private String text;

        private Direction skewText = null;
        private float skewTextPercent;

        private Direction skewTriangle = null;
        private float skewTrianglePercent;

        private final TriangleView triangle;
        private final CoachTextView textView;

        private boolean bounce = false;

        private CircleTextView circle;
        private int circleBorderWidthDp;
        private float circlePercent = 1.0f;

        public View getView(){
            return this.view;
        }

        public Target(View view){
            this.view = view;
            this.triangle = new TriangleView(view.getContext());
            this.textView = new CoachTextView(view.getContext());
        }
        public Target pointing(Direction direction){
            this.points = direction;
            return this;
        }
        public Target skewTextToward(Direction direction, float percent){
            this.skewText = direction;
            this.skewTextPercent = percent;
            return this;
        }
        public Target skewTriangleToward(Direction direction, float percent){
            this.skewTriangle = direction;
            this.skewTrianglePercent = percent;
            return this;
        }
        public Target withText(String text){
            this.text = text;
            return this;
        }
        public Target withText(int id){
            this.text = view.getContext().getString(id);
            return this;
        }
        public Target withBounce(){
            bounce = true;
            return this;
        }
        public Target withBullseye(float percent, int borderColor, int borderWidthDp) {
            if (circle == null){
                circle = new CircleTextView(view.getContext());
                circle.setBorderColor(borderColor);
                circlePercent = percent;
                circleBorderWidthDp = borderWidthDp;
            }
            return this;
        }
    }


    public Coachmark(Activity activity, String key, int backgroundColor, int textColor){
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        this.activity = activity;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.activityContent = activity.findViewById(android.R.id.content);
        this.displayMetrics = activity.getResources().getDisplayMetrics();

        showCoachmarks = !settings.getBoolean(key,false); // Show them if we have not
        if (showCoachmarks){
            // Don't show them again
            settings.edit().putBoolean(key,true).apply();
        }
    }

    /**
     * Clear this key so the coachmarks will display again
     * @param cw ContextWrapper allowing access to the SharedPreferences
     * @param key the key to remove
     */
    public static void reset(ContextWrapper cw, String key) {
        SharedPreferences settings = cw.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        settings.edit().remove(key).apply();
    }

    /**
     * Clear all keys so the coachmarks will display again
     * @param cw ContextWrapper allowing access to the SharedPreferences
     */
    public static void reset(ContextWrapper cw) {
        SharedPreferences settings = cw.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        settings.edit().clear().apply();
    }

    public Coachmark withTarget(Target target){
        targets.add(target);
        return this;
    }

    public Coachmark force(){
        showCoachmarks = true;
        return this;
    }

    public Coachmark withTextSize(float sp){
        this.textSize = sp;
        return this;
    }

    /**
     * Show the coachmarks attached to a View
     * @return true if the coachmarks were shown. False otherwise.
     */
    public Coachmark show(){
        if (showCoachmarks) {
            addGlobalLayoutListener(activityContent, createCoachmarks);
        }
        return this;
    }

    /**
     * Create all coachmarks (TriangleView and CoachTextView). Positioning happens later.
     */
    private final ViewTreeObserver.OnGlobalLayoutListener createCoachmarks = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            removeGlobalLayoutListener(activityContent, this);
            addGlobalLayoutListener(activityContent, positionViews);

            final FrameLayout coachmarks = new FrameLayout(activity);
            coachmarks.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    coachmarks.setVisibility(View.GONE);
                    return false;
                }
            });
            for(Target target : targets){
                target.triangle.setLayoutParams(new FrameLayout.LayoutParams(
                        getTriangleHorizontalSize(target),
                        getTriangleVerticalSize(target)
                ));
                target.triangle.setTriangleFillColor(backgroundColor);

                target.textView.setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                target.textView.setPadding((int)dp(10),(int)dp(10),(int)dp(10),(int)dp(10));
                target.textView.setText(target.text);
                target.textView.setCoachRadius(dp(COACHMARK_CORNER_RADIUS));
                target.textView.setCoachFillColor(backgroundColor);
                target.textView.setTextColor(textColor);
                if (textSize>0){
                    target.textView.setTextSize(textSize);
                }
                if (target.circle!=null){
                    int circleSize = (int) (
                            Math.min((float)target.view.getWidth(),(float)target.view.getHeight())
                            * target.circlePercent);
                    target.circle.setLayoutParams(new FrameLayout.LayoutParams(circleSize, circleSize));
                    target.circle.setBorderSize((int) dp(target.circleBorderWidthDp));
                    target.circle.setFillColor(backgroundColor);
                    coachmarks.addView(target.circle);
                }
                coachmarks.addView(target.triangle); // Order of these...
                coachmarks.addView(target.textView); // adds is important
            }
            activity.addContentView(coachmarks,new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
        private int getTriangleHorizontalSize(Target target){
            switch(target.points){
                case East:
                case West:
                    return (int)dp(TRIANGLE_CENTROID);
                default: // North, South
                    return (int)dp(TRIANGLE_BASE);
            }
        }
        private int getTriangleVerticalSize(Target target){
            switch(target.points){
                case East:
                case West:
                    return (int)dp(TRIANGLE_BASE);
                default: // North, South
                    return (int)dp(TRIANGLE_CENTROID);
            }
        }
    };

    ViewTreeObserver.OnGlobalLayoutListener positionViews = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            removeGlobalLayoutListener(activityContent, this);
            for(Target target : targets) {
                positionTarget(target);
            }
        }
    };

    private void positionTarget(final Target target){

        /** textView overlaps triangle by this much to remove animation artifacts */
        int dp2 = (int) dp(2);

        Point viewPoint = getAbsoluteXY(target.view);

        /** The point at the center of the side ajacent to the View */
        Point textViewCenterOfAdjacentSide = new Point();

        /** The upper left corner of the text view. This is where it ends up */
        Point textViewPoint = new Point();

        /** The upper left corner of the triangle. This is where it ends up */
        Point trianglePoint = new Point();

        Point skewTextDimensions = getSkewTextDimensions(target);
        Point skewTriangleDimensions = getSkewTriangleDimensions(target);

        switch (target.points) {
            case South:
                target.triangle.setDirection(TriangleView.POSITION_SOUTH);
                textViewCenterOfAdjacentSide.offset(
                        viewPoint.x + target.view.getWidth() / 2,
                        viewPoint.y - target.triangle.getHeight());
                textViewCenterOfAdjacentSide.offset(skewTextDimensions.x,skewTextDimensions.y);
                textViewPoint.set(
                        textViewCenterOfAdjacentSide.x - target.textView.getWidth() / 2,
                        textViewCenterOfAdjacentSide.y - target.textView.getHeight() + dp2);
                trianglePoint.set(
                        textViewCenterOfAdjacentSide.x - target.triangle.getWidth() / 2,
                        textViewCenterOfAdjacentSide.y);
                trianglePoint.offset(skewTriangleDimensions.x,skewTriangleDimensions.y);
                break;
            case North:
                target.triangle.setDirection(TriangleView.POSITION_NORTH);
                textViewCenterOfAdjacentSide.offset(
                        viewPoint.x + target.view.getWidth() / 2,
                        viewPoint.y + target.view.getHeight() + target.triangle.getHeight());
                textViewCenterOfAdjacentSide.offset(skewTextDimensions.x,skewTextDimensions.y);
                textViewPoint.set(
                        textViewCenterOfAdjacentSide.x - target.textView.getWidth() / 2,
                        textViewCenterOfAdjacentSide.y - dp2);
                trianglePoint.set(
                        textViewCenterOfAdjacentSide.x - target.triangle.getWidth() / 2,
                        textViewCenterOfAdjacentSide.y - target.triangle.getHeight());
                trianglePoint.offset(skewTriangleDimensions.x,skewTriangleDimensions.y);
                break;
            case West:
                target.triangle.setDirection(TriangleView.POSITION_WEST);
                textViewCenterOfAdjacentSide.offset(
                        viewPoint.x + target.view.getWidth() + target.triangle.getWidth(),
                        viewPoint.y + target.view.getHeight() / 2);
                textViewCenterOfAdjacentSide.offset(skewTextDimensions.x,skewTextDimensions.y);
                textViewPoint.set(
                        textViewCenterOfAdjacentSide.x - dp2,
                        textViewCenterOfAdjacentSide.y - target.textView.getHeight() / 2);
                trianglePoint.set(
                        textViewCenterOfAdjacentSide.x - target.triangle.getWidth(),
                        textViewCenterOfAdjacentSide.y - target.triangle.getHeight() / 2);
                trianglePoint.offset(skewTriangleDimensions.x,skewTriangleDimensions.y);
                break;
            default:
            case East:
                target.triangle.setDirection(TriangleView.POSITION_EAST);
                textViewCenterOfAdjacentSide.offset(
                        viewPoint.x - target.triangle.getWidth(),
                        viewPoint.y + target.view.getHeight() / 2);
                textViewCenterOfAdjacentSide.offset(skewTextDimensions.x,skewTextDimensions.y);
                textViewPoint.set(
                        textViewCenterOfAdjacentSide.x - target.textView.getWidth() + dp2,
                        textViewCenterOfAdjacentSide.y - target.textView.getHeight() / 2);
                trianglePoint.set(
                        textViewCenterOfAdjacentSide.x,
                        textViewCenterOfAdjacentSide.y - target.triangle.getHeight() / 2);
                trianglePoint.offset(skewTriangleDimensions.x,skewTriangleDimensions.y);
                break;
        }
        Point fix = getFixDimensions(target, textViewPoint);
        textViewPoint.offset(fix.x,fix.y);
        trianglePoint.offset(fix.x,fix.y);

        // Now draw the circle based on where the triangle is now pointing
        if (target.circle!=null){
            Point circlePoint = new Point();
            int tHigh = target.triangle.getHeight();
            int tWide = target.triangle.getWidth();
            int cSize = target.circle.getWidth();
            int cBorderWidth2 = (int)(target.circle.getBorderSize() / 2);
            int cDistToCenter = (Math.min(target.view.getWidth(),target.view.getHeight())-cSize)/2;
            switch (target.points){
                case North:
                    trianglePoint.offset(0,-cDistToCenter);
                    textViewPoint.offset(0,-cDistToCenter);
                    circlePoint.set(trianglePoint.x,trianglePoint.y);
                    circlePoint.offset(tWide / 2 - cSize / 2, -cSize);
                    trianglePoint.offset(0,-cBorderWidth2);
                    textViewPoint.offset(0,-cBorderWidth2);
                    break;
                case South:
                    trianglePoint.offset(0,cDistToCenter);
                    textViewPoint.offset(0,cDistToCenter);
                    circlePoint.set(trianglePoint.x,trianglePoint.y);
                    circlePoint.offset(tWide / 2 - cSize / 2, tHigh);
                    trianglePoint.offset(0,cBorderWidth2);
                    textViewPoint.offset(0,cBorderWidth2);
                    break;
                case East:
                    trianglePoint.offset(cDistToCenter,0);
                    textViewPoint.offset(cDistToCenter,0);
                    circlePoint.set(trianglePoint.x,trianglePoint.y);
                    circlePoint.offset(tWide, tHigh / 2 - cSize / 2);
                    trianglePoint.offset(cBorderWidth2,0);
                    textViewPoint.offset(cBorderWidth2,0);
                    break;
                case West:
                    trianglePoint.offset(-cDistToCenter,0);
                    textViewPoint.offset(-cDistToCenter,0);
                    circlePoint.set(trianglePoint.x,trianglePoint.y);
                    circlePoint.offset(-cSize, tHigh / 2 - cSize / 2);
                    trianglePoint.offset(-cBorderWidth2,0);
                    textViewPoint.offset(-cBorderWidth2,0);
                    break;
            }
            target.circle.setTranslationX(circlePoint.x);
            target.circle.setTranslationY(circlePoint.y);
        }
        target.textView.setTranslationX(textViewPoint.x);
        target.textView.setTranslationY(textViewPoint.y);
        target.triangle.setTranslationX(trianglePoint.x);
        target.triangle.setTranslationY(trianglePoint.y);

        if (target.bounce) {
            for(View view : new View[]{target.triangle,target.textView}) {
                AnimatorSet set = new AnimatorSet();
                float initial = getInitialValue(view, target.points.axis);
                ObjectAnimator[] animators = new ObjectAnimator[]{
                        ObjectAnimator.ofFloat(
                                view,
                                target.points.axis,
                                initial,
                                initial - dp(BOUNCE) * target.points.grows
                        )
                };
                for( ObjectAnimator animator : animators){
                    animator.setDuration(500);
                    animator.setRepeatMode(ObjectAnimator.REVERSE);
                    animator.setRepeatCount(ObjectAnimator.INFINITE);
                }
                set.playSequentially(animators);
                set.start();
            }
        }
    }

    private float getInitialValue(View view, String name){
        try {
            return (Float)view.getClass().getMethod("get"+name).invoke(view);
        } catch (Exception e) {
            throw new Error(e); // This should only break during development
        }
    }

    private Point getAbsoluteXY(View view){
        Point point = new Point();
        point.offset(view.getLeft(),view.getTop());
        ViewParent parent = view.getParent();
        while(parent!=null && parent!=activityContent){
            if (parent instanceof View) {
                view = (View) parent;
                point.offset(view.getLeft(), view.getTop());
            }
            parent = parent.getParent();
        }
        return point;
    }

    private Point getFixDimensions(Target target, Point textViewPoint){
        Point fix = new Point();
        int margin = (int)dp(MARGIN);
        if (textViewPoint.x<margin){
            fix.x = margin-textViewPoint.x;
        } else if ((textViewPoint.x+target.textView.getWidth()) > (activityContent.getWidth()-margin)){
            fix.x = (activityContent.getWidth()-margin) - (textViewPoint.x+target.textView.getWidth());
        }
        if (textViewPoint.y<margin){
            fix.y = margin-textViewPoint.y;
        } else if ((textViewPoint.y+target.textView.getHeight()) > (activityContent.getHeight()-margin)){
            fix.y = (activityContent.getHeight()-margin) - (textViewPoint.y+target.textView.getHeight());
        }
        return fix;
    }

    private Point getSkewTextDimensions(Target target){
        Point dim = new Point();
        if (target.skewText!=null){
            if (target.points.axis==target.skewText.axis){
                throw new IllegalArgumentException("Bad skew direction");
            }
            if (target.skewText.axis==Target.YAXIS) {
                dim.set(0,target.skewText.grows * (int) ( ((float)target.view.getHeight()/2) * target.skewTextPercent));
            } else {
                dim.set(target.skewText.grows * (int) ( ((float)target.view.getWidth()/2) * target.skewTextPercent),0);
            }
        }
        return dim;
    }

    private Point getSkewTriangleDimensions(Target target){
        Point dim = new Point();
        float twoRadians = dp(COACHMARK_CORNER_RADIUS * 2);
        if (target.skewTriangle!=null){
            if (target.points.axis==target.skewTriangle.axis){
                throw new IllegalArgumentException("Bad skew direction");
            }
            if (target.skewTriangle.axis==Target.YAXIS) {
                float maxHigh = target.textView.getHeight() / 2 - twoRadians;
                dim.set(0,target.skewTriangle.grows * (int) ( maxHigh * target.skewTrianglePercent));
            } else {
                float maxWide = target.textView.getWidth() / 2 - twoRadians;
                dim.set(target.skewTriangle.grows * (int) ( maxWide * target.skewTrianglePercent),0);
            }
        }
        return dim;
    }

    private void addGlobalLayoutListener(View onView, ViewTreeObserver.OnGlobalLayoutListener listener){
        onView.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }

    @SuppressWarnings("deprecation")
    private void removeGlobalLayoutListener(View onView, ViewTreeObserver.OnGlobalLayoutListener listener){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            onView.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            onView.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    private float dp(int fromDp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, fromDp, displayMetrics);
    }

}
