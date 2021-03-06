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
import android.widget.Switch;

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
    private Bitmap bluef;
    private Bitmap rose;
    private Bitmap violet;
    private Bitmap jaune;
    private Bitmap win;
    private Bitmap niveau2;
    private Bitmap niveau3;

    // constante modelisant les differentes types de cases
    static final int CST_vide = 0;
    static final int CST_plein = 1;
    static final int CST_blue = 2;
    static final int CST_rose = 3;
    static final int CST_violet = 4;
    static final int CST_jaune = 5;
    static final int CST_zone = 6;
    static final int CST_rouge = 7;
    static final int CST_vert = 8;
    static final int CST_vert1 = 9;
    static final int CST_bluef = 10;

    // Declaration des objets Ressources et Context permettant d'accéder aux ressources de notre application et de les charger
    private Resources mRes;
    private Context mContext;

    // taille de la carte du jeu
    static final int carteWidth = 10;
    static final int carteHeight = 18;
    static final int carteTileSize = 60;

    int carteWidthGain;
    int carteHeightGain;


    int niveau= 0;

    int[][] carte;
    int[][] carteGain;
    int[][] cartePosition = new int[19][18];

    // ancres pour pouvoir centrer la carte du jeu
    int carteTopAnchor;                   // coordonnées en Y du point d'ancrage de notre carte
    int carteLeftAnchor;                  // coordonnées en X du point d'ancrage de notre carte

    // ancres pour pouvoir centrer la carte du gain du jeu
    int carteTopGain; // coordonnées en Y du point d'ancrage de notre carte gain
    int carteLeftGain;// coordonnées en X du point d'ancrage de notre carte gain

    // position courante des diamants



    Random N1 = new Random();
    Random N2 = new Random();

        int xVert = 1 + N1.nextInt(carteWidth - 3);
        int yVert = 1 + N1.nextInt(carteHeight - 3);
        int xVert1 = 1 + N1.nextInt(carteWidth - 3);
        int yVert1 = 1 + N1.nextInt(carteHeight - 3);
        int xRouge = 1 + N1.nextInt(carteWidth - 3);
        int yRouge = 1 + N1.nextInt(carteHeight - 3);
        int xBlue = 1 + N1.nextInt(carteWidth - 3);
        int yBlue = 1 + N1.nextInt(carteHeight - 3);
        int xBluef = 1 + N1.nextInt(carteWidth - 3);
        int yBluef = 1 + N1.nextInt(carteHeight - 3);
        int xRose = 1 + N1.nextInt(carteWidth - 3);
        int yRose = 1 + N1.nextInt(carteHeight - 3);
        int xViolet = 1 + N1.nextInt(carteWidth - 3);
        int yViolet = 1 + N1.nextInt(carteHeight - 3);



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
        bluef = BitmapFactory.decodeResource(mRes, R.drawable.bluef);
        rose = BitmapFactory.decodeResource(mRes, R.drawable.rose);
        violet = BitmapFactory.decodeResource(mRes, R.drawable.violet);
        win = BitmapFactory.decodeResource(mRes, R.drawable.win);
        niveau2 = BitmapFactory.decodeResource(mRes, R.drawable.niveau2);
        niveau3 = BitmapFactory.decodeResource(mRes, R.drawable.niveau3);

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



        if ((cv_thread != null) && (!cv_thread.isAlive())) {
            cv_thread.start();
            Log.e("-FCT-", "cv_thread.start()");
        }
    }
    public void initparametersNiveau(){
        carte = new int[carteHeight][carteWidth];
        loadlevel();
        carteTopAnchor = (getHeight() - carteHeight * carteTileSize) / 2;
        carteLeftAnchor = (getWidth() - carteWidth * carteTileSize) / 2;
    }

    // tableau de reference du terrain
    int[][] refNiveau0 = {
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
    int[][] refNiveau1 = {
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_zone, CST_zone, CST_zone, CST_zone, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_zone, CST_zone, CST_zone, CST_zone, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_zone, CST_zone, CST_zone, CST_zone, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_zone, CST_zone, CST_zone, CST_zone, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_zone, CST_zone, CST_zone, CST_zone, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_zone, CST_zone, CST_zone, CST_zone, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
            {CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide, CST_vide},
    };
    int[][] refTestsCase = new int[carteHeight][carteWidth] ;
    // chargement du niveau a partir du tableau de reference du niveau
    private void loadlevel() {



        switch (niveau) {
            case 0:
                for (int i = 0; i < carteHeight; i++) {
                    for (int j = 0; j < carteWidth; j++) {
                        if(((i==yVert)&&(j==xVert ))||((i==yVert)&&(j==xVert+1 ))||((i==yVert+1)&&(j==xVert ))||((i==yVert+1)&&(j==xVert+1 ))){
                            refTestsCase[i][j]=CST_vert;
                        }
                        else if(((i==yVert1)&&(j==xVert1 ))||((i==yVert1)&&(j==xVert1+1 ))||((i==yVert1+1)&&(j==xVert1 ))||((i==yVert1+1)&&(j==xVert1+1 ))){
                            refTestsCase[i][j]=CST_vert1;
                        }
                        else if(((i==yBlue)&&(j==xBlue ))||((i==yBlue+1)&&(j==xBlue ))||((i==yBlue+2)&&(j==xBlue ))||((i==yBlue+2)&&(j==xBlue+1 ))){
                            refTestsCase[i][j]=CST_blue;
                        }
                        else if(((i==yRouge)&&(j==xRouge ))||((i==yRouge+1)&&(j==xRouge ))||((i==yRouge+2)&&(j==xRouge ))){
                            refTestsCase[i][j]=CST_rouge;
                        }
                        else{
                            refTestsCase[i][j]=CST_vide;
                        }
                    }
                }
            for (int i = 0; i < carteHeight; i++) {
                for (int j = 0; j < carteWidth; j++) {
                    carte[i][j] = refNiveau0[i][j];
                }
            }

                // taille de la carte du gain
                carteWidthGain = 3;
                carteHeightGain = 5;
                carteTopGain = (getHeight() - carteHeightGain * carteTileSize) / 2;
                carteLeftGain = (getWidth() - carteWidthGain * carteTileSize) / 2;
            break;
            case 1:

                for (int i = 0; i < carteHeight; i++) {
                    for (int j = 0; j < carteWidth; j++) {
                        if(((i==yVert)&&(j==xVert ))||((i==yVert)&&(j==xVert+1 ))||((i==yVert+1)&&(j==xVert ))||((i==yVert+1)&&(j==xVert+1 ))){
                            refTestsCase[i][j]=CST_vert;
                        } else if(((i==yVert1)&&(j==xVert1 ))||((i==yVert1)&&(j==xVert1+1 ))||((i==yVert1+1)&&(j==xVert1 ))||((i==yVert1+1)&&(j==xVert1+1 ))){
                            refTestsCase[i][j]=CST_vert1;
                        }
                        else if(((i==yBlue)&&(j==xBlue ))||((i==yBlue+1)&&(j==xBlue ))||((i==yBlue+2)&&(j==xBlue ))||((i==yBlue+2)&&(j==xBlue+1 ))){
                            refTestsCase[i][j]=CST_blue;
                        }
                        else if(((i==yBluef)&&(j==xBluef ))){
                            refTestsCase[i][j]=CST_bluef;
                        }
                        else if(((i==yRouge)&&(j==xRouge ))||((i==yRouge+1)&&(j==xRouge ))||((i==yRouge+2)&&(j==xRouge ))){
                            refTestsCase[i][j]=CST_rouge;
                        }
                        else if(((i==yViolet)&&(j==xViolet+1 ))||((i==yViolet+1)&&(j==xViolet+1 ))||((i==yViolet+2)&&(j==xViolet+1 ))||((i==yViolet+2)&&(j==xViolet ))){
                            refTestsCase[i][j]=CST_violet;
                        }
                        else if(((i==yRose)&&(j==xRose+1 ))||((i==yRose+1)&&(j==xRose+1 ))||((i==yRose+1)&&(j==xRose ))||((i==yRose+2)&&(j==xRose+1 ))){
                            refTestsCase[i][j]=CST_rose;
                        }
                        else{
                            refTestsCase[i][j]=CST_vide;
                        }
                    }
                }
                for (int i = 0; i < carteHeight; i++) {
                    for (int j = 0; j < carteWidth; j++) {
                        carte[i][j] = refNiveau1[i][j];
                    }
                }
                // taille de la carte du gain
                carteWidthGain = 4;
                carteHeightGain = 6;
                carteTopGain = (getHeight() - carteHeightGain * carteTileSize) / 2;
                carteLeftGain = (getWidth() - carteWidthGain * carteTileSize) / 2;

                break;
        }
    }

    Paint p = new Paint();

    // dessin du gagne si gagne
    private void paintWin(Canvas canvas) {
        canvas.drawBitmap(win, (int)(carteLeftAnchor+ 3.5*carteTileSize), carteTopAnchor+ 4*carteTileSize, null);
    }
    //dessin de la carte du jeu
    private void paintcarte(Canvas canvas) {

        p.setColor(Color.BLUE);

        p.setStyle(Paint.Style.FILL);
        //p.setAlpha(10);

        canvas.drawRect((float)(carteLeftGain+(0.5*carteTileSize)), (float)(carteTopGain+(0.5*carteTileSize)), (float)(carteLeftGain + ((carteWidthGain+(0.5)) * carteTileSize)), (float)(carteTopGain + ((carteHeightGain+(0.5)) * carteTileSize)), p);
    }

    //dessin de la carte du jeu
    private void paintcarteNiveau1(Canvas canvas) {

        p.setColor(Color.BLUE);

        p.setStyle(Paint.Style.FILL);
        //p.setAlpha(10);

        canvas.drawRect((float)(carteLeftGain), (float)(carteTopGain), (float)(carteLeftGain + ((carteWidthGain) * carteTileSize)), (float)(carteTopGain + ((carteHeightGain) * carteTileSize)), p);
    }

    private void paintGagneNiveau2(Canvas canvas){
        canvas.drawBitmap(niveau3, carteLeftAnchor+ 3*carteTileSize, carteTopAnchor+ 6*carteTileSize, null);
    }
    private void paintGagneNiveau1(Canvas canvas){
        canvas.drawBitmap(niveau2, carteLeftAnchor+ 3*carteTileSize, carteTopAnchor+ 6*carteTileSize, null);
    }
    // dessin du brique violet
    private void paintViolet(Canvas canvas) {
        canvas.drawBitmap(violet, carteLeftAnchor + ((xViolet + 1) * carteTileSize), carteTopAnchor + (yViolet * carteTileSize), null);
        canvas.drawBitmap(violet, carteLeftAnchor + ((xViolet + 1) * carteTileSize), carteTopAnchor + ((yViolet + 1) * carteTileSize), null);
        canvas.drawBitmap(violet, carteLeftAnchor + ((xViolet + 1) * carteTileSize), carteTopAnchor + ((yViolet + 2) * carteTileSize), null);
        canvas.drawBitmap(violet, carteLeftAnchor + ((xViolet) * carteTileSize), carteTopAnchor + ((yViolet + 2) * carteTileSize), null);
    }
    private void paintRose(Canvas canvas) {
        canvas.drawBitmap(rose, carteLeftAnchor + ((xRose + 1) * carteTileSize), carteTopAnchor + (yRose * carteTileSize), null);
        canvas.drawBitmap(rose, carteLeftAnchor + ((xRose + 1) * carteTileSize), carteTopAnchor + ((yRose + 1) * carteTileSize), null);
        canvas.drawBitmap(rose, carteLeftAnchor + ((xRose ) * carteTileSize), carteTopAnchor + ((yRose + 1) * carteTileSize), null);
        canvas.drawBitmap(rose, carteLeftAnchor + ((xRose + 1) * carteTileSize), carteTopAnchor + ((yRose + 2) * carteTileSize), null);
    }
    // dessin du brique vert violet
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
    // dessin du brique blue
    private void paintBlue(Canvas canvas) {
        canvas.drawBitmap(blue, carteLeftAnchor + (xBlue * carteTileSize), carteTopAnchor + (yBlue * carteTileSize), null);
        canvas.drawBitmap(blue, carteLeftAnchor + (xBlue * carteTileSize), carteTopAnchor + ((yBlue + 1) * carteTileSize), null);
        canvas.drawBitmap(blue, carteLeftAnchor + (xBlue * carteTileSize), carteTopAnchor + ((yBlue + 2) * carteTileSize), null);
        canvas.drawBitmap(blue, carteLeftAnchor + ((xBlue+1) * carteTileSize), carteTopAnchor + ((yBlue + 2) * carteTileSize), null);
    }
    // dessin du brique bluef
    private void paintBluef(Canvas canvas) {
        canvas.drawBitmap(bluef, carteLeftAnchor + (xBluef * carteTileSize), carteTopAnchor + (yBluef * carteTileSize), null);
    }

    //controle de la valeur d'une cellule
    private boolean IsCell(int x, int y, int mask) {
        if (carte[y][x] == mask) {
            return true;
        }
        return false;
    }
    // permet d'identifier si la partie est gagnee niveau1
    private boolean isWonNiveau1() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
             if (!IsCell(xVert+i, yVert+j, CST_zone)) {
                 return false;
                }
            }
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
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

    // permet d'identifier si la partie est gagnee niveau1
    private boolean isWonNiveau2() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (!IsCell(xVert+i, yVert+j, CST_zone)) {
                    return false;
                }
            }
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
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
            if (!IsCell(xRose+1, yRose + i, CST_zone)) {
                return false;
            }
        }
        if (!IsCell(xRose, yRose+1, CST_zone)) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            if (!IsCell(xBlue, yBlue + i, CST_zone)) {
                return false;
            }
        }
        if (!IsCell(xBlue+1, yBlue+2, CST_zone)) {
            return false;
        }
        if (!IsCell(xBluef, yBluef, CST_zone)) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            if (!IsCell(xViolet+1, yViolet + i, CST_zone)) {
                return false;
            }
        }
        if (!IsCell(xViolet, yViolet+2, CST_zone)) {
            return false;
        }
        return true;
    }
    // dessin du jeu (fond uni, en fonction du jeu gagne ou pas dessin du plateau et du joueur des diamants et des fleches)
    private void nDraw(Canvas canvas) {
        initparametersNiveau();
        switch (niveau) {
            case 0:
                canvas.drawRGB(44, 44, 44);
                if (isWonNiveau1()) {
                    paintWin(canvas);
                    paintGagneNiveau1(canvas);


                } else {
                    paintcarte(canvas);
                    paintVert(canvas);
                    paintVert1(canvas);
                    paintRouge(canvas);
                    paintBlue(canvas);
                }
                break;
            case 1:
                canvas.drawRGB(44, 44, 44);
                if (isWonNiveau2()) {
                    paintWin(canvas);
                    paintGagneNiveau2(canvas);


                } else {
                    paintcarteNiveau1(canvas);
                    paintVert(canvas);
                    paintVert1(canvas);
                    paintBlue(canvas);
                    paintRouge(canvas);
                    paintViolet(canvas);
                    paintBluef(canvas);
                    paintRose(canvas);
                }

                break;
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
    int xbluef=0;
    int ybluef=0;
    int xviolet=0;
    int yviolet=0;
    int xrose=0;
    int yrose=0;

    boolean moveVert = false;
    boolean moveVert1 = false;
    boolean moveRouge = false;
    boolean moveBlue = false;
    boolean moveViolet = false;
    boolean moveBluef = false;
    boolean moveRose = false;
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

                xbluef=(int) (event.getX()-(carteLeftAnchor+(xBluef*carteTileSize)));
                ybluef=(int) (event.getY()-(carteTopAnchor+(yBluef*carteTileSize)));

                xrose=(int) (event.getX()-(carteLeftAnchor+(xRose*carteTileSize)));
                yrose=(int) (event.getY()-(carteTopAnchor+(yRose*carteTileSize)));

                xviolet=(int) (event.getX()-(carteLeftAnchor+(xViolet*carteTileSize)));
                yviolet=(int) (event.getY()-(carteTopAnchor+(yViolet*carteTileSize)));

                if((carteLeftAnchor+(xVert*carteTileSize))<event.getX()&&event.getX()<(carteLeftAnchor+((xVert+2)*carteTileSize))&&((carteTopAnchor+(yVert*carteTileSize)))<event.getY()&&event.getY()<(carteTopAnchor+((yVert+2)*carteTileSize)))
                    moveVert = true;

                if((carteLeftAnchor+(xVert1*carteTileSize))<event.getX()&&event.getX()<(carteLeftAnchor+((xVert1+2)*carteTileSize))&&((carteTopAnchor+(yVert1*carteTileSize)))<event.getY()&&event.getY()<(carteTopAnchor+((yVert1+2)*carteTileSize)))
                    moveVert1 = true;

                if((carteLeftAnchor+(xRouge*carteTileSize))<event.getX()&&event.getX()<(carteLeftAnchor+((xRouge+1)*carteTileSize))&&((carteTopAnchor+(yRouge*carteTileSize)))<event.getY()&&event.getY()<(carteTopAnchor+((yRouge+3)*carteTileSize)))
                 moveRouge = true;

                if((carteLeftAnchor+(xBlue*carteTileSize))<event.getX()&&event.getX()<(carteLeftAnchor+((xBlue+1)*carteTileSize))&&((carteTopAnchor+(yBlue*carteTileSize)))<event.getY()&&event.getY()<(carteTopAnchor+((yBlue+3)*carteTileSize))||(carteLeftAnchor+((xBlue+1)*carteTileSize))<event.getX()&&event.getX()<(carteLeftAnchor+((xBlue+2)*carteTileSize))&&((carteTopAnchor+((yBlue+2)*carteTileSize)))<event.getY()&&event.getY()<(carteTopAnchor+((yBlue+3)*carteTileSize)))
                    moveBlue = true;

                if((carteLeftAnchor+((xViolet+1)*carteTileSize))<event.getX()&&event.getX()<(carteLeftAnchor+((xViolet+2)*carteTileSize))&&((carteTopAnchor+(yViolet*carteTileSize)))<event.getY()&&event.getY()<(carteTopAnchor+((yViolet+3)*carteTileSize))||(carteLeftAnchor+((xViolet)*carteTileSize))<event.getX()&&event.getX()<(carteLeftAnchor+((xViolet+2)*carteTileSize))&&((carteTopAnchor+((yViolet+2)*carteTileSize)))<event.getY()&&event.getY()<(carteTopAnchor+((yViolet+3)*carteTileSize)))
                    moveViolet = true;

                if((carteLeftAnchor+((xRose+1)*carteTileSize))<event.getX()&&event.getX()<(carteLeftAnchor+((xRose+2)*carteTileSize))&&((carteTopAnchor+(yRose*carteTileSize)))<event.getY()&&event.getY()<(carteTopAnchor+((yRose+4)*carteTileSize))||(carteLeftAnchor+((xRose)*carteTileSize))<event.getX()&&event.getX()<(carteLeftAnchor+((xRose+1)*carteTileSize))&&((carteTopAnchor+((yRose+1)*carteTileSize)))<event.getY()&&event.getY()<(carteTopAnchor+((yRose+2)*carteTileSize)))
                    moveRose = true;

                if((carteLeftAnchor+((xBluef)*carteTileSize))<event.getX()&&event.getX()<(carteLeftAnchor+((xBluef+1)*carteTileSize))&&((carteTopAnchor+(yBluef*carteTileSize)))<event.getY()&&event.getY()<(carteTopAnchor+((yBluef+1)*carteTileSize)))
                    moveBluef = true;

                if (isWonNiveau1()&&niveau==0){
                    if((carteLeftAnchor+(3*carteTileSize))<event.getX()&&event.getX()<(carteLeftAnchor+((8)*carteTileSize))&&((carteTopAnchor+(6*carteTileSize)))<event.getY()&&event.getY()<(carteTopAnchor+((12)*carteTileSize))){
                        niveau=1;
                        xVert = 1 + N1.nextInt(carteWidth - 3);
                        yVert = 1 + N1.nextInt(carteHeight - 3);
                        xVert1 = 1 + N1.nextInt(carteWidth - 3);
                        yVert1 = 1 + N1.nextInt(carteHeight - 3);
                        xRouge = 1 + N1.nextInt(carteWidth - 3);
                        yRouge = 1 + N1.nextInt(carteHeight - 3);
                        xBlue = 1 + N1.nextInt(carteWidth - 3);
                        yBlue = 1 + N1.nextInt(carteHeight - 3);
                        yBluef = 1 + N1.nextInt(carteHeight - 3);
                        xBluef = 1 + N1.nextInt(carteWidth - 3);
                        xRose = 1 + N1.nextInt(carteWidth - 3);
                        yRose = 1 + N1.nextInt(carteHeight - 3);
                        xViolet = 1 + N1.nextInt(carteWidth - 3);
                        yViolet = 1 + N1.nextInt(carteWidth - 3);
                    }
                }
            break;
            case MotionEvent.ACTION_MOVE:

                if(moveVert){
                    int xTmp = xVert;
                    int yTmp = yVert;
                    if(IsOut( (int)(event.getX()/carteTileSize), (int)(event.getY()/carteTileSize))){
                        xVert=xTmp;
                        yVert=yTmp;
                    }else if (IsFull((int)((event.getX()-xvert)/carteTileSize), (int)((event.getY()-yvert)/carteTileSize),CST_vert)||IsFull((int)((event.getX()-xvert)/carteTileSize), (int)(((event.getY()-yvert)/carteTileSize))+1,CST_vert)||IsFull((int)(((event.getX()-xvert)/carteTileSize)+1), (int)((event.getY()-yvert)/carteTileSize),CST_vert)||IsFull((int)(((event.getX()-xvert)/carteTileSize)+1), (int)(((event.getY()-yvert)/carteTileSize))+1,CST_vert))
                    {
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
                    }else if (IsFull((int)((event.getX()-xvert1)/carteTileSize), (int)((event.getY()-yvert1)/carteTileSize),CST_vert1)||IsFull((int)((event.getX()-xvert1)/carteTileSize), (int)(((event.getY()-yvert1)/carteTileSize))+1,CST_vert1)||IsFull((int)(((event.getX()-xvert1)/carteTileSize)+1), (int)((event.getY()-yvert1)/carteTileSize),CST_vert1)||IsFull((int)(((event.getX()-xvert1)/carteTileSize)+1), (int)(((event.getY()-yvert1)/carteTileSize))+1,CST_vert1))
                    {
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
                if(IsOut((int)(event.getX()/carteTileSize), (int)(event.getY()/carteTileSize))){
                    xRouge=xTmp;
                    yRouge=yTmp;
                }else if (IsFull((int)((event.getX()-xrouge)/carteTileSize), (int)((event.getY()-yrouge)/carteTileSize),CST_rouge)||IsFull((int)((event.getX()-xrouge)/carteTileSize), (int)(((event.getY()-yrouge)/carteTileSize))+1,CST_rouge)||IsFull((int)((event.getX()-xrouge)/carteTileSize), (int)(((event.getY()-yrouge)/carteTileSize))+2,CST_rouge))
                {
                    xRouge=xTmp;
                    yRouge=yTmp;
                }
                else{
                    xRouge = (int) ((event.getX()) - (xrouge)) / carteTileSize;
                    yRouge = (int) ((event.getY()) - (yrouge)) / carteTileSize;
                }
            }

                if(moveBlue){
                    int xTmp = xBlue;
                    int yTmp = yBlue;
                    if(IsOut( (int)(event.getX()/carteTileSize), (int)(event.getY()/carteTileSize))) {
                        xBlue = xTmp;
                        yBlue = yTmp;
                    }else if (IsFull((int)((event.getX()-xblue)/carteTileSize), (int)((event.getY()-yblue)/carteTileSize),CST_blue)||IsFull((int)((event.getX()-xblue)/carteTileSize), (int)(((event.getY()-yblue)/carteTileSize))+1,CST_blue)||IsFull((int)((event.getX()-xblue)/carteTileSize), (int)(((event.getY()-yblue)/carteTileSize))+2,CST_blue)||IsFull((int)(((event.getX()-xblue)/carteTileSize)+1), (int)(((event.getY()-yblue)/carteTileSize))+2,CST_blue))
                    {
                        xBlue = xTmp;
                        yBlue = yTmp;
                    }else
                    {
                        xBlue = (int) ((event.getX()) - (xblue)) / carteTileSize;
                        yBlue = (int) ((event.getY()) - (yblue)) / carteTileSize;
                    }
                }

                if(moveViolet){
                    int xTmp = xViolet;
                    int yTmp = yViolet;
                    if(IsOut( (int)(event.getX()/carteTileSize), (int)(event.getY()/carteTileSize))) {
                        xViolet = xTmp;
                        yViolet = yTmp;
                    }else if (IsFull((int)((event.getX()-xviolet)/carteTileSize)+1, (int)((event.getY()-yviolet)/carteTileSize),CST_violet)||IsFull((int)((event.getX()-xviolet)/carteTileSize)+1, (int)(((event.getY()-yviolet)/carteTileSize))+1,CST_violet)||IsFull((int)((event.getX()-xviolet)/carteTileSize)+1, (int)(((event.getY()-yviolet)/carteTileSize))+2,CST_violet)||IsFull((int)(((event.getX()-xviolet)/carteTileSize)), (int)(((event.getY()-yviolet)/carteTileSize))+2,CST_violet))
                    {
                        xViolet = xTmp;
                        yViolet = yTmp;
                    }else
                    {
                        xViolet = (int) ((event.getX()) - (xviolet)) / carteTileSize;
                        yViolet = (int) ((event.getY()) - (yviolet)) / carteTileSize;
                    }
                }

                if(moveRose){
                    int xTmp = xRose;
                    int yTmp = yRose;
                    if(IsOut( (int)(event.getX()/carteTileSize), (int)(event.getY()/carteTileSize))) {
                        xRose = xTmp;
                        yRose = yTmp;
                    }else if (IsFull((int)((event.getX()-xrose)/carteTileSize)+1, (int)((event.getY()-yrose)/carteTileSize),CST_rose)||IsFull((int)((event.getX()-xrose)/carteTileSize)+1, (int)(((event.getY()-yrose)/carteTileSize))+1,CST_rose)||IsFull((int)((event.getX()-xrose)/carteTileSize), (int)(((event.getY()-yrose)/carteTileSize))+1,CST_rose)||IsFull((int)(((event.getX()-xrose)/carteTileSize))+1, (int)(((event.getY()-yrose)/carteTileSize))+2,CST_rose))
                    {
                        xRose = xTmp;
                        yRose = yTmp;
                    }else
                    {
                        xRose = (int) ((event.getX()) - (xrose)) / carteTileSize;
                        yRose = (int) ((event.getY()) - (yrose)) / carteTileSize;
                    }
                }

                if(moveBluef){
                    int xTmp = xBluef;
                    int yTmp = yBluef;
                    if(IsOut( (int)(event.getX()/carteTileSize), (int)(event.getY()/carteTileSize))) {
                        xBluef = xTmp;
                        yBluef = yTmp;
                    }else if (IsFull((int)((event.getX()-xbluef)/carteTileSize), (int)((event.getY()-ybluef)/carteTileSize),CST_bluef))
                    {
                        xBluef = xTmp;
                        yBluef = yTmp;
                    }else
                    {
                        xBluef = (int) ((event.getX()) - (xbluef)) / carteTileSize;
                        yBluef = (int) ((event.getY()) - (ybluef)) / carteTileSize;
                    }
                }

                if(moveViolet){
                    int xTmp = xViolet;
                    int yTmp = yViolet;
                    if(IsOut( (int)(event.getX()/carteTileSize), (int)(event.getY()/carteTileSize))) {
                        xViolet = xTmp;
                        yViolet = yTmp;
                    }else if (IsFull((int)((event.getX()-xviolet)/carteTileSize)+1, (int)((event.getY()-yviolet)/carteTileSize),CST_violet)||IsFull((int)((event.getX()-xviolet)/carteTileSize)+1, (int)(((event.getY()-yviolet)/carteTileSize))+1,CST_violet)||IsFull((int)((event.getX()-xviolet)/carteTileSize)+1, (int)(((event.getY()-yviolet)/carteTileSize))+2,CST_violet)||IsFull((int)(((event.getX()-xviolet)/carteTileSize)), (int)(((event.getY()-yviolet)/carteTileSize))+2,CST_violet))
                    {
                        xViolet = xTmp;
                        yViolet = yTmp;
                    }else
                    {
                        xViolet = (int) ((event.getX()) - (xviolet)) / carteTileSize;
                        yViolet = (int) ((event.getY()) - (yviolet)) / carteTileSize;
                    }
                }
            break;
            case MotionEvent.ACTION_UP:
                moveVert = false;
                moveVert1 = false;
                moveRouge = false;
                moveBlue = false;
                moveBluef = false;
                moveViolet = false;
                moveRose = false;
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
    private boolean IsFull(int x,int y,int k){
        if(refTestsCase[y][x]==CST_vide||refTestsCase[y][x]==k){
            return false;
        }
        return true;
    }
}
