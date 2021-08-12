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
    public static void sendServerState(BedWars plugin){
        sendServerState(plugin,0);
    }
    private static void sendServerState(BedWars plugin, int number_of_tries){
        new BukkitRunnable() {
            @Override
            public void run() {
                try (Session session = HibernateUtil.getSessionFactory().openSession()){
                    Transaction transaction = session.beginTransaction();
                    session.saveOrUpdate(new ServerStateDomain(
                            "bedwars_" + Bukkit.getServer().getPort(),
                            "bedwars",
                            16,
                            16,
                            16
                            ));
                    transaction.commit();
                }
                catch (HibernateException e){
                    Bukkit.getLogger().warning(e.getMessage());
                    if (number_of_tries > 0){
                        Bukkit.getLogger().info(
                                "Trying to connect to the database for the "
                                        + (4-number_of_tries) + ". time...");
                        sendServerState(plugin, number_of_tries-1);
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
