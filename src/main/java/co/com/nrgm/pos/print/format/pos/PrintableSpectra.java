package co.com.nrgm.pos.print.format.pos;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import co.com.nrgm.pos.print.format.PrintableImage;

public class PrintableSpectra extends PrintableImage {

    public PrintableSpectra(BufferedImage biImage) {
        super(biImage);
    }

	private byte invertByte(byte data) {
		byte etyb = 0;
		int size = Byte.SIZE;
		for (int i = size-1; i >= 0; i--) {
			etyb |= (data & 1) << i;
			data >>>= 1;
		}
		return etyb;
	}

	/**
	 * Escribe en el flujo suministrado la representación binaria soportada por la terminal SPECTRA
	 * de la imagen almacenada.
	 *
	 * @param out el flujo de salida
	 * @throws IOException
	 */
    @Override
    public void writeImage(OutputStream out) throws IOException {
		// Alto x Ancho
		out.write(dataSize(MAX_WIDTH, biData.getHeight()));

		// Cada pixel de la imagen empacado en un bit.
		byte[] data = getData(MAX_WIDTH);
		for (int i = 0; i < data.length; i++) {
			out.write(invertByte(data[i]));
		}
    }

	@Override
	protected String getNameSufix() {
		return ".spe";
	}
    
}
