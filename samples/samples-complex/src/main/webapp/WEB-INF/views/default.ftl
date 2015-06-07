<#import "master.ftl" as layout /> 
<@layout.masterTemplate title=resources.getString("siteTitle") scripts="chart.js" scriptText="$.initDefaultPage();" homeTab="active">
<div class="jumbotron">
	<#if (securityContext.getUserPrincipal())??>
		<h1>Hello, ${(securityContext.getUserPrincipal().getName())?html}</h1>
	<#else>
		<h1>Register, please!</h1>
	</#if>	
	<p class="lead">
		Click button below, please!
	</p>
	<p>
		<button type="button" class="btn btn-lg btn-success" id="start-operation" data-loading-text="Loading stock quotes...">
			Load Google stock quotes
		</button>
	</p>
</div>
<div>
	<div class="alert alert-danger alert-dismissible hidden" id="request-error" role="alert">Request error</div>
	<div class="hidden" id="chart-place">
		<canvas id="myChart" width="800" height="400" class="center-block"></canvas>
	</div>
</div>
</@layout.masterTemplate>
