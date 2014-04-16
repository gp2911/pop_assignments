package display;
import queuesimulator.*;

import java.lang.*;
import java.util.*;
import java.util.concurrent.SynchronousQueue;


/**
 * Created with IntelliJ IDEA.
 * User: GP
 * Date: 16/09/13
 * Time: 8:01 AM
 */

/**
 *  Class      : DisplayByExitQueue
 *  Package    : display
 *
 *  Class Description:
 *              DisplayByExitQueue is a thread that keeps track of changes in the exit queue of the QueueSimulatorObject
 *              it helps simulate. It uses a shared resource 'pipe' to communicate with the other threads
 *
 *  Scope for Implementation and Modification:
 *              Why would you want to modify this? :P
 *              Implementation is easy: just import the class and use it.
 *
 */


public class DisplayByExitQueue extends Thread {
    QueueSimulatorObject q;
    SynchronousQueue<String> pipe;

    //parametrized constructor
    public DisplayByExitQueue(QueueSimulatorObject queueSimulatorObject, SynchronousQueue<String> pipe){
        q=queueSimulatorObject;
        this.pipe = pipe;
    }
    public void run(){
        System.out.println("Display by exit queue...");
        String log = null;
        try {
            while(!this.isInterrupted()) {
                // read log from pipe
                log = pipe.take();

                //if alert type is exit, print it
                if(log.startsWith("exit:"))
                    System.out.println("Time = "+q.getTime()+" :Log from Exit Queue : "+log);

                // exit when exit queue is empty
                QueueMember next=q.exit.peek();
                if(next==null && q.isExitStarted() && q.isActive() == false){
                    System.out.println("Exit Queue empty");
                    System.exit(0);
                }
            }
        } catch (InterruptedException e) {
            if(q.isActive()){
                System.out.println("Sync error in consumer wait : "+ e.toString());
                e.printStackTrace();
            }
        }
    }
}
