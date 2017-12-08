package com.example.gounane.cassetete;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class CasseTete extends AppCompatActivity {

    private CasseTeteView mCasseTeteView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeux);
        // recuperation de la vue une voie cree à partir de son id
        mCasseTeteView = (CasseTeteView)findViewById(R.id.CasseTeteView);
        // rend visible la vue
        mCasseTeteView.setVisibility(View.VISIBLE);
    }
}
