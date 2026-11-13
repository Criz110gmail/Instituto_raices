"""Presentación comercial del producto con ejemplos ficticios y gráficos vectoriales.
Ejecutar con ReportLab. No consulta la base ni usa datos personales reales.
"""
from pathlib import Path
import math, re
from xml.sax.saxutils import escape
from reportlab.pdfgen import canvas
from reportlab.lib.colors import HexColor, Color, white
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import Paragraph
from reportlab.lib.styles import ParagraphStyle

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'output/pdf/presentacion_sistema_gestion_escolar.pdf'
OUT.parent.mkdir(parents=True, exist_ok=True)
FONT = Path('/System/Library/Fonts/Supplemental')
for name, file in [('Body','Arial.ttf'),('Bold','Arial Bold.ttf'),('Italic','Arial Italic.ttf'),('Editorial','Georgia Italic.ttf')]:
    pdfmetrics.registerFont(TTFont(name, str(FONT/file)))
pdfmetrics.registerFontFamily('Body',normal='Body',bold='Bold',italic='Italic',boldItalic='Bold')
W,H = 960,540
NAVY='#123238'; GREEN='#1B554C'; TEAL='#2FAF99'; LIME='#CEE9A2'; CREAM='#F5F4EE'; INK='#16383D'; MUTED='#536D6C'; LINE='#DAE4DF'; GOLD='#E7BC70'
c = canvas.Canvas(str(OUT),pagesize=(W,H),pageCompression=1)
c.setTitle('Plataforma de gestión escolar | Presentación del producto')
c.setAuthor('Presentación de producto')
c.setSubject('Funciones del sistema y ejemplos sencillos con datos ficticios')
c.setCreator('Presentación editorial de la plataforma de gestión escolar')
PAGE=0
TOTAL=0

def color(v): return HexColor(v) if isinstance(v,str) else v

def box(x,y,w,h,fill=white,r=12,stroke=None):
    c.setFillColor(color(fill))
    c.setStrokeColor(color(stroke or fill));c.setLineWidth(.7)
    c.roundRect(x,H-y-h,w,h,r,stroke=bool(stroke),fill=1)

def line(x,y,x2,y2,col=LINE,width=1):
    c.setStrokeColor(color(col));c.setLineWidth(width);c.line(x,H-y,x2,H-y2)

def text(t,x,y,size=14,font='Body',col=INK):
    c.setFillColor(color(col));c.setFont(font,size);c.drawString(x,H-y-size*.78,t)

def para(t,x,y,w,size=14,col=INK,font='Body',leading=None,maxh=None):
    style=ParagraphStyle('p',fontName=font,fontSize=size,leading=leading or size*1.4,textColor=color(col),spaceAfter=0)
    p=Paragraph(t,style); _,h=p.wrap(w,1000)
    if maxh is not None and h>maxh+.1: raise ValueError(f'Página {PAGE}, texto excede {h:.1f}>{maxh}: {t[:70]}')
    if y+h>H-24: raise ValueError(f'Página {PAGE}, texto fuera de hoja: {t[:70]}')
    p.drawOn(c,x,H-y-h)
    return h

def pill(t,x,y,fill=LIME,col=GREEN,size=9):
    width=pdfmetrics.stringWidth(t,'Bold',size)+22
    box(x,y,width,24,fill,12);text(t,x+11,y+8,size,'Bold',col)
    return width

def icon(kind,x,y,col=TEAL,s=22):
    c.saveState();c.translate(x,H-y-s);c.setStrokeColor(color(col));c.setLineWidth(1.7)
    if kind=='book':
        c.lines([(0,2,0,s-2),(0,s-2,s/2,s-5),(s/2,s-5,s,s-2),(s,s-2,s,2),(s,2,s/2,0),(s/2,0,0,2),(s/2,0,s/2,s-5)])
    elif kind=='people':
        c.circle(s*.38,s*.72,s*.17,stroke=1,fill=0);c.arc(0,0,s*.76,s*.5,0,180);c.circle(s*.85,s*.7,s*.11,stroke=1,fill=0);c.arc(s*.64,1,s*1.15,s*.4,0,130)
    elif kind=='money':
        c.roundRect(0,2,s,s*.68,3,stroke=1,fill=0);c.circle(s*.5,s*.45,s*.18,stroke=1,fill=0);c.line(3,6,5,6)
    elif kind=='shield':
        p=c.beginPath();p.moveTo(s*.5,s);p.lineTo(s,s*.8);p.lineTo(s*.9,s*.25);p.lineTo(s*.5,0);p.lineTo(s*.1,s*.25);p.lineTo(0,s*.8);p.close();c.drawPath(p);c.lines([(s*.25,s*.5,s*.44,s*.3),(s*.44,s*.3,s*.75,s*.66)])
    else:
        c.roundRect(0,0,s,s,4,stroke=1,fill=0);c.lines([(4,s*.7,s-4,s*.7),(4,s*.45,s-4,s*.45),(4,s*.2,s*.55,s*.2)])
    c.restoreState()

