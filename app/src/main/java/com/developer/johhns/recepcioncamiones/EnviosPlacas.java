package com.developer.johhns.recepcioncamiones;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

public class EnviosPlacas {

    private RequestQueue colaPeticiones ;
    private Activity     actividad ;

    public EnviosPlacas(Activity actividad) {
        this.actividad       = actividad;
        this.colaPeticiones = Http_Util.get(actividad.getBaseContext()).obtenerPeticion() ;
    }

    public void enviar( String  placa, String latitud, String  longitud , View vista){

        StringRequest peticion = new StringRequest(Request.Method.GET, Http_Util.URL_LLEGADA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       if ( response.indexOf("REGISTRADA") != -1 ) {
                           Snackbar.make( vista , "Placa registrada" , Snackbar.LENGTH_LONG ).show();
                       } else  {
                           Snackbar.make( vista , "No se pudo enviar la placa" , Snackbar.LENGTH_LONG ).show();
                       }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make( vista , "No fue posible comunicarse con el ingenio", Snackbar.LENGTH_LONG ).show();
                    }
                }
        ) {
            @Override
            public Map<String,String> getParams(){
                Map<String,String> parametros = new HashMap<String,String>();
                parametros.put( "pPlaca" , placa ) ;
                parametros.put( "pLat"   , latitud ) ;
                parametros.put( "pLon"   , longitud ) ;
                return parametros ;
            }
            @Override
            public int getMethod(){
                return Method.POST ;
            }
        };
       colaPeticiones.add( peticion ) ;
    }

}
