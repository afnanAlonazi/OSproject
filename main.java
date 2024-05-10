package projectOS;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;



public class main {
	static PCB[] Q1  = new PCB[100];
	static PCB[] Q2 = new PCB[100];
	static PCB[] completedProcess=new PCB[100];
	static int completed =0;
	static  int currentTime = 0;
	static String  processOrder="[";
	static double avgTurnaroundTime=0;
	static double avgWaitingTime = 0;
	static double avgResponseTime = 0;
	
	public static void main(String[] args)  throws IOException {
	Scanner scanner = new Scanner(System.in);
	 
	      
	    int answer = 0;
        do {
        System.out.println("Chosse form the following menu:");
        System.out.println("1) Enter process’ information. ");
        System.out.println("2) Report detailed information about each process and different scheduling criteria. ");
        System.out.println("3) Exit the program.");

        answer = scanner.nextInt();

        switch (answer) {
    
        case 1:
    	    
    	    // Do-while to validate number of processes
        	 int  numProcesses=0;
    	    do {
    	        System.out.print("Enter the number of processes (P): ");
    	        while (!scanner.hasNextInt()){
    	            System.out.println("Invalid input. Please enter a positive integer.");
    	            scanner.next(); // Consume the invalid input
    	        }//End of while
    	        
    	        numProcesses = scanner.nextInt();
    	        if (numProcesses <= 0) {
    	            System.out.println("Invalid input. Please enter a positive integer.");
    	        }
    	    } while (numProcesses <= 0);
    	    
  	      System.out.println("1) Enter process’ information. ");
  	      for (int  i= 0; i < numProcesses; i++){
  	    	    int numberOfprocess=i+1;
                String pid = "P" + (i + 1);
                System.out.println("Enter "+pid+" information:");
                System.out.println("Enter priority for " + pid + " (1 or 2): ");
                int priority = scanner.nextInt();
                while(priority<1||priority>2) {
                	System.out.println("Invalid input. Please enter 1,2 ");
                	priority = scanner.nextInt();
                }
                System.out.println("Enter arrival time for " + pid + ": ");
                int arrivalTime = scanner.nextInt();
                System.out.print("Enter CPU burst for " + pid + ": ");
                int cpuBurst = scanner.nextInt();
                PCB process = new PCB(numberOfprocess,pid, priority, arrivalTime, cpuBurst);
                addProcess(process);
                }
    	    
  	      scheduleProcesses();
  	      excuteQ2() ;
  	      calculateAverages();
  	     // System.out.println( processOrder +"]");
  	    
  	      writeInFile();
            break;
        case 2:
        	 readFromFile();
           
            break;
        case 3:
            System.out.println("Exiting.. ");
            break;

        default:
            System.out.println("Invalid input, try again");
        }
    } while (answer != 3);
}

	      
	      public static void addProcess(PCB process) {
	    		
	    		if (process.getPriority() == 1) {
	    			// Find the first null element in Q1 and add the process there
	    			for (int i = 0; i < Q1.length; i++) {
	    				if (Q1[i] == null) {
	    					Q1[i] = process;
	    					
	    					return;
	    				}//End of if
	    			}//End of for
	    			
	    		} else if (process.getPriority() == 2) {
	    			// Find the first null element in Q2 and add the process there
	    			for (int i = 0; i < Q2.length; i++) {
	    				if (Q2[i] == null) {
	    					Q2[i] = process;
	    					return;
	    				}//End of if
	    			}//End of for
	    		}

	    	}//End of method addProcess	
	      
