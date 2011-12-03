var ThreeMap = new Class({
	Implements: [Options, Events],
	pieOption: null,
	container: null,
	width: 0,
	height: 0,
	options: {
		colors: ['#CE0071', '#DF004F', '#4E10AE', '#D5F800', '#0ACF00', '#FFE700'],
		padding: 5,
		zoom: 6
	},
	initialize: function(renderToId, options){
		this.container = $(renderToId);
		this.width = this.container.getSize().x;
		this.height = this.container.getSize().y;
	}, draw: function (values) {
		console.log(values);
		var that = this;
		//Order by the highest value
		values = values.sort(function (a, b) {
			return b.value-a.value;
		});
		var area = this.width*this.height;
		//Max:
		var widths = [];
		var heights = []; //values.length
		for(var i=0,ii=values.length;i<ii;i++) {
			//Create the element if not present
			var divs = this.container.getElements("div");
			if(i%2==0) {
				heights[i] = this.height;
				for(var j=1;j<i;j+=2) {
					heights[i] -= heights[j];
				}
				widths[i] = area*values[i].value/(100*heights[i]);
			} else {
				widths[i] = this.width ;
				for(var j=0;j<i;j+=2) {
					widths[i] -= widths[j];
				}
				heights[i] = area*values[i].value/(100*widths[i]);

			}
			var left = 0;
			for(var j=0;j<i;j+=2) {
				left+= widths[j];
			}
			var top = 0;
			for(var j=1;j<i;j+=2) {
				top+= heights[j];
			}
			if(divs.length<i+1) {
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
				div.inject(this.container);
				new Tips(div);
			} else {
				div = divs[i];
			}
			div.store('tip:text', values[i].text);
			div.store('tip:title', values[i].title);
			div.morph({left: left, 
				top: top,
				width: widths[i]-this.options.padding,
				height: heights[i]-this.options.padding
			});
			
			(function (div, left, top, width, height) {
				div.addEvents({'mouseover': function () {
						this.morph({
							left: left-that.options.zoom/2, 
							top: top-that.options.zoom/2,
							width: width+that.options.zoom,
							height: height+that.options.zoom
						});
					}, 
					'mouseout': function () {
						this.morph({
							left: left, 
							top: top,
							width: width,
							height: height
						});
					}
				});
			})(div, left, top, widths[i]-that.options.padding, heights[i]-this.options.padding)
			
		}
	}
});
