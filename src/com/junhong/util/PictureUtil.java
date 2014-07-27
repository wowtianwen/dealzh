package com.junhong.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import sun.awt.image.BufferedImageGraphicsConfig;

import com.junhong.forum.common.Constants;

public class PictureUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedImage img = null;
		try {
			String currDir = System.getProperty("user.dir");
			System.out.println(currDir);

			PictureUtil.class.getClassLoader();
			URL path = ClassLoader.getSystemResource("com/junhong/util/Hydrangeas.jpg");
			if (path == null) {
				// The file was not found, insert error handling here
			}
			img = ImageIO.read(path);

			int width = 100;
			int heigh = 100;

			BufferedImage first = resize(img, width, heigh);

			BufferedImage second = resize(blurImage(createCompatibleImage(img)), width, heigh);

			File outputfile = new File(currDir, "src\\com\\junhong\\util\\first.jpg");
			ImageIO.write(first, "jpg", outputfile);

			File outputfile2 = new File(currDir, "src\\com\\junhong\\util\\second.jpg");
			ImageIO.write(second, "jpg", outputfile2);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static BufferedImage dropAlphaChannel(BufferedImage src) {
		BufferedImage convertedImg = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
		convertedImg.getGraphics().drawImage(src, 0, 0, null);

		return convertedImg;
	}

	public static BufferedImage resize(BufferedImage image) {
		int width = 200;
		int height = 200;
		String profilePicWidth = WebConfigUtil.getProp(Constants.PROFILE_PIC_WIDTH);
		String profilePicHeight = WebConfigUtil.getProp(Constants.PROFILE_PIC_HEIGHT);
		try {
			if (profilePicWidth != null) {
				width = Integer.parseInt(profilePicWidth);

			}
			if (profilePicHeight != null) {
				height = Integer.parseInt(profilePicHeight);
			}
		} catch (NumberFormatException e) {
			System.out.println("profile width/height invalid number format");
			e.printStackTrace();

		}

		return resize(image, width, height);
	}

	private static BufferedImage resize(BufferedImage image, int width, int height) {
		int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.setComposite(AlphaComposite.Src);

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
	}

	public static BufferedImage blurImage(BufferedImage image) {
		float ninth = 1.0f / 9.0f;
		float[] blurKernel = { ninth, ninth, ninth, ninth, ninth, ninth, ninth, ninth, ninth };

		Map<Key, Object> map = new ConcurrentHashMap<Key, Object>();

		map.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		RenderingHints hints = new RenderingHints(map);
		BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_NO_OP, hints);
		return op.filter(image, null);
	}

	private static BufferedImage createCompatibleImage(BufferedImage image) {
		GraphicsConfiguration gc = BufferedImageGraphicsConfig.getConfig(image);
		int w = image.getWidth();
		int h = image.getHeight();
		BufferedImage result = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
		Graphics2D g2 = result.createGraphics();
		g2.drawRenderedImage(image, null);
		g2.dispose();
		return result;
	}

}
