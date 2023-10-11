package fr.nduheron.poc.springrestapi.tools.rest.security;

import fr.nduheron.poc.springrestapi.tools.rest.security.SecurityConfigProperties.Matcher;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityMatcherTest {

    @Mock
    private SecurityConfigProperties securityProperties;

    @ParameterizedTest
    @ValueSource(strings = {
            "/test1/path1",
            "/test1/path2",
            "/test2/path1",
            "/test2/path2",
    })
    void testNoConfiguration(String input) {
        SecurityMatcher securityMatcher = new SecurityMatcher(securityProperties);
        assertThat(securityMatcher.test(input)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/test1/path1",
            "/test1/path2",
            "/test2/path1",
            "/test2/path2",
    })
    void testExcludeAll(String input) {
        Matcher all = new Matcher();
        all.setAntPattern("/**");
        when(securityProperties.getExcludes()).thenReturn(singletonList(all));

        SecurityMatcher securityMatcher = new SecurityMatcher(securityProperties);
        assertThat(securityMatcher.test(input)).isFalse();
    }

    @ParameterizedTest
    @CsvSource({
            "/test1/path1,true",
            "/test1/path2,false",
            "/test2/path1,true",
            "/test2/path2,false",
    })
    void testCombineExcludeAndIncludes(String input, boolean result) {
        Matcher includeTest1 = new Matcher();
        includeTest1.setAntPattern("/test1/**");
        Matcher includeTest2 = new Matcher();
        includeTest2.setAntPattern("/test2/**");
        when(securityProperties.getIncludes()).thenReturn(Arrays.asList(includeTest1, includeTest2));

        Matcher excludePath2 = new Matcher();
        excludePath2.setAntPattern("/**/path2");
        when(securityProperties.getExcludes()).thenReturn(Collections.singletonList(excludePath2));

        SecurityMatcher securityMatcher = new SecurityMatcher(securityProperties);
        assertThat(securityMatcher.test(input)).isEqualTo(result);
    }

}
