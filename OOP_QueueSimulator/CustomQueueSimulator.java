/**
 * Created with IntelliJ IDEA.
 * User: GP
 * Date: 23/09/13
 * Time: 9:55 PM
 */

import  queuesimulator.*;

import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 *
 *  Class       : CustomQueueSimulator
 *  Package     : default
 *
 *  Class Description: My custom queue simulator obtained by extending QueueSimulatorObject (and overriding
 *   queueRandomGenerate() if required) .
 */
public class CustomQueueSimulator extends QueueSimulatorObject{

    //just a small parametrized constructor to construct the superclass :P
    public  CustomQueueSimulator( Queue<QueueMember> q1, Queue<QueueMember> q2, List<QueueCounter> cl, int disp){
        super(q1, q2, cl, disp);
    }


//    @Override
//    public int queueRandomGenerate(){
//        Random random = new Random();
//        return random.nextInt(10);
//    }
}
