import java.util.ArrayList;
import static java.lang.Math.abs;

public class ElevatorManager implements Runnable {
    private Home home;
    private ArrayList<Man> people;

    ElevatorManager(Home home){
        this.home = home;
        people = new ArrayList<>();
    }

    @Override
    public void run() {
        System.out.println("thread elevator manager is running...");

        while(true){

            for (Elevator ev : home.getElevators()) {
                ArrayList<Man> peopleOut = ev.updatePosition();

                // добавляем вышедших людей
                if (!peopleOut.isEmpty()){
                    home.addPeopleOut(peopleOut);
                }
            }

            // выводим информации в консоль
            System.out.println("\n");
            for (Elevator ev : home.getElevators()){
                System.out.print("Elevator:" + ev.getId() + " floor:" + ev.getCurrentFloor() + " occupancy:"+ ev.getOccupancy()+"/"+ev.getCapacity() +" " +ev.getIds()+" || ");
            }
            System.out.println("\n");


            // можем посадить человека, если лифт стоит или собирается ехать
            for (Elevator ev : home.getElevators()){
                if(!( (ev.getStatus() == ElevatorStatus.HOLDING) || (ev.getStatus() == ElevatorStatus.GOINGTOMOVE) )){
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

                for (Elevator ev : home.getElevators()){
                    /*if (((ev.getStatus() == ElevatorStatus.HOLDING) ||
                            (ev.getDirection()==ElevatorDirection.HOLD) ||
                            ( (ev.getStatus() == ElevatorStatus.MOVING)&&(ev.isEmpty())&&(ev.isMovingToFloor(man.getStartFloor()))) || // если лифт движется к нам и он пустой
                            ( (ev.getStatus() == ElevatorStatus.GOINGTOMOVE)&&(ev.isEmpty())&&(ev.isMovingToFloor(man.getStartFloor()))) || //если лифт собирается двигаться в нашу сторону
                            ( (ev.getStatus() == ElevatorStatus.GOINGTOMOVE)&&(ev.isEmpty())&&(ev.isMovingToFloor(man.getStartFloor())))
                        )
                            && (Math.abs(ev.getCurrentFloor() - man.getStartFloor()) < length)){
                        length = Math.abs(ev.getCurrentFloor() - man.getStartFloor());
                        nearest = posInArray;
                    }*/
                    // ищем ближаший элеватор к человеку, сохраняем в nearest
                    // или лифт стоит, или лифт движется вверх в сторону человека и человек хочет наверх, или вниз в сторону человека и человек хочет вниз
                    // если лифт стоит, ничего не будет обрабатываться?
                    if (( (ev.getDirection() == ElevatorDirection.HOLD) ||
                            (((ev.getCurrentFloor() <= man.getStartFloor()) && (ev.getDirection()== ElevatorDirection.UP) && (man.getDirection() == ElevatorDirection.UP) )//&& (!elevator.isEmpty())) // если лифт пустой и движется, значит, его кто-то вызвал, поэтому садиться в него нельзя (потом можно еще учитывать не только вызовы, но и направления, чтобы садить людей, если они хотят вверх, как и те, кто вызвал лифт)
                            || ((ev.getCurrentFloor() >= man.getStartFloor() ) && (ev.getDirection()== ElevatorDirection.DOWN) && (man.getDirection() == ElevatorDirection.DOWN) )//&& (!elevator.isEmpty()))
                            )
                        )
                            && (Math.abs(ev.getCurrentFloor() - man.getStartFloor()) < length)){
                        length = Math.abs(ev.getCurrentFloor() - man.getStartFloor());
                        nearest = posInArray;
                    }
                    posInArray++;
                }

                if (nearest != -1){
                    // учитывать направление
                    home.getElevators().get(nearest).addDirection(man.getStartFloor(), man.getDirection());
                }
            }

            try{
                Thread.sleep(1500);
            }
            catch(InterruptedException ex){
                return;
            }

            if(Thread.interrupted()){

            }
        }
    }

}
