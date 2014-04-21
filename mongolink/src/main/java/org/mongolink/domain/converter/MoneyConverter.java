package org.mongolink.domain.converter;

import org.joda.money.Money;

public class MoneyConverter extends Converter {
    @Override
    public Object toDbValue(Object value) {
        return value.toString();
    }

    @Override
    public Object fromDbValue(Object value) {
        return Money.parse((String) value);
    }
}
