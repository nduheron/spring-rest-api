package fr.nduheron.poc.springrestapi.arch;


import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "fr.nduheron.poc.springrestapi", importOptions = {ImportOption.DoNotIncludeTests.class, ImportOption.Predefined.DoNotIncludeJars.class})
public class LayeredArchitectureTest {

    @ArchTest
    static final ArchRule arch_dependencies_are_respected = layeredArchitecture()
            .consideringOnlyDependenciesInAnyPackage("fr.nduheron..")
            .layer("Tools").definedBy("*.*.*.*.tools..")
            .layer("Config").definedBy("*.*.*.*.config..")
            .layer("Security").definedBy("*.*.*.*.security..")
            .layer("User").definedBy("*.*.*.*.user..")
            .whereLayer("Config").mayNotBeAccessedByAnyLayer()
            .whereLayer("Config").mayOnlyAccessLayers("Tools", "User")
            .whereLayer("Security").mayNotBeAccessedByAnyLayer()
            .whereLayer("Security").mayOnlyAccessLayers("Tools", "User")
            .whereLayer("Tools").mayNotAccessAnyLayer()
            .whereLayer("User").mayOnlyBeAccessedByLayers("Config", "Security")
            .whereLayer("User").mayOnlyAccessLayers("Tools");

    @ArchTest
    static final ArchRule layer_dependencies_are_respected = layeredArchitecture()
            .consideringOnlyDependenciesInAnyPackage("fr.nduheron.poc.springrestapi.user..")
            .layer("Controller").definedBy("..controller..")
            .layer("Mapper").definedBy("..mapper..")
            .layer("Dto").definedBy("..dto..")
            .layer("Model").definedBy("..model..")
            .layer("Repository").definedBy("..repository..")
            .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("Controller").mayOnlyAccessLayers("Mapper", "Dto", "Repository", "Model")
            .whereLayer("Mapper").mayOnlyBeAccessedByLayers("Controller")
            .whereLayer("Mapper").mayOnlyAccessLayers("Dto", "Model")
            .whereLayer("Dto").mayOnlyBeAccessedByLayers("Controller", "Mapper")
            .whereLayer("Dto").mayOnlyAccessLayers("Model")
            .whereLayer("Model").mayNotAccessAnyLayer()
            .whereLayer("Model").mayOnlyBeAccessedByLayers("Controller", "Model", "Mapper", "Repository", "Dto")
            .whereLayer("Repository").mayOnlyBeAccessedByLayers("Controller")
            .whereLayer("Repository").mayOnlyAccessLayers("Model");
}