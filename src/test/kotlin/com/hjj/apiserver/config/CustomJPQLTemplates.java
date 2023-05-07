package com.hjj.apiserver.config;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.commons.lang.IteratorAdapter;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.QueryHandler;
import jakarta.persistence.Query;

import java.util.stream.Stream;

public class CustomJPQLTemplates extends JPQLTemplates {
    public CustomJPQLTemplates() {
        super(DEFAULT_ESCAPE, new QueryHandler() {

            @Override
            public void addEntity(Query query, String alias, Class<?> type) {
                // do nothing
            }

            @Override
            public void addScalar(Query query, String alias, Class<?> type) {
                // do nothing
            }

            @Override
            public boolean createNativeQueryTyped() {
                return true;
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T> CloseableIterator<T> iterate(Query query, final FactoryExpression<?> projection) {
                Stream<T> stream = stream(query, projection);
                return new IteratorAdapter<>(stream.iterator(), stream::close);
            }

            @Override
            public <T> Stream<T> stream(Query query, FactoryExpression<?> projection) {
                final Stream resultStream = query.getResultStream();
                if (projection != null) {
                    return resultStream.map(element -> projection.newInstance((Object[]) (element.getClass().isArray() ? element : new Object[]{element})));
                }
                return resultStream;
            }

            @Override
            public boolean transform(Query query, FactoryExpression<?> projection) {
                return false;
            }

            @Override
            public boolean wrapEntityProjections() {
                return false;
            }
        });
    }
}