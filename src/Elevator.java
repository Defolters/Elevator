import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class Elevator {
    private int currentFloor;
    private int capacity;
    private int occupancy;
    private int numberOfFloors;
    private int id;
    private int x;
    private int y;
    private int dist;
    private boolean moving;
    private boolean doorsClosing;
    private boolean doorsOpening;
    private boolean goingToMove;
    private double goingToMoveTime;
    private int doorStatus; // 0 - open, 1,2,3,4 - closed

    private ElevatorStatus status;
    private ElevatorDirection direction;
    private ArrayList<Man> peopleInElevator;
    private HashSet<Integer> directions;

    Elevator(int capacity, int numberOfFloors, int id, int x){
        peopleInElevator = new ArrayList<>();
        directions = new HashSet<>();

        this.capacity = capacity;
        this.occupancy = 0;
        this.currentFloor = 1;
        this.direction = ElevatorDirection.HOLD;
        this.status = ElevatorStatus.HOLDING;
        this.numberOfFloors = numberOfFloors;
        this.id = id;
        this.x = x;
        this.y = 480;
        this.doorStatus = 0;
    }

    void addDirection(int floor, ElevatorDirection direction){
        // поставить ближний
//        for (Integer integer : directions){
//            if (Math.abs(currentFloor-floor) < Math.abs(currentFloor - integer)){
//                directions.clear();
//                directions.add(floor);
//            }
//        }

        directions.add(floor);
        System.out.println("DIRECTIONSSSSS " + id + " " + directions);

        if (floor > currentFloor){
            this.direction = ElevatorDirection.UP;
        }
        else{
            this.direction = ElevatorDirection.DOWN;
        }

        /*if (status == ElevatorStatus.HOLDING){
            status = ElevatorStatus.DOORSCLOSING;
        }*/
        // если лифт стоит

        if (status == ElevatorStatus.HOLDING){
//            status = ElevatorStatus.GOINGTOMOVE;
            status = ElevatorStatus.MOVING;
            final long start = System.nanoTime() / 1000000; //ms
            goingToMoveTime = start/1000; //seconds
            System.out.println("Going to move holding time:" + goingToMoveTime + "s");
        }

        // если лифт двигается
        // ничего?
    }

    void addMan(Man man){
        man.x = this.x+15;
        man.y = this.y+40;

        peopleInElevator.add(man);

        if (man.getDesiredFloor() > currentFloor){
            direction = ElevatorDirection.UP;
        }
        else{
            direction = ElevatorDirection.DOWN;
        }

        // если лифт просто стоит (в другие состояния мы не должны добавлять людей)
        if (!(status == ElevatorStatus.GOINGTOMOVE)){
//            goingToMove = true;
//            status = ElevatorStatus.GOINGTOMOVE;
            status = ElevatorStatus.MOVING;
            final long start = System.nanoTime() / 1000000; //ms
            goingToMoveTime = start/1000; //seconds

            System.out.println("Going to move adding time:" + goingToMoveTime + "s");
        }

        //moving=true;
        System.out.println("Elevator " + id + " new man: " + man.getId());
        occupancy++;
    }

    ArrayList<Man> updatePosition(){

        // if elevator is holding, we do nothing
        /*if (status == ElevatorStatus.HOLDING){
            return new ArrayList<>();
        }*/

        /*
        // if elevator going to move, we wait for some time
        if (status == ElevatorStatus.GOINGTOMOVE){

            final long end = System.nanoTime() / 1000000; //ms
            double time = end/1000; //seconds

            // если время ожидания прошло, то закрыываем двери
            if ((time-goingToMoveTime) > 1.3){
//                status = ElevatorStatus.DOORSCLOSING;
                status = ElevatorStatus.MOVING;
            }
            // else return
            else{
                return new ArrayList<>();
            }

        }
        // if elevator closing doors, we close doors
        if (status == ElevatorStatus.DOORSCLOSING){
            // if doors are not closed, close it
            if (doorStatus<4){
                doorStatus++;
                return new ArrayList<>();
            }
            // if doors closed, we are moving
            status = ElevatorStatus.MOVING;
        }

        //if elevator moving, we move elevator
        if (status == ElevatorStatus.MOVING){
            if (((direction == ElevatorDirection.UP) && (currentFloor == 5)) || ((direction == ElevatorDirection.DOWN) && (currentFloor == 1))){
                System.out.println("FFFFFFFFFFFFFFFFFFFF");
                direction = ElevatorDirection.HOLD;
                status = ElevatorStatus.HOLDING;
            }

            if ((direction == ElevatorDirection.UP)){
//                this.y-=2;
                this.y-=120;
            }
            else if ((direction == ElevatorDirection.DOWN)){
//                this.y+=2;
                this.y+=120;
            }

            // move people in elevator with elevator
            for(Man man : peopleInElevator){
                man.y = this.y+40;
            }

            // if we reach some floor
            if (y % 120 == 0){
                // move elevator
                if ((direction == ElevatorDirection.UP)){
                    currentFloor++;
                }
                else if ((direction == ElevatorDirection.DOWN)){
                    currentFloor--;
                }

                // check if we should stop
                boolean needOut = false;
                for (Man man : peopleInElevator){
                    if (man.getDesiredFloor() == currentFloor){
                        needOut = true;
                        break;
                    }
                }
                // if some people want to get into elevator
                for (Integer floor : directions){
                    if (floor == currentFloor){
                        directions.remove(currentFloor);
                        needOut = true;
                        break;
                    }
                }
                directions.clear();
                if (peopleInElevator.isEmpty()){
                    status = ElevatorStatus.HOLDING;
                    direction = ElevatorDirection.HOLD;
                }

                // if we should stop, stop moving and open doors
                if (needOut){
//                    status = ElevatorStatus.DOORSOPENING;
                    status = ElevatorStatus.PEOPLEREMOVING;
//                    direction = ElevatorDirection.HOLD;
                }
                // else continue to move
                else{
                    return new ArrayList<>();
                }
            }
            // else continue to move
            else{
                return new ArrayList<>();
            }
        }

        //
        if (status == ElevatorStatus.DOORSOPENING){
            // if doors are not open, open it
            if (doorStatus>0){
                doorStatus--;
                return new ArrayList<>();
            }
            // if doors opened, we can remove people
            status = ElevatorStatus.PEOPLEREMOVING;
        }

        //
        if (status == ElevatorStatus.PEOPLEREMOVING){
            // remove people, if any going, if no, holding
            ArrayList<Man> peopleOut = new ArrayList<>();

            // если кому-то нужно выходить, переносим в другой список
            Iterator<Man> iterator = peopleInElevator.iterator();
            while (iterator.hasNext()) {
                Man man = iterator.next();
                if(man.getDesiredFloor() == currentFloor){
                    peopleOut.add(man);
                    iterator.remove();
                    System.out.println();
                    System.out.println(man.getId() + " removed!");
                    occupancy--;
                }
            }

            // if elevator is empty, set holding
            if (peopleInElevator.isEmpty()){
                status = ElevatorStatus.HOLDING;
                direction = ElevatorDirection.HOLD;
            }
            // if not empty, elevator going to continue its path
            else{
//                status = ElevatorStatus.GOINGTOMOVE;
                status = ElevatorStatus.MOVING;
            }

            return peopleOut;
        }*/

        if(direction == ElevatorDirection.HOLD){
            return new ArrayList<>();
        }

        if (((direction == ElevatorDirection.UP) && (currentFloor == 5)) || ((direction == ElevatorDirection.DOWN) && (currentFloor == 1))){
            System.out.println("FFFFFFFFFFFFFFFFFFFF");
            direction = ElevatorDirection.HOLD;
            status = ElevatorStatus.HOLDING;
        }

        if ((direction == ElevatorDirection.UP)){
            currentFloor++;
            this.y-=120;
        }
        else if ((direction == ElevatorDirection.DOWN)){
            currentFloor--;
            this.y+=120;
        }

        for(Man man : peopleInElevator){
            man.y = this.y+40;
        }

        ArrayList<Man> peopleOut = new ArrayList<>();
        // если кому-то нужно выходить (убираем из списка)
        Iterator<Man> iterator = peopleInElevator.iterator();
        while (iterator.hasNext()) {
            Man man = iterator.next();
            if(man.getDesiredFloor() == currentFloor){
                peopleOut.add(man);
                iterator.remove();
                System.out.println();
                System.out.println(man.getId() + " removed!");
                occupancy--;
            }
        }

        directions.remove(currentFloor);
        //System.out.println(directions);

        if (peopleInElevator.isEmpty() && directions.isEmpty()){
            direction = ElevatorDirection.HOLD;
        }
//        directions.clear();
        return peopleOut;



//        return new ArrayList<>();
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getOccupancy() {
        return occupancy;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ElevatorDirection getDirection() {
        return direction;
    }

    public ElevatorStatus getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Man> getPeopleInElevator() {
        return peopleInElevator;
    }

    public int getDoorStatus() {
        return doorStatus;
    }

    boolean isFull(){
        if(peopleInElevator.size() == capacity){
            return true;
        }

        return false;
    }

    boolean isEmpty(){
        if (occupancy == 0){
            return true;
        }
        return false;
    }

    public String getIds(){
        StringBuilder sb = new StringBuilder();
        sb.append("Ids: ");
        for (int i = 0; i < peopleInElevator.size();i++){
             sb.append(peopleInElevator.get(i).getId());
             sb.append(" ");
        }
        return sb.toString();
    }

    public void move(){
        for(Man man : peopleInElevator){
            man.y = this.y;
        }
        if ((direction == ElevatorDirection.UP) && (dist>0)){
            this.y-=2;
            dist-=2;
            moving=true;
            return;
        }
        else if ((direction == ElevatorDirection.DOWN)&&(dist>0)){
            this.y+=2;
            dist-=2;
            moving=true;
            return;
        }
        moving = false;

    }

    public boolean isMoving() {
        return moving;
    }

    boolean isMovingToFloor(int floor){
        return directions.contains(floor);
    }
}
