package com.kbsbng.androidapps.sunrise_sunset_calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class DisplayHelpActivity extends Activity {

	private TextView helpQuestionText;
	private TextView helpAnswerText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		final int helpIconId = extras.getInt("helpIconId");
		final int question;
		final int answer;
		switch (helpIconId) {
		case R.id.dawnHelpIcon:
			question = R.string.dawnHelpQuestion;
			answer = R.array.dawnHelpAnswer;
			break;
		case R.id.duskHelpIcon:
			question = R.string.duskHelpQuestion;
			answer = R.array.duskHelpAnswer;
			break;
		case R.id.nauticalDawnHelpIcon:
			question = R.string.nauticalDawnHelpQuestion;
			answer = R.array.nauticalDawnHelpAnswer;
			break;
		case R.id.nauticalDuskHelpIcon:
			question = R.string.nauticalDuskHelpQuestion;
			answer = R.array.nauticalDuskHelpAnswer;
			break;
		case R.id.astroDawnHelpIcon:
			question = R.string.astroDawnHelpQuestion;
			answer = R.array.astroDawnHelpAnswer;
			break;
		case R.id.astroDuskHelpIcon:
			question = R.string.astroDuskHelpQuestion;
			answer = R.array.astroDuskHelpAnswer;
			break;
		default:
			finish();
			return;
		}

		setContentView(R.layout.activity_display_help);
		helpQuestionText = (TextView) findViewById(R.id.question);
		helpAnswerText = (TextView) findViewById(R.id.answer);

		helpQuestionText.setText(question);
		String[] answerArray = getResources().getStringArray(answer);
		StringBuffer a = new StringBuffer(200);
		for (int i = 0; i < answerArray.length; i++) {
			a.append(answerArray[i]).append("\n\n");
		}
		helpAnswerText.setText(a);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_display_help, menu);
		return true;
	}

	public void handleBackClick(final View view) {
		finish();
	}
}
