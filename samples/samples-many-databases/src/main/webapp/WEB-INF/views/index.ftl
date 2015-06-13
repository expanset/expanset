<#import "master.ftl" as layout /> 

<@layout.masterTemplate>

<h1>Hello from the ${helloString} database!</h1>

<a href="${uriPrefix}">Go to the first database</a>
<br/>
<a href="${uriPrefix}/db2">Go to the second database</a>

</@layout.masterTemplate>
