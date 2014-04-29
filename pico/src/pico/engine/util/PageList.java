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

	private int total; // 전체 수
	private int pageRange = DEFAULT_PAGE_RANGE; // 페이지당 수
	private int pageNo = DEFAULT_PAGE_NO; // 페이지번호
	private int blockRange = DEFAULT_BLOCK_RANGE; // 페이지블럭당 수

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
	 * 각 페이지 시작하는 게시물의 논리적인 번호를 반환한다. 즉, 오라클의 Rownum 과 같이 그때 그때 붙여지는 번호중의 하나이다.
	 * @return 번호
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
		int destPage; // 링크되는 페이지
		int totalPage = getTotalPage();
		int totalBlock = (totalPage / blockRange) + (((totalPage % blockRange) > 0) ? 1 : 0); // 전체
																								// 블럭
																								// 갯수
		int blockNo = (pageNo / blockRange) + (((pageNo % blockRange) > 0) ? 1 : 0); // 현재
																						// 블럭
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
		// 페이지 번호 링크
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

		// 다음페이지블록 및 마지막 블록에 대한 페이지 링크
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
