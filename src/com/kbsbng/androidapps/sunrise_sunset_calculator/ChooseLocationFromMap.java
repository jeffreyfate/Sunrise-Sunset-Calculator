package com.kbsbng.androidapps.sunrise_sunset_calculator;

import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ChooseLocationFromMap extends MapActivity {

	private MapView mapView;
	private Button doneButton;

	private class SitesOverlay extends ItemizedOverlay<OverlayItem> {

		private Drawable marker;
		private List<OverlayItem> items = new ArrayList<OverlayItem>();
		OverlayItem mapOverlayItem = null;
		GeoPoint locationChosen;

		public SitesOverlay(Drawable defaultMarker) {
			super(defaultMarker);
			this.marker = defaultMarker;
			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return items.get(i);
		}

		@Override
		public int size() {
			return items.size();
		}

		@Override
		public boolean onTap(GeoPoint p, MapView mapView) {
			super.onTap(p, mapView);
			locationChosen = p;

			if (mapOverlayItem != null) {
				items.remove(mapOverlayItem);
			}
			doneButton.setVisibility(1);
			mapOverlayItem = new OverlayItem(p, "", "");
			items.add(mapOverlayItem);

			populate();
			return true;
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			boundCenterBottom(marker);
		}

	}

	SitesOverlay sitesOverlay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_location_from_map);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		Drawable marker = getResources().getDrawable(R.drawable.mapicon);
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());
		sitesOverlay = new SitesOverlay(marker);
		mapView.getOverlays().add(sitesOverlay);

		doneButton = (Button) findViewById(R.id.doneButton);
		doneButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent resultIntent = new Intent();
				resultIntent.putExtra("longitude",
						sitesOverlay.locationChosen.getLongitudeE6());
				resultIntent.putExtra("latitude",
						sitesOverlay.locationChosen.getLatitudeE6());
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
		});

		// getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.common, menu);
		return true;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		default:
			return Sharer.share(item);
		}
	}

}
