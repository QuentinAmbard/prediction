<!doctype html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
	<meta name="description" content="Prediction elections 2012">
	<title>Prediction</title>
	<link href="./resources/styles/default.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="./resources/js/mootools.js"></script>
	<script type="text/javascript" src="./resources/js/prediction/dataHandler.js"></script>
	<script type="text/javascript" src="./resources/js/prediction/pie.js"></script>
	<script type="text/javascript" src="./resources/js/prediction/chart.js"></script>
	<script type="text/javascript" src="./resources/js/highcharts/adapters/mootools-adapter.js"></script>
	<script type="text/javascript" src="./resources/js/highcharts/highcharts.src.js"></script>
		<!-- 1a) Optional: add a theme file -->
		<!-- <script type="text/javascript" src="./resources/js/highcharts/themes/gray.js"></script> -->
		<!-- 1b) Optional: the exporting module 
		<script type="text/javascript" src="./resources/js/highcharts/modules/exporting.js"></script>-->
</head>

<body>
	<div id="containerPie" style="width: 800px; height: 400px; margin: 0 auto"></div>
	<div id="containerChart" style="width: 1000px; height: 400px; margin: 0 auto"></div>
</body> 
		<!-- 2. Add the JavaScript to initialize the chart on document ready -->
		<script type="text/javascript">
		window.addEvent('domready', function() {
			var dataHandler = new DataHandler ();
			dataHandler.getData();
		});
			//voir pour décaler les labels, ligne 1050

				
		</script>
</html>