var DataHandler = new Class({
	Implements: [Options],
	candidats: null,
	lastTimestamp: 0,
	firstTimestamp: 99999999999999999,
	pie: null,
	options: {
	},
	initialize: function(profile, options){
		this.setOptions(options);
		this.pie = new Pie();
		this.chart = new Chart();
		var that = this ;
		this.chart.addEvent('clickOnChart', function (x, y) {that.updatePie(x, y);});
	},
	getData: function () {
		var that = this;
		new Request.JSON({url: './candidats/', 
			headers:{'Content-type':'application/json'},
			urlEncoded: false,
			method: "get",
			onSuccess: function(data){
				that.candidats = data.candidats ;
				that.reports = data.reports ;
				that.firstTimestamp = that.reports[0].timestamp ;
				that.lastTimestamp = that.reports[that.reports.length-1].timestamp ;
				var dataPie = [];
				var lastReport = that.reports[that.reports.length-1] ;
				console.log(that.reports[0])
				console.log(lastReport)
				for(var candidat in lastReport.candidats) {
					var candidatReport = lastReport.candidats[candidat];
					dataPie.push([candidat, candidatReport.tendance])
				}
				that.pie.initChart(dataPie);
				that.chart.initChart(that.getSeriesForChart(), that.firstTimestamp);
				
			}
		}).send();
	}, 
	/**
	 * Return the series given a specific type.
	 */
	getSeriesForChart: function (type) {
		type = type || "tendance" ;
		var series = []
		for(var i =0;i<this.reports.length;i++) {
			var data = [];
			var report = this.reports[i];
			for (candidat in report.candidats) {
				var serie = null;
				for(var j=0;j<series.length;j++) {
					if(series[j].name == candidat) {
						serie = series[j] ;
						break;
					} 
				}
				if(serie == null) {
					serie = {name: candidat, lineWidth: 2, data: []};
					series.push(serie);
				}
				serie.data.push(Math.round(report.candidats[candidat][type]*10)/10);
			}
		}
		return series ;
	},
	updatePie: function(date, type) {
		type = type || "tendance" ;
		var data = this.pie.chart.series[0].data ;
		for(i =0;i<data.length;i++) {
			var name = data[i].name;
			data[i].update(this.reports[date].candidats[name][type]);
		}
	}

	
});