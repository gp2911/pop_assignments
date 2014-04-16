package queuesimulator;

import java.lang.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: GP
 * Date: 07/09/13
 * Time: 9:51 AM
 */

/**
 *  Class       : QueuePopulater
 *  Package     : queuesimulator
 *
 *  Class Description:
 *              QueuePopulater is a thread that populates the entry queue at a user-defined time interval.
 *             When pushing in a QueueMember, it rises alerts of type 'entry' and 'qmem'.
 *
 *  Scope for Implementation and Modification:
 *              A QueuePopulater object can only exist tied to a QueueSimulatorObject. Alone, it is meaningless
 *              and loses functionality.
 *              There is little scope for modification. This is mostly a problem-specific class.
 */

public class QueuePopulater extends Thread{

    private QueueSimulatorObject q;
    public void run(){
        System.out.println("Populater ready...");
        for (int idx = 1; q.isActive == true ; ++idx){
            // generate a random number using user-defined random number generator
            int randomInt = q.queueRandomGenerate();

            // create a new QueueMember using the randNum generated
            QueueMember newMember = new QueueMember(randomInt, idx);

            // add it into entry queue and write out a host of alerts into the pipe
            try{
                q.entry.add(newMember);
                q.pipe.put("entry: Cust No. : "+ newMember.custNo + " Number added : "+ newMember.time
                        + " added to entry queue");
                q.pipe.put("qmem: Customer "+ newMember.custNo+ " came into entry queue. Time attribute = "+ newMember.time);
                q.pipe.put(Integer.toString(newMember.custNo));
            }
            catch (Exception e){
                System.out.println("Error adding to entry queue...: "+ e.toString());
            }

            //sleep for some time
            try{

                sleep ((long) Math.floor(1000*q.populationSleepTime));
            }
            catch (Exception e){
                System.out.println("Sleep Interrupted : "+ e.toString());
            }
        }

    }
    public QueuePopulater(QueueSimulatorObject q1){
        q=q1;
    }
}
