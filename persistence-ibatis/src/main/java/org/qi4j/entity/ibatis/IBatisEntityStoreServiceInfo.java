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

import java.io.Serializable;
import java.util.Properties;
import static org.qi4j.composite.NullArgumentException.validateNotEmpty;

/**
 * {@code IBatisEntityStoreServiceInfo} provides service information for {@link IBatisEntityStore}.
 *
 * @author edward.yakop@gmail.com
 * @since 0.1.0
 */
public final class IBatisEntityStoreServiceInfo
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final String sqlMapConfigURL;
    private final Properties configProperties;
    private boolean isDebugMode;

    /**
     * Construct an instance of {@code IBatisServiceInfo}.
     *
     * @param aSQLMapConfigURL The sql map config URL. This argument must not be {@code null}.
     * @throws IllegalArgumentException Thrown if one or some or all arguments are {@code null}.
     * @since 0.1.0
     */
    public IBatisEntityStoreServiceInfo( String aSQLMapConfigURL )
        throws IllegalArgumentException
    {
        this( aSQLMapConfigURL, null );
    }

    /**
     * Construct an instance of {@code IBatisServiceInfo}.
     *
     * @param aSQLMapConfigURL The sql map config URL. This argument must not be {@code null}.
     * @param aProperties      The sql map config properties. This argument is optional.
     * @throws IllegalArgumentException Thrown if one or some or all arguments are {@code null}.
     * @since 0.1.0
     */
    public IBatisEntityStoreServiceInfo( String aSQLMapConfigURL, Properties aProperties )
        throws IllegalArgumentException
    {
        validateNotEmpty( "aSqlMapConfigURL", aSQLMapConfigURL );

        sqlMapConfigURL = aSQLMapConfigURL;
        configProperties = aProperties;
        isDebugMode = false;
    }

    /**
     * Sets whether the entity store service info is in debug mode.
     *
     * @param isNewDebugMode The new debug mode.
     * @since 0.1.0
     */
    public final void setIsDebugMode( boolean isNewDebugMode )
    {
        isDebugMode = isNewDebugMode;
    }

    /**
     * Returns {@code true} if the {@code IBatisEntityStore} represented by this {@code IBatisEntityStoreServiceInfo}
     * is in debug mode, {@code false} otherwise.
     *
     * @return Returns a {@code boolean} indicator whether the {@code IBatisEntityStore} represented by this
     *         {@code IBatisEntityStoreServiceInfo} is in debug mode.
     * @since 0.1.0
     */
    public final boolean isDebugMode()
    {
        return isDebugMode;
    }

    /**
     * Returns the sql map config url.
     *
     * @return The sql map config url.
     * @since 0.1.0
     */
    final String getSQLMapConfigURL()
    {
        return sqlMapConfigURL;
    }

    /**
     * Returns the config properties.
     *
     * @return The config properties.
     * @since 0.1.0
     */
    final Properties getConfigProperties()
    {
        return configProperties;
    }
}