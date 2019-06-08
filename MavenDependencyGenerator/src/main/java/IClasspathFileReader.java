import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

public interface IClasspathFileReader {
    List<String> read(String filePath) throws ParserConfigurationException, IOException, SAXException;
}
