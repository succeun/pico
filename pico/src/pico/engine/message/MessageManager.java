package pico.engine.message;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageManager {
	private static ConcurrentHashMap<String, Message> messageMap = new ConcurrentHashMap<String, Message>();
	
	static {
		regist("MSG-0000", TypeLevel.INFO, "'${0}' is successed.", ShowLevel.STATUS);
		regist("MSG-0001", TypeLevel.ERROR, "'${0}' is failed.", ShowLevel.ALERT);
		regist("FAIL", TypeLevel.ERROR, "${0}", ShowLevel.ALERT);
	}
	
	/**
	 * 
	 * @param code 코드명
	 * @param type INFO, WARN, ERROR
	 * @param msg 출력 문자열
	 * @param show 1: Popup, 2: statusBar
	 */
	public static void regist(String code, TypeLevel type, String msg, ShowLevel show) {
		Message msgVO = new Message();
		msgVO.setCode(code);
        msgVO.setType(type);
        msgVO.setMessage(msg);
        msgVO.setShow(show);
        messageMap.put(msgVO.getCode(), msgVO);
	}
	
	public static Message getMessage(String code, Object ... params) {
		Message msgVO = messageMap.get(code);
		if (msgVO == null)
			throw new NullPointerException("'" + code + "' don't exist.");
		msgVO = msgVO.clone();

	    if (params != null) {
	      msgVO.setMessage(parse(msgVO.getMessage(), params));
	    }
	    return msgVO;
	}
	
	private static Pattern valpattern = Pattern.compile("\\$\\{([0-9]*)?(=([^}\t\n]*))?\\}");
	
	private static String parse(String msg, Object[] params) {
		if (params == null || params.length <= 0)
			return msg;
		
		StringBuffer result = new StringBuffer();

		Matcher matcher = valpattern.matcher(msg);
		while (matcher.find()) {
			String index = matcher.group(1); // index
			String defaultValue = matcher.group(3); // default value

			Object value = params[Integer.parseInt(index)];
			if (value == null) {
				if (defaultValue != null && defaultValue.length() > 0)
					matcher.appendReplacement(result, defaultValue);
				else
					matcher.appendReplacement(result, "");
			} else {
				matcher.appendReplacement(result, value.toString());
			}
		}

		matcher.appendTail(result);

		return result.toString();
	}
}
