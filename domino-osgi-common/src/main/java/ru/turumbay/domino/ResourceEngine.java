package ru.turumbay.domino;

import com.ibm.domino.osgi.core.context.ContextInfo;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;


public abstract class ResourceEngine {
    private final static Logger logger = LoggerFactory.getLogger(ResourceEngine.class);

    protected HttpService httpService;

    protected abstract String getResourceAlias();

    protected abstract String getResourceName();

    @SuppressWarnings("unused")
    protected void bindHttpService(HttpService httpService) throws ServletException, NamespaceException {
        logger.debug("HttpService is here");
        if (this.httpService == null){
            this.httpService = httpService;
            try {
                ContextInfo.registerResourcesHttpService(getResourceAlias(), getResourceName(), httpService.createDefaultHttpContext());
            }catch (NoClassDefFoundError ignore) {
                logger.info("Running outside Domino Server?");
                httpService.registerResources(getResourceAlias(), getResourceName(), httpService.createDefaultHttpContext());
            }

        }
    }

    @SuppressWarnings("unused")
    protected void unbindHttpService(HttpService httpService){
        logger.debug("HttpService goes out");
        try {
            ContextInfo.unregisterHttpService(getResourceAlias());
        } catch (NoClassDefFoundError ignore) {
            httpService.unregister(getResourceAlias());
        }
        this.httpService = null;
    }
}
