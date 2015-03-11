package flipagram.android.widget.ondraw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class PaddingColor {
    public static void draw(View view, Canvas canvas, Paint paint){
        final float left = view.getPaddingLeft();
        final float top = view.getPaddingTop();
        final float right = view.getPaddingRight();
        final float bottom = view.getPaddingBottom();

        final float width = view.getWidth();
        final float height = view.getHeight();

        canvas.drawRect(0, 0, left, height, paint);
        canvas.drawRect(left, 0, width - right, top, paint);
        canvas.drawRect(width - right, 0, width, height, paint);
        canvas.drawRect(left, height - bottom, width - right, height, paint);
    }
}
