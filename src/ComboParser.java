public class ComboParser {
    public static final int COMBO_FINISHED = -999;
    public static boolean FirstComboDialed = false;
    public static final int POLARITY = -1;
    public static final int NO_PREVIOUS_COMBO = -998;
    public static final int COUNTER_CLOCKWISE = -1;
    public static final int CLOCKWISE = 1;
    public static final int DEGREES_TO_UNLOCK = 300; //FIXME: this will need to be fine-tuned
    public static final int UNLOCK_INDEX = 80;
    private final double degreesPerTick;

    private final int size;
    private final Dialer dialer;

    private int[] combo;
    private int[] prevCombo = {0, 0, 0};
    private int index;
    private int currPosInDegrees;
    private boolean tryToOpen;
    private boolean firstRun;
    private int stepSizeMultiplier = 1;

    ComboParser(Dialer dialer){
        this.dialer = dialer;
        this.combo = dialer.getNextCombination();
        this.index = 0;
        this.size = combo.length;
        this.degreesPerTick = 360.0 / dialer.getTickCount();
        this.currPosInDegrees = 0;
        this.tryToOpen = false;
        this.firstRun = true;
    }

    ComboParser(Dialer dialer, int[] startingCombo){
        this(dialer);
        this.combo = startingCombo;
        dialer.setCurrentCombination(startingCombo);
    }

    //
    public int getNextNumber(){
        if(this.index < this.size){
            return this.combo[index++];
        }
        return COMBO_FINISHED;
    }

    public String getNextRotationCommand(){
        if(firstRun) {
            setInitialCombo(dialer.getCurrentCombination());
            firstRun = false;
        }
        String command = "TurnDial:";
        int degreesToTurn = 0;

        if(tryToOpen){
            System.out.println("Trying to open");
            degreesToTurn += getDegreesFromNumber(combo[2]); //BACK TO 0
            degreesToTurn += getDegreesFromNumber(100 - UNLOCK_INDEX);
            this.index = 0;
            tryToOpen = false;
            prevCombo = this.combo.clone();
            this.combo = dialer.getNextCombination(1);
        }
        else {
            switch (index) {
                case 0:
                    System.out.println("Rotating first index");
                    if (previousComboOnlyDiffersByLastNumber() && FirstComboDialed) {
                        System.out.println("prevonlydiff");
                        degreesToTurn -= getDegreesFromNumber(100- UNLOCK_INDEX); //back to 0
                        degreesToTurn -= getDegreesFromNumber(combo[2]);
                        tryToOpen = true;
                        //keep index at 0;
                    }
                    else{
                        degreesToTurn -= 360 * 4; // 3 FULL ROTATIONS
                        degreesToTurn -= 360 - currPosInDegrees; // back to 0
                        degreesToTurn -= getDegreesFromNumber(combo[0]);
                        System.out.println("Reset deg: " + degreesToTurn);
                        this.index = 1;
                    }
                    break;
                case 1:
                    System.out.println("Rotating second index");
                    degreesToTurn += 360 * 2; // 2 full rotations
                    if(combo[0] > combo[1] ){
                        degreesToTurn += getDegreesFromNumber(combo[0] - combo[1]);
                    }
                    else{
                        degreesToTurn += 360 - getDegreesFromNumber(combo[1] - combo[0]);
                    }
                    this.index = 2;
                    break;
                case 2:
                    System.out.println("Rotating third index");
                    degreesToTurn -= 360;
                    if(combo[1] >= combo[2]){
                        degreesToTurn -= 360 - getDegreesFromNumber(combo[1] - combo[2]);
                    }
                    else{
                        degreesToTurn -= getDegreesFromNumber(combo[2] - combo[1]);
                    }
                    FirstComboDialed = true;
                    tryToOpen = true;
                    //index gets set back to 0 in the tryToOpen block
                    break;
                default:
                    System.out.println("Error in index switch");
                    break;
            }
        }
        degreesToTurn *= POLARITY;
        System.out.println("Combo being turned: " + dialer.ToString());
        currPosInDegrees += degreesToTurn;
        currPosInDegrees = getEquivalentAngle(currPosInDegrees);
        command += Integer.toString(degreesToTurn * stepSizeMultiplier);
        System.out.println(command);
        return command;
    }

    private boolean previousComboOnlyDiffersByLastNumber(){
        if(prevCombo[0] == NO_PREVIOUS_COMBO)
            return true;

        for(int i = 0; i < this.combo.length - 1; i++){
            if(this.combo[i] != this.prevCombo[i])
                return false;
        }
        return true;
    }

    double getDegreesFromNumber(int ticks){
        return ticks * degreesPerTick;
    }

    int[] getPrevCombo(){
        return prevCombo;
    }

    String getComboAsString(){
        return Integer.toString(combo[0]) + "-" + Integer.toString(combo[1]) + "-" + Integer.toString(combo[2]);
    }

    int getEquivalentAngle(int angle){
        if(angle < 0) {
            while(angle < 0){
                angle += 360;
            }
        }
        else{
            while(angle > 360){
                angle -= 360;
            }
        }
        return angle;
    }

    public void setInitialCombo(int[] combo){
        this.combo = combo;
    }
    public void setStepSizeMultiplier(int multiplier){
        this.stepSizeMultiplier = multiplier;
    }
}
