package com.kbsbng.androidapps.sunrise_sunset_calculator;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

class LocationTypeDialog extends DialogFragment {
	public static LocationTypeDialog newInstance() {
		return new LocationTypeDialog();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Dialog d = new Dialog(getActivity());
		d.setTitle("Choose Location:");
		return d;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.location_type_chooser,
				container, false);
		TextView currentLocationText = (TextView) v
				.findViewById(R.id.currentLocationOption);
		currentLocationText.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				((CurrentLocationSunriseSunsetCalcution) getActivity())
						.handleCurrectLocationChoice();
				getDialog().cancel();
			}
		});

		TextView pointOnMapText = (TextView) v
				.findViewById(R.id.pointOnMapOption);
		pointOnMapText.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				((CurrentLocationSunriseSunsetCalcution) getActivity())
						.handlePointOnMapChoice();
				getDialog().cancel();
			}
		});

		return v;
	}
}