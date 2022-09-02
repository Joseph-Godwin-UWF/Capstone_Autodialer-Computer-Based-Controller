public class Test {
    public static void main(String args[]){
        Dialer dialer = new Dialer(3, 50);
        int combo[];
        do{
            combo = dialer.getNextCombination(3);
            for(int i = 0; i < combo.length; i++){
                System.out.print(combo[i] + " ");
            }
            System.out.println();
        }while(combo[0] < 2);
    }
}
