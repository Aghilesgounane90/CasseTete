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
    private Bitmap win;

    // constante modelisant les differentes types de cases
    static final int CST_vide = 0;
    static final int CST_vert = 1;
    static final int CST_blue = 2;
    static final int CST_rose = 3;
    static final int CST_violet = 4;
    static final int CST_jaune = 5;
    static final int CST_zone = 6;
    static final int CST_rouge = 7;

    // Declaration des objets Ressources et Context permettant d'accéder aux ressources de notre application et de les charger
    private Resources mRes;
    private Context mContext;

    // taille de la carte du jeu
    static final int carteWidth = 10;
    static final int carteHeight = 18;
    static final int carteTileSize = 60;

    // taille de la carte du gain
    static final int carteWidthGain = 3;
    static final int carteHeightGain = 5;

    int[][] carte;
    int[][] carteGain;

    // ancres pour pouvoir centrer la carte du jeu
    int carteTopAnchor;                   // coordonnées en Y du point d'ancrage de notre carte
    int carteLeftAnchor;                  // coordonnées en X du point d'ancrage de notre carte

    // ancres pour pouvoir centrer la carte du gain du jeu
    int carteTopGain; // coordonnées en Y du point d'ancrage de notre carte gain
    int carteLeftGain;// coordonnées en X du point d'ancrage de notre carte gain

    // position courante des diamants



    Random r = new Random();
    int xVert = 1 + r.nextInt(carteWidth - 3);
    int yVert = 1 + r.nextInt(carteHeight - 3);
    int xVert1 = 1 + r.nextInt(carteWidth - 3);
    int yVert1 = 1 + r.nextInt(carteHeight - 3);
    int xRouge = 1 + r.nextInt(carteWidth - 3);
    int yRouge = 1 + r.nextInt(carteHeight - 3);
    int xBlue = 1 + r.nextInt(carteWidth - 3);
    int yBlue = 1 + r.nextInt(carteHeight - 3);


    // thread utiliser pour animer les zones de depot des diamants
    boolean keepDrawing = true;
    private boolean in = true;
    private Thread cv_thread;
    SurfaceHolder holder;

    Paint paint;

    /**
     * Utilisé pour construire la vue depuis XML sans style
     *
     * @param context le contexte qui héberge la vue
     * @param attrs   les attributs définis en XML
     */
    public CasseTeteView(Context context, AttributeSet attrs) {
        super(context, attrs);


        // permet d'ecouter les surfaceChanged, surfaceCreated, surfaceDestroyed
        holder = getHolder();
        holder.addCallback(this);

        // chargement des images
        mContext = context;
        mRes = mContext.getResources();
        vide = BitmapFactory.decodeResource(mRes, R.drawable.vide);
        vert = BitmapFactory.decodeResource(mRes, R.drawable.vert);
        rouge = BitmapFactory.decodeResource(mRes, R.drawable.rouge);
        blue = BitmapFactory.decodeResource(mRes, R.drawable.blue);
        win = BitmapFactory.decodeResource(mRes, R.drawable.win);

        // initialisation des parmametres du jeu
        initparameters();
        // creation du thread
        cv_thread = new Thread(this);
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
        carte = new int[carteHeight][carteWidth];
        loadlevel();
        carteTopAnchor = (getHeight() - carteHeight * carteTileSize) / 2;
        carteLeftAnchor = (getWidth() - carteWidth * carteTileSize) / 2;

        carteTopGain = (getHeight() - carteHeightGain * carteTileSize) / 2;
        carteLeftGain = (getWidth() - carteWidthGain * carteTileSize) / 2;

        if ((cv_thread != null) && (!cv_thread.isAlive())) {
            cv_thread.start();
            Log.e("-FCT-", "cv_thread.start()");
        }
    }

    // tableau de reference du terrain
    int[][] ref = {
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_zone, CST_zone, CST_zone, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_zone, CST_zone, CST_zone, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_zone, CST_zone, CST_zone, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_zone, CST_zone, CST_zone, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_zone, CST_zone, CST_zone, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
    };
    // chargement du niveau a partir du tableau de reference du niveau
    private void loadlevel() {
        for (int i = 0; i < carteHeight; i++) {
            for (int j = 0; j < carteWidth; j++) {
                carte[i][j] = ref[i][j];
            }
        }
    }

    Paint p = new Paint();

    // dessin du gagne si gagne
    private void paintwin(Canvas canvas) {
        canvas.drawBitmap(win, carteLeftAnchor+ 3*carteTileSize, carteTopAnchor+ 4*carteTileSize, null);
    }
    //dessin de la carte du jeu
    private void paintcarte(Canvas canvas) {
        /*for (int i=0; i< carteHeight; i++) {
            for (int j=0; j< carteWidth; j++) {
                canvas.drawBitmap(vide, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);

            }
        }*/

        p.setColor(Color.BLUE);

        p.setStyle(Paint.Style.FILL);
        //p.setAlpha(10);

        canvas.drawRect((float)(carteLeftGain+(0.5*carteTileSize)), (float)(carteTopGain+(0.5*carteTileSize)), (float)(carteLeftGain + ((carteWidthGain+(0.5)) * carteTileSize)), (float)(carteTopGain + ((carteHeightGain+(0.5)) * carteTileSize)), p);
    }



    // dessin du brique vert
    private void paintVert(Canvas canvas) {
        canvas.drawBitmap(vert, carteLeftAnchor + (xVert * carteTileSize), carteTopAnchor + (yVert * carteTileSize), null);
        canvas.drawBitmap(vert, carteLeftAnchor + (xVert * carteTileSize), carteTopAnchor + ((yVert + 1) * carteTileSize), null);
        canvas.drawBitmap(vert, carteLeftAnchor + ((xVert + 1) * carteTileSize), carteTopAnchor + (yVert * carteTileSize), null);
        canvas.drawBitmap(vert, carteLeftAnchor + ((xVert + 1) * carteTileSize), carteTopAnchor + ((yVert + 1) * carteTileSize), null);
    }

    // dessin du brique vert
    private void paintVert1(Canvas canvas) {
        canvas.drawBitmap(vert, carteLeftAnchor + (xVert1 * carteTileSize), carteTopAnchor + (yVert1 * carteTileSize), null);
        canvas.drawBitmap(vert, carteLeftAnchor + (xVert1 * carteTileSize), carteTopAnchor + ((yVert1 + 1) * carteTileSize), null);
        canvas.drawBitmap(vert, carteLeftAnchor + ((xVert1 + 1) * carteTileSize), carteTopAnchor + (yVert1 * carteTileSize), null);
        canvas.drawBitmap(vert, carteLeftAnchor + ((xVert1 + 1) * carteTileSize), carteTopAnchor + ((yVert1 + 1) * carteTileSize), null);
    }
    // dessin du brique rouge
    private void paintRouge(Canvas canvas) {
        canvas.drawBitmap(rouge, carteLeftAnchor + (xRouge * carteTileSize), carteTopAnchor + (yRouge * carteTileSize), null);
        canvas.drawBitmap(rouge, carteLeftAnchor + (xRouge * carteTileSize), carteTopAnchor + ((yRouge + 1) * carteTileSize), null);
        canvas.drawBitmap(rouge, carteLeftAnchor + (xRouge * carteTileSize), carteTopAnchor + ((yRouge + 2) * carteTileSize), null);
    }
    // dessin du brique rouge
    private void paintBlue(Canvas canvas) {
        canvas.drawBitmap(blue, carteLeftAnchor + (xBlue * carteTileSize), carteTopAnchor + (yBlue * carteTileSize), null);
        canvas.drawBitmap(blue, carteLeftAnchor + (xBlue * carteTileSize), carteTopAnchor + ((yBlue + 1) * carteTileSize), null);
        canvas.drawBitmap(blue, carteLeftAnchor + (xBlue * carteTileSize), carteTopAnchor + ((yBlue + 2) * carteTileSize), null);
        canvas.drawBitmap(blue, carteLeftAnchor + ((xBlue+1) * carteTileSize), carteTopAnchor + ((yBlue + 2) * carteTileSize), null);
    }

    //controle de la valeur d'une cellule
    private boolean IsCell(int x, int y, int mask) {
        if (carte[y][x] == mask) {
            return true;
        }
        return false;
    }
    // permet d'identifier si la partie est gagnee (tous les diamants à leur place)
    private boolean isWon() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; i < 2; i++) {
             if (!IsCell(xVert+i, yVert+j, CST_zone)) {
                 return false;
                }
            }
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; i < 2; i++) {
                if (!IsCell(xVert1+i, yVert1+j, CST_zone)) {
                    return false;
                }
            }
        }
        for (int i = 0; i < 3; i++) {
            if (!IsCell(xRouge, yRouge + i, CST_zone)) {
                return false;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (!IsCell(xBlue, yBlue + i, CST_zone)) {
                return false;
            }
        }
        if (!IsCell(xBlue+1, yBlue+2, CST_zone)) {
            return false;
        }
     return true;
    }
    // dessin du jeu (fond uni, en fonction du jeu gagne ou pas dessin du plateau et du joueur des diamants et des fleches)
    private void nDraw(Canvas canvas) {
        canvas.drawRGB(44,44,44);
        if (isWon()) {
            paintcarte(canvas);
            paintwin(canvas);
        }else{
        paintcarte(canvas);
        paintVert(canvas);
        paintVert1(canvas);
        paintRouge(canvas);
        paintBlue(canvas);
        }
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
    int xvert1=0;
    int yvert1=0;
    int xrouge=0;
    int yrouge=0;
    int xblue=0;
    int yblue=0;
    boolean moveVert = false;
    boolean moveVert1 = false;
    boolean moveRouge = false;
    boolean moveBlue = false;
    int xTmpPlayer;
    int yTmpPlayer;

    // fonction permettant de recuperer les evenements tactiles
    public boolean onTouchEvent (MotionEvent event) {

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                xvert= (int) (event.getX()-(carteLeftAnchor+(xVert*carteTileSize)));
                yvert= (int) (event.getY()-(carteTopAnchor+(yVert*carteTileSize)));

                xvert1= (int) (event.getX()-(carteLeftAnchor+(xVert1*carteTileSize)));
                yvert1= (int) (event.getY()-(carteTopAnchor+(yVert1*carteTileSize)));

                xrouge=(int) (event.getX()-(carteLeftAnchor+(xRouge*carteTileSize)));
                yrouge=(int) (event.getY()-(carteTopAnchor+(yRouge*carteTileSize)));

                xblue=(int) (event.getX()-(carteLeftAnchor+(xBlue*carteTileSize)));
                yblue=(int) (event.getY()-(carteTopAnchor+(yBlue*carteTileSize)));


                if((carteLeftAnchor+(xVert*carteTileSize))<event.getX()&&event.getX()<(carteLeftAnchor+((xVert+2)*carteTileSize))&&((carteTopAnchor+(yVert*carteTileSize)))<event.getY()&&event.getY()<(carteTopAnchor+((yVert+2)*carteTileSize)))
                    moveVert = true;

                if((carteLeftAnchor+(xVert1*carteTileSize))<event.getX()&&event.getX()<(carteLeftAnchor+((xVert1+2)*carteTileSize))&&((carteTopAnchor+(yVert1*carteTileSize)))<event.getY()&&event.getY()<(carteTopAnchor+((yVert1+2)*carteTileSize)))
                    moveVert1 = true;

                if((carteLeftAnchor+(xRouge*carteTileSize))<event.getX()&&event.getX()<(carteLeftAnchor+((xRouge+1)*carteTileSize))&&((carteTopAnchor+(yRouge*carteTileSize)))<event.getY()&&event.getY()<(carteTopAnchor+((yRouge+3)*carteTileSize)))
                 moveRouge = true;

                if((carteLeftAnchor+(xBlue*carteTileSize))<event.getX()&&event.getX()<(carteLeftAnchor+((xBlue+1)*carteTileSize))&&((carteTopAnchor+(yBlue*carteTileSize)))<event.getY()&&event.getY()<(carteTopAnchor+((yBlue+3)*carteTileSize))||(carteLeftAnchor+((xBlue+1)*carteTileSize))<event.getX()&&event.getX()<(carteLeftAnchor+((xBlue+2)*carteTileSize))&&((carteTopAnchor+((yBlue+2)*carteTileSize)))<event.getY()&&event.getY()<(carteTopAnchor+((yBlue+3)*carteTileSize)))
                    moveBlue = true;
            break;
            case MotionEvent.ACTION_MOVE:

                if(moveVert){
                    int xTmp = xVert;
                    int yTmp = yVert;
                    if(IsOut( (int)(event.getX()/carteTileSize), (int)(event.getY()/carteTileSize))){
                        xVert=xTmp;
                        yVert=yTmp;
                    }else
                    {
                        xVert = (int) ((event.getX()) - (xvert)) / carteTileSize;
                        yVert = (int) ((event.getY()) - (yvert)) / carteTileSize;

                    }
                }

                if(moveVert1){
                    int xTmp = xVert1;
                    int yTmp = yVert1;
                    if(IsOut( (int)(event.getX()/carteTileSize), (int)(event.getY()/carteTileSize))){
                        xVert1=xTmp;
                        yVert1=yTmp;
                    }else
                    {
                        xVert1 = (int) ((event.getX()) - (xvert1)) / carteTileSize;
                        yVert1 = (int) ((event.getY()) - (yvert1)) / carteTileSize;

                    }
                }


            if(moveRouge){
                int xTmp = xRouge;
                int yTmp = yRouge;
                if(IsOut( (int)(event.getX()/carteTileSize), (int)(event.getY()/carteTileSize))){
                    xRouge=xTmp;
                    yRouge=yTmp;
                }else
                {
                    xRouge = (int) ((event.getX()) - (xrouge)) / carteTileSize;
                    yRouge = (int) ((event.getY()) - (yrouge)) / carteTileSize;
                }
            }

                if(moveBlue){
                    int xTmp = xBlue;
                    int yTmp = yBlue;
                    if(IsOut( (int)(event.getX()/carteTileSize), (int)(event.getY()/carteTileSize))){
                        xBlue=xTmp;
                        yBlue=yTmp;
                    }else
                    {
                        xBlue = (int) ((event.getX()) - (xblue)) / carteTileSize;
                        yBlue = (int) ((event.getY()) - (yblue)) / carteTileSize;
                    }
                }
            break;
            case MotionEvent.ACTION_UP:

                moveVert = false;
                moveVert1 = false;
                moveRouge = false;
                moveBlue = false;
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
