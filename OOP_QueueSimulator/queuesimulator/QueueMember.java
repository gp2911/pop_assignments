package queuesimulator;

/**
 * Created with IntelliJ IDEA.
 * User: GP
 * Date: 07/09/13
 * Time: 8:31 AM
 *
 */


/**
 *  Class       : QueueMember
 *  Package     : queuesimulator
 *
 * Class Description:
 *              The QueueCounter models the members (requests/customers) of the entry and exit queues used in
 *              the QueueSimulatorObject.
 *
 * Scope for Modification and Implementation:
 *              There is a huge scope for modification. If you want to add extra attributes (like name,
 *              let us say), then you can just extend this class and add it. No major changes are required.
 *              Implementation is also simple. This class can be easily adapted to work with any other object apart from
 *              QueueSimulatorObject. However, it is worth remembering that is a trivial, problem-generic class. You may
 *              want a QueueMember more suited to your specific problem.
 *
 */


public class QueueMember {
    int time;
    int items;
    int custNo;
    public QueueMember(int n, int c){
        time = n;
        custNo = c;
    }
}

