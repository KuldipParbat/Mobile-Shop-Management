package com.mobileshop.tenant;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver
        implements CurrentTenantIdentifierResolver<String> {

    private static final String DEFAULT_SCHEMA = "public";

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = TenantContext.getCurrentTenant();
        return (tenant != null && !tenant.isBlank()) ? tenant : DEFAULT_SCHEMA;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}