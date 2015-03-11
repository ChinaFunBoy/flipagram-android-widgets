package flipagram.android.widget.ondraw;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class PaddingFrame {
    public static void draw(View view, Canvas canvas, Paint paint){
        final float left = view.getPaddingLeft();
        final float top = view.getPaddingTop();
        final float right = view.getPaddingRight();
        final float bottom = view.getPaddingBottom();

        final float width = view.getWidth();
        final float height = view.getHeight();

        // Draw the frame
        float stroke = paint.getStrokeWidth();
        if (paint.getStrokeWidth()>0.0){
            float halfStroke = stroke/2;
            canvas.drawRect(
                left-halfStroke,
                top-halfStroke,
                width-right+halfStroke,
                height-bottom+halfStroke,
                paint
            );
        }
    }

}
