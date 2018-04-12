import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import static java.lang.Math.abs;

public class ElevatorManager implements Runnable {
    Home home;
    ArrayList<Man> people;
//    ArrayList<Elevator> elevators;

    ElevatorManager(Home home){
        this.home = home;
        people = new ArrayList<>();
//        elevators = new ArrayList<>();
//        int id = 0;
//        for (int i =0; i< home.getNumberOfElevators();i++){
//            Elevator ev = new Elevator(5, home.getNumberOfFloors(), id);
//            elevators.add(ev);
//            id++;
//        }
    }

    void getPeople(){
        people.addAll(home.getPeople());

        System.out.println("Waiting");
        for (Man man : people){
            System.out.println(man);
        }
    }

//    public ArrayList<Elevator> getElevators(){
//        return elevators;
//    }

    @Override
    public void run() {
        System.out.println("thread elevator manager is running...");
        int i = 0;

        while(true){
//            getPeople();

//            home.moveElevators();

            for (Elevator ev : home.getElevators()) {
                /*if (ev.isMoving()){
                    continue;
                }*/
                ArrayList<Man> peopleOut = ev.updatePosition();
                // меняем этаж только после того, как передвинулись, ближайшие ничего не меняют
                home.addPeopleOut(peopleOut);
            }

            System.out.println("\n");
            for (Elevator ev : home.getElevators()){
                System.out.print("Elevator:" + ev.getId() + " floor:" + ev.getCurrentFloor() + " occupancy:"+ ev.getOccupancy()+"/"+ev.getCapacity() +" " +ev.getIds()+" || ");
            }
            System.out.println("\n");


            // это можно перенести в home
            // добавялем людей в лифт
            for (Elevator ev : home.getElevators()){
                if(ev.isMoving()){
                    continue;
                }
                ArrayList<Man> peopleToElevator = home.getPeople(ev.getCurrentFloor(), ev.getDirection(), ev.getCapacity()-ev.getOccupancy());

                for(Man man : peopleToElevator){
                    ev.addMan(man);
                }
            }

            //если движется наверх искать ближайшего
            //если стоит, а человек добавляется далеко (выбрать ближайший и добавить в план) (добавить в план, если ближе и едет вверх)
            // если ждет или движется по нашему пути, то добавить желаемое
            // найти ближайшего (движущегося или стоячего)
            for (Man man : home.getPeople()){
                int nearest = -1;
                int posInArray = 0;
                int length = home.getNumberOfFloors() + 1;

                for (Elevator elevator : home.getElevators()){
                    // ищем ближаший элеватор к человеку, сохраняем в nearest
                    // или лифт стоит, или лифт движется вверх в сторону человека и человек хочет наверх, или вниз в сторону человека и человек хочет вниз
                    // если лифт стоит, ничего не будет обрабатываться?
                    if (( (elevator.getDirection() == ElevatorDirection.HOLD) ||
                            (((elevator.getCurrentFloor() < man.getStartFloor()) && (elevator.getDirection()== ElevatorDirection.UP) && (man.getDirection() == ElevatorDirection.UP) )//&& (!elevator.isEmpty())) // если лифт пустой и движется, значит, его кто-то вызвал, поэтому садиться в него нельзя (потом можно еще учитывать не только вызовы, но и направления, чтобы садить людей, если они хотят вверх, как и те, кто вызвал лифт)
                            || ((elevator.getCurrentFloor() > man.getStartFloor() ) && (elevator.getDirection()== ElevatorDirection.DOWN) && (man.getDirection() == ElevatorDirection.DOWN) )//&& (!elevator.isEmpty()))
                            )
                        )
                            && (Math.abs(elevator.getCurrentFloor() - man.getStartFloor()) < length)){
                        length = Math.abs(elevator.getCurrentFloor() - man.getStartFloor());
                        nearest = posInArray;
                    }
                    posInArray++;
                }

                if (nearest != -1){
                    home.getElevators().get(nearest).addDirection(man.getStartFloor());
                }
            }

            try{
                Thread.sleep(10);
            }
            catch(InterruptedException ex){
                return;
            }

            if(Thread.interrupted()){

            }
        }
    }

}
