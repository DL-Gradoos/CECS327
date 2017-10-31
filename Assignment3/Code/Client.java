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

    /**
     * This function displays the menu of the client. 
     */
    public static void showMenu() {
        System.out.println("Menu");
        System.out.println("1. Join [ip] [port]");
        System.out.println("2. List");
        System.out.println("3. Touch [fileName]");
        System.out.println("4. Delete [fileName]");
        System.out.println("5. Read [fileName] [pageNumber]");
        System.out.println("6. Tail");
        System.out.println("7. Head");
        System.out.println("8. Append");
        System.out.println("9. Move [oldFileName] [newFileName]");
        System.out.println("10. Quit");
    }

    /**
	 * Runs user chosen command
	 *
     * @param c The client
     * @param input User input
     * @throws Exception
     */
    private static void runCommand(Client c, String[] input) throws Exception {
        Scanner in = new Scanner(System.in);
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
                System.out.println("Creating new file...");
                c.dfs.touch(input[1]);
                System.out.println("File created!");
                break;
            case 4:
                //delete
                c.dfs.delete(input[1]);
                break;
            case 5:
                //read
                if(!input[2].matches("\\d+")) {
                    System.out.println("Please enter a valid page number.");
                    break;
                }
                byte[] readData = c.dfs.read(input[1], Integer.parseInt(input[2]));
                if(readData == null) {
                    System.out.println("No data could be extracted.");
                } else {
                    String message = new String(readData);
                    System.out.println(message);
                }
                break;
            case 6:
                //tail
                byte[] tailData = c.dfs.tail(input[1]);
                if(tailData == null) {
                    System.out.println("No data could be extracted.");
                } else {
                    String message = new String(tailData);
                    System.out.println(message);
                }
                break;
            case 7:
                //head
                byte[] headData = c.dfs.head(input[1]);
                if(headData == null) {
                    System.out.println("No data could be extracted.");
                } else {
                    String message = new String(headData);
                    System.out.println(message);
                }
                break;
            case 8:
                //append
                System.out.println("What is the file name?");
                String fileName = in.nextLine();
                System.out.println("Please type the data you wish to append. Press enter to finish.");
                byte[] data = in.nextLine().getBytes();
                c.dfs.append(fileName, data);
                break;
            case 9:
                //move
                c.dfs.mv(input[1], input[2]);
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