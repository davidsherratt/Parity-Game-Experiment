/* 	AverageResult.java
*
*	AverageResult reads in minisat results through the arguments and 
*	calculates the averages for the satisfiable and the unsatisfiable
*
*	@version 1
*	@author David Sherratt
 */
 
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
 
 public class AverageResult {
	public static void main(String[] args) {
	
		int args_length = args.length;
		
		ArrayList<Double> satisfiable = new ArrayList<Double>();
		ArrayList<Double> unsatisfiable = new ArrayList<Double>();
		
		// Loops through all the args and
		// Determines if file is sat or unsatisfiable
		// and stores the CPU time into the array
		try{
		
			for (int i = 0; i < args_length; i++) {
				//Read file from args. Most probable error.
				File inputFile = new File(args[i]);
				//To read the file
				Scanner fileIn = new Scanner(inputFile);
				
				fileIn.useDelimiter("CPU time");
				fileIn.next();
				
				fileIn.useDelimiter(":");
				fileIn.next();
				
				fileIn.useDelimiter(" ");
				fileIn.next();
				
				Double temp_d = Double.parseDouble(fileIn.next());
				
				fileIn.nextLine();
				String sat = fileIn.next();
				
				// --- Compare and Store ---
				
				if (sat.equals("\nSATISFIABLE\n")) {
					//System.out.println("SAT " + temp_d + " " + sat + "end");
					satisfiable.add(temp_d);
				} else if (sat.equals("\nUNSATISFIABLE\n")){
					//System.out.println("UNSAT " + temp_d + " " + sat + "end");
					unsatisfiable.add(temp_d);
				}
				
			}
			
			// --- Calculate Averages and Report ---
			
			//Satisfiable
			double sat_size_i = satisfiable.size();
			double sat_total_i = 0.0;
			//for (int i = 0; i < sat_size_i; i++) {
			//	sat_total_i = sat_total_i + satisfiable.get(i);
			//}
			//double sat_average = sat_total_i / sat_size_i;
			for (int i = 0; i < sat_size_i; i++) {
			    if (satisfiable.get(i) > sat_total_i)
				sat_total_i = satisfiable.get(i);
			}
			double sat_average = sat_total_i;
			
			//Unsatisfiable
			double unsat_size_i = unsatisfiable.size();
			double unsat_total_i = 0.0;
			//for (int i = 0; i < unsat_size_i; i++) {
			//	unsat_total_i = unsat_total_i + unsatisfiable.get(i);
			//}
			//double unsat_average = unsat_total_i / unsat_size_i;
			for (int i = 0; i < unsat_size_i; i++) {
			    if (unsatisfiable.get(i) > unsat_total_i)
				unsat_total_i = unsatisfiable.get(i);
			}
			double unsat_average = unsat_total_i;
			
			System.out.println("Total Satisfiable Files = " + sat_size_i);
			System.out.println("Average time for satisfiable = " + sat_average);
			System.out.println(" ");
			System.out.println("Total Unsatisfiable Files = " + unsat_size_i);
			System.out.println("Average time for unsatisfiable = " + unsat_average);
			
		} catch (Exception e) {
			if (args.length == 0) {
				Usage();
			} else if (args[0].equals("--help")) {
				Usage();
			} else {
				System.out.println("Error processing");
				System.out.println("Cannot find file or wrong file type");
			}
		}
	
	}
	
	public static void Usage() {
		System.out.println("AverageResult file1 file2 ... filen");
		System.out.println("Where filen is a minisat results file");
	}
}
