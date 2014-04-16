package display;
import queuesimulator.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.SynchronousQueue;

/**
 * Created with IntelliJ IDEA.
 * User: GP
 * Date: 16/09/13
 * Time: 8:01 AM
 */

/**
 *  Class      : DisplayByEntryQueue
 *  Package    : display
 *
 *  Class Description:
 *              DisplayByEntryQueue is a thread that keeps track of changes in the entry queue of the QueueSimulatorObject
 *              it helps simulate. It uses a shared resource 'pipe' to communicate with the other threads
 *
 *  Scope for Implementation and Modification:
 *              Why would you want to modify this? :P
 *              Implementation is easy: just import the class and use it.
 *
 */


public class DisplayByEntryQueue extends Thread {
    QueueSimulatorObject q;
    SynchronousQueue<String> pipe;

    //paremetrized constructor
    public DisplayByEntryQueue (QueueSimulatorObject q1, SynchronousQueue<String> p){
        pipe = p;
        q=q1;
    }


    public void run(){
        System.out.println("Display by entry queue...");
        String log = null;


        try {
            while(!this.isInterrupted()) {
                // Read log from the pipe
                log = pipe.take();

                if(log.startsWith("entry:"))
                    // This is an entry type alert. Print it.
                    System.out.println("Time = "+q.getTime()+" :Log from Entry Queue : "+log);

                // Get the head of the exit queue using peek()
                QueueMember next=q.exit.peek();
                if(next==null && q.isExitStarted()){
                    System.out.println("Exit Queue empty");
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            System.out.println("Display interrupted... q.isActive = "+ q.isActive());
            if(q.isActive()){
                System.out.println("Sync error in consumer wait : "+ e.toString());
                e.printStackTrace();
            }
            else
                System.exit(0);
        }

    }
}
