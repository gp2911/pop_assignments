package queuesimulator;

/**
 * Created with IntelliJ IDEA.
 * User: GP
 * Date: 07/09/13
 * Time: 6:06 PM
 */

/**
 *  Class       : QueueServiceManager
 *  Package     : queuesimulator
 *
 *  Class Description:
 *             QueueServiceManager is a thread that oversees the interaction between a QueueCounter and a QueueMember.
 *             When assigning a QueueMember form the head of the entry queue to a counter, it raises alerts of type
 *             'qmem' and 'counter'.
 *             Once the service is over, it checks if the request was served 'completely' or not before pushing the
 *             QueueMember to the exit queue. A 'qmem' alert is given to communincate the completeness of the
 *             transaction. When pushing a QueueMember into exit queue, alerts of type 'qmem' and 'exit' are
 *             raised. Similar alerts of corresponding types are raised at other critical events.
 *
 *  Scope for Implementation and Modification:
 *              A QueueServiceManager object can only exist tied to a QueueSimulatorObject. Alone, it is meaningless
 *              and loses functionality.
 *              There is some scope for modification. Though this is mostly a problem-specific class, there is still
 *              some flexibility as to 'how' the transaction is going to be carried out.
 */

public class QueueServiceManager extends Thread {
    private QueueCounter counter;
    private QueueMember member;
    private QueueSimulatorObject queue;

    // Parametrized consructor
    public QueueServiceManager ( QueueCounter c, QueueMember m, QueueSimulatorObject q){
        counter = c;
        member = m;
        queue = q;
    }
    public void run(){
        // write out alerts into the pipe
        try{
            queue.pipe.put("qmem: Transaction successful for customer "+ member.custNo);
            queue.pipe.put(Integer.toString(member.custNo));
            queue.pipe.put("counter: Counter serving customer "+ member.custNo +" for "+member.time+" seconds. ");
            queue.pipe.put(Integer.toString(counter.index));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        // counter is now busy
        counter.busy=true;
        try{
            sleep(1000 * member.time);
        }
        catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
        }

        // if counter has sufficient stock
        if(counter.stock >= member.time) {
            // subtract member's time from stock
            counter.stock = counter.stock - member.time;
            member.items = member.time;
            member.time=0;
            queue.exit.add(member);

            // write out alerts into pipe after adding member to the exit queue
            try {
                queue.pipe.put("exit: Customer " + member.custNo + " with " + member.items + " items has joined exit queue");
                queue.pipe.put("qmem: Customer " + member.custNo + " has joined exit queue. Items = " + member.items);
                queue.pipe.put(Integer.toString(member.custNo));
                queue.exitStarted=true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try{
                queue.pipe.put("qmem: Transaction complete for customer "+ member.custNo + " at counter "+ counter.index+" !!");
                queue.pipe.put(Integer.toString(member.custNo));
            }
            catch (Exception e){
                e.printStackTrace();
            }

            // if counter becomes empty, then close it
            if(counter.stock==0){
                counter.open=false;
                try{
                    queue.pipe.put("counter: Counter " + counter.index + 1 + " out of stock! Closing down now...");
                    queue.pipe.put(Integer.toString(counter.index));
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        else{
            // if counter does NOT have sufficient stock
            member.items = counter.stock;
            counter.stock = 0;
            counter.open = false;
            member.time = 0;
            queue.exit.add(member);

            // write out a host of alerts on adding the member to the exit queue
            try {
                queue.pipe.put("exit: Customer " + member.custNo + " with " + member.items + " items has joined exit queue");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try{
                queue.pipe.put("qmem: Transaction incomplete for customer "+ member.custNo + " at counter "+ counter.index+" !!");
                queue.pipe.put(Integer.toString(member.custNo));
            }
            catch (Exception e){
                e.printStackTrace();
            }

            try{
                queue.pipe.put("counter: Counter "+ Integer.toString(counter.index) + " out of stock! Closing down now...");
                queue.pipe.put(Integer.toString(counter.index));
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }

        // counter is now free
        counter.busy = false;
        if(counter.open==true){
            //if counter is still open, write out a host of alerts
            try{
                queue.pipe.put("counter: "+ counter.index + " : Counter free !");
                queue.pipe.put(Integer.toString(counter.index));
                queue.pipe.put("counter: "+ counter.index + " : Stock left at counter   =   "+ counter.stock);
                queue.pipe.put(Integer.toString(counter.index));
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}
