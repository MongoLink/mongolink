package org.mongolink.domain.updateStrategy;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBList;

import java.util.Comparator;

public class Diff {

    public static Iterable<Object> diff(BasicDBList origin, BasicDBList newVersion) {
        return Iterables.mergeSorted(Lists.newArrayList(origin, newVersion), new Comparator<Object>() {
            @Override
            public int compare(Object o, Object o2) {
                return ((Integer) o.hashCode()).compareTo(o2.hashCode());
            }
        });

    }
}
