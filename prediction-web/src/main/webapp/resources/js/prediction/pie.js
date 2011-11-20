var Pie = new Class({
	Implements: [Options],
	options: {
	},
	initialize: function(profile, options){
		this.setOptions(options);
		chart = new Highcharts.Chart({
			chart: {
				renderTo: 'containerPie',
				plotBackgroundColor: null,
				plotBorderWidth: null,
				plotShadow: false
			},
			tooltip: {
				formatter: function() {
					return '<b>'+ this.point.name +'</b>: '+ this.percentage +' %';
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
							return '<b>'+ this.point.name +'</b>: '+ this.percentage +' %';
						}
					}
				}
			},
		    series: [{
				type: 'pie',
				name: 'Browser share',
				data: [
					['Firefox',   45.0],
					['IE',       26.8],
					['Safari',    8.5],
					['Opera',     6.2],
					['Others',   0.7],
					['IE',       26.8],
					['Safari',    8.5],
					['Opera',     6.2],
					['Others',   0.7]
				]
			}]
		});
	}
});