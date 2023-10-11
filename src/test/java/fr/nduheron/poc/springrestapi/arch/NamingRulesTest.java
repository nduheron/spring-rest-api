package fr.nduheron.poc.springrestapi.arch;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "fr.nduheron.poc.springrestapi", importOptions = {ImportOption.DoNotIncludeTests.class, ImportOption.Predefined.DoNotIncludeJars.class})
public class NamingRulesTest {

    @ArchTest
    static final ArchRule repositories_must_reside_in_a_repository_package =
            classes().that().haveNameMatching(".*Repository").should().resideInAPackage("..repository..")
                    .as("Repository should reside in a package '..repository..'");

    @ArchTest
    static final ArchRule controllers_must_reside_in_a_controller_package =
            classes().that().haveNameMatching(".*Controller").should().resideInAPackage("..controller..")
                    .as("Controller should reside in a package '..controller..'");

    @ArchTest
    static final ArchRule dtos_must_reside_in_a_dto_package =
            classes().that().haveNameMatching(".*Dto").should().resideInAPackage("..dto..")
                    .as("Dto should reside in a package '..dto..'");

    @ArchTest
    static final ArchRule mappers_must_reside_in_a_mapper_package =
            classes().that().haveNameMatching(".*Mapper").should().resideInAPackage("..mapper..")
                    .as("Mapper should reside in a package '..mapper..'");

}
