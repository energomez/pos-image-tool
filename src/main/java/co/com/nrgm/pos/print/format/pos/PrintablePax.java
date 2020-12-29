package co.com.nrgm.pos.print.format.pos;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import co.com.nrgm.pos.print.format.PrintableImage;

public class PrintablePax extends PrintableImage {

    public PrintablePax(BufferedImage biImage) {
        super(biImage);
    }

	/**
	 * Escribe en el flujo suministrado la representaci�n binaria soportada por la terminal PAX
	 * de la imagen almacenada.
	 *
	 * @param out el flujo de salida
	 * @throws IOException
	 */
    @Override
    public void writeImage(OutputStream out) throws IOException {
		// Alto x Ancho
		out.write(dataSize(MAX_WIDTH, biData.getHeight()));

		// Altura de la imagen
		out.write((byte) (biData.getHeight() & 0xFF));

		// Marcar cada l�nea horizontal de la imagen, indicar la cantidad de bytes que representan una l�nea
		// horizontal, el valor debe separarse en bytes
		final int bytesLine = MAX_WIDTH/Byte.SIZE;
		final byte[] bytesWith = { (byte) (bytesLine >> Byte.SIZE & 0xFF), (byte) (bytesLine & 0xFF)} ;
		byte[] data = getData(MAX_WIDTH);
		for (int i = 0; i < data.length; i += bytesLine) {
			// Marcar el inicio de una l�nea horizontal
			out.write(bytesWith);
			// Escribir los datos de la l�nea horizontal
			out.write(data, i, bytesLine);
		}
    }

	@Override
	protected String getNameSufix() {
		return ".pax";
	}
    
}