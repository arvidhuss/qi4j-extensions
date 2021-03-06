/*
 * Copyright (c) 2009, Rickard Öberg. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.qi4j.migration.assembly;

import org.qi4j.migration.Migrator;
import org.qi4j.spi.entitystore.helpers.StateStore;

import java.io.IOException;

/**
 * Non-entity specific migration operation. These operations
 * may perform anything necessary to migrate the application. This
 * could include performing disk operations and other non-entity related
 * tasks.
 */
public interface MigrationOperation
{
    void upgrade( StateStore stateStore, Migrator migrator )
        throws IOException;

    void downgrade( StateStore stateStore, Migrator migrator )
        throws IOException;
}