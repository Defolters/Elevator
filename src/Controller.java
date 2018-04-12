
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    @FXML
    public Button add;
    @FXML
    public ChoiceBox<Integer> startFloor;
    @FXML
    public ChoiceBox<Integer> desiredFloor;
    @FXML
    public Label floor1;
    @FXML
    public Label direction1;
    @FXML
    public Label peopleIn1;
    @FXML
    public Label capacity1;
    @FXML
    public Label floor2;
    @FXML
    public Label direction2;
    @FXML
    public Label peopleIn2;
    @FXML
    public Label capacity2;
    @FXML
    public Canvas canvas;

    private Home home;

    private GraphicsContext graphCon;

    Image image;
    Image imageMan;

//    int x = 400;
    int y = 0;
//    int x2 = 565; // 675

    @Override
    public void initialize(URL location, ResourceBundle resources) {

//        lmuParser = new LMUParser();
        startFloor.getItems().addAll(1,2,3,4,5);
        startFloor.setValue(1);

        desiredFloor.getItems().addAll(1,2,3,4,5);
        desiredFloor.setValue(5);

        graphCon = canvas.getGraphicsContext2D();

        image = new Image("elevator.png", 120,120,false,false);
        imageMan = new Image("man2.png", 35,80,false,false);

        add.setOnAction(e -> {
            home.addMan(startFloor.getValue(), desiredFloor.getValue());
        });
    }

//    @FXML
    public void addMan(){
        System.out.println(startFloor.getValue() +" "+ desiredFloor.getValue());
        // add man
    }

    public void addHome(Home home){
        this.home = home;
    }

    public void update(Home home){
        // update

        //System.out.println(aa);
        graphCon.setGlobalAlpha(1.0);
        graphCon.setFill(Color.GREEN);
        graphCon.fillRect(0,0,675,600);

        Elevator ev1 = home.getElevators().get(0);
        Elevator ev2 = home.getElevators().get(1);

        floor1.setText(Integer.toString(ev1.getCurrentFloor()));
        direction1.setText(ev1.getDirection().toString());
        peopleIn1.setText(Integer.toString(ev1.getOccupancy()));
        capacity1.setText(Integer.toString(ev1.getCapacity()));

        floor2.setText(Integer.toString(ev2.getCurrentFloor()));
        direction2.setText(ev2.getDirection().toString());
        peopleIn2.setText(Integer.toString(ev2.getOccupancy()));
        capacity2.setText(Integer.toString(ev2.getCapacity()));

        graphCon.drawImage(image, ev1.getX(),ev1.getY());
//        System.out.println("ELEVATOR getY: " + ev1.getY());
//        System.out.println("ELEVATOR Y480-120: " + (480-120*(ev1.getCurrentFloor()-1)));//480-120*(ev1.getCurrentFloor()-1));
        //graphCon.drawImage(image, ev1.getX(),480-120*(ev1.getCurrentFloor()-1));

        int i = 0;
        // если лифт двигается, людей можно не рисовать?
        for(Man man : ev1.getPeopleInElevator()){
            graphCon.drawImage(imageMan, ev1.getX()+15+15*i, man.y);//480-120*(ev1.getCurrentFloor()-1) + 40);
            i++;
        }
        // draw door (5 states)


//        graphCon.drawImage(image, ev2.getX(),480-120*(ev2.getCurrentFloor()-1));
        graphCon.drawImage(image, ev2.getX(), ev2.getY());
        i = 0;
        // если лифт двигается, людей можно не рисовать?
        for(Man man : ev2.getPeopleInElevator()){
            graphCon.drawImage(imageMan, ev2.getX()+15+15*i, man.y);//480-120*(ev2.getCurrentFloor()-1) + 40);
            i++;
        }
        // draw door (5 states)

        // рисуем ждущих людей

        i = 0;
        for(Man man : home.getPeople()){
            System.out.println("Waiting:");
            System.out.println(man);

            graphCon.drawImage(imageMan, man.x-i, 480-120*(man.getStartFloor()-1) + 40);
            i+=10;
        }

        for(Man man : home.getPeopleIn()){
            graphCon.drawImage(imageMan, man.x, 480-120*(man.getStartFloor()-1) + 40);
        }

        for(Man man : home.getPeopleOut()){
            graphCon.drawImage(imageMan, man.x, 480-120*(man.getDesiredFloor()-1) + 40);
        }
    }

}
