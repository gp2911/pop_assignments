package queuesimulator;

import java.util.concurrent.SynchronousQueue;

/**
 * Created with IntelliJ IDEA.
 * User: GP
 * Date: 07/09/13
 * Time: 8:39 AM
 */

/**
 *  Class       : QueueController
 *  Package     : queuesimulator
 *
 *  Class Description:
 *              QueueController is a thread that handles assignment of QueueMembers to  counters. Counters are assigned
 *              in increasing order of index, irrespective of whether they have sufficient stock or not.
 *
 *  Scope for Implentation and Modification:
 *              An QueueController object can only exist tied to a QueueSimulatorObject. Alone, it is meaningless
 *              and loses functionality.
 *              There is some scope for modification. For instance, you may want to send the member only to a counter
 *              that has sufficient stock n all. This can be done without majorly changing the code.
 */
public class QueueController extends Thread {
    private QueueSimulatorObject q;
    public void run(){
        System.out.println("Controller ready...");
        boolean allBusy = false;                       // are all the counters busy?
        QueueMember member = null;
        while (q.isActive ){
            // loop as long as queue simulator is active

            //if entry queue is empty, have patience. Wait for some more time.
            if (q.entry.isEmpty())
                continue;
            try{
                // if there is some free counter, fetch a request from head of entry queue
                if(!allBusy){
                    member = q.entry.remove();
                }

                int numberBusy=0;
                int numberClosed = 0;

                // scan the counterList to get a viable counter
                for (int i=0; i < q.numberOfCounters; i++){
                    // get the ith counter and see if it is viable
                    QueueCounter counter = q.counterList.get(i);

                    if(counter.open && counter.busy == false){
                        // if it is viable, ask it to serve member
                        allBusy=false;
                        counter.service(member);
                        counter.busy=true;                 // counter is now busy!

                        // write out alerts to the pipe
                        try {
                        q.pipe.put("entry: Customer "+ member.custNo + " has left the entry Queue");
                        q.pipe.put("counter: "+ counter.index+" : Counter has accepted Customer "+ member.custNo + " with Time attribute = " +
                                member.time);
                        q.pipe.put(Integer.toString(counter.index));
                        q.pipe.put("qmem: Customer "+member.custNo+" has left entry queue and reached counter "+ counter.index
                                                + " with stock "+ counter.stock);
                        q.pipe.put(Integer.toString(member.custNo));
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        break;
                    }

                    if(counter.open == false){
                        // if counter is closed, increment numberClosed
                        numberClosed++;
                        if(numberClosed == q.numberOfCounters){
                            // if all counters are closed, deactivate the simulation
                            q.isActive = false;
                            break;
                        }
                    }
                    else if (counter.busy == true){
                        // if counter is busy but not closed, increment numberBusy
                        numberBusy++;
                        if(numberBusy + numberClosed==q.numberOfCounters){
                            // no counter is free...hold on for some more time
                            allBusy = true;
                            continue;
                        }
                    }

                }

            }
            catch (Exception e){
                System.out.println("Error removing from entry queue...: "+ e.toString());
                e.printStackTrace();
            }

        }

        System.out.println("All counters out of stock. Queue no longer active...");
    }
    public QueueController(QueueSimulatorObject q1){
        q=q1;
    }

}