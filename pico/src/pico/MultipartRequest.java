package pico;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.io.FileCleaningTracker;

import pico.engine.util.FileRenamePolicy;
import pico.engine.util.RenamePolicy;

public class MultipartRequest extends HttpServletRequestWrapper {
	/** 기본적인 업로드 허용 확장자 */
	public static final String[] DEFAULT_UPLOAD_ALLOWED_EXTS = new String[] { "jpg", "jpeg", "gif", "bmp", "png",
			"psd", "swf", "tar", "gz", "tgz", "alz", "zip", "rar", "ace", "arj", "jar", "exe", "avi", "ppt", "xls",
			"doc" };
	/** 기본적인 업로드되는 모든 화일의 총 허용 최대 크기 (req.getContentLength()) */
	public static final long DEFAULT_UPLOAD_MAX_SIZE = -1;
	/** 인메모리에서 디스크로 넘어가는 문턱의 크기 */
	public static final int DEFAULT_SIZE_THRESHOLD = DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD; // 10Kb
	private RenamePolicy renamePolicy = new FileRenamePolicy();
	/** Request가 Multipart 인지 여부 */
	private boolean isMultipart;
	private int sizeThreshold = DEFAULT_SIZE_THRESHOLD;
	/** FieldName에 의한 @see org.apache.commons.fileupload.FileItem 의 Map */
	private Map<String, FileItem> fileItems = null;
	private Map<String, Part> savedParts = null;
	private String encoding;
	private String defaultSavePath;
	
	public MultipartRequest(ServletContext context, HttpServletRequest req) throws FileUploadException {
		this(context, req, null, DEFAULT_UPLOAD_MAX_SIZE, DEFAULT_SIZE_THRESHOLD, System.getProperty("java.io.tmpdir"));
	}

	/**
	 * Multipart 여부인지를 판별하여 처리한다.
	 * 
	 * @param req
	 */
	public MultipartRequest(ServletContext context, HttpServletRequest req, String defaultSavePath) throws FileUploadException {
		this(context, req, defaultSavePath, DEFAULT_UPLOAD_MAX_SIZE, DEFAULT_SIZE_THRESHOLD, System.getProperty("java.io.tmpdir"));
	}

	/**
	 * Multipart 여부인지를 판별하여 처리한다.
	 * 
	 * @param req
	 * @param max 허용하는 최대 업로드 크기
	 */
	public MultipartRequest(ServletContext context, HttpServletRequest req, String defaultSavePath, long max, int sizeThreshold, String tempDir)
			throws FileUploadException {
		super(req);
		this.defaultSavePath = defaultSavePath;
		isMultipart = ServletFileUpload.isMultipartContent(new ServletRequestContext(req));

		if (isMultipart)
			initUploadItemList(context, req, sizeThreshold, max, new File(tempDir));
	}

	/**
	 * MultiPart를 초기화한다.
	 * 
	 * @param req HttpServletRequest
	 * @param sizeThreshold Memory에서 Disk로 저장이 넘어갈 Threshold
	 * @param maxSize 최대허용 크기
	 * @param tempDir Disk로 저장될 임시 저장소
	 * @throws FileUploadException
	 */
	@SuppressWarnings("unchecked")
	private void initUploadItemList(ServletContext context, HttpServletRequest req, int sizeThreshold, long maxSize,
			File tempDir) throws FileUploadException {
		this.sizeThreshold = sizeThreshold;
		DiskFileItemFactory factory = new DiskFileItemFactory(this.sizeThreshold, tempDir);

		FileCleaningTracker fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(context);
		factory.setFileCleaningTracker(fileCleaningTracker);

		ServletFileUpload upload = new ServletFileUpload(factory);

		if (maxSize != -1)
			upload.setSizeMax(maxSize);

		// 넘어온 페이지가 UTF-8 일경우처럼, 파일이 깨질 수 있으므로, Request Encoding으로 분석
		encoding = req.getCharacterEncoding();
		upload.setHeaderEncoding(encoding);

		// 실제 클라이언트로부터 데이터를 수신
		List uploadItemList = upload.parseRequest(req);

		fileItems = new LinkedHashMap<String, FileItem>();
		savedParts = new LinkedHashMap<String, Part>();

		for (Iterator<FileItem> iter = uploadItemList.iterator(); iter.hasNext();) {
			FileItem item = iter.next();
			fileItems.put(item.getFieldName(), item);
		}
	}

	public void setRenamePolicy(RenamePolicy renamePolicy) {
		this.renamePolicy = renamePolicy;
	}

	@Override
	public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException {
		this.encoding = encoding;
		super.setCharacterEncoding(encoding);
	}

	/**
	 * Request가 MultiPart 라면, {@link FileItem#getString() FileItem#getString()}을 반환한다.
	 * 
	 * @param name Field 명
	 * @return 값
	 * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
	 */
	@Override
	public String getParameter(String name) {
		if ((isMultipart) && (fileItems != null)) {
			FileItem item = fileItems.get(name);
			return getUploadItemValue(item);
		} else
			return super.getParameter(name);
	}

