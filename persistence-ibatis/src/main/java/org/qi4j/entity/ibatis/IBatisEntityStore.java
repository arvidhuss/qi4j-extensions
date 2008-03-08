/*  Copyright 2008 Edward Yakop.
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
package org.qi4j.entity.ibatis;

import com.ibatis.sqlmap.client.SqlMapClient;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import static org.qi4j.composite.NullArgumentException.validateNotNull;
import org.qi4j.entity.EntitySession;
import org.qi4j.service.Activatable;
import org.qi4j.spi.composite.CompositeBinding;
import org.qi4j.spi.entity.EntityState;
import org.qi4j.spi.entity.EntityStore;
import org.qi4j.spi.entity.StoreException;
import org.qi4j.spi.structure.ServiceDescriptor;

/**
 * @author edward.yakop@gmail.com
 */
final class IBatisEntityStore
    implements EntityStore, Activatable
{
    private final ServiceDescriptor serviceDescriptor;
    private SqlMapClient client;

    /**
     * Construct a new instance of {@code IBatisEntityStore}.
     *
     * @param aServiceDescriptor The service descriptor. This argument must not be {@code null}.
     * @throws IllegalArgumentException Thrown if the specified {@code aServiceDescriptor} argument is {@code null}.
     * @since 0.1.0
     */
    IBatisEntityStore( ServiceDescriptor aServiceDescriptor )
        throws IllegalArgumentException
    {
        validateNotNull( "aServiceDescriptor", aServiceDescriptor );

        serviceDescriptor = aServiceDescriptor;
        client = null;
    }

    /**
     * Returns {@code true}  if the specified {@code compositeType} for the specified {@code identity} is found,
     * {@code false} otherwise.
     *
     * @param anIdentity        The identity.
     * @param aCompositeBinding The composite binding. This argument must not be {@code null}.
     * @return A {@code boolean} indicator whether there exists a composite type with the specified identity.
     * @throws StoreException Thrown if retrieval failed or this method is invoked when this service is not active.
     * @since 0.1.0
     */
    public boolean exists( final String anIdentity, CompositeBinding aCompositeBinding )
        throws StoreException
    {
        throwIfNotActive();

        // TODO: Figure out how to retrieve the query name
        final String statementId = "existStatentId";

        IBatisTemplate<Boolean> template = new IBatisTemplate<Boolean>( client )
        {
            protected Boolean onExecute( SqlMapClient aClient )
                throws SQLException
            {
                return aClient.queryForObject( statementId, anIdentity ) != null;
            }
        };

        return template.execute();
    }

    private void throwIfNotActive()
        throws StoreException
    {
        if( client == null )
        {
            String message = "Possibly bug in the qi4j where the store is not activate but its service is invoked.";
            throw new StoreException( message );
        }
    }

    public EntityState newEntityInstance(
        EntitySession session, String identity, CompositeBinding compositeBinding, Map<String, Object> propertyValues )
        throws StoreException
    {
        throwIfNotActive();

        // TODO
        return null;
    }

    public EntityState getEntityInstance( EntitySession session, String identity, CompositeBinding compositeBinding )
        throws StoreException
    {
        throwIfNotActive();

        // TODO
        return null;
    }

    public void complete( EntitySession session, List<EntityState> states )
        throws StoreException
    {
        throwIfNotActive();

        // TODO
    }

    public final void activate()
        throws Exception
    {
        // Initialize client
    }

    public final void passivate()
        throws Exception
    {
        // clean up client
        client = null;
    }
}