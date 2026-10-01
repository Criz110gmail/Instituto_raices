package escuela.archivo.imagen;

public record ImagenOptimizada(
        byte[] contenido,
        String nombreArchivo,
        String tipoMime,
        String checksumSha256,
        long tamanoOriginal
) {
    public long tamanoBytes() {
        return contenido.length;
    }
}
