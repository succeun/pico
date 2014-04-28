package pico;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Upload가 정상적으로 되었다면 이 클래스를 통하여 알 수 있다.
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
	 * Upload가 된 파일을 정보를 반환한다.
	 * 
	 * @param requestName 파일의 원래 이름
	 * @param fileSystemName 시스템에 저장된 파일의 Full명
	 * @param contentType Content Type
	 * @param size 파일의 싸이즈
	 * @param saveDir 저장할 업로드 폴더
	 */
	public Part(String requestName, String fileSystemName, String contentType, long size, String savePath) {
		this.requestName = requestName;
		this.fileSystemName = fileSystemName;
		this.contentType = contentType;
		this.size = size;
		this.savePath = savePath.replaceAll("\\\\", "/");
	}

	/**
	 * Request의 파일 원래 이름을 반환한다.
	 * 
	 * @return Parameter 값
	 */
	public String getName() {
		return requestName;
	}

	/**
	 * 실제 파일시스템에 기록된 파일의 기준 폴더이하의 Full명을 반환한다.
	 * 
	 * @return Full 경로명
	 */
	public String getRelativeFileSystemName() {
		return fileSystemName.substring(savePath.length() + 1);
	}

	/**
	 * 실제 파일시스템에 패스까지 포함된 파일의 Full 명을 반환한다.
	 * 
	 * @return Full 경로명
	 */
	public String getFileSystemName() {
		return fileSystemName;
	}

	/**
	 * 패스까지 포함된 저장된 파일을 반환한다.
	 * 
	 * @return 저장된 파일
	 */
	public File getFile() {
		return new File(fileSystemName);
	}

	/**
	 * Content Type을 반환한다.
	 * 
	 * @return Content Type
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * 파일의 싸이즈를 반환한다.
	 * 
	 * @return 파일의 싸이즈
	 */
	public long getSize() {
		return size;
	}

	/**
	 * 저장된 업로드 폴더를 반환한다.
	 * 
	 * @return 저장된 업로드 폴더
	 */
	public String getSavePath() {
		return savePath;
	}

	/**
	 * 파일의 InputStream을 반환한다.
	 * 
	 * @return InputStream
	 * @throws FileNotFoundException
	 */
	public InputStream getInputStream() throws IOException {
		return new BufferedInputStream(new FileInputStream(getFile()));
	}

	/**
	 * 저장된 파일을 삭제한다.
	 * 
	 * @return 삭제 여부
	 */
	public boolean delete() {
		return getFile().delete();
	}
}
