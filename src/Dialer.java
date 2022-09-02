public class Dialer {

    /**  @tickCount   the number of ticks on the dia */
    int tickCount;
    /** @combination an array storing the current combination */
    int combination[];

    public Dialer(int comboSize, int tickCount){
        this.tickCount = tickCount;
        combination = new int[comboSize];
    }

    public int[] getNextCombination(){
        return getNextCombination(2);
    }
    public int[] getNextCombination(int delta){
        int lastIndex = combination.length - 1;
        for(int i = 0; i < combination.length; i++) {
            if (combination[lastIndex - i] < tickCount - (delta - 1)) {
                combination[lastIndex - i] += delta;
                break;
            }
            combination[lastIndex - i] = 0;
        }

        return combination;
    }
}


