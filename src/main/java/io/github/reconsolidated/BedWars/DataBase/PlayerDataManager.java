package io.github.reconsolidated.BedWars.DataBase;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.Participant;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;
import java.util.List;

public class PlayerDataManager {

    public static void savePlayerData(BedWars plugin, Participant p){
        new BukkitRunnable() {
            @Override
            public void run() {
                try (Session session = HibernateUtil.getSessionFactory().openSession()){
                    Transaction transaction = session.beginTransaction();
                    p.updateDomain();
                    session.saveOrUpdate(p.getDomain());
                    transaction.commit();
                }
                catch (HibernateException e){
                    Bukkit.getLogger().warning(e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);

    }


    public static void fetchPlayersData(List<Participant> participants){
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            for (int i = 0; i<participants.size(); i++){
                Participant p = participants.get(i);
                try {
                    BedWarsPlayerDomain domain = session.createQuery("from BedWarsPlayerDomain d where d.playerName='"
                            + p.getPlayer().getName() + "'", BedWarsPlayerDomain.class).getSingleResult();

                    p.setDomain(domain);
                }
                catch (NoResultException e){
                    p.setDomain(new BedWarsPlayerDomain(p.getPlayer().getName(),
                            0, 0, 0, 0, 0, 0, 0));
                }
            }
        }
        catch (HibernateException e){
            Bukkit.getLogger().warning(e.getMessage());
        }
    }
}
