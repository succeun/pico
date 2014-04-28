<h1>Thread Status</h1>



  <p>현재 동작하는 쓰레드의 목록을 나타냅니다.</p>

  <h2>Note</h2>
  <p>
  <ul>
  <li>현재 시스템에 동작중인 쓰레드의 현황을 보여줍니다.</li>
  <li>쓰레드가 현재 <b>Runnable 상태인지 Wait 상태인지</b> 여부는 알 수 없습니다.</li>
  </ul>
  </p>

  <h2>목록</h2>
  <p>
  <ul>
  <li><b>ThreadGroup Name :</b> ${request.threadgroup.name}</li>
  <li><b>Thread Count :</b> ${request.threadgroup.entries?size}</li>
  </ul>
  <p>

  <p>
  <table>
      <tr>
          <th>No</th>
          <th>Name</th>
          <th>Deaemon</th>
          <th>Alive</th>
          <th>Priority</th>
      </tr>
      <#list request.threadgroup.entries as entry>
      <tr>
          <td>${entry_index + 1}</td>
          <td>${entry.name}</td>
          <td>${entry.daemon}</td>
          <td>${entry.alive}</td>
          <td>${entry.priority}</td>
      </tr>
      </#list>
  </table>
  </p>