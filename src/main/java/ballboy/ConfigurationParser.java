package ballboy;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * A basic JSON parser.
 */
public class ConfigurationParser {
    /**
     * @param name The file name, relative to the resource directory.
     * @return JSONObject The parsed file.
     * @throws ConfigurationParseException Thrown in the instance of any parsing errors, with user-facing messages.
     */
    public JSONObject parseConfig(String name) {
        try {
            JSONParser parser = new JSONParser();

            Optional<URI> configUrl = Optional.ofNullable(getClass().getResource("/" + name).toURI());
            if (configUrl.isEmpty()) {
                throw new FileNotFoundException();
            }
            FileReader fileReader = new FileReader(configUrl.map(url -> url.getPath()).get());
            Object parsedFile = parser.parse(fileReader);
            return (JSONObject) parsedFile;
        } catch (FileNotFoundException | URISyntaxException e) {
            throw new ConfigurationParseException(String.format("Configuration file (%s) not found \n", name));
        } catch (ParseException | IOException e) {
            throw new ConfigurationParseException(String.format("Error reading configuration file | %s", e));
        }
    }

}


