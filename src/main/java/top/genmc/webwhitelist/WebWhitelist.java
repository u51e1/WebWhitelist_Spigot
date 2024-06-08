package top.genmc.webwhitelist;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;

public class WebWhitelist extends JavaPlugin implements Listener {
    private Connection connection;
    private String notAllowedMessage;
    private String verifyErrorMessage;

    @Override
    public void onEnable() {
        this.saveDefaultConfig(); // 保存和加载默认配置文件
        this.setupDatabase(); // 设置数据库
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // 关闭数据库连接
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                getLogger().severe("Error closing the MySQL connection: " + e.getMessage());
            }
        }
    }

    private void setupDatabase() {
        String host = getConfig().getString("database.host");
        String port = getConfig().getString("database.port");
        String database = getConfig().getString("database.name");
        String username = getConfig().getString("database.username");
        String password = getConfig().getString("database.password");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
        try {
            connection = DriverManager.getConnection(url, username, password);
            getLogger().info("Connected to the database successfully!");
        } catch (SQLException e) {
            getLogger().severe("Could not connect to the database: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String username = player.getName();
        try {
            if (!isUsernameAllowed(username)) {
                player.kickPlayer(notAllowedMessage);
            }
        } catch (SQLException e) {
            player.kickPlayer(verifyErrorMessage);
            getLogger().severe("Database read error: " + e.getMessage());
        }
    }

    private boolean isUsernameAllowed(String username) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE username = ?");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        rs.close();
        ps.close();
        return false;
    }
}
