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

    private final SharedPreferences settings;
    private final DisplayMetrics displayMetrics;
    private final Activity activity;
    private final View activityContent;
    private final String key;
    private final List<Target> targets = new ArrayList<Target>();
    private final int backgroundColor;
    private final int textColor;
    private final int actionBarHeight;
    private boolean showCoachmarks = true;
    private boolean hasActionBar = true;


    public abstract static class Target{
        public enum Direction {North, South, East, West}

        protected Direction points = Direction.North;
        protected Direction skew = null;
        protected float skewPercent;
        protected String text;

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
         * @param actionBarHeight
         * @return
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
        public Target skew(Direction skew, float percent){
            this.skew = skew;
            this.skewPercent = percent;
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

    private Coachmark(){
        throw new IllegalArgumentException("Activity can't be null");
    }

    public Coachmark(Activity activity, String key, int backgroundColor, int textColor){
        this.activity = activity;
        this.key = key;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;
        this.settings = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.activityContent = activity.findViewById(android.R.id.content);
        this.displayMetrics = activity.getResources().getDisplayMetrics();

        TypedValue tv = new TypedValue();
        actionBarHeight = activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)?
                TypedValue.complexToDimensionPixelSize(tv.data, displayMetrics):
                0;

        showCoachmarks = !settings.getBoolean(key,false); // Show them if we have not
        if (showCoachmarks){
            // Don't show them again
            settings.edit().putBoolean(key,true).commit();
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
                target.textView.setCoachRadius(dp(5));
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
        private float dp(int fromDp){
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, fromDp, displayMetrics);
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
        Point triMidBase = new Point();
        int offsetX = 0;
        int offsetY = target.getOffsetGivenActionBarHeight(hasActionBar?actionBarHeight:0);

        switch (target.points) {
            case South:
                target.triangle.setDirection(TriangleView.POSITION_SOUTH);
                triMidBase.set(
                        offsetX + view.getLeft() + view.getWidth() / 2,
                        offsetY + view.getTop() - target.triangle.getHeight()
                );
                target.triangle.setTranslationX(triMidBase.x - target.triangle.getWidth() / 2);
                target.triangle.setTranslationY(triMidBase.y);

                target.textView.setTranslationX(triMidBase.x - target.textView.getWidth() / 2);
                target.textView.setTranslationY(triMidBase.y - target.textView.getHeight());
                break;
            case North:
                target.triangle.setDirection(TriangleView.POSITION_NORTH);
                triMidBase.set(
                        offsetX + view.getLeft() + view.getWidth() / 2,
                        offsetY + view.getTop() + view.getHeight() + target.triangle.getHeight()
                );
                target.triangle.setTranslationX(triMidBase.x - target.triangle.getWidth() / 2);
                target.triangle.setTranslationY(triMidBase.y - target.triangle.getHeight());

                target.textView.setTranslationX(triMidBase.x - target.textView.getWidth() / 2);
                target.textView.setTranslationY(triMidBase.y);
                break;
            case West:
                target.triangle.setDirection(TriangleView.POSITION_WEST);
                triMidBase.set(
                        offsetX + view.getLeft() + view.getWidth() + target.triangle.getWidth(),
                        offsetY + view.getTop() + view.getHeight() / 2
                );
                target.triangle.setTranslationX(triMidBase.x - target.triangle.getWidth());
                target.triangle.setTranslationY(triMidBase.y - target.triangle.getHeight() / 2);

                target.textView.setTranslationX(triMidBase.x);
                target.textView.setTranslationY(triMidBase.y - target.textView.getHeight() / 2);
                break;
            default:
            case East:
                target.triangle.setDirection(TriangleView.POSITION_EAST);
                triMidBase.set(
                        offsetX + view.getLeft() - target.triangle.getWidth(),
                        offsetY + view.getTop() + view.getHeight() / 2
                );
                target.triangle.setTranslationX(triMidBase.x);
                target.triangle.setTranslationY(triMidBase.y - target.triangle.getHeight() / 2);

                target.textView.setTranslationX(triMidBase.x - target.textView.getWidth());
                target.textView.setTranslationY(triMidBase.y - target.textView.getHeight() / 2);
                break;
        }
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
}
