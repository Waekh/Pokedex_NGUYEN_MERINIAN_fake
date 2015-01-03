package com.wingnity.pokedex;

public class Pokemons {

	private String nameFr;
	private String nameUs;
	private String nameJap;
	private String num;
	private String type1;
	private String type2;
	private String picture;

	public Pokemons() {
		// TODO Auto-generated constructor stub
	}

    //Constructeur au cas où on voudrait ajouter des entrées dans le pokédex
	public Pokemons(String nameFr, String nameUs, String nameJap, String num,
                    String type1, String type2, String picture) {
		super();
		this.nameFr = nameFr;
		this.nameUs = nameUs;
		this.nameJap = nameJap;
		this.num = num;
		this.type1 = type1;
		this.type2 = type2;
		this.picture = picture;
	}


	public String getNameFr() {
		return nameFr;
	}

	public void setNameFr(String nameFr) {
		this.nameFr = nameFr;
	}

	public String getNameUs() {
		return nameUs;
	}

	public void setNameUs(String nameUs) {
		this.nameUs = nameUs;
	}

	public String getNameJap() {
		return nameJap;
	}

	public void setNameJap(String nameJap) {
		this.nameJap = nameJap;
	}

	public String getNum() { return num; }

	public void setNum(String num) {
		this.num = num;
	}

	public String getType1() {
		return type1;
	}

	public void setType1(String type1) {
		this.type1 = type1;
	}

	public String getType2() {
		return type2;
	}

	public void setType2(String type2) {
		this.type2 = type2;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}


}
