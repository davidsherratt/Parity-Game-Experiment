/* 	SGGSolver.java
*
*	SGGSolver reads in a Parity Game produced by PGSolver as a Simplified Graph Game 
*	and produces a .cnf file that holds the cnf for the winning strategy for player 0.
*
*	@version 2
*	@author David Sherratt
 */

import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.text.DecimalFormat;
import java.io.PrintWriter;

public class SGGSolver {
    public static void main(String[] args) {
	try {
	    File inputFile = new File(args[0]);
	    Scanner fileIn = new Scanner(inputFile);
			
	    fileIn.next();
			
	    String numberOfNodes_s = fileIn.next();
	    numberOfNodes_s = numberOfNodes_s.substring(0, numberOfNodes_s.length()-1);
	    int numOfNodes_i = 1 + Integer.parseInt(numberOfNodes_s);
	    System.out.println(numOfNodes_i);
	    
	    boolean[][] edges = new boolean[numOfNodes_i][numOfNodes_i];
	    int[] owner = new int[numOfNodes_i];

	    //Make nodes
	    for (int i = 0; i < numOfNodes_i; i++) {
		for (int j = 0; j < numOfNodes_i; j++) {
		    edges[i][j] = false;
		}
	    }
			
	    fileIn.nextLine();
			
	    //for each node set owner and edges
	    for (int i = 0; i < numOfNodes_i; i++) {
		String nodeInfo = fileIn.nextLine();
		Scanner infoScanner = new Scanner(nodeInfo);
				
		//System.out.println(nodeInfo);
				
		infoScanner.next();
		infoScanner.next();
				
		//System.out.println("Got Here");
				
		String owner_s = infoScanner.next();
		int owner_i = Integer.parseInt(owner_s);
		
		owner[i] = owner_i;

		//System.out.println(owner_i);
				
		String edges_s = infoScanner.next();
		for (String succ: edges_s.split(",")){
		    //System.out.println(succ);
		    int nodeto_i = Integer.parseInt(succ);
		    edges[i][nodeto_i] = true;
		}
				
		infoScanner.next();
	    }
			

	    int player = Integer.parseInt(args[1]);
	    DetermineClauses(edges, owner, numOfNodes_i, args[0], player);
			
	}
	catch (Exception e) {
	    if (args.length < 2) {
		Usage();
	    } else if (args[0].equals("--help")) {
		Usage();
	    } else {
		System.out.println("Error processing");
		System.out.println("Cannot find file or wrong file type");
	    }
	}
    }


    private static int twoNodes(int n, int i, int j) {
	int r = (n * i + j + 1);
	return r;
    }

    private static int threeNodes(int n, int i, int j, int k) {
	int r = n * n + (n * (n * i + j) + k + 1);
	return r;
    }


    private static long noOfLines = 0;

    private static void choosemethod(PrintWriter out, int round, String output) {
	if (round == 0) {
	    noOfLines++;
	} else {
	    out.println(output);
	}
    }


    public static void DetermineClauses(boolean[][] edges, int[] owner, int n, String outputName, int player) {

        // player is 0 or 1, so opponent is 1-player
	int opponent = 1 - player;

        //List of nodes (With set owners), List of Edges and number of nodes
	String n_s = Integer.toString(n);
	String format = "";
	for (int i = 0; i < n_s.length(); i++) {
	    format = format + "0";
	}
	DecimalFormat df = new DecimalFormat(format);
	//Number of Clauses
		
	int numOfVariables = (n * n * n) + (n * n);
						
	try {
	    outputName = outputName.substring(0, outputName.length() - 4);
	    System.out.println(outputName);
	    //Write to file and call other program
	    PrintWriter out = new PrintWriter(outputName + "player" + player + ".cnf");
			
	    out.println("c clauses.cnf");
	    out.println("c - MOVE LINES AT BOTTOM OF FILE TO TOP");		
		
	    for (int round = 0; round < 2; round++) {
		if (round > 0) {
		    out.println("p cnf " + numOfVariables + " " + noOfLines);
		    out.println(" ");
		}
		
		//E sigma is a subset of E
		for (int i = 0; i < n; i++) {
		    for (int j = 0; j < n; j++) {
			if (edges[i][j] == false) {
			    String eij = "-" + twoNodes(n,i,j) + " 0";
			    choosemethod(out, round, eij);
			}
		    }
		}
		
		//If node belongs to player, then there must be an outgoing edge
		for (int i = 0; i < n; i++) {
		    if (owner[i] == player) {
			String eij = "";
			for (int j = 0; j < n; j++) {
			    if (edges[i][j]) {
				eij = eij + twoNodes(n,i,j) + " ";
			    }
			}
			eij = eij + "0";
			choosemethod(out, round, eij);
		    }
		}
		
		//All the edges starting from a node owned by opponent are untouched in e sigma
		for (int i = 0; i < n; i++) {
		    if (owner[i] == opponent) {
			for (int j = 0; j < n; j++) {
			    if (edges[i][j]) {
				String eij = "" + twoNodes(n,i,j) + " 0";
				choosemethod(out, round, eij);
			    }
			}
		    }
		}
      	    
		//Base case of Induction for winning strategy
		for (int i = 0; i < n; i++) {
		    for (int j = 0; j < n; j++) {
			if (edges[i][j]) {
			    int k = j;
			    if (i < j) {
				k = i;
			    }
			    String eij = "-" + twoNodes(n,i,j) + " " + threeNodes(n,i,j,k) + " 0";
			    choosemethod(out, round, eij);
			}
		    }
		}
		
		// transitivity of R
		// if Rijk and Ejl and o=min(k,l) then Rilo
		for (int j = 0; j < n; j++) {
		    for (int l = 0; l < n; l++) {
			if (edges[j][l]) {
			    for (int k = 0; k < n; k++) {
				for (int i = 0; i < n; i++) {
				    int o = k;
				    if (l < o){  // o = min (k,l)
					o = l;
				    }
				    String eij = "-" + threeNodes(n,i,j,k) + " -" + twoNodes(n,j,l) + " " + threeNodes(n,i,l,o) + " 0";
				    choosemethod(out, round, eij);
				}
			    }
			}
		    }
		}
		
		// Winning condition
		// not R0ij or not Riik   for k owned by opponent
		for (int k = 0; k < n; k++) {
		    if (owner[k] == opponent) {
			for (int j = 0; j < n; j++) {
			    for (int i = 0; i < n; i++) {
				String eij = "-" + threeNodes(n,0,i,j) + " -" + threeNodes(n,i,i,k) + " 0";
				choosemethod(out, round, eij);
			    }
			}
		    }
		}

	    }

	    out.close();
	    //Write to file and call other program
	    //PrintWriter out = new PrintWriter("clauses.cnf");
			
	    //out.println("c clauses.cnf");
	    //out.println("c");
	} catch (Exception ee) {
	    System.out.println("Error printing file");
	}
    }
	
    public static void Usage() {
	System.out.println("usage:  SGGSolver <filename> <player>");
    }
}
