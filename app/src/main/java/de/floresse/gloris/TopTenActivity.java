package de.floresse.gloris;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TopTenActivity extends Activity {
	private String TAG = "TopTenActivity";
	private LinearLayout[] ttll = new LinearLayout[10];
	private TextView[] ttscore = new TextView[10];
	private TextView[] ttlevel = new TextView[10];
	private TextView[] tttime = new TextView[10];
	private int[] ttZscore = new int[10]; 
	private int[] ttZlevel = new int[10];
	private long[] ttZtime = new long[10];
	private int pos;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy    HH:mm", Locale.getDefault());
    private DecimalFormat df = new DecimalFormat("##,###");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        pos  = getIntent().getIntExtra("pos", 9999);

		setContentView(R.layout.activity_topten);
		ttll[0] = (LinearLayout)findViewById(R.id.ttll1);
		ttscore[0] = (TextView)findViewById(R.id.ttscore1);
		ttlevel[0] = (TextView)findViewById(R.id.ttlevel1);
		tttime[0] = (TextView)findViewById(R.id.tttime1);
		ttll[1] = (LinearLayout)findViewById(R.id.ttll2);
		ttscore[1] = (TextView)findViewById(R.id.ttscore2);
		ttlevel[1] = (TextView)findViewById(R.id.ttlevel2);
		tttime[1] = (TextView)findViewById(R.id.tttime2);
		ttll[2] = (LinearLayout)findViewById(R.id.ttll3);
		ttscore[2] = (TextView)findViewById(R.id.ttscore3);
		ttlevel[2] = (TextView)findViewById(R.id.ttlevel3);
		tttime[2] = (TextView)findViewById(R.id.tttime3);
		ttll[3] = (LinearLayout)findViewById(R.id.ttll4);
		ttscore[3] = (TextView)findViewById(R.id.ttscore4);
		ttlevel[3] = (TextView)findViewById(R.id.ttlevel4);
		tttime[3] = (TextView)findViewById(R.id.tttime4);
		ttll[4] = (LinearLayout)findViewById(R.id.ttll5);
		ttscore[4] = (TextView)findViewById(R.id.ttscore5);
		ttlevel[4] = (TextView)findViewById(R.id.ttlevel5);
		tttime[4] = (TextView)findViewById(R.id.tttime5);
		ttll[5] = (LinearLayout)findViewById(R.id.ttll6);
		ttscore[5] = (TextView)findViewById(R.id.ttscore6);
		ttlevel[5] = (TextView)findViewById(R.id.ttlevel6);
		tttime[5] = (TextView)findViewById(R.id.tttime6);
		ttll[6] = (LinearLayout)findViewById(R.id.ttll7);
		ttscore[6] = (TextView)findViewById(R.id.ttscore7);
		ttlevel[6] = (TextView)findViewById(R.id.ttlevel7);
		tttime[6] = (TextView)findViewById(R.id.tttime7);
		ttll[7] = (LinearLayout)findViewById(R.id.ttll8);
		ttscore[7] = (TextView)findViewById(R.id.ttscore8);
		ttlevel[7] = (TextView)findViewById(R.id.ttlevel8);
		tttime[7] = (TextView)findViewById(R.id.tttime8);
		ttll[8] = (LinearLayout)findViewById(R.id.ttll9);
		ttscore[8] = (TextView)findViewById(R.id.ttscore9);
		ttlevel[8] = (TextView)findViewById(R.id.ttlevel9);
		tttime[8] = (TextView)findViewById(R.id.tttime9);
		ttll[9] = (LinearLayout)findViewById(R.id.ttll10);
		ttscore[9] = (TextView)findViewById(R.id.ttscore10);
		ttlevel[9] = (TextView)findViewById(R.id.ttlevel10);
		tttime[9] = (TextView)findViewById(R.id.tttime10);
		
		loadFile();
		if (ttZscore[0]>0) {
			ttscore[0].setText(df.format(ttZscore[0]));
			ttlevel[0].setText(String.valueOf(ttZlevel[0]));
			tttime[0].setText(sdf.format(ttZtime[0]));
		}
		if (ttZscore[1]>0) {
			ttscore[1].setText(df.format(ttZscore[1]));
			ttlevel[1].setText(String.valueOf(ttZlevel[1]));
			tttime[1].setText(sdf.format(ttZtime[1]));
		}
		if (ttZscore[2]>0) {
			ttscore[2].setText(df.format(ttZscore[2]));
			ttlevel[2].setText(String.valueOf(ttZlevel[2]));
			tttime[2].setText(sdf.format(ttZtime[2]));
		}
		if (ttZscore[3]>0) {
			ttscore[3].setText(df.format(ttZscore[3]));
			ttlevel[3].setText(String.valueOf(ttZlevel[3]));
			tttime[3].setText(sdf.format(ttZtime[3]));
		}
		if (ttZscore[4]>0) {
			ttscore[4].setText(df.format(ttZscore[4]));
			ttlevel[4].setText(String.valueOf(ttZlevel[4]));
			tttime[4].setText(sdf.format(ttZtime[4]));
		}
		if (ttZscore[5]>0) {
			ttscore[5].setText(df.format(ttZscore[5]));
			ttlevel[5].setText(String.valueOf(ttZlevel[5]));
			tttime[5].setText(sdf.format(ttZtime[5]));
		}
		if (ttZscore[6]>0) {
			ttscore[6].setText(df.format(ttZscore[6]));
			ttlevel[6].setText(String.valueOf(ttZlevel[6]));
			tttime[6].setText(sdf.format(ttZtime[6]));
		}
		if (ttZscore[7]>0) {
			ttscore[7].setText(df.format(ttZscore[7]));
			ttlevel[7].setText(String.valueOf(ttZlevel[7]));
			tttime[7].setText(sdf.format(ttZtime[7]));
		}
		if (ttZscore[8]>0) {
			ttscore[8].setText(df.format(ttZscore[8]));
			ttlevel[8].setText(String.valueOf(ttZlevel[8]));
			tttime[8].setText(sdf.format(ttZtime[8]));
		}
		if (ttZscore[9]>0) {
			ttscore[9].setText(df.format(ttZscore[9]));
			ttlevel[9].setText(String.valueOf(ttZlevel[9]));
			tttime[9].setText(sdf.format(ttZtime[9]));
		}
		if (pos!=9999) {
			ttll[pos].setBackgroundResource(R.drawable.bg_blau);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            // app icon in action bar clicked; go home
        	finish();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
	
	private void loadFile() {
    	File file = new File(getFilesDir(), "bestenliste");
    	int i;
    	if (file.exists()) {
    		try {
    			DataInputStream fin = new DataInputStream(new FileInputStream(file));
    			for (i=0;i<10;i++) {
    				ttZscore[i]=fin.readInt();
    				ttZlevel[i]=fin.readInt();
    				ttZtime[i]=fin.readLong();
    			}
    			fin.close();
    		} catch (IOException e){
    			Log.d(TAG, " IOException : " + e);
    		}
    	} else {
			for (i=0;i<10;i++) {
				ttZscore[i]=0;
				ttZlevel[i]=0;
				ttZtime[i]=0;
			}
    		
    	}
	}
	
}
