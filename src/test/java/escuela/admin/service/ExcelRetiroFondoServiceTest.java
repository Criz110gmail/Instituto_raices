package escuela.admin.service;

import escuela.admin.dto.FiltroRetiroFondo;
import escuela.admin.dto.RetiroFondoFila;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExcelRetiroFondoServiceTest {
    @Test
    void exportaElMismoFiltroQueLaPantalla() throws Exception {
        RetiroFondoConsultaService consulta = mock(RetiroFondoConsultaService.class);
        RetiroFondoFila fila = new RetiroFondoFila(7L, "15 sep 2026 · 12:00", "Caja",
                "Plantel Centro", "Proveedor Uno", "Servicios", "Material", "FACT-7",
                new BigDecimal("25.00"), "MXN", "admin", "EJECUTADO", 20L);
        when(consulta.consultar(any())).thenReturn(new PageImpl<>(List.of(fila), PageRequest.of(0, 100), 1));
        FiltroRetiroFondo filtro = new FiltroRetiroFondo(1L, 3L, "Caja", 2L,
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30), "Proveedor", 0, 25);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        new ExcelRetiroFondoService(consulta).exportar(filtro, salida);
        try (XSSFWorkbook libro = new XSSFWorkbook(new ByteArrayInputStream(salida.toByteArray()))) {
            assertThat(libro.getSheetAt(0).getRow(1).getCell(4).getStringCellValue())
                    .isEqualTo("Proveedor Uno");
            assertThat(libro.getSheetAt(0).getRow(1).getCell(8).getNumericCellValue())
                    .isEqualTo(25.0);
        }
        var usado = org.mockito.ArgumentCaptor.forClass(FiltroRetiroFondo.class);
        verify(consulta).consultar(usado.capture());
        assertThat(usado.getValue().fechaDesde()).isEqualTo(filtro.fechaDesde());
        assertThat(usado.getValue().fechaHasta()).isEqualTo(filtro.fechaHasta());
        assertThat(usado.getValue().beneficiario()).isEqualTo("Proveedor");
        assertThat(usado.getValue().cuentaId()).isEqualTo(3L);
        assertThat(usado.getValue().pagina()).isZero();
        assertThat(usado.getValue().tamanio()).isEqualTo(100);
    }
}
