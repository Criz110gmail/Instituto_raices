package escuela.common.support;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/** Conserva disponible la respuesta HTML de error hasta el primer byte del archivo. */
public final class SalidaArchivoHttp extends OutputStream {
    private final HttpServletResponse response;
    private final String tipo;
    private final String disposicion;
    private OutputStream destino;

    public SalidaArchivoHttp(HttpServletResponse response, String tipo, String disposicion) {
        this.response=response;this.tipo=tipo;this.disposicion=disposicion;
    }
    private OutputStream abrir() throws IOException {
        if(destino==null) {
            response.setContentType(tipo);
            response.setHeader("Content-Disposition",disposicion);
            response.setHeader("Cache-Control","no-store");
            destino=response.getOutputStream();
        }
        return destino;
    }
    @Override public void write(int valor) throws IOException { abrir().write(valor); }
    @Override public void write(byte[] datos,int inicio,int cantidad) throws IOException {
        Objects.checkFromIndexSize(inicio,cantidad,datos.length);
        if(cantidad>0)abrir().write(datos,inicio,cantidad);
    }
    @Override public void flush() throws IOException { if(destino!=null)destino.flush(); }
    // La respuesta pertenece al contenedor, no al generador de reportes.
    @Override public void close() throws IOException { flush(); }
}
