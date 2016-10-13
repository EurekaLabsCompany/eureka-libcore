package br.com.eurekalabs.libcore.controller;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;

import java.util.Optional;

public abstract class EurekaRepositoryEntityController<T> {

    @Autowired
    private PagedResourcesAssembler<T> pagedResourcesAssembler;

    @Autowired
    private RepositoryEntityLinks entityLinks;

    private final Class<T> type;
    private final QueryDslPredicateExecutor<T> executor;

    public EurekaRepositoryEntityController(Class<T> type, QueryDslPredicateExecutor<T> executor) {
        this.type = type;
        this.executor = executor;
    }

    protected abstract Optional<Predicate> getDataRestriction();

    protected PagedResources getCollectionResource(Predicate predicate, Pageable pageable) {
        final Optional<Predicate> dataRestriction = getDataRestriction();
        if (dataRestriction.isPresent()) {
            predicate = predicate == null ? dataRestriction.get() : new BooleanBuilder(dataRestriction.get()).and(predicate);
        }

        final Page<T> page = executor.findAll(predicate, pageable);

        final Link baseLink = entityLinks.linkToPagedResource(type, pageable);

        if (page.getContent().isEmpty()) {
            return pagedResourcesAssembler.toEmptyResource(page, type, baseLink);
        }

        return pagedResourcesAssembler.toResource(page, baseLink);
    }

}
