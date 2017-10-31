import java.nio.charset.Charset;
import java.rmi.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.math.BigInteger;
import java.security.*;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
// import a json package


/* JSON Format

 {
    "metadata" :
    {
        file :
        {
            name  : "File1"
            numberOfPages : "3"
            pageSize : "1024"
            size : "2291"
            page :
            {
                number : "1"
                guid   : "22412"
                size   : "1024"
            }
            page :
            {
                number : "2"
                guid   : "46312"
                size   : "1024"
            }
            page :
            {
                number : "3"
                guid   : "93719"
                size   : "243"
            }
        }
    }
}
 
 
 */


public class DFS
{
    int port;
    Chord  chord;

    /**
     * @param objectName Parameter that contains a md5 hash
     * @return the md5 value
     * 
     * This function obtains the md5 value. 
     */
    private long md5(String objectName)
    {
        try
        {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(objectName.getBytes());
            BigInteger bigInt = new BigInteger(1,m.digest());
            return Math.abs(bigInt.longValue());
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();

        }
        return 0;
    }



    /**
     * @param port Parameter that contains the port for the program.
     * @throws Exception
     * 
     * DFS constructor with a port number as a parameter.
     */
    public DFS(int port) throws Exception
    {
        this.port = port;
        long guid = md5("" + port);
        chord = new Chord(port, guid);
        Files.createDirectories(Paths.get(guid+"/repository"));
    }

    /**
     * @param Ip Parameter that contains the IP address for the connection.
     * @param port Parameter that contains the port for the program.
     * @throws Exception
     * 
     * This method joins two clients that are concurrently running. 
     */
    public  void join(String Ip, int port) throws Exception
    {
        chord.joinRing(Ip, port);
        chord.Print();
    }

    /**
     * @return the metadata file
     * @throws Exception
     * 
     * This method reads the metadata file in the repositories. 
     */
    public Metadata readMetaData() throws Exception
    {
        Metadata jsonFile = null;
        JsonReader rawData = null;
        Gson gson = new GsonBuilder().create();
        long guid = md5("Metadata.json");
        ChordMessageInterface peer = chord.locateSuccessor(guid);

        /* If Metadata.json has not be created yet, create it, else read it in */
        if(Files.notExists(Paths.get(peer.getId()+"/repository/" + guid))) {
            jsonFile = new Metadata();
            writeMetaData(jsonFile);
        } else {
            rawData = new JsonReader(new InputStreamReader(peer.get(guid)));
            jsonFile = gson.fromJson(rawData, Metadata.class);

            /*File f = new File("./" + peer.getId()+"/repository/" + guid);
            BufferedReader metadataraw = new BufferedReader(new FileReader(f));
            StringBuilder json = new StringBuilder();
            String line;
            while((line = metadataraw.readLine()) != null) {
                json.append(line);
            }
            jsonFile = gson.fromJson(json.toString(), Metadata.class);*/
        }
        return jsonFile;
    }

    /*public void writeMetaData(InputStream stream) throws Exception
    {
        JsonParser jsonParser = null;
        long guid = md5("Metadata.json");
        ChordMessageInterface peer = chord.locateSuccessor(guid);
        peer.put(guid, stream);
    }*/

    /**
     * @param m parameter that contains the metadata file to be written
     * @throws Exception
     * 
     * This method reads the metadata file in the repositories. 
     */
    public void writeMetaData(Metadata m) throws Exception {
        Metadata jsonFile = m;
        Gson gson = new GsonBuilder().create();
        long guid = md5("Metadata.json");
        ChordMessageInterface peer = chord.locateSuccessor(guid);
        peer.customPut(guid, gson.toJson(m));
        /*File f = new File("./" + peer.getId()+"/repository/" + guid);
        FileWriter fw = new FileWriter(f);
        fw.write(gson.toJson(jsonFile));
        fw.close();*/
    }
    /**
     * Renames the name of the file
     *
     * @param oldName Parameter that contains the name of the old file. 
     * @param newName Parameter that contains the name of the new file.
     * @throws Exception
     * 
     * This method switches the name of a file to a name that the user specifies. 
     */
    public void mv(String oldName, String newName) throws Exception
    {
        // TODO:  Change the name in Metadata
        // Write Metadata
    	/*JsonObject metadata = readMetaData();
    	JsonArray files = metadata.getJsonArray("files");
    	files.forEach(file -> {
    		if(file.asJsonObject().getString("name").equals(oldName)) {

    		}
    	});*/
        Metadata m = readMetaData();
        boolean fileExists = false;
        for(MetaFile f : m.getListOfFiles()) {
            if(f.getName().equals(oldName)) {
                f.setName(newName);
                fileExists = true;
            }
        }
        if(fileExists) {
            writeMetaData(m);
            System.out.println("File successfully updated");
        } else {
            System.out.println("No such file exists, please try again");
        }
    }


