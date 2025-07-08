package com.crm.record.bisync.service.handler;

import com.crm.record.bisync.dao.ContactDao;
import com.crm.record.bisync.model.OperationType;
import com.crm.record.bisync.service.transformation.TransformationService;
import com.crm.record.bisync.service.validation.JSONSchemaValidatorService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.ObjectProvider;
import com.crm.record.bisync.client.SyncClient;

import java.util.EnumMap;
import java.util.Map;

@Configuration
public class HandlerConfig {

    // CHAIN OF RESPONSIBILITY DESIGN PATTERN (For Step By Step process on APIs)

    private final ObjectProvider<ValidationHandler> validationHandlerProvider;
    private final ObjectProvider<TransformationHandler> transformationHandlerProvider;
    private final ObjectProvider<PersistenceHandler> persistenceHandlerProvider;
    private final ObjectProvider<OutgoingSyncHandler> syncHandlerProvider;

    public HandlerConfig(ObjectProvider<ValidationHandler> validationHandlerProvider,
                         ObjectProvider<TransformationHandler> transformationHandlerProvider,
                         ObjectProvider<PersistenceHandler> persistenceHandlerProvider,
                         ObjectProvider<OutgoingSyncHandler> syncHandlerProvider) {
        this.validationHandlerProvider = validationHandlerProvider;
        this.transformationHandlerProvider = transformationHandlerProvider;
        this.persistenceHandlerProvider = persistenceHandlerProvider;
        this.syncHandlerProvider = syncHandlerProvider;
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
    public OutgoingSyncHandler syncHandler(SyncClient syncClient) {
        return new OutgoingSyncHandler(syncClient);
    }

    /**
     * Chain for CREATE operation: validate -> transform -> persist
     */
    @Bean(name = "createHandlerChain")
    public Handler createHandlerChain() {
        ValidationHandler validationHandler = validationHandlerProvider.getObject();
        TransformationHandler transformationHandler = transformationHandlerProvider.getObject();
        PersistenceHandler persistenceHandler = persistenceHandlerProvider.getObject();
        OutgoingSyncHandler syncHandler = syncHandlerProvider.getObject();

        validationHandler.setNext(transformationHandler);
        transformationHandler.setNext(persistenceHandler);
        persistenceHandler.setNext(syncHandler);

        return validationHandler;
    }

    /**
     * Chain for GET operation: persist -> transform
     */
    @Bean(name = "getHandlerChain")
    public Handler getHandlerChain() {

        PersistenceHandler persistenceHandler = persistenceHandlerProvider.getObject();
        TransformationHandler transformationHandler = transformationHandlerProvider.getObject();

        persistenceHandler.setNext(transformationHandler);

        return persistenceHandler;
    }

    /**
     * Chain for UPDATE operation: validate -> transform -> persist
     */
    @Bean(name = "updateHandlerChain")
    public Handler updateHandlerChain() {
        ValidationHandler validationHandler = validationHandlerProvider.getObject();
        TransformationHandler transformationHandler = transformationHandlerProvider.getObject();
        PersistenceHandler persistenceHandler = persistenceHandlerProvider.getObject();
        OutgoingSyncHandler syncHandler = syncHandlerProvider.getObject();

        validationHandler.setNext(transformationHandler);
        transformationHandler.setNext(persistenceHandler);
        persistenceHandler.setNext(syncHandler);

        return validationHandler;
    }

    /**
     * Chain for DELETE operation: persist
     */
    @Bean(name = "deleteHandlerChain")
    public Handler deleteHandlerChain() {
        PersistenceHandler persistenceHandler = persistenceHandlerProvider.getObject();
        OutgoingSyncHandler syncHandler = syncHandlerProvider.getObject();

        persistenceHandler.setNext(syncHandler);

        return persistenceHandler;
    }

    /**
     * Handler map that maps CRUD operations to their handler chains.
     */
    @Bean
    public Map<OperationType, Handler> handlerMap(
            @Qualifier("createHandlerChain") Handler createHandlerChain,
            @Qualifier("getHandlerChain") Handler getHandlerChain,
            @Qualifier("updateHandlerChain") Handler updateHandlerChain,
            @Qualifier("deleteHandlerChain") Handler deleteHandlerChain) {

        Map<OperationType, Handler> map = new EnumMap<>(OperationType.class);
        map.put(OperationType.CREATE, createHandlerChain);
        map.put(OperationType.GET, getHandlerChain);
        map.put(OperationType.UPDATE, updateHandlerChain);
        map.put(OperationType.DELETE, deleteHandlerChain);
        return map;
    }
}
