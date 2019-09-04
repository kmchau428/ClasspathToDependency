package dependency_generator.searcher;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import dependency_generator.Main;
import dependency_generator.dto.DependencyEntry;

import javax.ws.rs.core.MediaType;

public class PublicDependencySearcher implements IDependencySearcher, Cacheable{

    public DependencyEntry resolveDependency(String artifactId, String version, String checksum) {
        Client client = Client.create();

        boolean useChecksum = !checksum.contains("-");
        String endpoint = useChecksum ?
                "https://search.maven.org/solrsearch/select?q=1:" + checksum + "&wt=json" :
                "https://search.maven.org/solrsearch/select?q=a:%22" + artifactId + "%22%20AND%20v:%22" + version + "%22&wt=json"
                ;
        System.out.println("resolving the dependency: " + endpoint);
        System.out.println( "checksum:" + checksum);
        WebResource webResource = client.resource(endpoint);

        String resp = webResource
                .accept(MediaType.APPLICATION_JSON)
                .get(Object.class)
                .toString();

        String numFoundStr = "numFound=";
        int numFound = Integer.valueOf(resp.substring(
                resp.indexOf(numFoundStr)+numFoundStr.length(),
                resp.indexOf(",",resp.indexOf(numFoundStr)))
        );

        String versionStr = version.equals("") ? "" : "-" + version;
        if (numFound == 0) {
            System.out.println("The jar " + artifactId + versionStr + " cannot be found");
            Main.unresolvedJars.add(artifactId + versionStr + ".jar");

            return new DependencyEntry();
        }
        else {
            if (!useChecksum && numFound > 1) {
                System.out.println("The jar " + artifactId + versionStr + " cannot be resolved due to multiple groupIds found");
                Main.unresolvedJars.add(artifactId + versionStr + ".jar");

                return new DependencyEntry();
            }

            String g = resp.substring( resp.indexOf("g=")+2, resp.indexOf(",",resp.indexOf("g=")));
            String a = resp.substring( resp.indexOf("a=")+2, resp.indexOf(",",resp.indexOf("a=")));
            String v = resp.substring( resp.indexOf("v=")+2, resp.indexOf(",",resp.indexOf("v=")));

            if (!LocalCacheDependencySearcher.cache.containsKey(a+"-"+v))
                writeToCache(g, a, v);

            Main.resolvedByNameJars.add(a + "-" + v + ".jar");

            return new DependencyEntry(g,a,v);
        }
    }

}
