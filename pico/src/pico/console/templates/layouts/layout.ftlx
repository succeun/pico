<#setting url_escaping_charset="UTF-8">
<#macro getresource path>resource?path=${path}</#macro>
<!DOCTYPE html
     PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
     "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ko" lang="ko">
<head>
<title>Administrator Console</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="<@getresource path="default.css"/>"/>
<link rel="stylesheet" type="text/css" href="<@getresource path="syntax.css"/>"/>
<script SRC="<@getresource path="jquery.js"/>" type="text/javascript"></script>
<script SRC="<@getresource path="admin.js"/>" type="text/javascript"></script>

</head>
<body>

<div class="all">
  <div class="box">
    <!-- The header -->
    <div class="header">
        <div class="title">
            <a href="../Main/main">Administrator Console</a>
        </div>
    </div>
    <div id="information">
        <div class="path">${request.selectPath!""}</div>
        <div class="loadingother" id="loadingother" style="display: none;"></div>
    </div>
    <p><!-- menu here --></p>

    <#include "sidebar.ftlx"/>

    <div class="content">

        <#include getContentPath()/>

    </div>

    <div class="clearfix"></div>


    <div class="footer">
      &copy; 2011 Powered by Pico.
    </div>

  </div>
</div>

</body>
</html>