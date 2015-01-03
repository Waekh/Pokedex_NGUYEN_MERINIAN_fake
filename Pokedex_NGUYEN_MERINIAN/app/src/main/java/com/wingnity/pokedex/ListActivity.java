package com.wingnity.pokedex;

import java.io.IOException;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.tino.pokedex.R;


public class ListActivity extends Activity {

    ArrayList<Pokemons> pokemonsList;
    PkmnListAdapter adapter;
    TextView textResult;

    // Gestion de l'ActionBar
    private ActionBar actionBar;

    // Item "Rafraîchir"
    private MenuItem refreshMenuItem;

    ProgressBar progressBar;

    private PkmnReceiver receiver;
    private PkmnReceiverUpdate receiverUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupération de l'ActionBar
        actionBar = getActionBar();

        // Liste de Pokémon
        pokemonsList = new ArrayList<Pokemons>();

        // Adresse du serveur
        new loadPkmn().execute("http://tino.ovh/pokedexTuto");

        // Récupération de la ListView
        ListView listview = (ListView) findViewById(R.id.list);
        adapter = new PkmnListAdapter(getApplicationContext(), R.layout.listpkmn, pokemonsList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), pokemonsList.get(position).getNameFr(), Toast.LENGTH_SHORT).show();
            }
        });
        listview.setTextFilterEnabled(true);


        // ProgressBar pour le design pattern IntentService/BroadcastReceiver
        textResult = (TextView)findViewById(R.id.idResult);
        textResult.setText("Pré-chargement (patientez...)");
        progressBar = (ProgressBar)findViewById(R.id.idProgressBar);

        String msgToIntentService = "Terminé!";

        // Démarrage du service
        Intent intentMyIntentService = new Intent(this, PkmnIntentService.class);
        intentMyIntentService.putExtra(PkmnIntentService.EXTRA_KEY_IN, msgToIntentService);
        startService(intentMyIntentService);

        // Gestion du BroadcastReceiver
        receiver = new PkmnReceiver();
        receiverUpdate = new PkmnReceiverUpdate();

        // Enregistrement du BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(PkmnIntentService.ACTION_PkmnService);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, intentFilter);

        IntentFilter intentFilter_update = new IntentFilter(PkmnIntentService.ACTION_PkmnUpdate);
        intentFilter_update.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiverUpdate, intentFilter_update);


        /*
        //============================= GESTION DU BROADCASTRECEIVER ==============================

        IntentFilter filter = new IntentFilter(PkmnReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new PkmnReceiver();
        registerReceiver(receiver, filter);

        Button addButton = (Button) findViewById(R.id.sendRequest);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent msgIntent = new Intent(ListActivity.this, PkmnService.class);

                msgIntent.putExtra(PkmnService.REQUEST_STRING, "http://tino.ovh/pokedexTuto");
                startService(msgIntent);

            }
        });
        */

    }// onCreate

    //Méthode qui semble détruire le thread à la fin de sa vie
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
        this.unregisterReceiver(receiverUpdate);

    }//onDestroy()


    // Classe Gérant le BroadcastReceiver
    public class PkmnReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(PkmnIntentService.EXTRA_KEY_OUT);
            textResult.setText(result);
        }//onReceive()
    }//class PkmnReceiver

    // Classe Gérant le BroadcastReceiver (notification à l'activité principale)
    public class PkmnReceiverUpdate extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int update = intent.getIntExtra(PkmnIntentService.EXTRA_KEY_UPDATE, 0);
            progressBar.setProgress(update);
        }// onReceive
    }//class PkmnReceiverUpdate

    // Classe qui parse les donnée JSON dans la listview
    class loadPkmn extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog;

        @Override // Première méthode de l'Asynctask, est exécuté avant la tâche principale
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ListActivity.this);
            dialog.setMessage("Chargement...");
            dialog.setTitle("Connexion au serveur Rails");
            dialog.show();
            dialog.setCancelable(false);

        }//onPreExecute()

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                // Requêtes HTTP
                HttpGet httppost = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    // Déclaration des structures contenant les donnée JSON
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jarray = jsonObject.getJSONArray("pokedex");


                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject object = jarray.getJSONObject(i);

                        Pokemons singlePokemon = new Pokemons();

                        singlePokemon.setNameFr(object.getString("nameFr"));
                        singlePokemon.setNum(object.getString("num"));
                        singlePokemon.setNameUs(object.getString("nameUs"));
                        singlePokemon.setNameJap(object.getString("nameJap"));
                        singlePokemon.setType1(object.getString("type1"));
                        singlePokemon.setType2(object.getString("type2"));
                        singlePokemon.setPicture(object.getString("picture"));

                        pokemonsList.add(singlePokemon);
                    } //for


                    return true;
                }//if

            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }//doInBackground

        protected void onPostExecute(Boolean result) {
            dialog.cancel();
            adapter.notifyDataSetChanged();
            if (result == false)
                Toast.makeText(getApplicationContext(), "Problème lors de la récupération de données...", Toast.LENGTH_LONG).show();

        }// onPostExecute()

    }//class loadPkmn


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

