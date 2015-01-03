package com.wingnity.pokedex;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Tino on 02/01/15.
 * Classe pour utiliser IntentService
 */
public class PkmnIntentService extends IntentService{

    // Variables utilisés pour envoyer les données Intent

    public static final String ACTION_PkmnService = "com.wingnity.pokedex.RESPONSE";
    public static final String ACTION_PkmnUpdate = "com.wingnity.pokedex.UPDATE";
    public static final String EXTRA_KEY_IN = "EXTRA_IN";
    public static final String EXTRA_KEY_OUT = "EXTRA_OUT";
    public static final String EXTRA_KEY_UPDATE = "EXTRA_UPDATE";
    String msgFromActivity;
    String extraOut;

    public PkmnIntentService() {
        super("com.wingnity.pokedex.PkmnIntentService");
    }// PkmnIntentService()

    @Override
    protected void onHandleIntent(Intent intent) {

        msgFromActivity = intent.getStringExtra(EXTRA_KEY_IN);
        extraOut = "Test IntentService: " +  msgFromActivity;

        // Test de fonctionnalité de l'IntentService
        for(int i = 0; i <=10; i++){
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //Instanciation d'un Intent pour envoyer l'update
            Intent intentUpdate = new Intent();
            intentUpdate.setAction(ACTION_PkmnUpdate);
            intentUpdate.addCategory(Intent.CATEGORY_DEFAULT);
            intentUpdate.putExtra(EXTRA_KEY_UPDATE, i);
            sendBroadcast(intentUpdate);
        }//for

        //Instanciation d'un Intent pour retourner le résultat
        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_PkmnService);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra(EXTRA_KEY_OUT, extraOut);
        sendBroadcast(intentResponse);
    }//onHandleIntent()

}// class PkmnIntentService
