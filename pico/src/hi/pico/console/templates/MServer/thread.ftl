<h1>Thread Status</h1>



  <p>���� �����ϴ� �������� ����� ��Ÿ���ϴ�.</p>

  <h2>Note</h2>
  <p>
  <ul>
  <li>���� �ý��ۿ� �������� �������� ��Ȳ�� �����ݴϴ�.</li>
  <li>�����尡 ���� <b>Runnable �������� Wait ��������</b> ���δ� �� �� �����ϴ�.</li>
  </ul>
  </p>

  <h2>���</h2>
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