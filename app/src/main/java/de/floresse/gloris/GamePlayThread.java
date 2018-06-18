package de.floresse.gloris;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.util.Log;

public class GamePlayThread extends Thread {
	static final String TAG = "Gloris GamePlayThread";
    private float FPS = 1;
    private GameView gameView;
    private boolean isRunning = false;
    private boolean isPause = false;
    private long TPS;
    private long startTime, sleepTime;
    
    public GamePlayThread(GameView gameView) {
        this.gameView = gameView;
        setLevel(1);
    }
 
    public void setRunning(boolean run) {
        isRunning = run;
    }
    
    public void setPause(boolean pause) {
        isPause = pause;
    }
    
    public void setLevel(int level) {
    	switch (level) {
    	case 1:
    		FPS = 1f;
    		break;
    	case 2:
    		FPS = 1.25f;
    		break;
    	case 3:
    		FPS = 1.5f;
    		break;
    	case 4:
    		FPS = 2f;
    		break;
    	case 5:
    		FPS = 2.5f;
    		break;
    	case 6:
    		FPS = 3f;
    		break;
    	case 7:
    		FPS = 4f;
    		break;
    	case 8:
    		FPS = 5f;
    		break;
    	case 9:
    		FPS = 6f;
    		break;
    	case 10:
    		FPS = 7f;
    		break;
    	case 11:
    		FPS = 8f;
    		break;
    	case 12:
    		FPS = 9f;
    		break;
    	case 13:
    		FPS = 10f;
    		break;
    	case 14:
    		FPS = 11f;
    		break;
    	case 15:
    		FPS = 12f;
    		break;
    	default:
    		FPS = 13f;
    		break;
    	}
        TPS = (long)(1000 / FPS);
    }
 
	@SuppressLint("WrongCall")
	public void run() {
        while (isRunning) {
            startTime = System.currentTimeMillis();
            if (!isPause) gameView.shiftTiefer();
            sleepTime = TPS - (System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (Exception e) {
 
            }
        }
        Log.d(TAG, "Thread ended");
    }


}
