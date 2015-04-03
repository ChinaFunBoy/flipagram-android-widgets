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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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

        Button.class.cast(findViewById(R.id.percentLayoutButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(KitchenSinkActivity.this,PercentLayoutActivity.class));
            }
        });
    }

}
