<!-- TODO this should be moved to a javascript file -->
<r:script>
$(function() {
	$("#poll-graph-btn").live("click", function(){
		if(!pollGraph.loaded) {
			pollGraph.hideMessages();
			pollGraph.showGraph();
		} else {
			pollGraph.showMessages();
		}
		
	});

	var pollGraph = {
		stats: {},
		loaded: false,
		refresh:false,
		getStats: function() {
			if(jQuery.isEmptyObject(pollGraph.stats)) {
				pollGraph.stats.xdata = $.map(${pollResponse}, function(a) {return a.value;});
				pollGraph.stats.data =  $.map(${pollResponse}, function(a) {return a.percent;});
			}
			return pollGraph.stats;
		},
		setStats: function() {
			var params = {};
			params.ownerId = "${ownerInstance.id}"
			var stats = {};
			$.getJSON("${createLink(controller:'poll', action:'pollStats')}", params, function(data) {
				pollGraph.refresh=true;
				pollGraph.stats.id = $.map(data, function(a) {return a.id;});
				pollGraph.stats.count = $.map(data, function(a) {return a.count;});
				pollGraph.stats.xdata = $.map(data, function(a) {return a.value;});
				pollGraph.stats.data =  $.map(data, function(a) {return a.percent;});
				$.each(pollGraph.stats.id, function(index, value){
					$("#response-"+ value).find("td.count").html(pollGraph.stats.count[index]);
					$("#response-"+ value).find("td.percent").html(pollGraph.stats.data[index] + "%");
				});
				pollGraph.showGraph();
			});
		
		},
		hideMessages: function() {
			pollGraph.loaded = true;
			var pollGraphBtn = $("#poll-graph-btn");
			pollGraphBtn.html(i18n("fmessage.hidepolldetails"));
			pollGraphBtn.addClass("hide-arrow");
			pollGraphBtn.removeClass("show-arrow");
			$("#poll-details").show();
			$('#main-list').hide();
			$("#main-list-foot").hide();
		},
		showMessages: function() {
			var pollGraphBtn = $("#poll-graph-btn");
			$("#poll-details").hide();
			pollGraphBtn.html(i18n("fmessage.showpolldetails"));
			pollGraphBtn.addClass("show-arrow");
			pollGraphBtn.removeClass("hide-arrow");
			$('#main-list').show();
			$("#main-list-foot").show();
			pollGraph.loaded = false;
		},
		showGraph: function() {
			var top = $("#main-list-head").offset().top + $("#main-list-head").height();
			var left = $("#main-list-head").offset().left;
			var h = $("#main-list-container").height();
			$("#pollGraph").css('top', top);
			$("#pollGraph").css('left', left);
			$("#pollGraph").css('height', h);
			var show = true;
			if (pollGraph.loaded || pollGraph.refresh) {
				var stats = pollGraph.getStats();
				$("#pollGraph").empty();
				var holder = "pollGraph";
				var colors = ["#40B857", "#F2202B", "#ff9600", "#D5AFC6", "#9E9E9E","#DFDFDF,"];
				pollGraph.refresh = false;
				plot3 = $.jqplot(holder, [stats.data], {
						seriesColors: colors,
					    captureRightClick: true,
					    seriesDefaults:{
					      renderer:$.jqplot.BarRenderer,
					      rendererOptions: {
					          barMargin: 15,
					          varyBarColor : true,
					          highlightMouseDown: true   
					      },
					      pointLabels: {show: true}
					    },
					    axes: {
					      xaxis: {
					          renderer: $.jqplot.CategoryAxisRenderer,
					          ticks: stats.xdata,
					      },
					      yaxis:{
								ticks:[0, 100],
								tickOptions:{formatString:'%d\%'}
							}

					    }
				  });
			}					
		}
	};

	setInterval(triggerPollGraphRefresh, 5000);
	function triggerPollGraphRefresh() {
		pollGraph.setStats();
	}
	
});
</r:script>

