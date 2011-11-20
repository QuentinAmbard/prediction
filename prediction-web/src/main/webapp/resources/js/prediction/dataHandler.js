var DataHandler = new Class({
	Implements: [Options],
	options: {
	},
	initialize: function(profile, options){
		this.setOptions(options);
	},
	getData: function () {
		new Request.JSON({url: './candidats/', 
			headers:{'Content-type':'application/json'},
			urlEncoded: false,
			method: "get",
			onSuccess: function(candidats){
				console.log(candidats);
			}
		}).send();
	}
});