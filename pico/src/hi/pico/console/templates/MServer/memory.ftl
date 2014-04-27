<h1>Memory Status</h1>



  <p>현재 동작하는 시스템의 메모리를 나타냅니다.</p>

  <h2>Note</h2>
  <p>
  <ul>
  <li>현재 시스템에 동작중인 WAS의 메모리를 현황을 보여줍니다.</li>
  <li>전체 WAS는 아니며, 엔진이 구동중인 Context가 포함된 <b>JVM</b>에 한하여 보여줍니다.</li>
  </ul>
  </p>

  <h2>현황</h2>
  <p>
  <table>
      <tr>
          <th>Name</th>
          <th>Size</th>
      </tr>
      <tr>
          <td>Max</td>
          <td><div class="greenbar" style="width: 70%"></div><div class="right">${request.memory.max} Bytes</div></td>
      </tr>
      <tr>
          <td>Total</td>
          <td><div class="redbar" style="width: ${request.memory.total * 70 / request.memory.max}%"></div><div class="right">${request.memory.total} Bytes</div></td>
      </tr>
      <tr>
          <td>Free</td>
          <td><div class="yellowbar" style="width: ${request.memory.free * 70 / request.memory.max}%"></div><div class="right">${request.memory.free} Bytes</div></td>
      </tr>
      <tr>
          <td>Used</td>
          <td><div class="bluebar" style="width: ${request.memory.used * 70 / request.memory.max}%"></div><div class="right">${request.memory.used} Bytes</div></td>
      </tr>
  </table>
  </p>
  <p>
    <input type="button" value="Gabage Collector" onclick="javascript:location.href = 'gc';"/>
  </p>