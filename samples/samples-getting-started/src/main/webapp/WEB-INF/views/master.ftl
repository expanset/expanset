<#macro masterTemplate>

<!DOCTYPE html>
<html lang="${.lang}">
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<!-- Use variable 'siteTitle' added in MasterTemplatePopulator. -->
		<title>${(siteTitle?html)!}</title>
		<link href="/assets/css/site.css" rel="stylesheet">
	</head>
	<body>
	
		<#nested />
		
	</body>
</html>

</#macro>