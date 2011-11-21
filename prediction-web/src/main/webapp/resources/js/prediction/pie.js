var Pie = new Class({
	Implements: [Options, Events],
	options: {
	},
	initialize: function(options){
		this.setOptions(options);
	},
	initChart: function (data) {
		this.chart = new Highcharts.Chart({
			chart: {
				renderTo: 'containerPie',
				plotBackgroundColor: null,
				plotBorderWidth: null,
				plotShadow: false
			},
			title: {
				text: ''
			},
			tooltip: {
				formatter: function() {
					return '<b>'+ this.point.name +'</b>: '+ Math.round(this.percentage*10)/10 +' %';
				}
			},
			plotOptions: {
				pie: {
					circ: Math.PI,
					innerSize: 150,
					allowPointSelect: true,
					cursor: 'pointer',
					//pointStart: 100,
					dataLabels: {
						enabled: true,
						color: '#000000',
						connectorColor: '#000000',
						formatter: function() {
							return '<b>'+ this.point.name +'</b>: '+ Math.round(this.percentage*10)/10 +' %';
						}
					}
				}
			},
		    series: [{
				type: 'pie',
				name: 'Browser share',
				data: data
			}]
		});
	}
});