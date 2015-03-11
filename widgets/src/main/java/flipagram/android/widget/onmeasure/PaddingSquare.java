package flipagram.android.widget.onmeasure;

import android.view.View;

public class PaddingSquare {
    public static void measure(View view, int widthMeasureSpec, int heightMeasureSpec){
        int basePad = Math.min(view.getPaddingLeft(),
            Math.min(view.getPaddingTop(),
                Math.min(view.getPaddingRight(),view.getPaddingBottom())));

        final float width = view.getWidth();
        final float height = view.getHeight();

        if (width==height){
            view.setPadding(basePad,basePad,basePad,basePad);
        } else if (width>height) {
            float diffPad = (width-height)/2;
            view.setPadding((int)(basePad+diffPad),basePad,(int)(basePad+diffPad),basePad);
        } else { // width<height
            float diffPad = (height-width)/2;
            view.setPadding(basePad,(int)(basePad+diffPad),basePad,(int)(basePad+diffPad));
        }
    }
}
