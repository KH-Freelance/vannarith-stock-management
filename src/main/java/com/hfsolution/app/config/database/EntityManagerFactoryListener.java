// package com.hfsolution.app.config.database;

// import jakarta.persistence.EntityManager;
// import jakarta.persistence.EntityManagerFactory;
// import org.hibernate.Session;
// import org.hibernate.SessionFactory;
// import org.hibernate.engine.spi.SessionImplementor;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;

// import jakarta.annotation.PostConstruct;
// import jakarta.persistence.PersistenceContext;

// @Component
// public class EntityManagerFactoryListener {

//     @PersistenceContext
//     private EntityManager entityManager;

//     @PostConstruct
//     public void enableFilter() {
//         Session session = entityManager.unwrap(Session.class);
//         session.enableFilter("deletedProductFilter").setParameter("deleted", false);
//     }
// }