<h1>Memory Status</h1>



  <p>���� �����ϴ� �ý����� �޸𸮸� ��Ÿ���ϴ�.</p>

  <h2>Note</h2>
  <p>
  <ul>
  <li>���� �ý��ۿ� �������� WAS�� �޸𸮸� ��Ȳ�� �����ݴϴ�.</li>
  <li>��ü WAS�� �ƴϸ�, ������ �������� Context�� ���Ե� <b>JVM</b>�� ���Ͽ� �����ݴϴ�.</li>
  </ul>
  </p>

  <h2>��Ȳ</h2>
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