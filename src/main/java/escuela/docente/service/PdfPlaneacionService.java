package escuela.docente.service;

import escuela.docente.dto.PlaneacionDocumento;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.springframework.stereotype.Service;
import java.awt.Color;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service @RequiredArgsConstructor
public class PdfPlaneacionService {
    private static final float M=42,W=PDRectangle.LETTER.getWidth()-84,TOP=PDRectangle.LETTER.getHeight()-42,BOTTOM=52;
    private static final PDFont REG=new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDFont BOLD=new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final DateTimeFormatter FECHA=DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void exportar(PlaneacionDocumento d,OutputStream out){try(PDDocument pdf=new PDDocument()){Writer w=new Writer(pdf,d);w.nuevaPagina();w.titulo("PLANEACIÓN SEMANAL",18);w.texto(d.maestro()+" · "+d.numeroEmpleado(),11,BOLD);w.texto(d.plantel()+" · "+d.grado()+" · "+d.grupo(),10,REG);w.texto(d.ciclo()+" · "+FECHA.format(d.fechaInicio())+" al "+FECHA.format(d.fechaFin()),10,REG);w.etiqueta("Estado: "+d.estado()+(d.revision()>0?" · Revisión "+d.revision():""));w.separador();w.seccion("Propósito",d.proposito());w.seccion("Situación didáctica",d.situacionDidactica());w.seccion("Ejes articuladores",d.ejesArticuladores());w.tresColumnas("Conocimientos",d.conocimientos(),"Habilidades",d.habilidades(),"Actitudes",d.actitudes());w.dosColumnas("Técnica de evaluación",d.tecnicaEvaluacion(),"Instrumento de evaluación",d.instrumentoEvaluacion());w.seccion("Materias seleccionadas",String.join(" · ",d.materias().stream().map(PlaneacionDocumento.Materia::nombre).toList()));if(!d.alineaciones().isEmpty()){w.encabezado("Alineación curricular");for(var a:d.alineaciones()){w.tarjeta(a.materia()+" · "+a.campoFormativo(),"Contenido: "+a.contenido()+"\nProceso de desarrollo de aprendizaje: "+a.procesoDesarrollo());}}w.dosColumnas("Recursos",d.recursos(),"Actividades permanentes",d.actividadesPermanentes());w.seccion("Ajustes razonables",d.ajustesRazonables());if(!d.actividades().isEmpty())w.asegurar(35+w.altoActividad(d.actividades().getFirst()));w.encabezado("Secuencia semanal");for(var a:d.actividades()){w.actividad(a);}w.seccion("Observaciones generales",d.observaciones());w.pies();pdf.save(out);}catch(IOException e){throw new IllegalStateException("No fue posible generar el PDF de la planeación",e);}}

