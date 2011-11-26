var Chart = new Class({
	Implements : [ Options,Events],
	chartOptions : null,
	chart : null,
	options : {},
	initialize : function(options) {
		this.setOptions(options);
		var that = this ;
		this.chartOptions = {
			chart : {
				renderTo : 'containerChart',
				plotBackgroundColor : null,
				plotBorderWidth : null,
				plotShadow : false
			},
			title : {
				text : ''
			},
			xAxis : {
				title : {
					text : 'Date'
				},
				type : 'datetime',
				tickInterval : 24 * 3600 * 1000, // one day
				labels : {
					step : 10
				},
				dateTimeLabelFormats : {
					day : '%e %b'
				}
			},

			yAxis : [ {
				title : {
					text : 'Tendance'
				},
				labels : {
					x : 3,
					y : 16,
					formatter : function() {
						return this.value + ' %';
					}
				},
				showFirstLabel : false,
				showLastLabel : false
			}],

			legend : {
				borderWidth : 0
			},

			tooltip : {
			/*	formatter:function () {
					console.log(this);
					return 'The value for <b>'+ this.x +'</b> is <b>'+ this.y +'</b>';
				},*/
				shared : true,
				crosshairs : true
			},

			plotOptions : {
				spline : {
					pointInterval : 24 * 3600000, // one day
					pointStart : 0 //To be defined during initialization.
				},
				series : {
					cursor : 'pointer',
					point : {
						events : {
							click : function() {
								that.fireEvent('clickOnChart', this.x, this.y);
							}
						}
					},
					marker : {
						enabled : false,
						states : {
							hover : {
								enabled : true,
								symbol : 'circle',
								radius : 4,
								lineWidth : 1
							}
						}
					}
				}
			}
		}
	},
	initChart : function(series, startDate) {
		this.chartOptions.plotOptions.spline.pointStart= parseInt(startDate,10) ;
		this.chartOptions.series = series;
		this.chart = new Highcharts.Chart(this.chartOptions);
	}
});