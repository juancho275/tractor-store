package com.tractorstore;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Spring Modulith architecture verification test.
 *
 * Verifies:
 * 1. No illegal cross-module dependencies (e.g. cart importing catalog repositories)
 * 2. Each module only exposes its public API (application services)
 * 3. Domain events are used for cross-module communication
 *
 * If this test passes, our modular architecture is structurally correct.
 */
class ModularityTests {

    ApplicationModules modules = ApplicationModules.of(TractorStoreApplication.class);

    @Test
    void verifiesModularStructure() {
        // Este test falla si cualquier módulo accede
        // directamente a las clases internas de otro módulo
        modules.verify();
    }

    @Test
    void generateDocumentation() {
        // Genera documentación de la arquitectura modular
        new Documenter(modules)
            .writeModulesAsPlantUml()
            .writeIndividualModulesAsPlantUml();
    }
}