module contactmanager {
    // JavaFX dependencies for UI components
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    
    // SQL support for SQLite database integration
    requires java.sql;

    exports com.virtucio;
}

