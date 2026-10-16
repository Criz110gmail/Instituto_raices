package escuela.admin.service;

import escuela.common.support.SalidaArchivoHttp;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletOutputStream;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class SalidaArchivoHttpTest {
    @Test void sinBytesNiFlushAbrenLaRespuestaBinaria() throws Exception {
        var response=mock(HttpServletResponse.class);var salida=new SalidaArchivoHttp(response,"application/pdf","inline");
        salida.write(new byte[0]);salida.flush();salida.close();verifyNoInteractions(response);
    }
    @Test void primerBytePreparaCabecerasYReutilizaStreamSinAcumularDatos() throws Exception {
        var response=mock(HttpServletResponse.class);var stream=mock(ServletOutputStream.class);when(response.getOutputStream()).thenReturn(stream);
        var salida=new SalidaArchivoHttp(response,"application/pdf","inline");byte[] datos={1,2,3};
        salida.write(datos,1,2);salida.write(7);salida.flush();
        verify(response).setContentType("application/pdf");verify(response).setHeader("Content-Disposition","inline");
        verify(response).setHeader("Cache-Control","no-store");verify(response,times(1)).getOutputStream();
        verify(stream).write(datos,1,2);verify(stream).write(7);verify(stream).flush();
    }
}