	      public static void scheduleProcesses() {

	    	    Arrays.sort(Q1, Comparator.nullsLast(Comparator.comparingInt(PCB::getArrivalTime)));
	    	  
	    	    int quantum = 3;
	    	    PCB deleted=null;
	    	    boolean isChanged = false;

	    	    int i = 0;
	    	    while (i < Q1.length && Q1[i] != null) {
	    	        if (currentTime >= Q1[i].getArrivalTime()) {
	    	            if (Q1[i].getRemainingTime() <= quantum) {
	    	            	if(Q1[i].getStartTime()==-1){//process not started before
	    	            		Q1[i].setStartTime(currentTime);}	
	    	                currentTime += Q1[i].getRemainingTime();
	    	                Q1[i].setTerminationTime(currentTime);
	    	                processOrder= processOrder+Q1[i].getProcessID()+"|";
	    	                Q1[i].setRemainingTime(0);
	    	              if(completed<100)
	    	            	   deleted= remove(Q1, i);
	    	            	     completedProcess[completed++]=deleted;// The process has been executed completely
	    	        
	    	                } //End of if2
	    	            
	    	            
	    	            else {
	    	            	deleted=null;
	    	            	Q1[i].setRemainingTime(Q1[i].getRemainingTime() - quantum);
	    	                if(Q1[i].getStartTime()==-1){//process not started before
	    	                	Q1[i].setStartTime(currentTime);}
	    	                currentTime += quantum;
	    	                processOrder= processOrder+Q1[i].getProcessID()+"|";  // Add the process to the scheduling order
	    	                if (i + 1 < Q1.length && Q1[i + 1] != null && Q1[i + 1].getArrivalTime() <= currentTime) {
	    	                	 isChanged= changeIndex(Q1, i, currentTime);
	    	                }
	    	               
	    	            }
	    	            // Check if the loop should reset the index
	    	            if(Q1.length!=i+1)	
	    	            if (Q1[i]!= null && Q1[i+ 1]== null ) {
	    	            	if (findFirstNotNullIndex(Q1)!=-1)
	    	            	 i = findFirstNotNullIndex(Q1);
	    	            }
	    	            else if(Q1[i]!= null&&currentTime >= Q1[i].getArrivalTime()&&currentTime < Q1[i + 1].getArrivalTime()) {
	    	            	if (findFirstNotNullIndex(Q1)!=-1)
		    	            	 i = findFirstNotNullIndex(Q1);
	    	            }
	    	            	else if (deleted==null&&isChanged==false)
	    	                	i++;
	    	            	
	    	            
	    	           
	    	        } else {
	    	            break;//no process arrive
	    	        }
	    	    }

//	    	    
//	    	    
	    	    
	    	}
	      public static int findFirstNotNullIndex(PCB[] array) {
	    	  int index=-1;
	          for (int i = 0; i < array.length; i++) {
	              if (array[i] != null) {
	            	  index=i;
	            	  break;   
	              }
	          }	
	          return index;
	      }
	      public static PCB remove(PCB[] array, int indexToRemove) {
	    	   if (indexToRemove < 0 || indexToRemove >= array.length) {
	    	        // Invalid index, return null
	    	        return null;
	    	    }

	    	    // Store the object to be deleted
	    	    PCB deletedObj = array[indexToRemove];

	    	    // Shift elements to the left starting from the index to remove
	    	    for (int i = indexToRemove; i < array.length - 1; i++) {
	    	        array[i] = array[i + 1];
	    	    }

	    	    // Set the last element to null
	    	    array[array.length - 1] = null;

	    	    // Return the deleted object
	    	    return deletedObj;
	    	}
	      
	    	public static boolean changeIndex(PCB[] array, int index, int currentTime) {
	    		boolean isChanged=false;
	    	    if (index < 0 || index >= array.length) {
	    	        // Invalid index, do nothing
	    	       }
	    	    
	    	    PCB target = array[index];
	    	    int firstGreaterIndex = -1;

	    	    // Find the index of the first element with a greater arrival time
	    	    for (int i = index + 1; i < array.length; i++) {
	    	    	if(array[i]!=null)
	    	        if (array[i].getArrivalTime() > currentTime) {
	    	            firstGreaterIndex = i;
	    	            isChanged=true;
	    	            break;
	    	        }
	    	    	
	    	    	
	    	    }

	    	    // If a greater index is found, shift elements to make space and insert the target
	    	    if (firstGreaterIndex != -1) {
	    	        for (int i = index; i < firstGreaterIndex; i++) {
	    	            array[i] = array[i + 1];
	    	        }
	    	        array[firstGreaterIndex - 1] = target;
	    	    }
	    	    return isChanged;
	    	}
	    	//////////////////////
	    	 public static void excuteQ2() {
	    		 
	    		 // Sort Q2 based on CPU burst time SJF
	    		 Arrays.sort(Q2, Comparator.nullsLast(Comparator.comparingInt(PCB::getCpuBurstTime)));
	    		 PCB deleted = null;

	    	        // Repeatedly iterate through Q2 until all processes are executed
	    	        while (true) {
	    	            boolean processExecuted = false; // Track if any process is executed in this iteration
	    	            for (int i = 0; i < Q2.length; i++) {
	    	                PCB process = Q2[i];
	    	                if (process != null && process.getArrivalTime() <= currentTime) {
	    	                    processExecuted = true; // A process is executed in this iteration
	    	                    excuteProcess(process); // Execute the process for its CPU burst time
	    	                    if(completed<100) {
	 	    	            	   deleted= remove(Q2, i);
	 	    	            	   completedProcess[completed++]=deleted;// The process has been executed completely
	    	                   break;
	    	                   }    
	 	    	        
	    	   
	    	                   
	    	                }else {
	    	                    if(processArriveAtQ1()){ //check q1 if there is arrival excute it and don't twice increment time
	    	                        processExecuted = true; // A process is executed in this iteration
	    	                    }
	    	                }
	    	            }
	    	            if (empty(Q2) && empty(Q1)) {
	    	                // All processes executed, exit the loop
	    	                break;
	    	            }
	    	            if (!processExecuted) {
	    	                // No process executed, increment currentTime
	    	                currentTime++;
	    	            }
	    	        }
	    	        
	    	    }
	    	    public static void excuteProcess(PCB process){
	    	    	process.setStartTime(currentTime);
	    	    	 boolean processArrive=false;
	    	         int cpuBurst = process.getCpuBurstTime();
	    	         processOrder= processOrder+process.getProcessID()+"|";
	    	        //for each ms of the time 
	    	        for(int i=0;i<cpuBurst;i++){
	    	        if(processArrive==true)
	    	        	processOrder= processOrder+process.getProcessID()+"|";
	    	        currentTime++;    
	    	        process.setRemainingTime(process.getRemainingTime()-1); //could not be necessary !!
	    	        processArrive= processArriveAtQ1(); //check q1 if there is arrival, excute it 
	    	        
	    	        }
	    	        process.setTerminationTime(currentTime);
	    	    }

