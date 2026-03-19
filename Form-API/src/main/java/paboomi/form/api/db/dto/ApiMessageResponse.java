package paboomi.form.api.db.dto;

public class ApiMessageResponse {

  private String message;
  private Long idFormulario;

  public ApiMessageResponse() {
  }

  public ApiMessageResponse(String message, Long idFormulario) {
    this.message = message;
    this.idFormulario = idFormulario;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Long getIdFormulario() {
    return idFormulario;
  }

  public void setIdFormulario(Long idFormulario) {
    this.idFormulario = idFormulario;
  }
}
