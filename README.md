## CECS 327: Introduction to Networks and Distributed Computing
Projects done for CECS327  
Group Members: **Daniel Lee** & **Anhkhoi Vu**

_Note: Use the command "g++ -std=c++11 ftpclient.cpp to compile code"_

### Projects Included
1. FTP Client  
A basic ftp client that runs in terminal. Allows the user to choose 6 commands, HELP, LIST, SHOW [filename], GET [filename], INTO [directory], and QUIT.
    * **HELP**  
    Prints a description on how to use each command
    * **LIST**  
    Prints all files and folders in the current directory
    * **SHOW filename**  
    Shows the contents of filename in terminal
    * **GET filename**  
    Downloads filename
    * **INTO foldername**  
    Changes working directory to foldername
    * **QUIT**  
    Exits the application

2. KiloBots  
This program needs to be used in conjunction with the kilobots library. The code supplied shows how a leader is chosen in a ring of robots, where the leader will light up green. The leader is chosen out of whichever robot has the lowest id number.
