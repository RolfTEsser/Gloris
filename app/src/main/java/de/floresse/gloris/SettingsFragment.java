package de.floresse.gloris;

import java.io.File;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


public class SettingsFragment extends PreferenceFragment 
                               implements OnSharedPreferenceChangeListener {
	
	private SwitchPreference prefisSound;
	private SwitchPreference prefisNext;
	private SwitchPreference prefisShadow;
	private SwitchPreference prefisDropButton;
	private SwitchPreference prefisAnimation;
	private ListPreference prefbegLevel;
	private Preference prefSize; 
	private Preference prefhighScore; 
	private int progressChanged = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    // Load the preferences from an XML resource
	    addPreferencesFromResource(R.xml.preferences);
	    PreferenceScreen pfs = getPreferenceScreen();
	    
        final SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();

		prefisSound = (SwitchPreference)findPreference("pref_isSound");
		prefisSound.setSummary(prefisSound.isChecked() ? 
    			"ist aktiviert" : "ist nicht aktiviert");
		prefisNext = (SwitchPreference)findPreference("pref_isNext");
		prefisNext.setSummary(prefisNext.isChecked() ? 
    			"ist aktiviert" : "ist nicht aktiviert");
		prefisShadow = (SwitchPreference)findPreference("pref_isShadow");
		prefisShadow.setSummary(prefisShadow.isChecked() ? 
    			"ist aktiviert" : "ist nicht aktiviert");
		prefisDropButton = (SwitchPreference)findPreference("pref_isDropButton");
		prefisDropButton.setSummary(prefisDropButton.isChecked() ? 
    			"ist aktiviert" : "ist nicht aktiviert");
		prefisAnimation = (SwitchPreference)findPreference("pref_isAnimation");
		if (prefisAnimation!=null) {
			prefisAnimation.setSummary(prefisAnimation.isChecked() ? 
    			"ist aktiviert" : "ist nicht aktiviert");
		}
		prefbegLevel = (ListPreference)findPreference("pref_begLevel");
		prefbegLevel.setSummary(prefbegLevel.getEntry()); 
		prefSize = findPreference("pref_size");
        prefSize.setSummary(String.valueOf(sharedPreferences.getInt("pref_size", 40)) + " Pixel");
		prefSize.setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference pf) {

			        	final AlertDialog.Builder alertDialogBuilder = 
			        			new AlertDialog.Builder(getActivity());

			        	LayoutInflater inflater = getActivity().getLayoutInflater();
			        	View dialogLayout = inflater.inflate(R.layout.alertdialog,
			        			(ViewGroup) getActivity().getCurrentFocus(), false);
			        	//
			        	LinearLayout ll = (LinearLayout)dialogLayout.findViewById(R.id.ald_layout);
			        	ll.setBackgroundResource(R.drawable.bg_blau);
			        	//ll.removeView(dialogLayout.findViewById(R.id.ald_title));
			        	TextView title = (TextView)dialogLayout.findViewById(R.id.ald_title);
			        	title.setText("Größe Tetris-Stein");
			        	final TextView text = (TextView)dialogLayout.findViewById(R.id.ald_text);
			        	SeekBar sb;
			    		sb = (SeekBar) dialogLayout.findViewById(R.id.ald_seek);
			    		sb.setVisibility(View.VISIBLE);
			    		progressChanged=sharedPreferences.getInt("pref_size", 40);
	    				text.setText(String.valueOf(progressChanged));
			    		sb.setProgress((int)((sharedPreferences.getInt("pref_size", 40) - 20) * 2));
			    		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			    			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
			    				progressChanged = (int)(progress/2 + 20);
			    				text.setText(String.valueOf(progressChanged));
			    			}

			    			public void onStartTrackingTouch(SeekBar seekBar) {
			    			}

			    			public void onStopTrackingTouch(SeekBar seekBar) {
			    			}
			    		});
			        	//
			        	Button cancel = (Button)dialogLayout.findViewById(R.id.ald_button_cancel);
			        	Button okay = (Button)dialogLayout.findViewById(R.id.ald_button_okay);
			        	alertDialogBuilder.setView(dialogLayout);
			        	final AlertDialog alertDialog = alertDialogBuilder.create();
			        	cancel.setText("Abbrechen");
			    		cancel.setOnClickListener(new OnClickListener() {
			    			public void onClick(View v) {
			    				alertDialog.cancel();
			    			}
			    		});
			        	//LinearLayout ll = (LinearLayout)dialogLayout.findViewById(R.id.ald_two_buttons);
			        	//ll.removeView(okay);
			        	okay.setText("Okay");
			    		okay.setOnClickListener(new OnClickListener() {
			    			public void onClick(View v) {
			    				// save
			    				 SharedPreferences.Editor editor = 
			    						 getPreferenceManager().getSharedPreferences().edit();
			    				 editor.putInt("pref_size", progressChanged);
			    				 editor.commit();
			    				alertDialog.cancel();
			    			}
			    		});
			        	 
			        	// show it
			        	alertDialog.show();
			        	
			        	return true;

					}
				});
		prefhighScore = findPreference("pref_highScore");
        prefhighScore.setSummary(String.valueOf(sharedPreferences.getInt("pref_highScore", 0)) + " Punkte");
		prefhighScore.setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference pf) {

			        	final AlertDialog.Builder alertDialogBuilder = 
			        			new AlertDialog.Builder(getActivity());

			        	LayoutInflater inflater = getActivity().getLayoutInflater();
			        	View dialogLayout = inflater.inflate(R.layout.alertdialog,
			        			(ViewGroup) getActivity().getCurrentFocus(), false);
			        	//
			        	LinearLayout ll = (LinearLayout)dialogLayout.findViewById(R.id.ald_layout);
			        	ll.setBackgroundResource(R.drawable.bg_blau);
			        	ll.removeView(dialogLayout.findViewById(R.id.ald_title));
			        	ll.removeView(dialogLayout.findViewById(R.id.ald_trenner));
			        	//TextView title = (TextView)dialogLayout.findViewById(R.id.ald_title);
			        	//title.setText("Größe Tetris-Stein");
			        	final TextView text = (TextView)dialogLayout.findViewById(R.id.ald_text);
			        	text.setText("Wirklich zurücksetzen ?");
			        	//
			        	Button cancel = (Button)dialogLayout.findViewById(R.id.ald_button_cancel);
			        	Button okay = (Button)dialogLayout.findViewById(R.id.ald_button_okay);
			        	alertDialogBuilder.setView(dialogLayout);
			        	final AlertDialog alertDialog = alertDialogBuilder.create();
			        	cancel.setText("Abbrechen");
			    		cancel.setOnClickListener(new OnClickListener() {
			    			public void onClick(View v) {
			    				alertDialog.cancel();
			    			}
			    		});
			        	//LinearLayout ll = (LinearLayout)dialogLayout.findViewById(R.id.ald_two_buttons);
			        	//ll.removeView(okay);
			        	okay.setText("Ja");
			    		okay.setOnClickListener(new OnClickListener() {
			    			public void onClick(View v) {
			    				// save
			    				 SharedPreferences.Editor editor = 
			    						 getPreferenceManager().getSharedPreferences().edit();
			    				 editor.putInt("pref_highScore", 0);
			    				 editor.commit();
			    			     File file = new File(getActivity().getFilesDir(), "bestenliste");
			    				 if (file.exists()) {
			    					 file.delete();
			    				 }
			    				alertDialog.cancel();
			    			}
			    		});
			        	 
			        	// show it
			        	alertDialog.show();
			        	
			        	return true;

					}
				});


	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_isSound")) {
        	findPreference(key)
        		.setSummary(sharedPreferences.getBoolean(key, false) ? 
        					"ist aktiviert" : "ist nicht aktiviert");
        }	
        if (key.equals("pref_isNext")) {
        	findPreference(key)
        		.setSummary(sharedPreferences.getBoolean(key, false) ? 
        					"ist aktiviert" : "ist nicht aktiviert");
        }	
        if (key.equals("pref_isShadow")) {
        	findPreference(key)
        		.setSummary(sharedPreferences.getBoolean(key, false) ? 
        					"ist aktiviert" : "ist nicht aktiviert");
        }	
        if (key.equals("pref_isDropButton")) {
        	findPreference(key)
        		.setSummary(sharedPreferences.getBoolean(key, false) ? 
        					"ist aktiviert" : "ist nicht aktiviert");
        }	
        if (key.equals("pref_isAnimation")) {
        	findPreference(key)
        		.setSummary(sharedPreferences.getBoolean(key, false) ? 
        					"ist aktiviert" : "ist nicht aktiviert");
        }	
        if (key.equals("pref_begLevel")) {
        	findPreference(key)
        		.setSummary(prefbegLevel.getEntry()); 
        }	
       if (key.equals("pref_size")) {
            Preference satz = findPreference(key);
            // Set summary to be the user-description for the selected value
            satz.setSummary(sharedPreferences.getInt(key, 40) + " Pixel");
        }
       if (key.equals("pref_highScore")) {
           Preference satz = findPreference(key);
           // Set summary to be the user-description for the selected value
           satz.setSummary(sharedPreferences.getInt(key, 0) + " Punkte");
       }
	}
	
	
	@Override
	public void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}
	
	public class myChangeListener implements OnPreferenceChangeListener {
    	public boolean onPreferenceChange(Preference pref, Object newVal) {
    		String key = pref.getKey();
    		CheckBoxPreference p;
    		
    		return true;
    	}
	}  // end internal class PrefChangeListener
	

}
