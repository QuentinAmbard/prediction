UTILS = {
	color : {
		/**
		 * Transform a rgb to a hex[6]
		 */
		rgb2hex : function(rgb) {
			function hex(x) {
				return ("0" + parseInt(x).toString(16)).slice(-2);
			}
			return "#" + hex(rgb[0]) + hex(rgb[1]) + hex(rgb[2]);
		},
		/**
		 * Transform a hex[6] to a rgb
		 */
		hex2rgb : function(colour) {
			var r, g, b;
			if (colour.charAt(0) == "#") {
				colour = colour.substr(1);
			}
			function hex(i) {
				return parseInt(colour.charAt(i) + colour.charAt(i + 1), 16);
			}
			return [ hex(0), hex(2), hex(4) ];
		},
		/**
		 * Return an interpolation of a color between a min and a max (hex
		 * values), given a percentage.
		 */
		getColor : function(percent, colorMax, colorMin) {
			var rgbMax = this.hex2rgb(colorMax);
			var rgbMin = this.hex2rgb(colorMin);
			var rgb = [];
			for ( var i = 0; i < rgbMax.length; i++) {
				rgb[i] = (rgbMax[i] - rgbMin[i]) * percent / 100 + rgbMin[i];
			}
			return this.rgb2hex(rgb);
		}
	}
}