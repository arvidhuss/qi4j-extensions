package org.qi4j.entity.rmi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import org.qi4j.composite.AppliesTo;
import org.qi4j.composite.scope.This;
import org.qi4j.service.Activatable;

/**
 * Create and delegate to a RMI registry.
 */
@AppliesTo( { Registry.class, Activatable.class } )
public class RegistryMixin
    implements InvocationHandler, Activatable
{
    Registry registry;

    @This RegistryConfiguration config;

    public void activate() throws Exception
    {
        try
        {
            Integer port = config.port().get();
            registry = LocateRegistry.createRegistry( port );
        }
        catch( RemoteException e )
        {
            registry = LocateRegistry.getRegistry();
        }
    }

    public void passivate() throws Exception
    {
        UnicastRemoteObject.unexportObject( registry, true );
    }

    public Object invoke( Object o, Method method, Object[] objects ) throws Throwable
    {
        return method.invoke( registry, objects );
    }

}