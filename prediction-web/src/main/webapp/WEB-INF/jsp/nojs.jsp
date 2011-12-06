<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<!doctype html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1" />
	<meta name="description" content="Prediction elections 2012">
	<title>Prediction</title>
	<link href="./resources/styles/default.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="./resources/js/tween.js"></script>
	<script type="text/javascript" src="./resources/js/mootools.js"></script>
	<script type="text/javascript" src="./resources/js/mootools-more.js"></script>
	<script type="text/javascript" src="./resources/js/prediction/dataHandler.js"></script>
	<script type="text/javascript" src="./resources/js/prediction/geoDataHandler.js"></script>
	<script type="text/javascript" src="./resources/js/prediction/utils.js"></script>
	<script type="text/javascript" src="./resources/js/prediction/threemap.js"></script>
	<script type="text/javascript" src="./resources/js/prediction/pie.js"></script>
	<script type="text/javascript" src="./resources/js/prediction/chart.js"></script>
	<script type="text/javascript" src="./resources/js/prediction/barChart.js"></script>
	<script type="text/javascript" src="./resources/js/highcharts/adapters/mootools-adapter.src.js"></script>
	<script type="text/javascript" src="./resources/js/highcharts/highcharts.src.js"></script>
		<!-- 1a) Optional: add a theme file -->
		<script type="text/javascript" src="./resources/js/highcharts/themes/gray.js"></script>
		<!-- 1b) Optional: the exporting module 
		<script type="text/javascript" src="./resources/js/highcharts/modules/exporting.js"></script>-->
</head>
<body>
<header style="margin: auto; width: 200px">
		<a title="retourner sur la page d'accueil" href="#" id="logo"></a>
		<div id="blue"></div>
		<div id="red"></div>
</header>
	<div id="container">
		<div id="contentNoJs">
			<h1>Si vous avez des problèmes avec l'affichage graphique, voici une version texte simplifié des données :</h1>
			<h2>Informations sur les candidats :</h2>
			<h3>Légende :</h3>
			la tendance : Représente le résultat prévisionnel des élections de 2012, avec les données du web<br />
			le buzz : Représente de combien on parle de ce candidat<br />
			les avis négatifs : Représente de combien on parle en mauvais termes de ce candidat<br />
			les avis positifs : Représente de combien on parle en bon termes de ce candidat<br />
			les désinteressés : Représente à quel point les français ne s'interessent pas à ce candidat<br /><br /><br />
			
			<TABLE>
			  <CAPTION>Données pour les elections de 2012</CAPTION>
			  <TR>
				 <TH>Date</TH>
				 <TH>Candidat</TH>
				 <TH>Tendance</TH>
				 <TH>Buzz</TH>
				 <TH>Avis négatifs</TH>
				 <TH>Avis positifs</TH>
				 <TH>Désinteressés</TH>
			  </TR>
			 <c:forEach var='report' items='${reports}'>
					<c:forEach var='candidat' items='${candidats}'>
					 <TH><c:out value='${report.timestamp}'/></TH>
					 <TD><c:out value='${candidat.candidatName}'/></TD>
					 <TD><c:out value='${report.candidats[candidat.candidatName].tendance}'/></TD>
					 <TD><c:out value='${report.candidats[candidat.candidatName].buzz}'/></TD>
					 <TD><c:out value='${report.candidats[candidat.candidatName].neg}'/></TD>
					 <TD><c:out value='${report.candidats[candidat.candidatName].pos}'/></TD>
					 <TD><c:out value='${report.candidats[candidat.candidatName].none}'/></TD>					 
				 </TR>
				 </c:forEach>
			</c:forEach>
			</TABLE> 
			<h2>Informations sur les themes, par candidat :</h2>
			<TABLE>
			  <CAPTION>Données pour les elections de 2012</CAPTION>
			  <TR>
				 <TH>Date</TH>
				 <TH>Candidat</TH>
				 <TH>Sécurité</TH>
				 <TH>Europe</TH>
				 <TH>economie</TH>
				 <TH>Ecologie</TH>
				 <TH>Immigration</TH>
				 <TH>Social</TH>
			  </TR>
			 <c:forEach var='report' items='${reports}'>
					<c:forEach var='candidat' items='${candidats}'>
						 <TH><c:out value='${report.timestamp}'/></TH>
						 <TD><c:out value='${candidat.candidatName}'/></TD>
						<c:forEach var='theme' items='${themes}'>
							 <TD><c:out value='${report.candidats[candidat.candidatName].themes[theme]}'/></TD>
						 </c:forEach>
				 </TR>
				 </c:forEach>
			</c:forEach>
			</TABLE>
		</div>
	</div>
</body> 
<script type="text/javascript">
TWEEN.start();
window.addEvent('domready', function() {
	var dataHandler = new DataHandler ();
	dataHandler.getData();
});
</script>
</html>