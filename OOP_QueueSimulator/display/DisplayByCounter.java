package display;
import queuesimulator.*;
import java.util.concurrent.SynchronousQueue;

/**
 * Created with IntelliJ IDEA.
 * User: GP
 * Date: 16/09/13
 * Time: 8:04 AM
 */

/**
 *  Class       : DisplayByCounter
 *  Package     : display
 *  Class Description:
 *      DisplayByCounter is a thread that follows the state of one fixed counter of the QueueSimulatorObject it helps
 *      display. It uses a SynchronousQueue (pipe) to communicate with the other threads.
 *      Note: The SynchronousQueue pipe is a shared resource that is shared by all threads in the simulation.
 *
 *  Scope for Implementation and Modification:
 *      Not much scope for modification. But the good thing is, the User may not need to modify this class at all !
 */


public class DisplayByCounter extends Thread{
    QueueSimulatorObject q;
    int counterId;
    SynchronousQueue<String> pipe;

    //parametrized constructor
    public DisplayByCounter(QueueSimulatorObject q, int id, SynchronousQueue<String> pipe){
        this.q = q;
        counterId = id;
        this.pipe = pipe;
    }

    public void run(){
        System.out.println("Display by counter...counter number = " + Integer.toString(counterId+1));
        String log = null;
        String tmp = null;
        int reqId = 0;
        try {
            while(!this.isInterrupted()){
                //loop indefinitely till this thread is interrupted
                log = pipe.take();
                if(log.startsWith("counter:") ){
                    // This is a counter type alert. Maybe it is important.
                    // Read from pipe again to get the counter index.
                    tmp = pipe.take();
                    while(!Character.isDigit(tmp.charAt(0)))
                        //Maybe I just read some other alert...check if the string starts with a digit.
                        //If not, read again.
                        tmp = pipe.take();
                    reqId = Integer.parseInt(tmp);
                    if(reqId == counterId+1)
                        // This alert is for the required counter only. Print it!
                        System.out.println("Time = "+q.getTime()+" :Log from Counter : "+log);
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
