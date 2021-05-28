package org.rand;

import org.rand.aio.AioServer;

/**
 * Hello world!
 *
 */
public class AioApplication
{
    public static void main( String[] args )
    {
        AioServer.run(AioApplication.class, args);
    }
}
