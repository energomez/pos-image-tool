package co.com.nrgm.pos.print.format;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class PrintableImage {

	// El ancho máximo común a todas las terminales, el menor ancho máximo.
	public static final int MAX_WIDTH = 384;

	protected BufferedImage biData;

	public PrintableImage(BufferedImage biImage) {
		super();
		setData(biImage);
	}

	protected BufferedImage getImage(int preferedWidth) {
		BufferedImage newBi = new BufferedImage(preferedWidth, biData.getHeight(), biData.getType());

		// Desplazamiento para centrar horizontalmente la imagen
		int offset = (preferedWidth - biData.getWidth())/2;
		Graphics g = newBi.getGraphics();
		if (offset < 0) {
			// Recortar la imagen
			g.drawImage(biData.getSubimage(-offset, 0, biData.getWidth() + offset, biData.getHeight()), 0, 0, null);
		} else {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, preferedWidth, biData.getHeight());
			g.drawImage(biData, offset, 0, null);
		}
		g.dispose();
		return newBi;
	}

	/**
	 * Empaqueta los pixeles de la imagen en un arreglo de bytes, un pixel en un bit.
	 * Un pixel blanco en un bit en cero y un pixel negro en un bit en uno.
	 *
	 * @param preferedWidth
	 * @return
	 */
	protected byte[] getData(int preferedWidth) {
		// La imagen se expande hasta el ancho preferido, cada pixel en un byte (1 byte = 8 bits = 8 pixeles)
		final int width = (int) Math.ceil(preferedWidth/8.0);
		byte[] pixelBits = new byte[biData.getHeight() * width];

		// Desplazamiento para centrar horizontalmente la imagen
		int offset = (preferedWidth - biData.getWidth())/2;

		int byteIndex = 0;
		byte colorBit = 0;
	    for (int y = 0; y < biData.getHeight(); y++){
	        for (int  x = 0; x < biData.getWidth(); x++) {
	        	// Conversi�n de la coordena x a indice de byte
	        	byteIndex = (x + offset)/Byte.SIZE;

	        	// Descartar los pixeles que quedan por fuera del ancho delimitado por preferedWidth
	        	if (byteIndex >= 0 && byteIndex < width) {

	        		// Se lee el color azul (BLUE), leer uno de los colores es suficiente.
	        		// El blanco (FF) se transforma en cero y el negro (00) en uno.
	        		colorBit = (byte) ((biData.getRGB(x, y) & 0xFF) > 0 ? 0 : 1);

	        		/*
	        		 * Almace los pixeles de la imagen en un arreglo de bytes, un pixel en un bit
	        		 *
	        		 * Traducir la coordenada (x,y) a una posición líneal dentro del arreglo de bytes:
	        		 * -----------------------------------------------------------------------------------------------------
	        		 * La posición del byte, es la suma de las coordenadas "y" y "x" así:
	        		 *  y * width           La coordenada y se multiplica por el ancho.
	        		 *  byteIndex		    La coordenada coordenada x ajustada a bytes.
	        		 * -----------------------------------------------------------------------------------------------------
	        		 * La posición del bit dentro byte:
	        		 * Los pixeles (bits) en el byte se ingresan de izquierda a derecha, pero el bit como n�mero se posiciona
	        		 * de derecha a izquierda, por esto se deben desplazar de derecha a izquierda:
	        		 * El bit-0 7 posiciones, el bit-1 6 posiciones, el bit-2 5 posiciones, etc
	        		 * posiciones = 7 - bit_index = ((BITS_BYTE - 1) -  (x + offset) % BITS_BYTE)
	        		 */
	        		pixelBits[y * width + byteIndex] |= (byte)(colorBit << ((Byte.SIZE - 1) - (x + offset) % Byte.SIZE)) & 0xFF;
	        	}
            }
	    }
        return pixelBits;
    }

	protected void setData(BufferedImage biImage) {
		if (biImage.getType() != BufferedImage.TYPE_BYTE_BINARY) {
			throw new IllegalArgumentException("Imagen no soportada: sólo colores blanco y negro son válidos.");
		}
		if (biImage.getWidth() > MAX_WIDTH) {
			throw new IllegalArgumentException("El ancho de la imagen supera el máximo permitido (" + MAX_WIDTH + ").");
		}

		this.biData = new BufferedImage(biImage.getWidth(), biImage.getHeight(), biImage.getType());
		Graphics g = biData.getGraphics();
		g.drawImage(biImage, 0, 0, null);
		g.dispose();
	}

	protected byte[] dataSize(int width, int height) {
		byte[] bytes = new byte[4];

		// Alto en pixeles, separar por bytes
		bytes[0] = (byte) (height >> Byte.SIZE & 0xFF);
		bytes[1] = (byte) (height & 0xFF);
		// Ancho en pixeles, separar por bytes
		bytes[2] = (byte) (MAX_WIDTH >> Byte.SIZE & 0xFF);
		bytes[3] = (byte) (MAX_WIDTH & 0xFF);

		return bytes;
	}

    public void writeToFile(String path, String prefix) {
        FileOutputStream out = null;
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
        }
        File file = new File(path, prefix + getNameSufix());
        try {
            out = new FileOutputStream(file);
            writeImage(out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(out);
            out = null;
        }
    }

    private void close(Closeable closeable) {
		if (closeable == null) {
			return;
		}
		try {
			closeable.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    protected abstract void writeImage(OutputStream out) throws IOException;

    protected abstract String getNameSufix();
}
