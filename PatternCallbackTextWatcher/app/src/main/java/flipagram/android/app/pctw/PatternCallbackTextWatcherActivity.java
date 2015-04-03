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
package flipagram.android.app.pctw;

import android.app.Activity;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.EditText;

import java.util.regex.Pattern;

import flipagram.android.text.watcher.pattern.PatternCallbackTextWatcher;


public class PatternCallbackTextWatcherActivity extends Activity {

    private final static String TAG = PatternCallbackTextWatcherActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editText = (EditText) findViewById(R.id.editText);

        PatternCallbackTextWatcher tw = new PatternCallbackTextWatcher(editText);

        PatternCallbackTextWatcher.Callback logit = new PatternCallbackTextWatcher.Callback() {
            @Override
            public void onMatch(int matchStart, int matchEnd) {
                Log.i(TAG,
                    "start="+matchStart+
                        " end="+matchEnd+
                        " string="+editText.getText().toString().substring(matchStart,matchEnd));
            }

            @Override
            public void noMatch() {
                Log.i(TAG,"noMatch");
            }
        };

        tw.addPatternCallback(new PatternCallbackTextWatcher.PatternCallback(
            Pattern.compile("@[A-z][A-z0-9\\._\\-]*"), logit));

        tw.addPatternCallback(new PatternCallbackTextWatcher.PatternCallback(
            Pattern.compile("#[A-z0-9]+"), logit ));

        editText.addTextChangedListener(tw);

    }
}