	    	    public static boolean empty(PCB[] array) {
	    	        boolean empty = true;
	    	        for (int i = 0; i < array.length; i++) {
	    	            if (array[i] != null)
	    	                empty = false;
	    	        }
	    	        return empty;
	    	    }
	    	    
	    	    public static boolean processArriveAtQ1(){
	    	        for (int i = 0; i < Q1.length; i++){
	    	            if (Q1[i] != null && Q1[i].getArrivalTime() <= currentTime){
	    	            	scheduleProcesses() ;
	    	                return true;
	    	            }
	    	        }
	    	        return false;
	    	    } 


	    	    public static void writeInFile(){
	    	    	
		    		try {
		    		    File f = new File("report1.txt");
		    		    FileOutputStream f2 = new FileOutputStream(f);
		    		    PrintWriter outputfile= new PrintWriter(f2);
		    		    outputfile.println("process order:" +processOrder +"]");
		    		    Arrays.sort(completedProcess, Comparator.nullsLast(Comparator.comparingInt(PCB::getNumberOfprocess)));
		    		    for (int i = 0; i < completed; i++) {
		    		    	if(completedProcess[i]!=null) {
		    		    		outputfile.println(" The processID " +completedProcess[i].getProcessID()  );
		    		            outputfile.println(" The priority : " +completedProcess[i].getPriority());
		    		            outputfile.println(" The arrivalTime : " +completedProcess[i].getArrivalTime());
		    		            outputfile.println(" The cpuBurstTime :" + completedProcess[i].getCpuBurstTime());
		    		            outputfile.println(" Starting time: " + completedProcess[i].getStartTime() );
		    		            outputfile.println(" termination time  " + completedProcess[i].getTerminationTime() );
		    		            outputfile.println(" turnaround time  " + calculateTurnaroundTime(completedProcess[i]));
		    		            outputfile.println(" waiting time  " + calculateWaitingTime(completedProcess[i]) );
		    		            outputfile.println(" response time  " + calculateResponseTime(completedProcess[i]) );
		    		            outputfile.println( "---------------------------------");
		    		            
		    		    			
		    		    }
		    		    }
		    		    outputfile.println( "---------------------------------");
		    		    outputfile.println(" Average  Turn around time= "+avgTurnaroundTime);
		    		    outputfile.println(" Average  Waiting time= "+avgWaitingTime);
		    		    outputfile.println(" Average  Response time= "+avgResponseTime);
		    		    outputfile.println( "---------------------------------");

		    		    outputfile.close();
		    		    
		    		} catch (FileNotFoundException ex) {
		    			
		    		 } catch (IOException ex) {
		    		 
		    		 }   
		    		}
		    		
		    		public static void readFromFile(){
		    		    try{
		    		      File f = new File("report1.txt");
		    		      Scanner sc = new Scanner(f);
		    		           while(sc.hasNext()){
		    		              System.out.println(sc.nextLine());
		    		            }
		    		           sc.close();
		    		      }catch(IOException ex){
		    		    
		    		   }
		    		  }
	                        
	      public static int calculateWaitingTime(PCB process) {
	        return  calculateTurnaroundTime( process)- process.getCpuBurstTime();
	     }

	    public static int calculateTurnaroundTime(PCB process) {
	     return process.getTerminationTime() - process.getArrivalTime();
	   }

	           

	        
	  
	    public static int calculateResponseTime(PCB process) {
	    // Check if the process has started execution
	    if (process.getStartTime() != -1) {
	        // Response time = Start time - Arrival time
	        return process.getStartTime() - process.getArrivalTime();
	    } else {
	        // Process has not started execution yet, so response time is not applicable
	        return -1;
	    }
	 }
	   public static void calculateAverages() {
	    int numProcesses = completed;
	    int totalTurnaroundTime = 0;
	    int totalWaitingTime = 0;
	    int totalResponseTime = 0;

	    for (int i = 0; i < completed; i++) {
	        PCB process = completedProcess[i];
	        if (process != null) {
	            int turnaroundTime = calculateTurnaroundTime(process);
	            int waitingTime = calculateWaitingTime(process);
	            totalTurnaroundTime += turnaroundTime;
	            totalWaitingTime += waitingTime;
	            totalResponseTime += calculateResponseTime(process);
	        }
	    }

	     avgTurnaroundTime = (double) totalTurnaroundTime / numProcesses;
	     avgWaitingTime = (double) totalWaitingTime / numProcesses;
	     avgResponseTime = (double) totalResponseTime / numProcesses;

	   
	}

	

}
