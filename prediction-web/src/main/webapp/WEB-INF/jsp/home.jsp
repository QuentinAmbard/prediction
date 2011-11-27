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
	<script type="text/javascript" src="./resources/js/prediction/barChart.js"></script>
	<script type="text/javascript" src="./resources/js/highcharts/adapters/mootools-adapter.js"></script>
	<script type="text/javascript" src="./resources/js/highcharts/highcharts.src.js"></script>
		<!-- 1a) Optional: add a theme file -->
		<script type="text/javascript" src="./resources/js/highcharts/themes/gray.js"></script>
		<!-- 1b) Optional: the exporting module 
		<script type="text/javascript" src="./resources/js/highcharts/modules/exporting.js"></script>-->
</head>
<body>
	<select id="selectType" style="z-index: 5000">
		<option value="tendance">Tendance</option>
		<option value="buzz">Buzz</option>
		<option value="pos">Avis positifs</option>
		<option value="neg">Avis négatifs</option>
		<option value="none">Désinteressés</option>
	</select>
	
	<div id="containerPie" style="position: absolute; width: 800px; height: 400px; margin: 0 auto"></div>
	<div id="containerPiePosition" style="position: absolute; top: 145px; left: 330px; width: 140px; height: 145px; margin: 0 auto"></div>
	<div id="containerChart" style="position: absolute; top: 300px; left: 200px; width: 1000px; height: 400px; margin: 0 auto"></div>
	<div id="containerChartDetails" style="position: absolute; top: 400px;width: 200px; height: 200px; margin: 0 auto"></div>

	
</body> 
<script type="text/javascript">
window.addEvent('domready', function() {
	var dataHandler = new DataHandler ();
	dataHandler.getData();
});
</script>
</html>