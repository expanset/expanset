<#import "master.ftl" as layout /> 
<@layout.masterTemplate title=resources.getString("siteTitle") profileTab="active">
<h2>${resources.getString("profilePage")}</h2>
<div class="well well-lg">
	<form class="form-horizontal" method="post">
		<div class="form-group ${(validation.hasErrors('password')?string('has-error',''))!}">
			<label for="password" class="col-sm-2 control-label">${resources.getString('passwordField')}</label>
			<div class="col-sm-6">
				<input type="password" name="password" class="form-control" placeholder="${resources.getString('passwordField')}">
				<span class="help-block">${(validation.getErrors('password'))!}</span>
			</div>
		</div>
		<div class="form-group ${(validation.hasErrors('passwordMatch')?string('has-error',''))!}">
			<label for="confirmPassword" class="col-sm-2 control-label">${resources.getString('confirmPasswordField')}</label>
			<div class="col-sm-6">
				<input type="password" name="confirmPassword" class="form-control" placeholder="${resources.getString('confirmPasswordField')}">
				<span class="help-block">${(validation.getErrors('passwordMatch'))!}</span>
			</div>
		</div>
		<div class="form-group">
			<label for="stockQuoteDate" class="col-sm-2 control-label">${resources.getString('stockQuoteDateField')}</label>
			<div class="col-sm-6">
				<p class="form-control-static">${(siteUser.getStockQuoteDate())?datetime?string.full}</p>
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-offset-2 col-sm-6">
				<button type="submit" class="btn btn-default">${resources.getString('saveButton')}</button>
			</div>
		</div>
	</form>
</div>
<div>
	${(siteUser.getStockQuoteDate())?string('dd.MM.yyyy HH:mm:ss')}
</div>
</@layout.masterTemplate>
