package com.kbsbng.androidapps.sunrise_sunset_calculator;

import java.io.IOException;
import java.util.List;
import java.util.TimeZone;

import com.kbsbng.utils.SunriseSunsetCalculation;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.app.Activity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class DisplaySunCalculationResults extends Activity {

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
	private TextView countryText;
	private double longitude;
	private double latitude;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras(); 
        longitude = extras.getDouble("longitude");
        latitude = extras.getDouble("latitude");
        year = extras.getInt("year");
        month = extras.getInt("month");
        day = extras.getInt("day");
        sunriseSunsetCalculation = new SunriseSunsetCalculation(day, month, year, longitude, latitude);
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
        countryText = (TextView) findViewById(R.id.country);
        
        Geocoder geocoder = new Geocoder(this);
        try {
        	List<Address> fromLocations = geocoder.getFromLocation(latitude, longitude, 1);
        	if (fromLocations != null && fromLocations.size() != 0 ) {
        		countryText.setText(fromLocations.get(0).getCountryName());
        	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.e(DisplaySunCalculationResults.class.getName(), "got io error", e);
		}

        java.text.DateFormat dateFormat = DateFormat.getTimeFormat(getApplicationContext());
        dateFormat.setTimeZone(getTimeZone());
        
        sunriseTime.setText(dateFormat.format(sunriseSunsetCalculation.getOfficialSunrise()));
        sunsetTime.setText(dateFormat.format(sunriseSunsetCalculation.getOfficialSunset()));
        dawnText.setText(dateFormat.format(sunriseSunsetCalculation.getCivilSunrise()));
        duskText.setText(dateFormat.format(sunriseSunsetCalculation.getCivilSunset()));
        timeZoneText.setText(dateFormat.getTimeZone().getID());
        
        latitudeText.setText(Double.valueOf(latitude).toString());
        longitudeText.setText(Double.valueOf(longitude).toString());
    }

	private TimeZone getTimeZone() {
		double longitudeHours = (longitude / 15);
		String sym;
		if (longitudeHours >= 0) {
			sym = "+";
		} else {
			sym = "-";
			longitudeHours *= (-1);
		}
		double gmtOffsetHours = Math.round(longitudeHours * 2) / 2.0;
		String gmtOffsetMinStr;
		if (gmtOffsetHours % 1 == 0) {
			gmtOffsetMinStr = "00";
		}
		else {
			gmtOffsetMinStr = "30";
		}
		String gmtOffsetHoursStr;
		gmtOffsetHours =  Math.floor(gmtOffsetHours);
		if (gmtOffsetHours < 10) {
			gmtOffsetHoursStr = "0" + (int)gmtOffsetHours;
		}
		else {
			gmtOffsetHoursStr = String.valueOf((int)gmtOffsetHours);
		}
		Log.e("test", "GMT" + sym + gmtOffsetHoursStr+ ":" + gmtOffsetMinStr);
		return TimeZone.getTimeZone("GMT" + sym + gmtOffsetHoursStr+ ":" + gmtOffsetMinStr);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(
				R.menu.activity_display_sun_calculation_results, menu);
		return true;
	}

}
