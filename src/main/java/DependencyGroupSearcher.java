import com.owlike.genson.ext.jaxrs.GensonJsonConverter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import javax.ws.rs.core.MediaType;
import java.util.Scanner;

public class DependencyGroupSearcher {

    public static String findGroupId(String artifactId, String version) {
        Client client = Client.create();

        String endpoint = "https://search.maven.org/solrsearch/select?q=a:%22" + artifactId + "%22%20AND%20v:%22" + version + "%22&wt=json";
//        String endpoint = "https://search.maven.org/solrsearch/select?q=a:%22" + "maven-ear-plugin" + "%22&wt=json";
        System.out.println("resolving the groupId: " + endpoint);
        WebResource webResource = client.resource(endpoint);

        String resp = webResource
                .accept(MediaType.APPLICATION_JSON)
                .get(Object.class)
                .toString();

        String groupId = "unclassified";
        int resultIdxStart = resp.indexOf("docs=");
        int resultIdxEnd = resp.indexOf("}]", resultIdxStart);
        if (resultIdxEnd < 0) { //not found
            System.out.println("The groupId of the jar " + artifactId + "-" + version + " cannot be found");
        }
        else {
            String[] results = resp.substring(resultIdxStart + "docs=".length(), resp.indexOf("}]", resultIdxStart)).split("},");
            if (results.length > 1) {
                String groupIdCandidates = "";

                for (String s : results) {
                    int groupIdIdx = s.indexOf("g=");
                    groupIdCandidates += s.substring(groupIdIdx+2, s.indexOf(",",groupIdIdx)) + "\n";
                }
                System.out.println("Multiple groupIds found: please choose one from:\n" + groupIdCandidates);
                Scanner reader = new Scanner(System.in);
                System.out.println("Enter the groupId name: ");
                groupId = reader.next();

            }
            else {
                int groupIdIdx = resp.indexOf("g=");
                if (groupIdIdx < 0) { //not found
                    System.out.println("The groupId of the jar " + artifactId + "-" + version + " cannot be found");
                }
                else {
                    groupId = resp.substring(groupIdIdx+2, resp.indexOf(",",groupIdIdx));
                }
            }
        }

        return groupId;
    }

}
