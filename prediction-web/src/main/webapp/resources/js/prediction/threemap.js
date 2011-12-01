var ThreeMap = new Class({
	Implements: [Options, Events],
	pieOption: null,
	container: null,
	width: 0,
	height: 0,
	options: {
	},
	initialize: function(renderToId, options){
		this.container = $(renderToId);
		this.width = this.container.getSize().x;
		this.height = this.container.getSize().y;
	}, draw: function (values) {
		//Get the hightest value
		var max = 0;
		for(var i =0, ii=values.length;i<ii;i++) {
			max = Math.max(max, values[i]);
		}
		var div = new Element('div', {
			styles: {
				position: "absolute",
				left: 0,
				top:0,
				width: 100,
				height: 50,
				backgroundColor: "#00FF00"
			}
		});
		div.inject(this.container);
	}
});
