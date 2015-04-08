package flipagram.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class StyledTextView extends View {

    private final Paint paint = new Paint();

    private CharSequence text;
    private boolean hasDropShadow = false;
    private boolean hasTickMark = false;

    public StyledTextView(Context context) {
        this(context, null);
    }

    public StyledTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StyledTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Default values
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setAntiAlias(true);
        paint.setTextScaleX(1.0f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (text==null || text.length()==0){
            return;
        }

        int x = paint.getTextAlign() == Paint.Align.LEFT ?
            0 :
            canvas.getWidth() / (paint.getTextAlign() == Paint.Align.RIGHT ? 1 : 2);
        int y = 0;

        // get text bounds
        // This doesn't take into account multi-line text, so the
        // result will be too wide, but we only care about bounds.top.
        Rect bounds = new Rect();
        paint.getTextBounds(text.toString(), 0, text.length(), bounds);

        // calculate x and y
        y -= bounds.top;

        // show start pos for debugging only
        if (hasTickMark) {
            Paint p = new Paint();
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(2);
            p.setColor(0xFFFFFF00);
            int l = 12;
            canvas.drawLine(x - l, y, x + l, y, p);
            canvas.drawLine(x, y - l, x, y + l, p);
        }

        Paint.FontMetrics fm = paint.getFontMetrics();

        // draw it
        final float shadowSize = 5F/44F * paint.getTextSize();
        for (String piece : text.toString().split("\n")) {
            if (hasDropShadow) {
                paint.setShadowLayer(shadowSize, 0, 0, Color.argb(255 / 4, 0, 0, 0));
                canvas.drawText(piece, x, y, paint);
                paint.clearShadowLayer();
            }
            canvas.drawText(piece, x, y, paint);
            y += -fm.top + fm.leading;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int setWidth;
        int setHeight;

        Point size = null;

        if (widthMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            setWidth = widthSize;
        } else {
            size = calculateSize();
            if (widthMeasureSpec == MeasureSpec.AT_MOST) {
                setWidth = Math.min(widthSize, size.x);
            } else {
                setWidth = size.x;
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            setHeight = heightSize;
        } else {
            if (size==null){
                size = calculateSize();
            }
            if (heightMeasureSpec == MeasureSpec.AT_MOST) {
                setHeight = Math.min(heightSize, size.y);
            } else {
                setHeight = size.y;
            }
        }

        setMeasuredDimension(setWidth, setHeight);
    }

    /**
     * Calculate the width/depth of the View in pixels
     * @return a Point that has the width/depth of the View in pixels
     */
    public Point calculateSize(){
        final Point ret = new Point();
        final Rect textBounds = new Rect();
        final String endCaps = "..";
        final Paint.FontMetrics fm = paint.getFontMetrics();
        final StringBuilder builder = new StringBuilder();

        // Leading or trailing whitespace isn't measured by getTextBounds() unless we
        // surround it with some printable character and subtract those later.
        paint.getTextBounds(endCaps, 0, endCaps.length(), textBounds);
        final int endCapsLength = textBounds.width();

        float lineHeight = -fm.top + fm.leading;
        for (String line : text.toString().split("\n")) {
            line = builder.append(".").append(line).append(".").toString();
            builder.setLength(0);
            paint.getTextBounds(line, 0, line.length(), textBounds);
            ret.x = Math.max(ret.x, textBounds.width() - endCapsLength);
            ret.y += lineHeight;
        }
        return ret;
    }

    // getter / setters

    public void setDropShadow(boolean hasDropShadow) { this.hasDropShadow = hasDropShadow; }
    public boolean getDropShadow() { return hasDropShadow; }

    public CharSequence getText() { return text; }
    public void setText(CharSequence text) { this.text = text; }

    public Paint.Align getAlign(){ return paint.getTextAlign(); }
    public void setAlign(Paint.Align align) { paint.setTextAlign(align); }

    public int getTextColor() { return paint.getColor(); }
    public void setTextColor(int textColor) { paint.setColor(textColor); }

    public Typeface getTypeface() { return paint.getTypeface(); }
    public void setTypeface(Typeface typeface) { paint.setTypeface(typeface); }

    public float getTextSize() { return paint.getTextSize(); }
    public void setTextSize(float size) { paint.setTextSize(size); }

    public boolean getHasTickMark() { return hasTickMark; }
    public void setHasTickMark(boolean hasTickMark) { this.hasTickMark = hasTickMark; }

}
