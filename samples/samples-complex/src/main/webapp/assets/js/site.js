$(function() {
	$.initDefaultPage = function() {
		var chart = null;
		
		$('#start-operation').click(function() {
			var that = $(this);
			
			that.button('loading');
			$('#request-error').addClass('hidden');
			
            $.get('/stock-quotes/GOOG')
	            .always(function () {
	            	that.button('reset');
	            })
	            .success(function (data) {
	    			$('#chart-place').removeClass('hidden');
	    			
	            	if(chart == null) {
		            	var ctx = $("#myChart")[0].getContext("2d");
		            	chart = new Chart(ctx);
	            	}
	            	
	            	var chartData = {
	            		    labels: [],
	            		    datasets: [
	            		        {
	            		        	label: "Stock quotes",
	            		            fillColor: "rgba(151,187,205,0.2)",
	            		            strokeColor: "rgba(151,187,205,1)",
	            		            pointColor: "rgba(151,187,205,1)",
	            		            pointStrokeColor: "#fff",
	            		            pointHighlightFill: "#fff",
	            		            pointHighlightStroke: "rgba(151,187,205,1)",
	            		            data: []
	            		        }
	            		    ]
	            		};
	            	
	            	for(var i = data.length > 20 ? data.length - 20 : 0; i < data.length; i++) {
	            		chartData.labels.push(data[i].date);
	            		chartData.datasets[0].data.push(data[i].value);
	            	}
	            	chart.Line(chartData);
	            })
	            .error(function (response) {
	            	$('#request-error').removeClass('hidden');
	            });

			return false;
		});
	}
});