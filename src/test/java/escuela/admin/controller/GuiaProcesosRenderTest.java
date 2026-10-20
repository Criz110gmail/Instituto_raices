package escuela.admin.controller;

import escuela.admin.service.GuiaProcesosService;
import escuela.admin.service.GuiaProcesosService.*;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class GuiaProcesosRenderTest {
    @Test void permisosLaboralesRenderizaOchoPasosYExcelSinOperarRoles() throws Exception {
        var sql=java.nio.file.Files.readString(java.nio.file.Path.of("src/main/resources/db/migration/V115__guia_permisos_laborales.sql"));
        var patron=java.util.regex.Pattern.compile("(?m)^\\((\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$,'(.*?)'\\)");
        var pasos=patron.matcher(sql).results().map(m->new Paso(Integer.parseInt(m.group(1)),m.group(2),"Administración",m.group(3),m.group(4),m.group(5),m.group(6),m.group(7),m.group(8))).toList();
        assertThat(pasos).hasSize(8);
        var principal=new UsuarioPrincipal(1L,2L,Set.of(),true,false,"admin","",List.of(new SimpleGrantedAuthority("GUIA_PROCESOS_CONSULTAR")));
        var guia=new Guia(5L,"empleado-permisos-laborales","Permisos de empleados: proteger datos laborales y verificar Excel","Personal","Recorrido confirmado","Cuenta de capacitación","EMP-ADM-PRUEBA-01",1,LocalDate.of(2026,10,20),"CONFIRMADA");
        var service=mock(GuiaProcesosService.class);
        when(service.obtener(principal,guia.slug())).thenReturn(new Detalle(guia,pasos));
        when(service.listar(eq(principal),any())).thenReturn(new PageImpl<>(List.of(guia)));
        var templates=new ClassLoaderTemplateResolver();templates.setPrefix("templates/");templates.setSuffix(".html");templates.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(templates);
        var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new GuiaProcesosAdminController(service)).setViewResolvers(views).setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver()).build();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal,"",principal.authorities()));
        try {
            var detalle=mvc.perform(get("/admin/guias/"+guia.slug())).andReturn().getResponse();
            assertThat(detalle.getStatus()).isEqualTo(200);
            assertThat(detalle.getContentAsString()).contains("paso-8","20/10/2026","Exportar Excel","#laboral-empleado","no acredita todos los endpoints privados","/admin/catalogos/roles","/admin/catalogos/usuarios");
            var excel=mvc.perform(get("/admin/guias/excel").param("q","permisos").param("categoria","Personal")).andReturn().getResponse();
            try(var libro=new XSSFWorkbook(new ByteArrayInputStream(excel.getContentAsByteArray()))) {
                assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(8);
                assertThat(libro.getSheetAt(0).getRow(6).getCell(10).getStringCellValue()).contains("Código laboral","sin información laboral privada");
                assertThat(libro.getSheetAt(0).getRow(8).getCell(10).getStringCellValue()).contains("cierra sesión");
                assertThat(libro.getSheetAt(0).getRow(8).getCell(13).getStringCellValue()).contains("todavía no existen formularios de sueldo");
            }
        } finally {SecurityContextHolder.clearContext();}
    }
    @Test void guiasPersonalRenderizanYExportanSusQuincePasosReales() throws Exception {
        var sql=java.nio.file.Files.readString(java.nio.file.Path.of("src/main/resources/db/migration/V113__guias_empleados_planteles.sql"));
        var patron=java.util.regex.Pattern.compile("(?m)^\\((\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$\\)");
        var pasos=patron.matcher(sql).results().map(m->new Paso(Integer.parseInt(m.group(1)),m.group(2),"Administración",m.group(3),m.group(4),m.group(5),m.group(6),m.group(7),"/admin/empleados")).toList();
        assertThat(pasos).hasSize(15);
        var principal=new UsuarioPrincipal(1L,2L,Set.of(),true,false,"admin","",List.of(new SimpleGrantedAuthority("GUIA_PROCESOS_CONSULTAR")));
        var alta=new Guia(3L,"empleado-administrativo-sin-perfil-docente","Registrar un empleado administrativo sin perfil docente","Personal","Confirmado","Expediente autorizado","María Prueba",1,LocalDate.of(2026,10,20),"CONFIRMADA");
        var planteles=new Guia(4L,"empleado-planteles-cambio-principal","Varios planteles y cambio programado de principal","Personal","Confirmado","Dos planteles","A y B",1,LocalDate.of(2026,10,20),"CONFIRMADA");
        var service=mock(GuiaProcesosService.class);
        when(service.listar(eq(principal),any())).thenReturn(new PageImpl<>(List.of(alta,planteles)));
        when(service.categorias(principal)).thenReturn(List.of("Personal"));
        when(service.obtener(principal,alta.slug())).thenReturn(new Detalle(alta,pasos.subList(0,7)));
        when(service.obtener(principal,planteles.slug())).thenReturn(new Detalle(planteles,pasos.subList(7,15)));
        var templates=new ClassLoaderTemplateResolver();templates.setPrefix("templates/");templates.setSuffix(".html");templates.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(templates);
        var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new GuiaProcesosAdminController(service)).setViewResolvers(views).setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver()).build();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal,"",principal.authorities()));
        try {
            var lista=mvc.perform(get("/admin/guias").param("categoria","Personal")).andReturn().getResponse();
            assertThat(lista.getStatus()).isEqualTo(200);assertThat(lista.getContentAsString()).contains("value=\"Personal\" selected=\"selected\"","categoria=Personal");
            var detalle=mvc.perform(get("/admin/guias/"+planteles.slug())).andReturn().getResponse();
            assertThat(detalle.getStatus()).isEqualTo(200);assertThat(detalle.getContentAsString()).contains("paso-8","Cambio de principal programado","21/10/2026","no repitas la operación");
            var excel=mvc.perform(get("/admin/guias/excel").param("categoria","Personal")).andReturn().getResponse();
            try(var libro=new XSSFWorkbook(new ByteArrayInputStream(excel.getContentAsByteArray()))) {
                assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(15);
                assertThat(libro.getSheetAt(0).getRow(15).getCell(10).getStringCellValue()).contains("baja laboral","permisos privados");
            }
        } finally {SecurityContextHolder.clearContext();}
    }
    @Test void planeacionesRenderizaDiezPasosYExportaSuContenidoReal() throws Exception {
        var sql=java.nio.file.Files.readString(java.nio.file.Path.of("src/main/resources/db/migration/V112__guia_planeaciones_docentes.sql"));
        var patron=java.util.regex.Pattern.compile("(?m)^\\((\\d+),\\$texto\\$(.*?)\\$texto\\$,'(.*?)',\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$,(NULL|'(.*?)')\\)");
        var pasos=patron.matcher(sql).results().map(m->new Paso(Integer.parseInt(m.group(1)),m.group(2),m.group(3),m.group(4),m.group(5),m.group(6),m.group(7),m.group(8),m.group(10))).toList();
        assertThat(pasos).hasSize(10);
        var principal=new UsuarioPrincipal(1L,2L,Set.of(),true,false,"admin","",List.of(
                new SimpleGrantedAuthority("GUIA_PROCESOS_CONSULTAR"),new SimpleGrantedAuthority("PLANEACION_LEER")));
        var guia=new Guia(2L,"planeaciones-revision-versiones-pdf","Planeaciones docentes: captura, revisión y versiones PDF",
                "Gestión académica","Recorrido confirmado","Cuenta docente y asignaciones","Sumas con fichas",1,LocalDate.of(2026,10,19),"CONFIRMADA");
        var service=mock(GuiaProcesosService.class);
        when(service.obtener(principal,guia.slug())).thenReturn(new Detalle(guia,pasos));
        when(service.listar(eq(principal),any())).thenReturn(new PageImpl<>(List.of(guia)));
        var templates=new ClassLoaderTemplateResolver();templates.setPrefix("templates/");templates.setSuffix(".html");templates.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(templates);
        var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new GuiaProcesosAdminController(service)).setViewResolvers(views)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver()).build();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal,"",principal.authorities()));
        try {
            var detalle=mvc.perform(get("/admin/guias/"+guia.slug())).andReturn().getResponse();
            assertThat(detalle.getStatus()).isEqualTo(200);
            assertThat(detalle.getContentAsString()).contains("paso-10","19/10/2026","Confirmar reapertura",
                    "Ver PDF · Versión 2 — Nueva publicación","/admin/maestros","target=\"_blank\"");
            var excel=mvc.perform(get("/admin/guias/excel").param("q","planeaciones").param("categoria","Gestión académica")).andReturn().getResponse();
            try(var libro=new XSSFWorkbook(new ByteArrayInputStream(excel.getContentAsByteArray()))) {
                assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(10);
                assertThat(libro.getSheetAt(0).getRow(10).getCell(10).getStringCellValue()).contains("Versión 1","Versión 2","no publiques ni reabras otra vez");
            }
        } finally {SecurityContextHolder.clearContext();}
    }

    @Test void listadoDetalleYExcelConservanFiltrosYNoEjecutanPagos() throws Exception {
        var principal=new UsuarioPrincipal(1L,2L,Set.of(),true,false,"admin","",
                List.of(new SimpleGrantedAuthority("GUIA_PROCESOS_CONSULTAR")));
        var guia=new Guia(1L,"transferencia-dos-hijos","Una transferencia para dos hijos","Cobranza",
                "Caso confirmado","Dos inscripciones y mismo tutor","$600 + $400 = $1,000",1,LocalDate.of(2026,10,6),"CONFIRMADA");
        var paso=new Paso(1,"Preparar cuotas","Administración","Cuotas por alumno",
                "1. Selecciona inscripción. 2. Guarda cuota.","Ana $600","Sin ingreso","No duplicar cuotas","/admin/catalogos/cuotas-alumno");
        var service=mock(GuiaProcesosService.class);
        when(service.listar(eq(principal),any())).thenReturn(new PageImpl<>(List.of(guia),PageRequest.of(0,25),26));
        when(service.obtener(principal,guia.slug())).thenReturn(new Detalle(guia,List.of(paso)));
        var templates=new ClassLoaderTemplateResolver();templates.setPrefix("templates/");templates.setSuffix(".html");templates.setCharacterEncoding("UTF-8");
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(templates);
        var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
        var mvc=MockMvcBuilders.standaloneSetup(new GuiaProcesosAdminController(service)).setViewResolvers(views)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver()).build();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal,"",principal.authorities()));
        try {
            var listado=mvc.perform(get("/admin/guias").param("q","dos hijos").param("categoria","Cobranza").param("estado","CONFIRMADA")).andReturn().getResponse();
            assertThat(listado.getStatus()).isEqualTo(200);
            assertThat(listado.getContentAsString()).contains("Una transferencia para dos hijos","Ver paso a paso","estado=CONFIRMADA","pagina=1");
            var detalle=mvc.perform(get("/admin/guias/transferencia-dos-hijos")).andReturn().getResponse();
            assertThat(detalle.getStatus()).isEqualTo(200);
            assertThat(detalle.getContentAsString()).contains("06/10/2026","paso-1","<li>Selecciona inscripción.</li>","target=\"_blank\"","Esta guía orienta");
            when(service.listar(eq(principal),any())).thenReturn(new PageImpl<>(List.of(guia)));
            var excel=mvc.perform(get("/admin/guias/excel").param("q","dos hijos").param("categoria","Cobranza").param("estado","CONFIRMADA")).andReturn().getResponse();
            try(var libro=new XSSFWorkbook(new ByteArrayInputStream(excel.getContentAsByteArray()))) {
                assertThat(libro.getSheetAt(0).getRow(1).getCell(0).getStringCellValue()).isEqualTo(guia.titulo());
                assertThat(libro.getSheetAt(0).getRow(1).getCell(10).getStringCellValue()).contains("Selecciona inscripción");
            }
            verify(service,times(2)).listar(eq(principal),argThat(f->f.q().equals("dos hijos") && f.categoria().equals("Cobranza") && f.estado().equals("CONFIRMADA")));
        } finally {SecurityContextHolder.clearContext();}
    }
}
