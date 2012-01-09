package fr.bodysplash.mongolink.domain;

import fr.bodysplash.mongolink.domain.updateStategy.DiffStrategy;
import fr.bodysplash.mongolink.domain.updateStategy.OverwriteStrategy;
import fr.bodysplash.mongolink.domain.updateStategy.UpdateStrategy;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

public class TestsUpdateStrategies {

    @Test
    public void canGetDiffStrategyInstance() {
        UpdateStrategy strat = UpdateStrategies.DIFF.instance();

        assertThat(strat, notNullValue());
        assertThat(strat, instanceOf(DiffStrategy.class));
    }

    @Test
    public void canGetOverwriteStrategyIntance() {
        UpdateStrategy strat = UpdateStrategies.OVERWRITE.instance();

        assertThat(strat, notNullValue());
        assertThat(strat, instanceOf(OverwriteStrategy.class));
    }

}
