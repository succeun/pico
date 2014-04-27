package hi.pico.engine.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class Thumbnail {
	
	public static void createThumb(String orgName, String destName, int width, int height) throws IOException {
		File orgFile = new File(orgName);
		File destFile = new File(destName);
		
		createThumb(orgFile, destFile, width, height);
	}
	
	public static void createThumb(File orgFile, File destFile, int width, int height) throws IOException {
		Image srcImg = ImageIO.read(orgFile);
		
		BufferedImage destImg = convertImage(width, height, srcImg);
		ImageIO.write(destImg, "jpg", destFile);
	}
	
	public static void createThumb(InputStream in, OutputStream out, int width, int height) throws IOException {
		Image srcImg = ImageIO.read(in);
		
		BufferedImage destImg = convertImage(width, height, srcImg);
		ImageIO.write(destImg, "jpg", out);
	}

	private static BufferedImage convertImage(int width, int height, Image srcImg) throws IOException {
		int srcWidth = srcImg.getWidth(null);
		int srcHeight = srcImg.getHeight(null);
		int destWidth = -1, destHeight = -1;

		if (width < 0) {
			destWidth = srcWidth;
		} else if (width > 0) {
			destWidth = width;
		}

		if (height < 0) {
			destHeight = srcHeight;
		} else if (height > 0) {
			destHeight = height;
		}
		
		Image imgTarget = srcImg.getScaledInstance(destWidth, destHeight, Image.SCALE_SMOOTH);
		int pixels[] = new int[destWidth * destHeight];
		PixelGrabber pg = new PixelGrabber(imgTarget, 0, 0, destWidth, destHeight, pixels, 0, destWidth);

		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			throw new IOException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

		BufferedImage destImg = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_RGB);
		destImg.setRGB(0, 0, destWidth, destHeight, pixels, 0, destWidth);
		return destImg;
	}
}
