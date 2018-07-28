import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Добавить лист ожидания для людей, которые появились, но идут в сторону лифта
 */
public class Home {

//    private ArrayList<Man> people;
    List<Man> people;
    ArrayList<Man> peopleOut; // for animation out
    ArrayList<Man> peopleIn;

    ArrayList<Elevator> elevators;

    private int numberOfFloors;
    private int numberOfElevators;

    Home(int numberOfFloors, int numberOfElevators){
        //this.people = new ArrayList<>();
        elevators = new ArrayList<>();
        peopleOut = new ArrayList<>();
        peopleIn = new ArrayList<>();


        this.numberOfFloors = numberOfFloors;
        this.numberOfElevators = numberOfElevators;
        people = Collections.synchronizedList(new ArrayList<Man>());

        for (int i =0; i < numberOfElevators; i++){
            Elevator ev = new Elevator(5, numberOfFloors, i, 400+165*i);
            elevators.add(ev);
        }
    }

    public void movePeople(){
        Iterator<Man> it = peopleIn.iterator();
        while (it.hasNext()){
            Man man = it.next();
            man.x+=5;
            man.nextAnimation();
            if(man.x==350){
                synchronized (people){
                    people.add(man);
                }
                it.remove();
            }
        }


        it = peopleOut.iterator();
        while (it.hasNext()){
            Man man = it.next();
            man.x-=5;
            if(man.x == 0){
                it.remove();
            }
        }
    }

    public void moveElevators(){
        for(Elevator elevator : elevators){
            elevator.move();
        }
    }

    public ArrayList<Man> getPeople(){
        synchronized (people){

            ArrayList<Man> peopleRet;
            peopleRet = new ArrayList<>(people);

            return peopleRet;
        }
    }

    /**
     * Method analyse and return people to elevator
     * delete from list and delete from graphics
     * @param floor
     * @param direction
     */
    ArrayList<Man> getPeople(int floor, ElevatorDirection direction, int amount){
        synchronized (people) {
            Iterator<Man> it = people.iterator();
            ArrayList<Man> peopleToElevator = new ArrayList<>();

            while (it.hasNext()) {
                if (peopleToElevator.size() == amount){
                    return peopleToElevator;
                }
                Man man = it.next();

                // если лифт стоит на том же месте, где и человек, и едет по его пути или просто стоит, то добавляем
                if (man.getStartFloor() == floor){
                    if (man.getDirection() == direction){
                        peopleToElevator.add(man);
                        it.remove();
                    }
                    //если стоит, то посадить одного и отправить куда-то
                    else if (direction == ElevatorDirection.HOLD){
                        direction = man.getDirection();
                        peopleToElevator.add(man);
                        it.remove();
                    }
                }
            }
            return peopleToElevator;
        }
    }

    public ArrayList<Man> getPeopleIn() {
        return peopleIn;
    }

    public ArrayList<Man> getPeopleOut() {
        return peopleOut;
    }

    public void addMan(Man man){
        synchronized (peopleIn){
            peopleIn.add(man);
        }
    }

    public void addMan(int start, int desired){
        synchronized (peopleIn){
            Man man = new Man(0, start, desired);
            peopleIn.add(man);
        }
    }

    public void addPeopleOut(ArrayList<Man> peopleOut){
        this.peopleOut.addAll(peopleOut);
    }

    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    public int getNumberOfElevators() {
        return numberOfElevators;
    }

    public ArrayList<Elevator> getElevators() {
        return elevators;
    }

    public int getNumberOfWaiting(){
        return people.size();
    }
}
