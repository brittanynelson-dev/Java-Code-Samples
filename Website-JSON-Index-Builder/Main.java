/*  ************************************
    This program is designed to build an index file to be referenced by Lunr search for a User Guide website.
    The JAR file should be placed in the home directory of the site and run directly from there. It will check all folders
    aside from css, js, img, and Templates to gather data from all included .html files and compile a JSON formatted index
    file named idx.txt that will be read by Lunr search when the website's search box is used.
************************************** */


import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;



public class Main {

    public static void main(String[] args)
    {
        //Check to see if user supplied a directory, otherwise use the default directory
        String directory = "../Default";
        if (args.length > 0) {
            directory = args[0];
        }

        //Create a list of files to gather results & filter to get .html files only
        List<File> files = new ArrayList<>();

        FilenameFilter htmlFilter = new FilenameFilter(){
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                if (lowercaseName.endsWith(".html")) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        //Setup String to hold data to be printed to index file
        String indexData = "var json = '[";

        //Call getFiles method to populate the list of files
        getFiles(System.getProperty("user.dir"), files, htmlFilter);

        //Read from each file to gather data for the index file
        for(File file: files) {
            try {
                //Convert file to String for more efficient searching
                String data = new String(Files.readAllBytes(Paths.get(file.getPath())));
                //Get required fields from file content
                String title = data.substring(data.indexOf("<title>") + 7,data.lastIndexOf("</title>"));
                String desc = data.substring(data.indexOf("description\" content=\"") + 22,data.indexOf("<title>") - 5);
                String content = data.substring(data.indexOf("InstanceBeginEditable name=\"Content\" -->") + 40,data.lastIndexOf("<!-- InstanceEndEditable"));
                //Strip html and trailing whitespace from content - not needed for searching pages
                content = content.replaceAll("<[^>]*>|\\r|\\n|\\t|\"", "+").trim();
                content = content.replaceAll("[+]+","+");
                content = content.replaceAll("[^a-zA-Z0-9-_\\s]"," ");

                //Get file paths relative to idx file location for link field
                String path = file.getPath().replace("\\","/");
                path = path.substring(path.indexOf("/Default") + 8);

                //Add relevant fields to indexData String
                indexData += "{\"title\":\"" + title + "\",";
                indexData += "\"description\":\"" + desc + "\",";
                indexData += "\"content\":\"" + content + "\",";
                indexData += "\"link\":\"" + path + "\"},";

            } catch (IOException e){
                System.out.println(Arrays.toString(e.getStackTrace()));
            }
        }

        //Remove final comma and properly close JSON data/JS variable
        indexData = indexData.substring(0,indexData.length() - 1) + "]'";

        //Setup index file
        String idxFile = directory + "/idx.txt";
        File indexFile = new File(idxFile);
        try {
            FileWriter fw = new FileWriter(indexFile);
            fw.write(indexData);
            fw.close();
        } catch (IOException e){
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    public static void getFiles(String directoryName, List<File> files, FilenameFilter filter) {
        File directory = new File(directoryName);

        // Get all files from a directory.
        File[] fList = directory.listFiles();
        if(fList != null) {
            for (File file : fList) {
                if (file.isFile()) {
                    //Ignore everything aside from HTML files
                    String[] htmlFiles = directory.list(filter);
                    for (String s : htmlFiles) {
                        if (s.equals(file.getName())) {
                            //Exclude 404 & search pages from list, should not be searchable
                            if (!s.equals("404.html") && !s.equals("search.html")){
                                files.add(file);
                            }
                        }
                    }
                } else if (file.isDirectory()) {
                    getFiles(file.getPath(), files, filter);
                }
            }
        }
    }
}