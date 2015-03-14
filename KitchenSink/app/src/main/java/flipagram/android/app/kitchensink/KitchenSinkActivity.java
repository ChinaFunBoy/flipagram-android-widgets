package flipagram.android.app.kitchensink;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import flipagram.android.widget.PrevNextView;


public class KitchenSinkActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final PrevNextView pn = PrevNextView.class.cast(findViewById(R.id.prevnext));
        pn.setN(1);
        pn.setM(10);
        pn.setPrevOnClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pn.setN(pn.getN()-1);
            }
        });
        pn.setNextOnClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pn.setN(pn.getN()+1);
            }
        });
    }

}