    /**
     * @return the list of the files in the metadata
     * @throws Exception
     * 
     * This function lists all of the files in the metadata.
     */
    public String ls() throws Exception
    {
        String listOfFiles;
        // TODO: returns all the files in the Metadata
        Metadata m = readMetaData();
        if(m.getListOfFiles().isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        m.getListOfFiles().forEach(metaFile -> {
            sb.append(metaFile.getName());
            sb.append(",");
        });
        listOfFiles = sb.toString();
        return listOfFiles.substring(0, listOfFiles.length() - 1);
    }


    /**
     * @param fileName Parameter that contains the file and the name of it.
     * @throws Exception
     * 
     * This function creates a new entry to the metadata.
     */
    public void touch(String fileName) throws Exception
    {
        // TODO: Create the file fileName by adding a new entry to the Metadata
        // Write Metadata
        /*read -> dosomething -> write*/
        Metadata m = readMetaData();
        MetaFile f = new MetaFile(fileName, 0, 1024, 0);
        m.addFile(f);
        writeMetaData(m);
    }
    
    /**
     * @param fileName Parameter that contains the file and the name of it.
     * @throws Exception
     * 
     * This function deletes a page from the metadata.
     */
    public void delete(String fileName) throws Exception
    {
        // TODO: remove all the pages in the entry fileName in the Metadata and then the entry
        // for each page in Metadata.filename
        //     peer = chord.locateSuccessor(page.guid);
        //     peer.delete(page.guid)
        // delete Metadata.filename
        // Write Metadata
        Metadata m = readMetaData();
        MetaFile f = m.getFile(fileName);
        for(Page p : f.getListOfPages()) {
            chord.delete(p.getGuid());
        }
        f.removeAllPages();
        m.removeFile(fileName);
        writeMetaData(m);
    }

    /**
     * @param fileName Parameter that contains the file and the name of it.
     * @param pageNumber Parameter that contains the page number of the file
     * @return the list of the files in the metadata.
     * @throws Exception
     * 
     * This function lists all of the files in the metadata.
     */
    public byte[] read(String fileName, int pageNumber) throws Exception
    {
        // TODO: read pageNumber from fileName
        Metadata m = readMetaData();
        byte[] data = null;
        if(!m.doesFileExist(fileName)) {
            System.out.println("File does not exist.");
        } else if(pageNumber > m.getFile(fileName).getNumberOfPages()) {
            System.out.println("Page does not exist in " + fileName + ".");
        } else {
            Page p = m.getFile(fileName).getPage(pageNumber - 1);
            data = chord.customGet(p.getGuid()).getBytes();
        }
        return data;
    }


    /**
     * @param fileName Parameter that contains the file and the name of it.
     * @return the last page of the fileName.
     * @throws Exception
     * 
     * This function returns the last page of the file in metadata.json
     */
    public byte[] tail(String fileName) throws Exception
    {
        // TODO: return the last page of the fileName
        Metadata m = readMetaData();
        int numberOfPages = m.getFile(fileName).getNumberOfPages();
        return read(fileName, numberOfPages);
    }
    /**
     * @param fileName Parameter that contains the file and the name of it.
     * @return the first page of the fileName.
     * @throws Exception
     * 
     * This function returns the first page of the file in Metadata.json
     */
    public byte[] head(String fileName) throws Exception
    {
        // TODO: return the first page of the fileName
        return read(fileName, 1);
    }
    
    /**
     * @param fileName Parameter that contains the file and the name of it.
     * @param data Parameter that contains the data of the file.
     * @throws Exception
     * 
     * This function adds data to the end of a file in Metadata.json
     */
    public void append(String fileName, byte[] data) throws Exception
    {
        // TODO: append data to fileName. If it is needed, add a new page.
        // Let guid be the last page in Metadata.filename
        //ChordMessageInterface peer = chord.locateSuccessor(guid);
        //peer.put(guid, data);
        // Write Metadata
        Metadata m = readMetaData();
        MetaFile f = m.getFile(fileName);
        Page p = null;
        final int MAX_SIZE = 1024;
        int lastPageNum = 1;
        int offset = 0;
        int sizeOfData = data.length,
                iterations = (int)(Math.ceil((double)sizeOfData / MAX_SIZE)),
                currentByte = 0;
        byte[] remainingData = null;
        if(f == null) {
            System.out.println("File does not exist");
            return;
        }
        System.out.println(f.getNumberOfPages());
        /* If pages exist and theres room, append data */
        if(!f.getListOfPages().isEmpty() && f.getPage(f.getNumberOfPages() - 1).getSize() < 1024) {
            lastPageNum = f.getNumberOfPages() - 1;
            p = f.getPage(lastPageNum);
            remainingData = appendToUnfilledPage(m, f, p, data);
            if(remainingData == null) {
                writeMetaData(m);
                return;
            }
        }

        /**
         * Need to check:
         * 1) Whether file already has pages, if:
         *      1a) True: Get last page, if:
         *          1ai) < 1024, append
         *          1aii) == 1024, create new page
         *      1b) False: append
         */

        for(int ii = 0; ii < iterations; ii++) {
            if(sizeOfData > MAX_SIZE) currentByte = MAX_SIZE;
            else currentByte = sizeOfData;
            byte[] portion = new byte[currentByte];
            for(int jj = 0; jj < currentByte; jj++) {
                portion[jj] = data[jj * (ii + 1)];
            }
            Page p2 = new Page(f.getNumberOfPages() + 1, md5(f.getName() + "Page" + (f.getNumberOfPages() + 1)), currentByte);
            f.addPage(p2);
            f.updateSize();
            InputStream stream = new ByteArrayInputStream(portion);
            chord.put(p2.getGuid(), stream);
        }
        writeMetaData(m);
    }

    /**
     * @param m Parameter that contains the Metadata 
     * @param f Parameter that contains the Metadata file
     * @param p Parameter that contains a page
     * @param data Parameter where the data is stored
     * @return the appended data of a new file. 
     * @throws Exception
     */
    private byte[] appendToUnfilledPage(Metadata m, MetaFile f, Page p, byte[] data) throws Exception {
        //String currFile = convertInputStreamToString(chord.get(p.getGuid()));
        String currFile = chord.customGet(p.getGuid());
        int remainingBytes = 1024 - p.getSize();
        byte[] transferData = null;//new byte[remainingBytes];
        byte[] remainingData = null;
        //If there is more data than the page can handle, return the remaining data
        //If there isn't, fill it up completely.
        if(data.length > remainingBytes) {
            transferData = new byte[remainingBytes];
            remainingData = new byte[data.length - remainingBytes];
            for(int ii = 0; ii < data.length; ii++) {
                if(ii < remainingBytes)
                    transferData[ii] = data[ii];
                else
                    remainingData[ii] = data[ii];
            }
        } else {
            transferData = new byte[data.length];
            for(int ii = 0; ii < data.length; ii++) {
                transferData[ii] = data[ii];
            }
        }
        String addition = convertInputStreamToString(new ByteArrayInputStream(transferData));
        StringBuilder sb = new StringBuilder();
        sb.append(currFile);
        sb.append(addition);
        chord.customPut(p.getGuid(), sb.toString());
        p.setSize(sb.toString().getBytes().length);
        f.updateSize();
        return remainingData;
    }

    /**
     * Found on https://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
     */
    /**
     * @param is Parameter that contains input to be converted to a string
     * @return the string of the input
     * @throws Exception
     * 
     * This function contains the input converted to a string.
     */
    private String convertInputStreamToString(InputStream is) throws Exception {
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            int result = bis.read();
            while(result != -1) {
                buf.write((byte) result);
                result = bis.read();
            }
            // StandardCharsets.UTF_8.name() > JDK 7
            return buf.toString("UTF-8");
        } catch(IOException ioe) {

        }
        return "";
    }

}