package org.rand.aio.core.handler.dispatcher;


import org.rand.aio.core.protocol.AioProtocol;

import java.util.Collection;
import java.util.List;

public class DefaultProtocolManager {

    private Collection<AioProtocol> protocols;

    public DefaultProtocolManager(Collection<AioProtocol> protocols){
        this.protocols=protocols;
    }


    public Collection<AioProtocol> getProtocols() {
        return protocols;
    }

    public void setProtocols(Collection<AioProtocol> protocols) {
        this.protocols = protocols;
    }

    public void addProtocol(AioProtocol aioProtocol){
        protocols.add(aioProtocol);
    }

    public void addProtocols(List<AioProtocol> protocols){
        protocols.addAll(protocols);
    }

}