def start(chapter,title,subtitle,dark=False):
    global PAGE
    PAGE+=1;c.bookmarkPage(f'p{PAGE}');c.addOutlineEntry(title,f'p{PAGE}',level=0,closed=False)
    box(0,0,W,H,NAVY if dark else CREAM,0)
    text('GESTIÓN ESCOLAR',42,25,9,'Bold',LIME if dark else GREEN)
    text(chapter.upper(),640,25,8.5,'Bold','#B4CCC6' if dark else MUTED)
    line(42,47,918,47,'#345A5D' if dark else LINE)
    para(title,42,65,850,30,white if dark else INK,'Bold',leading=34,maxh=75)
    if subtitle:para(subtitle,44,110,850,13,'#C5D9D3' if dark else MUTED,maxh=44)
    footer(dark)

def footer(dark=False):
    col='#AEC6BE' if dark else MUTED
    line(42,505,918,505,'#345A5D' if dark else LINE,.7)
    text('PRESENTACIÓN DEL PRODUCTO  /  NOVIEMBRE 2026',42,517,8,'Bold',col)
    text('Ejemplos ficticios · Representaciones ilustrativas',351,517,7.7,'Body',col)
    text(f'{PAGE:02d} / {TOTAL:02d}',865,516,9,'Bold',LIME if dark else GREEN)

def end():c.showPage()

def bullets(items,x,y,w,size=14,col=INK,gap=12):
    for s in items:
        box(x,y+5,5,5,TEAL,2)
        h=para(s,x+17,y,w-17,size,col,maxh=58)
        y+=h+gap
    return y

