package org.iesalixar.daw2.rocioCortes.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.iesalixar.daw2.rocioCortes.dao.ProvinceDAO;
import org.iesalixar.daw2.rocioCortes.dao.ProvinceDAOImpl;
import org.iesalixar.daw2.rocioCortes.dao.RegionDAO;
import org.iesalixar.daw2.rocioCortes.dao.RegionDAOImpl;
import org.iesalixar.daw2.rocioCortes.entity.Province;
import org.iesalixar.daw2.rocioCortes.entity.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet que maneja las operaciones CRUD para la entidad `Province`.
 * Utiliza `RegionDAO` y `ProvinceDAO` para interactuar con la base de datos.
 */
@WebServlet("/provinces")
public class ProvinceServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ProvinceServlet.class);

    private ProvinceDAO provinceDAO;
    private RegionDAO regionDAO;

    /**
     * Inicializa los DAOs que usa el servlet.
     *
     * @throws ServletException si no se pueden crear los DAOs
     */
    @Override
    public void init() throws ServletException {
        logger.info("Inicializando ProvinceServlet");
        try {
            provinceDAO = new ProvinceDAOImpl();
            regionDAO = new RegionDAOImpl();
        } catch (Exception e) {
            logger.error("Error al inicializar los DAOs: {}", e.getMessage(), e);
            throw new ServletException("Error al inicializar el ProvinceDAO", e);
        }
        logger.info("ProvinceServlet inicializado correctamente");
    }
    /**
     * Maneja las solicitudes GET al servlet. Según el parámetro "action", decide qué método invocar.
     * @param request Solicitud HTTP.
     * @param response Respuesta HTTP.
     * @throws ServletException en caso de errores en el servlet.
     * @throws IOException en caso de errores de E/S.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        logger.info("Entrando en doGet(), action={}", action);
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        try {
            // Manejo para evitar el valor null
            if (action == null) {
                action = "list"; // O cualquier acción predeterminada que desees manejar
            }
            switch (action) {
                case "new":
                    showNewForm(request, response); // Mostrar formulario para nueva provincia
                    break;
                case "edit":
                    showEditForm(request, response); // Mostrar formulario para editar provincia
                    break;
                default:
                    listProvinces(request, response); // Listar todas las provincias
                    break;
            }
        } catch (SQLException ex) {
            logger.error("Error de base de datos en doGet(): {}", ex.getMessage(), ex);
            throw new ServletException(ex);
        }
        logger.info("Saliendo de doGet()");
    }
    /**
     * Maneja las solicitudes POST al servlet. Según el parámetro "action", decide qué método invocar.
     * @param request Solicitud HTTP.
     * @param response Respuesta HTTP.
     * @throws ServletException en caso de errores en el servlet.
     * @throws IOException en caso de errores de E/S.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        logger.info("Entrando en doPost(), action={}", action);
        try {
            if (action == null) {
                action = "list";
            }
            switch (action) {
                case "insert":
                    insertProvince(request, response); // Insertar nueva provincia
                    break;
                case "update":
                    updateProvince(request, response); // Actualizar provincia existente
                    break;
                case "delete":
                    deleteProvince(request, response); // Eliminar provincia
                    break;
                default:
                    listProvinces(request, response); // Listar todas las provincias
                    break;
            }
        } catch (SQLException ex) {
            logger.error("Error de base de datos en doPost(): {}", ex.getMessage(), ex);
            throw new ServletException(ex);
        }
        logger.info("Saliendo de doPost()");
    }
    /**
     * Lista todas las provincias y las pasa como atributo a la vista `provinces.jsp`. Además, tambien pasa a la vista un mapa
     * id de región -> nombre de región para poder mostrar la Comunidad Autónoma.
     * @param request Solicitud HTTP.
     * @param response Respuesta HTTP.
     * @throws SQLException en caso de error en la consulta SQL.
     * @throws IOException en caso de error de E/S.
     * @throws ServletException en caso de error en el servlet.
     */
    private void listProvinces(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        logger.info("Entrando en listProvinces()");
        List<Province> listProvinces = provinceDAO.listAllProvinces();// Obtener todas las provincias desde el DAO

        Map<Integer, String> regionNames = new HashMap<>();
        for (Region region : regionDAO.listAllRegions()) {
            regionNames.put(region.getId(), region.getName());
        }
        request.setAttribute("listProvinces", listProvinces);// Pasar la lista de provincias a la vista
        request.setAttribute("regionNames", regionNames); //Pasar la lista de regiones a la vista
        request.getRequestDispatcher("provinces.jsp").forward(request, response);
        logger.info("Saliendo de listProvinces()");
    }

    /**
     * Muestra el formulario para crear una nueva región.
     * @param request Solicitud HTTP.
     * @param response Respuesta HTTP.
     * @throws ServletException en caso de error en el servlet.
     * @throws IOException en caso de error de E/S.
     */
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        logger.info("Entrando en showNewForm()");
        request.setAttribute("listRegions", regionDAO.listAllRegions()); // Carga la lista de regiones en la request para rellenar el {@code <select>}.
        request.getRequestDispatcher("province-form.jsp").forward(request, response); // Redirige a la vista para nueva provincia
        logger.info("Saliendo de showNewForm()");
    }

    /**
     * Muestra el formulario para editar una región existente.
     * @param request Solicitud HTTP.
     * @param response Respuesta HTTP.
     * @throws SQLException en caso de error en la consulta SQL.
     * @throws ServletException en caso de error en el servlet.
     * @throws IOException en caso de error de E/S.
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        logger.info("Entrando en showEditForm()");
        int id = Integer.parseInt(request.getParameter("id"));
        Province existingProvince = (id > 0) ? provinceDAO.getProvinceById(id) : null;
        if (existingProvince == null) {
            logger.warn("Provincia no encontrada para editar, id={}", request.getParameter("id"));
            response.sendRedirect("provinces");
            return;
        }
        request.setAttribute("province", existingProvince);
        request.setAttribute("listRegions", regionDAO.listAllRegions());// Carga la lista de regiones en la request para rellenar el {@code <select>}.
        request.getRequestDispatcher("province-form.jsp").forward(request, response);
        logger.info("Saliendo de showEditForm()");
    }

    /**
     * Inserta una nueva provincia en la base de datos después de realizar
     validaciones.
     * Verifica que el código de la provincia sea único (ignorando mayúsculas) y
     que los campos
     * de código y nombre no estén vacíos.
     *
     * @param request la solicitud HTTP con los datos del formulario.
     * @param response la respuesta HTTP para redirigir o mostrar errores.
     * @throws SQLException si ocurre un error en la base de datos.
     * @throws IOException si ocurre un error de entrada/salida.
     * @throws ServletException si ocurre un error en el procesamiento del
    servlet.
     */
    private void insertProvince(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        logger.info("Entrando en insertProvince()");
        String code = request.getParameter("code").trim().toUpperCase(); // Convertir a mayúsculas
        String name = request.getParameter("name").trim();
        int idRegion = Integer.parseInt(request.getParameter("id_region"));
        Province province = new Province(code, name, idRegion);
        // Validaciones básicas
        if (code.isEmpty() || name.isEmpty()) {
            logger.warn("Validación fallida al insertar: campos vacíos");
            request.setAttribute("errorMessage", "El código y el nombre no  pueden estar vacíos.");
            request.getRequestDispatcher("province-form.jsp").forward(request,
                    response);
            return;
        }
        if (idRegion <= 0) {
            logger.warn("Validación fallida al insertar: región no seleccionada");
            request.setAttribute("errorMessage", "La región no puede estar vacía.");
            request.getRequestDispatcher("province-form.jsp").forward(request,
                    response);
            return;
        }
        // Validar si el código ya existe ignorando mayúsculas
        if (provinceDAO.existsProvinceByCode(code)) {
            logger.warn("Validación fallida al insertar: el código {} ya existe", code);
            request.setAttribute("errorMessage", "El código de la provincia ya  existe.");
            request.getRequestDispatcher("province-form.jsp").forward(request,
                    response);
            return;
        }
        try {
            provinceDAO.insertProvince(province);
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Código SQL para unique constraint violation
                request.setAttribute("errorMessage", "El código de la provincia  debe ser único.");
                request.getRequestDispatcher("province-form.jsp").forward(request, response);
            } else {
                throw e;
            }
        }
        logger.info("Provincia {} insertada correctamente", code);
        response.sendRedirect("provinces");
    }

    /**
     * Actualiza una provincia existente en la base de datos después de realizar
     validaciones.
     * Verifica que el código de la provincia sea único para otras regiones
     (ignorando mayúsculas)
     * y que los campos de código, nombre e id_region no estén vacíos.
     *
     * @param request la solicitud HTTP con los datos del formulario.
     * @param response la respuesta HTTP para redirigir o mostrar errores.
     * @throws SQLException si ocurre un error en la base de datos.
     * @throws IOException si ocurre un error de entrada/salida.
     * @throws ServletException si ocurre un error en el procesamiento del
    servlet.
     */
    private void updateProvince(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        logger.info("Entrando en updateProvince()");
        int id = Integer.parseInt(request.getParameter("id"));
        String code = request.getParameter("code").trim().toUpperCase(); // Convertir a mayúsculas
        String name = request.getParameter("name").trim();
        int idRegion = Integer.parseInt(request.getParameter("id_region"));
        // Validaciones básicas
        if (code.isEmpty() || name.isEmpty()) {
            logger.warn("Validación fallida al actualizar: campos vacíos");
            request.setAttribute("errorMessage", "El código y el nombre no  pueden estar vacíos.");
            request.getRequestDispatcher("province-form.jsp").forward(request,
                    response);
            return;
        }
        if (idRegion <= 0) {
            logger.warn("Validación fallida al actualizar: región no seleccionada");
            request.setAttribute("errorMessage", "La region no puede estar vacia.");
            request.getRequestDispatcher("province-form.jsp").forward(request,
                    response);
            return;
        }
        // Validar si el código ya existe para otra provincia
        if (provinceDAO.existsProvinceByCodeAndNotId(code, id)) {
            logger.warn("Validación fallida al actualizar: el código {} ya existe en otra provincia", code);
            request.setAttribute("errorMessage", "El código de la provincia ya  existe para otra provincia.");
            request.getRequestDispatcher("province-form.jsp").forward(request,
                    response);
            return;
        }
        Province updateProvince = new Province(id, code, name, idRegion);
        try {
            provinceDAO.updateProvince(updateProvince);
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) { // Código SQL para unique constraint violation
                logger.warn("Violación de unicidad al actualizar el código {}", code);
                request.setAttribute("errorMessage", "El código de la provincia debe ser único.");
                request.getRequestDispatcher("province-form.jsp").forward(request, response);
            } else {
                throw e;
            }
        }
        logger.info("Provincia {} actualizada correctamente", id);
        response.sendRedirect("provinces");
    }

    /**
     * Elimina una provincia de la base de datos según su ID.
     * @param request Solicitud HTTP.
     * @param response Respuesta HTTP.
     * @throws SQLException en caso de error en la consulta SQL.
     * @throws IOException en caso de error de E/S.
     */
    private void deleteProvince(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        logger.info("Entrando en deleteProvince()");
        int id = Integer.parseInt(request.getParameter("id"));
        provinceDAO.deleteProvince(id); // Eliminar provincia usando el DAO
        logger.info("Provincia {} eliminada", id);
        response.sendRedirect("provinces");// Redirigir al listado de provincias
    }

}