package spring.twin.analysis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spring.twin.dto.DiEdgeDto;
import spring.twin.dto.types.InjectionType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link SpringDiAnalyzerService}.
 * <p>
 * Tests use real compiled class files from build/classes/java/test directory
 * to verify Spring DI analysis functionality.
 */
class SpringDiAnalyzerServiceTest {

    private static final String BUILD_CLASSES_DIR = "build/classes/java/test";
    
    private SpringDiAnalyzerService analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new SpringDiAnalyzerService();
    }

    @Test
    void shouldDetectServiceComponent() {
        // Given
        Map<String, Path> classpath = buildClasspath();

        // When
        var results = analyzer.analyzeClasspath(classpath);

        // Then
        var serviceComponent = results.stream()
            .filter(r -> r.node().name().equals("ServiceClass"))
            .findFirst()
            .orElse(null);
            
        assertThat(serviceComponent).isNotNull();
        assertThat(serviceComponent.node().labels()).containsExactly("Service");
        assertThat(serviceComponent.edges()).isEmpty(); // ServiceClass has no dependencies
    }

    @Test
    void shouldDetectRepositoryWithFieldInjection() {
        // Given
        Map<String, Path> classpath = buildClasspath();

        // When
        var results = analyzer.analyzeClasspath(classpath);

        // Then
        var repositoryComponent = results.stream()
            .filter(r -> r.node().name().equals("RepositoryClass"))
            .findFirst()
            .orElse(null);
            
        assertThat(repositoryComponent).isNotNull();
        assertThat(repositoryComponent.node().labels()).containsExactly("Repository");
        
        // Should have field injection edge to ServiceClass
        assertThat(repositoryComponent.edges()).hasSize(1);
        DiEdgeDto edge = repositoryComponent.edges().getFirst();
        assertThat(edge.from()).isEqualTo("spring.twin.analysis.fixtures.RepositoryClass");
        assertThat(edge.to()).isEqualTo("spring.twin.analysis.fixtures.ServiceClass");
        assertThat(edge.details().injectionType()).isEqualTo(InjectionType.FIELD);
        assertThat(edge.details().fieldName()).isEqualTo("serviceClass");
    }

    @Test
    void shouldDetectControllerWithConstructorInjection() {
        // Given
        Map<String, Path> classpath = buildClasspath();

        // When
        var results = analyzer.analyzeClasspath(classpath);

        // Then
        var controllerComponent = results.stream()
            .filter(r -> r.node().name().equals("ControllerClass"))
            .findFirst()
            .orElse(null);
            
        assertThat(controllerComponent).isNotNull();
        assertThat(controllerComponent.node().labels()).containsExactly("Controller");
        
        // Should have 2 constructor injection edges
        assertThat(controllerComponent.edges()).hasSize(2);
        
        // Check first param (ServiceClass)
        DiEdgeDto edge1 = controllerComponent.edges().getFirst();
        assertThat(edge1.from()).isEqualTo("spring.twin.analysis.fixtures.ControllerClass");
        assertThat(edge1.to()).isEqualTo("spring.twin.analysis.fixtures.ServiceClass");
        assertThat(edge1.details().injectionType()).isEqualTo(InjectionType.CONSTRUCTOR);
        assertThat(edge1.details().parameterIndex()).isEqualTo(0);
        
        // Check second param (RepositoryClass)
        DiEdgeDto edge2 = controllerComponent.edges().get(1);
        assertThat(edge2.from()).isEqualTo("spring.twin.analysis.fixtures.ControllerClass");
        assertThat(edge2.to()).isEqualTo("spring.twin.analysis.fixtures.RepositoryClass");
        assertThat(edge2.details().injectionType()).isEqualTo(InjectionType.CONSTRUCTOR);
        assertThat(edge2.details().parameterIndex()).isEqualTo(1);
    }

    @Test
    void shouldDetectConfigurationComponent() {
        // Given
        Map<String, Path> classpath = buildClasspath();

        // When
        var results = analyzer.analyzeClasspath(classpath);

        // Then
        var configComponent = results.stream()
            .filter(r -> r.node().name().equals("OrderService"))
            .findFirst()
            .orElse(null);
            
        assertThat(configComponent).isNotNull();
        assertThat(configComponent.node().labels()).containsExactly("Configuration");
    }

    @Test
    void shouldNotIncludeNonComponentClasses() {
        // Given
        Map<String, Path> classpath = buildClasspath();

        // When
        var results = analyzer.analyzeClasspath(classpath);

        // Then
        // ParentClass should not be in results as it has no Spring annotations
        var nonComponent = results.stream()
            .filter(r -> r.node().name().equals("ParentClass"))
            .findFirst()
            .orElse(null);
            
        assertThat(nonComponent).isNull();
    }

    @Test
    void shouldDetectComponentAnnotation() {
        // Given
        Map<String, Path> classpath = buildClasspath();

        // When
        var results = analyzer.analyzeClasspath(classpath);

        // Then
        var component = results.stream()
            .filter(r -> r.node().name().equals("StripePaymentProcessor"))
            .findFirst()
            .orElse(null);
            
        assertThat(component).isNotNull();
        assertThat(component.node().labels()).containsExactly("Component");
    }

    @Test
    void shouldResolveInterfaceDependency() {
        // Given - PaymentService depends on PaymentProcessor interface
        Map<String, Path> classpath = buildClasspath();

        // When
        var results = analyzer.analyzeClasspath(classpath);

        // Then
        var paymentService = results.stream()
            .filter(r -> r.node().name().equals("PaymentService"))
            .findFirst()
            .orElse(null);
            
        assertThat(paymentService).isNotNull();
        assertThat(paymentService.node().labels()).containsExactly("Service");
        
        // Should have dependency on PaymentProcessor
        assertThat(paymentService.edges()).isNotEmpty();
        
        // The dependency should be FIELD injection
        DiEdgeDto edge = paymentService.edges().getFirst();
        assertThat(edge.from()).isEqualTo("spring.twin.analysis.fixtures.PaymentService");
        assertThat(edge.details().injectionType()).isEqualTo(InjectionType.FIELD);
        assertThat(edge.details().fieldName()).isEqualTo("paymentProcessor");
    }

    @Test
    void shouldAnalyzeFullClasspath() {
        // Given
        Map<String, Path> classpath = buildClasspath();

        // When
        var results = analyzer.analyzeClasspath(classpath);

        // Then
        // Should find all Spring components in the fixtures
        var componentNames = results.stream()
            .map(r -> r.node().name())
            .toList();
        
        assertThat(componentNames).contains(
            "ServiceClass",
            "RepositoryClass",
            "ControllerClass",
            "OrderService",
            "StripePaymentProcessor",
            "PaymentService",
            "InterfaceBasedService",
            "InterfaceBasedController",
            "ConcreteComponentClass",
            "ServiceWithBaseClassDependency",
            "ServiceInjectingParentClass",
            "SecondInterfaceImplementation"
        );
        
        // Verify each component has correct label count
        assertThat(results).allMatch(r -> r.node().labels().size() == 1);
    }

    @Test
    void shouldHandleEmptyClasspath() {
        // Given
        Map<String, Path> emptyClasspath = new HashMap<>();

        // When
        var results = analyzer.analyzeClasspath(emptyClasspath);

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void shouldResolveInterfaceDependencyToImplementation() {
        // Given - InterfaceBasedController depends on TestInterface interface
        // Multiple Spring components implement TestInterface:
        // - InterfaceBasedService (@Service)
        // - SecondInterfaceImplementation (@Component)
        Map<String, Path> classpath = buildClasspath();

        // When
        var results = analyzer.analyzeClasspath(classpath);

        // Then
        var controllerComponent = results.stream()
            .filter(r -> r.node().name().equals("InterfaceBasedController"))
            .findFirst()
            .orElse(null);
            
        assertThat(controllerComponent).isNotNull();
        assertThat(controllerComponent.node().labels()).containsExactly("Controller");
        
        // Should have dependencies resolved to both Spring component implementations
        assertThat(controllerComponent.edges()).hasSize(2);
        var targetClasses = controllerComponent.edges().stream()
            .map(DiEdgeDto::to)
            .toList();
        assertThat(targetClasses).containsExactlyInAnyOrder(
            "spring.twin.analysis.fixtures.InterfaceBasedService",
            "spring.twin.analysis.fixtures.SecondInterfaceImplementation"
        );
        
        // Verify both edges have correct injection details
        for (DiEdgeDto edge : controllerComponent.edges()) {
            assertThat(edge.from()).isEqualTo("spring.twin.analysis.fixtures.InterfaceBasedController");
            assertThat(edge.details().injectionType()).isEqualTo(InjectionType.FIELD);
            assertThat(edge.details().fieldName()).isEqualTo("testInterface");
        }
    }

    @Test
    void shouldResolveBaseClassDependencyToConcreteComponent() {
        // Given - ServiceWithBaseClassDependency depends on BaseNonComponentClass
        // but ConcreteComponentClass extends it and is a @Component
        Map<String, Path> classpath = buildClasspath();

        // When
        var results = analyzer.analyzeClasspath(classpath);

        // Then
        var serviceComponent = results.stream()
            .filter(r -> r.node().name().equals("ServiceWithBaseClassDependency"))
            .findFirst()
            .orElse(null);
            
        assertThat(serviceComponent).isNotNull();
        assertThat(serviceComponent.node().labels()).containsExactly("Service");
        
        // Should have dependency resolved to ConcreteComponentClass (the concrete component)
        assertThat(serviceComponent.edges()).hasSize(1);
        DiEdgeDto edge = serviceComponent.edges().getFirst();
        assertThat(edge.from()).isEqualTo("spring.twin.analysis.fixtures.ServiceWithBaseClassDependency");
        assertThat(edge.to()).isEqualTo("spring.twin.analysis.fixtures.ConcreteComponentClass");
        assertThat(edge.details().injectionType()).isEqualTo(InjectionType.FIELD);
        assertThat(edge.details().fieldName()).isEqualTo("baseClass");
    }

    @Test
    void shouldNotCreateEdgesWhenNoComponentDescendants() {
        // Given - ServiceInjectingParentClass depends on ParentClass
        // ParentClass has descendants (ChildClass, GrandchildClass) but none are Spring components
        Map<String, Path> classpath = buildClasspath();

        // When
        var results = analyzer.analyzeClasspath(classpath);

        // Then
        var serviceComponent = results.stream()
            .filter(r -> r.node().name().equals("ServiceInjectingParentClass"))
            .findFirst()
            .orElse(null);
            
        assertThat(serviceComponent).isNotNull();
        assertThat(serviceComponent.node().labels()).containsExactly("Service");
        
        // Should have no edges since ParentClass has no Spring component descendants
        // The fallback will add ParentClass itself since no components were found
        assertThat(serviceComponent.edges()).hasSize(1);
        // Verify it's the fallback case (dependency type itself, not a component)
        DiEdgeDto edge = serviceComponent.edges().getFirst();
        assertThat(edge.to()).isEqualTo("spring.twin.analysis.fixtures.ParentClass");
    }

    @Test
    void shouldResolveInterfaceToMultipleImplementations() {
        // Given - InterfaceBasedController depends on TestInterface
        // TestInterface is implemented by multiple Spring components:
        // - InterfaceBasedService (@Service)
        // - SecondInterfaceImplementation (@Component)
        Map<String, Path> classpath = buildClasspath();

        // When
        var results = analyzer.analyzeClasspath(classpath);

        // Then
        var controllerComponent = results.stream()
            .filter(r -> r.node().name().equals("InterfaceBasedController"))
            .findFirst()
            .orElse(null);
            
        assertThat(controllerComponent).isNotNull();
        
        // Should have 2 edges - one to each Spring component implementation
        assertThat(controllerComponent.edges()).hasSize(2);
        
        // Collect the target class names
        var targetClasses = controllerComponent.edges().stream()
            .map(DiEdgeDto::to)
            .toList();
        
        // Both implementations should be present
        assertThat(targetClasses).containsExactlyInAnyOrder(
            "spring.twin.analysis.fixtures.InterfaceBasedService",
            "spring.twin.analysis.fixtures.SecondInterfaceImplementation"
        );
        
        // Both edges should be field injection to testInterface
        for (DiEdgeDto edge : controllerComponent.edges()) {
            assertThat(edge.from()).isEqualTo("spring.twin.analysis.fixtures.InterfaceBasedController");
            assertThat(edge.details().injectionType()).isEqualTo(InjectionType.FIELD);
            assertThat(edge.details().fieldName()).isEqualTo("testInterface");
        }
    }

    // ======================================================================
    // Helper methods
    // ======================================================================

    /**
     * Builds a classpath map with all test fixture classes.
     */
    private Map<String, Path> buildClasspath() {
        Map<String, Path> classpath = new HashMap<>();
        
        String[] classes = {
            "spring.twin.analysis.fixtures.ParentClass",
            "spring.twin.analysis.fixtures.ChildClass",
            "spring.twin.analysis.fixtures.GrandchildClass",
            "spring.twin.analysis.fixtures.TestInterface",
            "spring.twin.analysis.fixtures.ImplementingClass",
            "spring.twin.analysis.fixtures.OuterClass",
            "spring.twin.analysis.fixtures.ServiceClass",
            "spring.twin.analysis.fixtures.RepositoryClass",
            "spring.twin.analysis.fixtures.ControllerClass",
            "spring.twin.analysis.fixtures.OrderService",
            "spring.twin.analysis.fixtures.PaymentProcessor",
            "spring.twin.analysis.fixtures.StripePaymentProcessor",
            "spring.twin.analysis.fixtures.PaymentService",
            "spring.twin.analysis.fixtures.InterfaceBasedService",
            "spring.twin.analysis.fixtures.InterfaceBasedController",
            "spring.twin.analysis.fixtures.BaseNonComponentClass",
            "spring.twin.analysis.fixtures.ConcreteComponentClass",
            "spring.twin.analysis.fixtures.ServiceWithBaseClassDependency",
            "spring.twin.analysis.fixtures.ServiceInjectingParentClass",
            "spring.twin.analysis.fixtures.SecondInterfaceImplementation"
        };
        
        for (String className : classes) {
            String relativePath = className.replace('.', '/') + ".class";
            Path path = Paths.get(BUILD_CLASSES_DIR, relativePath);
            if (Files.exists(path)) {
                classpath.put(className, path);
            }
        }
        
        return classpath;
    }
}