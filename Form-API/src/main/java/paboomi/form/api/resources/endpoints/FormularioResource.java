package paboomi.form.api.resources.endpoints;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.io.InputStream;
import java.sql.SQLException;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import paboomi.form.api.db.dto.ApiErrorResponse;
import paboomi.form.api.db.dto.ApiMessageResponse;
import paboomi.form.api.services.ContestarFormularioService;
import paboomi.form.api.services.DescargarFormularioService;
import paboomi.form.api.services.GuardarFormularioService;

@Path("/formularios")
public class FormularioResource {

  private final GuardarFormularioService guardarFormularioService;
  private final DescargarFormularioService descargarFormularioService;
  private final ContestarFormularioService contestarFormularioService;

  public FormularioResource() {
    this.guardarFormularioService = new GuardarFormularioService();
    this.descargarFormularioService = new DescargarFormularioService();
    this.contestarFormularioService = new ContestarFormularioService();
  }

  // * Endpoint para guardar formularios en la DB */

  @Path("/guardar")
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response guardarFormulario(
      @FormDataParam("autor") String autor,
      @FormDataParam("nombreFormulario") String nombreFormulario,
      @FormDataParam("formulario") InputStream formularioInputStream,
      @FormDataParam("formulario") FormDataContentDisposition formularioFileMeta) {

    try {
      if (formularioInputStream == null) {
        return badRequest("El archivo 'formulario' es obligatorio.");
      }

      byte[] archivoBytes = formularioInputStream.readAllBytes();
      long idFormulario = guardarFormularioService.guardar(autor, nombreFormulario, archivoBytes);
      String nombreOriginal = formularioFileMeta == null ? "sin_nombre" : formularioFileMeta.getFileName();

      return Response.status(Response.Status.CREATED)
          .entity(new ApiMessageResponse(
              "Formulario guardado exitosamente: " + nombreOriginal,
              idFormulario))
          .build();
    } catch (IllegalArgumentException e) {
      return badRequest(e.getMessage());
    } catch (IOException e) {
      return badRequest("No se pudo leer el archivo 'formulario'.");
    } catch (SQLIntegrityConstraintViolationException e) {
      return Response.status(Response.Status.CONFLICT)
          .entity(new ApiErrorResponse("CONFLICT", "Ya existe un formulario con ese nombre.", e.getMessage()))
          .type(MediaType.APPLICATION_JSON)
          .build();
    } catch (SQLException e) {
      return internalError("No se pudo guardar el formulario en la DB.", e.getMessage());
    }
  }

  // * Endpoint para descargar formularios de la DB con paginacion */
  @Path("/descargar")
  @GET
  @Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON })
  public Response descargarFormulario(
      @QueryParam("page") @DefaultValue("1") int page,
      @QueryParam("size") @DefaultValue("10") int size,
      @QueryParam("id") Long idFormulario) {

    try {
      if (idFormulario != null) {
        var formularioOptional = descargarFormularioService.obtenerPorId(idFormulario);
        if (formularioOptional.isEmpty()) {
          return Response.status(Response.Status.NOT_FOUND)
              .entity(new ApiErrorResponse("NOT_FOUND", "Formulario no encontrado", "id=" + idFormulario))
              .type(MediaType.APPLICATION_JSON)
              .build();
        }

        var formulario = formularioOptional.get();
        return Response.ok(formulario.getContenido(), MediaType.APPLICATION_OCTET_STREAM)
            .header("Content-Disposition", "attachment; filename=\"" + formulario.buildFileName() + "\"")
            .build();
      }

      return Response.ok(descargarFormularioService.obtenerPagina(page, size), MediaType.APPLICATION_JSON).build();
    } catch (IllegalArgumentException e) {
      return badRequest(e.getMessage());
    } catch (SQLException e) {
      return internalError("No se pudo descargar formulario(s) desde la DB.", e.getMessage());
    }
  }

  /*
   * Endpoint para obtener el formulario de la DB y que pueda ser contetado en
   * la aplicacion android
   */

  @Path("/contestar/{nombreFormulario}")
  @GET
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response constestarFormulario(@PathParam("nombreFormulario") String nombreFormulario) {
    try {
      var formularioOptional = contestarFormularioService.obtenerFormularioParaContestar(nombreFormulario);
      if (formularioOptional.isEmpty()) {
        return Response.status(Response.Status.NOT_FOUND)
            .entity(
                new ApiErrorResponse("NOT_FOUND", "Formulario no encontrado", "nombreFormulario=" + nombreFormulario))
            .type(MediaType.APPLICATION_JSON)
            .build();
      }

      var formulario = formularioOptional.get();
      return Response.ok(formulario.getContenido(), MediaType.APPLICATION_OCTET_STREAM)
          .header("Content-Disposition", "inline; filename=\"" + formulario.buildFileName() + "\"")
          .build();
    } catch (IllegalArgumentException e) {
      return badRequest(e.getMessage());
    } catch (SQLException e) {
      return internalError("No se pudo obtener el formulario para contestar.", e.getMessage());
    }
  }

  private Response badRequest(String message) {
    return Response.status(Response.Status.BAD_REQUEST)
        .entity(new ApiErrorResponse("BAD_REQUEST", message, null))
        .type(MediaType.APPLICATION_JSON)
        .build();
  }

  private Response internalError(String message, String detail) {
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(new ApiErrorResponse("INTERNAL_ERROR", message, detail))
        .type(MediaType.APPLICATION_JSON)
        .build();
  }
}
