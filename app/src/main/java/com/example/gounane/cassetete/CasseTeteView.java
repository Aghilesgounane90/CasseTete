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

    // taille de la carte
    static final int    carteWidth    = 6;
    static final int    carteHeight   = 6;
    static final int    carteTileSize = 40;

    int[][] carte;

    // ancres pour pouvoir centrer la carte du jeu
    int        carteTopAnchor;                   // coordonnées en Y du point d'ancrage de notre carte
    int        carteLeftAnchor;                  // coordonnées en X du point d'ancrage de notre carte

    // thread utiliser pour animer les zones de depot des diamants
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
                carte[j][i]= met.ref[j][i];
            }
        }
    }
    //dessin de la carte du jeu
    private void paintcarte(Canvas canvas) {
        for (int i=0; i< carteHeight; i++) {
            for (int j=0; j< carteWidth; j++) {
                canvas.drawBitmap(vide, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
            }
        }
    }
    // dessin du jeu (fond uni, en fonction du jeu gagne ou pas dessin du plateau et du joueur des diamants et des fleches)
    private void nDraw(Canvas canvas) {
        canvas.drawRGB(44,44,44);
        paintcarte(canvas);


    }

    @Override
    public void run() {

        Canvas c = null;
        while (in) {
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




        boolean joined = false;

        while (!joined) {

            try {

                cv_thread.join();

                joined = true;

            } catch (InterruptedException e) {}

        }
    }
}