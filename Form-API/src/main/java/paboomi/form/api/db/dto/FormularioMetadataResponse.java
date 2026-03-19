package paboomi.form.api.db.dto;

import java.time.LocalDateTime;

public class FormularioMetadataResponse {

    private long id;
    private String nombreFormulario;
    private String autor;
    private LocalDateTime fechaCreacion;
    private int tamanioBytes;

    public FormularioMetadataResponse() {
    }

    public FormularioMetadataResponse(long id, String nombreFormulario, String autor, LocalDateTime fechaCreacion,
            int tamanioBytes) {
        this.id = id;
        this.nombreFormulario = nombreFormulario;
        this.autor = autor;
        this.fechaCreacion = fechaCreacion;
        this.tamanioBytes = tamanioBytes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombreFormulario() {
        return nombreFormulario;
    }

    public void setNombreFormulario(String nombreFormulario) {
        this.nombreFormulario = nombreFormulario;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public int getTamanioBytes() {
        return tamanioBytes;
    }

    public void setTamanioBytes(int tamanioBytes) {
        this.tamanioBytes = tamanioBytes;
    }
}
