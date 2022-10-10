public class ComboParser {
    public static final int COMBO_FINISHED = -999;
    public static final int NO_PREVIOUS_COMBO = -998;
    public static final int COUNTER_CLOCKWISE = -1;
    public static final int CLOCKWISE = 1;

    private final int size;
    private final Dialer dialer;

    private int[] combo;
    private int[] prevCombo = {NO_PREVIOUS_COMBO};
    private int index;

    ComboParser(Dialer dialer){
        this.dialer = dialer;
        this.combo = dialer.getNextCombination();
        this.index = 0;
        this.size = combo.length;
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

        if(previousComboOnlyDiffersByLastNumber()){
            //FIXME: do rotate counterclockwise UNLOCKING_DEGREES + (lastNum - prevLastNum)
            //FIXME: getNextCombo
        }
        else{
            if(getNextNumber() != COMBO_FINISHED) {
                switch (index) {
                    case 0:
                        //FIXME:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }
            }
            else{
                //FIXME: getNextCombo
            }
            //FIXME: check index of current number. do appropriate action
        }
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
}
