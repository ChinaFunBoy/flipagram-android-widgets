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
package flipagram.android.app.kitchensink;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import flipagram.android.widget.Coachmark;
import flipagram.android.widget.PrevNextView;


public class KitchenSinkActivity extends ActionBarActivity {

    private final String COACH_KEY = "KitchenSinkActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        Button.class.cast(findViewById(R.id.percentLayoutButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(KitchenSinkActivity.this,PercentLayoutActivity.class));
            }
        });
        Button.class.cast(findViewById(R.id.ratioLayoutButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(KitchenSinkActivity.this,RatioLayoutActivity.class));
            }
        });

        int backgroundColor = getResources().getColor(android.R.color.holo_orange_dark);
        int textColor = getResources().getColor(android.R.color.white);
        new Coachmark(this,COACH_KEY, backgroundColor, textColor)
                .removeKey()
                .withToolBar((Toolbar)findViewById(R.id.toolbar))
                .withTargetView(
                        new Coachmark.TargetView(findViewById(R.id.percentLayoutButton))
                                .fromDirection(Coachmark.TargetView.Direction.South)
                                .withText("Start a PercentLayout")
                )
                .withTargetView(
                        new Coachmark.TargetView(findViewById(R.id.ratioLayoutButton))
                                .fromDirection(Coachmark.TargetView.Direction.East)
                                .withText("Start a RatioLayout")
                )
                .withTargetView(
                        new Coachmark.TargetView(findViewById(R.id.circleTextView))
                                .fromDirection(Coachmark.TargetView.Direction.North)
                                .withText("North")
                )
                .withTargetView(
                        new Coachmark.TargetView(findViewById(R.id.circleTextView))
                                .fromDirection(Coachmark.TargetView.Direction.South)
                                .withText("This is the southern most tip\n" +
                                        "of the cirlce. It's a nice circle.\n" +
                                        "It does nothing but be a circle.")
                )
                .withTargetView(
                        new Coachmark.TargetView(findViewById(R.id.circleTextView))
                                .fromDirection(Coachmark.TargetView.Direction.East)
                                .withText("East")
                )
                .withTargetView(
                        new Coachmark.TargetView(findViewById(R.id.circleTextView))
                                .fromDirection(Coachmark.TargetView.Direction.West)
                                .withText("West")
                )
                .show()
        ;

    }

}
