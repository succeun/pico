<#setting url_escaping_charset="UTF-8">
<#macro getresource path>resource?path=${path}</#macro>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=euc-kr" />
  <title>Pico Console</title>
  <style>
	html, body { 
	  height: 100%; /* body�� html�� ���̸� 100% �� ���� */ 
	  margin: 0; 
	  padding: 0; 
	} 
	#container { 
	  width: 440px; /* ���̳� ���̰� �����ؾ� �մϴ�. */ 
	  height: 100px; /* ���̳� ���̰� �����ؾ� �մϴ�. */ 
	  position: absolute; 
	  top: 50%; /* ȭ���� �߾ӿ� ��ġ */ 
	  left: 50%; /* ȭ���� �߾ӿ� ��ġ */ 
	  margin: -100px 0 0 -220px; /* ������ ���ݰ� �ʺ��� ���� ��ŭ margin �� �̿��Ͽ� ���� �� �ݴϴ�. */ 
	  border: 0px; 
	}
	#login{
	    padding-top:20px;
	    padding-bottom:20px;
	    border:6px solid #666;
	    background-color:#fff;
	    width:440px;
	}
	
	#login h2{
	    padding-left:30px;
	    font:18px dotum;
	    font-weight:bold;
	    padding-bottom:10px;
	    border-bottom:1px solid #ccc;
	    letter-spacing:-1px;
	}
	
	#login .loginbox{
	    padding-left:10px;
	    margin:0 auto;
	    width:420px;
	}
	 #login .loginbox input{
	    width:150px;
	    height:30px;
	    padding-top:5px;
	    padding-left:3px;
	    font:14px Verdana;
	    font-weight:bold;
	}
	
	#login .loginbox input.submit {
	    background-color:#a39883;
	    border:1px solid #817459;
	    color:#fff;
	    font-size:14px;
	    font-weight:bold;
	    width:100px;
	    height:30px;
	    margin-left:7px;
	}
	
	 #login .loginbox .warn {
	    padding-top:10px;
	    padding-left:50px;
	    color:#FF0000;
	    font-size:14px;
	    font-weight:bold;
	    margin-left:7px;
	}
	</style>
  <script SRC="<@getresource path="rsa.js"/>" type="text/javascript"></script>
</head>
<body onload="document.loginForm.password.focus();">
<div id="container">
	<form name="loginForm" method="post" action="loginok" onsubmit="return submitEncrypted("${request.modulus}", "${request.exponent}", this);">
	  <div id="login">
	    <input type="hidden" name="url" value="${request.url!}"/>
	    <h2>Administrator Console Login</h2>
	    <div class="loginbox">
	      <table width="100%" border="0" cellpadding="2" cellspacing="0">
	        <tr>
	          <td style="font-size:14px; text-align:right; padding-right:10px;">��й�ȣ</td>
	          <td><input type="password" name="password" maxlength="64" onkeydown="if (event.keyCode == 13) document.forms[0].submit()" tabindex="3" /></td>
	          <td><input type="submit" value="�α���" class="submit" /></td>
	        </tr>
	        <tr>
	          <td colspan="3"><div class="warn">${request.message}</div></td>
	        </tr>
	      </table>
		</div>
	  </div>
	</form>
</div>
</body>
</html>