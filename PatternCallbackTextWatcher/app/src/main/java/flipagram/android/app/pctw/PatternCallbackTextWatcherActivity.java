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
        };

        tw.addPatternCallback(new PatternCallbackTextWatcher.PatternCallback(
            Pattern.compile("@[A-z][A-z0-9\\._\\-]*"), logit));

        tw.addPatternCallback(new PatternCallbackTextWatcher.PatternCallback(
            Pattern.compile("#[A-z0-9]+"), logit ));

        editText.addTextChangedListener(tw);

    }
}
