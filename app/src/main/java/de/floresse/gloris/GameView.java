package de.floresse.gloris;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class GameView extends SurfaceView {
	static final String TAG = "GameView";
	
    private SurfaceHolder surfaceHolder;
    private GameDrawThread gameDrawThread;
    private GamePlayThread gamePlayThread;
    private Context context;
    
    private int lines = 22;
    private int cols = 10;
    private int size;
    private boolean isShadow;
    private boolean isNext;
    private boolean isDropButton;
    private boolean isDropPressed=false;
    private int highScore;
    private int margLeft, margRight, margTop, margBottom;
    private SoundPool soundPool;
    private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private String outZeit = new String();
    private int[] sounds = new int[7];
    private float actVolume;
	private Point screenSize = new Point();
    
    private boolean gameOver;
    private boolean isAnimation;
    private int score = 0;
    private int level = 0;
    private int delLines = 0;
    private int zL = 0;
    private int zDrop = 0;
    private Point[] pt = new Point[4];
    private Point[] ptnext = new Point[4];
    private Point[] ptchk = new Point[4];
    private Point[] ptrot = new Point[4];
    private Point[] ptsh = new Point[4];
    private Point[] ptdrop = new Point[4];
    private int[][] ih = new int[lines][cols];
    private int aktih;
    private int nextih;
    private int dropih;
    private int aktLine;
    private float faktor1 = 1;
    private float faktor2 = 1;
    private int ttpos = 9999;
    
    private Bitmap[] bm = new Bitmap[12];
    private Bitmap[] bmnext = new Bitmap[7];
    private Bitmap[] bmVor = new Bitmap[4];
    private Random random = new Random();
    
    private Point syncpoint = new Point(0,0);
    
    private Paint paint1 = new Paint();
    private Paint paintTxt = new Paint();
    private Point down = new Point();
    private Point move = new Point();
    private long downTime;
    private long upTime;
    private long Zeit = 0;
    private long startZeit = 0;
    private boolean unzul;
    private int frameCount = 0;
    private int diff=0;
    private boolean actionDown = false;
    private boolean actionMove = false;
    private int begLevel;
    private int diffTop; 
    private int levelUpFrames = 0; 
    private boolean isPause = false;
    private boolean isSound;
    private boolean isLevelUp = false;
    
    public GameView(Context ctxt, boolean neuesSpiel) {
        super(ctxt);		
        this.context=ctxt;
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		size = sharedPref.getInt("pref_size", 40);
		begLevel=Integer.valueOf(sharedPref.getString("pref_begLevel", "1"));
		isShadow=sharedPref.getBoolean("pref_isShadow", true);
		isSound=sharedPref.getBoolean("pref_isSound", true);
		isNext=sharedPref.getBoolean("pref_isNext", true);
		isDropButton=sharedPref.getBoolean("pref_isDropButton", false);
		highScore=sharedPref.getInt("pref_highScore", 0);
		gameOver=sharedPref.getBoolean("pref_gameOver", false);
		isAnimation=sharedPref.getBoolean("pref_isAnimation", true);

        init();
        if (neuesSpiel) {
        	gameOver=false;
        	level=begLevel;
        	neuer();
        } else {
        	loadGame();
        }
        outZeit = sdf.format(Zeit);
        gameDrawThread = new GameDrawThread(this);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
 
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                gameDrawThread.setRunning(false);
                while(retry){
                    try {
                        gameDrawThread.join();
                        retry=false;
                    }catch(InterruptedException e){
                    }
                }
            }
 
            public void surfaceCreated(SurfaceHolder holder) {
            }
 
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            	screenSize.x=width;
            	screenSize.y=height;
        		margLeft = (screenSize.x - (size * cols)) / 2;
        		margRight = screenSize.x - margLeft;
        		margTop = (screenSize.y - (size * (lines-2))) / 2;
        		margBottom = screenSize.y - margTop;
        		if (margLeft < 0 || margTop < 0) {
        			// size zu groß
        			Toast.makeText(context, "Größe Tetris-Stein unzulässig", Toast.LENGTH_LONG).show();
        		}
        		bm[8] = Bitmap.createScaledBitmap(bm[8], margLeft-20, margLeft-20, false);
        		bm[9] = Bitmap.createScaledBitmap(bm[9], margLeft-20, margLeft-20, false);
        		bm[10] = Bitmap.createScaledBitmap(bm[10], screenSize.x, screenSize.y, false);

            	gameDrawThread.setRunning(true);
                gameDrawThread.start();
            }
        });
        
        gamePlayThread = new GamePlayThread(this);
        gamePlayThread.setLevel(level);
    }
    
    public void onPause() {
    	gameDrawThread.setRunning(false);
    	gamePlayThread.setRunning(false);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    	sharedPref.edit().putInt("pref_highScore", highScore).commit();
    	sharedPref.edit().putBoolean("pref_gameOver", gameOver).commit();
    	sharedPref.edit().putBoolean("pref_gameStored", true).commit();
    	saveGame();
    	((Activity)context).finish();
    }
    
    private void neuer() {
    	int i;
    	aktih = nextih;
    	nextih = random.nextInt(7) + 1;
        switch (nextih) {
        case 1:
        	neuer1(ptnext);
        	break;
        case 2:
        	neuer2(ptnext);
        	break;
        case 3:
        	neuer3(ptnext);
        	break;
        case 4:
        	neuer4(ptnext);
        	break;
        case 5:
        	neuer5(ptnext);
        	break;
        case 6:
        	neuer6(ptnext);
        	break;
        case 7:
        	neuer7(ptnext);
        	break;
        }
        switch (aktih) {
        case 1:
        	neuer1(pt);
        	break;
        case 2:
        	neuer2(pt);
        	break;
        case 3:
        	neuer3(pt);
        	break;
        case 4:
        	neuer4(pt);
        	break;
        case 5:
        	neuer5(pt);
        	break;
        case 6:
        	neuer6(pt);
        	break;
        case 7:
        	neuer7(pt);
        	break;
        }
        for (i=0;i<4;i++) {
        	ih[pt[i].y][pt[i].x]=aktih;
        }
        aktLine=0;
        score+=5;
        shadow();
        actionDown=false;  // cancel Tetris Move
    }

    private void neuer1(Point[] pt) {
    	pt[0].x=3;  //   XXXX
    	pt[0].y=2;  //
    	pt[1].x=4;
    	pt[1].y=2;
    	pt[2].x=5;
    	pt[2].y=2;
    	pt[3].x=6;
    	pt[3].y=2;
    }

    private void neuer2(Point[] pt) {
    	pt[0].x=3;  //    ZZZ    
    	pt[0].y=2;  //    Z
    	pt[1].x=4;
    	pt[1].y=2;
    	pt[2].x=5;
    	pt[2].y=2;
    	pt[3].x=3;
    	pt[3].y=3;
    }

    private void neuer3(Point[] pt) {
    	pt[0].x=3;  //    ZZZ
    	pt[0].y=2;  //      Z
    	pt[1].x=4;
    	pt[1].y=2;
    	pt[2].x=5;
    	pt[2].y=2;
    	pt[3].x=5;
    	pt[3].y=3;
    }

    private void neuer4(Point[] pt) {
    	pt[0].x=3;  //    XX 
    	pt[0].y=2;  //     XX
    	pt[1].x=4;
    	pt[1].y=2;
    	pt[2].x=4;
    	pt[2].y=3;
    	pt[3].x=5;
    	pt[3].y=3;
    }

    private void neuer5(Point[] pt) {
    	pt[0].x=4;  //     XX
    	pt[0].y=2;  //    XX
    	pt[1].x=5;
    	pt[1].y=2;
    	pt[2].x=3;
    	pt[2].y=3;
    	pt[3].x=4;
    	pt[3].y=3;
    }

    private void neuer6(Point[] pt) {
    	pt[0].x=4;  //     XX
    	pt[0].y=2;  //     XX
    	pt[1].x=5;
    	pt[1].y=2;
    	pt[2].x=4;
    	pt[2].y=3;
    	pt[3].x=5;
    	pt[3].y=3;
    }

    private void neuer7(Point[] pt) {
    	pt[0].x=3;  //    ZZZ
    	pt[0].y=2;  //     Z
    	pt[1].x=4;
    	pt[1].y=2;
    	pt[2].x=5;
    	pt[2].y=2;
    	pt[3].x=4;
    	pt[3].y=3;
    }
    
    public void shadow() {
    	int i,j,p;
    	j=0;
    	synchronized (syncpoint) { 
    		pruefPunkte();
    		for (i=1;i<lines;i++) {
    			unzul=false;
    			for (p=0;p<4;p++) {
    				if (ptchk[p].y!=999) {
    					if (ptchk[p].y+i<lines) {
    						if (ih[ptchk[p].y+i][ptchk[p].x]>0) {
    							unzul=true;
    						}
    					} else {
    						unzul=true; 
    					}
    				}
    			}
    			if (unzul) {
    				j=i-1;
    				i=lines;
    			}
    		}
    		if (j!=0) {
    			for (p=0;p<4;p++) {
    				ptsh[p].y=pt[p].y+j;
    				ptsh[p].x=pt[p].x;
    			}
    		}
    	}
    }
    
    public void drop() {
    	int i,j,p;
    	j=0;
    	synchronized (syncpoint) { 
    		pruefPunkte();
    		for (i=1;i<lines;i++) {
    			unzul=false;
    			for (p=0;p<4;p++) {
    				if (ptchk[p].y!=999) {
    					if (ptchk[p].y+i<lines) {
    						if (ih[ptchk[p].y+i][ptchk[p].x]>0) {
    							unzul=true;
    						}
    					} else {
    						unzul=true; 
    					}
    				}
    			}
    			if (unzul) {
    				j=i-1;
    				i=lines;
    			}
    		}
    		if (j!=0) {
    			for (p=0;p<4;p++) {
    				ih[pt[p].y][pt[p].x]=0;
    			}
    			for (p=0;p<4;p++) {
    				pt[p].y=pt[p].y+j;
    				ptdrop[p].x=pt[p].x;
    				ptdrop[p].y=pt[p].y;
    			}
    			zDrop=j;
    			dropih=aktih;
    			aktLine+=j;
				playSound(1);
    		}
    	}	
    }
   
    public void shiftTiefer() {
    	int p;
    	synchronized (syncpoint) { 
    		if (zDrop==0) {
    			pruefPunkte();
    			unzul=false;
    			for (p=0;p<4;p++) {
    				if (pt[p].y+1>=lines) {
    					unzul=true; 
    				} else {
    					if (ptchk[p].y!=999) {
    						//Log.d(TAG, "   Pruef : " + p + " " + ptchk[p].x + " " + ptchk[p].y);
    						if ((ih[ptchk[p].y+1][ptchk[p].x]>0)) {
    							unzul=true;
    						}
    					}
    				}	
    			}
    			if (unzul) {
    				if (aktLine==0) {
    					gameOver=true;
    					playSound(4);
    					if (score>highScore) {
    						highScore=score;
    					}
    					gamePlayThread.setRunning(false);
    					topten();
    				} else {
    					gefuellteZeile();
    					neuer();
    					playSound(0);
    				}	
    			} else {
    				for (p=0;p<4;p++) {
    					ih[pt[p].y][pt[p].x]=0;
    					if (pt[p].y<lines-1) {
    						pt[p].y++;
    					}
    				}
    				for (p=0;p<4;p++) {
    					ih[pt[p].y][pt[p].x]=aktih;
    				}
    				aktLine++;
    				playSound(0);
    			}
    		} else {
				playSound(0);
    		}
    	}
    }
    
    private void gefuellteZeile() {
    	int i,j;
    	boolean soundDel=false;
    	for (i=lines-1;i>=0;i--) {
    		if (ih[i][0]>0 && ih[i][1]>0 && ih[i][2]>0 && ih[i][3]>0 && ih[i][4]>0 && ih[i][5]>0 &&
    				ih[i][6]>0 && ih[i][7]>0 && ih[i][8]>0 && ih[i][9]>0) {
    			for (j=i;j>0;j--) {
    				ih[j][0]=ih[j-1][0];
    				ih[j][1]=ih[j-1][1];
    				ih[j][2]=ih[j-1][2];
    				ih[j][3]=ih[j-1][3];
    				ih[j][4]=ih[j-1][4];
    				ih[j][5]=ih[j-1][5];
    				ih[j][6]=ih[j-1][6];
    				ih[j][7]=ih[j-1][7];
    				ih[j][8]=ih[j-1][8];
    				ih[j][9]=ih[j-1][9];
    			}
    			delLines++;
    			zL++;
    			soundDel=true;
    			score+=100;
    			if (zL==10) {
    				zL=0;
    				level++;
    				gamePlayThread.setLevel(level);
    				isLevelUp=true;
    			}
    			i++;
    		}
    	}		
		if (soundDel) {
			playSound(3);
		}
    }
    
    public void pruefPunkte() {
    	int p,q;
    	for (p=0;p<4;p++) {
    		ptchk[p].y=pt[p].y;
    		ptchk[p].x=pt[p].x;
    	}
    	for (p=0;p<4;p++) {
    		for (q=0;q<4;q++) {
    			if (pt[p].x==ptchk[q].x) {
    				if (pt[p].y<ptchk[q].y) {
    					ptchk[p].y=999;
    					ptchk[p].x=999;
    				}
    			}
    		}	
    	}
    }
    
    private void shiftHori(int diff) {
    	int shiftHori;
    	int p;
    	unzul=false;
    	if (diff>0) {
    		shiftHori=1;
    	} else {
    		shiftHori=-1;
    	}
    	synchronized (syncpoint) {
    		pruefPunkteHori(shiftHori);
    		for (p=0;p<4;p++) {
    			if (ptchk[p].x!=999) {
    				if ((ptchk[p].x+shiftHori>=cols) || 
    						(ptchk[p].x+shiftHori<0) ||
    						(ih[ptchk[p].y][ptchk[p].x+shiftHori]>0)) {
    					unzul=true;
    				}
    			}
    		}
    		if (!unzul) {
    			for (p=0;p<4;p++) {
    				ih[pt[p].y][pt[p].x]=0;
    				pt[p].x=pt[p].x+shiftHori;
    			}
    			for (p=0;p<4;p++) {
    				ih[pt[p].y][pt[p].x]=aktih;
    			}
   				playSound(0);
    			shadow();
    		}
    	}
    }
    
    private void pruefPunkteHori(int y) {
    	int p,q;
    	for (p=0;p<4;p++) {
    		ptchk[p].y=pt[p].y;
    		ptchk[p].x=pt[p].x;
    	}
    	for (p=0;p<4;p++) {
    		for (q=0;q<4;q++) {
    			if (pt[p].y==ptchk[q].y) {
    				if (y>0 && pt[p].x<ptchk[q].x) {
    					ptchk[p].y=999;
    					ptchk[p].x=999;
    				}
    				if (y<0 && pt[p].x>ptchk[q].x) {
    					ptchk[p].y=999;
    					ptchk[p].x=999;
    				}
    			}	
    		}
    	}
    }
    
    private void rotate() {
    	int i,j;
    	boolean unzul=false;
    	int maxLine = Math.max(pt[0].y,Math.max(pt[1].y, Math.max(pt[2].y, pt[3].y)));
    	int minLine = Math.min(pt[0].y,Math.min(pt[1].y, Math.min(pt[2].y, pt[3].y)));
    	int maxCol = Math.max(pt[0].x,Math.max(pt[1].x, Math.max(pt[2].x, pt[3].x)));
    	int minCol = Math.min(pt[0].x,Math.min(pt[1].x, Math.min(pt[2].x, pt[3].x)));
    	int line = (maxLine + minLine) / 2;
    	int col = (maxCol + minCol) / 2;
    	synchronized (syncpoint) {
    		for (i=0;i<4;i++) {
    			ptrot[i].x=pt[i].y-line+col;
    			ptrot[i].y=col-pt[i].x+line+1;
    			if (ptrot[i].x<0 || ptrot[i].x >= cols ||
    				ptrot[i].y<0 || ptrot[i].y >= lines) {
    				unzul=true;
    			}
    		}
    		if (!unzul) {
        		for (i=0;i<4;i++) {
        			ptchk[i].x=ptrot[i].x;
        			ptchk[i].y=ptrot[i].y;
        		}
        		for (i=0;i<4;i++) {
        			for (j=0;j<4;j++) {
        				if ((pt[j].x==ptchk[i].x) && (pt[j].y==ptchk[i].y)) {
        					ptchk[i].x=9999;
        				}
        			}
        		}
        		for (i=0;i<4;i++) {
        			if ((ptchk[i].x!=9999) && (ih[ptchk[i].y][ptchk[i].x]>0)) {
        				unzul=true;
        			}
        		}
    		}	
    		if (!unzul) {
    			for (i=0;i<4;i++) {
    				ih[pt[i].y][pt[i].x]=0;
    				pt[i].x=ptrot[i].x;
    				pt[i].y=ptrot[i].y;
    			}
    			for (i=0;i<4;i++) {
    				ih[pt[i].y][pt[i].x]=aktih;
    			}
				playSound(2);
	    		shadow();
    		}
    	}
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	int i,j;
    	frameCount++;
    	if (startZeit > 0 && !isPause && !gameOver) {
    		outZeit = sdf.format(Zeit + System.currentTimeMillis() - startZeit);
    	}	
    	paint1.reset();
    	paint1.setAlpha(255);
    	canvas.drawBitmap(bm[10], 0, 0, paint1);
    	paint1.setARGB(255, 200, 200, 200);
    	paint1.setStrokeWidth(2);
    	// Spielfeldumrandung
    	canvas.drawLine(margLeft, margTop, margRight, margTop, paint1);  // oben
    	canvas.drawLine(margLeft, margTop, margLeft, margBottom, paint1);  // links
    	canvas.drawLine(margRight, margTop, margRight, margBottom, paint1);  // rechts
    	canvas.drawLine(margLeft, margBottom, margRight, margBottom, paint1);  // unten
    	// Spielfeld
    	spielfeld(canvas);
    	paint1.reset();
    	// shadow
    	paint1.setARGB(50, 0, 0, 0);
    	if (isShadow) {
    		for (i=0;i<4;i++) {
    			canvas.drawRect(margLeft + ptsh[i].x * size, margTop + (ptsh[i].y - 2) * size,
    						margLeft + (ptsh[i].x + 1) * size, margTop + (ptsh[i].y - 1) * size, paint1);
    		}
    	}
    	// Inhalt 
    	paint1.setARGB(255, 0, 0, 0);
    	inhalt(canvas);
    	// Drop 
    	paint1.setARGB(100, 0, 0, 0);
    	if (zDrop>0) {
    		for (j=zDrop;j>=0;j-=2) {
    			for (i=0;i<4;i++) {
    				canvas.drawBitmap(bm[aktih-1], margLeft + ptdrop[i].x * size, 
    						margTop + (ptdrop[i].y - j - 2) * size, paint1);
    			}
    			if (zDrop-j>0) {
    				j=0;
    			}
    		}
    		zDrop-=2;
    		if (zDrop<2) {
    			zDrop=0;
    			synchronized (syncpoint) {
    				for (i=0;i<4;i++) {
    					ih[ptdrop[i].y][ptdrop[i].x]=dropih;
    				}
    			}	
    		}
    	}
    	if (isPause) {
        	gameDrawThread.setPause(true);
    		paint1.reset(); 
        	paint1.setARGB(255, 200, 200, 200);
        	paint1.setTextSize(40);
        	String p = "paused ...";
        	float w = paint1.measureText(p);
        	Rect r = new Rect();
        	paint1.getTextBounds(p, 0, p.length(), r);
        	canvas.drawRect(screenSize.x/2 - r.width()/2 - 30 - 1,
        			margTop+(diffTop*0)+120 - 1,
        			screenSize.x/2 + r.width()/2 + 30 + 1,
        			margTop+(diffTop*0)+120+r.height()+30 + 1,
        			paint1);
        	paint1.setARGB(255, 0, 0, 255);
        	canvas.drawRect(screenSize.x/2 - r.width()/2 - 30,
        			margTop+(diffTop*0)+120,
        			screenSize.x/2 + r.width()/2 + 30,
        			margTop+(diffTop*0)+120+r.height()+30,
        			paint1);
        	paint1.setARGB(255, 200, 200, 200);
        	canvas.drawText(p , ((screenSize.x/2) - (w/2)), margTop+(diffTop*0)+120+45, paint1);
    	}
    	if (gameOver) {
        	paint1.reset();
        	paint1.setARGB(255, 200, 200, 200);
        	paint1.setStrokeWidth(2);
    		canvas.drawLine(margLeft-(size/2), (margBottom-margTop)/2-bm[7].getHeight()/2, 
    				        margRight+(size/2), (margBottom-margTop)/2-bm[7].getHeight()/2, paint1);
    		canvas.drawLine(margLeft-(size/2), (margBottom-margTop)/2-bm[7].getHeight()/2, 
    				        margLeft-(size/2), (margBottom-margTop)/2-bm[7].getHeight()/2+size*5, paint1);
    		canvas.drawLine(margRight+(size/2), (margBottom-margTop)/2-bm[7].getHeight()/2, 
			        margRight+(size/2), (margBottom-margTop)/2-bm[7].getHeight()/2+size*5, paint1);
    		canvas.drawLine(margLeft-(size/2), (margBottom-margTop)/2-bm[7].getHeight()/2+size*5, 
			        margRight+(size/2), (margBottom-margTop)/2-bm[7].getHeight()/2+size*5, paint1);
        	paint1.reset();
        	paint1.setAlpha(180);
    		canvas.drawBitmap(bm[7], margLeft-(size/2), (margBottom-margTop)/2-bm[7].getHeight()/2,
    				paint1);
    		if (ttpos!=9999) {
    			if (ttpos==0) {
    				paint1.setARGB(255, 255, 0, 0);  
    			} else {
    				paint1.setARGB(255, 126, 0, 0);  
    			}
    		} else {
    			paint1.setARGB(255, 0, 0, 255);  
    		}
        	canvas.drawRect(11, margTop+(diffTop*2)+41, 129, margTop+(diffTop*2)+99, paint1);
        	paintTxt.setTextSize(40);
        	canvas.drawText(String.valueOf(highScore), 20, margTop+(diffTop*2)+83, paintTxt);
    		if (frameCount==6) {
				playSound(4);
    		}
    	} else {
    		if (isAnimation) {
    			if (frameCount<82) {
    				mAnimate(canvas);
    			}
    			if (frameCount==82) {
    				playSound(6);
    			}
    			if (frameCount==102) {
    	            startZeit = System.currentTimeMillis();
    		        gamePlayThread.setRunning(true);
    		        gamePlayThread.start();
    			}
    		} else {
    			if (frameCount==22) {
    				playSound(6);
    	            startZeit = System.currentTimeMillis();
    	        	gamePlayThread.setRunning(true);
    	        	gamePlayThread.start();
    			}	
    		}
    	}
    }
    
    private void mAnimate(Canvas canvas) {
    	int i;
		int left, top, right, bottom, fak=16;
		faktor1=1;
		faktor2=1;
		paint1.reset();
		paint1.setARGB(100, 0, 0, 0);
		if (frameCount<22) {
			fak=2;
		} else {
			if (frameCount<42) {
				fak=4;
			} else {
				if (frameCount<62) {
					fak=8;
				}
			}
		}
		/*
		if (frameCount<52) {
			faktor1=0;
			faktor2=0;
		} else {
			if (frameCount<81) {
				faktor1=frameCount/80f;
				faktor2=(160-frameCount)/80f;
			}
		}
		*/
		for (i=0;i<4;i++) {
			left = random.nextInt(screenSize.x);
			top = random.nextInt(screenSize.y);
			right = random.nextInt((screenSize.x-left)/fak+1)+left;
			bottom = random.nextInt((screenSize.y-top)/fak+1)+top;
	    	canvas.drawRect(left, top, right, bottom, paint1);
		}
		// Spielfeld
		spielfeld(canvas);
		paint1.reset();
		paint1.setAlpha(255);
		if (frameCount==1) {
		}
		if (frameCount==21) {
			diff=10;
			playSound(5);
		}
		if (frameCount>21 && frameCount<42) {
			paint1.setAlpha(255-(Math.abs(diff*25)));
			canvas.drawBitmap(bmVor[0], (screenSize.x-bmVor[0].getWidth())/2+(diff*7), margTop, paint1);
			diff--;
		}
		if (frameCount==41) {
			diff=10;
			playSound(5);
		}
		if (frameCount>41 && frameCount<62) {
			paint1.setAlpha(255-(Math.abs(diff*25)));
			canvas.drawBitmap(bmVor[1], (screenSize.x-bmVor[1].getWidth())/2+(diff*7), margTop, paint1);
			diff--;
		}
		if (frameCount==61) {
			diff=10;
			playSound(5);
		}
		if (frameCount>61) {
			paint1.setAlpha((frameCount-61)*12);
			inhalt(canvas);
		}
		if (frameCount>61 && frameCount<82) {
			paint1.reset();
			paint1.setAlpha(255-(Math.abs(diff*25)));
			canvas.drawBitmap(bmVor[2], (screenSize.x-bmVor[2].getWidth())/2+(diff*7), margTop, paint1);
			diff--;
		}
		if (frameCount<82) {
			// Loading ...
			paint1.setAlpha(255);
			canvas.drawBitmap(bmVor[3], margLeft+size, (margBottom-margTop)/2+50, paint1);
			paint1.setARGB(255, 200, 200, 200);
			canvas.drawRect(margLeft+size, (margBottom-margTop)/2+50+size+20, 
					margLeft+size+(size*8), (margBottom-margTop)/2+50+size+60, paint1);
			paint1.setARGB(255, 200, 0, 0);
			canvas.drawRect(margLeft+size+8, (margBottom-margTop)/2+50+size+28, 
					margLeft+size+(((size*8)-8)*(frameCount/81f)), (margBottom-margTop)/2+50+size+52, paint1);
		}	
    }
    
    public void inhalt(Canvas canvas) {
    	int i,j;
    	synchronized (syncpoint) {
    		for (i=2;i<lines;i++) {
    			for (j=0;j<cols;j++) {
    				if (ih[i][j]>0) {
    					canvas.drawBitmap(bm[ih[i][j]-1], (margLeft*faktor1) + j * (size*faktor2),
    							(margTop*faktor1) + (i-2) * (size*faktor2), paint1);
    				}
    			}
    		}
    	}
    }
    
    public void spielfeld(Canvas canvas) {
    	int i,j;
    	paint1.reset();
    	paint1.setARGB(255, 10, 10, 200);
    	canvas.drawRect(margLeft, margTop, margRight, margBottom, paint1);
    	// Orientierungspunkte
    	paint1.reset();
    	paint1.setARGB(255, 200, 200, 200);
    	paint1.setStrokeWidth(2);
    	for (i=2;i<lines;i+=2) {
    		for (j=2;j<cols;j+=2) {
    			canvas.drawPoint(margLeft + j * size, margTop + i * size, paint1);
    		}
    	}
    	diffTop = (margBottom - margTop) / 4;
    	// score
    	paint1.reset();
    	paint1.setARGB(255, 200, 200, 200);
    	canvas.drawRect(10, margTop+(diffTop*0)+40, 130, margTop+(diffTop*0)+100, paint1);
    	paint1.setARGB(255, 0, 0, 255);
    	canvas.drawRect(11, margTop+(diffTop*0)+41, 129, margTop+(diffTop*0)+99, paint1);
    	paintTxt.setTextSize(40);
    	canvas.drawText(String.valueOf(score), 15, margTop+(diffTop*0)+83, paintTxt);
    	paintTxt.setTextSize(35);
    	canvas.drawText("Score", 20, margTop+(diffTop*0)+20, paintTxt);
    	// abg. Lines
    	paint1.reset();
    	paint1.setARGB(255, 200, 200, 200);
    	canvas.drawRect(10, margTop+(diffTop*1)+40, 130, margTop+(diffTop*1)+100, paint1);
    	paint1.setARGB(255, 0, 0, 255);
    	canvas.drawRect(11, margTop+(diffTop*1)+41, 129, margTop+(diffTop*1)+99, paint1);
    	paintTxt.setTextSize(40);
    	canvas.drawText(String.valueOf(delLines), 40, margTop+(diffTop*1)+83, paintTxt);
    	paintTxt.setTextSize(35);
    	canvas.drawText("Lines", 20, margTop+(diffTop*1)+20, paintTxt);
    	// highsore
    	paint1.reset();
    	paint1.setARGB(255, 200, 200, 200);
    	canvas.drawRect(10, margTop+(diffTop*2)+40, 130, margTop+(diffTop*2)+100, paint1);
    	paint1.setARGB(255, 0, 0, 255);
    	canvas.drawRect(11, margTop+(diffTop*2)+41, 129, margTop+(diffTop*2)+99, paint1);
    	paintTxt.setTextSize(40);
    	canvas.drawText(String.valueOf(highScore), 15, margTop+(diffTop*2)+83, paintTxt);
    	paintTxt.setTextSize(35);
    	canvas.drawText("Hiscore", 10, margTop+(diffTop*2)+20, paintTxt);
    	// level
    	paint1.reset();
    	paint1.setARGB(255, 200, 200, 200);
    	canvas.drawRect(10, margTop+(diffTop*3)+40, 130, margTop+(diffTop*3)+100, paint1);
    	paint1.setARGB(255, 0, 0, 255);
    	canvas.drawRect(11, margTop+(diffTop*3)+41, 129, margTop+(diffTop*3)+99, paint1);
    	paintTxt.setTextSize(40);
    	canvas.drawText(String.valueOf(level), 50, margTop+(diffTop*3)+83, paintTxt);
    	paintTxt.setTextSize(35);
    	canvas.drawText("Level", 20, margTop+(diffTop*3)+20, paintTxt);
    	if (isLevelUp) {
    		if (levelUpFrames<60) {
    			levelUpFrames++;
    			if (levelUpFrames%10 > 4) {
    				canvas.drawBitmap(bm[11], 47, margTop+(diffTop*3)+120, paint1);
    			}
    		} else {
    			levelUpFrames=0;
    			isLevelUp=false;
    		}
    	}
    	// Zeit
    	paint1.reset();
    	paint1.setARGB(255, 200, 200, 200);
    	canvas.drawRect(screenSize.x-140+10, margTop+(diffTop*0)+40, screenSize.x-140+130, margTop+(diffTop*0)+100, paint1);
    	paint1.setARGB(255, 0, 0, 255);
    	canvas.drawRect(screenSize.x-140+11, margTop+(diffTop*0)+41, screenSize.x-140+129, margTop+(diffTop*0)+99, paint1);
    	paintTxt.setTextSize(40);
    	canvas.drawText(outZeit, screenSize.x-140+15, margTop+(diffTop*0)+83, paintTxt);
    	paintTxt.setTextSize(35);
    	canvas.drawText("Zeit", screenSize.x-140+20, margTop+(diffTop*0)+20, paintTxt);
    	// next
    	if (isNext) {
        	paint1.reset();
        	paint1.setARGB(255, 200, 200, 200);
        	canvas.drawRect(screenSize.x-140+10, margTop+(diffTop*1)+40, screenSize.x-140+130, margTop+(diffTop*1)+100, paint1);
        	paint1.setARGB(255, 0, 0, 255);
        	canvas.drawRect(screenSize.x-140+11, margTop+(diffTop*1)+41, screenSize.x-140+129, margTop+(diffTop*1)+99, paint1);
        	paintTxt.setTextSize(35);
        	canvas.drawText("Next", screenSize.x-140+20, margTop+(diffTop*1)+20, paintTxt);
    		for (i=0;i<4;i++) {
    			int p = nextih - 1;
    			canvas.drawBitmap(bmnext[p], screenSize.x-140 - 30 + ptnext[i].x * 22,
    					margTop+(diffTop*1)+50 + (ptnext[i].y - 2) * 22, paint1);
    		}
    	}
    	// dropbutton
    	if (isDropButton) {
    		paint1.reset();
    		if (isDropPressed) {
    			canvas.drawBitmap(bm[9], screenSize.x-bm[9].getWidth()-10, 
    					margBottom-bm[9].getHeight(), paint1);
    		} else {
    			canvas.drawBitmap(bm[8], screenSize.x-bm[8].getWidth()-10, 
    					margBottom-bm[8].getHeight(), paint1);
    		}
    	}

    }

    public void playSound(int i) {
    	if (isSound) {
    		soundPool.play(sounds[i], actVolume, actVolume, 1, 0, 1f);
    	}
    }
    
    @Override
    public boolean performClick() {
    	super.performClick();
		Intent intent = new Intent(context, TopTenActivity.class);
		intent.putExtra("pos", ttpos);
		context.startActivity(intent);
		((Activity)context).finish();
    	return true;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if (isPause) return true;
    	switch (event.getAction()) {
    	case MotionEvent.ACTION_DOWN: 
    		if (!gameOver && zDrop==0) {
    			actionDown=true;
    			actionMove=false;
    			downTime = System.currentTimeMillis();
    			down.x = (int)event.getX();
    			down.y = (int)event.getY();
    			if ((isDropButton) && (down.x>margRight) && (down.y>margBottom-bm[9].getHeight())) {
    				isDropPressed=true;
    			}
    		}
    		break;

    	case MotionEvent.ACTION_MOVE:
    		move.x = (int)event.getX();
    		move.y = (int)event.getY();
    		if (!gameOver && zDrop==0) {
    			if (actionDown) {
    				if (!isDropPressed) {
    					if (move.y - down.y > (int)(size * 1.5f)) {
    						isDropPressed=true;
   							drop();
    						down.y = move.x;
    						down.x = move.x;
    					} else {
    						if (Math.abs(move.x - down.x) > size) {
    							actionMove=true;
    							shiftHori(move.x - down.x);
    							down.x = move.x;
    						}
    						if ((move.y - down.y) > size) {
    							actionMove=true;
    							shiftTiefer();
    							down.y = move.y;
    						}
    					}
    				}
    			}
    		}
    		break;

    	case MotionEvent.ACTION_UP:   
    		upTime = System.currentTimeMillis();
    		move.x = (int)event.getX();
    		move.y = (int)event.getY();
    		if (!gameOver) {
    			if (zDrop==0) {
    				if (actionDown) {
    					if (isDropPressed) {
    						if ((isDropButton) && (move.x>margRight) && (move.y>margBottom-bm[9].getHeight())) {
    							drop();
    						}	
    					} else {
    						if (upTime-downTime<150) {
    							if (!actionMove) {
    								rotate();
    							}
    						} else {
    							if (Math.abs(move.x - down.x) > size) {
    								shiftHori(move.x - down.x);
    								down.x = move.x;
    							}
    							if ((move.y - down.y) > size) {
    								shiftTiefer();
    								down.y = move.y;
    							}
    						}
    					}
    				}
    			}
    		} else {
    			performClick();
    		}
    		actionDown=false;
    		isDropPressed=false;
    		break;
    	}
    	return true;
    }
    
    public void swapPause() {
    	if (isPause) {
            startZeit = System.currentTimeMillis();
            gamePlayThread.setPause(false);
        	gameDrawThread.setPause(false);
    		isPause=false;
    	} else {
            Zeit = Zeit + System.currentTimeMillis() - startZeit;
            gamePlayThread.setPause(true);
    		isPause=true;
    	}
    }
    
	protected void init() {
    	int i,j;
    	for (i=0;i<4;i++) {
    		pt[i] = new Point(0,0);
    		ptchk[i] = new Point(0,0);
    		ptsh[i] = new Point(0,0);
    		ptnext[i] = new Point(0,0);
    		ptrot[i] = new Point(0,0);
    		ptdrop[i] = new Point(0,0);
    	}
    	for (i=0;i<lines;i++) {
    		for (j=0;j<cols;j++) {
        		ih[i][j] = 0;
    		}
    	}
    	Zeit=0;
    	nextih = random.nextInt(7) + 1;
		paintTxt.setARGB(255, 200, 200, 200);
		paintTxt.setTextSize(40);
		bm[0] = BitmapFactory.decodeResource(getResources(), R.drawable.tetris_blau); 
		bm[0] = Bitmap.createScaledBitmap(bm[0], size, size, false); 
		bm[1] = BitmapFactory.decodeResource(getResources(), R.drawable.tetris_rot); 
		bm[1] = Bitmap.createScaledBitmap(bm[1], size, size, false); 
		bm[2] = BitmapFactory.decodeResource(getResources(), R.drawable.tetris_gruen); 
		bm[2] = Bitmap.createScaledBitmap(bm[2], size, size, false); 
		bm[3] = BitmapFactory.decodeResource(getResources(), R.drawable.tetris_gelb); 
		bm[3] = Bitmap.createScaledBitmap(bm[3], size, size, false); 
		bm[4] = BitmapFactory.decodeResource(getResources(), R.drawable.tetris_lila); 
		bm[4] = Bitmap.createScaledBitmap(bm[4], size, size, false); 
		bm[5] = BitmapFactory.decodeResource(getResources(), R.drawable.tetris_orange); 
		bm[5] = Bitmap.createScaledBitmap(bm[5], size, size, false); 
		bm[6] = BitmapFactory.decodeResource(getResources(), R.drawable.tetris_babyblau); 
		bm[6] = Bitmap.createScaledBitmap(bm[6], size, size, false); 
		bmnext[0] = Bitmap.createScaledBitmap(bm[0], 22, 22, false); 
		bmnext[1] = Bitmap.createScaledBitmap(bm[1], 22, 22, false); 
		bmnext[2] = Bitmap.createScaledBitmap(bm[2], 22, 22, false); 
		bmnext[3] = Bitmap.createScaledBitmap(bm[3], 22, 22, false); 
		bmnext[4] = Bitmap.createScaledBitmap(bm[4], 22, 22, false); 
		bmnext[5] = Bitmap.createScaledBitmap(bm[5], 22, 22, false); 
		bmnext[6] = Bitmap.createScaledBitmap(bm[6], 22, 22, false); 
		bm[7] = BitmapFactory.decodeResource(getResources(), R.drawable.gameover); 
		bm[7] = Bitmap.createScaledBitmap(bm[7], size*11, size*5, false);
		bm[8] = BitmapFactory.decodeResource(getResources(), R.drawable.green_button); 
		bm[9] = BitmapFactory.decodeResource(getResources(), R.drawable.green_button_dark); 
		bm[10] = BitmapFactory.decodeResource(getResources(), R.drawable.nachthimmel); 
		bm[11] = BitmapFactory.decodeResource(getResources(), R.drawable.pfeil); 
		bm[11] = Bitmap.createScaledBitmap(bm[11], bm[11].getWidth()/10, bm[11].getHeight()/10, false);
		bmVor[0] = BitmapFactory.decodeResource(getResources(), R.drawable.drei); 
		bmVor[0] = Bitmap.createScaledBitmap(bmVor[0], bmVor[0].getWidth()/2, bmVor[0].getHeight()/2, false);
		bmVor[1] = BitmapFactory.decodeResource(getResources(), R.drawable.zwei); 
		bmVor[1] = Bitmap.createScaledBitmap(bmVor[1], bmVor[1].getWidth()/2, bmVor[1].getHeight()/2, false);
		bmVor[2] = BitmapFactory.decodeResource(getResources(), R.drawable.eins); 
		bmVor[2] = Bitmap.createScaledBitmap(bmVor[2], bmVor[2].getWidth()/2, bmVor[2].getHeight()/2, false);
		bmVor[3] = BitmapFactory.decodeResource(getResources(), R.drawable.loading); 
		bmVor[3] = Bitmap.createScaledBitmap(bmVor[3], size*8, size, false);
		
		AudioManager audioManager = (AudioManager) ((Context)context).getSystemService(Context.AUDIO_SERVICE);
		actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		sounds[0] = soundPool.load(context, R.raw.ein_schalten, 1);
		sounds[1] = soundPool.load(context, R.raw.drop, 1); 
		sounds[2] = soundPool.load(context, R.raw.rotate, 1); 
		sounds[3] = soundPool.load(context, R.raw.ceres, 1); 
		sounds[4] = soundPool.load(context, R.raw.gameover, 1);
		sounds[5] = soundPool.load(context, R.raw.tischglocke, 1);
		sounds[6] = soundPool.load(context, R.raw.tischglocke3, 1);
	}
    
    private void loadGame() {
    	try {
        	File file = new File(context.getFilesDir(), "glorissave");
    		DataInputStream fin = new DataInputStream(new FileInputStream(file));
    		int i,j;
    		score=fin.readInt();
    		level=fin.readInt();
    		delLines=fin.readInt();
    		zL=fin.readInt();
    		aktih=fin.readInt();
    		aktLine=fin.readInt();
    		nextih=fin.readInt();
    		for (i=0;i<4;i++) {
    			pt[i].x=fin.readInt();
    			pt[i].y=fin.readInt();
    		}
    		for (i=0;i<4;i++) {
    			ptnext[i].x=fin.readInt();
    			ptnext[i].y=fin.readInt();
    		}
    		for (i=0;i<4;i++) {
    			ptsh[i].x=fin.readInt();
    			ptsh[i].y=fin.readInt();
    		}
    		for (i=0;i<lines;i++) {
    			for (j=0;j<cols;j++) {
    				ih[i][j]=fin.readInt();
    			}
    		}
    		Zeit=fin.readLong();
    		fin.close();
    	} catch (IOException e){
    		Log.d(TAG, " IOException : " + e);
    	}
    }
    
    private void saveGame() {
        try {
        	File file = new File(context.getFilesDir(), "glorissave");
        	DataOutputStream fout = new DataOutputStream(new FileOutputStream(file));
        	int i,j;
            fout.writeInt(score);
            fout.writeInt(level);
            fout.writeInt(delLines);
            fout.writeInt(zL);
            fout.writeInt(aktih);
            fout.writeInt(aktLine);
            fout.writeInt(nextih);
            for (i=0;i<4;i++) {
            	fout.writeInt(pt[i].x);
            	fout.writeInt(pt[i].y);
            }
            for (i=0;i<4;i++) {
            	fout.writeInt(ptnext[i].x);
            	fout.writeInt(ptnext[i].y);
            }
            for (i=0;i<4;i++) {
            	fout.writeInt(ptsh[i].x);
            	fout.writeInt(ptsh[i].y);
            }
            for (i=0;i<lines;i++) {
	            for (j=0;j<cols;j++) {
	            	fout.writeInt(ih[i][j]);
	            }
            }
            if (!isPause) Zeit = Zeit + System.currentTimeMillis() - startZeit;
            fout.writeLong(Zeit);
            
            fout.close();
        } catch (IOException e){
    		Log.d(TAG, " IOException : " + e);
        }
    }
    
    private void topten() {
    	File file = new File(context.getFilesDir(), "bestenliste");
    	int[] ttscore = new int[10]; 
    	int[] ttlevel = new int[10];
    	long[] tttime = new long[10];
    	int i,j;
    	if (file.exists()) {
    		try {
    			DataInputStream fin = new DataInputStream(new FileInputStream(file));
    			for (i=0;i<10;i++) {
    				ttscore[i]=fin.readInt();
    				ttlevel[i]=fin.readInt();
    				tttime[i]=fin.readLong();
    			}
    			fin.close();
    		} catch (IOException e){
    			Log.d(TAG, " IOException : " + e);
    		}
    	} else {
    		for (i=0;i<10;i++) {
    			ttscore[i]=0;
    			ttlevel[i]=0;
    			tttime[i]=0;
    		}
    	}
    	for (i=0;i<10;i++) {
    		if (score>=ttscore[i]) {
    			for (j=9;j>i;j--) {
    				ttscore[j]=ttscore[j-1];
    				ttlevel[j]=ttlevel[j-1];
    				tttime[j]=tttime[j-1];
    			}
				ttscore[i]=score;
				ttlevel[i]=level;
				tttime[i]=System.currentTimeMillis();
				ttpos=i;
				i=10;
    		}
    	}
        try {
        	DataOutputStream fout = new DataOutputStream(new FileOutputStream(file));
            for (i=0;i<10;i++) {
            	fout.writeInt(ttscore[i]);
            	fout.writeInt(ttlevel[i]);
            	fout.writeLong(tttime[i]);
            }
            fout.close();
        } catch (IOException e){
    		Log.d(TAG, " IOException : " + e);
        }
    }
    
}

