package fr.nduheron.poc.springrestapi.tools.security;

import fr.nduheron.poc.springrestapi.tools.security.SecurityConfigProperties.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SecurityMatcherTest {

    @Mock
    private SecurityConfigProperties securityProperties;

    @Test
    public void testNoConfiguration() {
        SecurityMatcher securityMatcher = new SecurityMatcher(securityProperties);

        assertTrue(securityMatcher.test("/test1/path1"));
        assertTrue(securityMatcher.test("/test1/path2"));
        assertTrue(securityMatcher.test("/test2/path1"));
        assertTrue(securityMatcher.test("/test2/path2"));
    }

    @Test
    public void testExcludeAll() {
        Matcher all = new Matcher();
        all.setAntPattern("/**");
        when(securityProperties.getExcludes()).thenReturn(Collections.singletonList(all));

        SecurityMatcher securityMatcher = new SecurityMatcher(securityProperties);

        assertFalse(securityMatcher.test("/test1/path1"));
        assertFalse(securityMatcher.test("/test1/path2"));
        assertFalse(securityMatcher.test("/test2/path1"));
        assertFalse(securityMatcher.test("/test2/path2"));
    }

    @Test
    public void testCombineExcludeAndIncludes() {
        Matcher includeTest1 = new Matcher();
        includeTest1.setAntPattern("/test1/**");
        Matcher includeTest2 = new Matcher();
        includeTest2.setAntPattern("/test2/**");
        when(securityProperties.getIncludes()).thenReturn(Arrays.asList(includeTest1, includeTest2));

        Matcher excludePath2 = new Matcher();
        excludePath2.setAntPattern("/**/path2");
        when(securityProperties.getExcludes()).thenReturn(Collections.singletonList(excludePath2));

        SecurityMatcher securityMatcher = new SecurityMatcher(securityProperties);

        assertTrue(securityMatcher.test("/test1/path1"));
        assertFalse(securityMatcher.test("/test1/path2"));
        assertTrue(securityMatcher.test("/test2/path1"));
        assertFalse(securityMatcher.test("/test2/path2"));
    }

}
