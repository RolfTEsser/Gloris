package de.floresse.gloris;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.util.Log;

public class GameDrawThread extends Thread {
	static final String TAG = "GameDrawThread";
    static final long FPS = 20;
    private GameView gameView;
    private boolean isRunning = false;
    private boolean isPause = false;
 
    public GameDrawThread(GameView gameView) {
        this.gameView = gameView;
    }
 
    public void setRunning(boolean run) {
        isRunning = run;
    }
    
    public void setPause(boolean pause) {
        isPause = pause;
    }
 
	@SuppressLint("WrongCall")
	public void run() {
        long TPS = 1000 / FPS;
        long startTime, sleepTime;
        while (isRunning) {
        	Canvas canvas = null;
        	startTime = System.currentTimeMillis();
        	if (!isPause) {
        		try {
        			canvas = gameView.getHolder().lockCanvas();
        			synchronized (gameView.getHolder()) {
        				gameView.onDraw(canvas);
        			}
        		} finally {
        			if (canvas != null) {
        				gameView.getHolder().unlockCanvasAndPost(canvas);
        			}
        		}
        	}
        	sleepTime = TPS - (System.currentTimeMillis() - startTime);
        	//Log.d(TAG, "sleepTime : " + sleepTime);
        	try {
        		if (sleepTime > 0)
        			sleep(sleepTime);
        		else
        			sleep(5);
        	} catch (Exception e) {

        	}
        }
        Log.d(TAG, "Thread ended");
    }
}


