package pico.view;

import java.io.StringWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import pico.ControllerContext;

public class Xml extends AbstractData {
	private String xml;
	private Object bean;

	public Xml(String xml) {
		this.xml = xml;
	}
	
	public Xml(Object bean) {
		if (bean instanceof String) {
			this.xml = (String) bean;
		} else {
			this.bean = bean;
		}
	}
	
	private String getXml() throws Exception {
		if (xml == null && bean != null) {
			JAXBContext jaxbContext = JAXBContext.newInstance(bean.getClass());
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, res.getCharacterEncoding());
			StringWriter writer = new StringWriter();
			marshaller.marshal(bean, writer);
			return writer.toString();
		} else {
			return (xml == null) ? "" : xml;
		}
	}
	
	private String addXmlDeclaration() {
		if (xml != null && !xml.startsWith("<?xml")) {
			String encoding = res.getCharacterEncoding();
			xml = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n" + xml;
		}

		return xml;
	}

	@Override
	public void renderInternal(ServletContext context, HttpServletRequest req,
			HttpServletResponse res, ControllerContext controllerContext) throws Exception {
		this.req = req;
		this.res = res;
		String str = getXml();
		addXmlDeclaration();
		write("text/xml", str);
	}
}
