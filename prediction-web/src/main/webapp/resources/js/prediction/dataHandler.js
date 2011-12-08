var DataHandler = new Class({
	Implements: [Options],
	candidats: null,
	lastTimestamp: null,
	firstTimestamp: null,
	selectedTimestamp: null,
	selectedType: "tendance",
	selectedCandidat: undefined,
	future: true, //True: display the tendance on the pie.
	pie: null,
	chart: null,
	selectType: null,
	piePosition: null,
	threeMap: null,
	tweets: [],
	tweetsTimer: null,
	globalTheme: null,
	forcedCandidat: {},
	options: {
		events: {
			"1319061600000": [{value: "Accouchement de Carla Bruni.", candidatName: "SARKOZY"}],
			"1318716000000": [{value: "Second tour des primaires socialistes", candidatName: "HOLLANDE"}],
			"1318370400000": [{value: "Premier tour des primaires socialistes", candidatName: "HOLLANDE"}],
			"1319666400000": [{value: "Sommet européen", candidatName: "SARKOZY"}],
			"1320361200000": [{value: "Ouverture du G20 à Cannes", candidatName: "SARKOZY"}],
			"1319320800000": [{value: "convention d'investiture", candidatName: "HOLLANDE"}],
			"1322002800000": [{value: "Discours d'Eva Joly à Tokyo", candidatName: "JOLY"}],
			"1322002800000": [{value: "Discours d'Eva Joly à Tokyo", candidatName: "JOLY"}],
			"1322694000000": [{value: "Discours de Nicolas Sarkozy à Toulon", candidatName: "SARKOZY"}],
			"1322434800000": [{value: "Hervé Morin officialise sa candidature", candidatName: "MORIN"}],
			"1321398000000": [{value: "Polémique sur l'EPR entre le PS et les verts ", candidatName: "HOLLANDE"}]
		},
		opinionDescription: { "tendance": {title: "la tendance", text: "Représente le résultat prévisionnel des élections de 2012, avec les données du web."}, 
			"buzz": {title: "le buzz", text: "Représente de combien on parle de ce candidat."}, 
			"neg": {title: "les avis négatifs", text: "Représente de combien on parle en mauvais termes de ce candidat."}, 
			"pos": {title: "les avis positifs", text: "Représente de combien on parle en bons termes de ce candidat."}, 
			"none": {title: "les désinteressés", text: "Représente à quel point les français ne s'interessent pas à ce candidat."},
			"SECURITY": {color: "", title: "la Sécurité", text: "Représente l'importance du thème de la sécurité pour les français."}, 
			"EUROPE": {color: "", title: "l'Europe", text: "Représente l'importance du thème de l'Europe pour les français."},
			"ECONOMIC": {color: "", title: "l'Economie",  text: "Représente l'importance du thème de l'économie pour les français."},
			"GREEN": {color: "", title: "l'Ecologie",  text: "Représente l'importance du thème de l'écologie pour les français."},
			"IMIGRATION": {color: "", title: "l'Immigration",  text: "Représente l'importance du thème de l'immigration pour les français."},
			"SOCIAL": {color: "", title: "le Social", text: "Représente l'importance du thème du social pour les français."}
		},
		candidatColor: {
			"SARKOZY": "#09589D",
			"DUPONT_AIGNAN" : "#723E80",
			"JOLY" : "#7BA600",
			"MELENCHON" : "#FD0000",
			"MORIN" : "#7695C3",
			"BAYROU" : "#EB690B",
			"VILLEPIN" : "#A6B0E6",
			"NIHOUS": "#7CA86B",
			"LEPEN": "#969696",
			"LEPAGE": "#8EC640",
			"BOUTIN": "#0CA0D1",
			"ARTHAUD": "#C20027",
			"POUTOU": "#FF3D64",
			"CHEVENEMENT": "#B77171",
			"HOLLANDE": "#FF29B8"
		},
		positionColor: {
			"EXTREME_GAUCHE": "#FC3232",
			"GAUCHE": "#E854A8",
			"CENTRE": "#A4ED2F",
			"DROITE": "#2F2FED",
			"EXTREME_DROITE": "#969696"
		}
	},
	initialize: function(profile, options){
		this.setOptions(options);
		$('nojs').fade(0);
		Highcharts.setOptions({lang: {loading: "Chargement en cours, patientez..."}});
		new Tips(".tooltips", {className: "tips"});
		this.setVisualizationType(this.options.opinionDescription["tendance"]);
		var that = this ;
		$$('.cristal').addEvent('click', function () {
			that.future = true ;
			that.updateVisualizationDate()
			that.updatePie("future", "tendance");
		});
		this.threeMap = new ThreeMap("treeMap");
		this.threeMap.addEvent('click', function (theme) {
			that.setVisualizationType(that.options.opinionDescription[theme]);
			$$('.likeArea div').setStyle('opacity', 0.7);
			that.updateVisualizationDate(that.selectedTimestamp);
			that.selectedType = "theme."+theme
			that.updatePie(that.selectedTimestamp, that.selectedType);
			that.updateGraph();
			
		});
		this.geoDataHandler = new GeoDataHandler();
		this.selectType = $('selectType')
		this.pie = new Pie("containerPie", {
			stickyTracking: true
		});
		/*
		this.piePosition = new Pie("containerPiePosition", {
			dataLabelsEnabled: false,
			innerSize: 120
		});*/
		//svg Z-index hack.
		this.pie.addEvents({
			'mouseOver': function() {
				$('containerPieParent').setStyle('z-index', 30000);
			},
			'mouseOut': function() {
				$('containerPieParent').setStyle('z-index', 1);
			}
		});
		this.chart = new Chart("containerChart", {events: this.options.events});
		this.chart.addEvent('clickOnChart', function (date, value) {
			that.future = false ;
			that.selectedTimestamp = date ;
			that.updateVisualizationDate(date);
			that.updatePie(date);
			that.updateThemes(date);
		});
		this.chart.addEvent('clickOnLegend', function (displayName, visible) {
			var candidat = that.getCandidat(displayName);
			that.forcedCandidat[candidat.candidatName] = !visible ;
			//TODO: ca c'est vraiment bourrin :p
			that.updateGraph(that.selectedType, true);
		});
		this.chartDetails = new BarChart("containerChartDetails", 
				[{id: "tendance", title: "Tendance", text: "Le résultat de la prévision<br />des élections de 2012"}, 
				 {id: "buzz", title: "Buzz", text: "De combien on parle<br />de ce candidat."}, 
				 {id: "neg", title: "Avis Négatifs", text: "De combien on parle <br />en mauvais termes de <br />ce candidat."}, 
				 {id: "pos", title: "Avis positifs", text: "De combien on parle en<br />bons termes de ce candidat."}, 
				 {id: "none", title: "Désinteressé", text: "De combien les français <br />ne s'interessent pas à <br />ce candidat."}]);
		this.chartDetails.addEvent('click', function (type) {
			that.clickOnChartDetail(type);
		});
	},
	clickOnChartDetail: function (type) {
		$$('.likeArea div').setStyle('opacity', 0.7);
		$(type).setStyle('opacity', 1);
		this.setVisualizationType(this.options.opinionDescription[type]);
		this.updatePie(this.selectedTimestamp, type);
		this.updateGraph(type);
	},
	/**
	 * Update the visualization date and its event.
	 */
	updateVisualizationDate: function (date) {
		if(this.future) {
			$('winnerDetail').setStyle('visibility', 'hidden') ;
			$('visualizationDate').set('html', 'Voici les résultats du premier tour des élections 2012 !');
			$('visualizationEvent').set('html', 'Présidentielles !');
		} else {
			$('winnerDetail').setStyle('visibility', 'visible') ;
			var events = this.options.events[date] ;
			var txt = "";
			if(typeof(events) != "undefined") {
				for(var i=0,ii=events.length;i<ii;i++) {
					if(txt.length>0) {
						txt+=",";
					}
					txt+='<a href="http://www.google.fr/#q='+encodeURIComponent(events[i].value)+'" target="_blank">'+events[i].value+'</a>';
				}
				$('visualizationEvent').set('html', txt);
				$('visualizationEvent').fade(1);
			} else {
				$('visualizationEvent').fade(0);
			}
			var day = new Date(date) ;
			$('visualizationDate').set('html', "Données du "+day.getDate()+"/"+(day.getMonth()+1)+"/"+day.getFullYear());
		}
	},
	/**
	 * VisualizationType : value + tip
	 */
	setVisualizationType: function(obj) {
		$('visualizationType').set('html', obj.title);
		$('visualizationType').store('tip:title', obj.title);
		$('visualizationType').store('tip:text', obj.text);
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
				$('access').fade(0);
				that.candidats = {};
				for(var i =0,ii=data.candidats.length;i<ii;i++) {
					that.candidats[data.candidats[i].candidatName] = data.candidats[i] ;
				}
				that.reports = data.reports ;
				//Add picto Analyse click events
				$$('.likeArea div').addEvent('click', function () {
					that.updateVisualizationDate(that.selectedTimestamp);
					that.clickOnChartDetail(this.id);
				});
				that.firstTimestamp = that.reports[0].timestamp ;
				that.lastTimestamp = that.reports[that.reports.length-1].timestamp ;
				that.selectedTimestamp = that.lastTimestamp ;
				that.geoDataHandler.displayGeoReport(that.selectedTimestamp);
				//Main pie serie
				var dataPie = [];
				var lastReport = that.reports[that.reports.length-1] ;
				var winner ;
				var winnerValue = 0;
				var total = 0;
				for(candidat in that.candidats) {
					var value = that.candidats[candidat].report.tendance ;
					if(value>winnerValue) {
						winnerValue =value ;
						winner = that.candidats[candidat] ;
					}
					total+=value ;
					dataPie.push({parti: that.candidats[candidat].parti,
						color: that.options.candidatColor[candidat],
						partiFullName: that.candidats[candidat].partiFullName,
						name: that.candidats[candidat].displayName, 
						y:value,
						events:{
							//Click on a point on the main pie
			    			click:function () {
			    				var candidat = this.selected ? undefined : that.getCandidat(this.name) ;
			    				if(!candidat) {
			    					$('visualizationTarget').set('html', 'tous les candidats');
			    					$('visualizationTarget').setStyle('color', '');
			    				} else {
			    					$('visualizationTarget').set('html', candidat.displayName);
			    					$('visualizationTarget').setStyle('color', that.options.candidatColor[candidat.candidatName]);
			    				}
			    				that.updateGraphDetails(candidat);
			    				var candidatName = this.selected ? undefined : candidat.candidatName;
			    				that.geoDataHandler.displayGeoReport(that.selectedTimestamp, candidatName);
			    				that.updateThemes(that.selectedTimestamp, candidat);
			    				that.updateCandidatInfo(candidat);
				    		}
			    		}
					});
				}
				for(var i =0,ii=dataPie.length;i<ii;i++) {
					if(dataPie[i].y/total<0.01) {
						dataPie[i].y+= 0.01*total;
					}
				}
			    var series= [{
					type: 'pie',
					data: dataPie
				}]
				$('winner').set('html', winner.displayName+" "+(Math.round(winnerValue/total*1000)/10)+"%");
				that.pie.initChart(series);

			    //Position pie serie
			    /*
			    var dataPiePositionObj = that.getPositionsForReport(lastReport);
			    var dataPiePosition = [];
			    for(var position in dataPiePositionObj) {
			    	dataPiePosition.push({name: position, 
			    		y: dataPiePositionObj[position],
			    		color: that.options.positionColor[position]
			    	});
			    }
			    var series= [{
					type: 'pie',
					data: dataPiePosition
				}]
			    that.piePosition.initChart(series);
			    */
				that.chart.initChart(that.getSeriesForChart(), that.firstTimestamp);
				
				//details chart
				that.chartDetails.initChart(that.getSeriesForChartDetails(that.lastTimestamp));
				
				//Theme threemap
				that.updateThemes();
			}
		}).send();
	}, 
	/**
	 * Select and change a random tweet on the left picture.
	 */
	changeTweet: function () {
		var id = Math.round((this.tweets.length-1)*Math.random()) ;
		var tweet = this.tweets[id];
		var spans = $$('#candidatImage p a') ;
		var span = spans[Math.round((spans.length-1)*Math.random())];
		var fx = new Fx.Morph(span, {
		    duration: 500,
		    transition: Fx.Transitions.Quart.easeOut
		  });
		fx.start({
			opacity: 0
		}).chain(function (){
			var fonts = span.getElements('font');
			span.setProperty('href', "http://www.twitter.com/#!/"+tweet.userId)
			for(var i =0,ii=fonts.length;i++;i<ii) {
				if(tweet.value.length<i) {
					if(id == this.tweets.length) {
						tweet.value+= tweet.value[id-1];
					} else {
						tweet.value+= tweet.value[id+1];
					}
				}
				font.set('html', tweet.value[i]);
			}
			this.start({
				opacity: 1
			});
		})
		var that = this;
		this.tweetsTimer = setTimeout(function () {that.changeTweet()}, 1000+3000*Math.random());
	},
	/**
	 * Update the candidat infos, left part.
	 * 
	 */
	updateCandidatInfo: function (candidat) {
		var that = this ;
		if(typeof(candidat) != "undefined") {
			$('candidatName').set('html', candidat.displayName);
//			$('partiImage').setProperty('src', './resources/images/parti/'+candidat.parti+".jpg");
//			$('tendancy').set('html', candidat.tendancy);
//			var birth = new Date(candidat.birthday) ;
//			var birthday = birth.getDate()+"/"+(birth.getMonth()+1)+"/"+birth.getFullYear();
//			$('birthday').set('html', birthday);
			$('parti').set('href', candidat.siteUrl);
			$('parti').set('html', candidat.partiFullName);
			var fx = new Fx.Morph('help', {
			    duration: 400,
			    transition: Fx.Transitions.Quart.easeOut
			});
			fx.start({
				opacity: 0
			}).chain(function (){
				$('help').setStyle('display', 'none');
				$('candidatInfo').setStyle('display', 'block');
				$('candidatInfo').fade(1);
			});
			new Request.HTML({
				url: 'image/'+candidat.candidatName,
				onSuccess: function(responseTree, responseElements, responseHTML) {
					$('candidatImage').set('html', responseHTML);
					new Request.JSON({url: 'image/tweets/'+candidat.candidatName, 
						headers:{'Content-type':'application/json'},
						urlEncoded: false,
						method: "get",
						onSuccess: function(tweets){
							that.tweets = tweets ;
							if(that.tweetsTimer != null)
							clearTimeout(that.tweetsTimer);
							that.tweetsTimer = setTimeout(function () {that.changeTweet()}, 2000+3000*Math.random());
						}
					}).send();
				}
			}).get();
		} else {
			var fx = new Fx.Morph('candidatInfo', {
			    duration: 400,
			    transition: Fx.Transitions.Quart.easeOut
			});
			fx.start({
				opacity: 0
			}).chain(function (){
				$('candidatInfo').setStyle('display', 'none');
				$('help').setStyle('display', 'block');
				$('help').fade(1);
			});
		}
	},
	getThemesData:function (timestamp, candidat) {
		var themes, reports ;
		if(typeof(timestamp) == "undefined") {
			reports = this.reports ;
		} else {
			reports = [this.getReport(timestamp)]
		}
		themes = {};
		if(typeof(candidat) == "undefined") {
			//Moyenne de tous les candidats.
			for (var candidat in this.candidats) {
				for(theme in this.candidats[candidat].report.themes) {
					value = themes[theme] || 0 ;
					value += this.candidats[candidat].report.themes[theme] ;
					themes[theme] = value ;
				}
			}
		} else {
			if(this.future) {
				for(theme in this.candidats[candidat.candidatName].report.themes) {
					value = themes[theme] || 0 ;
					value += this.candidats[candidat.candidatName].report.themes[theme] ;
					themes[theme] = value ;
				}
			} else {
				var report = this.getReport(timestamp) ;
				for(theme in report.candidats[candidat.candidatName].themes) {
					themes[theme] = report.candidats[candidat.candidatName].themes[theme] ;
				}
			}
		}
		return themes ;
	},
	/**
	 * Update the theme threechart.
	 */
	updateThemes: function(timestamp, candidat) {
		var themes = this.getThemesData(timestamp, candidat) ;
		var values = [];
		var total = 0;
		for(theme in themes) {
			total += themes[theme] ;
		}
		for(theme in themes) {
			var title = "Place de "+this.options.opinionDescription[theme].title+" dans le discours";
			if(typeof(candidat) != "undefined") {
				title += " de "+candidat.displayName;
			} else {
				title += " des candidats"
			}
			var percent;
			total == 0 ? percent = 0 : percent=themes[theme]/total*100 ;
			var text = "Plus le carré est important, plus la préoccupation est grande.<br /> Part de ce thème dans les discours : "+Math.round(percent*10)/10+"%";
			values.push({id: theme, value: percent, title: title, text: text});
		}

		this.threeMap.draw(values);
		if(total == 0) {
			$('noTheme').setStyle('display', '');
		} else {
			$('noTheme').setStyle('display', 'none');
		}
	},
	/**
	 * Return the series given a specific type.
	 * Themes must start with theme.ID
	 */
	getSeriesForChart: function (type) {
		type = type || this.selectedType ;
		var theme = type.indexOf("theme.")!=-1;
		if(theme) {
			type = type.substring("theme.".length, type.length);
		}
		max = 0;
		var seriesIndexed = {}
		var series = []
		for(var i =0, ii=this.reports.length;i<ii;i++) {
			var data = [];
			var report = this.reports[i];
			for (var candidatName in report.candidats) {
				serie = seriesIndexed[candidatName] ;
				if(typeof(serie) == "undefined") {
					serie = {color: this.options.candidatColor[candidatName], nameBrut: candidatName, name: this.candidats[candidatName].displayName, lineWidth: 2, data: [], av:0, max: 0};
					seriesIndexed[candidatName] = serie ;
					series.push(serie);
				} 
				var value ;
				if(theme) {
					value = report.candidats[candidatName].themes[type];
				} else {
					value = report.candidats[candidatName][type] ;
				}
				value = Math.round(value*10)/10 ;
				var point = {
						x: report.timestamp, 
						y: value
				}
				
				// && this.options.events[report.timestamp].candidatName == candidatName
				var events = this.options.events[report.timestamp] ;
				var star = false ;
				if(typeof(events) != "undefined") {
					for(var e=0,ee=events.length;e<ee;e++) {
						if(events[e].candidatName == candidatName) {
							point.marker = {
								enabled: true,
								symbol: 'url(resources/images/star.png)'
							}
							star = true ;
							break;
						}
					}
				} 
				if(!star){
					point.marker = {
						enabled : false
					}
				}
				serie.data.push(point);
				serie.av += value ;
				serie.max = Math.max(value, serie.max) ;
				max = Math.max(value, max);
			}
		}
		delete seriesIndexed ;
		//Just display the graph with values. Max 7.
		var displayed = 0;
		var seriesSorted = Array.clone(series);
		seriesSorted.sort(function (s1, s2) {
			return s2.av-s1.av;
		}) ;
		var hidden = 0;
		for(i =0, ii=seriesSorted.length;i<ii;i++) {
			if(i>7 && (seriesSorted[i].max<15 || seriesSorted[i].max<3)) {
				seriesSorted[i].visible = false ;
				for(var j =0, jj=series.length;j<jj;j++) {
					if(series[j].nameBrut == seriesSorted[i].nameBrut) {
						series[j].visible = seriesSorted[i].visible
						if(!series[j].visible && (typeof(this.forcedCandidat[series[j].nameBrut]) == "undefined " || !this.forcedCandidat[series[j].nameBrut])) {
							series[j].data = [];
						}
						break;
					}
					
				}
			}
		}
		if(max>0) {
			for(i =0, ii=series.length;i<ii;i++) {
				//Report on 100% the values.
				for(d=0, dd=series[i].data.length; d<dd;d++) {
					series[i].data[d].y = Math.round(series[i].data[d].y * 100 / max*10)/10;
				}
			}
		}
		return series ;
	},
	/**
	 * Return the series given a specific type.
	 */
	getSeriesForChartDetails: function (date, type, candidat) {
		type = type || this.selectedType ;
		var theme = type.indexOf("theme.")!=-1;
		if(theme) {
			type = type.substring("theme.".length, type.length);
		}
		var report = this.getReport(date);
		var series = [{name: "", data: [] }];
		var values = {"tendance": 0, "buzz": 0, "neg": 0, "pos": 0, "none": 0};

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
			if(this.future) {
				values["tendance"] *= 15;
			}
		} //We just update for a specifc candidat 
		else {
			for(var value in values) {
				values[value] = Math.round(report.candidats[candidat.candidatName][value]*10)/10;
			}
		}
		var colors = ['#00FF00', '#FF00FF', '#FF0044', '#3749ed', '#8fed37'];
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
		type = type || this.selectedType ;
		var theme = type.indexOf("theme.")!=-1;
		if(theme) {
			type = type.substring("theme.".length, type.length);
		}
		var data = this.pie.chart.series[0].data ;
		var newData = [];
		var total =0;
		//var positionData = this.piePosition.chart.series[0].data ;
		var addToData = function(report) {
			var value ;
			if(theme) {
				value = report.themes[type] ;
			} else {
				value= report[type];
			}
			total += value ;
			newData.push(value)
		}
		if(this.future){
			var i =0;
			for(candidat in this.candidats){
				addToData(this.candidats[candidat].report);
				i++;
			}
		} else {
			var report = this.getReport(date);
			for(i =0,ii=data.length;i<ii;i++) {
				var name = this.getCandidat(data[i].name).candidatName;
				addToData(report.candidats[name]);
			}
		}
		if(total == 0) {
			total = 100;
		}
		for(var i =0,ii=data.length;i<ii;i++) {
			if(newData[i]/total<0.01) {
				data[i].update(0.01*total+newData[i]);
			} else {
				data[i].update(newData[i]);
			}
		}
	//		var dataReport = this.getPositionsForReport(report);
	//		var i=0;
	//		for(var position in dataReport) {
	//			positionData[i].update(dataReport[position]);
	//	    	i++;
	//	    }
			
	},
	/**
	 * Update the graph with new datas.
	 */
	updateGraph: function (type, doNotHide) {
		doNotHide = doNotHide || false;
		if(this.isWorking) {
			return ;
		}
		this.isWorking = true ;
		var that = this ;
		var newSeries = this.getSeriesForChart(type);
		type = type || this.selectedType ;
		var theme = type.indexOf("theme.")!=-1;
		if(theme) {
			type = type.substring("theme.".length, type.length);
		}
		var update = function () {
			//this.chart.chart.series = newSeries ;
			for(var i=0, ii=that.chart.chart.series.length;i<ii;i++){
				that.chart.chart.series[i].setData(newSeries[i].data) ;
			}
			that.chart.chart.hideLoading();
			$("containerChartType").set('html', "Représente "+that.options.opinionDescription[type].title+" des candidats.");
			that.isWorking = false ;
		}
		if(doNotHide) {
			update()
		} else {
			this.chart.chart.showLoading();
			setTimeout(function () {
				update () ;
			}, 100);
		}
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