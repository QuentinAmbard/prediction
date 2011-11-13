var FiguresHandler = new Class({
	Implements : [ Options, Events ],
	options : {
		figuresId : "figures",
		figureOverId : "figureOver",
		nextIndication : "nextIndication",
		joker : "joker"
	},
	figureOver : null,
	indicationIndex : 0,
	roar : null,
	score : 0,
	figures : null,
	autocompleter : null,
	scrollFx : null,
	figureNames : [],
	userJoker: false,
	figureNumber: 1,
	initialize : function(figures, fisrtIndication, score, options) {
		this.setOptions(options);
		this.setScore(score);
		this.roar = new Roar();
		this.figureOver = $(this.options.figureOverId);
		var that = this;
		this.addNextIndication(fisrtIndication);
		this.scrollFx = new Fx.Scroll(this.options.figuresId, {
			'link' : 'cancel'
		});

		var figuresContainer = new Element('div');
		var size = figures.length;
		$(nextIndication).addEvent('click', function(e) {
			that.getNextIndication();
		});
		$("jokerButton").addEvent('click', function(e) {
			that.getJoker();
		});
		$("giveUpButton").addEvent('click', function(e) {
			that.giveUp();
		});

		$("answer").addEvent('keyup', function(e) {
			that.filterFigures(this.get("value"));
		});
		for (i = 0; i < size; i++) {
			var figure = figures[i];
			this.figureNames[this.figureNames.length] = figure.name;
			var el = new Element('div', {
				'class' : 'figure',
				html : figure.name,
				events : {
					click : function() {
						$("answer").set('value', this.get('html'));
						$("answer").focus();
					},
					mouseover : function() {
						var name = this.get('html');
						that.figureOver.set('html', name.substring(0, 1)
								+ name.length);
					}
				}
			});
			el.inject(figuresContainer);
		}
		figuresContainer.inject($(this.options.figuresId));
		this.figures = $(this.options.figuresId).getElements(".figure");
		this.autocompleter = new Autocompleter.Local('answer', this.figureNames, {
			'minLength' : 1,
			'overflow' : true,
			'selectMode' : 'pick',
			'maxChoices' : 5
		});
		$('answerForm').addEvent('submit', function () {
			that.answer();
			return false ;
		});
	},
	filterFigures : function(value) {
		var height = -this.figures[0].getSize().y;
		var match = false;
		var size = this.figures.length;
		for ( var i = 0; i < size; i++) {
			var html = this.figures[i].get('html');
			height += this.figures[i].getSize().y;
			if (html.substring(0, value.length) === value.toUpperCase()) {
				match = true;
				break;
			}
		}
		if (match) {
			this.scrollFx.start(0, height);
		}
	},
	reset : function() {
		this.figureNumber++;
		$('figureFound').set('html', this.figureNumber+"/20");
		this.indicationIndex = 0;
		for ( var i = 0; i < 5; i++) {
			$("indication" + i).set('html', "&nbsp;");
		}
		this.figures.each(function(element) {
			element.setStyle('display', 'block');
		});
		$('answer').set('value', '');
		if(this.userJoker) {
			this.autocompleter.tokens = this.figureNames;
			this.userJoker = false ;
			$('joker').set('html', '&nbsp;');
		}
	},
	setScore : function(score) {
		this.score = parseInt(score);
		$("score").set('html', score+" pts");
	},
	getScoreLost : function(newScore) {
		return this.score - parseInt(newScore);
	},
	addNextIndication : function(indication) {
		$("indication" + this.indicationIndex).set('html', indication);
		this.indicationIndex++;
	},
	getJoker : function() {
		var that = this;
		new Request.JSON({
			url : '/utomia/game/joker',
			onSuccess : function(a) {
				that.userJoker = true;
				that.roar.alert("Joker : " + a.joker + " ! ", "Vous perdez "
						+ that.getScoreLost(a.score) + " points");
				that.setScore(a.score);
				$(that.options.joker).set('html', a.joker);
				var figuresJoker = [];
				that.figures.each(function(element) {
					var html = element.get('html');
					if (html.substring(0, 1) + html.length !== a.joker) {
						element.setStyle('display', 'none');
					} else {
						figuresJoker[figuresJoker.length] = html;
					}
				});
				that.autocompleter.tokens = figuresJoker;
				$("answer").focus();
			}
		}).get();
	},
	giveUp : function() {
		var that = this;
		new Request.JSON({
			url : '/utomia/game/giveUp',
			onSuccess : function(a) {
				that.roar.alert("Vous avez pass&eacute; et perdu "
						+ that.getScoreLost(a.score) + " points !",
						"La bonne r&eacute;ponse &eacute;tait <strong>" + a.oldFigure
								+ "</strong>");
				that.setScore(a.score);
				that.reset();
				that.addNextIndication(a.indication);
			}
		}).get();
	},
	getNextIndication : function() {
		var that = this;
		new Request.JSON({
			url : '/utomia/game/nextIndication',
			onSuccess : function(a) {
				that.addNextIndication(a.indication);
				that.setScore(a.score);
			}
		}).get();
	},
	answer : function() {
		var userName = $("answer").get('value');
		if(userName !== "") {
			var that = this;
			new Request.JSON({
				url : '/utomia/game/answer',
				onSuccess : function(a) {
					if(a.correctAnswer) {
						that.reset();
						that.roar.alert('Bonne r&eacute;ponse.', 'Bravo !');
						that.addNextIndication(a.nextIndication);
						that.setScore(a.score);
					} else {
						that.roar.alert('Mauvaise réponse !', "Ce n'était pas " + userName + ". <br /> Vous perdez "+ that.getScoreLost(a.score) + " points !");
						that.setScore(a.score);
						$('answer').set('value', '');
					}
					$('answer').focus();
				}
			}).post("userName="+userName);
		}
	}, 
	endOfGame: function() {
		new MUX.Dialog({
			size: {x: '750px'},
		    loader: 'none',
		    title: 'Fin de la partie !',
		    content: new Element('p', {html: '<div class="smallLabel">Bravo ! Vous avez d&eacute;couvert les 20 personnages.</div>'+
		    	''+
		    	'<div style="float: left"><span class="smallLabel">Montrez votre score &agrave; vos amis ! </span> '+
		    	'<a href="http://twitter.com/share" class="twitter-share-button" data-url="http://www.utomia.com" data-text="test" data-count="horizontal" data-lang="fr">Tweeter</a>'+
		    	'</div>'+
		    	'<div style="float: left"><div id="fb-root"></div><fb:like href="http://www.utomia.com" send="true" layout="button_count" width="80" show_faces="true" font="arial"></fb:like></div>'+
		    	'<div class="g-plusone" data-size="medium" style="float: left" data-href="http://www.utomia.com"></div>'}),
		    buttons: [{
		        title: 'Nouvelle partie',
		        click: 'submit'
		    }],
		    onSubmit: function()
		    {
		        alert('The Job Is Done!');
		        this.close();
		    }
		});
		//twitter tag
		this.dsl('http://platform.twitter.com/widgets.js');
		this.dsl('http://apis.google.com/js/plusone.js');
		//facebook tag
		this.dsl('http://connect.facebook.net/en_US/all.js#xfbml=1');


	}, 
	dsl: function(url) {
	    var e = document.createElement('script');
	    e.async = true;
	    e.src = url;
	    e.innerHTML = "{lang: 'fr'}";
	    document.body.appendChild(e);
	}
});
