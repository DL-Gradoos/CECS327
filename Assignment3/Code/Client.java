import java.rmi.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.nio.file.*;


public class Client
{
    DFS dfs;
    public Client(int p) throws Exception {
        dfs = new DFS(p);
        
            // User interface:
            // join, ls, touch, delete, read, tail, head, append, move
    }

    public static void showMenu() {
        System.out.println("Menu");
        System.out.println("1. Join");
        System.out.println("2. List");
        System.out.println("3. Touch");
        System.out.println("4. Delete");
        System.out.println("5. Read");
        System.out.println("6. Tail");
        System.out.println("7. Head");
        System.out.println("8. Append");
        System.out.println("9. Move");
        System.out.println("10. Quit");
    }

    private static void runCommand(Client c, String[] input) throws Exception {
        switch(Integer.parseInt(input[0])) {
            case 1:
                //join
                c.dfs.join(input[1], Integer.parseInt(input[2]));
                break;
            case 2:
                //ls
                String x = c.dfs.ls();
                System.out.println(x);
                break;
            case 3:
                //touch
                c.dfs.touch(input[1]);
                break;
            case 4:
                //delete
                break;
            case 5:
                //read
                break;
            case 6:
                //tail
                break;
            case 7:
                //head
                break;
            case 8:
                //append
                break;
            case 9:
                //move
                break;
            case 10:
                //quit
                System.out.println("Shutting down...");
                System.exit(0);
            default:
                return;
        }

    }
    
    static public void main(String args[]) throws Exception
    {
        if (args.length < 1 ) {
            throw new IllegalArgumentException("Parameter: <port>");
        }
        Client client = new Client( Integer.parseInt(args[0]));
        Scanner input = new Scanner(System.in);
        String[] command;
        while(true) {
        	showMenu();
        	command = input.nextLine().split("\\s+");
        	runCommand(client, command);
        }
     } 
}
