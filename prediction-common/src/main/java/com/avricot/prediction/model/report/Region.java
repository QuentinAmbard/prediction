package com.avricot.prediction.model.report;

public enum Region {
	PACA("Provence-Alpes-Côte d'Azur", 4882913), //
	RHONE_ALPES("Rhône-Alpes", 6117229), //
	ALSACE("Alsace", 1837087), //
	AQUITAINE("Aquitaine", 3177625), //
	AUVERGNE("Auvergne", 1341863), //
	BOURGOGNE("Bourgogne", 1638588), //
	BRETAGNE("Bretagne", 3149701), //
	CENTRE("centre", 2531588), //
	CHAMPAGNE_ARDENNE("Champagne-Ardenne", 1338004), //
	CORSE("corse", 302966), //
	FRANCHE_COMPTE("Franche-Compté", 1163931), //
	ILE_DE_FRANCE("Ile-de-France", 11659260), //
	LANGUEDOC_ROUSSILLON("Languedoc-Roussillon", 2581718), //
	LIMOUSIN("Limousin", 740743), //
	LORRAINE("Lorraine", 2346361), //
	MIDI_PYRENEES("Midi-Pyrénées", 2838228), //
	NORD_PAS_DE_CALAIS("Nord-Pas-de-Calais", 4024490), //
	BASSE_NORMANDIE("Basse-Normandie", 1467425), //
	HAUTE_NORMANDIE("Haute-Normandie", 1825667), //
	PAYS_DE_LA_LOIRE("Pays de la Loire", 3510170), //
	PICARDIE("Picardie", 1906601), //
	POITOU_CHARENTES("Poitou-Charentes", 1752708);

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
}
