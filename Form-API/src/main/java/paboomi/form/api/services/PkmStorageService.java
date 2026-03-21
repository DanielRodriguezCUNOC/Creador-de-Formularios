package paboomi.form.api.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PkmStorageService {

  private static final String STORAGE_ENV = "PKM_STORAGE_DIR";
  private static final String DEFAULT_STORAGE_DIR = "/home/luluwalilith/pkm-storage";
  private static final DateTimeFormatter FILE_TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

  public String guardarArchivo(long idFormulario, String nombreFormulario, byte[] contenido) throws IOException {
    Path baseDir = obtenerDirectorioBase();
    Files.createDirectories(baseDir);

    String nombreArchivo = construirNombreArchivo(idFormulario, nombreFormulario);
    Path outputFile = baseDir.resolve(nombreArchivo);
    Files.write(outputFile, contenido);

    return nombreArchivo;
  }

  private Path obtenerDirectorioBase() {
    String configuredPath = System.getenv(STORAGE_ENV);
    if (configuredPath == null || configuredPath.isBlank()) {
      return Paths.get(DEFAULT_STORAGE_DIR);
    }
    return Paths.get(configuredPath.trim());
  }

  private String construirNombreArchivo(long idFormulario, String nombreFormulario) {
    String nombreLimpio = sanitizar(nombreFormulario);
    if (nombreLimpio.isBlank()) {
      nombreLimpio = "formulario";
    }

    String timestamp = LocalDateTime.now().format(FILE_TS_FORMAT);
    return nombreLimpio + "_id" + idFormulario + "_" + timestamp + ".pkm.txt";
  }

  private String sanitizar(String valor) {
    if (valor == null) {
      return "";
    }

    String normalizado = Normalizer.normalize(valor, Normalizer.Form.NFD)
        .replaceAll("\\p{M}+", "");

    return normalizado
        .toLowerCase()
        .replaceAll("[^a-z0-9]+", "_")
        .replaceAll("^_+|_+$", "")
        .replaceAll("_+", "_");
  }
}
