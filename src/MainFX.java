import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {
    private Stage window;
    static private Controller controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Home home = new Home(5,2);

        final long start = System.nanoTime();

        PeopleManager pm = new PeopleManager(home);
        Thread t1 = new Thread(pm);


        ElevatorManager em = new ElevatorManager(home);
        Thread t2 = new Thread(em);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainwindow.fxml"));
        Parent pane = fxmlLoader.load();
        controller = fxmlLoader.getController();

        window = primaryStage;
        window.setTitle("Elevator");

        controller.addHome(home);

        Scene scene = new Scene(pane);

        window.setOnCloseRequest(event ->  {
            t1.interrupt();
            t2.interrupt();
        });

        window.setScene(scene);
        window.show();
        t1.start();
        t2.start();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                controller.update(home);
                home.movePeople();
            }

        };
        timer.start();
        final long end = System.nanoTime();

        System.out.println("Took: " + ((end - start) / 1000000) + "ms");
    }
}
