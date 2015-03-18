package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.util.Collection;

import com.bamboocloud.uros.io.UrosWriter;

@SuppressWarnings("rawtypes")
final class CollectionSerializer implements UrosSerializer<Collection> {

    public final static UrosSerializer<Collection> instance = new CollectionSerializer();

    public void write(UrosWriter writer, Collection obj) throws IOException {
        writer.writeCollectionWithRef(obj);
    }
}
