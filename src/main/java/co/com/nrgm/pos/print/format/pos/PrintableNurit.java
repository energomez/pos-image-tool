package co.com.nrgm.pos.print.format.pos;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import co.com.nrgm.pos.print.format.PrintableImage;

public class PrintableNurit extends PrintableImage {

    public PrintableNurit(BufferedImage biImage) {
        super(biImage);
    }

	/**
	 * Escribe en el flujo suministrado la representación binaria soportada por la terminal NURIT
	 * de la imagen almacenada.
	 *
	 * @param out el flujo de salida
	 * @throws IOException
	 */
    @Override
    public void writeImage(OutputStream out) throws IOException {
		// Alto x Ancho
		out.write(dataSize(MAX_WIDTH, biData.getHeight()));

		// Los pixeles de la imagen sin cambios
		out.write(getData(MAX_WIDTH));
    }

	@Override
	protected String getNameSufix() {
		return ".nur";
	}

}