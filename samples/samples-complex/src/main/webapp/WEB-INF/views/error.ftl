<#import "master.ftl" as layout /> 
<@layout.masterTemplate title=resources.getString("siteTitle")>
<div class="jumbotron">
	<h1>${status}</h1>
	<p class="lead">Sorry.</p>
	<p><a class="btn btn-lg btn-success" href="/" role="button">${resources.getString("goStart")}</a></p>
</div>
</@layout.masterTemplate>
