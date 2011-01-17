package de.imaze;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class LiveGalleryPreferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}