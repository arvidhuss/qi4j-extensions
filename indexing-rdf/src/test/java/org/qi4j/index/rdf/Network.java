/*
 * Copyright 2008 Alin Dreghiciu.
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
package org.qi4j.index.rdf;

import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.unitofwork.UnitOfWorkCompletionException;
import org.qi4j.api.value.ValueBuilder;
import org.qi4j.api.value.ValueBuilderFactory;
import org.qi4j.bootstrap.SingletonAssembler;
import org.qi4j.index.rdf.model.Account;
import org.qi4j.index.rdf.model.Address;
import org.qi4j.index.rdf.model.Cat;
import org.qi4j.index.rdf.model.City;
import org.qi4j.index.rdf.model.Domain;
import org.qi4j.index.rdf.model.Female;
import org.qi4j.index.rdf.model.Male;
import org.qi4j.index.rdf.model.Person;
import org.qi4j.index.rdf.model.Protocol;
import org.qi4j.index.rdf.model.QueryParam;
import org.qi4j.index.rdf.model.URL;
import org.qi4j.index.rdf.model.entities.CatEntity;
import org.qi4j.index.rdf.model.entities.FemaleEntity;
import org.qi4j.index.rdf.model.entities.MaleEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * JAVADOC Add JavaDoc
 *
 */
