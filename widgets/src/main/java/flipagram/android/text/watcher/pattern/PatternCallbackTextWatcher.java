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

public class PatternCallbackTextWatcher implements TextWatcher {

    private final EditText editText;

    private String prev = null;
    private List<PatternCallback> patternCallbacks = new ArrayList<PatternCallback>();

    public PatternCallbackTextWatcher(EditText editText){
        this.editText = editText;
    }

    public PatternCallbackTextWatcher addPatternCallback(PatternCallback patternCallback){
        patternCallbacks.add(patternCallback);
        return this;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (prev==null || !prev.equals(s.toString()) ) {
            prev = s.toString();
            for (PatternCallback patternCallback : patternCallbacks) {
                Matcher matcher = patternCallback.pattern.matcher(s);
                while(matcher.find()){
                    patternCallback.callback.onMatch(matcher.start(), matcher.end());
                }
            }
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
        return spannable;
    }

    public interface Callback {
        void onMatch(int matchStart, int matchEnd);
    }

    /**
     * The PatternCallback class associates a Pattern with a Callback
     */
    public static class PatternCallback {
        public Pattern pattern;
        public Callback callback;

        public PatternCallback(Pattern pattern, Callback callback){
            this.pattern = pattern;
            this.callback = callback;
        }
    }
}
