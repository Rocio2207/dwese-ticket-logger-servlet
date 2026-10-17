package org.iesalixar.daw2.rocioCortes.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.iesalixar.daw2.rocioCortes.dao.DatabaseConnectionManager;
import org.iesalixar.daw2.rocioCortes.dao.ProvinceDAO;
import org.iesalixar.daw2.rocioCortes.entity.Province;
public class ProvinceDAOImpl implements ProvinceDAO {
    /**
     * 
     */
    /**
     * Lista todas las provincias de la base de datos.
     * @return Lista de provincias
     * @throws SQLException
     */
    public List<Province> listAllProvinces() throws SQLException {
        List<Province> provinces = new ArrayList<>();
        String query = "SELECT * FROM provinces";
        // Obtener una nueva conexión para cada operación
        try (Connection connection = DatabaseConnectionManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String code = resultSet.getString("code");
                String name = resultSet.getString("name");
                int id_region = resultSet.getInt("id_region");
                provinces.add(new Province(id, code, name, id_region));
            }
        }
        return provinces;
    }

    public void insertProvince(Province province) throws SQLException {
        String query = "INSERT INTO provinces (code, name, id_region) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(query)) {
            preparedStatement.setString(1, province.getCode());
            preparedStatement.setString(2, province.getName());
            preparedStatement.setInt(3, province.getId_region());
            preparedStatement.executeUpdate();
        }
    }
    /**
     * Actualiza una provincia existente en la base de datos.
     * @param province Provincia a actualizar
     * @throws SQLException
     */
    public void updateProvince(Province province) throws SQLException {
        String query = "UPDATE provinces SET code = ?, name = ?, id_region = ? WHERE id = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(query)) {
            preparedStatement.setString(1, province.getCode());
            preparedStatement.setString(2, province.getName());
            preparedStatement.setInt(3, province.getId_region());
            preparedStatement.setInt(4, province.getId());
            preparedStatement.executeUpdate();
        }
    }
    /**
     * Elimina una provincia de la base de datos.
     * @param id ID de la provincia a eliminar
     * @throws SQLException
     */
    public void deleteProvince(int id) throws SQLException {
        String query = "DELETE FROM provinces WHERE id = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }
    /**
     * Verifica si una provincia con el código especificado ya existe en la base de
     datos,
     * ignorando mayúsculas.
     *
     * @param code el código de la provincia a verificar.
     * @return true si una provincia con el código ya existe, false de lo contrario.
     * @throws SQLException si ocurre un error en la consulta SQL.
     */
    public Province getProvinceById(int id) throws SQLException {
        String query = "SELECT * FROM provinces WHERE id = ?";
        Province province = null;
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String code = resultSet.getString("code");
                String name = resultSet.getString("name");
                int id_region = resultSet.getInt("id_region");
                province = new Province(id, code, name, id_region);
            }
        }
        return province;
    }
    /**
     * Verifica si una provincia con el código especificado ya existe en la base de
     datos,
     * ignorando mayúsculas.
     *
     * @param code el código de la provincia a verificar.
     * @return true si una provincia con el código ya existe, false de lo contrario.
     * @throws SQLException si ocurre un error en la consulta SQL.
     */
    @Override
    public boolean existsProvinceByCode(String code) throws SQLException {
        String sql = "SELECT COUNT(*) FROM provinces WHERE UPPER(code) = ?";
        try (Connection connection = DatabaseConnectionManager.getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, code.toUpperCase());
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        }
    }
    /**
     * Verifica si una provincia con el código especificado ya existe en la base de
     datos,
     * ignorando mayúsculas, pero excluyendo una provincia con un ID específico.
     *
     * @param code el código de la provincia a verificar.
     * @param id el ID de la provincia a excluir de la verificación.
     * @return true si una provincia con el código ya existe (y no es la provincia con
    el ID dado),
     * false de lo contrario.
     * @throws SQLException si ocurre un error en la consulta SQL.
     */
    @Override
    public boolean existsProvinceByCodeAndNotId(String code, int id) throws
            SQLException {
        String sql = "SELECT COUNT(*) FROM provinces WHERE UPPER(code) = ? AND id != ?";
        try (Connection connection = DatabaseConnectionManager.getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, code.toUpperCase());
            statement.setInt(2, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        }
    }
}