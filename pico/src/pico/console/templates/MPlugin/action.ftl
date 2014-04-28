    <#macro getlink action param="">action.ftl?id=${request.id}&action=${action}<#if param != "">&${param}</#if></#macro>
    <#macro getresource path>resource?path=${path}</#macro>

<#assign inlineTemplate = request._content_?interpret>
<@inlineTemplate />