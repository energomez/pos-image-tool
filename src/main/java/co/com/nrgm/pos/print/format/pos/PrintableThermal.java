package co.com.nrgm.pos.print.format.pos;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import co.com.nrgm.pos.print.format.PrintableImage;

public class PrintableThermal extends PrintableImage {

    public PrintableThermal(BufferedImage biImage) {
        super(biImage);
    }

    @Override
    public void writeImage(OutputStream out) throws IOException {
		BufferedImage jadePunto = getImage(512);
		ImageIO.write(jadePunto, "gif", out);
    }

    @Override
    protected String getNameSufix() {
      return "_thermal.gif";
    }
    
}
