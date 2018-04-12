/*import javafx.animation.Animation;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainFX extends Application {

    private static final Image IMAGE = new Image("horse.jpg");

    private static final int COLUMNS  =   4;
    private static final int COUNT    =  10;
    private static final int OFFSET_X =  18;
    private static final int OFFSET_Y =  25;
    private static final int WIDTH    = 374;
    private static final int HEIGHT   = 243;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("The Horse in Motion");

        final ImageView imageView = new ImageView(IMAGE);
        imageView.setViewport(new Rectangle2D(OFFSET_X, OFFSET_Y, WIDTH, HEIGHT));

        final Animation animation = new SpriteAnimation(
                imageView,
                Duration.millis(1000),
                COUNT, COLUMNS,
                OFFSET_X, OFFSET_Y,
                WIDTH, HEIGHT
        );
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();

        primaryStage.setScene(new Scene(new Group(imageView)));
        primaryStage.show();
    }
}*/
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainFX extends Application {
    Stage window;
//    Button button;
    static Controller controller;
//    static Home home;

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
        //Controller controller = (Controller) fxmlLoader.getController();
        controller = fxmlLoader.getController();
        //Parent root = FXMLLoader.load(getClass().getResource("mainwindow.fxml"));

        window = primaryStage;
        window.setTitle("Elevator");
//        button = new Button("Click me");
        controller.addHome(home);
//        StackPane layout = new StackPane();
//        layout.getChildren().add(button);
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
                //move people in home;
                home.movePeople();
                //home.moveElevators();
            }

        };
        timer.start();
        final long end = System.nanoTime();

        System.out.println("Took: " + ((end - start) / 1000000) + "ms");
    }
}

/*
- лифт закрывает и открвает двери (после того, как в него сели)
- лифт ждет, пока в него сядут (поставить флаг пауза, пока координаты человека не будут рядом с лифтом, затем его перенести туда) (некоторое время)
- люди анимация ходьбы
- людей опустить, чтобы шли по земле
- фон дома и этажей
- ставить лифт на паузу
- анимацию лифта плавную
- люди стоят рядом, а не на друг друге
- люди в лифте стоят рядом
- координаты: лифт в движении, лифт ждет какое-то время
- двигать каждые 120

- стоит.
подошел человек.
сел.
еще некторое время лифт стоит.
затем двери закрываются.
он едет вверх, пока этаж или не нужный, или не в желаемых.
если нужный, останавливается и перемещает человека в дом
если желаемый, то останавливается
ждет, пока сядет человек

впускает человека и ждет некоторое время,
затем двери закрыываются
он продолжает ехать


сел человек и лифт ждет некоторое время
потом двери закрываются
лифт двигается, пока не достигнет нужного или ждущего этажа
*/
/*
import javafx.animation.*;
import javafx.application.Application;
import javafx.event.*;
import javafx.scene.Scene;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class MainFX extends Application {
    private static final int FIELD_SIZE = 10;

    private static final Random random = new Random(42);

    @Override
    public void start(Stage stage) throws Exception {
        TilePane field = generateField();

        Scene scene = new Scene(field);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        field.addEventFilter(
                LightningEvent.PLASMA_STRIKE,
                event -> System.out.println(
                        "Field filtered strike: " + event.getI() + ", " + event.getJ()
                )
        );

        field.addEventHandler(
                LightningEvent.PLASMA_STRIKE,
                event -> System.out.println(
                        "Field handled strike: " + event.getI() + ", " + event.getJ()
                )
        );

        periodicallyStrikeRandomNodes(field);
    }

    private void periodicallyStrikeRandomNodes(TilePane field) {
        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        event -> strikeRandomNode(field)
                ),
                new KeyFrame(
                        Duration.seconds(2)
                )
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void strikeRandomNode(TilePane field) {
        LightningReactor struckNode = (LightningReactor)
                field.getChildren()
                        .get(
                                random.nextInt(
                                        FIELD_SIZE * FIELD_SIZE
                                )
                        );
        LightningEvent lightningStrike = new LightningEvent(
                this,
                struckNode
        );

        struckNode.fireEvent(lightningStrike);
    }

    private TilePane generateField() {
        TilePane field = new TilePane();
        field.setPrefColumns(10);
        field.setMinWidth(TilePane.USE_PREF_SIZE);
        field.setMaxWidth(TilePane.USE_PREF_SIZE);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                field.getChildren().add(
                        new LightningReactor(
                                i, j,
                                new StrikeEventHandler()
                        )
                );
            }
        }
        return field;
    }

    private class LightningReactor extends Rectangle {
        private static final int SIZE = 20;
        private final int i;
        private final int j;

        private FillTransition fillTransition = new FillTransition(Duration.seconds(4));

        public LightningReactor(int i, int j, EventHandler<? super LightningEvent> lightningEventHandler) {
            super(SIZE, SIZE);

            this.i = i;
            this.j = j;

            Color baseColor =
                    (i + j) % 2 == 0
                            ? Color.RED
                            : Color.WHITE;
            setFill(baseColor);

            fillTransition.setFromValue(Color.YELLOW);
            fillTransition.setToValue(baseColor);
            fillTransition.setShape(this);

            addEventHandler(
                    LightningEvent.PLASMA_STRIKE,
                    lightningEventHandler
            );
        }

        public void strike() {
            fillTransition.playFromStart();
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }
    }

    private class StrikeEventHandler implements EventHandler<LightningEvent> {
        @Override
        public void handle(LightningEvent event) {
            LightningReactor reactor = (LightningReactor) event.getTarget();
            reactor.strike();

            System.out.println("Reactor received strike: " + reactor.getI() + ", " + reactor.getJ());


            // event.consume();  if event is consumed the handler for the parent node will not be invoked.
        }
    }

    static class LightningEvent extends Event {

        private static final long serialVersionUID = 20121107L;

        private int i, j;

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }

        /**
         * The only valid EventType for the CustomEvent.
         */
        /*public static final EventType<LightningEvent> PLASMA_STRIKE =
                new EventType<>(Event.ANY, "PLASMA_STRIKE");
*/
        /**
         * Creates a new {@code LightningEvent} with an event type of {@code PLASMA_STRIKE}.
         * The source and target of the event is set to {@code NULL_SOURCE_TARGET}.
         */
  /*      public LightningEvent() {
            super(PLASMA_STRIKE);
        }
*/
        /**
         * Construct a new {@code LightningEvent} with the specified event source and target.
         * If the source or target is set to {@code null}, it is replaced by the
         * {@code NULL_SOURCE_TARGET} value. All LightningEvents have their type set to
         * {@code PLASMA_STRIKE}.
         *
         * @param source    the event source which sent the event
         * @param target    the event target to associate with the event
         */
  /*      public LightningEvent(Object source, EventTarget target) {
            super(source, target, PLASMA_STRIKE);

            this.i = ((LightningReactor) target).getI();
            this.j = ((LightningReactor) target).getJ();
        }

        @Override
        public LightningEvent copyFor(Object newSource, EventTarget newTarget) {
            return (LightningEvent) super.copyFor(newSource, newTarget);
        }

        @Override
        public EventType<? extends LightningEvent> getEventType() {
            return (EventType<? extends LightningEvent>) super.getEventType();
        }

    }

}*/