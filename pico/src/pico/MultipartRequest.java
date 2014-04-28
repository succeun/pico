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
	/** �⺻���� ���ε� ��� Ȯ���� */
	public static final String[] DEFAULT_UPLOAD_ALLOWED_EXTS = new String[] { "jpg", "jpeg", "gif", "bmp", "png",
			"psd", "swf", "tar", "gz", "tgz", "alz", "zip", "rar", "ace", "arj", "jar", "exe", "avi", "ppt", "xls",
			"doc" };
	/** �⺻���� ���ε�Ǵ� ��� ȭ���� �� ��� �ִ� ũ�� (req.getContentLength()) */
	public static final long DEFAULT_UPLOAD_MAX_SIZE = -1;
	/** �θ޸𸮿��� ��ũ�� �Ѿ�� ������ ũ�� */
	public static final int DEFAULT_SIZE_THRESHOLD = DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD; // 10Kb
	private RenamePolicy renamePolicy = new FileRenamePolicy();
	/** Request�� Multipart ���� ���� */
	private boolean isMultipart;
	private int sizeThreshold = DEFAULT_SIZE_THRESHOLD;
	/** FieldName�� ���� @see org.apache.commons.fileupload.FileItem �� Map */
	private Map<String, FileItem> fileItems = null;
	private Map<String, Part> savedParts = null;
	private String encoding;
	private String defaultSavePath;
	
	public MultipartRequest(ServletContext context, HttpServletRequest req) throws FileUploadException {
		this(context, req, null, DEFAULT_UPLOAD_MAX_SIZE, DEFAULT_SIZE_THRESHOLD, System.getProperty("java.io.tmpdir"));
	}

	/**
	 * Multipart ���������� �Ǻ��Ͽ� ó���Ѵ�.
	 * 
	 * @param req
	 */
	public MultipartRequest(ServletContext context, HttpServletRequest req, String defaultSavePath) throws FileUploadException {
		this(context, req, defaultSavePath, DEFAULT_UPLOAD_MAX_SIZE, DEFAULT_SIZE_THRESHOLD, System.getProperty("java.io.tmpdir"));
	}

	/**
	 * Multipart ���������� �Ǻ��Ͽ� ó���Ѵ�.
	 * 
	 * @param req
	 * @param max ����ϴ� �ִ� ���ε� ũ��
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
	 * MultiPart�� �ʱ�ȭ�Ѵ�.
	 * 
	 * @param req HttpServletRequest
	 * @param sizeThreshold Memory���� Disk�� ������ �Ѿ Threshold
	 * @param maxSize �ִ���� ũ��
	 * @param tempDir Disk�� ����� �ӽ� �����
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

		// �Ѿ�� �������� UTF-8 �ϰ��ó��, ������ ���� �� �����Ƿ�, Request Encoding���� �м�
		encoding = req.getCharacterEncoding();
		upload.setHeaderEncoding(encoding);

		// ���� Ŭ���̾�Ʈ�κ��� �����͸� ����
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
	 * Request�� MultiPart ���, {@link FileItem#getString() FileItem#getString()}�� ��ȯ�Ѵ�.
	 * 
	 * @param name Field ��
	 * @return ��
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
	 * Request�� MultiPart ���, {@link FileItem FileItem}�� ��ȯ�Ѵ�.
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
	 * Multipart ��� {@link FileItem#getString() FileItem#getString()}�� ��ȯ�ϸ�, �ƴ϶��, �Ϲ����� ������ ��ȯ�Ѵ�.
	 * 
	 * @param name Field ��
	 * @return �����̳�, ����Ʈ�� ���ڿ� ��ȯ������ �迭 ��ȯ
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
	 * name�� ���� ���ڿ��� ��ȯ�Ѵ�.
	 * 
	 * @param item {@link FileItem FileItem}
	 * @return ���ڿ�
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
	 * Request�� ��Ƽ��Ʈ���� ���θ� ��ȯ�Ѵ�.
	 * 
	 * @return ��Ƽ��Ʈ���� ����
	 */
	public boolean isMultipart() {
		return isMultipart;
	}
	
	/**
	 * ����� ���ε� ������ ��ȯ�Ѵ�.
	 * 
	 * @return ����� ���ε� ����
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
	 * Multipart �϶�, ������ ���ε� ���� �� ������, ��� Ȯ���ڷ� ó���Ѵ�. 
	 * 
	 * @param saveDir ������ ���ε� ����
	 * @param maxSize ȭ�Ϻ� �ִ� ��� ũ��, -1�� �������� �ʴ´�.
	 * @param allowedExts ��� Ȯ����
	 * @return ����� ��ü ������
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
	 * Multipart �϶�, ������ ���ε� ���� �� ������, ��� Ȯ���ڷ� ó���Ѵ�. �������, ������������ multipart���, ���ε��� ȭ�Ͽ� ���Ͽ� suffix�� ������ �ѱ� �� + "_text"��
	 * ��ũ�� ���� ���ϸ��̸�, ������ �ѱ� �� + "_type"�� ���ϸ��� Content Type�̸�, ������ �ѱ� �� + "_size"�� ������ ũ���̴�.
	 * 
	 * @param saveDir ������ ���ε� ����
	 * @param allowedExts ��� Ȯ����
	 * @param maxSize ���ε� ȭ�Ϻ� ��� �ִ� ������
	 * @return ����� ��ü ������
	 * @throws java.io.IOException
	 */
	private long processUpload(File saveDir, String[] allowedExts, long maxSize) throws IOException {
		if ((!isMultipart) || (fileItems == null))
			return -1;

		FileItem[] items = fileItems.values().toArray(new FileItem[0]);
		// ����
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

		// ���� ���� ó��
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
	 * Multipart �϶�, ������, ��� Ȯ���ڷ� �����Ѵ�.
	 * 
	 * @param item {@link FileItem FileItem}
	 * @param allowedExts ��� Ȯ����
	 * @param maxSize ���ε� ȭ�Ϻ� ��� �ִ� ������
	 * @return ���� ������
	 * @throws java.io.IOException
	 */
	private long verifyFileItem(FileItem item, String[] allowedExts, long maxSize) throws IOException {
		String tmp = item.getName();
		if (tmp == null || tmp.length() <= 0)
			return 0;

		String originalfilename = tmp; // ������ ���� ���� Original File Name
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
				throw new IOException(ext + "Ȯ���ڴ� ����ϴ� Ȯ���ڰ� �ƴմϴ�.");
		}

		if (maxSize != -1 && item.getSize() > maxSize)
			throw new IOException("ȭ�ϴ� ���ε� �ִ� ũ�� " + maxSize + "�� �Ѿ����ϴ�.");

		return item.getSize();
	}

	/**
	 * Multipart �϶�, ������ ���ε� ���� �� ������, ��� Ȯ���ڷ� ó���Ѵ�. �������, ������������ multipart���, ���ε��� ȭ�Ͽ� ���Ͽ� suffix�� ������ �ѱ� �� + "_text"��
	 * ��ũ�� ���� ���ϸ��̸�, ������ �ѱ� �� + "_type"�� ���ϸ��� Content Type�̸�, ������ �ѱ� �� + "_size"�� ������ ũ���̴�.
	 * 
	 * @param item {@link FileItem FileItem}
	 * @param saveDir ������ ���ε� ����
	 * @throws java.io.IOException
	 */
	private void processFileItem(FileItem item, File saveDir) throws IOException {
		String originalfilename = item.getName(); // ������ ���� ���� Original File Name
		// ���丮�� ������ �̸��� ©��
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
			String fileSystemName = file.getCanonicalPath(); // ����� ���Ͻý��ۻ��� File Name
			// �����ͺ��̽��� �̻��ϰ� ��..���� ��ȯ.. \ -> /
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
	 * Multupart�� ������ ���� �ʵ���� ������, ������ ������ {@link Part}�� ��ȯ�Ѵ�.
	 * 
	 * @param name ���� �ʵ��
	 * @return {@link Part}, ȭ���� ÷�ε��� �ʾҴٸ�, �� {@link Part}�� ��ȯ�Ѵ�.
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
