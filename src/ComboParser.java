public class ComboParser {
    public static boolean FirstComboDialed = false;
    public static final int POLARITY = -1;
    public static final int NO_PREVIOUS_COMBO = -998;

    public static final int STEPS_PER_TICK = 2;
    public static final int UNLOCK_INDEX = 80;
    private final double degreesPerTick;
    private final Dialer dialer;

    private int[] combo;
    private int[] prevCombo = {0, 0, 0};
    private int index;
    private int currPosition;
    private boolean tryToOpen;
    private boolean firstRun;
    private int stepSizeMultiplier = 1;
    private int delta = 2;
    int positiveOffset = 0;
    int negativeOffset = 0;

    ComboParser(Dialer dialer){
        this.dialer = dialer;
        this.combo = dialer.getCurrentCombination();
        this.index = 0;
        this.degreesPerTick = 360.0 / dialer.getTickCount();
        this.currPosition = 0;
        this.tryToOpen = false;
        this.firstRun = true;
    }

    ComboParser(Dialer dialer, int[] startingCombo){
        this(dialer);
        this.combo = startingCombo;
        dialer.setCurrentCombination(startingCombo);
    }

    public String getNextRotationCommand(){
        if(firstRun) {
            setInitialCombo(dialer.getCurrentCombination());
            firstRun = false;
        }
        String command = "003:";
        int ticksToTurn = 0;

        if(tryToOpen){
            //System.out.println("Trying to open");
            ticksToTurn += combo[2]; //BACK TO 0
            ticksToTurn += 100 - UNLOCK_INDEX;
            this.index = 0;
            tryToOpen = false;
            prevCombo = this.combo.clone();
            this.combo = dialer.getNextCombination(delta);
        }
        else {
            switch (index) {
                case 0:
                    //System.out.println("Rotating first index");
                    if (previousComboOnlyDiffersByLastNumber() && FirstComboDialed) {
                        ticksToTurn -= 100- UNLOCK_INDEX; //back to 0
                        ticksToTurn -= combo[2];
                        tryToOpen = true;
                        //keep index at 0;
                    }
                    else{
                        ticksToTurn -= 100 * 3; // 3 FULL ROTATIONS
                        ticksToTurn -= 100 - currPosition; // back to 0
                        ticksToTurn -= combo[0];
                        this.index = 1;
                    }
                    break;
                case 1:
                    //System.out.println("Rotating second index");
                    ticksToTurn += 100 * 2; // 2 full rotations
                    if(combo[0] > combo[1] ){
                        ticksToTurn += combo[0] - combo[1];
                    }
                    else{
                        ticksToTurn += 100 - combo[1] - combo[0];
                    }
                    this.index = 2;
                    break;
                case 2:
                    //System.out.println("Rotating third index");
                    ticksToTurn -= 100;
                    if(combo[1] >= combo[2]){
                        if(! (combo[1] == combo[2])  )
                            ticksToTurn -= 100 - combo[1] - combo[2];
                    }
                    else{
                        ticksToTurn -= combo[2] - combo[1];
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
        ticksToTurn *= POLARITY;
        //System.out.println("Combo being turned: " + dialer.ToString());
        currPosition += ticksToTurn;
        currPosition = getCorrespondingDialNumber(currPosition);

        /* CONVERTING TO STEPS */
        ticksToTurn *= STEPS_PER_TICK;
        /* ADDING BACKLASH COMPENSATION */
        if(ticksToTurn < 0) { ticksToTurn -= negativeOffset;}
        else if(ticksToTurn > 0){ ticksToTurn += positiveOffset;}

        command += Integer.toString(ticksToTurn * stepSizeMultiplier);
        System.out.println(command);
        return command;
    }

    public void setPositiveOffset(int positiveOffset) {
        this.positiveOffset = positiveOffset;
    }

    public void setNegativeOffset(int negativeOffset){
        this.negativeOffset = negativeOffset;
    }

    private boolean previousComboOnlyDiffersByLastNumber(){
        if(prevCombo[0] == NO_PREVIOUS_COMBO) //FIXME: faulty logic? never true?
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
        return combo[0] + "-" + combo[1] + "-" + combo[2];
    }

    int getCorrespondingDialNumber(int angle){
        if(angle < 0) {
            while(angle < 0){
                angle += 100;
            }
        }
        else{
            while(angle > 100){
                angle -= 100;
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
