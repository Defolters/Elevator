public class Man {
    private int id;
    private int startFloor;
    private int desiredFloor;
    private ElevatorDirection direction;
    public int x;
    public int y;
    private int animationTurn;

    Man(int id, int startFloor, int desiredFloor){
        this.id = id;
        this.startFloor = startFloor;
        this.desiredFloor = desiredFloor;

        x=0;
        y=0;

        if (startFloor < desiredFloor){
            direction = ElevatorDirection.UP;
        }
        else{
            direction = ElevatorDirection.DOWN;
        }
    }

    public void nextAnimation(){}

    public int getId() {
        return id;
    }

    public int getStartFloor() {
        return startFloor;
    }

    public int getDesiredFloor() {
        return desiredFloor;
    }

    public ElevatorDirection getDirection(){
        return direction;
    }

    @Override
    public String toString() {

        return new String("Start:" + startFloor+" Desire:" + desiredFloor + " id:" +id);
    }
}
