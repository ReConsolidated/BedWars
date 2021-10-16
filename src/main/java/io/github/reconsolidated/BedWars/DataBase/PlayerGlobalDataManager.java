package io.github.reconsolidated.BedWars.DataBase;

import io.github.reconsolidated.BedWars.BedWars;
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
            return new PlayerGlobalDataDomain(uuid, 0, 0, 0);
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
            return new PlayerGlobalDataDomain(uuid, 0, 0, 0);
        }
        return new PlayerGlobalDataDomain(uuid, 0, 0, 0);
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
        PlayerGlobalDataDomain domain = getPlayerDomain(player.getUniqueId());
        domain.setGrypcioCoins(domain.getGrypcioCoins() + amount);
        savePlayerDomain(plugin, domain);
    }

    public static void addExperience(Plugin plugin, Player player, int amount){
        PlayerGlobalDataDomain domain = getPlayerDomain(player.getUniqueId());
        domain.setExperience(domain.getExperience() + amount);
        savePlayerDomain(plugin, domain);
    }

    public static int getPlayerGold(Player player){
        PlayerGlobalDataDomain domain = getPlayerDomain(player.getUniqueId());
        return domain.getGrypcioCoins();
    }

    public static int getPlayerLevel(Player player) {
        PlayerGlobalDataDomain domain = getPlayerDomain(player.getUniqueId());
        return domain.getExperience();
    }

    public static String getRankDisplayName(Player player) {
        player.recalculatePermissions();
        if (player.isOp()) return ChatColor.RED + "[ADMIN] ";
        for (PermissionAttachmentInfo perm : player.getEffectivePermissions()){
            switch (perm.getPermission().toLowerCase()){
                case "admin" -> {
                    return ChatColor.RED + "[ADMIN] ";
                }
                case "mvip" -> {
                    return ChatColor.GOLD + "[MVIP] ";
                }
                case "svip" -> {
                    return ChatColor.DARK_PURPLE + "[SVIP] ";
                }
                case "vip" -> {
                    return ChatColor.LIGHT_PURPLE + "[VIP] ";
                }
            }
        }
        return "";
    }
}
