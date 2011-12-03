var ThreeMap = new Class({
	Implements: [Options, Events],
	pieOption: null,
	container: null,
	width: 0,
	height: 0,
	options: {
		colors: ['#CE0071', '#DF004F', '#4E10AE', '#D5F800', '#0ACF00', '#FFE700'],
		padding: 5
	},
	initialize: function(renderToId, options){
		this.container = $(renderToId);
		this.width = this.container.getSize().x;
		this.height = this.container.getSize().y;
	}, draw: function (values) {
		//Order by the highest value
		values = values.sort(function (a, b) {
			return b-a;
		});
		var area = this.width*this.height;
		//Max:
		var widths = [];
		var heights = []; //values.length
		for(var i=0,ii=values.length;i<ii;i++) {
			//Create the element
			var div = new Element('div', {
				styles: {
					position: "absolute",
					backgroundColor: this.options.colors[i],
					left: this.width*Math.random(), 
					top: this.height*Math.random(),
					width: 0,
					height: 0
				}
			});
			
			if(i%2==0) {
				heights[i] = this.height;
				for(var j=1;j<i;j+=2) {
					heights[i] -= heights[j];
				}
				widths[i] = area*values[i]/(100*heights[i]);
			} else {
				widths[i] = this.width ;
				for(var j=0;j<i;j+=2) {
					widths[i] -= widths[j];
				}
				heights[i] = area*values[i]/(100*widths[i]);

			}
			var left = 0;
			for(var j=0;j<i;j+=2) {
				left+= widths[j];
			}
			var top = 0;
			for(var j=1;j<i;j+=2) {
				top+= heights[j];
			}
			div.morph({left: left, 
				top: top,
				width: widths[i]-this.options.padding,
				height: heights[i]-this.options.padding
			});
			div.inject(this.container);
		}
	}
});
