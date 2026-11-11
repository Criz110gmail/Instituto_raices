package escuela.sitioweb;

/** Identificadores de ilustraciones locales; jamás SVG o URLs enviados por el usuario. */
public final class SitioObjetosEscolares {
    private SitioObjetosEscolares() {}
    public static final String PATRON="cuaderno|libro|libros|lapiz|crayola|regla|mochila|tijeras|escuela|birrete|pizarron|globo|microscopio|matraz|atomo|calculadora|paleta|pincel|musica|teatro|cohete|avion|balon|trofeo|medalla|campana|reloj|computadora|estrella|sol|arcoiris|nube|manzana|corazon|sonrisa|hoja";
    public static String normalizar(String clave) {
        return clave!=null && clave.matches(PATRON) ? clave : "cuaderno";
    }
    public static String recurso(String clave) {
        return "/images/sitio-objetos-escolares.svg?v=20261111-objetos#"+normalizar(clave);
    }
}
