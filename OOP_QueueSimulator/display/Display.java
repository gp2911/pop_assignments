package display;
import queuesimulator.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.SynchronousQueue;

/**
 * Created with IntelliJ IDEA.
 * User: GP
 * Date: 16/09/13
 * Time: 8:04 AM
 */

/**
 * Class Name   : Display
 * Package      : display
 *
 * Class Description:
 *              The Display class handles displaying the queue stats. Its constructor accepts a QueueSimulatorObject
 * ( the one that is being simulated ) and an integer, that represents the users choice of display. According to the
 * value of choice, a corresponding Display thread is started.
 *
 *                                  choice = 0 : DisplayByQueueMember()
 *                                  choice = 1 : DisplayByCounter()
 *                                  choice = 2 : DisplayByEntryQueue()
 *                                  choice = 3 : DisplayByExitQueue()
 *
 *               After starting off the display thread, the class gets into an indefinite while loop that keeps checking
 * if the QueueSimulatorObject is 'active'. The moment the QueueSimulatorObject becomes inactive, the class interrupts
 * its Display thread.
 *
 *  Scope for Implementation and  Modification:
 *              If User feels like adding a new kind of Display thread, he/she can simply write the new thread, add it
 *  to the queuesimulator package and add it to the switch()'s set of cases. The rest of the class can remain unchanged.
 *
 *
 */


public class Display {
    // dispThread is the currently executing display thread

    Thread dispThread;

    public Display( QueueSimulatorObject q, int choice){
        boolean repeat = false;     // should I display the menu again?

        // Pipe for communication
        SynchronousQueue <String> pipe = q.getPipe();

        do{
            // input reader
            BufferedReader inputBuffer = new BufferedReader( new InputStreamReader( System.in));

            switch (choice){

                case 0:{
                    // DisplayByQueueMember
                    System.out.println("Enter customer number to follow : ");
                    DisplayByQueueMember disp = null;

                    try {
                        int reqId = Integer.parseInt(inputBuffer.readLine());
                        disp = new DisplayByQueueMember(q, reqId, pipe);

                        // current display thread = disp
                        dispThread = disp;
                        disp.start();

                    } catch (IOException e) {
                        System.out.println("IO error : " + e.toString());
                        e.printStackTrace();
                    }

                    // if simulator becomes inactive, interrupt the display thread
                    while (true){
                        if(q.isActive() == false )    {
                            disp.interrupt();
                            break;
                        }
                    }
                    repeat = false;
                    break;
                }

                case 1:{
                    //DisplayByCounter
                    System.out.println("Enter counter number to follow : ");
                    DisplayByCounter disp = null;
                    try {
                        int reqId = Integer.parseInt(inputBuffer.readLine());
                        disp = new DisplayByCounter(q, reqId-1, pipe);

                        //  current display thread = disp
                        dispThread = disp;
                        disp.start();
                    }
                    catch (IOException e) {
                        System.out.println("IO error : " + e.toString());
                        e.printStackTrace();
                    }
                    repeat = false;


                    // if simulator becomes inactive, interrupt the display thread
                    while (true){
                        if(q.isActive() == false )    {
                            disp.interrupt();
                            break;
                        }
                    }
                    break;
                }
                case 2:{
                    DisplayByEntryQueue disp = new DisplayByEntryQueue(q, pipe);

                    //  current display thread = disp
                    dispThread = disp;
                    disp.start();

                    repeat = false;


                    // if simulator becomes inactive, interrupt the display thread
                    while (true){
                        if(q.isActive() == false )    {
                            disp.interrupt();
                            break;
                        }
                    }
                    break;
                }
                case 3:{
                    DisplayByExitQueue disp = new DisplayByExitQueue(q, pipe);


                    //  current display thread = disp
                    dispThread = disp;
                    disp.start();

                    repeat = false;


                    // if simulator becomes inactive, interrupt the display thread
                    while (true){
                        if(q.isActive() == false && q.exit.isEmpty() && q.isExitStarted())    {
                            disp.interrupt();
                            break;
                        }
                    }
                    break;
                }
                default:
                    System.out.println("Enter 0,1,2 or 3 only...");
                    repeat = true;
            }
        } while ( repeat == true);

    }

}
