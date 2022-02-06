package io.github.reconsolidated.BedWars.DataBase;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import java.util.UUID;

public class PlayerGlobalDataManager {
    private static PlayerGlobalDataDomain getPlayerDomain(UUID uuid){
        if (HibernateUtil.getSessionFactory() == null)
            return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            try {
                for (PlayerGlobalDataDomain domain : session.createQuery
                        ("from PlayerGlobalDataDomain d WHERE d.uuid = '" + uuid.toString() + "'",
                                PlayerGlobalDataDomain.class).getResultList()){
                    if (domain != null) {
                        return domain;
                    }
                }
            }
            catch (NoResultException e){
                return new PlayerGlobalDataDomain(uuid, 0, 0, 0);
            }
        }
        catch (Exception e){
            Bukkit.getLogger().warning(e.getMessage());
            return null;
        }
        return null;
    }

    private static void savePlayerDomain(Plugin plugin, PlayerGlobalDataDomain domain){
        new BukkitRunnable() {
            @Override
            public void run() {
                try (Session session = HibernateUtil.getSessionFactory().openSession()){
                    Transaction transaction = session.beginTransaction();
                    session.saveOrUpdate(domain);
                    transaction.commit();
                }
                catch (HibernateException e){
                    Bukkit.getLogger().warning(e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public static void addGold(Plugin plugin, Player player, int amount){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerGlobalDataDomain domain = getPlayerDomain(player.getUniqueId());
            if (domain == null) return;
            domain.setGrypcioCoins(domain.getGrypcioCoins() + amount);
            savePlayerDomain(plugin, domain);
        });
    }

    public static void addExperience(Plugin plugin, Player player, int amount){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerGlobalDataDomain domain = getPlayerDomain(player.getUniqueId());
            if (domain == null) return;
            domain.setExperience(domain.getExperience() + amount);
            savePlayerDomain(plugin, domain);
        });
    }

    public static int getPlayerGold(Player player){
        PlayerGlobalDataDomain domain = getPlayerDomain(player.getUniqueId());
        if (domain == null) return 0;
        return domain.getGrypcioCoins();
    }

    public static int getPlayerLevel(Player player) {
        PlayerGlobalDataDomain domain = getPlayerDomain(player.getUniqueId());
        if (domain == null) return 0;
        return domain.getExperience();
    }

    public static String getRankDisplayName(Player player) {
        if (player.isOp()) return ChatColor.RED + "[ADMIN] ";
        if (player.hasPermission("admin")) return ChatColor.RED + "[ADMIN] ";
        if (player.hasPermission("moderator")) return ChatColor.DARK_GREEN + "[MODERATOR] ";
        if (player.hasPermission("helper")) return ChatColor.AQUA + "[HELPER] ";
        if (player.hasPermission("youtuber")) return ChatColor.GRAY + "[" + ChatColor.RED + "YOU" + ChatColor.WHITE + "TUBER" + ChatColor.GRAY + "] ";
        if (player.hasPermission("mvip")) return ChatColor.GOLD + "[MVIP] ";
        if (player.hasPermission("svip")) return ChatColor.DARK_PURPLE + "[SVIP] ";
        if (player.hasPermission("vip")) return ChatColor.LIGHT_PURPLE + "[VIP] ";

        return "";
    }
}
