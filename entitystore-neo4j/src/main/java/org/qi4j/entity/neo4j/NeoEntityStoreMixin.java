/* Copyright 2008 Neo Technology, http://neotechnology.com.
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
package org.qi4j.entity.neo4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.qi4j.composite.scope.Service;
import org.qi4j.entity.neo4j.state.NeoEntityState;
import org.qi4j.entity.neo4j.state.direct.DirectEntityStateFactory;
import org.qi4j.entity.neo4j.state.indirect.IndirectEntityStateFactory;
import org.qi4j.spi.entity.EntityState;
import org.qi4j.spi.entity.EntityStatus;
import org.qi4j.spi.entity.EntityStore;
import org.qi4j.spi.entity.EntityStoreException;
import org.qi4j.spi.entity.QualifiedIdentity;
import org.qi4j.spi.entity.StateCommitter;
import org.qi4j.spi.structure.CompositeDescriptor;
import org.qi4j.structure.Module;

/**
 * @author Tobias Ivarsson (tobias.ivarsson@neotechnology.com)
 */
public class NeoEntityStoreMixin implements EntityStore
{
    // Dependancies
    private @Service NeoIdentityService idService;
    private @Service DirectEntityStateFactory directFactory;
    private @Service( optional = true ) IndirectEntityStateFactory indirectFactory;
    private @Service NeoCoreService neo;

    // EntityStore implementation

    public EntityState newEntityState( CompositeDescriptor compositeDescriptor, QualifiedIdentity identity ) throws EntityStoreException
    {
        return factory().createEntityState( idService, compositeDescriptor, identity, EntityStatus.NEW );
    }

    public EntityState getEntityState( CompositeDescriptor compositeDescriptor, QualifiedIdentity identity ) throws EntityStoreException
    {
        return factory().createEntityState( idService, compositeDescriptor, identity, EntityStatus.LOADED );
    }

    public StateCommitter prepare( Iterable<EntityState> newStates, Iterable<EntityState> loadedStates, Iterable<QualifiedIdentity> removedStates, Module module ) throws EntityStoreException
    {
        List<NeoEntityState> updated = new ArrayList<NeoEntityState>();
        for( EntityState state : newStates )
        {
            updated.add( (NeoEntityState) state );
        }
        for( EntityState state : loadedStates )
        {
            NeoEntityState neoState = (NeoEntityState) state;
            // TODO: add a check to see if they are actually updated?
            updated.add( neoState );
        }
        return factory().prepareCommit( idService, updated, removedStates );
    }

    public Iterator<EntityState> iterator()
    {
        return factory().iterator();
    }

    // Implementation details

    private NeoEntityStateFactory factory()
    {
        if( neo.inTransaction() )
        {
            return directFactory;
        }
        else if( indirectFactory != null )
        {
            return indirectFactory;
        }
        else
        {
            return directFactory;
        }
    }
}