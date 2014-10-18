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
