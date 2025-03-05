package ru.sscefalix.sEngineX.api.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jspecify.annotations.Nullable;
import ru.sscefalix.sEngineX.SEngine;
import ru.sscefalix.sEngineX.api.manager.AbstractManager;

import java.lang.reflect.Field;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager<P extends SEngine<P>> extends AbstractManager<P> {
    private HikariDataSource dataSource;

    private String host;
    private int port;
    private String database;
    private String user;
    private String password;

    public DatabaseManager(P plugin) {
        super(plugin);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public <T extends AbstractTable<P>> T insert(T entity) throws SQLException {
        Class<?> clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();

        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        Field autoIncrementField = null;

        for (Field field : fields) {
            TableColumn column = field.getAnnotation(TableColumn.class);
            if (column != null) {
                field.setAccessible(true);
                if (column.autoIncrement()) {
                    autoIncrementField = field;
                } else {
                    try {
                        Object value = field.get(entity);
                        if (value != null) {
                            columns.add(column.name());
                            values.add(value);
                        }
                    } catch (IllegalAccessException e) {
                        throw new SQLException("Failed to access field value", e);
                    }
                }
            }
        }

        String sql = String.format(
                "INSERT INTO %s (%s) VALUES (%s)",
                entity.getTableName(),
                String.join(", ", columns),
                String.join(", ", columns.stream().map(c -> "?").toArray(String[]::new))
        );

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < values.size(); i++) {
                stmt.setObject(i + 1, values.get(i));
            }

            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();

            if (generatedKeys.next() && autoIncrementField != null) {
                long generatedId = generatedKeys.getLong(1);
                try {
                    autoIncrementField.setAccessible(true);
                    autoIncrementField.set(entity, generatedId);
                } catch (IllegalAccessException e) {
                    throw new SQLException("Failed to set generated ID", e);
                }
            }

            return entity;
        }
    }

    public <T extends AbstractTable<P>> T getById(Class<T> clazz, long id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM %s WHERE id = ?".formatted(getTableName(clazz)))) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToObject(rs, clazz);
            }
            return null;
        }
    }

    public <T extends AbstractTable<P>> List<T> getByField(Class<T> clazz, String field, Object value)
            throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM %s WHERE %s = ?".formatted(getTableName(clazz), field))) {
            stmt.setObject(1, value);
            ResultSet rs = stmt.executeQuery();

            List<T> results = new ArrayList<>();
            while (rs.next()) {
                results.add(mapResultSetToObject(rs, clazz));
            }
            return results;
        }
    }

    public <T extends AbstractTable<P>> @Nullable T getOneByField(Class<T> clazz, String field, Object value) throws SQLException {
        List<T> list = getByField(clazz, field, value);

        return list.isEmpty() ? null : list.getFirst();
    }

    public ResultSet executeSql(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);

        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        if (sql.trim().toUpperCase().startsWith("SELECT")) {
            return stmt.executeQuery();
        } else {
            stmt.executeUpdate();
            return null;
        }
    }

    public <T extends AbstractTable<P>> void createTableIfNotExists(Class<T> clazz) throws SQLException {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sql.append(getTableName(clazz)).append(" (");

        Field[] fields = clazz.getDeclaredFields();
        List<String> columnDefs = new ArrayList<>();

        for (Field field : fields) {
            TableColumn column = field.getAnnotation(TableColumn.class);
            if (column != null) {
                StringBuilder columnDef = new StringBuilder()
                        .append(column.name())
                        .append(" ")
                        .append(getSqlType(field.getType()));

                if (column.primaryKey()) {
                    columnDef.append(" PRIMARY KEY");
                }
                if (column.autoIncrement()) {
                    columnDef.append(" AUTO_INCREMENT");
                }
                if (!column.nullable()) {
                    columnDef.append(" NOT NULL");
                }
                if (column.unique()) {
                    columnDef.append(" UNIQUE");
                }
                if (!column.defaultValue().isEmpty()) {
                    String defaultVal = column.defaultValue();
                    if (field.getType() == String.class) {
                        defaultVal = "'" + defaultVal + "'";
                    }
                    columnDef.append(" DEFAULT ").append(defaultVal);
                }

                columnDefs.add(columnDef.toString());

                if (column.index()) {
                    columnDefs.add(String.format("INDEX idx_%s (%s)", column.name(), column.name()));
                }
            }
        }

        sql.append(String.join(", ", columnDefs)).append(")");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql.toString());
        }
    }

    public <T extends AbstractTable<P>> T mapResultSetToObject(ResultSet rs, Class<T> clazz)
            throws SQLException {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                TableColumn column = field.getAnnotation(TableColumn.class);
                if (column != null) {
                    Object value = rs.getObject(column.name());
                    if (value != null) {
                        if (value instanceof Timestamp && field.getType() == LocalDateTime.class) {
                            value = ((Timestamp) value).toLocalDateTime();
                        } else if (value instanceof Date && field.getType() == LocalDate.class) {
                            value = ((Date) value).toLocalDate();
                        }
                        field.set(instance, value);
                    }
                }
            }
            return instance;
        } catch (Exception e) {
            throw new SQLException("Failed to map ResultSet to object: " + e.getMessage());
        }
    }

    private <T extends AbstractTable<P>> String getTableName(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance().getTableName();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get table name", e);
        }
    }

    private String getSqlType(Class<?> type) {
        if (type == Long.class || type == long.class) {
            return "BIGINT";
        } else if (type == String.class) {
            return "VARCHAR(255)";
        } else if (type == Integer.class || type == int.class) {
            return "INT";
        } else if (type == Date.class
                || type == Timestamp.class
                || type == LocalDateTime.class) {
            return "TIMESTAMP";
        }
        return "TEXT";
    }

    private void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public void setConfig(String host, int port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    @Override
    protected void onSetup() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s", host, port, database));
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        this.dataSource = new HikariDataSource(config);
    }

    @Override
    protected void onShutdown() {
        close();
    }
}