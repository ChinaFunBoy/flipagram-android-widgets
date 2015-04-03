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
