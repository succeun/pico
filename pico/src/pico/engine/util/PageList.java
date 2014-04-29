package pico.engine.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eun Jeong-Ho, succeun@gmail.com
 * @version 2007. 5. 22
 */
@SuppressWarnings("serial")
public class PageList implements java.io.Serializable {
	public static final int DEFAULT_PAGE_NO = 1;
	public static final int DEFAULT_PAGE_RANGE = 10;
	public static final int DEFAULT_BLOCK_RANGE = 10;

	private int total; // ��ü ��
	private int pageRange = DEFAULT_PAGE_RANGE; // �������� ��
	private int pageNo = DEFAULT_PAGE_NO; // ��������ȣ
	private int blockRange = DEFAULT_BLOCK_RANGE; // ���������� ��

	public PageList() {
		this(DEFAULT_PAGE_RANGE, DEFAULT_PAGE_NO, DEFAULT_BLOCK_RANGE);
	}

	public PageList(int pageNo) {
		this(DEFAULT_PAGE_RANGE, pageNo, DEFAULT_BLOCK_RANGE);
	}

	public PageList(int pageRange, int pageNo, int total) {
		this(pageRange, pageNo, DEFAULT_BLOCK_RANGE, total);
	}

	public PageList(int pageRange, int pageNo, int blockRange, int total) {
		this.pageRange = pageRange;
		this.pageNo = pageNo;
		this.blockRange = blockRange;
		this.total = total;
	}

	public int getTotal() {
		return total;
	}

	public int getPageRange() {
		return pageRange;
	}

	public int getPageNo() {
		return pageNo;
	}

	public int getBlockRange() {
		return blockRange;
	}

	public int getTotalPage() {
		return (total / pageRange) + (((total % pageRange) > 0) ? 1 : 0);
	}

	/**
	 * �� ������ �����ϴ� �Խù��� ������ ��ȣ�� ��ȯ�Ѵ�. ��, ����Ŭ�� Rownum �� ���� �׶� �׶� �ٿ����� ��ȣ���� �ϳ��̴�.
	 * @return ��ȣ
	 */
	public int getPageFirstNumber() {
		return pageRange * (pageNo - 1);
	}

	public String getNavigation(String pageParameterName, String linkURL, String... extraParameters) {
		Linkable linker = new DefaultLink(pageParameterName, linkURL, extraParameters);
		return getNavigation("&lt;&lt;", "&lt;", "&gt;", "&gt;&gt;", "|", linker);
	}

	public String getAdvanceNavigation(String firstblock, String prevblock, String nextblock, String lastblock, String token, String pageParameterName, String linkURL, String... extraParameters) {
		Linkable linker = new DefaultLink(pageParameterName, linkURL, extraParameters);
		return getNavigation(firstblock, prevblock, nextblock, lastblock, token, linker);
	}

	public String getNavigation(Linkable linker) {
		return getNavigation("&lt;&lt;", "&lt;", "&gt;", "&gt;&gt;", "|", linker);
	}

	public String getNavigation(String firstblock, String prevblock, String nextblock, String lastblock, String token, Linkable linker) {
		StringBuffer buffer = new StringBuffer();
		int destPage; // ��ũ�Ǵ� ������
		int totalPage = getTotalPage();
		int totalBlock = (totalPage / blockRange) + (((totalPage % blockRange) > 0) ? 1 : 0); // ��ü
																								// ��
																								// ����
		int blockNo = (pageNo / blockRange) + (((pageNo % blockRange) > 0) ? 1 : 0); // ����
																						// ��
		int firstPage = (blockNo - 1) * blockRange;
		int lastPage = blockNo * blockRange;

		buffer.append("<ul id=\"pages\">");

		if (totalBlock <= blockNo)
			lastPage = totalPage;

		if (blockNo > 1) {
			buffer.append("<li class=\"first\"><a href=\"" + linker.getLink(1) + "\">" + firstblock + "</a></li>");
			destPage = firstPage;
			buffer.append("<li class=\"prev\"><a href=\"" + linker.getLink(destPage) + "\">" + prevblock + "</a></li>");
		}
		// ������ ��ȣ ��ũ
		for (int page = firstPage + 1; page <= lastPage; page++) {
			if (page == firstPage + 1)
				buffer.append("&nbsp;");
			else
				buffer.append(token);

			if (pageNo == page)
				buffer.append("<li class=\"here\">" + pageNo + "</li>");
			else
				buffer.append("<li><a href=\"" + linker.getLink(page) + "\">" + page + "</a></li>");

			if (page == lastPage)
				buffer.append("&nbsp;");
		}

		// ������������� �� ������ ��Ͽ� ���� ������ ��ũ
		if (blockNo < totalBlock) {
			destPage = lastPage + 1;
			buffer.append("<li class=\"next\"><a href=\"" + linker.getLink(destPage) + "\">" + nextblock + "</a></li>");
			buffer.append("<li class=\"last\"><a href=\"" + linker.getLink(totalPage) + "\">" + lastblock + "</a></li>");
		}

		buffer.append("</ul>");

		return buffer.toString();
	}

	public static interface Linkable {
		public String getLink(int page);
	}

	public static class DefaultLink implements Linkable {
		private static Pattern pattern = Pattern.compile("(\\?|\\&)([\\w#!:.+%@!\\-\\/]+\\=[\\w#!:.+%@!\\-\\/]*)+");
		private String linkURL;
		private String parameterName;
		private String[] extraParameters;

		public DefaultLink(String pageParameterName, String linkURL, String... extraParameters) {
			this.linkURL = linkURL;
			this.parameterName = pageParameterName;
			this.extraParameters = extraParameters;
		}

		public String getLink(int page) {
			String url = linkURL;
			Matcher matcher = pattern.matcher(linkURL);
			if (matcher.find())
				url += "&";
			else
				url += "?";
			url += parameterName + "=" + page;
			for (String parameter : extraParameters) {
				if (parameter != null && parameter.length() > 0)
					url += "&" + parameter;
			}
			return url;
		}
	}
}
