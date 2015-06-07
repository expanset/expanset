<#import "master.ftl" as layout /> 

<@layout.masterTemplate>

<h1>Subscribe now</h1>
<form method="post" action="/subscribe">
	<p>
		<label for="email">Email</label>
		<input type="text" name="email" value="${(email?html)!}">
		<!-- Show validation error if exists. -->
		<span class="error">${(validation.getErrors('email'))!}</span>
	</p>
	<p>
		<button type="submit">Submit</button>
	</p>
</form>

</@layout.masterTemplate>
