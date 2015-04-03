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
package flipagram.android.widget.onmeasure;

import android.view.View;

public class PaddingSquare {
    public static void measure(View view, int widthMeasureSpec, int heightMeasureSpec){
        int basePad = Math.min(view.getPaddingLeft(),
            Math.min(view.getPaddingTop(),
                Math.min(view.getPaddingRight(),view.getPaddingBottom())));

        final float width = View.MeasureSpec.getSize(widthMeasureSpec);
        final float height = View.MeasureSpec.getSize(heightMeasureSpec);

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
