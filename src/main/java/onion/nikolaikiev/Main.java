package onion.nikolaikiev;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import onion.nikolaikiev.database.MySQLManager;
import onion.nikolaikiev.listeners.InventoryClickListener;
import onion.nikolaikiev.commands.Commands;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private MySQLManager mySQLManager;
    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            luckPerms = LuckPermsProvider.get();
        }

        this.mySQLManager = new MySQLManager();
        mySQLManager.connect();
        Bukkit.getConsoleSender().sendMessage("§a[yasDenuncias] §aLigado com sucesso!");
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        Commands commands = new Commands(this);
        commands.registerCommands();
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§c[yasDenuncias] §cDesligado com sucesso!"); // Corrigido para "Desligado"
        mySQLManager.disconnect();
    }

    // Método para obter a instância do LuckPerms
    public LuckPerms getLuckPermsApi() {
        return luckPerms;
    }

    // Método para obter o gerenciador MySQL
    public MySQLManager getMySQLManager() {
        return mySQLManager;
    }
}
