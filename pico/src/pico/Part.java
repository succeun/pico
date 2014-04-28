package pico;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Upload�� ���������� �Ǿ��ٸ� �� Ŭ������ ���Ͽ� �� �� �ִ�.
 * 
 * @author Eun Jeong-Ho, silver@intos.biz
 * @version 2005. 5. 27.
 */
@SuppressWarnings("serial")
public class Part implements Serializable {
	private String requestName;
	private String fileSystemName;
	private String contentType;
	private long size;
	private String savePath;

	/**
	 * Upload�� �� ������ ������ ��ȯ�Ѵ�.
	 * 
	 * @param requestName ������ ���� �̸�
	 * @param fileSystemName �ý��ۿ� ����� ������ Full��
	 * @param contentType Content Type
	 * @param size ������ ������
	 * @param saveDir ������ ���ε� ����
	 */
	public Part(String requestName, String fileSystemName, String contentType, long size, String savePath) {
		this.requestName = requestName;
		this.fileSystemName = fileSystemName;
		this.contentType = contentType;
		this.size = size;
		this.savePath = savePath.replaceAll("\\\\", "/");
	}

	/**
	 * Request�� ���� ���� �̸��� ��ȯ�Ѵ�.
	 * 
	 * @return Parameter ��
	 */
	public String getName() {
		return requestName;
	}

	/**
	 * ���� ���Ͻý��ۿ� ��ϵ� ������ ���� ���������� Full���� ��ȯ�Ѵ�.
	 * 
	 * @return Full ��θ�
	 */
	public String getRelativeFileSystemName() {
		return fileSystemName.substring(savePath.length() + 1);
	}

	/**
	 * ���� ���Ͻý��ۿ� �н����� ���Ե� ������ Full ���� ��ȯ�Ѵ�.
	 * 
	 * @return Full ��θ�
	 */
	public String getFileSystemName() {
		return fileSystemName;
	}

	/**
	 * �н����� ���Ե� ����� ������ ��ȯ�Ѵ�.
	 * 
	 * @return ����� ����
	 */
	public File getFile() {
		return new File(fileSystemName);
	}

	/**
	 * Content Type�� ��ȯ�Ѵ�.
	 * 
	 * @return Content Type
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * ������ ����� ��ȯ�Ѵ�.
	 * 
	 * @return ������ ������
	 */
	public long getSize() {
		return size;
	}

	/**
	 * ����� ���ε� ������ ��ȯ�Ѵ�.
	 * 
	 * @return ����� ���ε� ����
	 */
	public String getSavePath() {
		return savePath;
	}

	/**
	 * ������ InputStream�� ��ȯ�Ѵ�.
	 * 
	 * @return InputStream
	 * @throws FileNotFoundException
	 */
	public InputStream getInputStream() throws IOException {
		return new BufferedInputStream(new FileInputStream(getFile()));
	}

	/**
	 * ����� ������ �����Ѵ�.
	 * 
	 * @return ���� ����
	 */
	public boolean delete() {
		return getFile().delete();
	}
}
