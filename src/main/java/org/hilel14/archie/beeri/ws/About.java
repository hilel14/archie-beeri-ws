package org.hilel14.archie.beeri.ws;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author hilel14
 */
@Path("about")
public class About {

    static final Logger LOGGER = LoggerFactory.getLogger(About.class);

    @Context
    private Configuration configuration;

    @PermitAll
    @GET
    @Path("configuration")
    @Produces(MediaType.TEXT_PLAIN)
    public String getConfiguration() throws Exception {
        configuration.getPropertyNames().forEach((key) -> {
            LOGGER.debug("{} = {}", key, configuration.getProperty(key));
        });
        String archieEnvironment = System.getProperty("archie.environment");
        return "Archie Beeri WS running in " + archieEnvironment + " environment";
    }

}
