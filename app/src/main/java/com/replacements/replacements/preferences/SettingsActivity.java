package com.replacements.replacements.preferences;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.replacements.replacements.R;

public class SettingsActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

        //Ustawienie widoku aktywności, toolbara jako domyślnego i ustawienie jego koloru i przycisku wstecz na nim
        setContentView(R.layout.replacements_pref);
        Toolbar toolbar = (Toolbar) findViewById(R.id.pref_toolbar);

        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blue));
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	    // Display the fragment as the main content of layout "replacements_pref", because of necessary of support Material Design
	    getFragmentManager().beginTransaction().replace(R.id.content_frame_pref, new SettingsFragment()).commit();
	}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}