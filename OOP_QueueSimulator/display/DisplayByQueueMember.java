package display;
import queuesimulator.*;

import java.util.concurrent.SynchronousQueue;

/**
 * Created with IntelliJ IDEA.
 * User: GP
 * Date: 16/09/13
 * Time: 8:00 AM
 */

/**
 *  Class      : DisplayByQueueMember
 *  Package    : display
 *
 *  Class Description:
 *              DisplayByQueueMember is a thread that keeps track of one fixed request in the QueueSimulatorObject
 *              it helps simulate. As ususal, it uses a shared resource 'pipe' to communicate with the other threads
 *
 *  Scope for Implementation and Modification:
 *              Not much scope for modification, but why this kolaveri?
 *              Implementation is peace max only: just import the class and use it.
 *
 */

public class DisplayByQueueMember extends Thread{
    QueueSimulatorObject q;
    int custId;
    SynchronousQueue<String> pipe;

    // Parametrized constructor
    DisplayByQueueMember(QueueSimulatorObject q1, int id, SynchronousQueue<String> p){
        q = q1;
        custId = id;
        pipe = p;
    }

    public void run(){
        System.out.println("Display by customer...customer number = " + custId);
        String log = null;
        String tmp = null;
        int reqId = 0;
        try {
            while(!this.isInterrupted()) {
                // read log from the pipe
                log = pipe.take();
                if(log.startsWith("qmem:") ){
                    // This is an alert about QueueMember. Maybe it is important. Read from pipe again to get the
                    // custId of the QueueMember
                    tmp = pipe.take();

                    // Maybe I got some other thread's alert. Check for a digit at the first location.
                    // Otherwise, try to read again.
                    while(!Character.isDigit(tmp.charAt(0)))
                        tmp = pipe.take();

                    reqId = Integer.parseInt(tmp);
                    if(reqId == custId)
                        // This is important. Display it.
                        System.out.println("Time = "+q.getTime()+" :Log from Customer : "+log);
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
