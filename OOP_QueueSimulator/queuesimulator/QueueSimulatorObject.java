package queuesimulator;
import display.*;

import java.util.*;
import java.io.*;
import java.util.concurrent.SynchronousQueue;


/**
 * Created with IntelliJ IDEA.
 * User: GP
 * Date: 07/09/13
 * Time: 8:49 AM
 */

/**
 *  Class       : QueueSimulatorObject
 *  Package     : queuesimulator
 *
 *  Class Description:
 *             QueueSimulatorObject is the main class which the User will extend to create his custom QueueSimulator.
 *             It has 2 Queues, entry and exit, and a list of counters, which can be dynamically initialised at run time
 *             if required. It also contains the Display object used to display its simulation.
 *
 *  Scope for Implementation and Modification:
 *              User just needs to extend this class, over-ride queueRandomGenerate (if required) and use it.
 *              To start the simulation, you need to pass the class as an argument to a Simulator thread and start it.
 *              This class is NOT meant to be modified.
 */

public class QueueSimulatorObject {

    // the major components - entry queue, exit queue and counter list
    public Queue<QueueMember> entry;
    public Queue<QueueMember> exit;
    public List<QueueCounter> counterList;

    // pipe is used for inter-thread communication
    SynchronousQueue<String> pipe;

    // other data members
    int numberOfCounters;
    boolean isActive;
    boolean exitStarted;
    float populationSleepTime;
    int displayChoice;
    int time;

    // Display class for displaying
    Display disp;

    // parametrized constructor
    public QueueSimulatorObject ( Queue<QueueMember> q1, Queue<QueueMember> q2, List<QueueCounter> cl, int disp){
        entry = q1;
        exit = q2;
        counterList = cl;
        numberOfCounters = counterList.size();
//        System.out.println(numberOfCounters);
        for(int i=0; i< numberOfCounters; i++){
            counterList.get(i).queue = this;
        }
        isActive = true;
        exitStarted = false;
        displayChoice = disp;
        time = 0;
        this.disp = null;
        pipe = new SynchronousQueue<String>();
        System.out.println("Population sleep time? ");
        BufferedReader inputBuffer = new BufferedReader( new InputStreamReader( System.in));
        try{
            populationSleepTime = Float.parseFloat(inputBuffer.readLine());
        }
        catch (Exception e){
            System.out.println("I/O Error :" + e.toString());
            e.printStackTrace();
        }
    }

    // default constructor
    public  QueueSimulatorObject (){
        entry = null;
        exit = null;
        counterList = null;
        numberOfCounters = 0;
        isActive = true;
        exitStarted = false;
        populationSleepTime = 0;
        displayChoice = 0;
        time = 0;
        pipe = null;
        disp = null;
    }

    // random number generator
    public int queueRandomGenerate(){
        Random random = new Random();
        return random.nextInt(10);
    }

    // accessor functions
    public boolean isActive(){
        return isActive;
    }

    public int getTime(){
        return time;
    }

    public SynchronousQueue<String> getPipe(){
        return pipe;
    }

    public boolean isExitStarted(){
        return exitStarted;
    }

}
