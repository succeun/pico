package pico.sample;

import hi.pico.After;
import hi.pico.Before;
import hi.pico.MultipartConfig;
import hi.pico.MultipartRequest;
import hi.pico.Part;
import hi.pico.WebController;
import hi.pico.WebMethod;
import hi.pico.view.Renderer;
import hi.pico.view.View;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebController
@MultipartConfig(location=Upload.UPLOAD_PATH, maxFileSize=1000*1024*1024)
public class Upload {
	public static final String UPLOAD_PATH = "c:/tmp";
	
	@Before
	public void onBefore(ServletContext context, HttpServletRequest req, HttpServletResponse res) throws Exception {
		res.setContentType("text/html; charset=utf-8");
		req.setCharacterEncoding("utf-8");
	}
	
	@After
	public void onAfter() {
		System.out.println("After call.");
	}
	
	@WebMethod
	public View upload(MultipartRequest req) throws IOException, ServletException {
		File file = null;
		Part part = req.getPart("file");
        if (part != null) {
            file = part.getFile();
            String reqFileName = part.getName();
            if (!reqFileName.equals(file.getName())) {
            	if (new File(UPLOAD_PATH, reqFileName).delete()) {	// 기존 화일 삭제
            		File newFile = new File(UPLOAD_PATH, reqFileName);
            		file.renameTo(newFile);
            		file = newFile;
            	}
            }
        }
        return Renderer.ok();
	}
}
