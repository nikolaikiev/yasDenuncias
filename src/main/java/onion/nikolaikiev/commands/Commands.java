package onion.nikolaikiev.commands;

import onion.nikolaikiev.Main;
import onion.nikolaikiev.commands.reports.*;
import org.bukkit.command.CommandExecutor;

public class Commands {
    private final Main plugin;

    public Commands(Main plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        // Registra o comando de denúncia
        registerCommand("denunciar", new DenunciaCommand(plugin));

        // Registra o comando para abrir o menu de denúncias
        registerCommand("menu-denuncias", new MenuDenunciasCommand(plugin));
    }

    private void registerCommand(String commandName, CommandExecutor executor) {
        // Registra o comando usando o plugin principal
        plugin.getCommand(commandName).setExecutor(executor);
    }
}
