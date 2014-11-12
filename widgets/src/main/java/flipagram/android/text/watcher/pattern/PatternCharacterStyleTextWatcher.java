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