/*
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextChange(String newText){
                // Filtre
                adapter.getFilter().filter(newText);
                System.out.println("Recherche: "+newText);
                return true;
            }//onQueryTextChange
            @Override
            public boolean onQueryTextSubmit(String query){
                adapter.getFilter().filter(query);
                System.out.println("Submit: "+query);
                return true;
            }//onQueryTextSubmit
        };
        searchView.setOnQueryTextListener(textChangeListener);
*/

        return super.onCreateOptionsMenu(menu);


    }//onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // On exécute l'action appropriée selon l'item cliqué
        switch (id) {
            case R.id.action_search:
                // Recherche
                Toast.makeText(getApplicationContext(), "La fonction Recherche n'est pas encore implémentée", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_refresh:
                // Rafraîchissement
                refreshMenuItem = item;
                //On réexécute l'AsyncTask lors de l'appui sur Rafraîchir
                new loadPkmn().execute("http://tino.ovh/pokedexTuto");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }//onOptionsItemSelected

/*

    // ==============   TENTATIVE D'IMPLEMENTER INTENTSERVICE ET BROADCASTRECEIVER POUR PARSER LE JSON DANS LA LISTVIEW   ==============

    public class PkmnService  extends IntentService {

        public static final String REQUEST_STRING = "myRequest";
        public static final String RESPONSE_STRING = "myResponse";
        public static final String RESPONSE_MESSAGE = "myResponseMessage";

        private String URL = null;
        private static final int REGISTRATION_TIMEOUT = 3 * 1000;
        private static final int WAIT_TIMEOUT = 30 * 1000;

        public PkmnService() {
            super("PkmnService");
        }

        @Override
        protected void onHandleIntent(Intent intent) {

            String requestString = intent.getStringExtra(REQUEST_STRING);
            String responseString = requestString + " " + DateFormat.format("MM/dd/yy h:mmaa", System.currentTimeMillis());
            String responseMessage = "";
            SystemClock.sleep(10000); // Wait 10 seconds
            Log.v("PkmnService:", responseString);

            // Requête de récupération des données JSON
            try {

                URL = requestString;
                HttpClient httpclient = new DefaultHttpClient();
                HttpParams params = httpclient.getParams();

                HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);

                HttpGet httpGet = new HttpGet(URL);
                HttpResponse response = httpclient.execute(httpGet);

                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseMessage = out.toString();

                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);


                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jarray = jsonObject.getJSONArray("pokedex");


                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject object = jarray.getJSONObject(i);

                        Pokemons singlePokemon = new Pokemons();

                        singlePokemon.setNameFr(object.getString("nameFr"));
                        singlePokemon.setNum(object.getString("num"));
                        singlePokemon.setNameUs(object.getString("nameUs"));
                        singlePokemon.setNameJap(object.getString("nameJap"));
                        singlePokemon.setType1(object.getString("type1"));
                        singlePokemon.setType2(object.getString("type2"));
                        singlePokemon.setPicture(object.getString("picture"));

                        pokemonsList.add(singlePokemon);
                    }
                }

                else //On ferme la connexion
                {
                    Log.w("HTTP1:",statusLine.getReasonPhrase());
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }

            }catch (ClientProtocolException e) {
                Log.w("HTTP2:",e );
                responseMessage = e.getMessage();
            } catch (IOException e) {
                Log.w("HTTP3:",e );
                responseMessage = e.getMessage();
            }catch (Exception e) {
                Log.w("HTTP4:",e );
                responseMessage = e.getMessage();
            }

            // Instanciation d'un Intent et gestion du broadcast

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(PkmnReceiver.PROCESS_RESPONSE);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(RESPONSE_STRING, responseString);
            broadcastIntent.putExtra(RESPONSE_MESSAGE, responseMessage);
            sendBroadcast(broadcastIntent);
        }
    }

    // ==============   TENTATIVE D'IMPLEMENTER BROADCASTRECEIVER   ==============
    public class PkmnReceiver extends BroadcastReceiver {
        // Dans ce bradcastreceiver, on créé la listview et on charge les données dedans

        public static final String PROCESS_RESPONSE = "com.wingnity.pokedex.intent.action.PROCESS_RESPONSE";

        ProgressDialog dialog;

        @Override
        public void onReceive(Context context, Intent intent) {
            String responseString = intent.getStringExtra(PkmnService.RESPONSE_STRING);
            String reponseMessage = intent.getStringExtra(PkmnService.RESPONSE_MESSAGE);

            // Une petite popup de chargement pour montrer que l'appli travaille
            dialog = new ProgressDialog(ListActivity.this);
            dialog.setMessage("Chargement...");
            dialog.setTitle("Connection au serveur Rails");
            dialog.show();
            dialog.setCancelable(false);

            TextView myTextView = (TextView) findViewById(R.id.response);
            myTextView.setText(responseString);

            ListView listview = (ListView) findViewById(R.id.list);
            adapter = new PkmnListAdapter(getApplicationContext(), R.layout.listpkmn, pokemonsList);

            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                        long id) {
                    // TODO Auto-generated method stub
                    Toast.makeText(getApplicationContext(), pokemonsList.get(position).getNameFr(), Toast.LENGTH_SHORT).show();
                }
            });

            listview.setTextFilterEnabled(true);

        }
    }
    */
}