class Network
{
    static void populate( SingletonAssembler assembler )
        throws UnitOfWorkCompletionException
    {
        UnitOfWork unitOfWork = assembler.unitOfWorkFactory().newUnitOfWork();
        ValueBuilderFactory valueBuilderFactory = assembler.valueBuilderFactory();

        NameableAssert.clear();
        Domain gaming;
        {
            EntityBuilder<Domain> domainBuilder = unitOfWork.newEntityBuilder( Domain.class, "Gaming" );
            gaming = domainBuilder.instance();
            gaming.name().set( "Gaming" );
            gaming.description().set( "Gaming domain" );
            gaming = domainBuilder.newInstance();
            NameableAssert.trace( gaming );
        }

        Domain programming;
        {
            EntityBuilder<Domain> domainBuilder = unitOfWork.newEntityBuilder( Domain.class );
            programming = domainBuilder.instance();
            programming.name().set( "Programming" );
            programming.description().set( "Programing domain" );
            programming = domainBuilder.newInstance();
            NameableAssert.trace( programming );
        }

        Domain cooking;
        {
            EntityBuilder<Domain> domainBuilder = unitOfWork.newEntityBuilder( Domain.class );
            cooking = domainBuilder.instance();
            cooking.name().set( "Cooking" );
            cooking.description().set( "Cooking domain" );
            cooking = domainBuilder.newInstance();
            NameableAssert.trace( cooking );
        }

        Domain cars;
        {
            EntityBuilder<Domain> domainBuilder = unitOfWork.newEntityBuilder( Domain.class );
            cars = domainBuilder.instance();
            cars.name().set( "Cars" );
            cars.description().set( "Cars" );
            cars = domainBuilder.newInstance();
            NameableAssert.trace( cars );
        }

        City kualaLumpur;
        {
            EntityBuilder<City> cityBuilder = unitOfWork.newEntityBuilder( City.class );
            kualaLumpur = cityBuilder.instance();
            kualaLumpur.name().set( "Kuala Lumpur" );
            kualaLumpur.country().set( "Malaysia" );
            kualaLumpur.county().set( "Some Jaya" );
            kualaLumpur = cityBuilder.newInstance();
            NameableAssert.trace( kualaLumpur );
        }

        City penang;
        {
            EntityBuilder<City> cityBuilder = unitOfWork.newEntityBuilder( City.class );
            penang = cityBuilder.instance();
            penang.name().set( "Penang" );
            penang.country().set( "Malaysia" );
            penang.county().set( "Some Other Jaya" );
            penang = cityBuilder.newInstance();
            NameableAssert.trace( penang );
        }

        Account annsAccount;
        {
            EntityBuilder<Account> accountBuilder = unitOfWork.newEntityBuilder( Account.class );
            annsAccount = accountBuilder.instance();
            annsAccount.number().set( "accountOfAnnDoe" );
            annsAccount = accountBuilder.newInstance();
        }

        Account jacksAccount;
        {
            EntityBuilder<Account> accountBuilder = unitOfWork.newEntityBuilder( Account.class );
            jacksAccount = accountBuilder.instance();
            jacksAccount.number().set( "accountOfJackDoe" );
            jacksAccount = accountBuilder.newInstance();
        }

        ValueBuilder<Address> addressBuilder = valueBuilderFactory.newValueBuilder( Address.class );
        Address address = addressBuilder.prototype();
        address.line1().set( "Qi Street 4j" );
        address.line2().set( "Off main Java Street" );
        address.zipcode().set( "12345" );

        Female annDoe;
        {
            EntityBuilder<FemaleEntity> femaleBuilder = unitOfWork.newEntityBuilder( FemaleEntity.class );
            annDoe = femaleBuilder.instance();
            annDoe.name().set( "Ann Doe" );
            annDoe.title().set( Person.Title.MRS );
            annDoe.placeOfBirth().set( kualaLumpur );
            annDoe.yearOfBirth().set( 1975 );
            annDoe.interests().add( 0, cooking );
            annDoe.password().set( "passwordOfAnnDoe" );
            annDoe.mainAccount().set( annsAccount );
            annDoe.accounts().add( 0, annsAccount );
            annDoe.accounts().add( 0, jacksAccount );
            annDoe.address().set( addressBuilder.newInstance() );
            annDoe = femaleBuilder.newInstance();
            NameableAssert.trace( annDoe );
        }

        {

            EntityBuilder<MaleEntity> maleBuilder = unitOfWork.newEntityBuilder( MaleEntity.class );
            Male joeDoe = maleBuilder.instance();
            joeDoe.name().set( "Joe Doe" );
            joeDoe.title().set( Person.Title.MR );
            joeDoe.placeOfBirth().set( kualaLumpur );
            joeDoe.yearOfBirth().set( 1990 );
            joeDoe.mother().set( annDoe );
            joeDoe.interests().add( 0, programming );
            joeDoe.interests().add( 0, gaming );
            joeDoe.email().set( "joe@thedoes.net" );
            joeDoe.password().set( "passwordOfJoeDoe" );
            joeDoe = maleBuilder.newInstance();
            address.line1().set( "Qi Alley 4j" );
            joeDoe.address().set( addressBuilder.newInstance() );
            NameableAssert.trace( joeDoe );
        }

        {
            EntityBuilder<MaleEntity> maleBuilder = unitOfWork.newEntityBuilder( MaleEntity.class );
            Male jackDoe = maleBuilder.instance();
            jackDoe.name().set( "Jack Doe" );
            jackDoe.title().set( Person.Title.DR );
            jackDoe.placeOfBirth().set( penang );
            jackDoe.yearOfBirth().set( 1970 );
            jackDoe.interests().add( 0, cars );
            jackDoe.wife().set( annDoe );
            jackDoe.password().set( "passwordOfJohnDoe" );
            jackDoe.mainAccount().set( jacksAccount );
            jackDoe.accounts().add( 0, annsAccount );
            jackDoe.accounts().add( 0, jacksAccount );
            address.line1().set( "Qi Avenue 4j" );
            jackDoe.address().set( addressBuilder.newInstance() );

            ValueBuilder<URL> urlBuilder = valueBuilderFactory.newValueBuilder( URL.class );
            ValueBuilder<Protocol> protocolBuilder = valueBuilderFactory.newValueBuilder( Protocol.class );
            ValueBuilder<QueryParam> queryParamBuilder = valueBuilderFactory.newValueBuilder( QueryParam.class );

            Protocol protocol = protocolBuilder.prototype();
            protocol.value().set( "http" );

            List<QueryParam> queryParams = new ArrayList<QueryParam>();
            QueryParam param = queryParamBuilder.prototype();
            param.name().set( "user" );
            param.value().set( "jackdoe" );
            queryParams.add( queryParamBuilder.newInstance() );
            param.name().set( "password" );
            param.value().set( "somepassword" );
            queryParams.add( queryParamBuilder.newInstance() );

            URL url = urlBuilder.prototype();
            url.protocol().set( protocolBuilder.newInstance() );
            url.queryParams().set( queryParams );

            jackDoe.personalWebsite().set( urlBuilder.newInstance() );

            jackDoe = maleBuilder.newInstance();
            NameableAssert.trace( jackDoe );
        }

        {
            EntityBuilder<CatEntity> catBuilder = unitOfWork.newEntityBuilder( CatEntity.class );
            Cat felix = catBuilder.instance();
            felix.name().set( "Felix" );
            felix = catBuilder.newInstance();
            NameableAssert.trace( felix );
        }

        unitOfWork.complete();
    }
}