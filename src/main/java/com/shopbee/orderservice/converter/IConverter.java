package com.shopbee.orderservice.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface IConverter<S, D> {

    D convert(S s);

    S reverse(D d);

    default List<D> convertAll(Collection<S> source) {
        if (source == null) {
            return Collections.emptyList();
        }
        return source.stream().map(this::convert).toList();
    }

    default List<S> reverseAll(Collection<D> destination) {
        if (destination == null) {
            return Collections.emptyList();
        }
        return destination.stream().map(this::reverse).toList();
    }
}
