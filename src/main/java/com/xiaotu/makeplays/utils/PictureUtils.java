package com.xiaotu.makeplays.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 图片工具类
 * @author xuchangjian 2016-10-26下午2:55:47
 */
public class PictureUtils {
	
	static Logger logger = LoggerFactory.getLogger(PictureUtils.class);

	/**
	 * 图片加文字文字(五个位置，分别是图片的左上角，右上角，中间，左下角，右下角）对应参数 int weizhi 可以取0到4)
	 * 
	 * @param pressText 水印文字
	 * @param srcPath 原图片路径
	 * @param targetPath 新图片路径
	 * @param position 位置 0:左上角 1:右上角 2:左下角 3:右下角 4:居中
	 * @param fontSize 字体大小
	 * @param color 字体颜色
	 * @param fontName 字体
	 * @param isBold 字体是否加粗
	 * @param alpha 透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
	 */
	public static void pressImgText(String pressText, String srcPath,
			String targetPath, int position, int fontSize, Color color,
			String fontName, boolean isBold, String alpha) {
		try {
			File img = new File(srcPath);
			File targetImg = new File(targetPath);
			FileUtils.copyFile(img, targetImg);
			
			Image src = ImageIO.read(targetImg);
			int width = src.getWidth(null);
			int height = src.getHeight(null);
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.drawImage(src, 0, 0, width, height, null);

			// ==========设置默认字体样式/颜色/是否加粗/大小====
			if (color == null) {
				g.setColor(Color.BLACK);
			} else {
				g.setColor(color);
			}

			// 大小
			if (fontSize == 0) {
				fontSize = 20;
			}
			if (fontSize * getLength(pressText) > (width - 20)) {
				fontSize = (width - 20) / pressText.length();
			}

			// 字体样式
			if (StringUtils.isBlank(fontName)) {
				fontName = "宋体";
			}
			// 是否加粗
			if (isBold) {
				g.setFont(new Font(fontName, Font.BOLD, fontSize));
			} else {
				g.setFont(new Font(fontName, Font.PLAIN, fontSize));
			}
			
			// 设置默认的透明度
			if (StringUtils.isEmpty(alpha) || alpha.equals("0")) {
				alpha = "0.5";
			}

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, Float.parseFloat(alpha)));
			
			//消除锯齿
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			int x = 0;
			int y = 0;

			switch (position) {
			case 0:
				x = 5;
				y = fontSize + 5;
				break;
			case 1:
				x = (width - (getLength(pressText) * fontSize)) - 20;
				y = fontSize + 5;
				break;
			case 2:
				x = 5;
				y = height - fontSize;
				break;
			case 3:
				x = (width - (getLength(pressText) * fontSize)) - 20;
				y = height - fontSize;
				break;
			case 4:
				x = (width - getLength(pressText) * fontSize) / 2 - 5;
				y = (height + fontSize) / 2 - 5;
				break;
			default:
				break;
			}
			
			g.drawString(pressText, x, y);
			g.dispose();
			ImageIO.write((BufferedImage) image, "JPG", targetImg);
		} catch (Exception e) {
			logger.error("添加文字水印失败！", e);
		}
	}
	
	/**
	 * 取得汉字的长度
	 * 
	 * @param text
	 * @return
	 */
	public static int getLength(String text) {
		int length = 0;
		for (int i = 0; i < text.length(); i++) {
			if (new String(text.charAt(i) + "").getBytes().length > 1) {
				length += 2;
			} else {
				length += 1;
			}
		}
		return length / 2;
	}
	
	/**
	 * 图片缩放
	 * @param srcPath 源图片路径
	 * @param targetPath 目标图片路径
	 * @param height 高度
	 * @param width 宽度
	 * @param bb 比例不对时是否需要补白
	 */
	public static void zoomPerImg(String srcPath, String targetPath, int height, int width, boolean bb) {
		try {
			double ratio = 0; // 缩放比例
			
			File srcFile = new File(srcPath);
			File targetFile = new File(targetPath);
			FileUtils.copyFile(srcFile, targetFile);
			
			BufferedImage bi = ImageIO.read(targetFile);
			Image itemp = bi.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
			
			// 计算比例
			if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
				if (bi.getHeight() > bi.getWidth()) {
					ratio = (new Integer(height)).doubleValue()
							/ bi.getHeight();
				} else {
					ratio = (new Integer(width)).doubleValue() / bi.getWidth();
				}
				AffineTransformOp op = new AffineTransformOp(
						AffineTransform.getScaleInstance(ratio, ratio), null);
				itemp = op.filter(bi, null);
			}
			if (bb) {
				BufferedImage image = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
				Graphics2D g = image.createGraphics();
				g.setColor(Color.white);
				g.fillRect(0, 0, width, height);
				if (width == itemp.getWidth(null))
					g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2,
							itemp.getWidth(null), itemp.getHeight(null),
							Color.white, null);
				else
					g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0,
							itemp.getWidth(null), itemp.getHeight(null),
							Color.white, null);
				g.dispose();
				itemp = image;
			}
			ImageIO.write((BufferedImage) itemp, "jpg", targetFile);
		} catch (IOException e) {
			logger.error("缩放图片失败", e);
		}
	}
}
