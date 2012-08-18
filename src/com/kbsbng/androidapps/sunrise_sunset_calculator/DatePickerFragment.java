package com.kbsbng.androidapps.sunrise_sunset_calculator;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

class DatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		CurrentLocationSunriseSunsetCalcution calculation = (CurrentLocationSunriseSunsetCalcution)getActivity();
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(calculation, this, calculation.year, calculation.month, calculation.day);
	}

	public void onDateSet(DatePicker view, int y, int m, int d) {
		// Do something with the date chosen by the user
		((CurrentLocationSunriseSunsetCalcution)getActivity()).handleDateSelection(y, m, d);
	}
}