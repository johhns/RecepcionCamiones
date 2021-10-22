package com.developer.johhns.recepcioncamiones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {

    EnviosPlacas enviosPlacas ;
    Button       btnEnviar , btnEscanear  ;
    TextView     numero_placa ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enviosPlacas = new EnviosPlacas( this ) ;

        numero_placa = findViewById( R.id.txtPlaca ) ;

        if (savedInstanceState != null ) {
            numero_placa.setText( savedInstanceState.getString("PLACA") );
        }

        btnEscanear = findViewById( R.id.btnEscanear ) ;
        btnEscanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intengrador = new IntentIntegrator( MainActivity.this ) ;
                intengrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES) ;
                intengrador.setPrompt("Lectura de Codigos de Barra");
                intengrador.setCameraId(0) ;
                intengrador.setOrientationLocked(false);
                intengrador.setBeepEnabled(true) ;
                intengrador.setBarcodeImageEnabled(true);
                intengrador.initiateScan();
            }
        });

        btnEnviar    = findViewById( R.id.btnEnviar ) ;
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviosPlacas.enviar( numero_placa.getText().toString() , "11", "50" , view );
            }
        });

    }

    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        IntentResult resultado = IntentIntegrator.parseActivityResult( requestCode , resultCode , data ) ;

        if ( resultado != null ) {
            if ( resultado.getContents() == null ) {
                Toast.makeText(this,"Lectura cancelada",Toast.LENGTH_LONG).show();
            } else {
                numero_placa.setText( resultado.getContents() );
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("PLACA", numero_placa.getText().toString() );
        super.onSaveInstanceState(outState);
    }


}