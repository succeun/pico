package hi.pico.view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractDownload extends View {
	protected HttpServletRequest req;
	protected HttpServletResponse res;
	
	protected void write(String mimetype, String fileName, InputStream in, boolean isDownload) throws IOException {
        BufferedInputStream bin = null;
        BufferedOutputStream bout = null;
        try {
            /**
             * 참고사항
             * application/x-msdownload : 브라우저에서 실행
             * application/octet-stream : 다운로드
             */

            String mime = "application/octet-stream";
            if(mimetype == null || mimetype.length() == 0)
            {
            	if (isDownload)
            		mime = "application/octet-stream";
            	else 
            		mime = "application/x-msdownload";
            }
            else
                mime = mimetype;

            if (isDownload) {
                String userAgent = req.getHeader("User-Agent");
                if (userAgent.indexOf("MSIE 5.5") > -1) {
                    res.setContentType(mime + "; charset=euc-kr");
                    res.setHeader("Content-Disposition", "filename=" + URLEncoder.encode(fileName, "euc-kr") + ";");
                } else if(userAgent.indexOf("MSIE 6.0") > -1) {
                    res.setContentType(mime + "; charset=UTF-8");
                    res.setHeader("Content-Disposition", "filename=" + URLEncoder.encode(fileName, "UTF-8") + ";");
                } else if(userAgent.indexOf("MSIE") > -1) {
                    res.setContentType(mime + "; charset=UTF-8");
                    res.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8") + ";");
                } else {
                    res.setContentType(mime + "; charset=latin1");
                    res.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("euc-kr"), "latin1") + ";");
                }
            } else {
            	res.setContentType(mime);
            }

            fileName = fileName.replaceAll(" ", "%20");

            bin = new BufferedInputStream(in);
            bout = new BufferedOutputStream(res.getOutputStream());
            long totalSize = bin.available();

            if(totalSize > 0L)
                res.setHeader("Content-Length", totalSize + "");

            int read;
            byte b[] = new byte[2048];
            while ((read = bin.read(b)) != -1) {
                bout.write(b,0,read);
                totalSize += read;
            }
        } finally {
            if (bout != null)
                bout.flush();
            if (bin != null)
                bin.close();
        }
    }
}
