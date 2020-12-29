package co.com.nrgm.pos.print.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageTool {
    private static final int WHITE = Color.WHITE.getRGB();
    private static final int BLACK = Color.BLACK.getRGB();

	private static int obtainGrayColor(int red, int green, int blue) {
		/* Conversion por brillo: promedio de los mayor y menor predominante color
		 * Este algoritmo asocia un tono oscuro el amarillo */
		return (int) ((Math.max(red, Math.max(green, blue)) + Math.min(red, Math.min(green, blue)))/ 2.0);
	}

    public static int rgb2WhiteBlack(int rgbColor, int grayLimit) {
        int alpha = (rgbColor >> 24) & 0xFF;
        if (alpha == 0x00) { // Transparente
            return WHITE;
        } else {
            int red = (rgbColor >> 16) & 0xFF;
            int green = (rgbColor >> 8) & 0xFF;
            int blue = rgbColor & 0xFF;
            int grayColor = obtainGrayColor(red, green, blue);
            return ( grayColor >= grayLimit ? WHITE : BLACK);
        }
	}

	public static BufferedImage resizeImage(BufferedImage originalImage, Point size) {
        BufferedImage resizedImage = new BufferedImage(size.x, size.y, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, size.x, size.y, 0, 0, originalImage.getWidth(), originalImage.getHeight(), null);
        g.dispose();

        return resizedImage;
    }

    public static Point getPrintDimentions(BufferedImage bimage, int width) {
		Point sizePrint = new Point();

		float factor = ((float) width / bimage.getWidth());
		if (factor >= 1) {
			// Ancho inferior a los limites propuestos
			sizePrint.x = bimage.getWidth();
			sizePrint.y = bimage.getHeight();
		} else {
			sizePrint.x = (int) (bimage.getWidth() * factor);
			sizePrint.y = (int) (bimage.getHeight() * factor);
		}

		return sizePrint;
	}

}
