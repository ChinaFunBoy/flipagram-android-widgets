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
package flipagram.android.text.watcher.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.widget.EditText;

/**
 * A {@link android.text.TextWatcher} that styles any text within the attached
 * {@link android.widget.EditText}. One or more
 * {@link flipagram.android.text.watcher.pattern.PatternCharacterStyleTextWatcher.PatternCharacterStyle}s
 * must be added to define the {@link java.util.regex.Pattern} within the text and the associated
 * {@link android.text.style.CharacterStyle}. Because this {@link android.text.TextWatcher} modifies
 * the text within the {@link android.widget.EditText}, it should be the last one in any chain.
 */
public class PatternCharacterStyleTextWatcher implements TextWatcher {

    private final EditText editText;

    private List<PatternCharacterStyle> patternCharacterStyles = new ArrayList<PatternCharacterStyle>();

    public PatternCharacterStyleTextWatcher(EditText editText){
        this.editText = editText;
    }

    /**
     * Add the {@link flipagram.android.text.watcher.pattern.PatternCharacterStyleTextWatcher} to
     * the internal list.
     * @param patternCharacterStyle
     * @return this for chaining
     */
    public PatternCharacterStyleTextWatcher addPatternCharacterStyle(PatternCharacterStyle patternCharacterStyle){
        patternCharacterStyles.add(patternCharacterStyle);
        return this;
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
    @Override public void afterTextChanged(Editable s) {
        for (PatternCharacterStyle patternCharacterStyle : patternCharacterStyles) {
            Matcher matcher = patternCharacterStyle.pattern.matcher(s);
            while(matcher.find()){
                s.setSpan(
                    CharacterStyle.wrap(patternCharacterStyle.characterStyle),
                    matcher.start(),
                    matcher.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    /**
     * Associate a {@link java.util.regex.Pattern} with a
     * {@link android.text.style.CharacterStyle}.
     */
    public static class PatternCharacterStyle {
        public Pattern pattern;
        public CharacterStyle characterStyle;

        public PatternCharacterStyle(Pattern pattern, CharacterStyle characterStyle){
            this.pattern = pattern;
            this.characterStyle = characterStyle;
        }
    }
}
