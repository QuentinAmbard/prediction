package com.avricot.prediction.model.report;

public enum Region {
	PAC("Provence-Alpes-Côte d'Azur", 4882913), //
	RHO("Rhône-Alpes", 6117229), //
	ALS("Alsace", 1837087), //
	AQU("Aquitaine", 3177625), //
	AUV("Auvergne", 1341863), //
	BOU("Bourgogne", 1638588), //
	BRE("Bretagne", 3149701), //
	CEN("Centre", 2531588), //
	CHA("Champagne-Ardenne", 1338004), //
	COR("Corse", 302966), //
	FRA("Franche-Comté", 1163931), //
	ILE("Île-de-France", 11659260), //
	LAN("Languedoc-Roussillon", 2581718), //
	LIM("Limousin", 740743), //
	LOR("Lorraine", 2346361), //
	MID("Midi-Pyrénées", 2838228), //
	NOR("Nord-Pas-de-Calais", 4024490), //
	BNO("Basse-Normandie", 1467425), //
	HNO("Haute-Normandie", 1825667), //
	PAY("Pays de la Loire", 3510170), //
	PIC("Picardie", 1906601), //
	POI("Poitou-Charentes", 1752708);

	private final String name;
	private final int population;

	private Region(String name, int population) {
		this.name = name;
		this.population = population;
	}

	public String getName() {
		return name;
	}

	public static Region findByName(String name) {
		String nameLowerCase = name.toLowerCase();
		for (Region region : Region.values()) {
			if (region.getName().toLowerCase().equals(nameLowerCase)) {
				return region;
			}
		}
		return null;
	}

	public int getPopulation() {
		return population;
	}
}
