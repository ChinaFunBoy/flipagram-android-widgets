package flipagram.android.text.watcher.pattern;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternCharacterStyleTextWatcher implements TextWatcher {

    private final EditText editText;

    private String prev = null;
    private List<PatternCharacterStyle> patternCharacterStyles = new ArrayList<PatternCharacterStyle>();

    public PatternCharacterStyleTextWatcher(EditText editText){
        this.editText = editText;
    }

    public PatternCharacterStyleTextWatcher addPatternCharacterStyle(PatternCharacterStyle patternCharacterStyle){
        patternCharacterStyles.add(patternCharacterStyle);
        return this;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (prev==null || !prev.equals(s.toString()) ) {
            prev = s.toString();
            int ss = editText.getSelectionStart();
            int se = editText.getSelectionEnd();
            editText.setText(applyCharacterStyles(s));
            editText.setSelection(ss,se);
        }
    }
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
    @Override public void afterTextChanged(Editable s) { }

    /**
     *
     * @param s the input CharSequence
     * @return a new CharSequence with spans set
     */
    private CharSequence applyCharacterStyles(CharSequence s){
        Spannable spannable = new SpannableString(s);

        // Remove old spans
        Object[] spans = spannable.getSpans(0,spannable.length(), Object.class);
        for (Object span : spans)
            spannable.removeSpan(span);

        // Create new spans to give CharacterStyles
        for (PatternCharacterStyle patternCharacterStyle : patternCharacterStyles) {
            Matcher matcher = patternCharacterStyle.pattern.matcher(s);
            while(matcher.find()){
                spannable.setSpan(
                    CharacterStyle.wrap(patternCharacterStyle.characterStyle),
                    matcher.start(),
                    matcher.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannable;
    }

    /**
     * The PatternCharacterStyle class associates a Pattern with a CharacterStyle
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
