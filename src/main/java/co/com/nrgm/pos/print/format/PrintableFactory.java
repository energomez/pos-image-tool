package co.com.nrgm.pos.print.format;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import co.com.nrgm.pos.print.format.pos.PrintableDotMatrix;
import co.com.nrgm.pos.print.format.pos.PrintableNurit;
import co.com.nrgm.pos.print.format.pos.PrintablePax;
import co.com.nrgm.pos.print.format.pos.PrintableSpectra;
import co.com.nrgm.pos.print.format.pos.PrintableThermal;

public final class PrintableFactory {
    private static Map<PrintableType, Class<? extends PrintableImage>> map = new HashMap<>();
    static {
        map.put(PrintableType.DOT_MATRIX, PrintableDotMatrix.class);
        map.put(PrintableType.NURIT, PrintableNurit.class);
        map.put(PrintableType.PAX, PrintablePax.class);
        map.put(PrintableType.SPECTRA, PrintableSpectra.class);
        map.put(PrintableType.THERMAL, PrintableThermal.class);
    }

    private PrintableFactory() {
        super();
    }

    public static PrintableImage getPrintable(BufferedImage biImage, PrintableType type) {
        Class<? extends PrintableImage> classRef = map.get(type);
        try {
            return classRef.getConstructor(BufferedImage.class).newInstance(biImage);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException("El tipo " + type + " no es instanciable", e);
        }
    }
}
