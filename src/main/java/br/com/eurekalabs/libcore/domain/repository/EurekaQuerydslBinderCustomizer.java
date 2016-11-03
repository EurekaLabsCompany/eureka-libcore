package br.com.eurekalabs.libcore.domain.repository;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.*;
import org.springframework.data.querydsl.binding.MultiValueBinding;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

public interface EurekaQuerydslBinderCustomizer <T extends EntityPath<?>> extends QuerydslBinderCustomizer<T> {

    @Override
    default void customize(QuerydslBindings querydslBindings, T entityPath) {

        querydslBindings.bind(String.class).first(
                (SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);

        querydslBindings.bind(Float.class).all(
                (MultiValueBinding<NumberPath<Float>, Float>) this::getNumberCondition);

        querydslBindings.bind(Integer.class).all(
                (MultiValueBinding<NumberPath<Integer>, Integer>) this::getNumberCondition);

        querydslBindings.bind(BigDecimal.class).all(
                (MultiValueBinding<NumberPath<BigDecimal>, BigDecimal>) this::getNumberCondition);

        querydslBindings.bind(Long.class).all(
                (MultiValueBinding<NumberPath<Long>, Long>) this::getNumberCondition);

        querydslBindings.bind(Short.class).all(
                (MultiValueBinding<NumberPath<Short>, Short>) this::getNumberCondition);

        querydslBindings.bind(Date.class).all(
                (MultiValueBinding<DatePath<Date>, Date>) this::getTemporalCondition);

        querydslBindings.bind(ZonedDateTime.class).all(
                (MultiValueBinding<DateTimePath<ZonedDateTime>, ZonedDateTime>) this::getTemporalCondition);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default BooleanExpression getTemporalCondition(final TemporalExpression path, Collection<? extends Comparable> values) {
        final BooleanExpression expression;

        if (values.size() == 2) {
            final List<? extends Comparable> dateValues = new ArrayList<>(values);
            Collections.sort(dateValues);
            expression = path.between(dateValues.get(0), dateValues.get(1));
        } else {
            expression = path.eq(values.iterator().next());
        }

        return expression;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    default BooleanExpression getNumberCondition(final NumberPath path, Collection<? extends Number> values) {
        final BooleanExpression expression;

        if (values.size() == 2) {
            final List<? extends Comparable> numberValues = new ArrayList(values);
            Collections.sort(numberValues);
            expression = path.between((Number) numberValues.get(0), (Number) numberValues.get(1));
        } else {
            expression = path.eq(values.iterator().next());
        }

        return expression;
    }

}
