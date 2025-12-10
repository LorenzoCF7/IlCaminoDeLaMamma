module ilcaminodelamamma {
    // JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires javafx.swing;
    requires javafx.base;

    // Spring Boot
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;

    // Hibernate & Jakarta
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.naming;
    requires java.base;
    requires java.sql;

    // Jackson
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    // JAXB
    requires java.xml.bind;
    requires java.xml;

    // BCrypt para encriptación de contraseñas
    requires jbcrypt;

    // iText para generación de PDFs
    requires kernel;
    requires layout;
    requires io;

    // Exports & Opens
    opens ilcaminodelamamma to javafx.fxml;
    opens ilcaminodelamamma.controller to javafx.fxml;
    opens ilcaminodelamamma.view to javafx.fxml;
    opens ilcaminodelamamma.view.chef to javafx.fxml;
    opens ilcaminodelamamma.view.assistant to javafx.fxml;
    opens ilcaminodelamamma.view.waiter to javafx.fxml;
    opens ilcaminodelamamma.view.components to javafx.fxml;
    opens ilcaminodelamamma.config to org.hibernate.orm.core;

    opens ilcaminodelamamma.model to org.hibernate.orm.core, javafx.fxml;
    opens ilcaminodelamamma.DAO to org.hibernate.orm.core;
    opens ilcaminodelamamma.util to org.hibernate.orm.core;

    exports ilcaminodelamamma;
    exports ilcaminodelamamma.controller;
    exports ilcaminodelamamma.view;
    exports ilcaminodelamamma.view.chef;
    exports ilcaminodelamamma.view.assistant;
    exports ilcaminodelamamma.view.waiter;
    exports ilcaminodelamamma.view.components;
    exports ilcaminodelamamma.model;
    exports ilcaminodelamamma.service;
    exports ilcaminodelamamma.repository;
}
