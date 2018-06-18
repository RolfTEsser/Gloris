package de.floresse.gloris;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewOutlineProvider;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.graphics.Outline;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class StartActivity extends Activity {
	
	private boolean gameOver;
	private boolean gameStored;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_start);
		
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		gameOver = sharedPref.getBoolean("pref_gameOver", false);
		gameStored = sharedPref.getBoolean("pref_gameStored", false);
    	sharedPref.edit().putBoolean("pref_isAnimation", true).commit();
		Button btfs = (Button)findViewById(R.id.fortsetzen);
		if (!gameStored) {
			btfs.setEnabled(false);
		} else {
			btfs.setEnabled(true);
		}
		if (gameOver) {
			btfs.setTextColor(Color.GRAY);
		} else {
			btfs.setTextColor(Color.WHITE);
		}
		/*
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			ImageView iv = (ImageView)findViewById(R.id.schnellStart);
			iv.setOutlineProvider(new ViewOutlineProvider() {
				@Override
				public void getOutline(View view, Outline outline) {
					int diameter = getResources().getDimensionPixelSize(48);
					outline.setOval(0, 0, diameter, diameter);
				}
			});
			iv.setClipToOutline(true);
		}
		*/
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        findViewById(R.id.schnellTest).startAnimation(shake);
        findViewById(R.id.schnellStart).startAnimation(shake);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			settings();
			return true;
		}
		if (id == R.id.action_about) {
			about();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void about(View v) {
		about();
	}
	
	public void about() {
    	final AlertDialog.Builder alertDialogBuilder = 
    			new AlertDialog.Builder(this);

    	LayoutInflater inflater = getLayoutInflater();
    	View dialogLayout = inflater.inflate(R.layout.alertdialog,
    			(ViewGroup) getCurrentFocus(), false);
    	//
    	LinearLayout ll = (LinearLayout)dialogLayout.findViewById(R.id.ald_layout);
    	ll.setBackgroundResource(R.drawable.bg_blau);
    	//ll.removeView(dialogLayout.findViewById(R.id.ald_title));
    	TextView title = (TextView)dialogLayout.findViewById(R.id.ald_title);
    	title.setText("GloRis");
    	TextView text = (TextView)dialogLayout.findViewById(R.id.ald_text);
    	//
    	text.setText("Copyright R.T.E. © 2014");
    	Button cancel = (Button)dialogLayout.findViewById(R.id.ald_button_cancel);
    	Button okay = (Button)dialogLayout.findViewById(R.id.ald_button_okay);
    	alertDialogBuilder.setView(dialogLayout);
    	final AlertDialog alertDialog = alertDialogBuilder.create();
    	cancel.setText("OK");
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				alertDialog.cancel();
			}
		});
    	LinearLayout ll1 = (LinearLayout)dialogLayout.findViewById(R.id.ald_two_buttons);
    	ll1.removeView(okay);
    	okay.setText("Ja");
		okay.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				alertDialog.cancel();
			}
		});
    	 
    	// show it
    	alertDialog.show();

	}
	
	public void doit(View v) {
		Intent intent = new Intent(this, GameActivity.class);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    	sharedPref.edit().putBoolean("pref_isAnimation", false).commit();
		if (gameOver || !gameStored) {
			intent.putExtra("isNeuesSpiel", true);
		} else {	
			intent.putExtra("isNeuesSpiel", false);
		}	
		startActivity(intent);
	}
	
	public void settings(View v) {
		settings();
	}
	
	public void settings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void bestenliste(View v) {
		Intent intent = new Intent(this, TopTenActivity.class);
		startActivity(intent);
	}
	
	public void fortsetzen(View v) {
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra("isNeuesSpiel", false);
		startActivity(intent);
	}
	
	public void neuesSpiel(View v) {
		if (gameOver || !gameStored) {
			Intent intent = new Intent(this, GameActivity.class);
			intent.putExtra("isNeuesSpiel", true);
			startActivity(intent);
		} else {
        	final AlertDialog.Builder alertDialogBuilder = 
        			new AlertDialog.Builder(this);

        	LayoutInflater inflater = getLayoutInflater();
        	View dialogLayout = inflater.inflate(R.layout.alertdialog,
        			(ViewGroup) getCurrentFocus(), false);
        	//
        	LinearLayout ll = (LinearLayout)dialogLayout.findViewById(R.id.ald_layout);
        	ll.setBackgroundResource(R.drawable.bg_blau);
        	//ll.removeView(dialogLayout.findViewById(R.id.ald_title));
        	TextView title = (TextView)dialogLayout.findViewById(R.id.ald_title);
        	title.setText("altes Spiel noch nicht beendet !");
        	TextView text = (TextView)dialogLayout.findViewById(R.id.ald_text);
        	//
        	text.setText("neues Spiel ?");
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
			final Intent intent = new Intent(this, GameActivity.class);
        	okay.setText("Ja");
    		okay.setOnClickListener(new OnClickListener() {
    			public void onClick(View v) {
    				intent.putExtra("isNeuesSpiel", true);
    				startActivity(intent);
    				alertDialog.cancel();
    			}
    		});
        	 
        	// show it
        	alertDialog.show();

		}
	}
}