    private static class Writer{
        final PDDocument pdf;final PlaneacionDocumento documento;PDPage page;PDPageContentStream c;float y;
        Writer(PDDocument pdf,PlaneacionDocumento documento){this.pdf=pdf;this.documento=documento;}
        void nuevaPagina()throws IOException{cerrar();page=new PDPage(PDRectangle.LETTER);pdf.addPage(page);c=new PDPageContentStream(pdf,page);c.setNonStrokingColor(new Color(14,42,67));c.addRect(0,PDRectangle.LETTER.getHeight()-27,PDRectangle.LETTER.getWidth(),27);c.fill();linea(documento.institucion(),M,PDRectangle.LETTER.getHeight()-19,8,BOLD,Color.WHITE);y=TOP-16;}
        void asegurar(float h)throws IOException{if(y-h<BOTTOM)nuevaPagina();}
        void titulo(String s,float size)throws IOException{linea(s,M,y,size,BOLD,new Color(14,42,67));y-=size+8;}
        void texto(String s,float size,PDFont f)throws IOException{for(String l:wrap(valor(s),f,size,W)){linea(l,M,y,size,f,new Color(38,53,65));y-=size+3;}}
        void etiqueta(String s)throws IOException{float an=Math.min(W,ancho(s,BOLD,8)+18);c.setNonStrokingColor(new Color(225,247,243));c.addRect(M,y-12,an,18);c.fill();linea(s,M+8,y-7,8,BOLD,new Color(0,112,98));y-=26;}
        void separador()throws IOException{c.setStrokingColor(new Color(214,224,232));c.moveTo(M,y);c.lineTo(M+W,y);c.stroke();y-=13;}
        void encabezado(String s)throws IOException{asegurar(35);y-=4;linea(s,M,y,12,BOLD,new Color(14,42,67));y-=18;}
        void seccion(String t,String v)throws IOException{if(v==null||v.isBlank())return;List<String> ls=wrap(v,REG,9,W-20);float h=30+ls.size()*12;asegurar(h);c.setNonStrokingColor(new Color(245,248,251));c.addRect(M,y-h+8,W,h);c.fill();linea(t,M+10,y-10,9,BOLD,new Color(14,42,67));float yy=y-25;for(String l:ls){linea(l,M+10,yy,9,REG,new Color(48,63,75));yy-=12;}y-=h+7;}
        void tresColumnas(String t1,String v1,String t2,String v2,String t3,String v3)throws IOException{columnas(List.of(t1,t2,t3),List.of(valor(v1),valor(v2),valor(v3)));}
        void dosColumnas(String t1,String v1,String t2,String v2)throws IOException{if((v1==null||v1.isBlank())&&(v2==null||v2.isBlank()))return;columnas(List.of(t1,t2),List.of(valor(v1),valor(v2)));}
        void columnas(List<String> ts,List<String> vs)throws IOException{float gap=8,cw=(W-gap*(ts.size()-1))/ts.size();List<List<String>> all=vs.stream().map(v->wrap(v,REG,8,cw-16)).toList();int n=all.stream().mapToInt(List::size).max().orElse(1);float h=28+n*11;asegurar(h);for(int i=0;i<ts.size();i++){float x=M+i*(cw+gap);c.setNonStrokingColor(new Color(245,248,251));c.addRect(x,y-h+8,cw,h);c.fill();linea(ts.get(i),x+8,y-10,8,BOLD,new Color(14,42,67));float yy=y-24;for(String l:all.get(i)){linea(l,x+8,yy,8,REG,new Color(48,63,75));yy-=11;}}y-=h+7;}
        void tarjeta(String titulo,String cuerpo)throws IOException{List<String> ls=wrap(cuerpo,REG,8,W-24);float h=28+ls.size()*11;asegurar(h);c.setStrokingColor(new Color(206,219,228));c.setNonStrokingColor(Color.WHITE);c.addRect(M,y-h+8,W,h);c.fillAndStroke();linea(titulo,M+11,y-10,8,BOLD,new Color(0,112,98));float yy=y-24;for(String l:ls){linea(l,M+11,yy,8,REG,new Color(48,63,75));yy-=11;}y-=h+7;}
        void actividad(PlaneacionDocumento.Actividad a)throws IOException{tarjeta(FECHA.format(a.fecha())+" · "+a.materia()+" · "+a.titulo(),cuerpoActividad(a));}
        float altoActividad(PlaneacionDocumento.Actividad a){return 28+wrap(cuerpoActividad(a),REG,8,W-24).size()*11;}
        String cuerpoActividad(PlaneacionDocumento.Actividad a){return "INICIO\n"+valor(a.inicio())+"\nDESARROLLO\n"+valor(a.desarrollo())+"\nCIERRE\n"+valor(a.cierre())+(a.tarea()==null||a.tarea().isBlank()?"":"\nTAREA\n"+a.tarea())+(a.duracionMinutos()==null?"":"\nDuración estimada: "+a.duracionMinutos()+" minutos");}
        void pies()throws IOException{cerrar();int total=pdf.getNumberOfPages();for(int i=0;i<total;i++){PDPage p=pdf.getPage(i);try(PDPageContentStream f=new PDPageContentStream(pdf,p,PDPageContentStream.AppendMode.APPEND,true)){String iz="Nexo Escolar · Planeación "+(documento.revision()>0?"revisión "+documento.revision():"en "+documento.estado().toLowerCase());linea(f,iz,M,27,7,REG,new Color(95,110,122));String der="Página "+(i+1)+" de "+total;linea(f,der,PDRectangle.LETTER.getWidth()-M-ancho(der,REG,7),27,7,REG,new Color(95,110,122));}}}
        void cerrar()throws IOException{if(c!=null){c.close();c=null;}}
        void linea(String s,float x,float yy,float size,PDFont f,Color color)throws IOException{linea(c,s,x,yy,size,f,color);}
        static void linea(PDPageContentStream cs,String s,float x,float y,float size,PDFont f,Color color)throws IOException{cs.beginText();cs.setFont(f,size);cs.setNonStrokingColor(color);cs.newLineAtOffset(x,y);cs.showText(seguro(s));cs.endText();}
        static List<String> wrap(String text,PDFont f,float size,float width){List<String> out=new ArrayList<>();for(String par:valor(text).split("\\R",-1)){if(par.isBlank()){out.add("");continue;}String line="";for(String word:par.trim().split("\\s+")){String next=line.isEmpty()?word:line+" "+word;if(ancho(next,f,size)<=width)line=next;else{if(!line.isEmpty())out.add(line);line=word;}}if(!line.isEmpty())out.add(line);}return out.isEmpty()?List.of(""):out;}
        static float ancho(String s,PDFont f,float size){try{return f.getStringWidth(seguro(s))/1000*size;}catch(IOException e){return s.length()*size*.5f;}}
        static String seguro(String s){return valor(s).replace("•","-").replace("–","-").replace("—","-").replace("“","\"").replace("”","\"");}
        static String valor(String s){return s==null?"":s;}
    }
}
