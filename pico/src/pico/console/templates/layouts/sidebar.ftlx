<#macro createcategory category>
    <ul>
      <#if category.url??>
        <#if category.select>
          <li class="selected"><a href="${category.url}"<#if category.popup> target="_blank"</#if>>${category.name?replace("\n", "<br/>")}</a></li>
        <#else>
          <li><a href="${category.url}"<#if category.popup> target="_blank"</#if>>${category.name?replace("\n", "<br/>")}</a></li>
        </#if>
      <#else>
        <li>${category.name?replace("\n", "<br/>")}</li>
      </#if>
      <#list category.children as subcategory>
        <@createcategory category=subcategory/>
      </#list>
    </ul>
</#macro>

<div class="sidebar">
<#if request.sidebar??>    
  <#list request.sidebar.children as category>
  <#if category.url??>
    <h1><a href="${category.url}"<#if category.popup> target="_blank"</#if>>${category.name}</a></h1>
  <#else>
    <h1>${category.name?replace("\n", "<br/>")}</h1>
  </#if>
  <div class="p2">
  <#list category.children as subcategory>
    <@createcategory category=subcategory/>
  </#list>
  </div>
  </#list>
</#if>
</div>