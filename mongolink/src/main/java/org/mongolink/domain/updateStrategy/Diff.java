package org.mongolink.domain.updateStrategy;

import com.google.common.collect.*;

import java.util.*;

public class Diff {

    public static Iterable<Object> diff(Collection<?> origin, Collection<?> newVersion) {
        return Iterables.mergeSorted(Lists.newArrayList(origin, newVersion), (o, o2) -> ((Integer) o.hashCode()).compareTo(o2.hashCode()));
    }
}
