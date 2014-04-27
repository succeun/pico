<h1>Plugin - <b>${request.id} (${request.action})</b></h1>

<p>콘트롤의 호출 횟수를 파이챠트로 보여줍니다.</p>

<h2>Help Information</h2>
<p>각각의 콘트롤의 호출 횟수를 보여줍니다.</p>

<h2>Executive Overview</h2>
<p>
    <#macro gettags list><#list list as entry>${entry.name}<#if entry_has_next>|</#if></#list></#macro>
    <#macro getdata list><#list list as entry>${entry.count}<#if entry_has_next>|</#if></#list></#macro>

    <script type="text/javascript" language="javascript">
        function showFlashObject(id, width, height, movie, flashvars, style) {
            var s = '<object id="' + id + '" classid="clsid:D27CDB6E-AE6D-11CF-96B8-444553540000" width="' + width + '" height="' + height + '" style="' + style + '"><param name="movie" value="' + movie + '"/><param name="menu" value="false"/><param name="quality" value="high"/><param name="allowScriptAccess" value="sameDomain"/><param name="play" value="true"/><param name="wmode" value="transparent"/><param name="flashvars" value="' + flashvars + '"/><embed swLiveConnect="true" flashvars="' + flashvars + '" src="' + movie + '" quality="high" bgcolor="" wmode="transparent" width="' + width + '" height="' + height + '" name="' + id + '" align="middle" allowScriptAccess="sameDomain" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer"></embed></object>';
            document.write(s);
        }
    </script>

    <div class="left">
        <div><b>${request.totallabel}</b></div>
        <div>
            <script type="text/javascript" language="javascript">
                showFlashObject('pizza', 190, 160,
                        '<@getresource path="/hi/pico/console/plugins/pizza.swf"/>',
                        'gData=<@getdata list=request.sheet.total/>' +
                        '&gTags=<@gettags list=request.sheet.total/>' +
                        '&gAutoStart=highest', '');
            </script>
        </div>
    </div>
    <#assign keys = request.sheet?keys>
    <#list keys as key>
    <#if key != "total">
    <div class="left">
        <div>${key}</div>
        <div>
            <script type="text/javascript" language="javascript">
                showFlashObject('pizza', 190, 160,
                        '<@getresource path="/hi/pico/console/plugins/pizza.swf"/>',
                        'gData=<@getdata list=request.sheet[key]/>' +
                        '&gTags=<@gettags list=request.sheet[key]/>' +
                        '&gAutoStart=highest', '');
            </script>
        </div>
    </div>
    </#if>
    </#list>
</p>
