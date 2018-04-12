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

    ElevatorDirection direction;
    ArrayList<Man> peopleInElevator;
    HashSet<Integer> directions;

    Elevator(int capacity, int numberOfFloors, int id, int x){
        peopleInElevator = new ArrayList<>();
        directions = new HashSet<>();

        this.capacity = capacity;
        this.occupancy = 0;
        this.currentFloor = 1;
        this.direction = ElevatorDirection.HOLD;
        this.numberOfFloors = numberOfFloors;
        this.id = id;
        this.x = x;
        this.y = 480;
        this.doorStatus = 0;
    }

    //void
    //void setDirection(){}

    void addDirection(int floor){
        directions.add(floor);

        System.out.println("DIRECTIONSSSSS " + id + " " + directions);

        if (floor > currentFloor){
            direction = ElevatorDirection.UP;
        }
        else{
            direction = ElevatorDirection.DOWN;
        }

        if (!moving){
            if (!goingToMove){
                goingToMove = true;

                final long start = System.nanoTime() / 1000000; //ms
                goingToMoveTime = start/1000; //seconds
            }
        }

    }

    void addMan(Man man){
        man.x = this.x;
        peopleInElevator.add(man);

        if (man.getDesiredFloor() > currentFloor){
            direction = ElevatorDirection.UP;
        }
        else{
            direction = ElevatorDirection.DOWN;
        }

        if (!goingToMove){
            goingToMove = true;

            final long start = System.nanoTime() / 1000000; //ms
            goingToMoveTime = start/1000; //seconds
        }

        //moving=true;
        System.out.println("Elevator " + id + " new man: " + man.getId());
        occupancy++;
    }

    ArrayList<Man> updatePosition(){
        // если мы собираемся ехать и время ожидания не прошло, то ждем
        // если время прошло, то собираемся ехать
        if (goingToMove){
            final long end = System.nanoTime() / 1000000; //ms
            double time = end/1000; //seconds
            goingToMoveTime = end/1000; //seconds
            if (true){//((end/1000 - goingToMoveTime) > 3){
                goingToMove = false;
                doorsClosing = true;
            }
            else{
                return new ArrayList<>();
            }
        }

        // если лифт стоит, то из него никто не выходит и он не двигается.
        if (direction == ElevatorDirection.HOLD){
            return new ArrayList<>();
        }

        // если состояние закрытия дверей, то закрываем двери и выходим из функции
        // если двери закрылись, то продолжаем работу, перемещаем лифт
        if (doorsClosing){
            if (doorStatus<4){
                doorStatus++;
                return new ArrayList<>();
            }
            doorsClosing = false;
            moving = true;
        }

        // открытие дверей
        if (doorsOpening){
            if (doorStatus>0){
                doorStatus--;
                return new ArrayList<>();
            }
            doorsOpening = false;
        }

        // двигаем лифт если moving, пока он не y%120 == 0
        if(moving){

            if ((direction == ElevatorDirection.UP)){
                this.y-=2;
            }
            else if ((direction == ElevatorDirection.DOWN)){
                this.y+=2;
            }

            for(Man man : peopleInElevator){
                man.y = this.y;
            }

            // если лифт на каком-то этаже, то нужно проверить, нужно ли нам останавливаться на этом этаже
            if (y % 120 == 0){
                moving = false;
                if ((direction == ElevatorDirection.UP)){
                    currentFloor++;
//            dist+=120;
                }
                else if ((direction == ElevatorDirection.DOWN)){
                    currentFloor--;
//            dist+=120;
                }
            }
            else{
                return new ArrayList<>();
            }
        }

        // проверка, что мы не уехали на крышу?
        // изменили этаж
        // дважды может выполниться это
//        if ((direction == ElevatorDirection.UP)){
//            currentFloor++;
////            dist+=120;
//        }
//        else if ((direction == ElevatorDirection.DOWN)){
//            currentFloor--;
////            dist+=120;
//        }

        // остановится и начать открывать двери в том случае, если нужно убрать человека или забрать
        // (сделать так, чтобы люди не могли сесть, пока двери не открылись)
        boolean needOut = false;
        for (Man man : peopleInElevator){
            if (man.getDesiredFloor() == currentFloor){
                needOut = true;
                break;
            }
        }

        for (Integer floor : directions){
            if (floor == currentFloor){
                needOut = true;
                break;
            }
        }

        if (needOut){
            doorsOpening = true;
        }
        else{
            moving=true;
            return new ArrayList<>();
        }

        // если двери еще не открылись, не продолжать
        if (doorStatus != 0){
            return new ArrayList<>();
        }

        //System.out.println("Elevator:" + id + " Current floor: " + currentFloor);
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

        // проверка направления (направления используются для того, чтобы мы ехали куда-то, когда мы стоим)
        directions.remove(currentFloor);
        //System.out.println(directions);

        if (peopleInElevator.isEmpty() && directions.isEmpty()){
            direction = ElevatorDirection.HOLD;
        }
//        directions.clear();
        return peopleOut;
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
}
