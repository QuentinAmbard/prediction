var DataHandler = new Class({
	Implements: [Options],
	candidats: null,
	lastTimestamp: null,
	firstTimestamp: null,
	selectedTimestamp: null,
	selectedType: "tendance",
	pie: null,
	chart: null,
	selectType: null,
	piePosition: null,
	threeMap: null,
	options: {
	},
	initialize: function(profile, options){
		this.setOptions(options);
		var that = this ;
		this.threeMap = new ThreeMap("treeMap");
		this.threeMap.draw([20,50,10,10,5,5]);
		this.geoDataHandler = new GeoDataHandler();
		this.selectType = $('selectType')
		this.selectType.addEvent('click', function () {
			var type = this.getSelected().get("value");
			that.updatePie(that.selectedTimestamp, type);
			that.updateGraph(type);
			that.updateGraphDetails();
		});
		this.pie = new Pie("containerPie", {
			stickyTracking: false
		});
		this.piePosition = new Pie("containerPiePosition", {
			dataLabelsEnabled: false,
			innerSize: 130
		});
		//svg Z-index hack.
		this.pie.addEvents({
			'mouseOver': function() {
				$('containerPie').setStyle('z-index', 100);
			},
			'mouseOut': function() {
				$('containerPie').setStyle('z-index', -1);
			}
		});
		this.chart = new Chart("containerChart");
		this.chart.addEvent('clickOnChart', function (date, type) {
			that.updatePie(date, type);
		});
		this.chartDetails = new BarChart("containerChartDetails", ["Buzz", "Avis Négatifs", "Avis positifs", "Désinteressé"]);
	},
	/**
	 * Return the candidat with a displayedName (eg. Nicolas Sarkozy)
	 */
	getCandidat: function(displayName) {
		for(candidat in this.candidats){
			if(displayName == this.candidats[candidat].displayName) {
				return this.candidats[candidat] ;
			} ;
		}
	},
	getData: function () {
		var that = this;
		new Request.JSON({url: './candidats/', 
			headers:{'Content-type':'application/json'},
			urlEncoded: false,
			method: "get",
			onSuccess: function(data){
				that.candidats = {};
				for(var i =0,ii=data.candidats.length;i<ii;i++) {
					that.candidats[data.candidats[i].candidatName] = data.candidats[i] ;
				}
				that.reports = data.reports ;
				that.firstTimestamp = that.reports[0].timestamp ;
				that.lastTimestamp = that.reports[that.reports.length-1].timestamp ;
				that.selectedTimestamp = that.lastTimestamp ;
				that.geoDataHandler.displayGeoReport(that.selectedTimestamp);
				//Main pie serie
				var dataPie = [];
				var lastReport = that.reports[that.reports.length-1] ;
				for(candidat in that.candidats) {
					var candidatReport = lastReport.candidats[candidat];
					dataPie.push({name: that.candidats[candidat].displayName, 
						y:candidatReport.tendance,
						events:{
							//Click on a point on the main pie
			    			click:function () {
			    				var candidat = this.selected ? undefined : that.getCandidat(this.name) ;
			    				that.updateGraphDetails(candidat);
			    				var candidatName = this.selected ? undefined : candidat.candidatName;
			    				that.geoDataHandler.displayGeoReport(that.selectedTimestamp, candidatName);
				    		}
			    		}
					});
				}
			    var series= [{
					type: 'pie',
					data: dataPie
				}]
				that.pie.initChart(series);

			    //Position pie serie
			    var dataPiePositionObj = that.getPositionsForReport(lastReport);
			    var dataPiePosition = [];
			    for(var position in dataPiePositionObj) {
			    	dataPiePosition.push({name: position, 
			    		y: dataPiePositionObj[position]
			    	});
			    }
			    var series= [{
					type: 'pie',
					data: dataPiePosition
				}]
			    that.piePosition.initChart(series);
				that.chart.initChart(that.getSeriesForChart(), that.firstTimestamp);
				
				//details chart
				that.chartDetails.initChart(that.getSeriesForChartDetails(that.lastTimestamp));
			}
		}).send();
	}, 
	/**
	 * Return the series given a specific type.
	 */
	getSeriesForChart: function (type) {
		type = type || "tendance" ;
		var series = []
		for(var i =0, ii=this.reports.length;i<ii;i++) {
			var data = [];
			var report = this.reports[i];
			for (candidat in report.candidats) {
				var serie = null;
				for(var j=0,jj=series.length;j<jj;j++) {
					if(series[j].nameBrut == candidat) {
						serie = series[j] ;
						break;
					} 
				}
				if(serie == null) {
					serie = {nameBrut: candidat, name: this.candidats[candidat].displayName, lineWidth: 2, data: []};
					series.push(serie);
				}
				serie.data.push([report.timestamp, Math.round(report.candidats[candidat][type]*10)/10]);
			}
		}
		return series ;
	},
	/**
	 * Return the series given a specific type.
	 */
	getSeriesForChartDetails: function (date, type, candidat) {
		type = type || "tendance" ;
		var report = this.getReport(date);
		var series = [{name: "", data: [] }];
		var values = {"buzz": 0, "neg": 0, "pos": 0, "none": 0};

		//All candidats, we take the average.
		if(typeof(candidat) == "undefined") {
			var i = 0;
			for (var candidat in this.candidats) {
				i++;
				for(var value in values) {
					values[value] += report.candidats[candidat][value];
				}
			}
			for(var value in values) {
				values[value] = Math.round(values[value]/i*10)/10;
			}
		} //We just update for a specifc candidat 
		else {
			for(var value in values) {
				values[value] = Math.round(report.candidats[candidat.candidatName][value]*10)/10;
			}
		}
		var colors = ['#00FF00', '#FF00FF', '#FF0044'];
		var i =0;
		for(var value in values) {
			series[0].data.displayName = value ;
			series[0].data.push({y: values[value], color: colors[i]});
			i++
		}
		return series ;
	},
	/**
	 * Return the position for the given report.
	 */
	getPositionsForReport: function(report) {
		var dataPiePositionObj = {};
		for(candidat in this.candidats) {
			var candidatReport = report.candidats[candidat];
			if(typeof(dataPiePositionObj[this.candidats[candidat].position]) =="undefined") {
				dataPiePositionObj[this.candidats[candidat].position] = 0;
			}
			dataPiePositionObj[this.candidats[candidat].position] += candidatReport.tendance ;
		}
		return dataPiePositionObj;
	},
	/**
	 * Return the report of the date.
	 */
	getReport: function (date) {
		for (var i=0, ii=this.reports.length;i<ii;i++) {
			if(this.reports[i].timestamp == date){
				return this.reports[i] ;
			}
		}
	},
	/**
	 * Update the 2 pies
	 */
	updatePie: function(date, type) {
		type = type || "tendance" ;
		var data = this.pie.chart.series[0].data ;
		var positionData = this.piePosition.chart.series[0].data ;
		var report = this.getReport(date);
		for(i =0,ii=data.length;i<ii;i++) {
			var name = this.getCandidat(data[i].name).candidatName;
			data[i].update(report.candidats[name][type]);
		}
		var dataReport = this.getPositionsForReport(report);
		var i=0;
		for(var position in dataReport) {
			positionData[i].update(dataReport[position]);
	    	i++;
	    }
	},
	/**
	 * Update the graph with new datas.
	 */
	updateGraph: function (type) {
		var newSeries = this.getSeriesForChart(type);
		type = type || "tendance" ;
		for(var i=0, ii=this.chart.chart.series.length;i<ii;i++){
			this.chart.chart.series[i].setData(newSeries[i].data) ;
		}
		this.chart.chart.redraw();
	},
	/**
	 * Update the graph details
	 */
	updateGraphDetails: function (candidat) {
		var newSeries = this.getSeriesForChartDetails(this.selectedTimestamp, this.selectedType, candidat);
		var data = this.chartDetails.chart.series[0].data ;
		for(var i=0,ii=data.length;i<ii;i++){
			data[i].update(newSeries[0].data[i]) ;
		}
	}
	
});