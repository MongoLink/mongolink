/*
 * MongoLink, Object Document Mapper for Java and MongoDB
 *
 * Copyright (c) 2012, Arpinum or third-party contributors as
 * indicated by the @author tags
 *
 * MongoLink is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MongoLink is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the Lesser GNU General Public License
 * along with MongoLink.  If not, see <http://www.gnu.org/licenses/>. 
 *
 */

package fr.bodysplash.mongolink.test;

import fr.bodysplash.mongolink.MongoSession;
import fr.bodysplash.mongolink.MongoSessionManager;
import fr.bodysplash.mongolink.Settings;
import fr.bodysplash.mongolink.domain.criteria.RestrictionFactory;
import fr.bodysplash.mongolink.domain.criteria.Restrictions;
import fr.bodysplash.mongolink.domain.mapper.ContextBuilder;
import fr.bodysplash.mongolink.test.criteria.FakeRestrictonFactory;
import org.junit.rules.ExternalResource;

public class FakePersistentContext extends ExternalResource {

    public FakePersistentContext(String packageToScan) {
        this.packageToScan = packageToScan;
    }

    @Override
    protected void before() throws Throwable {
        ContextBuilder context = new ContextBuilder(packageToScan);
        final MongoSessionManager manager = MongoSessionManager.create(context, Settings.defaultInstance()
                .withDbFactory(FakeDBFactory.class)
                .withCriteriaFactory(FakeCriteriaFactory.class));
        session = manager.createSession();
        Restrictions.setFactory(new FakeRestrictonFactory());
    }

    @Override
    protected void after() {
        Restrictions.setFactory(new RestrictionFactory());
    }

    public MongoSession getSession() {
        return session;
    }

    private MongoSession session;
    private String packageToScan;
}
