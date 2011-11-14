
<!doctype html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<meta name="description" content="Prediction elections 2012">
	<title>Prediction</title>
	<link href="./resources/styles/default.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="./resources/js/mootools.js"></script>
	<script type="text/javascript" src="./resources/js/highcharts/adapters/mootools-adapter.js"></script>
	<script type="text/javascript" src="./resources/js/highcharts/highcharts.src.js"></script>
		<!-- 1a) Optional: add a theme file -->
		<!-- <script type="text/javascript" src="./resources/js/highcharts/themes/gray.js"></script> -->
		<!-- 1b) Optional: the exporting module 
		<script type="text/javascript" src="./resources/js/highcharts/modules/exporting.js"></script>-->
</head>

<body>
	<div id="container" style="width: 800px; height: 400px; margin: 0 auto"></div>
</body> 
		<!-- 2. Add the JavaScript to initialize the chart on document ready -->
		<script type="text/javascript">
		
			var chart;
		window.addEvent('domready', function() {
			//voir pour décaler les labels, ligne 1050
				chart = new Highcharts.Chart({
					chart: {
						renderTo: 'container',
						plotBackgroundColor: null,
						plotBorderWidth: null,
						plotShadow: false
					},
					title: {
						text: 'Browser market shares at a specific website, 2010'
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
			});
				
		</script>
</html>