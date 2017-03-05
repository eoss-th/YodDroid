package com.th.eoss.sevenyods;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

/**
 * Created by wisarutsrisawet on 3/5/17.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setContentView(R.layout.splash);

        new SETFINAsyncTask() {

            @Override
            protected void onPostExecute(List<String> list) {

                SETFINStackedBarFragment.tableFragments();

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

                SETFINStackedBarFragment.load(list);

            }

        }.execute();
    }
}
