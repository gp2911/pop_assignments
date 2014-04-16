import queuesimulator.*;
import java.io.InputStreamReader;
import java.lang.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.io.*;
/**
 * Created with IntelliJ IDEA.
 * User: GP
 * Date: 07/09/13
 * Time: 8:41 AM
 *
 */

/**
 *
 *  Class       : QueueSimulator
 *  Package     : default
 *
 *  Main class.
 *  End user who is going to use my package must write a similar class. But nothing special... it just
 *  initialises the CustomQueueSimulator object and starts it off.
 */
public class QueueSimulator {



    public static void main( String args[] ){
        int numOfCounters=0;
        boolean repeat = false;                                 // should I display the menu again?
        int reqId = 0;
        Queue<QueueMember> entryList= new LinkedList<QueueMember>();      // creating entry and
        Queue<QueueMember> exitList = new LinkedList<QueueMember>();      // exit queues
        BufferedReader inputBuffer = new BufferedReader( new InputStreamReader( System.in));
        System.out.print("Enter number of counters:   ");       // prompt user to enter number of counters
        try{
            numOfCounters = Integer.parseInt(inputBuffer.readLine());
        }
        catch ( Exception e ){
            System.out.println("I/O exception... "+ e.toString());
            e.printStackTrace();
        }
        List<QueueCounter> queueCounters = new ArrayList<QueueCounter>(numOfCounters);
        System.out.print("Initialise all counters?   ");        // user initialised counters or default?
        char choice = 'x';
        do{
            repeat = false;
            try{
                choice = inputBuffer.readLine().charAt(0);
            }
            catch (Exception e){
                System.out.println( e.toString());
                e.printStackTrace();
            }
            if(choice == 'y'){

                // if user wants to initialize counters
                for (int i=1; i <= numOfCounters; i++){
                    System.out.print("Stock to be filled in counter "+ i +" ?       ");
                    int stock = 0;
                    try{
                        stock = Integer.parseInt(inputBuffer.readLine());  //accept user' value for counter's stock
                    }
                    catch (Exception e){
                        System.out.println(e.toString());
                        e.printStackTrace();
                    }
                    QueueCounter counter = new QueueCounter(stock, i);     //create a new counter with stock number if items
                    queueCounters.add(counter);
                }
            }

            else if(choice == 'n'){
                // if user wants default values (default stock = 20)
                for (int i=1; i <= numOfCounters; i++){
                    QueueCounter counter = new QueueCounter(20, i);         //create a new counter with 20 items
                    queueCounters.add(counter);
                }
            }
            else {
                System.out.println("Enter y or n only...");
                repeat = true;
            }
        } while (repeat == true);

        // Menu for user's choice of display
        System.out.println("Choice of display?  ");
        System.out.println("1. Display by Request");
        System.out.println("2. Display by Counter");
        System.out.println("3. Display by Entry Queue");
        System.out.println("4. Display by Exit Queue");

        int dispChoice = 0;

        do{
            repeat = false;
            try{
                dispChoice = Integer.parseInt(inputBuffer.readLine());
            }
            catch (Exception e){
                System.out.println( e.toString());
                e.printStackTrace();
            }
            if(dispChoice < 1 || dispChoice > 4){
                System.out.println("Enter 1, 2, 3 or 4 only");
                repeat = true;
            }
        } while (repeat == true);

        //create a QueueSimulatorObject as per user's requirenments and simulate it
        QueueSimulatorObject q = new CustomQueueSimulator(entryList, exitList, queueCounters, dispChoice-1);
        Simulator s = new Simulator(q);
        s.start();

    }

}
