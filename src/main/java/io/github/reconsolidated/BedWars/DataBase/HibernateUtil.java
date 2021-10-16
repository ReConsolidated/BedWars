package io.github.reconsolidated.BedWars.DataBase;


import io.github.reconsolidated.BedWars.DataBase.LobbyConnection.ServerStateDomain;
import io.github.reconsolidated.BedWars.Party.PartyDomain;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                // Hibernate settings equivalent to hibernate.cfg.xml's properties
                Properties settings = new Properties();
                settings.put(Environment.DRIVER, "org.postgresql.Driver");
                settings.put(Environment.URL, "jdbc:postgresql://164.90.180.62:5432/hibernate_db?allowPublicKeyRetrieval=true&useSSL=false&createDatabaseIfNotExist=true");
                settings.put(Environment.USER, "postgres");
                settings.put(Environment.PASS, "docker");
                settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQL82Dialect");
                settings.put(Environment.SHOW_SQL, "false");
                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
                settings.put(Environment.HBM2DDL_AUTO, "update");
                configuration.setProperties(settings);

                configuration.setProperty("c3p0.testConnectionOnCheckout", "true");

                configuration.addAnnotatedClass(BedWarsPlayerDomain.class);
                configuration.addAnnotatedClass(ServerStateDomain.class);
                configuration.addAnnotatedClass(PartyDomain.class);
                configuration.addAnnotatedClass(PlayerGlobalDataDomain.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }
}