<#macro masterTemplate title="Sample" scripts=[] scriptText="" homeTab="" profileTab="" loginTab="" registerTab="">
<!DOCTYPE html>
<html lang="${.lang}">
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>${title?html}</title>
		<#include "layout/styles.ftl">
	</head>
	<body>
		<div class="container">
		
			<div class="header clearfix">
				<nav>
					<ul class="nav nav-pills pull-right">
						<li role="presentation" class="${homeTab}"><a href="/">${resources.getString("homeLink")}</a></li>
						<li role="presentation" class="${profileTab}"><a href="/account/profile">${resources.getString("profileLink")}</a></li>
						<#if (securityContext.getUserPrincipal())??>
							<li role="presentation" class=""><a href="/account/logout">${resources.getString("logoutLink")}</a></li>
						<#else>
							<li role="presentation" class="${loginTab}"><a href="/account/login">${resources.getString("loginLink")}</a></li>
							<li role="presentation" class="${registerTab}"><a href="/account/register">${resources.getString("registerLink")}</a></li>
						</#if>
						<li class="dropdown">
          					<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">${resources.getString("language")}<span class="caret"></span></a>
          					<ul class="dropdown-menu" role="menu">
            					<li>
            						<a href="/language/en">
            							<#if .lang == 'en'> 
            								<span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
            							</#if>
            							${resources.getString("languageEnglish")}
            						</a>
								</li>
            					<li>
            						<a href="/language/de">
            							<#if .lang == 'de'> 
            								<span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
            							</#if> 
            							${resources.getString("languageDeutsche")}
            						</a>
								</li>
          					</ul>
        				</li>						
					</ul>
				</nav>
				<h3 class="text-muted">Complex sample</h3>
			</div>
	
			<#nested />

			<footer class="footer">
				<p>&copy; Expanset 2015</p>
			</footer>
		</div>
		
		<#include "layout/scripts.ftl">

		<#if scriptText!?has_content>
			<script>
				$(function() {
					${scriptText}
				});
			</script>
		</#if>
	</body>
</html>
</#macro>