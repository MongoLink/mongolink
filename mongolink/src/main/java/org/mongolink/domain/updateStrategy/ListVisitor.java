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

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class ListVisitor extends Visitor {

    public ListVisitor(final DbObjectDiff dbObjectDiff, final List origin) {
        super(dbObjectDiff, origin);
    }

    @Override
    public void visit(final Object target) {
        final List targetList = (List) target;
        final List workingList = getOrigin();
        removeIfNeeded(targetList, workingList);
        Iterator targetIterator = targetList.iterator();
        Iterator originIterator = workingList.iterator();
        int index = 0;
        while (targetIterator.hasNext() && originIterator.hasNext()) {
            compare(index, originIterator.next(), targetIterator.next());
            index++;
        }
        addNewElements(targetIterator);
    }

    private void removeIfNeeded(List targetList, List workingList) {
        if(targetList.size() != workingList.size()) {
            final Set<Object> toDelete = (Set<Object>) getOrigin().stream().filter(e -> !targetList.contains(e)).collect(Collectors.toSet());
            toDelete.forEach(getDbObjectDiff()::addPull);
            workingList.removeAll(toDelete);
        }
    }

    private void compare(int index, Object originElement, Object targetElement) {
        if (!Objects.equal(originElement, targetElement)) {
            getDbObjectDiff().pushKey(String.valueOf(index));
            getDbObjectDiff().addSet(targetElement);
            getDbObjectDiff().popKey();
        }
    }

    private void addNewElements(Iterator<Object> targetIterator) {
        ArrayList<Object> newElements = Lists.newArrayList(targetIterator);
        if (newElements.size() == 1) {
            getDbObjectDiff().addPush(newElements.get(0));
        }
        if (newElements.size() > 1) {
            getDbObjectDiff().addPushAll(newElements);
        }
    }

    @Override
    protected List getOrigin() {
        return (List) super.getOrigin();
    }

}
