package com.developer.johhns.recepcioncamiones;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Http_Util {

    private static Http_Util instancia ;
    private RequestQueue mPeticion ;

    public static final String URL_LLEGADA = "https://appserver.iea.com.sv/wsag/sag_hh_util.Registrar_Llegada_Propiedad" ;


    private Http_Util( Context contexto ){
        this.mPeticion = Volley.newRequestQueue( contexto.getApplicationContext() ) ;
    }

    public static synchronized Http_Util get( Context contexto ){
        if ( instancia == null ) {
            instancia = new Http_Util(contexto);
        }
        return instancia ;
    }

    public RequestQueue obtenerPeticion() {
        return mPeticion ;
    }

}
