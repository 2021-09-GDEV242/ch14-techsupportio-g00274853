import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/**
 * The responder class represents a response generator object.
 * It is used to generate an automatic response, based on specified input.
 * Input is presented to the responder as a set of words, and based on those
 * words the responder will generate a String that represents the response.
 *
 * Internally, the reponder uses a HashMap to associate words with response
 * strings and a list of default responses. If any of the input words is found
 * in the HashMap, the corresponding response is returned. If none of the input
 * words is recognized, one of the default responses is randomly chosen.
 * 
 * @author Yixuan Sun
 * @version 2021.12.6
 */
public class Responder
{
    // Used to map key words to responses.
    private HashMap<String, String> responseMap;
    // Default responses to use if we don't recognise a word.
    private ArrayList<String> defaultResponses;
    // The name of the file containing the default responses.
    private static final String FILE_OF_DEFAULT_RESPONSES = "default.txt";
    private static final String FILE_OF_RESPONSES = "responses.txt";
    private Random randomGenerator;

    /**
     * Construct a Responder
     */
    public Responder()
    {
        responseMap = new HashMap<>();
        defaultResponses = new ArrayList<>();
        fillResponseMap();
        fillDefaultResponses();
        randomGenerator = new Random();
    }

    /**
     * Generate a response from a given set of input words.
     * 
     * @param words  A set of words entered by the user
     * @return       A string that should be displayed as the response
     */
    public String generateResponse(HashSet<String> words)
    {
        Iterator<String> it = words.iterator();
        while(it.hasNext()) {
            String word = it.next();
            String response = responseMap.get(word);
            if(response != null) {
                return response;
            }
        }
        // If we get here, none of the words from the input line was recognized.
        // In this case we pick one of our default responses (what we say when
        // we cannot think of anything else to say...)
        return pickDefaultResponse();
    }

    /**
     * Enter all the known keywords and their associated responses
     * into our response map.
     */
    private void fillResponseMap()
    {
         Charset charset = Charset.forName("US-ASCII");
        Path path = Paths.get(FILE_OF_RESPONSES);
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String key = reader.readLine();
            String[] keys = null;
            String response = reader.readLine();
            String nextLine = reader.readLine();
            String fullResponse = "";
            while (response != null) {
                if (key.contains(",")) {
                    keys = key.split(", ");
                }

                if (nextLine == null) {
                    fullResponse += response;
                } else if (response.length() != 0 && nextLine.length() == 0) {
                    fullResponse += response;
                } else if (response.length() != 0 && fullResponse.length() != 0) {
                    fullResponse += response;
                    response = reader.readLine();
                    continue;
                } else if (response.length() != 0 && nextLine.length() != 0) {
                    fullResponse += response + nextLine;
                    response = reader.readLine();
                    if (response.length() != 0) {
                        nextLine = reader.readLine();
                    }
                    continue;
                } 

                if (keys != null) {
                    responseMap.put(keys[0], fullResponse);
                    responseMap.put(keys[1], fullResponse);
                } else {
                    responseMap.put(key, fullResponse);
                }

                fullResponse = "";
                keys = null;
                key = reader.readLine();
                response = reader.readLine();
                nextLine = reader.readLine();
            }
        }
        catch(FileNotFoundException e) {
            System.err.println("Unable to open " + FILE_OF_RESPONSES);
        }
        catch(IOException e) {
            System.err.println("A problem was encountered reading " +
                               FILE_OF_RESPONSES);
        }
        System.out.println(responseMap);
    }

     /**
     * Build up a list of default responses from which we can pick
     * if we don't know what else to say.
     */
    private void fillDefaultResponses()
    {
        Charset charset = Charset.forName("US-ASCII");
        Path path = Paths.get(FILE_OF_DEFAULT_RESPONSES);
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) 
        {
            String fullResponse = "";
            String response = reader.readLine();
            String nextLine = reader.readLine();
            while(response != null) 
            {
                if (nextLine == null) {
                    break;
                }
                if (response.length() != 0 && nextLine.length() == 0) {
                    fullResponse += response;
                    defaultResponses.add(fullResponse);
                    fullResponse = "";
                } else if (response.length() == 0 && nextLine.length() != 0) {
                    if (fullResponse.length() != 0) {
                        defaultResponses.add(fullResponse);
                        fullResponse = "";
                    }
                    fullResponse += nextLine;
                    response = reader.readLine();
                    nextLine = reader.readLine();
                } else if (response.length() != 0 && nextLine.length() != 0) {
                    fullResponse += response + nextLine;
                }
                response = reader.readLine();
                nextLine = reader.readLine();
            }
        }
        catch(FileNotFoundException e) {
            System.err.println("Unable to open " + FILE_OF_DEFAULT_RESPONSES);
        }
        catch(IOException e) {
            System.err.println("A problem was encountered reading " +
                               FILE_OF_DEFAULT_RESPONSES);
        }
        // Make sure we have at least one response.
        if(defaultResponses.size() == 0) {
            defaultResponses.add("Could you elaborate on that?");
        }
    }

    /**
     * Randomly select and return one of the default responses.
     * @return     A random default response
     */
    private String pickDefaultResponse()
    {
        // Pick a random number for the index in the default response list.
        // The number will be between 0 (inclusive) and the size of the list (exclusive).
        int index = randomGenerator.nextInt(defaultResponses.size());
        return defaultResponses.get(index);
    }
}
