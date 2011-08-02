package fr.bodysplash.mongolink.domain;

import fr.bodysplash.mongolink.domain.updateStategy.DiffStrategy;
import fr.bodysplash.mongolink.domain.updateStategy.OverwriteStrategy;
import fr.bodysplash.mongolink.domain.updateStategy.UpdateStrategy;

public enum UpdateStrategies {
    DIFF {
        @Override
        public UpdateStrategy instance() {
            return new DiffStrategy();
        }
    }, OVERWRITE {
        @Override
        public UpdateStrategy instance() {
            return new OverwriteStrategy();
        }
    };

    public abstract UpdateStrategy instance();
}
