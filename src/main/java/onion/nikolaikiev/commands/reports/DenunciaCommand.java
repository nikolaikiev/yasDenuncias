package onion.nikolaikiev.commands.reports;

import onion.nikolaikiev.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DenunciaCommand implements CommandExecutor {
    private final Main plugin;
    private final Map<Player, Long> cooldowns = new HashMap<>();
    private static final long COOLDOWN_TIME = 60 * 1000; // 1 minute in milliseconds

    public DenunciaCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sendMessage(sender, "§cApenas jogadores podem usar este comando.");
            return true;
        }

        if (args.length < 2) {
            sendMessage(sender, "§cUso incorreto! Use: /denunciar <jogador> <motivo>");
            return true;
        }

        Player denunciante = (Player) sender;
        String reportadoName = args[0];
        String motivo = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        Player reportado = Bukkit.getPlayer(reportadoName);
        if (reportado == null) {
            sendMessage(denunciante, "§cJogador não encontrado ou offline.");
            return true;
        }

        if (denunciante.equals(reportado)) {
            sendMessage(denunciante, "§cVocê não pode se denunciar.");
            return true;
        }

        long lastReportTime = cooldowns.getOrDefault(denunciante, 0L);
        long timeSinceLastReport = System.currentTimeMillis() - lastReportTime;

        if (timeSinceLastReport < COOLDOWN_TIME) {
            long remainingTime = (COOLDOWN_TIME - timeSinceLastReport) / 1000; // Remaining time in seconds
            sendMessage(denunciante, "§cVocê precisa esperar " + remainingTime + " segundo(s) antes de denunciar novamente.");
            return true;
        }

        registerPlayers(denunciante, reportado);

        if (!saveDenuncia(denunciante, reportado, motivo)) {
            sendMessage(denunciante, "§cErro ao registrar a denúncia. Tente novamente.");
            return true;
        }

        cooldowns.put(denunciante, System.currentTimeMillis());
        sendMessage(denunciante, String.format("§a§l          [!] DENÚNCIA FEITA  §6✦          \n"
                + "§a§l* §aSua denúncia foi enviada com sucesso! §6✦\n"
                + "§aNossos staff's estão analisando a situação e, se necessário, "
                + "aplicaremos a punição ao jogador: §7%s§a.\n"
                + "§7§lDICA §7Abusar desse comando pode resultar em punições. ☢", reportado.getName()));

        notifyStaff(denunciante, reportado, motivo);
        return true;
    }

    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

    private void registerPlayers(Player denunciante, Player reportado) {
        plugin.getMySQLManager().insertPlayer(denunciante.getUniqueId().toString(), denunciante.getName());
        plugin.getMySQLManager().insertPlayer(reportado.getUniqueId().toString(), reportado.getName());
    }

    private boolean saveDenuncia(Player denunciante, Player reportado, String motivo) {
        String query = "INSERT INTO denuncias (denunciante, reportado, motivo) VALUES (?, ?, ?)";
        try (Connection conn = plugin.getMySQLManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, denunciante.getUniqueId().toString());
            stmt.setString(2, reportado.getUniqueId().toString());
            stmt.setString(3, motivo);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void notifyStaff(Player denunciante, Player reportado, String motivo) {
        String messageHeader = "§c[!] NOVA DENÚNCIA";
        String reportMessage = String.format("§7Denunciante: %s\n§7Acusado: §c%s\n§7Motivo: §c%s",
                denunciante.getName(), reportado.getName(), motivo);

        for (Player staff : Bukkit.getOnlinePlayers()) {
            if (staff.hasPermission("denuncias.notify")) {
                staff.sendMessage(messageHeader);
                staff.sendMessage(reportMessage);
            }
        }
    }
}
