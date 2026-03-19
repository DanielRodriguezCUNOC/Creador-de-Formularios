package paboomi.form.api.db.dto;

import java.util.List;

public class PaginatedFormulariosResponse {

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private List<FormularioMetadataResponse> formularios;

    public PaginatedFormulariosResponse() {
    }

    public PaginatedFormulariosResponse(
            int page,
            int size,
            long totalElements,
            int totalPages,
            List<FormularioMetadataResponse> formularios) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.formularios = formularios;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<FormularioMetadataResponse> getFormularios() {
        return formularios;
    }

    public void setFormularios(List<FormularioMetadataResponse> formularios) {
        this.formularios = formularios;
    }
}
