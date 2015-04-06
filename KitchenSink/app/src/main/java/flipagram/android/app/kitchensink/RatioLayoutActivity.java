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
package flipagram.android.app.kitchensink;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import flipagram.android.widget.RatioDynamicLayout;

public class RatioLayoutActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratio_layout);

        RatioDynamicLayout.LayoutParams.addDynamicLayout(findViewById(R.id.root), layoutListener);
    }

    private RatioDynamicLayout.LayoutParams.Listener layoutListener = new RatioDynamicLayout.LayoutParams.Listener() {
        @Override
        public Point measureForContainer(RatioDynamicLayout.LayoutParams lp, int containerWidth, int containerHeight) {
            Point pixels = new Point();
            pixels.x = (int)(containerWidth * (2*lp.ratio));
            pixels.y = (int)(containerHeight * (0.5*lp.ratio));
            return pixels;
        }
    };

}
