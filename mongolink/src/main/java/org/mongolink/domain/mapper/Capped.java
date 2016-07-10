package org.mongolink.domain.mapper;

import com.mongodb.client.model.CreateCollectionOptions;

public class Capped {

    public boolean isCapped() {
        return true;
    }

    public Capped withSize(final int cappedSize) {
        this.size = cappedSize;
        return this;
    }

    public Capped withMax(final int max) {
        this.max = max;
        return this;
    }

    public CreateCollectionOptions getDbValue() {
        return new CreateCollectionOptions()
                .capped(true)
                .maxDocuments(max)
                .sizeInBytes(size);
    }

    public int getMax() {
        return max;
    }

    public int getSize() {
        return size;
    }

    private int size;
    private int max;

}
