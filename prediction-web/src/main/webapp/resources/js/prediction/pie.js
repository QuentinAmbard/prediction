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
					var val = Math.round(this.percentage*10)/10 ;
					if(this.point.name == "DROITE") {
						return '<b>La droite</b><br />Répartition : '+val+" %";
					} else if(this.point.name == "EXTREME_DROITE") {
						return '<b>Ex. droite</b><br />Répartition : '+val+" %";
					} else if(this.point.name == "GAUCHE") {
						return '<b>La gauche</b><br />Répartition : '+val+" %";
					} else if(this.point.name == "CENTRE") {
						return '<b>Le centre</b><br />Répartition : '+val+" %";
					} else if(this.point.name == "EXTREME_GAUCHE") {
						return '<b>Ex. gauche</b><br />Répartition : '+val+" %";
					} else 
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
							var firstName = this.point.name.substring(0, this.point.name.indexOf(" "));
							var lastName = this.point.name.substring(this.point.name.lastIndexOf(" "), this.point.name.length);
							if(lastName.length>6) {
								lastName = lastName.substring(0,6)+".";
							}
							//return '<b>'+ this.point.name +'</b>';
							return '<b>'+ firstName.substring(0,1)+'. '+lastName+'</b>';
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