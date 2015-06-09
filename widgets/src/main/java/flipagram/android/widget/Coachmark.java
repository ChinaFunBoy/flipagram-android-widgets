package flipagram.android.widget;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
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
    private final List<TargetView> targetViews = new ArrayList<TargetView>();
    private final int backgroundColor;

    public static class TargetView {
        private final View view;
        private Direction direction;
        private String text;

        private final TriangleView triangle;
        private final CoachTextView textView;

        private TargetView(){
            throw new IllegalArgumentException("View can't be null");
        }

        public enum Direction {North, South, East, West}
        public TargetView(View view){
            this.view = view;
            this.triangle = new TriangleView(view.getContext());
            this.textView = new CoachTextView(view.getContext());
        }
        public TargetView fromDirection(Direction direction){
            this.direction = direction;
            return this;
        }
        public TargetView withText(String text){
            this.text = text;
            return this;
        }
    }

    private Coachmark(){
        throw new IllegalArgumentException("Activity can't be null");
    }

    public Coachmark(Activity activity, String key, int backgroundColor){
        this.activity = activity;
        this.key = key;
        this.backgroundColor = backgroundColor;
        this.settings = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.activityContent = activity.findViewById(android.R.id.content);
        this.displayMetrics = activity.getResources().getDisplayMetrics();
    }

    public Coachmark withTargetView(TargetView targetView){
        targetViews.add(targetView);
        return this;
    }

    public Coachmark removeKey(){
        settings.edit().remove(key).commit();
        return this;
    }

    /**
     * Show the coachmarks to the user
     * @return true if the coachmarks were shown. False otherwise.
     */
    public boolean show(){
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean alreadySeen = settings.getBoolean(key,false);
        if (alreadySeen) {
            // Not showing, the user has already seen it.
            return false;
        }

        activityContent.getViewTreeObserver().addOnGlobalLayoutListener(drawCoachmarks);

        // Don't show them again
        settings.edit().putBoolean(key,true).commit();
        return true;
    }

    private final ViewTreeObserver.OnGlobalLayoutListener drawCoachmarks = new ViewTreeObserver.OnGlobalLayoutListener() {
        @SuppressWarnings("deprecation")
        @Override
        public void onGlobalLayout() {
            // Only once
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                activityContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
                activityContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
            activityContent.getViewTreeObserver().addOnGlobalLayoutListener(positionCoachmarks);

            final FrameLayout coachmarks = new FrameLayout(activity);
            coachmarks.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    coachmarks.setVisibility(View.GONE);
                    return false;
                }
            });
            for(TargetView target : targetViews){
                target.triangle.setLayoutParams(new FrameLayout.LayoutParams(
                        getTriangleHorizontalSize(target),
                        getTriangleVerticalSize(target)
                ));
                target.triangle.setTriangleFillColor(backgroundColor);
                coachmarks.addView(target.triangle);
            }
            activity.addContentView(coachmarks,new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
        private int getTriangleHorizontalSize(TargetView target){
            switch(target.direction){
                case East:
                case West:
                    return (int)dp(TRIANGLE_CENTROID);
                default: // North, South
                    return (int)dp(TRIANGLE_BASE);
            }
        }
        private int getTriangleVerticalSize(TargetView target){
            switch(target.direction){
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

    private final ViewTreeObserver.OnGlobalLayoutListener positionCoachmarks = new ViewTreeObserver.OnGlobalLayoutListener() {
        @SuppressWarnings("deprecation")
        @Override
        public void onGlobalLayout() {
            // Only once
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                activityContent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
                activityContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
            for(TargetView target : targetViews){
                switch(target.direction){
                    case North:
                        target.triangle.setDirection(TriangleView.POSITION_SOUTH);
                        target.triangle.setTranslationX(target.view.getLeft()+target.view.getWidth()/2-target.triangle.getWidth()/2);
                        target.triangle.setTranslationY(target.view.getTop() - target.triangle.getHeight());
                        break;
                    case South:
                        target.triangle.setDirection(TriangleView.POSITION_NORTH);
                        target.triangle.setTranslationX(target.view.getLeft()+target.view.getWidth()/2-target.triangle.getWidth()/2);
                        target.triangle.setTranslationY(target.view.getTop()+target.view.getHeight());
                        break;
                    case East:
                        target.triangle.setDirection(TriangleView.POSITION_WEST);
                        target.triangle.setTranslationX(target.view.getLeft()+target.view.getWidth());
                        target.triangle.setTranslationY(target.view.getTop()+target.view.getHeight()/2-target.triangle.getHeight()/2);
                        break;
                    default:
                    case West:
                        target.triangle.setDirection(TriangleView.POSITION_EAST);
                        target.triangle.setTranslationX(target.view.getLeft()-target.triangle.getWidth());
                        target.triangle.setTranslationY(target.view.getTop()+target.view.getHeight()/2-target.triangle.getHeight()/2);
                        break;
                }
            }
        }
    };
}
