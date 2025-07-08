package com.crm.record.bisync.service.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.ObjectProvider;
import com.crm.record.bisync.model.OperationType;
import java.util.*;
import com.crm.record.bisync.service.ratelimiting.RateLimiterService;
import com.crm.record.bisync.service.validation.JSONSchemaValidatorService;
import com.crm.record.bisync.service.transformation.TransformationService;
import com.crm.record.bisync.service.SyncService;
import com.crm.record.bisync.dao.ContactDao;
import org.springframework.context.annotation.Scope;

@Configuration
public class HandlerConfig {

    // CHAIN OF RESPONSIBILITY DESIGN PATTERN (For Step By Step process on APIs)

    private final ObjectProvider<RateLimitingHandler> rateLimitingHandlerProvider;
    private final ObjectProvider<ValidationHandler> validationHandlerProvider;
    private final ObjectProvider<TransformationHandler> transformationHandlerProvider;
    private final ObjectProvider<PersistenceHandler> persistenceHandlerProvider;
    private final ObjectProvider<IncomingSyncHandler> syncHandlerProvider;

    public HandlerConfig(ObjectProvider<RateLimitingHandler> rateLimitingHandlerProvider,
                         ObjectProvider<ValidationHandler> validationHandlerProvider,
                         ObjectProvider<TransformationHandler> transformationHandlerProvider,
                         ObjectProvider<PersistenceHandler> persistenceHandlerProvider,
                         ObjectProvider<IncomingSyncHandler> syncHandlerProvider) {
        this.rateLimitingHandlerProvider = rateLimitingHandlerProvider;
        this.validationHandlerProvider = validationHandlerProvider;
        this.transformationHandlerProvider = transformationHandlerProvider;
        this.persistenceHandlerProvider = persistenceHandlerProvider;
        this.syncHandlerProvider = syncHandlerProvider;
    }

    @Bean
    @Scope("prototype")
    public RateLimitingHandler rateLimitingHandler(RateLimiterService rateLimiterService) {
        return new RateLimitingHandler(rateLimiterService);
    }

    @Bean
    @Scope("prototype")
    public ValidationHandler validationHandler(JSONSchemaValidatorService schemaValidator) {
        return new ValidationHandler(schemaValidator);
    }

    @Bean
    @Scope("prototype")
    public TransformationHandler transformationHandler(TransformationService transformationService) {
        return new TransformationHandler(transformationService);
    }

    @Bean
    @Scope("prototype")
    public PersistenceHandler persistenceHandler(ContactDao contactDao) {
        return new PersistenceHandler(contactDao);
    }

    @Bean
    @Scope("prototype")
    public IncomingSyncHandler syncHandler(SyncService syncService) {
        return new IncomingSyncHandler(syncService);
    }

    /**
     * Chain for CREATE operation: rate limit -> validate -> transform -> persist
     */
    @Bean(name = "createHandlerChain")
    public Handler createHandlerChain() {
        RateLimitingHandler rateLimitingHandler = rateLimitingHandlerProvider.getObject();
        ValidationHandler validationHandler = validationHandlerProvider.getObject();
        TransformationHandler transformationHandler = transformationHandlerProvider.getObject();
        PersistenceHandler persistenceHandler = persistenceHandlerProvider.getObject();

        rateLimitingHandler.setNext(validationHandler);
        validationHandler.setNext(transformationHandler);
        transformationHandler.setNext(persistenceHandler);

        return rateLimitingHandler;
    }

    /**
     * Chain for GET operation: rate limit -> persist -> transform
     */
    @Bean(name = "getHandlerChain")
    public Handler getHandlerChain() {

        RateLimitingHandler rateLimitingHandler = rateLimitingHandlerProvider.getObject();
        PersistenceHandler persistenceHandler = persistenceHandlerProvider.getObject();
        TransformationHandler transformationHandler = transformationHandlerProvider.getObject();

        rateLimitingHandler.setNext(persistenceHandler);
        persistenceHandler.setNext(transformationHandler);

        return rateLimitingHandler;
    }

    /**
     * Chain for UPDATE operation: rate limit -> validate -> transform -> persist
     */
    @Bean(name = "updateHandlerChain")
    public Handler updateHandlerChain() {
        RateLimitingHandler rateLimitingHandler = rateLimitingHandlerProvider.getObject();
        ValidationHandler validationHandler = validationHandlerProvider.getObject();
        TransformationHandler transformationHandler = transformationHandlerProvider.getObject();
        PersistenceHandler persistenceHandler = persistenceHandlerProvider.getObject();

        rateLimitingHandler.setNext(validationHandler);
        validationHandler.setNext(transformationHandler);
        transformationHandler.setNext(persistenceHandler);

        return rateLimitingHandler;
    }

    /**
     * Chain for DELETE operation: rate limit -> persist
     */
    @Bean(name = "deleteHandlerChain")
    public Handler deleteHandlerChain() {
        RateLimitingHandler rateLimitingHandler = rateLimitingHandlerProvider.getObject();
        PersistenceHandler persistenceHandler = persistenceHandlerProvider.getObject();

        rateLimitingHandler.setNext(persistenceHandler);

        return rateLimitingHandler;
    }

    /**
     * Chain for SYNC operation: rate limit -> transform -> sync
     */
    @Bean(name = "syncHandlerChain")
    public Handler syncHandlerChain() {

        RateLimitingHandler rateLimitingHandler = rateLimitingHandlerProvider.getObject();
        TransformationHandler transformationHandler = transformationHandlerProvider.getObject();
        IncomingSyncHandler syncHandler = syncHandlerProvider.getObject();

        rateLimitingHandler.setNext(transformationHandler);
        transformationHandler.setNext(syncHandler);

        return rateLimitingHandler;
    }

    /**
     * Handler map that maps CRUD operations to their handler chains.
     */
    @Bean
    public Map<OperationType, Handler> handlerMap(
            @Qualifier("createHandlerChain") Handler createHandlerChain,
            @Qualifier("getHandlerChain") Handler getHandlerChain,
            @Qualifier("updateHandlerChain") Handler updateHandlerChain,
            @Qualifier("deleteHandlerChain") Handler deleteHandlerChain,
            @Qualifier("syncHandlerChain") Handler syncHandlerChain) {

        Map<OperationType, Handler> map = new EnumMap<>(OperationType.class);
        map.put(OperationType.CREATE, createHandlerChain);
        map.put(OperationType.GET, getHandlerChain);
        map.put(OperationType.UPDATE, updateHandlerChain);
        map.put(OperationType.DELETE, deleteHandlerChain);
        map.put(OperationType.SYNC, syncHandlerChain);

        return map;
    }
}
