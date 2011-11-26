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
				that.firstTimestamp = data.reports[0].timestamp ;
				that.lastTimestamp = data.reports[data.reports.length-1].timestamp ;
				
				var dataPie = [];
				for(var i =0;i<that.candidats.length;i++) {
					var candidat = that.candidats[i];
					data.push([candidat.displayName, data.reports[data.reports.length-1].tendance])
				}
				that.pie.initChart(data);
				that.chart.initChart(that.getSeriesForChart(), that.firstTimestamp);
				
			}
		}).send();
	}, 
	getSeriesForChart: function (type) {
		type = type || "tendance" ;
		var series = []
		for(var i =0;i<this.candidats.length;i++) {
			var data = [];
			var candidat = this.candidats[i];
			for (var j=0;j<candidat.dailyReports.length;j++) {
				data.push(Math.round(candidat.dailyReports[j][type]*10)/10);
			}
			series.push({name: candidat.displayName, lineWidth: 2, data: data});
		}
		return series ;
	},
	updatePie: function(date, type) {
		type = type || "tendance" ;
		var data = []
		var k=0;
		for(var i =0;i<this.candidats[0].dailyReports.length;i++) {
			if(this.candidats[0].dailyReports[i].timestamp == date) {
				k = i;
				break;
			}
		}
		for(i =0;i<this.candidats.length;i++) {
			var candidat = this.candidats[i];
			data.push([candidat.displayName, candidat.dailyReports[k][type]])
		}
		for(i=0;i<data.length;i++) {
			this.pie.chart.series[0].data[i].update(data[i]);
		}
	}

	
});