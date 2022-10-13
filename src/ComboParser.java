public class ComboParser {
    public static final int COMBO_FINISHED = -999;
    public static final int NO_PREVIOUS_COMBO = -998;
    public static final int COUNTER_CLOCKWISE = -1;
    public static final int CLOCKWISE = 1;
    public static final int DEGREES_TO_UNLOCK = 300; //FIXME: this will need to be fine-tuned
    private final int degreesPerTick;

    private final int size;
    private final Dialer dialer;

    private int[] combo;
    private int[] prevCombo = {0, 0, 0};
    private int index;
    private int currPosInDegrees;
    private boolean tryToOpen;

    ComboParser(Dialer dialer){
        this.dialer = dialer;
        this.combo = dialer.getNextCombination();
        this.index = 0;
        this.size = combo.length;
        this.degreesPerTick = 360 / dialer.getTickCount();
        this.currPosInDegrees = 0;
        this.tryToOpen = false;
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
        String command = "TurnDial:";
        int degreesToTurn = 0;

        if(tryToOpen){
            degreesToTurn += DEGREES_TO_UNLOCK;
            this.index = 0;
            tryToOpen = false;
            prevCombo = this.combo.clone();
            this.combo = dialer.getNextCombination();
        }
        else {
            switch (index) {
                case 0:
                    if (previousComboOnlyDiffersByLastNumber()) {
                        degreesToTurn -= DEGREES_TO_UNLOCK;
                        degreesToTurn -= combo[2] - prevCombo[2];
                        tryToOpen = true;
                        //keep index at 0;
                    }
                    else{
                        degreesToTurn -= 360 - getDegreesFromNumber(currPosInDegrees);
                        degreesToTurn -= 360 * 3;
                        degreesToTurn -= getDegreesFromNumber(combo[0]);
                        this.index = 1;
                    }
                    break;
                case 1:
                    degreesToTurn += 360 * 2;
                    if(combo[0] > combo[1] ){
                        degreesToTurn += getDegreesFromNumber(combo[0] - combo[1]);
                    }
                    else{
                        degreesToTurn += 360 - getDegreesFromNumber(combo[1] - combo[0]);
                    }
                    this.index = 2;
                    break;
                case 2:
                    degreesToTurn -= 360;
                    if(combo[1] > combo[2]){
                        degreesToTurn -= 360 - getDegreesFromNumber(combo[1] - combo[2]);
                    }
                    else{
                        degreesToTurn -= getDegreesFromNumber(combo[2] - combo[1]);
                    }
                    tryToOpen = true;
                    //index gets set back to 0 in the tryToOpen block
                    break;
                default:
                    System.out.println("Error in index switch");
                    break;
            }
        }
        currPosInDegrees += degreesToTurn;
        currPosInDegrees = getEquivalentAngle(currPosInDegrees);
        command += Integer.toString(degreesToTurn);
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

    int getDegreesFromNumber(int ticks){
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
}
