package onion.nikolaikiev.menu;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import onion.nikolaikiev.Main;
import onion.nikolaikiev.models.Denuncia;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

public class MenuDenuncias {
    private final Main plugin;
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);
    private BukkitRunnable messageScheduler; // Agendador de mensagens

    public MenuDenuncias(Main plugin) {
        this.plugin = plugin; // Inicializa o plugin
    }

    // Abre o inventário de denúncias
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Lista de Denúncias"); // Cria um inventário
        Map<String, Denuncia> reportMap = loadDenuncias(); // Carrega denúncias
        populateInventory(inventory, reportMap); // Preenche o inventário
        player.openInventory(inventory); // Abre para o jogador

        checkInventorySize(reportMap); // Verifica o tamanho do inventário
    }

    private Map<String, Denuncia> loadDenuncias() {
        Map<String, Denuncia> reportMap = new HashMap<>();
        String query = "SELECT reportado, motivo, denunciante FROM denuncias";

        try (Connection conn = plugin.getMySQLManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String reportadoUUID = rs.getString("reportado");
                String motivo = rs.getString("motivo");
                String denuncianteUUID = rs.getString("denunciante");

                reportMap.computeIfAbsent(reportadoUUID, Denuncia::new).addDenuncia(motivo, denuncianteUUID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportMap;
    }

    private void populateInventory(Inventory inventory, Map<String, Denuncia> reportMap) {
        reportMap.values().forEach(denuncia -> {
            ItemStack item = createDenunciaItem(denuncia);
            inventory.addItem(item);
        });
    }

    private ItemStack createDenunciaItem(Denuncia denuncia) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();

        String reportadoUUID = denuncia.getReportado();
        String reportadoName = getPlayerName(reportadoUUID);

        skullMeta.setOwner(reportadoName);
        item.setItemMeta(skullMeta);

        String status = (Bukkit.getPlayer(UUID.fromString(reportadoUUID)) != null) ? "§aOnline" : "§cOffline";
        String header = String.format("§7%dx %s%s §7→ %s", denuncia.getTotalDenuncias(), getRankWithColor(plugin, reportadoUUID), reportadoName, denuncia.getMotivo());
        skullMeta.setDisplayName(header);

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§fTotal de denúncias: §c" + denuncia.getTotalDenuncias());
        lore.add("§fStatus: " + status);
        lore.add("");
        lore.add("§7Detalhes das denúncias:");
        lore.add("§7Últimos jogadores que reportaram:");
        lore.add("");

        // Agrupa denunciantes por UUID
        Map<String, Integer> denunciantes = new HashMap<>();
        denuncia.getDenunciantes().forEach((uuid, count) -> {
            denunciantes.merge(uuid, count, Integer::sum);
        });

        // Adiciona à lore
        int countIndex = 1;
        for (Map.Entry<String, Integer> entry : denunciantes.entrySet()) {
            String uuid = entry.getKey();
            String symbol = "§7§l" + (countIndex == 1 ? "┎" : countIndex == 2 ? "┇" : "┖");
            String rankColor = getRankWithColor(plugin, uuid);
            String name = getPlayerName(uuid);

            lore.add(symbol + " " + rankColor + name + "§7 (" + entry.getValue() + "x)");
            countIndex++;
        }

        skullMeta.setLore(lore);
        item.setItemMeta(skullMeta);
        return item;
    }

    // Obtém o nome do jogador pelo UUID
    private String getPlayerName(String uuid) {
        String query = "SELECT name FROM players WHERE uuid = ?";
        try (Connection conn = plugin.getMySQLManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, uuid);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("name") : "Desconhecido"; // Retorna nome ou valor padrão
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Desconhecido"; // Valor padrão
    }

    // Obtém o rank do jogador (pode ser offline)
    private String getRankWithColor(Main plugin, String playerUUID) {
        if (!UUID_PATTERN.matcher(playerUUID).matches()) {
            return "§7"; // Retorna valor padrão
        }

        LuckPerms luckPerms = plugin.getLuckPermsApi();
        if (luckPerms != null) {
            User user = luckPerms.getUserManager().loadUser(UUID.fromString(playerUUID)).join();
            String prefix = (user != null) ? user.getCachedData().getMetaData().getPrefix() : null;
            return (prefix != null) ? ChatColor.translateAlternateColorCodes('&', prefix) + " " : "§7";
        }
        return "§7"; // Valor padrão
    }

    // Verifica o tamanho do inventário de denúncias
    private void checkInventorySize(Map<String, Denuncia> reportMap) {
        if (reportMap.size() >= 54) {
            startMessageScheduler();
        }
    }

    // Inicia o agendador para enviar mensagens
    private void startMessageScheduler() {
        if (messageScheduler != null) {
            messageScheduler.cancel();
        }

        messageScheduler = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.hasPermission("denuncias.receive")) {
                        onlinePlayer.sendMessage(ChatColor.RED + "Atenção! O inventário de denúncias está cheio. Por favor, apague algumas denúncias.");
                    }
                }
            }
        };

        messageScheduler.runTaskTimer(plugin, 0, 3600 * 20); // 1 hora em ticks
    }
}
