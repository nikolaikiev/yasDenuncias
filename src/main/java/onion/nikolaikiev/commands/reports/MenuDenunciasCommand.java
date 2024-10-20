package onion.nikolaikiev.commands.reports;

import onion.nikolaikiev.Main;
import onion.nikolaikiev.menu.MenuDenuncias;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuDenunciasCommand implements CommandExecutor {
    private final Main plugin;

    public MenuDenunciasCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando.");
            return true;
        }

        Player player = (Player) sender;

        // Check if the player has the required permission
        if (!player.hasPermission("denuncias.open")) {
            player.sendMessage("§cVocê não tem permissão para usar este comando.");
            return true;
        }

        MenuDenuncias menu = new MenuDenuncias(plugin);
        menu.open(player);
        return true;
    }
}
