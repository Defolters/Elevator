

public class Main {
    public static void main(String argc[]){
        Home home = new Home(5,2);

        PeopleManager pm = new PeopleManager(home);
        Thread t1 = new Thread(pm);
        t1.start();

        ElevatorManager em = new ElevatorManager(home);
        Thread t2 = new Thread(em);
        t2.start();

        //t1.join();
        //t2.join();
    }
}
