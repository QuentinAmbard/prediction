var Pie = new Class({
	Implements: [Options, Events],
	pieOption: null,
	options: {
		dataLabelsEnabled: true,
		innerSize: 160,
		stickyTracking: true
	},
	initialize: function(renderToId, options){
		this.setOptions(options);
		var that = this ;
		this.pieOption = {
			chart: {
				renderTo: renderToId,
				backgroundColor: 'rgba(255,255,255,0)',
				plotBackgroundColor: null,
				plotBorderWidth: null,
				plotShadow: false
			},
			title: {
				text: ''
			},
			tooltip: {
				formatter: function() {
					return '<b>'+ this.point.name +'</b>: '+ Math.round(this.percentage*10)/10 +' %<br />Parti : '+this.point.partiFullName+'<br/>Cliquez pour afficher le détail';
				}
			},
			plotOptions: {
				pie: {
					events: {
						mouseOver: function () {
							that.fireEvent('mouseOver');
						},
						mouseOut: function () {
							that.fireEvent('mouseOut')
						}
					},
					stickyTracking: this.options.stickyTracking,
					circ: Math.PI,
					innerSize: this.options.innerSize,
					allowPointSelect: true,
					cursor: 'pointer',
					dataLabels: {
						enabled: this.options.dataLabelsEnabled,
						formatter: function() {
							return '<b>'+ this.point.parti +'</b>';
						}
					}
				}
			},
			credits : {
				enabled : false
			}
		}
	},
	initChart: function (series) {
		this.pieOption.series = series ;
		this.chart = new Highcharts.Chart(this.pieOption);
	}
});