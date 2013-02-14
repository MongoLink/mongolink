package org.mongolink.domain.mapper;

public class NotCapped extends Capped {

    @Override
    public boolean isCapped() {
        return false;
    }
}
