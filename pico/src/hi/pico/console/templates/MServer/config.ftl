<h1>System Config</h1>



  <p>현재 System의 Config를 나타냅니다.</p>

  <h2>설정사항</h2>
  <p>
  <table>
      <tr>
          <th>내용</th>
          <th>설정값</th>
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