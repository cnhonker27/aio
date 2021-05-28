package org.rand.aio.core.decoder;

import java.util.List;

public class DefaultRequestDecoderManager implements Runnable {

    private List<AioRequestDecoder> decoders;

    @Override
    public void run() {
        for(AioRequestDecoder decoder:decoders){
            
        }
    }
}
