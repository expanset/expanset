<#import "master.ftl" as layout /> 
<@layout.masterTemplate title=resources.getString("siteTitle") loginTab="active">
<h2>${resources.getString("authPage")}</h2>
<div class="well well-lg">
	<#if (validation.hasErrors('authenticate'))??>
	<div class="alert alert-warning" role="alert">${validation.getErrors('authenticate')}</div>
	</#if>
	<form class="form-horizontal" method="post">
		<input type="hidden" name="returnUrl" value="${(returnUrl?html)!}">
		<div class="form-group ${(validation.hasErrors('login')?string('has-error',''))!} ">
			<label for="login" class="col-sm-2 control-label">${resources.getString('loginField')}</label>
			<div class="col-sm-6">
				<input type="text" name="login" value="${(login?html)!}" class="form-control" placeholder="${resources.getString('loginField')}">
				<span class="help-block">${(validation.getErrors('login'))!}</span>
			</div>
		</div>
		<div class="form-group ${(validation.hasErrors('password')?string('has-error',''))!}">
			<label for="password" class="col-sm-2 control-label">${resources.getString('passwordField')}</label>
			<div class="col-sm-6">
				<input type="password" name="password" class="form-control" placeholder="${resources.getString('passwordField')}">
				<span class="help-block">${(validation.getErrors('password'))!}</span>
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-offset-2 col-sm-6">
				<button type="submit" class="btn btn-default">${resources.getString('loginButton')}</button>
			</div>
		</div>
	</form>
</div>
</@layout.masterTemplate>
