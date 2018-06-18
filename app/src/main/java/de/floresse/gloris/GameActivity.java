package de.floresse.gloris;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class GameActivity extends Activity {
	private MenuItem mnitempause = null;
	private MenuItem mnitemplay = null;
	private boolean isNeuesSpiel = false; 
	private GameView gameView; 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        isNeuesSpiel  = getIntent().getBooleanExtra("isNeuesSpiel", false);

        setContentView(gameView = new GameView(this, isNeuesSpiel));

    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	gameView.onPause();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play, menu);
		mnitempause = menu.findItem(R.id.action_pause);
		mnitemplay = menu.findItem(R.id.action_play);
		updateMenu(false);
		return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            // app icon in action bar clicked; go home
        	finish();
            return true;
        case R.id.action_pause:  
        	gameView.swapPause();
    		updateMenu(true);
        	return true;
        case R.id.action_play:  
        	gameView.swapPause();
    		updateMenu(false);
        	return true;
    }
        return super.onOptionsItemSelected(item);
    }
    
    private void updateMenu(boolean pause)  {
    	if (pause) {
    		mnitempause.setEnabled(false);
    		mnitemplay.setEnabled(true);
    	} else {
    		mnitempause.setEnabled(true);
    		mnitemplay.setEnabled(false);
  	}
    }	

}
