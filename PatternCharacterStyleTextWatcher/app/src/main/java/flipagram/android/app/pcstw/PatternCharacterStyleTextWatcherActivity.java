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
package flipagram.android.app.pcstw;

import android.app.Activity;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;

import java.util.regex.Pattern;

import flipagram.android.text.watcher.pattern.PatternCharacterStyleTextWatcher;


public class PatternCharacterStyleTextWatcherActivity extends Activity {

    private final static String TAG = PatternCharacterStyleTextWatcherActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editText = (EditText) findViewById(R.id.editText);

        PatternCharacterStyleTextWatcher tw = new PatternCharacterStyleTextWatcher(editText);

        tw.addPatternCharacterStyle(new PatternCharacterStyleTextWatcher.PatternCharacterStyle(
            Pattern.compile("@[A-z][A-z0-9\\._\\-]*"),
            new ForegroundColorSpan(getResources().getColor(android.R.color.holo_orange_dark))
        ));

        tw.addPatternCharacterStyle(new PatternCharacterStyleTextWatcher.PatternCharacterStyle(
            Pattern.compile("#[A-z0-9]+"),
            new ForegroundColorSpan(getResources().getColor(android.R.color.holo_blue_dark))
        ));

        editText.addTextChangedListener(tw);

    }

}
