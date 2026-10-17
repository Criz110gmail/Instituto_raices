package escuela.admin.service;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.Units;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

/** Identidad compartida del sistema; no sustituye los datos de la institución. */
final class MarcaReporteFinanciero {
    static final String RECURSO = "reports/assets/logo-institucion-ejemplo.png";
    static final int FILAS = 4;
    private MarcaReporteFinanciero() {}

    private static final class Logo {
        static final byte[] BYTES = cargar();
        static final BufferedImage IMAGEN = imagen();
        private static byte[] cargar() {
            try (var in = new ClassPathResource(RECURSO).getInputStream()) { return in.readAllBytes(); }
            catch (IOException e) { throw new IllegalStateException("No fue posible leer el logo de los reportes", e); }
        }
        private static BufferedImage imagen() {
            try {
                var imagen = ImageIO.read(new ByteArrayInputStream(BYTES));
                if (imagen == null) throw new IOException("Imagen no compatible");
                return imagen;
            } catch (IOException e) { throw new IllegalStateException("No fue posible procesar el logo de los reportes", e); }
        }
    }

    static BufferedImage imagen() { return Logo.IMAGEN; }

    static void excel(Workbook libro, Sheet hoja, int columnas) {
        excel(libro, hoja, columnas, hoja.getSheetName());
    }

    static void excel(Workbook libro, Sheet hoja, int columnas, String titulo) {
        // El relleno oculta la cuadrícula sólo en la cabecera, no en toda la hoja.
        CellStyle fondo = libro.createCellStyle();
        fondo.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        fondo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        for (int i=0; i<FILAS; i++) {
            Row fila = hoja.createRow(i); fila.setHeightInPoints(30);
            for (int c=0; c<columnas; c++) fila.createCell(c).setCellStyle(fondo);
        }
        // Las hojas Resumen tienen una primera columna ancha; en tablas se reservan
        // dos columnas para que el texto nunca cubra la imagen de 192px.
        int inicioTitulo = columnas == 2 ? 1 : 2;
        CellStyle estiloTitulo = libro.createCellStyle(); estiloTitulo.cloneStyleFrom(fondo);
        estiloTitulo.setAlignment(HorizontalAlignment.CENTER);
        estiloTitulo.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloTitulo.setWrapText(true);
        Font fuente = libro.createFont(); fuente.setFontName("Calibri");
        fuente.setFontHeightInPoints((short)18); fuente.setBold(true);
        fuente.setColor(IndexedColors.DARK_BLUE.getIndex()); estiloTitulo.setFont(fuente);
        Cell celdaTitulo = hoja.getRow(0).getCell(inicioTitulo);
        celdaTitulo.setCellValue(titulo); celdaTitulo.setCellStyle(estiloTitulo);
        hoja.addMergedRegion(new CellRangeAddress(0, 2, inicioTitulo, columnas-1));
        double escala = Math.min(192.0/Logo.IMAGEN.getWidth(), 128.0/Logo.IMAGEN.getHeight());
        int ancho = Units.pixelToEMU((int)Math.round(Logo.IMAGEN.getWidth()*escala));
        int alto = Units.pixelToEMU((int)Math.round(Logo.IMAGEN.getHeight()*escala));
        ClientAnchor ancla = libro.getCreationHelper().createClientAnchor();
        ancla.setCol1(0); ancla.setRow1(0); ancla.setCol2(0); ancla.setRow2(0);
        ancla.setDx1(Units.pixelToEMU(8)); ancla.setDy1(Units.pixelToEMU(4));
        ancla.setDx2(Units.pixelToEMU(8)+ancho);
        ancla.setDy2(Units.pixelToEMU(4)+alto);
        int imagen = -1;
        for (int i=0;i<libro.getAllPictures().size();i++) {
            if (java.util.Arrays.equals(libro.getAllPictures().get(i).getData(), Logo.BYTES)) { imagen=i; break; }
        }
        if (imagen<0) imagen=libro.addPicture(Logo.BYTES, Workbook.PICTURE_TYPE_PNG);
        XSSFWorkbook xlsx = libro instanceof SXSSFWorkbook streaming ? streaming.getXSSFWorkbook() : (XSSFWorkbook)libro;
        var dibujo = xlsx.getSheet(hoja.getSheetName()).createDrawingPatriarch();
        var foto = dibujo.createPicture(ancla, imagen);
        // Ancla absoluta real: Excel no debe recalcular proporciones al interpretar
        // puntos de inicio/fin de celda o al cambiar anchos y alturas de la tabla.
        var xml = dibujo.getCTDrawing();
        var absoluta = xml.addNewAbsoluteAnchor();
        var posicion = absoluta.addNewPos();
        posicion.setX(Units.pixelToEMU(12)); posicion.setY(Units.pixelToEMU(10));
        var tamano = absoluta.addNewExt(); tamano.setCx(ancho); tamano.setCy(alto);
        absoluta.setPic(foto.getCTPicture()); absoluta.addNewClientData();
        var transformacion = absoluta.getPic().getSpPr().getXfrm();
        transformacion.getOff().setX(posicion.getX()); transformacion.getOff().setY(posicion.getY());
        transformacion.getExt().setCx(ancho); transformacion.getExt().setCy(alto);
        absoluta.getPic().getNvPicPr().getCNvPicPr().getPicLocks().setNoChangeAspect(true);
        xml.removeTwoCellAnchor(xml.sizeOfTwoCellAnchorArray()-1);
    }
}
