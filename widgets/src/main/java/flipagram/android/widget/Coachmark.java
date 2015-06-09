package flipagram.android.widget;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class Coachmark {
    public static final String PREFS_NAME = "Coachmark";
    private static final int TRIANGLE_BASE = 20; // dips
    private static final int TRIANGLE_CENTROID = 10; // dips
    private static final int MARGIN = 16; //dips
    private static final int COACHMARK_CORNER_RADIUS = 5;

    private final DisplayMetrics displayMetrics;
    private final Activity activity;
    private final View activityContent;
    private final List<Target> targets = new ArrayList<Target>();
    private final int backgroundColor;
    private final int textColor;
    private final int actionBarHeight;
    private boolean showCoachmarks = true;
    private boolean hasActionBar = true;

    public abstract static class Target{
        static final int LATITUDINAL = 1;
        static final int LONGITUDINAL= 2;
        public enum Direction {
            North(LATITUDINAL,-1),
            South(LATITUDINAL,1),
            East(LONGITUDINAL,1),
            West(LONGITUDINAL,-1),
            ;
            int vector;
            int grows;
            Direction(int vector, int grows){
                this.vector = vector;
                this.grows = grows;
            }
        }

        protected Direction points = Direction.North;
        protected String text;

        protected Direction skewText = null;
        protected float skewTextPercent;

        protected Direction skewTriangle = null;
        protected float skewTrianglePercent;

        protected final TriangleView triangle;
        protected final CoachTextView textView;

        public abstract View getView();

        /**
         * It's up to the specific Target to decide how far it should skew to the south given the
         * actionBarHeight. Normally this is either 0 or actionBarHeight. This is because some
         * Views are expressed in coordinates that are relative to the bottom of the notification
         * bar and some are expressed in coordinates relative to the bottom of the action bar.
         * This probably isn't the best way of handling this particular Android wrinkle, but it
         * works for now.
         * @param actionBarHeight the height of the action bar
         * @return the chosen offset
         */
        public abstract int getOffsetGivenActionBarHeight(int actionBarHeight);

        protected Target(Context context){
            this.triangle = new TriangleView(context);
            this.textView = new CoachTextView(context);
        }
        public Target pointing(Direction direction){
            this.points = direction;
            return this;
        }
        public Target skewTextDirection(Direction direction, float percent){
            this.skewText = direction;
            this.skewTextPercent = percent;
            return this;
        }
        public Target skewTriangleDirection(Direction direction, float percent){
            this.skewTriangle = direction;
            this.skewTrianglePercent = percent;
            return this;
        }
        public Target withText(String text){
            this.text = text;
            return this;
        }
    }

    public static class TargetView extends Target {
        private final View view;

        public TargetView(View view){
            super(view.getContext());
            this.view = view;
        }

        public View getView(){
            return this.view;
        }

        public int getOffsetGivenActionBarHeight(int actionBarHeight){
            return actionBarHeight;
        }
    }

    /**
     * Android has a multitude of ways of working with the thing at the top of the screen called
     * the ActionBar. One of them is a Toolbar acting as an ActionBar. This Target knows how to
     * deal with that particular circumstance. Android. Sigh.
     */
    public static class TargetToolbarActionBar extends Target {
        private final Toolbar toolbar;

        public TargetToolbarActionBar(Toolbar toolbar){
            super(toolbar.getContext());
            this.toolbar = toolbar;
        }

        public View getView(){
            return toolbar;
        }

        public int getOffsetGivenActionBarHeight(int actionBarHeight){
            return 0;
        }
    }

    public Coachmark(Activity activity, String key, int backgroundColor, int textColor){
        TypedValue tv = new TypedValue();
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        this.activity = activity;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.activityContent = activity.findViewById(android.R.id.content);
        this.displayMetrics = activity.getResources().getDisplayMetrics();

        actionBarHeight = activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)?
            TypedValue.complexToDimensionPixelSize(tv.data, displayMetrics):
            0;

        showCoachmarks = !settings.getBoolean(key,false); // Show them if we have not
        if (showCoachmarks){
            // Don't show them again
            settings.edit().putBoolean(key,true).apply();
        }
    }

    public Coachmark withTarget(Target target){
        targets.add(target);
        return this;
    }

    public Coachmark force(){
        showCoachmarks = true;
        return this;
    }

    public Coachmark hasActionBar(boolean hasActionBar) {
        this.hasActionBar = hasActionBar;
        return this;
    }

    /**
     * Show the coachmarks attached to a View
     * @return true if the coachmarks were shown. False otherwise.
     */
    public Coachmark showTargetViews(){
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
                coachmarks.addView(target.triangle);

                target.textView.setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                target.textView.setPadding((int)dp(10),(int)dp(10),(int)dp(10),(int)dp(10));
                target.textView.setText(target.text);
                target.textView.setCoachRadius(dp(COACHMARK_CORNER_RADIUS));
                target.textView.setCoachFillColor(backgroundColor);
                target.textView.setTextColor(textColor);
                coachmarks.addView(target.textView);
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
                positionTarget(target,target.getView());
            }
        }
    };

    private void positionTarget(Target target, View view){
        /** The point at the center of the side ajacent to the View */
        Point textViewCenterOfAdjacentSide = new Point(
                0,
                target.getOffsetGivenActionBarHeight(hasActionBar?actionBarHeight:0)
        );

        /** The upper left corner of the text view. This is where it ends up */
        Point textViewPoint = new Point();

        /** The upper left corner of the triangle. This is where it ends up */
        Point trianglePoint = new Point();

        Point skewTextDimensions = getSkewTextDimensions(target,view);
        Point skewTriangleDimensions = getSkewTriangleDimensions(target);

        switch (target.points) {
            case South:
                target.triangle.setDirection(TriangleView.POSITION_SOUTH);
                textViewCenterOfAdjacentSide.offset(
                        view.getLeft() + view.getWidth() / 2,
                        view.getTop() - target.triangle.getHeight());
                textViewCenterOfAdjacentSide.offset(skewTextDimensions.x,skewTextDimensions.y);
                textViewPoint.set(
                        textViewCenterOfAdjacentSide.x - target.textView.getWidth() / 2,
                        textViewCenterOfAdjacentSide.y - target.textView.getHeight());
                trianglePoint.set(
                        textViewCenterOfAdjacentSide.x - target.triangle.getWidth() / 2,
                        textViewCenterOfAdjacentSide.y);
                trianglePoint.offset(skewTriangleDimensions.x,skewTriangleDimensions.y);
                break;
            case North:
                target.triangle.setDirection(TriangleView.POSITION_NORTH);
                textViewCenterOfAdjacentSide.offset(
                        view.getLeft() + view.getWidth() / 2,
                        view.getTop() + view.getHeight() + target.triangle.getHeight());
                textViewCenterOfAdjacentSide.offset(skewTextDimensions.x,skewTextDimensions.y);
                textViewPoint.set(
                        textViewCenterOfAdjacentSide.x - target.textView.getWidth() / 2,
                        textViewCenterOfAdjacentSide.y);
                trianglePoint.set(
                        textViewCenterOfAdjacentSide.x - target.triangle.getWidth() / 2,
                        textViewCenterOfAdjacentSide.y - target.triangle.getHeight());
                trianglePoint.offset(skewTriangleDimensions.x,skewTriangleDimensions.y);
                break;
            case West:
                target.triangle.setDirection(TriangleView.POSITION_WEST);
                textViewCenterOfAdjacentSide.offset(
                        view.getLeft() + view.getWidth() + target.triangle.getWidth(),
                        view.getTop() + view.getHeight() / 2);
                textViewCenterOfAdjacentSide.offset(skewTextDimensions.x,skewTextDimensions.y);
                textViewPoint.set(
                        textViewCenterOfAdjacentSide.x,
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
                        view.getLeft() - target.triangle.getWidth(),
                        view.getTop() + view.getHeight() / 2);
                textViewCenterOfAdjacentSide.offset(skewTextDimensions.x,skewTextDimensions.y);
                textViewPoint.set(
                        textViewCenterOfAdjacentSide.x - target.textView.getWidth(),
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
        target.textView.setTranslationX(textViewPoint.x);
        target.textView.setTranslationY(textViewPoint.y);
        target.triangle.setTranslationX(trianglePoint.x);
        target.triangle.setTranslationY(trianglePoint.y);
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

    private Point getSkewTextDimensions(Target target, View view){
        Point dim = new Point();
        if (target.skewText!=null){
            if (target.skewText !=null && target.points.vector==target.skewText.vector){
                throw new IllegalArgumentException("Bad skew direction");
            }
            if (target.skewText.vector==Target.LATITUDINAL) {
                dim.set(0,target.skewText.grows * (int) ( ((float)view.getHeight()/2) * target.skewTextPercent));
            } else {
                dim.set(target.skewText.grows * (int) ( ((float)view.getWidth()/2) * target.skewTextPercent),0);
            }
        }
        return dim;
    }

    private Point getSkewTriangleDimensions(Target target){
        Point dim = new Point();
        float twoRadians = dp(COACHMARK_CORNER_RADIUS * 2);
        if (target.skewTriangle!=null){
            if (target.skewTriangle !=null && target.points.vector==target.skewTriangle.vector){
                throw new IllegalArgumentException("Bad skew direction");
            }
            if (target.skewTriangle.vector==Target.LATITUDINAL) {
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
