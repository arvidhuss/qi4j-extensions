/*
 * Copyright 2009 Paul Merlin.
 * Copyright 2011 Niclas Hedhman.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.entitystore.hazelcast;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.UrlXmlConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import org.qi4j.api.configuration.Configuration;
import org.qi4j.api.entity.EntityDescriptor;
import org.qi4j.api.entity.EntityReference;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.service.Activatable;
import org.qi4j.io.Input;
import org.qi4j.io.Output;
import org.qi4j.io.Receiver;
import org.qi4j.io.Sender;
import org.qi4j.spi.entitystore.EntityNotFoundException;
import org.qi4j.spi.entitystore.EntityStoreException;
import org.qi4j.spi.entitystore.helpers.MapEntityStore;
import org.qi4j.spi.entitystore.helpers.MapEntityStoreMixin;

import java.io.*;
import java.util.Map;

/**
 *
 */
public abstract class HazelcastEntityStoreMixin extends MapEntityStoreMixin
    implements Activatable, HazelcastEntityStoreService, MapEntityStore
{

    private static final String DEFAULT_MAPNAME = "qi4j:entitystore:data";

    @This
    private Configuration<HazelcastConfiguration> config;

    private IMap<String, String> stringMap;
    private HazelcastInstance hazelcastInstance;

    public void activate()
        throws Exception
    {
        HazelcastConfiguration configuration = config.configuration();
        Config conf = createConfig( configuration );
        hazelcastInstance = Hazelcast.newHazelcastInstance( conf );
        String mapName = DEFAULT_MAPNAME;
        if( configuration != null && configuration.mapName() != null )
        {
            mapName = configuration.mapName().get();
        }
        stringMap = hazelcastInstance.getMap( mapName );
    }

    public void passivate()
        throws Exception
    {
        stringMap = null;
    }

    @Override
    public HazelcastInstance hazelcastInstanceUsed()
    {
        return hazelcastInstance;
    }

    @Override
    public IMap hazelcastMapUsed()
    {
        return stringMap;
    }

    public Reader get( EntityReference ref )
        throws EntityStoreException
    {
        final String serializedState = stringMap.get( ref.identity() );
        if( serializedState == null )
        {
            throw new EntityNotFoundException( ref );
        }
        return new StringReader( serializedState );
    }

    public void applyChanges( MapChanges changes )
        throws IOException
    {
        changes.visitMap( new MapChanger()
        {

            public Writer newEntity( final EntityReference ref, EntityDescriptor entityDescriptor )
                throws IOException
            {
                return new StringWriter( 1000 )
                {

                    @Override
                    public void close()
                        throws IOException
                    {
                        super.close();
                        stringMap.put( ref.identity(), toString() );
                    }
                };
            }

            public Writer updateEntity( EntityReference ref, EntityDescriptor entityDescriptor )
                throws IOException
            {
                return newEntity( ref, entityDescriptor );
            }

            public void removeEntity( EntityReference ref, EntityDescriptor entityDescriptor )
                throws EntityNotFoundException
            {
                stringMap.remove( ref.identity() );
            }
        } );
    }

    public Input<Reader, IOException> entityStates()
    {
        return new Input<Reader, IOException>()
        {
            @Override
            public <ReceiverThrowableType extends Throwable> void transferTo( Output<? super Reader, ReceiverThrowableType> output )
                throws IOException, ReceiverThrowableType
            {
                output.receiveFrom( new Sender<Reader, IOException>()
                {
                    @Override
                    public <ReceiverThrowableType extends Throwable> void sendTo( Receiver<? super Reader, ReceiverThrowableType> receiver )
                        throws ReceiverThrowableType, IOException
                    {
                        for( Map.Entry<String, String> eachEntry : stringMap.entrySet() )
                        {
                            receiver.receive( new StringReader( eachEntry.getValue() ) );
                        }
                    }
                } );
            }
        };
    }

    private Config createConfig( HazelcastConfiguration configuration )
        throws IOException
    {
        String hzConfLocation = configuration.configXmlLocation().get();
        if( hzConfLocation == null )
        {
            hzConfLocation = "org/qi4j/entitystore/hazelcast/defaultConfiguration.xml";
        }
        Config conf;
        if( hzConfLocation.contains( ":" ) )
        {
            conf = new UrlXmlConfig( hzConfLocation );
        }
        else
        {
            conf = new ClasspathXmlConfig( hzConfLocation );
        }
        return conf;
    }
}