def feature_grid(items,x=42,y=169,w=510,cols=2,cardh=97):
    gap=12;cw=(w-gap*(cols-1))/cols
    for i,(title,body) in enumerate(items):
        xx=x+(i%cols)*(cw+gap);yy=y+(i//cols)*(cardh+gap)
        box(xx,yy,cw,cardh,white,12,LINE)
        text(f'{i+1:02d}',xx+15,yy+15,10,'Bold',TEAL)
        para(title,xx+44,yy+13,cw-57,13,INK,'Bold',leading=16,maxh=34)
        para(body,xx+15,yy+49,cw-30,11.5,MUTED,leading=15,maxh=cardh-56)

def steps(items,x=596,y=198,w=280,dark=False):
    for i,s in enumerate(items,1):
        box(x,y,24,24,LIME,8);text(str(i),x+8,y+7,10,'Bold',GREEN)
        h=para(s,x+36,y+1,w-36,13,white if dark else INK,leading=18,maxh=62)
        y+=max(33,h)+16

def example(title,items,result=None,x=575,y=168,w=343,h=315):
    box(x,y,w,h,'#EAF1E7',16)
    pill('EJEMPLO SENCILLO',x+20,y+17,white,GREEN)
    para(title,x+20,y+56,w-40,18,INK,'Bold',leading=22,maxh=50)
    steps(items,x+20,y+116,w-40)
    if result:
        line(x+20,y+h-56,x+w-20,y+h-56,'#C6D9CB')
        para(result,x+20,y+h-43,w-40,11,GREEN,'Bold',leading=14,maxh=35)

def ribbon(t,x=42,y=448,w=510):
    box(x,y,w,35,NAVY,8);para(t,x+13,y+9,w-26,10.5,white,leading=14,maxh=20)

def stat(x,y,w,label,value,fill=white,valuecol=INK):
    box(x,y,w,72,fill,12,LINE if fill==white else None)
    text(label,x+16,y+13,10,'Body',MUTED if fill==white else '#C5D9D3')
    text(value,x+16,y+34,25,'Bold',valuecol)

def mini_table(headers,rows,x,y,widths):
    total=sum(widths);box(x,y,total,27,NAVY,6)
    xx=x
    for label,w in zip(headers,widths):text(label,xx+10,y+10,9,'Bold',white);xx+=w
    for n,row in enumerate(rows):
        yy=y+27+n*35;box(x,yy,total,35,white if n%2==0 else '#ECF1EC',0)
        xx=x
        for val,w in zip(row,widths):para(str(val),xx+10,yy+10,w-18,10.2,INK,leading=13,maxh=25);xx+=w

def mock_dashboard(x,y,w=420,h=285,dark=False):
    box(x+7,y+9,w,h,'#07272B',18);box(x,y,w,h,'#F0F4EF',18)
    side=88;box(x,y,side,h,GREEN,18);box(x+side-14,y+18,14,h-36,GREEN,0)
    text('ESCUELA',x+15,y+22,9,'Bold',LIME)
    for i,t in enumerate(['Inicio','Alumnos','Cobranza','Nóminas','Reportes']):
        if i==0:box(x+10,y+56+i*33,69,24,LIME,7)
        text(t,x+17,y+64+i*33,8,'Bold' if i==0 else 'Body',GREEN if i==0 else '#DBE8E0')
    text('Tu escuela, en un vistazo',x+107,y+25,15,'Bold',INK)
    ww=(w-side-45)/3
    for i,(a,b) in enumerate([('Alumnos','240'),('Grupos','12'),('Planteles','2')]):
        box(x+105+i*(ww+8),y+60,ww,66,white,8)
        text(a,x+115+i*(ww+8),y+73,8,'Body',MUTED);text(b,x+115+i*(ww+8),y+94,20,'Bold',GREEN)
    box(x+105,y+143,w-side-122,113,white,9)
    text('Seguimiento del periodo',x+119,y+157,10,'Bold',INK)
    for i,ratio in enumerate([.73,.92,.56,.85,.68,.96]):
        height=ratio*55;box(x+120+i*31,y+235-height,19,height,TEAL if i%2 else LIME,3)
    text('Datos ilustrativos',x+119,y+243,6.5,'Body',MUTED)

# Map current menu entries to the pages that describe them. Legacy salary submenus
# are intentionally excluded: their functions now live in the employee workflow.
CAT = {
'PUESTOS':('Puestos laborales','Variantes de horas y sueldo mensual.',15),
'CONCEPTOS_NOMINA':('Conceptos de nómina','Percepciones, deducciones y aportaciones.',16),
'EMPLEADOS':('Empleados','Expediente, fotografía, puestos y conceptos.',15),
'PERIODOS_NOMINA':('Periodos de nómina','Fechas de cobertura y pago previsto.',16),
'PREPARACION_NOMINA':('Nóminas','Cálculo, borradores, decisiones y pagos.',16),
'PRESTAMOS':('Préstamos','Cuotas, saldo, reprogramación y liquidación.',17),
'ALUMNOS':('Alumnos','Identidad, expediente, documentos y foto.',4),
'TUTORES':('Tutores','Expediente y cuenta de acceso familiar.',4),
'VINCULOS_TUTOR':('Familiares del alumno','Parentesco, autorizaciones y vigencias.',4),
'INSCRIPCIONES':('Inscripciones','Plantel, ciclo, grado, grupo y situación.',5),
'GRUPOS':('Grupos','Turno, capacidad y organización del alumnado.',3),
'ACTUALIZACIONES_EXPEDIENTE':('Actualizaciones familiares','Revisión de documentos y datos propuestos.',14),
'MATERIAS':('Materias','Catálogo y organización académica.',11),
'MAESTROS':('Maestros','Perfil docente, asignaciones y acceso.',11),
'HORARIOS_CLASE':('Horarios y clases','Bloques de clase, docentes y grupos.',11),
'CALENDARIO_ESCOLAR':('Calendario escolar','Fechas y actividades de la comunidad.',13),
'PLANEACIONES':('Planeaciones','Captura, revisión, corrección y publicación.',12),
'ASISTENCIA':('Asistencia','Registro diario y consulta por grupo.',11),
'CALIFICACIONES':('Calificaciones','Captura por materia y periodo; publicación.',11),
'BOLETAS':('Boletas','Consulta y PDF de resultados publicados.',11),
'CONCEPTOS_COBRO':('Conceptos de cobro','Define qué se cobra y sus reglas.',6),
'CUOTAS_ALUMNO':('Cuotas por alumno','Importes, frecuencias y vencimientos.',6),
'TIPOS_BECA':('Tipos de beca','Clasificaciones de los beneficios.',6),
'BECAS_ALUMNO':('Becas por alumno','Beneficio, concepto y vigencia.',6),
'CARGOS':('Adeudos de alumnos','Saldos, vencimientos y pagos parciales.',6),
'CONVENIOS_PAGO':('Convenios de pago','Acuerdos con conservación del historial.',6),
'AJUSTES_CARGO':('Historial de ajustes','Becas, descuentos, recargos y correcciones.',6),
'POLITICAS_RECARGO':('Políticas de recargo','Reglas y generación controlada de recargos.',6),
'AVISOS':('Avisos escolares','Publicaciones para las audiencias autorizadas.',13),
'EVENTOS_ESCOLARES':('Eventos escolares','Actividades y fechas con contexto escolar.',13),
'PAGOS':('Pagos recibidos','Registro, distribución, validación y comprobante.',7),
'MOTIVOS_FINANCIEROS':('Motivos financieros','Clasificación de ingresos y egresos.',8),
'CUENTAS_FINANCIERAS':('Cuentas financieras','Caja, bancos, alcance y saldos.',8),
'MOVIMIENTOS_FINANCIEROS':('Movimientos financieros','Libro de ingresos, egresos y compensaciones.',8),
'RETIROS_FONDO':('Retiros de fondos','Salidas documentadas con motivo y cuenta.',8),
'REPORTES_FINANCIEROS':('Reportes financieros','Tesorería, estados de cuenta y cobranza.',9),
'PROVEEDORES':('Proveedores','Catálogo de datos y estado de proveedor.',10),
'COMPRAS':('Compras','Detalle, comprobantes, confirmación y cancelación.',10),
'INSTITUCIONES':('Instituciones','Datos generales, moneda y zona horaria.',3),
'PLANTELES':('Planteles','Sedes y datos de operación.',3),
'NIVELES':('Niveles educativos','Catálogo de niveles de la institución.',3),
'OFERTA':('Niveles por plantel','Oferta educativa disponible en cada sede.',3),
'GRADOS':('Grados','Estructura académica por nivel.',3),
'CICLOS':('Ciclos escolares','Fechas y situación del ciclo.',3),
'PERIODOS':('Periodos de evaluación','Etapas para organizar resultados académicos.',3),
'ROLES':('Roles y permisos','Acceso por módulo, institución y plantel.',18),
'USUARIOS':('Usuarios','Invitaciones, estados, roles y recuperación.',18),
'AUDITORIA':('Auditoría','Consulta de acciones, responsables y motivos.',18),
'PORTAL_TUTOR':('Soporte del portal familiar','Consulta de apoyo en modo de lectura.',18),
'PORTAL_MAESTRO':('Soporte del portal docente','Consulta de apoyo sin suplantar al maestro.',18),
'PORTALES':('Portales e identidad','Colores, vistas previas, enlaces y versiones.',19),
'SITIO_WEB':('Diseño web','Secciones, componentes y publicación del sitio.',19),
'GUIAS':('Guía de procesos','Instrucciones consultables según permisos.',18),
}
EXTRA=[
('Configuración de nómina','Conceptos y simulador desde el empleado.',16),
('Historial de puestos','Sueldos y horas conservados por vigencia.',15),
('Cortes de caja','Esperado, declarado y diferencias justificadas.',9),
('Transferencias entre cuentas','Movimiento interno con cuentas de origen y destino.',8),
('Chequeras y folios','Inventario de cheques emitidos y cancelados.',17),
('Pago individual de nómina','Un empleado y un periodo, con confirmación.',17),
('Decisiones de nómina','Aplicar, no aplicar o posponer con motivo.',16),
('Recibos del empleado','Portal personal y descarga del PDF propio.',17),
('Portal familiar','Hijos vinculados, cobros y seguimiento escolar.',14),
('Portal docente','Grupos, alumnos y planeaciones autorizadas.',11),
('Pago anticipado','Preparación desde la inscripción y revisión.',7),
('Saldo a favor y devoluciones','Seguimiento y operaciones autorizadas.',7),
('Comprobantes de compra','Archivos PDF/XML y consulta de retirados.',10),
('Mis pendientes','Agenda personal y seguimiento de compromisos.',18),
('Biblioteca visual','Imágenes, álbumes, galerías y carruseles.',19),
('Mapa y WhatsApp','Componentes configurables del sitio público.',19),
]
source=(ROOT/'src/main/java/escuela/admin/dto/ModuloCatalogo.java').read_text().split('    private final String slug;')[0]
current=re.findall(r'^    ([A-Z_]+)\(',source,re.M)
legacy={'JORNADAS_LABORALES','ESQUEMAS_REMUNERACION','ASIGNACIONES_LABORALES','CONCEPTOS_EMPLEADO','CALENDARIOS_NOMINA','CONFIGURACION_NOMINA'}
assert set(current)-legacy==set(CAT),('Actualizar cobertura',set(current)-legacy-set(CAT),set(CAT)-set(current))
entries=[CAT[k] for k in current if k not in legacy]+EXTRA
APPENDIX=math.ceil(len(entries)/18)
TOTAL=20+APPENDIX+1

# 01 - Cover
PAGE+=1;c.bookmarkPage('p1');c.addOutlineEntry('Presentación', 'p1')
box(0,0,W,H,NAVY,0)
box(42,31,35,35,LIME,11);icon('book',50,39,NAVY,19)
text('PLATAFORMA DE',91,33,9,'Bold','#B6D3C8');text('GESTIÓN ESCOLAR',91,48,14,'Bold',white)
pill('PRESENTACIÓN DEL PRODUCTO',42,101,'#24514F',LIME,9)
para('La operación<br/>de tu escuela,<br/><font color="#CEE9A2">conectada.</font>',42,150,435,43,white,'Bold',leading=48,maxh=155)
para('Alumnos, familias, equipo académico, cobranza y personal en una misma plataforma.',44,330,385,17,'#D4E3DD',leading=24,maxh=77)
mock_dashboard(487,139,427,290)
text('CONOCE LO QUE PUEDES HACER',45,449,10,'Bold',LIME)
text('Funciones principales + ejemplos sencillos',45,471,12,'Body','#D4E3DD')
text('Demostración con datos ficticios',655,458,10,'Body','#AEC6BE');text('Noviembre 2026',655,478,9,'Body','#AEC6BE')
end()
# 02
start('Visión general','Una plataforma. Cuatro experiencias.','Cada persona entra a su espacio y consulta lo que tiene autorizado.')
for i,(name,body,kind) in enumerate([
 ('Administración','Organiza expedientes, operación académica, cobros, finanzas y personal.','shield'),
 ('Familias','Consulta información de sus hijos, pagos, avisos, horarios y boletas.','people'),
 ('Maestros','Trabaja con sus grupos, alumnos y planeaciones autorizadas.','book'),
 ('Personal','Consulta sus propios recibos de nómina y descarga comprobantes.','money')]):
 x=42+(i%2)*447;y=169+(i//2)*150;box(x,y,429,132,white,15,LINE);icon(kind,x+20,y+22);text(name,x+61,y+23,20,'Bold');para(body,x+22,y+65,380,14,MUTED,maxh=50)
end()
# 03
start('Organización escolar','Tu escuela, bien organizada.','Configura la estructura que comparten los demás módulos.')
feature_grid([('Instituciones y sedes','Datos generales, moneda, zona horaria y planteles.'),('Oferta educativa','Niveles, grados y niveles disponibles por plantel.'),('Ciclos y evaluación','Fechas del ciclo y periodos de evaluación.'),('Grupos y materias','Turnos, capacidad y estructura académica.')])
example('Abrir un nuevo ciclo', ['Registra el ciclo y sus fechas.','Prepara los grados y grupos de cada plantel.','Usa esa estructura al inscribir alumnos.'], 'Resultado: una organización consistente para operar.')
ribbon('La misma estructura conecta inscripción, clases, cobros y reportes.')
end()
# 04
start('Control escolar','Un expediente que reúne lo importante.','La información del alumno y su familia permanece vinculada.')
feature_grid([('Datos y fotografía','Identidad del alumno y documentos de su expediente.'),('Familiares vinculados','Parentesco, vigencia y autorizaciones de cada tutor.'),('Información médica','Datos de salud y contactos registrados en la ficha.'),('Consulta y exportación','Filtros, documentos y expedientes según los permisos.')])
example('Registrar a Valeria', ['Captura el expediente del alumno.','Registra al tutor y crea el vínculo familiar.','Verifica fotografía, documentos y contacto.'],'Resultado: un expediente listo para continuar la inscripción.')
ribbon('Los documentos privados se consultan mediante acciones autorizadas.')
end()
# 05
start('Inscripciones','Del expediente al grupo, con contexto.','Registra la inscripción y prepara los conceptos de cobro que correspondan.')
feature_grid([('Ubicación académica','Plantel, ciclo, grado y grupo del alumno.'),('Situación de inscripción','Consulta estados y conserva su seguimiento.'),('Cuotas individuales','Asocia los conceptos y las condiciones aplicables.'),('Anticipos preparados','Revisa cargos y cobertura antes de registrar un pago.')])
example('Inscribir en tercero A',['Selecciona el alumno y el ciclo.','Asigna plantel, grado y grupo.','Revisa cuotas y cargos antes del cobro.'],'Resultado: inscripción y cobranza vinculadas al mismo alumno.')
ribbon('Preparar un pago todavía no registra una entrada de dinero.')
end()
# 06
start('Cobranza escolar','Reglas claras para cada alumno.','Configura conceptos, beneficios y vigencias; revisa lo que realmente está pendiente.')
feature_grid([('Conceptos y cuotas','Importes y frecuencias de los cobros escolares.'),('Becas y descuentos','Beneficios por alumno, concepto y vigencia.'),('Recargos y ajustes','Reglas, generación e historial de modificaciones.'),('Convenios de pago','Acuerdos con seguimiento y conservación del historial.')])
box(575,168,343,315,'#EAF1E7',16);pill('EJEMPLO ILUSTRATIVO',595,185,white,GREEN)
text('Una beca del 10 %',595,225,22,'Bold')
mini_table(['Partida','Importe'],[['Colegiatura','$2,500.00'],['Beca del 10 %','- $250.00'],['Importe por cobrar','$2,250.00']],595,270,[183,120])
para('La beca se define en su módulo y se revisa en el adeudo del alumno.',595,421,295,12,GREEN,maxh=40)
ribbon('Filtra adeudos con saldo, parciales, vencidos o liquidados.')
end()
# 07
start('Pagos recibidos','Del pago al comprobante, con trazabilidad.','Vincula el ingreso con el tutor, los cargos y la cuenta declarada.')
feature_grid([('Registro y distribución','Aplica importes a los cargos seleccionados.'),('Validación de pagos','Revisa reportes de transferencia y comprobantes.'),('Anticipos y cobertura','Consulta pagos que cubren varios cargos o periodos.'),('Correcciones autorizadas','Seguimiento de cancelaciones, devoluciones y saldo a favor.')])
example('Registrar $2,250.00',['Selecciona al tutor y el adeudo.','Revisa la cuenta y la distribución del importe.','Confirma la operación autorizada y abre el comprobante.'],'Resultado: pago y afectación financiera consultables.')
ribbon('El sistema registra la operación; no realiza una transferencia bancaria.')
end()
# 08
start('Tesorería','Cada movimiento tiene una explicación.','Consulta las cuentas y el origen de las entradas y salidas de dinero.')
feature_grid([('Cuentas financieras','Caja o banco; alcance, saldo y estado.'),('Motivos de operación','Clasifica los ingresos y egresos.'),('Transferencias y retiros','Registra movimientos autorizados entre cuentas o salidas.'),('Historial de correcciones','Reversas y compensaciones con motivo e historial.')])
box(575,168,343,315,NAVY,16);pill('LECTURA DE UN SALDO',595,185,LIME,GREEN)
for yy,label,value in [(234,'Saldo anterior','$8,000.00'),(292,'Ingreso validado','+ $2,250.00')]:text(label,595,yy,12,'Body','#BED3CB');text(value,595,yy+21,23,'Bold',white)
line(595,365,896,365,'#3E625F');text('Nuevo saldo',595,389,12,'Body',LIME);text('$10,250.00',595,415,35,'Bold',LIME)
ribbon('Los saldos se explican con movimientos; no se sustituyen sin historial.')
end()
# 09
start('Caja y reportes','Información para cerrar y decidir.','Compara lo esperado con lo declarado y consulta reportes por fecha y alcance.')
feature_grid([('Cortes de caja','Captura importes declarados y revisa diferencias.'),('Tesorería','Consulta ingresos, egresos y variación de saldos.'),('Estados de cuenta','Seguimiento del alumno y de cada cuenta financiera.'),('Concentrado de cobranza','Agrupa y exporta información según los filtros.')])
box(575,168,343,315,'#EAF1E7',16);pill('EJEMPLO DE CIERRE',595,185,white,GREEN)
text('Caja sin diferencia',595,226,22,'Bold');mini_table(['Comparación','Importe'],[['Saldo esperado','$5,000.00'],['Importe declarado','$5,000.00'],['Diferencia','$0.00']],595,268,[183,120])
para('Si hay una diferencia, se revisa y justifica antes de cerrar.',595,421,292,12,GREEN,maxh=40)
ribbon('Exporta PDF o Excel en los reportes que ofrecen esa opción.')
end()
# 10
start('Compras y proveedores','Compras documentadas, de principio a fin.','Prepara el detalle, conserva sus archivos y registra la salida al confirmar.')
feature_grid([('Proveedores','Catálogo de contacto y estado del proveedor.'),('Detalle de compra','Partidas, cantidades, impuestos y total.'),('Comprobantes','Adjunta PDF/XML y consulta los datos importados.'),('Gestión e historial','Borrador, confirmación y cancelación autorizada.')])
box(575,168,343,315,'#EAF1E7',16);pill('EJEMPLO: MATERIAL ESCOLAR',595,185,white,GREEN)
text('10 cuadernos de $35',595,226,21,'Bold');mini_table(['Partida','Importe'],[['Subtotal','$350.00'],['Impuesto del ejemplo','$56.00'],['Total','$406.00']],595,268,[183,120])
para('Revisa el detalle y la cuenta de origen antes de confirmar el egreso.',595,420,295,12,GREEN,maxh=40)
ribbon('Leer un XML de compra no equivale a emitir o timbrar una factura.')
end()
# 11
start('Gestión académica','La actividad del aula, organizada.','Relaciona materias, maestros, horarios y resultados del alumnado.')
feature_grid([('Docentes y clases','Perfil, asignaciones y horarios de los grupos.'),('Asistencia diaria','Presente, ausencia, retardo o justificación.'),('Evaluación','Captura y publicación por materia y periodo.'),('Boletas y consulta','Resultados publicados y descarga de boletas.')])
example('Pasar lista de tercero A',['Abre Asistencia y elige grupo y fecha.','Registra la situación de cada alumno.','Revisa el registro y conserva las observaciones.'],'Resultado: seguimiento de asistencia por grupo y día.')
ribbon('El portal docente muestra la información que corresponde a sus asignaciones.')
end()
# 12
start('Planeación docente','Del borrador a una versión publicada.','Un flujo de trabajo compartido entre maestros y administración.')
flow=['Borrador','Enviada','En revisión','Publicada']
for i,label in enumerate(flow):
 x=42+i*220;box(x,170,203,60,GREEN if i==3 else white,12,LINE if i<3 else None);text(f'{i+1:02d}',x+15,187,12,'Bold',LIME if i==3 else TEAL);text(label,x+47,187,16,'Bold',white if i==3 else INK)
box(42,253,510,226,white,15,LINE);para('Captura con seguimiento',63,274,465,20,INK,'Bold')
bullets(['Propósitos, actividades, recursos y evaluación.','Observaciones de revisión visibles para el maestro.','Correcciones y nuevo envío con confirmación.','Versiones publicadas y consulta del PDF.'],64,319,460,13,gap=10)
box(575,253,343,226,'#EAF1E7',16)
pill('EJEMPLO SENCILLO',595,270,white,GREEN)
text('Proyecto: el agua',595,306,18,'Bold')
for i,label in enumerate(['El maestro captura y envía.', 'Administración revisa y comenta.', 'Se corrige, se revisa y se publica.']):
 yy=338+i*32;box(595,yy,22,22,LIME,7);text(str(i+1),602,yy+7,9,'Bold',GREEN)
 para(label,627,yy+3,268,11.7,INK,leading=15,maxh=20)
line(595,434,898,434,'#C6D9CB')
para('Reabrir permite trabajar de nuevo sin perder el PDF de la publicación anterior.',595,446,303,10.3,GREEN,'Bold',leading=13,maxh=28)
end()
# 13
start('Comunicación','Avisos y fechas que acompañan la operación.','La comunidad consulta publicaciones y actividades desde sus espacios autorizados.')
feature_grid([('Avisos escolares','Información dirigida a las audiencias correspondientes.'),('Eventos escolares','Actividades con fechas y contexto de la escuela.'),('Calendario escolar','Consulta y organización de fechas académicas.'),('Notificaciones internas','Seguimiento de novedades dentro de la plataforma.')])
example('Compartir una actividad',['Registra el aviso o evento con sus datos.','Define el alcance que ofrece el módulo.','Revisa su consulta desde el portal familiar.'],'Resultado: una comunicación vinculada al contexto escolar.')
ribbon('Las notificaciones descritas son internas; no implican envío masivo por WhatsApp.')
end()
# 14
start('Portal familiar','Un espacio para seguir a sus hijos.','Cada familia consulta a los alumnos con quienes tiene un vínculo autorizado.')
feature_grid([('Seguimiento escolar','Horarios, calendario, avisos y resultados publicados.'),('Consulta de pagos','Adeudos, pagos reportados y comprobantes.'),('Datos y documentos','Propuestas de actualización sujetas a revisión.'),('Varios hijos','Cambio de alumno sin mezclar sus expedientes.')])
example('Actualizar un documento',['La familia carga el archivo y envía su propuesta.','Administración revisa y aprueba o rechaza.','Lo aprobado se consulta en el expediente oficial.'],'Resultado: actualización revisada, con trazabilidad.')
ribbon('Reportar una transferencia no la valida automáticamente como pago recibido.')
end()
# 15
start('Personal','Un expediente y toda su historia laboral.','Conecta la identidad del empleado con sus puestos, planteles y perfil docente.')
feature_grid([('Expediente único','Datos personales, fotografía y acceso independiente.'),('Puestos laborales','Horas semanales y sueldo mensual por variante.'),('Historial por vigencia','Cambios de puesto y sueldo conservados por fecha.'),('Planteles y docencia','Principal, adicionales y perfil académico cuando aplique.')])
example('Programar un cambio de puesto',['Selecciona el nuevo puesto del empleado.','Indica desde cuándo aplica y escribe el motivo.','Consulta el historial y la configuración correspondiente.'],'Resultado: el cambio futuro conserva los datos anteriores.')
ribbon('Los datos laborales y de nómina se muestran según los permisos del usuario.')
end()
# 16
start('Nómina','Configura una vez. Revisa cada periodo.','Conceptos por empleado y vigencias para preparar el pago con información consistente.',dark=True)
box(42,168,510,315,'#21464A',15)
bullets(['Sueldo base desde el puesto y conceptos adicionales.','Conceptos mensuales, quincenales, semanales o de una sola vez.','Cálculo por días y vigencias; revisión del desglose.','Borradores, exclusiones y conceptos pospuestos con motivo.'],65,194,460,14,white,gap=15)
para('Periodo → Calcular → Revisar → Guardar borrador',65,435,460,12,LIME,'Bold',maxh=25)
box(575,168,343,315,white,16);pill('EJEMPLO MENSUAL',595,185,'#EAF1E7',GREEN)
mini_table(['Partida','Importe'],[['Sueldo base','$12,000.00'],['Bono configurado','+ $500.00'],['Deducción configurada','- $300.00']],595,231,[182,121])
text('Importe interno propuesto',595,389,11,'Body',MUTED);text('$12,200.00',595,414,34,'Bold',GREEN)
text('Sin cálculo fiscal automático.',595,461,10,'Body',MUTED)
end()
# 17
start('Pagos de personal','Cuentas, préstamos y cheques bajo control.','Una revisión de pago con detalle por empleado, periodo y origen del dinero.')
feature_grid([('Una o varias cuentas','Distribuye el total entre caja y cuentas bancarias.'),('Pagos y cancelaciones','Conserva intentos y registra compensaciones autorizadas.'),('Préstamos','Cuotas, saldo pendiente, reprogramación y liquidación.'),('Chequeras','Folios emitidos y cancelados; el folio se conserva.')])
example('Cubrir los $12,200.00',['Distribuye $8,000.00 en banco y $4,200.00 en caja.','Revisa el detalle y la disponibilidad de cada cuenta.','Confirma el registro del pago y consulta el recibo.'],'Se registra el egreso; no se envía dinero al banco.')
ribbon('Pospuestos se atienden por empleado; los netos cero tienen confirmación propia.')
end()
# 18
start('Seguridad y soporte','La información correcta, para cada persona.','Los derechos y alcances determinan qué puede consultar o gestionar cada usuario.')
feature_grid([('Usuarios y roles','Cuentas, invitaciones, estados y permisos por módulo.'),('Alcance de acceso','Institución, plantel o vínculos autorizados.'),('Auditoría y soporte','Consulta de acciones y apoyo en portales de sólo lectura.'),('Ayuda y agenda','Guía de procesos y pendientes personales.')])
example('Preparar una cuenta de consulta',['Crea el usuario y asigna su rol.','Define los módulos y el alcance correspondientes.','Verifica la consulta con esa cuenta.'],'Resultado: una experiencia alineada con su responsabilidad.')
ribbon('Invitar o recuperar una cuenta utiliza enlaces temporales de activación.')
end()
# 19
start('Diseño e identidad','Tu imagen, en la web y en cada portal.','Personaliza contenido y colores; revisa cómo se verá antes de publicar.')
feature_grid([('Editor visual','Secciones, componentes, texto, menús y logotipos.'),('Colocación precisa','Arrastre, guías, cuadrícula, giro, zoom y deshacer.'),('Recursos visuales','Imágenes, álbumes, galerías, carruseles y vista ampliada.'),('Publicación e identidad','Mapa, WhatsApp y colores de los cuatro portales.')])
example('Actualizar la portada',['Selecciona el texto o componente de la sección.','Edita su contenido y revisa computadora y celular.','Guarda el borrador y publica después de revisar.'],'Claro y oscuro se adaptan a los colores de tu institución.')
ribbon('El botón de WhatsApp abre el canal configurado; no es una campaña automática.')
end()
# 20
start('Demostración guiada','Un recorrido de 15 minutos para el cliente.','Presenta una historia completa y deja el catálogo como material de consulta.')
agenda=[('02 min','La visión general','Muestra los cuatro portales y la identidad institucional.'),('03 min','Un alumno y su inscripción','Abre un expediente ficticio, sus vínculos y su grupo.'),('03 min','Un cobro con evidencia','Consulta el adeudo, prepara un pago y muestra el comprobante.'),('02 min','Seguimiento académico','Enseña asistencia, boleta y la consulta de la familia.'),('03 min','Personal y nómina','Muestra configuración, borrador y detalle del pago.'),('02 min','Sitio e imagen','Edita una sección y compara la vista clara y oscura.')]
for i,(duration,title,body) in enumerate(agenda):
 x=42+(i%2)*447;y=168+(i//2)*99
 box(x,y,429,87,white,12,LINE);pill(duration,x+14,y+14,'#EAF1E7',GREEN)
 text(title,x+94,y+16,15,'Bold');para(body,x+94,y+43,315,11.8,MUTED,leading=15,maxh=34)
para('Recomendación: usa una base de demostración y datos ficticios. Las operaciones de ejemplo no deben ejecutarse sobre registros reales del cliente.',44,474,865,10.4,MUTED,leading=13,maxh=25)
end()
# Catalog pages
for part in range(APPENDIX):
 start('Catálogo de funciones',f'Todo lo que puedes explorar · {part+1}/{APPENDIX}','Referencia rápida de funciones actuales y de las páginas que las explican.')
 chunk=entries[part*18:(part+1)*18]
 for i,(title,description,page) in enumerate(chunk):
  x=42+(i%2)*447;y=162+(i//2)*37
  # 9 rows at 37pt fit exactly above the footer.
  text(title,x+2,y+1,11.6,'Bold');text(f'p. {page:02d}',x+380,y+2,9,'Bold',GREEN)
  c.linkRect('',f'p{page}',(x,H-y-32,x+425,H-y),relative=0,thickness=0)
  para(description,x+2,y+17,365,9.5,MUTED,leading=11.8,maxh=15)
  line(x,y+32,x+425,y+32,LINE,.6)
 end()
# Closing page
start('Siguiente paso','Una demostración con el escenario de tu institución.','Elige un proceso real de tu escuela y recorrámoslo con datos de ejemplo.',dark=True)
for i,(title,body) in enumerate([('01  Elegir prioridades','Define si la primera revisión se centrará en control escolar, cobranza, personal o comunicación.'),('02  Preparar la demostración','Usa una cuenta por rol y ejemplos ficticios con importes sencillos.'),('03  Acordar la puesta en marcha','Revisa configuración, datos iniciales, capacitación y validación del ambiente antes del arranque.')]):
 box(42,171+i*95,510,82,'#21464A',13);text(title,62,186+i*95,17,'Bold',white);para(body,62,214+i*95,470,12,'#D4E3DD',leading=16,maxh=33)
box(575,171,343,271,white,16);pill('ALCANCE DEL PRODUCTO',595,189,'#EAF1E7',GREEN)
para('Operación escolar, académica y financiera en un entorno configurable.',595,237,300,21,INK,'Bold',leading=26,maxh=92)
para('La nómina y sus recibos son de operación interna. No incluyen cálculo fiscal, timbrado ni dispersión bancaria automática.',595,352,300,12,MUTED,leading=17,maxh=67)
para('La presentación describe funciones implementadas; los accesos dependen de los permisos y de la configuración de cada institución.',44,474,850,10.2,'#B9D2C8',leading=14,maxh=26)
end()
assert PAGE==TOTAL,(PAGE,TOTAL)
c.save()
print(f'PDF generado: {OUT}\nPáginas: {PAGE}\nFunciones del catálogo: {len(entries)}')
