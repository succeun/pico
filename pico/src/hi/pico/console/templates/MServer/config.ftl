<h1>System Config</h1>



  <p>���� System�� Config�� ��Ÿ���ϴ�.</p>

  <h2>��������</h2>
  <p>
  <table>
      <tr>
          <th>����</th>
          <th>������</th>
      </tr>
      <#assign keys = request.props?keys>
      <#list keys as key>
      <tr>
          <td>${key}</td>
          <td>${request.props[key]?replace(",", ", ")?replace(";", "; ")}</td>
      </tr>
      </#list>
  </table>
  </p>