package paboomi.form.api.db.dto;

public class ApiErrorResponse {

  private String error;
  private String message;
  private String detail;

  public ApiErrorResponse() {
  }

  public ApiErrorResponse(String error, String message, String detail) {
    this.error = error;
    this.message = message;
    this.detail = detail;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }
}
