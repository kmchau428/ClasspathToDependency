import com.owlike.genson.ext.jaxrs.GensonJsonConverter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import javax.ws.rs.core.MediaType;

public class DependencyGroupSearcher {

    public static String findGroupId(String artifactId, String version) {
        Client client = Client.create();

        String endpoint = "https://search.maven.org/solrsearch/select?q=a:%22" + artifactId + "%22%20AND%20v:%22" + version + "%22%20&rows=1&wt=json";
        System.out.println("resolving the groupId: " + endpoint);
        WebResource webResource = client.resource(endpoint);

        String resp = webResource
                .accept(MediaType.APPLICATION_JSON)
                .get(Object.class)
                .toString();

        int groupIdIdx = resp.indexOf("g=");
        String groupId = "unclassified";

        if (groupIdIdx < 0) { //not found
            System.out.println("The groupId of the jar " + artifactId + "-" + version + " cannot be found");
        }
        else {
            groupId = resp.substring(groupIdIdx+2, resp.indexOf(",",groupIdIdx));
        }

        return groupId;
    }

}
