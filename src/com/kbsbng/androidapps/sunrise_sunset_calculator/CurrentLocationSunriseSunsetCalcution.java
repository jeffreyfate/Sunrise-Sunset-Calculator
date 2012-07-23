package com.kbsbng.androidapps.sunrise_sunset_calculator;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class CurrentLocationSunriseSunsetCalcution extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location_sunrise_sunset_calculation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_current_location_sunrise_sunset_calculation, menu);
        return true;
    }

    
}
