package com.example.gounane.cassetete;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by gounane on 15/11/17.
 */

public class CasseTeteView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    // Declaration des images
    private Bitmap vide;
    private Bitmap rouge;
    private Bitmap vert;
    private Bitmap blue;
    private Bitmap rose;
    private Bitmap violet;
    private Bitmap jaune;
    private Bitmap vvvv;

    // constante modelisant les differentes types de cases
    static final int    CST_vide    = 0;
    static final int    CST_vert   = 1;
    static final int    CST_blue     = 2;
    static final int    CST_rose      = 3;
    static final int    CST_violet     = 4;
    static final int    CST_jaune     = 5;
    static final int    CST_vvvv     = 6;
    static final int    CST_rouge    = 7;

    // Declaration des objets Ressources et Context permettant d'accéder aux ressources de notre application et de les charger
    private Resources 	mRes;
    private Context 	mContext;

    // taille de la carte du jeu
    static final int    carteWidth    = 10;
    static final int    carteHeight   = 18;
    static final int    carteTileSize = 60;

    // taille de la carte du gain
    static final int    carteWidthGain    = 4;
    static final int    carteHeightGain   = 6;

    int[][] carte;
    int[][] carteGain;

    // ancres pour pouvoir centrer la carte du jeu
    int        carteTopAnchor;                   // coordonnées en Y du point d'ancrage de notre carte
    int        carteLeftAnchor;                  // coordonnées en X du point d'ancrage de notre carte

    // ancres pour pouvoir centrer la carte du gain du jeu
    int        carteTopGain; // coordonnées en Y du point d'ancrage de notre carte gain
    int        carteLeftGain;// coordonnées en X du point d'ancrage de notre carte gain


    // thread utiliser pour animer les zones de depot des diamants
    boolean keepDrawing = true;
    private     boolean in      = true;
    private     Thread  cv_thread;
    SurfaceHolder holder;

    Paint paint;

    /**

     * Utilisé pour construire la vue depuis XML sans style

     * @param context le contexte qui héberge la vue

     * @param attrs les attributs définis en XML

     */
    public CasseTeteView(Context context, AttributeSet attrs) {
        super(context, attrs);


        // permet d'ecouter les surfaceChanged, surfaceCreated, surfaceDestroyed
        holder = getHolder();
        holder.addCallback(this);

        // chargement des images
        mContext	= context;
        mRes 		= mContext.getResources();
        vide 		= BitmapFactory.decodeResource(mRes, R.drawable.vide);
        vert 		= BitmapFactory.decodeResource(mRes, R.drawable.vert);

        // initialisation des parmametres du jeu
        initparameters();
        // creation du thread
        cv_thread   = new Thread(this);
        // prise de focus pour gestion des touches
        setFocusable(true);
    }

    // initialisation du jeu
    public void initparameters() {
        Paint paint = new Paint();
        paint.setColor(0xff0000);

        paint.setDither(true);
        paint.setColor(0xFFFFFF00);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(3);
        paint.setTextAlign(Paint.Align.LEFT);
        carte           = new int[carteHeight][carteWidth];
        loadlevel();
        carteTopAnchor  = (getHeight()-carteHeight*carteTileSize)/2;
        carteLeftAnchor = (getWidth()-carteWidth*carteTileSize)/2;

        carteTopGain = (getHeight()-carteHeightGain*carteTileSize)/2;
        carteLeftGain = (getWidth()-carteWidthGain*carteTileSize)/2;

        if ((cv_thread!=null) && (!cv_thread.isAlive())) {
            cv_thread.start();
            Log.e("-FCT-", "cv_thread.start()");
        }
    }

    Methodes met = new Methodes();
    // chargement du niveau a partir du tableau de reference du niveau
    private void loadlevel() {
        for (int i=0; i< carteHeight; i++) {
            for (int j=0; j< carteWidth; j++) {
                carte[i][j]= met.ref[i][j];
            }
        }
    }
    Paint p = new Paint();
    //dessin de la carte du jeu
    private void paintcarte(Canvas canvas) {
        /*for (int i=0; i< carteHeight; i++) {
            for (int j=0; j< carteWidth; j++) {
                canvas.drawBitmap(vide, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);

            }
        }*/

        p.setColor(Color.BLUE);

        p.setStyle(Paint.Style.FILL);

        canvas.drawRect(carteLeftGain,carteTopGain,carteLeftGain+(carteWidthGain*carteTileSize),carteTopGain+(carteHeightGain*carteTileSize), p);
    }

    Random r = new Random();
    int xVert = 1 + r.nextInt(carteWidth - 3);
    int yVert = 1 + r.nextInt(carteHeight - 3);

    // dessin du cadre vert
    private void paintVert(Canvas canvas) {
        canvas.drawBitmap(vert, carteLeftAnchor+(xVert*carteTileSize),  carteTopAnchor+(yVert*carteTileSize), null);
        canvas.drawBitmap(vert, carteLeftAnchor+(xVert*carteTileSize),  carteTopAnchor+((yVert+1)*carteTileSize), null);
        canvas.drawBitmap(vert, carteLeftAnchor+((xVert+1)*carteTileSize),  carteTopAnchor+(yVert*carteTileSize), null);
        canvas.drawBitmap(vert, carteLeftAnchor+((xVert+1)*carteTileSize),  carteTopAnchor+((yVert+1)*carteTileSize), null);
    }
    // dessin du jeu (fond uni, en fonction du jeu gagne ou pas dessin du plateau et du joueur des diamants et des fleches)
    private void nDraw(Canvas canvas) {
        canvas.drawRGB(44,44,44);
        paintcarte(canvas);
        paintVert(canvas);
    }

    @Override
    public void run() {

        Canvas c = null;
        while (keepDrawing) {
            try {
                cv_thread.sleep(40);
                //currentStepZone = (currentStepZone + 1) % maxStepZone;
                try {
                    c = holder.lockCanvas(null);
                    nDraw(c);
                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);
                    }
                }
            } catch(Exception e) {
                Log.e("-> RUN <-", "PB DANS RUN");
            }
        }
    }

    // callback sur le cycle de vie de la surfaceview
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("-> FCT <-", "surfaceChanged "+ width +" - "+ height);
        initparameters();
    }

    public void surfaceCreated(SurfaceHolder arg0) {
        Log.i("-> FCT <-", "surfaceCreated");
    }


    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.i("-> FCT <-", "surfaceDestroyed");
        keepDrawing = false;



        boolean joined = false;

        while (!joined) {

            try {

                cv_thread.join();

                joined = true;

            } catch (InterruptedException e) {}

        }
    }
    int xvert=0;
    int yvert=0;
    boolean move = false;
    int xTmpPlayer;
    int yTmpPlayer;

    // fonction permettant de recuperer les evenements tactiles
    public boolean onTouchEvent (MotionEvent event) {


        /*xTmpPlayer = xPlayer;
        yTmpPlayer = yPlayer;




        Log.i("-> FCT <-", "onTouchEvent: "+ event.getX());
        Log.i("-> FCT <-", "onTouchEvent&: "+ xvert);
        Log.i("-> FCT <-", "onTouchEvent3: "+ event.getAction());
        if((xPlayer*carteTileSize)<event.getX()&&event.getX()<((xPlayer+2)*carteTileSize)&&(yPlayer*carteTileSize)<event.getY()&&event.getY()<((yPlayer+2)*carteTileSize))
        {
            if (event.getAction()==MotionEvent.ACTION_DOWN){
                xvert= (int) ((-xPlayer*carteTileSize)+event.getX());
                yvert= (int) ((-yPlayer*carteTileSize)+event.getY());

            }
            if (event.getAction()==MotionEvent.ACTION_MOVE) {
                xPlayer = (int) ((event.getX()) - xvert) / carteTileSize;
                yPlayer = (int) ((event.getY()) - xvert) / carteTileSize;
                Log.i("-> FCT <-", "onTouchEvent&ééé11111111111: " + xPlayer);
                Log.i("-> FCT <-", "onTouchEvent&ééé11111111111: " + yPlayer);
            }
        }*/

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                xvert= (int) (event.getX()-(carteLeftAnchor+(xVert*carteTileSize)));
                yvert= (int) (event.getY()-(carteTopAnchor+(yVert*carteTileSize)));

                if((carteLeftAnchor+(xVert*carteTileSize))<event.getX()&&event.getX()<(carteLeftAnchor+((xVert+2)*carteTileSize))&&((carteTopAnchor+(yVert*carteTileSize)))<event.getY()&&event.getY()<(carteTopAnchor+((yVert+2)*carteTileSize)))
                    move = true;
                break;
            case MotionEvent.ACTION_MOVE:
                int xTmp = xVert;
                int yTmp = yVert;
                if(move){
                    if(IsOut( (int)(event.getX()/carteTileSize), (int)(event.getY()/carteTileSize))){
                        xVert=xTmp;
                        yVert=yTmp;
                    }else
                    {
                        xVert = (int) ((event.getX()) - (xvert)) / carteTileSize;
                        yVert = (int) ((event.getY()) - (xvert)) / carteTileSize;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:

                move = false;
                break;
        }

                invalidate ();
        return true;
    }

    // verification que nous sommes dans le tableau
    private boolean IsOut(int x, int y) {
        if ((x < (carteLeftAnchor/carteTileSize)) || (x > (carteLeftAnchor/carteTileSize)+carteWidth-3)) {
            return true;
        }
        if ((y < 1) || (y > 16)) {
            return true;
        }
        return false;
    }
}
