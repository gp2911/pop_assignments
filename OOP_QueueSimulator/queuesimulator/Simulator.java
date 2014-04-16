package queuesimulator;

import display.*;
import queuesimulator.*;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: GP
 * Date: 07/09/13
 * Time: 9:39 AM
 */

/**
 *  Class       : Simulator
 *  Package     : queuesimulator
 *
 *  Class Description:
 *             Simulator is the main thread which the User will create to simulate his custom QueueSimulator.
 *             It is a very simple thread: it just asks a QueuePopulater, a QueueController, an ExitQueueHandler,
 *             a Timer and a Display to start and then recedes into the background.
 *
 *  Scope for Implementation and Modification:
 *              User just needs to extend this class and use it.
 *              To start the simulation, you need to pass the class as an argument to a Simulator thread and start it.
 *              There is some scope for modification. Like if you want to add more threads to your working set, you can
 *              simply add them as members and start them in run().
 */


public class Simulator extends Thread{
    private QueueSimulatorObject q;
    private QueuePopulater populater;
    private QueueController controller;
    private ExitQueueHandler exitGuard;
    private Timer timer;
    public void run(){
        System.out.println("Simulating queue...");
        // Start off all the threads
        populater.start();
        controller.start();
        exitGuard.start();

        // reset the timer, just in case
        timer.resetTime();

        // schedule timer to execute every one second
        ScheduledExecutorService timerSchedule = Executors.newScheduledThreadPool(1);
        final ScheduledFuture<?> timerHandle;
        timerHandle = timerSchedule.scheduleAtFixedRate(timer, 1,1, TimeUnit.SECONDS);

        // create a new Display class to handle display
        q.disp = new Display(q, q.displayChoice);


    }

    // parametrized constructor
    public Simulator( QueueSimulatorObject q1){
        q=q1;
        populater = new QueuePopulater(q);
        controller = new QueueController(q);
        exitGuard = new ExitQueueHandler(q);
        timer = new Timer(q);
    }

}
