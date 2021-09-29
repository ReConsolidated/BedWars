package io.github.reconsolidated.BedWars.DataBase.LobbyConnection;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.DataBase.HibernateUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;


public class ServerStateManager {

    private static ServerStateDomain domain = null;

    public static void sendServerState(BedWars plugin){
        sendServerState(plugin,0, plugin.getServerName(), plugin.getTEAM_SIZE(), plugin.getTEAMS_COUNT());
    }

    public static void sendServerState(BedWars plugin, String name, int team_size, int teams){
        sendServerState(plugin,0, name, team_size, teams);
    }
    private static void sendServerState(BedWars plugin, int number_of_tries, String name, int team_size, int teams){
        if (domain == null){
            domain = new ServerStateDomain(
                    name + "_" + Bukkit.getServer().getPort(),
                    name,
                    team_size*teams,
                    team_size*teams,
                    team_size,
                    plugin.getPartiesCount()
            );
        }
        else{
            domain.setAvailableSlots(team_size*teams - Bukkit.getOnlinePlayers().size());
            domain.setParties(plugin.getPartiesCount());
        }
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
                    if (number_of_tries > 0){
                        Bukkit.getLogger().info(
                                "Trying to connect to the database for the "
                                        + (4-number_of_tries) + ". time...");
                        sendServerState(plugin, number_of_tries-1, name, team_size, teams);
                    }
                    else{
                        Bukkit.getServer().shutdown();
                    }

                }
            }
        }.runTaskAsynchronously(plugin);

    }

    public static ServerStateDomain getServerDomainByName(String serverName){
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            try {
                ServerStateDomain domain =
                        session.createQuery(
                                "from ServerStateDomain d where d.serverName='" + serverName + "'",
                                ServerStateDomain.class).getSingleResult();
                if (domain == null){
                    return null;
                }
                return domain;
            }
            catch (NoResultException e){
                return null;
            }
        }
        catch (HibernateException e){
            Bukkit.getLogger().warning(e.getMessage());
            return null;
        }
    }

    public static void removeServerFromList() {
        ServerStateDomain domain = getServerDomainByName("bedwars_" + Bukkit.getServer().getPort());
        if (domain == null){
            Bukkit.getLogger().warning("Nie znaleziono serwera do usunięcia: " + "bedwars_" + Bukkit.getServer().getPort());
            return;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            Transaction transaction = session.beginTransaction();
            Bukkit.getLogger().info("Usuwam serwer: " + domain.getServerName());
            session.delete(domain);
            session.flush();
            transaction.commit();
        }
        catch (HibernateException e){
            Bukkit.getLogger().warning(e.getMessage());
        }
    }
}