	/**
	 * Request가 MultiPart 라면, {@link FileItem FileItem}을 반환한다.
	 * 
	 * @return Enumeration
	 * @see javax.servlet.ServletRequest#getParameterNames()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getParameterNames() {
		if ((isMultipart) && (fileItems != null)) {
			Vector<String> v = new Vector<String>();

			for (Iterator<String> iter = fileItems.keySet().iterator(); iter.hasNext();) {
				String fieldName = iter.next();
				if (fieldName != null)
					v.add(fieldName);
			}
			return v.elements();
		} else {
			return super.getParameterNames();
		}
	}

	/**
	 * Multipart 라면 {@link FileItem#getString() FileItem#getString()}을 반환하며, 아니라면, 일반적인 폼값을 반환한다.
	 * 
	 * @param name Field 명
	 * @return 폼값이나, 바이트의 문자열 변환값들을 배열 반환
	 */
	@Override
	public String[] getParameterValues(String name) {
		if (name == null)
			return null;

		if (fileItems != null) {
			String ret;
			Object value = fileItems.get(name);
			if (value instanceof String)
				ret = (String) value;
			else
				ret = getUploadItemValue((FileItem) value);

			if (ret != null) {
				String[] r = new String[1];
				r[0] = ret;
				return r;
			} else {
				return null;
			}
		} else {
			return super.getParameterValues(name);
		}
	}

	/**
	 * name에 의한 문자열을 반환한다.
	 * 
	 * @param item {@link FileItem FileItem}
	 * @return 문자열
	 */
	private String getUploadItemValue(FileItem item) {
		if (item != null) {
			if (item.isFormField()) {
				// normal form field
				try {
					return item.getString(encoding);
				} catch (UnsupportedEncodingException e) {
					return item.getString();
				}
			} else {
				return item.getName();
			}
		}
		return null;
	}

	/**
	 * Request가 멀티파트인지 여부를 반환한다.
	 * 
	 * @return 멀티파트인지 여부
	 */
	public boolean isMultipart() {
		return isMultipart;
	}
	
	/**
	 * 저장된 업로드 폴더를 반환한다.
	 * 
	 * @return 저장된 업로드 폴더
	 */
	public String getSavePath() {
		return defaultSavePath;
	}
	
	public long doUpload() throws IOException {
		if (defaultSavePath == null || defaultSavePath.length() <= 0)
			throw new IOException("Default Save Dir is not valid. [" + defaultSavePath + "]");
		return doUpload(defaultSavePath, -1, null);
	}

	public long doUpload(String saveDir) throws IOException {
		return doUpload(saveDir, -1, null);
	}
	
	public long doUpload(long maxSize) throws IOException {
		if (defaultSavePath == null || defaultSavePath.length() <= 0)
			throw new IOException("Default Save Dir is not valid. [" + defaultSavePath + "]");
		return doUpload(defaultSavePath, maxSize, null);
	}

	public long doUpload(String saveDir, long maxSize) throws IOException {
		return doUpload(saveDir, maxSize, null);
	}
	
	public long doUpload(long maxSize, String[] allowedExts) throws IOException {
		if (defaultSavePath == null || defaultSavePath.length() <= 0)
			throw new IOException("Default Save Dir is not valid. [" + defaultSavePath + "]");
		return doUpload(defaultSavePath, maxSize, allowedExts);
	}

