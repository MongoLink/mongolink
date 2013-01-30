package org.mongolink.domain.converter;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Assert;
import org.junit.Test;

import static org.fest.assertions.Assertions.*;
import static org.hamcrest.CoreMatchers.*;

public class TestsMoneyConverter {


    @Test
    public void canGetMoneyConverter() throws NoSuchMethodException {
        Converter converter = Converter.forMethod(EntityWithMoney.class.getMethod("getMoney"));

        assertThat(converter).isNotNull();
        assertThat(converter).isInstanceOf(MoneyConverter.class);
    }

    @Test
    public void canSerialize() {
        Money money= Money.of(CurrencyUnit.EUR, 12);

        Object value = new MoneyConverter().toDbValue(money);

        Assert.assertThat((String) value, is(money.toString()));
    }

    @Test
    public void canDeserialize() {
        Money money = Money.of(CurrencyUnit.EUR, 33);

        Object value = new MoneyConverter().fromDbValue(money.toString());

        Assert.assertThat(value, notNullValue());
        Assert.assertThat((Money) value, is(money));
    }

    private class EntityWithMoney {

        private Money money;

        public Money getMoney() {
            return money;
        }

        public void setMoney(Money money) {
            this.money = money;
        }
    }
}
