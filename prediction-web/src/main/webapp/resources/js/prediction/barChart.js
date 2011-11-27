var BarChart = new Class({
	Implements : [ Options, Events ],
	chartOptions : null,
	chart : null,
	options : {},
	initialize : function(divId, labels, options) {
		var emptyLabel = [];
		for(label in labels) {
			emptyLabel.push("");
		}
		this.setOptions(options);
		var that = this;
		this.label = labels ;
		var that = this ;
		this.chartOptions = {
			chart : {
				renderTo : divId,
				backgroundColor: 'rgba(255,255,255,0)',
				defaultSeriesType : 'bar'
			},
			title : {
				text : ''
			},
			xAxis : {
				categories: emptyLabel,
				title : {
					text : null
				}
			},
			yAxis : {
				min : 0,
				title : {
					text : ''
				}
			},
			tooltip : {
				formatter : function() {
					return '' + that.label[this.point.x]+ ': ' + this.y + ' % ';
				}
			},
			plotOptions : {
				bar : {
					dataLabels : {
						enabled : true
					}
				}
			},
			legend : {
				enabled : false
			},
			credits : {
				enabled : false
			}
		}
	},
	initChart : function(series) {
		this.chartOptions.series = series;
		this.chart = new Highcharts.Chart(this.chartOptions);
	}
});