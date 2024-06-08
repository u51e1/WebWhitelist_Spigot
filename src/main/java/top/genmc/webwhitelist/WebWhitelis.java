package top.genmc.webwhitelist;

import org.bukkit.plugin.java.JavaPlugin;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class WebWhitelis extends JavaPlugin {
    public class Database {
        private JavaPlugin plugin;

        public Database(JavaPlugin plugin) {
            this.plugin = plugin;
        }

        public Connection getConnection() throws SQLException {
            String host = plugin.getConfig().getString("database.host");
            int port = plugin.getConfig().getInt("database.port");
            String database = plugin.getConfig().getString("database.name");
            String username = plugin.getConfig().getString("database.username");
            String password = plugin.getConfig().getString("database.password");

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&allowPublicKeyRetrieval=true";
                return DriverManager.getConnection(url, username, password);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    @Override
    public void onEnable() {
        this.saveDefaultConfig();  // 保存默认配置文件，如果还不存在的话


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
