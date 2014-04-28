<h1>Job List</h1>



  <p>현재 동작하는 Cron의 목록을 나타냅니다.</p>

  <h2>Note</h2>
  <p>
  <ul>
  <li>현재 시스템에 동작중인 Cron의 현황을 보여줍니다.</li>
  </ul>
  </p>

  <h2>목록</h2>
  
  <p>
  <table>
      <tr>
          <th>No</th>
          <th>Job Group & Name<br/>Trigger Group & Name<br/>Schedule Expression<br/>Schedule Description</th>
          <th>Last Run</th>
          <th>Next Run</th>
          <th>Paused</th>
          <th>Running</th>
          <th>Action</th>
      </tr>
      <#list request.items as item>
      <tr>
          <td>${item_index + 1}</td>
          <td>${item.jobGroup} & ${item.jobName}<br/>${item.triggerGroup} & ${item.triggerName}
          <br/>${item.triggerScheduleExpression}<br/>${item.description}</td>
          <#if item.trigger.previousFireTime??>
          	<td>${item.trigger.previousFireTime?string("yyyy-MM-dd HH:mm:ss")}</td>
          <#else>
          	<td>&nbsp;</td>
          </#if>
          <#if item.trigger.nextFireTime??>
          	<td>${item.trigger.nextFireTime?string("yyyy-MM-dd HH:mm:ss")}</td>
          <#else>
          	<td>&nbsp;</td>
          </#if>
          <td>${item.paused?string}</td>
          <td>${item.running?string}</td>
          <td>
          <#if !item.running>
          	<input type="button" value="Run It Now" onclick="javascript:requestPage('run?jobGroup=${item.jobGroup?url}&jobName=${item.jobName?url}');"/>
          </#if>
          <#if item.paused>
          	<br/><input type="button" value="Resume Schedule" onclick="javascript:requestPage('resume?triggerGroup=${item.triggerGroup?url}&triggerName=${item.triggerName?url}');"/>
          <#else>
          	<br/><input type="button" value="Pause Schedule" onclick="javascript:requestPage('pause?triggerGroup=${item.triggerGroup?url}&triggerName=${item.triggerName?url}');"/>
          </#if>
          	<br/><input type="button" value="Unschedule" onclick="javascript:requestPage('unschedule?triggerGroup=${item.triggerGroup?url}&triggerName=${item.triggerName?url}');"/>
          </td>
      </tr>
      </#list>
  </table>
  </p>