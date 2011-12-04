var BarChart = new Class({
	Implements : [ Options, Events ],
	chartOptions : null,
	chart : null,
	options : {},
	initialize : function(divId, labels, options) {
		//No category name.
		var emptyCategories = [];
		for(label in labels) {
			emptyCategories.push("");
		}
		this.setOptions(options);
		var that = this;
		this.labels = labels ;
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
				categories: emptyCategories,
				title : {
					text : null
				}
			},
			yAxis : {
				min : 0,
				max: 100,
				title : {
					text : ''
				}
			},
			tooltip : {
				formatter : function() {
					return '<b>' + that.labels[this.point.x].title+ '</b><br/>'+that.labels[this.point.x].text+'<br />Valeur : ' + this.y + ' % ';
				}
			},
			plotOptions : {
				series: {
		            cursor: 'pointer',
		            allowPointSelect: true
		        },
				bar : {
					dataLabels : {
						enabled : true,
						formatter: function() {
							return this.y+" % ";
						}
					},
					events: {
						click: function (event) {
							that.fireEvent('click', that.labels[event.point.x].id)
						}
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