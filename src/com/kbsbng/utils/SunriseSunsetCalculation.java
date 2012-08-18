package com.kbsbng.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.location.Location;

public class SunriseSunsetCalculation {
	
	public static class SunriseSunsetCalcResult {
		Date time;
		double hourAngle;
	}
	
	private SunriseSunsetCalcResult officialSunrise;
	private SunriseSunsetCalcResult officialSunset;

	private double longitude;
	private double latitude;
	private final double year;

	private final double month;

	private final double day;
	
	/**
	 * day of the year
	 */
	private double n;
	private SunriseSunsetCalcResult civilSunrise;
	private SunriseSunsetCalcResult civilSunset;


	private SunriseSunsetCalcResult nauticalSunrise;
	private SunriseSunsetCalcResult nauticalSunset;
	
	

	private SunriseSunsetCalcResult astroSunrise;
	private SunriseSunsetCalcResult astroSunset;
	
	public SunriseSunsetCalculation(int day, int month, int year,
			final Location location) {
		this.day = day;
		this.month = month;
		this.year = year;
		longitude = location.getLongitude();
		latitude = location.getLatitude();
	}
	public SunriseSunsetCalculation(int day, int month, int year,
			final double longitude, final double latitude) {
		this.day = day;
		this.month = month;
		this.year = year;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	public Date getOfficialSunrise() {
		return officialSunrise.time;
	}

	public boolean doesSunRise() {
		return officialSunrise.hourAngle <=  1;
    }
	
	public boolean doesSunSet() {
		return officialSunset.hourAngle >=  -1;
    }
	
	public Date getOfficialSunset() {
		return officialSunset.time;
	}
	
	public Date getCivilSunrise() {
		return civilSunrise.time;
	}
	
	public Date getCivilSunset() {
		return civilSunset.time;
	}
	
	public Date getNauticalSunrise() {
		return nauticalSunrise.time;
	}
	
	public Date getNauticalSunset() {
		return nauticalSunset.time;
	}
	
	public Date getAstroSunrise() {
		return astroSunrise.time;
	}
	
	public Date getAstroSunset() {
		return astroSunset.time;
	}
	
	private double mysin(double degrees) {
		return Math.sin((degrees * Math.PI) / 180);
	}

	private double mycos(double degrees) {
		return Math.cos((degrees * Math.PI) / 180);
	}

	private double mytan(double degrees) {
		return Math.tan((degrees * Math.PI) / 180);
	}
	
	private double myasin(double x) {
		return Math.asin(x)*180/Math.PI;
	}

	private double myacos(double x) {
		return Math.acos(x)*180/Math.PI;
	}
	
	private double myatan(double x) {
		return Math.atan(x)*180/Math.PI;
	}
	
	private double getRightAscension(double t) {
		double M = (0.9856 * t) - 3.289;
		double L = M + (1.916 * mysin(M))
				+ (0.020 * mysin(2 * M)) + 282.634;
		
		double RA = myatan(0.91764 * mytan(L));
		double Lquadrant = (Math.floor(L / 90)) * 90;
		double RAquadrant = (Math.floor(RA / 90)) * 90;
		RA = RA + (Lquadrant - RAquadrant);
		
		RA /= 15;
		return RA;
	}
	
	private double getHourAngle(double t, double zenith) {
		double M = (0.9856 * t) - 3.289;
		double L = M + (1.916 * mysin(M))
				+ (0.020 * mysin(2 * M)) + 282.634;
		
		double sinDec = 0.39782 * mysin(L);
		double cosDec = mycos(myasin(sinDec));
		return (mycos(zenith) - (sinDec * mysin(latitude)))
				/ (cosDec * mycos(latitude));
	}
	
	private SunriseSunsetCalcResult[] calculateSunriseSunset(double zenith) {
		Date sunrise;
		Date sunset;

		double N1 = Math.floor(275 * (month + 1) / 9.0);
		double N2 = Math.floor(((month + 1) + 9) / 12.0);
		double N3 = (1 + Math.floor((year - 4 * Math.floor(year / 4.0) + 2) / 3.0));
		n = N1 - (N2 * N3) + day - 30;

		double lngHour = longitude / 15;
		double rise = n + ((6 - lngHour) / 24);
		double set = n + ((18 - lngHour) / 24);

		double cosHrise = getHourAngle(rise, zenith);
		
		double cosHset = getHourAngle(set, zenith);
		
		double Hrise = 360 - myacos(cosHrise);
		double Hset = myacos(cosHset);

		Hrise /= 15;
		Hset /= 15;

		double RArise = getRightAscension(rise);
		double RAset = getRightAscension(set);
		
		double Trise = Hrise + RArise - (0.06571 * rise) - 6.622;
		double Tset = Hset + RAset - (0.06571 * set) - 6.622;

		double utrise = Trise - lngHour;
		double utset = Tset - lngHour;
		
		utrise %= 24;
		utset %= 24;

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
		calendar.set((int)year, (int)month, (int)day, (int)utrise,(int)Math.round((utrise-Math.floor(utrise)) * 60), 0);
		sunrise = calendar.getTime();

		calendar.set((int)year, (int)month, (int)day, (int)utset,(int)Math.round((utset-Math.floor(utset)) * 60), 0);
		sunset = calendar.getTime();
		
		SunriseSunsetCalcResult[] sunriseSunset = new SunriseSunsetCalcResult[2];
		sunriseSunset[0] = new SunriseSunsetCalcResult();
		sunriseSunset[1] = new SunriseSunsetCalcResult();
		sunriseSunset[0].time = sunrise;
		sunriseSunset[0].hourAngle = cosHrise;
		sunriseSunset[1].time = sunset;
		sunriseSunset[1].hourAngle = cosHset;
		return sunriseSunset;
	}

	public void calculateOfficialSunriseSunset() {
		SunriseSunsetCalcResult[] sunriseSunset = calculateSunriseSunset(90 + 5.0/6.0);
		officialSunrise = sunriseSunset[0];
		officialSunset = sunriseSunset[1];
	}

	public void calculateCivilSunriseSunset() {
		SunriseSunsetCalcResult[] sunriseSunset = calculateSunriseSunset(96);
		civilSunrise = sunriseSunset[0];
		civilSunset = sunriseSunset[1];
	}
	
	public void calculateNauticalSunriseSunset() {
		SunriseSunsetCalcResult[] sunriseSunset = calculateSunriseSunset(102);
		nauticalSunrise = sunriseSunset[0];
		nauticalSunset = sunriseSunset[1];
	}
	
	public void calculateAstroSunriseSunset() {
		SunriseSunsetCalcResult[] sunriseSunset = calculateSunriseSunset(108);
		astroSunrise = sunriseSunset[0];
		astroSunset = sunriseSunset[1];
	}
	
	public boolean doesSunDawn() {
		return civilSunrise.hourAngle <= 1;
	}
	
	public boolean doesSunDusk() {
		return civilSunset.hourAngle >= -1;
	}
	
	public boolean doesSunNauticalDawn() {
		return nauticalSunrise.hourAngle <= 1;
	}
	
	public boolean doesSunNauticalDusk() {
		return nauticalSunset.hourAngle >= -1;
	}
	
	public boolean doesSunAstroDawn() {
		return astroSunrise.hourAngle <= 1;
	}
	
	public boolean doesSunAstroDusk() {
		return astroSunset.hourAngle >= -1;
	}
}
