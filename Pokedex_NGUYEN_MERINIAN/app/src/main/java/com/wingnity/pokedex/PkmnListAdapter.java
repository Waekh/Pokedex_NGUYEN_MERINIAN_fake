package com.wingnity.pokedex;

import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.tino.pokedex.R;

public class PkmnListAdapter extends ArrayAdapter<Pokemons> {

	private ArrayList<Pokemons> pkmnList;
	private LayoutInflater vi;
	private int Resource;
	private AttributsPkmn att;



	public PkmnListAdapter(Context context, int resource, ArrayList<Pokemons> objects) {
		super(context, resource, objects);
		vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Resource = resource;
		pkmnList = objects;
	}
 
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		if (v == null) {
			att = new AttributsPkmn();
			v = vi.inflate(Resource, null);
			att.imagePkmn = (ImageView) v.findViewById(R.id.idPicture);
			att.numPkmn = (TextView) v.findViewById(R.id.idNum);
			att.nameFrPkmn = (TextView) v.findViewById(R.id.idNameFr);
			att.nameUsPkmn = (TextView) v.findViewById(R.id.idNameUs);
			att.nameJapPkmn = (TextView) v.findViewById(R.id.idNameJap);
			att.type1Pkmn = (TextView) v.findViewById(R.id.idType1);
			att.type2Pkmn = (TextView) v.findViewById(R.id.idType2);
			v.setTag(att);
		} else {
			att = (AttributsPkmn) v.getTag();
		}


		new DownloadImageTask(att.imagePkmn).execute("http://tino.ovh/spritesPkmn/" + pkmnList.get(position).getPicture());
		att.numPkmn.setText("#" + pkmnList.get(position).getNum());
		att.nameFrPkmn.setText(pkmnList.get(position).getNameFr());
		att.nameUsPkmn.setText(pkmnList.get(position).getNameUs() + "(US)");
		att.nameJapPkmn.setText(pkmnList.get(position).getNameJap()+ "(JAP)");
        att.type1Pkmn.setText(pkmnList.get(position).getType1());

        // Gestion du deuxième type
        if(pkmnList.get(position).getType2()=="null")
		    att.type2Pkmn.setText("");
        else
            att.type2Pkmn.setText(" / " + pkmnList.get(position).getType2());

        return v;

	}

	static class AttributsPkmn {
		public ImageView imagePkmn;
		public TextView numPkmn;
		public TextView nameFrPkmn;
		public TextView nameUsPkmn;
		public TextView nameJapPkmn;
		public TextView type1Pkmn;
		public TextView type2Pkmn;

	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}

	}
}