import java.nio.charset.Charset;
import java.rmi.*;
import java.net.*;
import java.util.*;

import org.json.JSONObject;

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
    
    
    
    public DFS(int port) throws Exception
    {
        this.port = port;
        long guid = md5("" + port);
        chord = new Chord(port, guid);
        Files.createDirectories(Paths.get(guid+"/repository"));
    }
    
    public  void join(String Ip, int port) throws Exception
    {
        chord.joinRing(Ip, port);
        chord.Print();
    }
    
    public Metadata readMetaData() throws Exception
    {
        Metadata jsonFile = null;
        JsonReader rawData = null;
        Gson gson = new GsonBuilder().create();
        long guid = md5("Metadata.json");
        ChordMessageInterface peer = chord.locateSuccessor(guid);

        /* If Metadata.json has not be created yet, create it, else read it in */
        if(Files.notExists(Paths.get(peer.getId()+"/repository/" + guid))) 
        {
            jsonFile = new Metadata();
            writeMetaData(jsonFile);
        } 
        else 
        {
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

    private void analyze() {

    }

    /*public void writeMetaData(InputStream stream) throws Exception
    {
        JsonParser jsonParser = null;
        long guid = md5("Metadata.json");
        ChordMessageInterface peer = chord.locateSuccessor(guid);
        peer.put(guid, stream);
    }*/

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
    * @param oldName
    * @param newName
    * @throws Exception
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
    	
    }

    
    public String ls() throws Exception
    {
        String listOfFiles;
       // TODO: returns all the files in the Metadata
        Metadata m = readMetaData();
        StringBuilder sb = new StringBuilder();
        m.getListOfFiles().forEach(metaFile -> {
            sb.append(metaFile.getName());
            sb.append(",");
        });
        listOfFiles = sb.toString();
        return listOfFiles.substring(0, listOfFiles.length() - 1);
    }

    
    public void touch(String fileName) throws Exception
    {
         // TODO: Create the file fileName by adding a new entry to the Metadata
        // Write Metadata
        /*read -> dosomething -> write*/
        Metadata m = readMetaData();
        MetaFile f = new MetaFile(fileName, 0, 1024, 0);
        m.addFile(f);
        writeMetaData(m);
        //m.getListOfFiles().forEach(mFile -> System.out.println(mFile));
        //System.out.println(m.getListOfFiles());
    }
    public void delete(String fileName) throws Exception
    {
        // TODO: remove all the pages in the entry fileName in the Metadata and then the entry
        // for each page in Metadata.filename
        //     peer = chord.locateSuccessor(page.guid);
        //     peer.delete(page.guid)
        // delete Metadata.filename
        // Write Metadata

        
    }
    
    public Byte[] read(String fileName, int pageNumber) throws Exception
    {
        // TODO: read pageNumber from fileName
        return null;
    }
    
    public Byte[] tail(String fileName) throws Exception
    {
        // TODO: return the last page of the fileName
        Metadata m = readMetaData();
        JsonParser parser = new JsonParser();
        JsonArray jsonarray = (JsonArray) parser.parse(new FileReader("Metadata.json"));
    	Object o = jsonarray.getAsJsonObject();
    	JSONObject jsonobject = (JSONObject) o;
    	String name = (String) jsonobject.get("name");
    	if(name.equals("File2"))
    	{
	    	System.out.println(name);
	    	JsonArray newarray = (JsonArray) jsonobject.get("page");
	    	for(Object n : newarray)
	    	{
	    		System.out.println(n+" ");
	    	}
	    	return null;
    	}
    	else
    	{
    		return null;
    	}
    }
    
    public Byte[] head(String fileName) throws Exception
    {
        // TODO: return the first page of the fileName
        Metadata m = readMetaData();
        JsonParser parser = new JsonParser();
        JsonArray jsonarray = (JsonArray) parser.parse(new FileReader("Metadata.json"));
    	
    	Object o = jsonarray.getAsJsonObject();
    	JSONObject jsonobject = (JSONObject) o;
    	String name = (String) jsonobject.get("name");
    	if(name.equals("File1"))
    	{
	    	System.out.println(name);
	    	JsonArray newarray = (JsonArray) jsonobject.get("page");
	    	for(Object n : newarray)
	    	{
	    		System.out.println(n+" ");
	    	}
	    	return null;
    	}
    	else
    	{
    		return null;
    	}
    }
    
    public void append(String filename, Byte[] data) throws Exception
    {
        // TODO: append data to fileName. If it is needed, add a new page.
        // Let guid be the last page in Metadata.filename
        //ChordMessageInterface peer = chord.locateSuccessor(guid);
        //peer.put(guid, data);
        // Write Metadata
        Metadata m = readMetaData();
    }
}