	/**
	 * Multipart 일때, 저장할 업로드 폴더 및 싸이즈, 허용 확장자로 처리한다. 
	 * 
	 * @param saveDir 저장할 업로드 폴더
	 * @param maxSize 화일별 최대 허용 크기, -1은 제한하지 않는다.
	 * @param allowedExts 허용 확장자
	 * @return 저장된 전체 싸이즈
	 * @throws java.io.IOException
	 */
	public long doUpload(String saveDir, long maxSize, String[] allowedExts) throws IOException {
		try {
			if (saveDir == null || saveDir.length() == 0)
				throw new IllegalArgumentException("Directory for saving is null or length is 0.");

			File dir = new File(saveDir);
			if (!dir.exists())
				if (!dir.mkdirs())
					throw new IOException("Directory don't make in " + saveDir);

			return processUpload(dir, allowedExts, maxSize);
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Multipart 일때, 저장할 업로드 폴더 및 싸이즈, 허용 확장자로 처리한다. 사용방법은, 이전페이지가 multipart라면, 업로드한 화일에 대하여 suffix로 폼에서 넘긴 명 + "_text"는
	 * 디스크상 실제 파일명이며, 폼에서 넘긴 명 + "_type"는 파일명의 Content Type이며, 폼에서 넘긴 명 + "_size"는 파일의 크기이다.
	 * 
	 * @param saveDir 저장할 업로드 폴더
	 * @param allowedExts 허용 확장자
	 * @param maxSize 업로드 화일별 허용 최대 싸이즈
	 * @return 저장된 전체 싸이즈
	 * @throws java.io.IOException
	 */
	private long processUpload(File saveDir, String[] allowedExts, long maxSize) throws IOException {
		if ((!isMultipart) || (fileItems == null))
			return -1;

		FileItem[] items = fileItems.values().toArray(new FileItem[0]);
		// 검증
		List<FileItem> availableItems = new ArrayList<FileItem>();
		long totalSize = -1;
		for (int i = 0; i < items.length; i++) {
			FileItem item = items[i];
			{
				if (!item.isFormField()) {
					long size = verifyFileItem(item, allowedExts, maxSize);
					if (size > 0) {
						availableItems.add(item);
						totalSize += size;
					}
				}
			}
		}

		// 실제 저장 처리
		for (int i = 0; i < availableItems.size(); i++) {
			FileItem item = (FileItem) availableItems.get(i);
			{
				if (!item.isFormField())
					processFileItem(item, saveDir);
			}
		}

		availableItems.clear();

		return totalSize;
	}

	/**
	 * Multipart 일때, 싸이즈, 허용 확장자로 검증한다.
	 * 
	 * @param item {@link FileItem FileItem}
	 * @param allowedExts 허용 확장자
	 * @param maxSize 업로드 화일별 허용 최대 싸이즈
	 * @return 저장 싸이즈
	 * @throws java.io.IOException
	 */
	private long verifyFileItem(FileItem item, String[] allowedExts, long maxSize) throws IOException {
		String tmp = item.getName();
		if (tmp == null || tmp.length() <= 0)
			return 0;

		String originalfilename = tmp; // 폼으로 부터 받은 Original File Name
		int slash = Math.max(tmp.lastIndexOf('/'), tmp.lastIndexOf('\\'));
		if (slash > -1)
			originalfilename = tmp.substring(slash + 1);

		int p = originalfilename.lastIndexOf('.');

		if (p > -1) {
			String ext = originalfilename.substring(p + 1).toLowerCase();
			boolean isFind = true;
			if (allowedExts == null || allowedExts.length == 0) {
				isFind = true;
			} else {
				isFind = false;
				for (int i = 0; i < allowedExts.length; i++) {
					if (allowedExts[i].equals(ext))
						isFind = true;
				}
			}

			if (!isFind)
				throw new IOException(ext + "확장자는 허용하는 확장자가 아닙니다.");
		}

		if (maxSize != -1 && item.getSize() > maxSize)
			throw new IOException("화일당 업로드 최대 크기 " + maxSize + "을 넘었습니다.");

		return item.getSize();
	}

	/**
	 * Multipart 일때, 저장할 업로드 폴더 및 싸이즈, 허용 확장자로 처리한다. 사용방법은, 이전페이지가 multipart라면, 업로드한 화일에 대하여 suffix로 폼에서 넘긴 명 + "_text"는
	 * 디스크상 실제 파일명이며, 폼에서 넘긴 명 + "_type"는 파일명의 Content Type이며, 폼에서 넘긴 명 + "_size"는 파일의 크기이다.
	 * 
	 * @param item {@link FileItem FileItem}
	 * @param saveDir 저장할 업로드 폴더
	 * @throws java.io.IOException
	 */
	private void processFileItem(FileItem item, File saveDir) throws IOException {
		String originalfilename = item.getName(); // 폼으로 부터 받은 Original File Name
		// 디렉토리를 제외한 이름만 짤라냄
		int slash = Math.max(originalfilename.lastIndexOf('/'), originalfilename.lastIndexOf('\\'));
		if (slash > -1)
			originalfilename = originalfilename.substring(slash + 1);

		File file = null;
		if (renamePolicy != null)
			file = renamePolicy.rename(new File(saveDir, originalfilename));
		else
			file = new File(saveDir, originalfilename);

		try {
			item.write(file);
			String fileSystemName = file.getCanonicalPath(); // 저장된 파일시스템상의 File Name
			// 데이터베이스에 이상하게 들어감..따라서 변환.. \ -> /
			fileSystemName = fileSystemName.replaceAll("\\\\", "/");
			Part part = new Part(item.getName(), fileSystemName, item.getContentType(), file.length(), saveDir.getCanonicalPath());
			savedParts.put(item.getFieldName(), part);
		} catch (Exception e) {
			IOException ie = new IOException();
			ie.initCause(e);
			throw ie;
		}
	}

	/**
	 * Multupart를 가지는 폼의 필드명을 가지고, 정보를 가지는 {@link Part}를 반환한다.
	 * 
	 * @param name 폼의 필드명
	 * @return {@link Part}, 화일이 첨부되지 않았다면, 빈 {@link Part}를 반환한다.
	 */
	public Part getPart(String name) throws IOException {
		return savedParts.get(name);
	}
	
	public List<Part> getParts() throws IOException {
		Iterator<String> itr = fileItems.keySet().iterator();
		List<Part> parts = new ArrayList<Part>();
		while (itr.hasNext()) {
			parts.add(getPart(itr.next()));
		}
		return parts;
	}
}
