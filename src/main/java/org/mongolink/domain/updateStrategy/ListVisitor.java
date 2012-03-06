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

package org.mongolink.domain.updateStrategy;

import com.mongodb.BasicDBList;

/**
 * @author jb
 */
public class ListVisitor extends Visitor {

    public ListVisitor(final DbObjectDiff dbObjectDiff, final BasicDBList origin) {
        super(dbObjectDiff, origin);
    }

    @Override
    public void visit(final Object target) {
        final BasicDBList targetList = (BasicDBList) target;
        if (getOrigin().size() == targetList.size()) {
            return;
        } else if (getOrigin().size() > targetList.size()) {
            compareDeletedElementsInList(targetList);
        } else {
            getDbObjectDiff().addPush(targetList.get(targetList.size() - 1));
        }
    }

    private void compareDeletedElementsInList(final BasicDBList target) {
        int targetIndex = 0;
        int originIndex = 0;
        while(originIndex < getOrigin().size()) {
            final Object current = getOrigin().get(originIndex);
            if (targetIndex < target.size()) {
                final Object currentTarget = target.get(targetIndex);
                if (current != currentTarget) {
                    removeElementAt(originIndex);
                } else {
                    targetIndex++;
                }
            } else {
                removeElementAt(originIndex);
            }
            originIndex++;
        }

        getDbObjectDiff().addPull(null);
    }


    private void removeElementAt(final int i) {
        getDbObjectDiff().pushKey(String.valueOf(i));
        getDbObjectDiff().addUnset();
        getDbObjectDiff().popKey();
    }


    @Override
    protected BasicDBList getOrigin() {
        return (BasicDBList) super.getOrigin();
    }

}
