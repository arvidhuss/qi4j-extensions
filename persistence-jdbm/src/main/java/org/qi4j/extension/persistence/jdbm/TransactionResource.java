/*  Copyright 2007 Niclas Hedhman.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.extension.persistence.jdbm;

import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import jdbm.RecordManager;
import org.qi4j.entity.EntityComposite;
import org.qi4j.entity.EntityCompositeNotFoundException;
import org.qi4j.entity.PersistenceException;
import org.qi4j.runtime.ProxyReferenceInvocationHandler;
import org.qi4j.runtime.composite.CompositeInstance;


public class TransactionResource
    implements XAResource
{
    private List<Operation> operations;
    private Xid xid;
    private RecordManager recordManager;

    TransactionResource( RecordManager recordManager )
    {
        this.recordManager = recordManager;
        operations = new LinkedList<Operation>();
    }

    void create( EntityComposite composite )
    {
        operations.add( new CreateOperation( composite ) );
    }

    public void read( EntityComposite aProxy )
    {
        String objectId = aProxy.getIdentity();
        try
        {
            long recordId = recordManager.getNamedObject( objectId );
            if( recordId == 0 )
            {
                // Here we need to check the "last value" in the Transaction log. Should this be built on the calls instead?
                String identity = aProxy.getIdentity();
                CompositeInstance handler = CompositeInstance.getCompositeInstance( aProxy.dereference() );
                Object[] mixins = handler.getMixins();

                for( Operation op : operations )
                {
                    op.playback( identity, mixins );
                }
                if( mixins.length == 0 )
                {
                    throw new EntityCompositeNotFoundException( "Object with identity " + objectId + " does not exist" );
                }
            }
            else
            {
                Map<Class, Serializable> mixins = (Map<Class, Serializable>) recordManager.fetch( recordId );
                if( mixins == null )
                {
                    throw new EntityCompositeNotFoundException( "Object with identity " + objectId + " does not exist" );
                }

                ProxyReferenceInvocationHandler proxyHandler = (ProxyReferenceInvocationHandler) Proxy.getInvocationHandler( aProxy );
                CompositeInstance handler = CompositeInstance.getCompositeInstance( aProxy.dereference() );
//                Object[] existingMixins = handler.getMixins();
//                for( Object existingMixin : existingMixins )
//                {
//                    existingMixins.put( existingMixin.getClass(), existingMixin );
//                }
//                proxyHandler.initializeMixins( existingMixins );
            }
        }
        catch( EOFException e )
        {
            throw new EntityCompositeNotFoundException( "Object with identity " + objectId + " does not exist" );
        }
        catch( Exception e )
        {
            throw new PersistenceException( e );
        }
    }

    void update( EntityComposite composite, Serializable mixin )
    {
        operations.add( new UpdateOperation( composite, mixin ) );
    }

    void delete( EntityComposite composite )
    {
        operations.add( new DeleteOperation( composite ) );
    }

    // XAResource Implementation
    public void commit( Xid xid, boolean onePhaseCommit )
        throws XAException
    {
        if( !this.xid.equals( xid ) )
        {
            throw new InternalError( "commit( " + xid + ") != " + this.xid );
        }
        try
        {
            for( Operation op : operations )
            {
                op.perform( recordManager );
            }
            recordManager.commit();
        }
        catch( IOException e )
        {
            try
            {
                recordManager.rollback();
            }
            catch( IOException e1 )
            {
                e1.printStackTrace();
            }
            throw new XAException( "Unable to commit." );
        }
    }

    public void end( Xid xid, int i )
        throws XAException
    {
        if( this.xid.equals( xid ) )
        {
            return;
        }
        throw new InternalError( "end( " + xid + ", " + i + ") != " + this.xid );
    }

    public void forget( Xid xid )
        throws XAException
    {
        if( this.xid.equals( xid ) )
        {
            return;
        }
        throw new InternalError( "forget( " + xid + ") != " + this.xid );
    }

    public int getTransactionTimeout()
        throws XAException
    {
        return 0;
    }

    public boolean isSameRM( XAResource xaResource )
        throws XAException
    {
        return false;
    }

    public int prepare( Xid xid )
        throws XAException
    {
        if( this.xid.equals( xid ) )
        {
            return XAResource.XA_OK;
        }
        throw new InternalError( "prepare( " + xid + ") != " + this.xid );
    }

    public Xid[] recover( int i )
        throws XAException
    {
        return new Xid[0];
    }

    public void rollback( Xid xid )
        throws XAException
    {
        if( this.xid.equals( xid ) )
        {
            operations.clear();
            return;
        }
        throw new InternalError( "rollback( " + xid + ") != " + this.xid );
    }

    public boolean setTransactionTimeout( int i )
        throws XAException
    {
        return false;
    }

    public void start( Xid xid, int i )
        throws XAException
    {
        this.xid = xid;
    }

}
