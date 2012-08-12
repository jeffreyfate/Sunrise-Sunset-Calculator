package com.kbsbng.androidapps.sunrise_sunset_calculator;


import com.kbsbng.utils.SunriseSunsetCalculation;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.widget.TextView;

public class DisplaySunCalculationResults extends Activity {

	Location location;
	private int year;
	private int month;
	private int day;
	
	private SunriseSunsetCalculation sunriseSunsetCalculation;
	private TextView sunriseTime;
	private TextView sunsetTime;
	private TextView latitudeText;
	private TextView longitudeText;
	private TextView dawnText;
	private TextView duskText;
	private TextView timeZoneText;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras(); 
        location = extras.getParcelable("location");
        year = extras.getInt("year");
        month = extras.getInt("month");
        day = extras.getInt("day");
        sunriseSunsetCalculation = new SunriseSunsetCalculation(day, month, year, location);
        sunriseSunsetCalculation.calculateOfficialSunriseSunset();
        sunriseSunsetCalculation.calculateCivilSunriseSunset();
        
        setContentView(R.layout.activity_display_sun_calculation_results);
        sunriseTime = (TextView) findViewById(R.id.sunriseTime);
        sunsetTime = (TextView) findViewById(R.id.sunsetTime);
        dawnText = (TextView) findViewById(R.id.dawnTime);
        duskText = (TextView) findViewById(R.id.duskTime);
        latitudeText = (TextView) findViewById(R.id.latitude);
        longitudeText = (TextView) findViewById(R.id.longitude);
        timeZoneText = (TextView) findViewById(R.id.timeZoneText);
        
        java.text.DateFormat dateFormat = DateFormat.getTimeFormat(getApplicationContext());
        
        sunriseTime.setText(dateFormat.format(sunriseSunsetCalculation.getOfficialSunrise()));
        sunsetTime.setText(dateFormat.format(sunriseSunsetCalculation.getOfficialSunset()));
        dawnText.setText(dateFormat.format(sunriseSunsetCalculation.getCivilSunrise()));
        duskText.setText(dateFormat.format(sunriseSunsetCalculation.getCivilSunset()));
        timeZoneText.setText(dateFormat.getTimeZone().getID());
        
        latitudeText.setText(Double.valueOf(location.getLatitude()).toString());
        longitudeText.setText(Double.valueOf(location.getLongitude()).toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_display_sun_calculation_results, menu);
        return true;
    }

    
}
