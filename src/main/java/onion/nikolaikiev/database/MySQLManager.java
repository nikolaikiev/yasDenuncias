package onion.nikolaikiev.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLManager {
    private HikariDataSource dataSource; // Fonte de dados HikariCP

    public void connect() {
        HikariConfig config = new HikariConfig(); // Configuração do HikariCP
        config.setJdbcUrl("jdbc:mysql://localhost:3306/baseado"); // URL do banco de dados
        config.setUsername("root"); // Usuário do banco de dados
        config.setPassword("vertrigo"); // Senha do banco de dados
        config.addDataSourceProperty("cachePrepStmts", "true"); // Habilitar cache para PreparedStatements
        config.addDataSourceProperty("prepStmtCacheSize", "250"); // Tamanho do cache para PreparedStatements
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048"); // Limite do SQL no cache
        dataSource = new HikariDataSource(config); // Criação da fonte de dados
        createTables(); // Criação das tabelas se não existirem
    }

    public void disconnect() {
        if (dataSource != null) {
            dataSource.close(); // Fecha a fonte de dados
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection(); // Obtém uma nova conexão do pool
    }

    private void createTables() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Cria a tabela players se não existir
            String sqlPlayers = "CREATE TABLE IF NOT EXISTS players (" +
                    "uuid VARCHAR(36) PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL" +
                    ");";
            stmt.executeUpdate(sqlPlayers); // Executa a criação da tabela players

            // Cria a tabela denuncias se não existir
            String sqlDenuncias = "CREATE TABLE IF NOT EXISTS denuncias (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "denunciante VARCHAR(36) NOT NULL," +
                    "reportado VARCHAR(36) NOT NULL," +
                    "motivo VARCHAR(255) NOT NULL," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (denunciante) REFERENCES players(uuid)," +
                    "FOREIGN KEY (reportado) REFERENCES players(uuid)" +
                    ");";
            stmt.executeUpdate(sqlDenuncias); // Executa a criação da tabela denuncias
        } catch (SQLException e) {
            e.printStackTrace(); // Exibe a pilha de erros em caso de falha
        }
    }

    public void insertDenuncia(String denuncianteUuid, String reportadoUuid, String motivo) {
        String sql = "INSERT INTO denuncias (denunciante, reportado, motivo) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, denuncianteUuid);
            pstmt.setString(2, reportadoUuid);
            pstmt.setString(3, motivo);
            pstmt.executeUpdate(); // Executa a inserção da denúncia
        } catch (SQLException e) {
            System.err.println("Erro ao inserir denúncia: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void insertPlayer(String uuid, String name) {
        String sql = "INSERT INTO players (uuid, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            pstmt.setString(2, name);
            pstmt.setString(3, name);
            pstmt.executeUpdate(); // Insere ou atualiza o jogador
        } catch (SQLException e) {
            System.err.println("Erro ao inserir ou atualizar jogador: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void getDenuncias() {
        String sql = "SELECT * FROM denuncias";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String denunciante = rs.getString("denunciante");
                String reportado = rs.getString("reportado");
                String motivo = rs.getString("motivo");
                String timestamp = rs.getString("timestamp");
                System.out.println("Denúncia ID: " + id + ", Denunciante: " + denunciante + ", Reportado: " + reportado + ", Motivo: " + motivo + ", Timestamp: " + timestamp);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao recuperar denúncias: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
