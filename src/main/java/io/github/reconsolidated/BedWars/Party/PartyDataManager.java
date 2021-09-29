package io.github.reconsolidated.BedWars.Party;

import io.github.reconsolidated.BedWars.BedWars;
import io.github.reconsolidated.BedWars.DataBase.HibernateUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.NoResultException;


public class PartyDataManager {

    public static PartyDomain getParty(Player player) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                PartyDomain domain =
                        session.createQuery(
                                "from PartyDomain d where d.owner='" + player.getName() + "'",
                                PartyDomain.class).getSingleResult();
                if (domain == null) {
                    return null;
                }
                return domain;
            } catch (NoResultException e) {
                return null;
            }
        } catch (HibernateException e) {
            e.printStackTrace();
            return null;
        }
    }


}
