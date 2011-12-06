var GeoDataHandler = new Class({
	Implements: [Options, Events],
	reqToCall: null,
	geochart: null,
	options: {
	},
	initialize: function(options){
		this.setOptions(options);
		this.regions =$$('.region');
		this.tips = new Tips(this.regions, {className: "tips"});
		this.region = new MapFr('mapContainer');
		this.region.draw();
	},

	/**
	 * Draw the map.
	 */
	drawMap: function (regions) {
		this.region.color(regions);
	},
	/**
	 * Display the given report.
	 */
	displayGeoReport: function(timestamp, candidatName) {
		req = {timestamp: timestamp}
		if(typeof(candidatName) != "undefined") {
			req.candidatName = candidatName ;
		}
		var that = this;
		new Request.JSON({url: './geoReport/', 
			headers:{'Content-type':'application/json'},
			urlEncoded: false,
			method: "get",
			onSuccess: function(data){
				that.drawMap(data);
			}
		}).get(req);
	}
	
});