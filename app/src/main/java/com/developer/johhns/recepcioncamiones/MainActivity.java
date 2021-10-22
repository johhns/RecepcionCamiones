package com.developer.johhns.recepcioncamiones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.client.android.BuildConfig;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EnviosPlacas enviosPlacas ;
    Button       btnEnviar , btnEscanear  ;
    TextView     numero_placa , latitud, longitud ;
    private FusedLocationProviderClient gps ;
    protected Location ubicacion ;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enviosPlacas = new EnviosPlacas( this ) ;
        numero_placa = findViewById( R.id.txtPlaca ) ;
        latitud      = findViewById( R.id.txtLatitud ) ;
        longitud     = findViewById( R.id.txtLongitud ) ;
        gps          = LocationServices.getFusedLocationProviderClient(this) ;

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
                enviosPlacas.enviar( numero_placa.getText().toString() ,
                                     latitud.getText().toString(),
                                     longitud.getText().toString() ,
                                     view
                                 );
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
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

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        if (shouldProvideRationale) {
            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            startLocationPermissionRequest();
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById( R.id.contenido_ppl ),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", BuildConfig.LIBRARY_PACKAGE_NAME, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        gps.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            ubicacion = task.getResult();

                            latitud.setText( String.valueOf( ubicacion.getLatitude() ) );
                            longitud.setText( String.valueOf( ubicacion.getLongitude() ) );
                        } else {
                            showSnackbar(R.string.no_location_detected , R.string.settings, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", BuildConfig.LIBRARY_PACKAGE_NAME, null);
                                    intent.setData(uri);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
    }


}