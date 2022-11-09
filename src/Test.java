import com.fazecast.jSerialComm.*;

import java.util.concurrent.TimeUnit;

public class Test {
    private int[] prevCombo = {1,2,3,4,2,6,7,9};
    private int[] combo = {1, 2, 3, 4, 5, 6, 7, 8};

    String getMessageCodeNumber(String message){
        //System.out.println("getMessageCodeNumber:" + message);
        if(message == null)
            return null;
        message = message.trim(); //removes leading and trailing whitespace
        int index = message.indexOf(':');

        if (index <= -1) //message is only one word //FIXME: changed this to <=
            return message;
        return message.substring(0, index).trim(); //returns the first word without whitespace
    }

    Test() {

    }


    public static void main(String[] args) throws InterruptedException {
        Test t = new Test();

        System.out.println(t.getMessageCodeNumber("000"));

    }
}
