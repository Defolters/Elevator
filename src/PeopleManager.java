import java.util.Random;

public class PeopleManager implements Runnable {

    private Home home;
    private int lastId;
    private int numberOfFloors;
    private boolean auto;

    PeopleManager(Home home){
        this.home = home;
        this.numberOfFloors = home.getNumberOfFloors();
        this.lastId = 0;
        this.auto = true;
    }

    void addMan(){
        Random rand = new Random();
        int start = rand.nextInt(numberOfFloors) + 1;
        int wished = start;
        while(wished == start){
            wished = rand.nextInt(numberOfFloors) + 1;
        }

        Man man = new Man(lastId, start, wished);
        home.addMan(man);
        lastId++;
    }

    public void addMan(int start, int desired){
        Man man = new Man(lastId, start, desired);
        home.addMan(man);
        lastId++;
    }

    @Override
    public void run() {
        System.out.println("PeopleManager is running...");

        while(true){

            if (auto) {
                addMan();
            }

            Random rand = new Random();
            int start = rand.nextInt(3) + 2;

            try{
                Thread.sleep(start*1000);
            }
            catch(InterruptedException ex){
                return;
            }
        }
    }

    /**
     * Method sets true to @param auto that starts adding new people to @class Home
     */
    public void setAuto(){
        this.auto = true;
    }

    /**
     * Method sets false to @param auto that stops adding new people to @class Home
     */
    public void setManual(){
        this.auto = false;
    }
}
