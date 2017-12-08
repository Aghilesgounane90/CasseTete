package com.example.gounane.cassetete;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by HP8440p on 08/12/2017.
 */

public class Menu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Button jouer = ((Button)this.findViewById(R.id.jouer));
        Button score = ((Button)this.findViewById(R.id.score));
        Button propos = ((Button)this.findViewById(R.id.apropos));
        jouer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Le premier paramètre est le nom de l'activité actuelle
                // Le second est le nom de l'activité de destination
                Intent secondeActivite = new Intent(Menu.this, CasseTete.class);
                // Puis on lance l'intent !
                startActivity(secondeActivite);
            }
        });
        propos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Le second est le nom de l'activité de destination
                Intent secondeActivite = new Intent(Menu.this, Apropos.class);
                // Puis on lance l'intent !
                startActivity(secondeActivite);
            }
        });

        }
    }


