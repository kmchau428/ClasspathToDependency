import com.owlike.genson.ext.jaxrs.GensonJsonConverter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import javax.ws.rs.core.MediaType;

public class DependencyGroupSearcher {

    public static void findGroup(String artifactId, String version) {
        Client client = Client.create();

//        WebResource webResource = client
//                .resource("https://search.maven.org/solrsearch/select?q=a:\"jersey-client\" AND v:\"1.19.4\" AND p:\"jar\"&rows=20&wt=json");


        ClientConfig cfg = new DefaultClientConfig(GensonJsonConverter.class);
        //Client client = Client.create(cfg);
        WebResource webResource = client.resource("https://search.maven.org/solrsearch/select?q=a:%22jersey-client%22%20AND%20v:%221.19.4%22%20AND%20p:%22jar%22&rows=20&wt=json");

        Object pojo = webResource
                .accept(MediaType.APPLICATION_JSON)
                .get(Object.class);

        System.out.println(pojo);
    }



}
