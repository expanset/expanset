<script src="/assets/js/jquery.js"></script>
<script src="/assets/js/bootstrap.js"></script>
<#if scripts?is_sequence>
  		<#list scripts as script>
		<script src="/assets/js/${script}"></script>
  		</#list>
 	<#else>
	<script src="/assets/js/${scripts}"></script>
</#if>
<script src="/assets/js/site.js"></script>