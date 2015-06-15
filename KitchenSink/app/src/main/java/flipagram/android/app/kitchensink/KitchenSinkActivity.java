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
        Button.class.cast(findViewById(R.id.restoreCoachmarks)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Coachmark.reset(KitchenSinkActivity.this,COACH_KEY);
            }
        });

        int backgroundColor = getResources().getColor(android.R.color.holo_orange_dark);
        int textColor = getResources().getColor(android.R.color.white);
        int bullseyeBorderColor = getResources().getColor(android.R.color.white);
//        Coachmark.reset(this);
        new Coachmark(this,COACH_KEY, backgroundColor, textColor)
                .withTextSize(16.0f)
                .withTarget(
                        new Coachmark.Target(findViewById(R.id.nextButton))
                                .pointing(Coachmark.Target.Direction.South)
                                .withBounce()
                                .withText(R.string.go_to_the_next_one)
                                .withClickthrough(true)
                )
                .withTarget(
                        new Coachmark.Target(toolbar)
                                .pointing(Coachmark.Target.Direction.North)
                                .withBounce()
                                .skewTextToward(Coachmark.Target.Direction.East, 0.75f)
                                .skewTriangleToward(Coachmark.Target.Direction.East, 1f)
                                .withText("This is the next button.\n" +
                                        "You can press it to go to\n" +
                                        "the next screen.")
                                .withBullseye(0.5f,bullseyeBorderColor, 4)
                )
                .withTarget(
                        new Coachmark.Target(findViewById(R.id.circleTextView))
                                .pointing(Coachmark.Target.Direction.South)
                                .withBounce()
                                .withText("This is the northern most tip\n" +
                                        "of the cirlce. It's a nice circle.\n" +
                                        "It does nothing but be a circle.")
                )
                .withTarget(
                        new Coachmark.Target(findViewById(R.id.restoreCoachmarks))
                                .pointing(Coachmark.Target.Direction.South)
                                .withBounce()
                                .withText("Restore these coachmarks!")
                                .withBullseye(1.0f,bullseyeBorderColor, 4)
                )
                .withTarget(
                        new Coachmark.Target(findViewById(R.id.leftButton))
                                .pointing(Coachmark.Target.Direction.West)
                                .withBounce()
                                .withText("oompa")
                                .withBullseye(1.0f,bullseyeBorderColor,4)
                )
                .withTarget(
                        new Coachmark.Target(findViewById(R.id.rightButton))
                                .pointing(Coachmark.Target.Direction.East)
                                .withBounce()
                                .withText("loompa")
                                .withBullseye(1.0f,bullseyeBorderColor,4)
                )
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.kitchen_sink, menu);
        return true;
    }
}
