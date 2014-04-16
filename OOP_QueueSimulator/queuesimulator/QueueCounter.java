package queuesimulator;

import queuesimulator.*;
import java.lang.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: GP
 * Date: 07/09/13
 * Time: 8:27 AM
 */

/**
 *  Class       : QueueCounter
 *  Package     : queuesimulator
 *
 * Class Description:
 *              The QueueCounter models the counters used in the QueueSimulatorObject.
 *
 * Scope for Modification and Implementation:
 *              There is a huge scope for modification. If you want to add extra attributes (like the employee in charge,
 *              let us say), then you can just extend this class and add it. No major changes are required.
 *              The lone member function service() can be over-ridden easily if the User wants to change the way the
 *              QueueMembers are going to be serviced by the counter.
 *              Implementation is also simple. This class can be easily adapted to work with any other object apart from
 *              QueueSimulatorObject.
 *
 */
 public class QueueCounter {
    boolean open;
    boolean busy;
    int index;
    int stock;
    QueueSimulatorObject queue;


    public QueueCounter(int s, int i){
        busy = false;
        open = true;
        stock = s;
        index = i;
        queue = null;
    }


    void service(QueueMember member){
        // create a new QueueServiceManager and start() it
        QueueServiceManager serviceManager = new QueueServiceManager( this, member, this.queue);
        serviceManager.start();
    }
}
