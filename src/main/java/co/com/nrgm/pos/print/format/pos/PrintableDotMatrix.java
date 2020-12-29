package co.com.nrgm.pos.print.format.pos;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import co.com.nrgm.pos.print.format.PrintableImage;

public class PrintableDotMatrix extends PrintableImage {

    public PrintableDotMatrix(BufferedImage biImage) {
        super(biImage);
    }

    @Override
    public void writeImage(OutputStream out) throws IOException {
		BufferedImage jadePunto = getImage(384);
		ImageIO.write(jadePunto, "gif", out);
    }

    @Override
    protected String getNameSufix() {
      return "_dot.gif";
    }

}
