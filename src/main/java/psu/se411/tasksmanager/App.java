package psu.se411.tasksmanager;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("My Project");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}