package flipagram.android.text.watcher.pattern;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link android.text.TextWatcher} that calls the matching
 * {@link flipagram.android.text.watcher.pattern.PatternCallbackTextWatcher.Callback}
 * when the text at the selectionStart of the {@link android.widget.EditText} matches one
 * of the {@link flipagram.android.text.watcher.pattern.PatternCallbackTextWatcher.PatternCallback}s
 */
public class PatternCallbackTextWatcher implements TextWatcher {

    private final EditText editText;

    private String prev = null;
    private List<PatternCallback> patternCallbacks = new ArrayList<PatternCallback>();

    /**
     * Create a new {@link flipagram.android.text.watcher.pattern.PatternCallbackTextWatcher} and
     * associate it with the given {@link android.widget.EditText}. You must
     * {@link android.widget.EditText#addTextChangedListener} the returned object to the
     * {@link android.widget.EditText}.
     * @param editText
     */
    public PatternCallbackTextWatcher(EditText editText){
        this.editText = editText;
    }

    /**
     * Add the {@link flipagram.android.text.watcher.pattern.PatternCallbackTextWatcher} to the
     * internal list.
     * @param patternCallback
     * @return this for chaining
     */
    public PatternCallbackTextWatcher addPatternCallback(PatternCallback patternCallback){
        patternCallbacks.add(patternCallback);
        return this;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (prev==null || !prev.equals(s.toString()) ) {
            prev = s.toString();
            int cursorPosition = editText.getSelectionStart();
            for (PatternCallback patternCallback : patternCallbacks) {
                Matcher matcher = patternCallback.pattern.matcher(s);
                while(matcher.find()){
                    if (matcher.start()<=cursorPosition && matcher.end()>=cursorPosition)
                        patternCallback.callback.onMatch(matcher.start(), matcher.end());
                }
            }
        }
    }
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
    @Override public void afterTextChanged(Editable s) { }

    /**
     * When the text at the cursor matches, <code>onMatch</code> is called
     */
    public interface Callback {
        void onMatch(int matchStart, int matchEnd);
    }

    /**
     * Associate a {@link java.util.regex.Pattern} with a
     * {@link flipagram.android.text.watcher.pattern.PatternCallbackTextWatcher.Callback}.
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
