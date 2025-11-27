module ilcaminodelamamma {
    // JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    
    // Spring Boot
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    
    // Hibernate
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    
    // MySQL
    requires java.sql;
    
    // Jackson
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    
    // JAXB
    requires java.xml.bind;
    
    // Exports
    opens ilcaminodelamamma to javafx.fxml;
    opens ilcaminodelamamma.controller to javafx.fxml;
    opens ilcaminodelamamma.view to javafx.fxml;
    opens ilcaminodelamamma.model to org.hibernate.orm.core;
    
    exports ilcaminodelamamma;
    exports ilcaminodelamamma.controller;
    exports ilcaminodelamamma.view;
    exports ilcaminodelamamma.model;
    exports ilcaminodelamamma.service;
    exports ilcaminodelamamma.repository;
}
