package queuesimulator;

/**
 * Created with IntelliJ IDEA.
 * User: GP
 * Date: 07/09/13
 * Time: 9:52 AM
 */

/**
 *  Class       : ExitQueueHandler
 *  Package     : queuesimulator
 *
 *  Class Description:
 *              ExitQueueHandler is a thread that handles the exit queue of the QueueSimulatorObject.
 *              It takes care of holding the head of the exit queue for time equal to its 'items' attribute, and then
 *              pushing it out of the queue. When pushing out a QueueMember, it rises alerts of type 'exit' and 'qmem'.
 *
 *  Scope for Implentation and Modification:
 *              An ExitQueueHandler object can exist only tied to a QueueSimulatorObject. Alone, it is meaningless
 *              and loses functionality.
 *              There is some scope for modification. The variable 'member'stores the head of the exit queue before it
 *              lost forever. So if you want to do something with it before pushing it out, you can do so, making only
 *              minor modifications to the code.
 */

public class ExitQueueHandler extends Thread {
    private QueueSimulatorObject q;
    public void run(){
        System.out.println("ExitQueueHandler started...");

        //loop as long as simulation is active OR the exit queue is not empty
        while ( q.isActive || !q.exit.isEmpty() ){
            // if exit queue is empty, just wait for someone to come
            if(q.exit.isEmpty())
                continue;

            // get the guy at the head of the exit queue
            QueueMember member = q.exit.peek();
            try{
                sleep( member.items * 1000);
            }
            catch ( Exception e){
                System.out.println(e.toString());
                e.printStackTrace();
            }

            // remove him...
            q.exit.remove();

            // write out alerts to the pipe
            try {
                q.pipe.put("exit: Customer " + member.custNo + " with " + member.items + " items has left " +
                        "the exit queue...");
                q.pipe.put("qmem: Customer " + member.custNo + " has left the exit queue");
                q.pipe.put(Integer.toString(member.custNo));
            } catch (InterruptedException e) {
                System.out.println("Interrupt in producer wait : "+ e.toString());
                e.printStackTrace();
            }

        }
        System.out.println("Exit Q Handler ended");

        //exit
        System.exit(0);
    }
    public ExitQueueHandler( QueueSimulatorObject q1){
        q=q1;
    }
}
