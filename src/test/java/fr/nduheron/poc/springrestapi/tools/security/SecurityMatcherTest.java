package fr.nduheron.poc.springrestapi.tools.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Lists;

import fr.nduheron.poc.springrestapi.tools.security.SecurityConfigProperties.Matcher;

@RunWith(MockitoJUnitRunner.class)
public class SecurityMatcherTest {

	@InjectMocks
	private SecurityMatcher securityMatcher;

	@Mock
	private SecurityConfigProperties securityProperties;

	@Test
	public void testNoConfiguration() {

		securityMatcher.init();

		assertTrue(securityMatcher.apply("/test1/path1"));
		assertTrue(securityMatcher.apply("/test1/path2"));
		assertTrue(securityMatcher.apply("/test2/path1"));
		assertTrue(securityMatcher.apply("/test2/path2"));
	}

	@Test
	public void testExcludeAll() {
		Matcher all = new Matcher();
		all.setAntPattern("/**");
		when(securityProperties.getExcludes()).thenReturn(Lists.newArrayList(all));

		securityMatcher.init();

		assertFalse(securityMatcher.apply("/test1/path1"));
		assertFalse(securityMatcher.apply("/test1/path2"));
		assertFalse(securityMatcher.apply("/test2/path1"));
		assertFalse(securityMatcher.apply("/test2/path2"));
	}

	@Test
	public void testCombineExcludeAndIncludes() {
		Matcher includeTest1 = new Matcher();
		includeTest1.setAntPattern("/test1/**");
		Matcher includeTest2 = new Matcher();
		includeTest2.setAntPattern("/test2/**");
		when(securityProperties.getIncludes()).thenReturn(Lists.newArrayList(includeTest1, includeTest2));

		Matcher excludePath2 = new Matcher();
		excludePath2.setAntPattern("/**/path2");
		when(securityProperties.getExcludes()).thenReturn(Lists.newArrayList(excludePath2));

		securityMatcher.init();

		assertTrue(securityMatcher.apply("/test1/path1"));
		assertFalse(securityMatcher.apply("/test1/path2"));
		assertTrue(securityMatcher.apply("/test2/path1"));
		assertFalse(securityMatcher.apply("/test2/path2"));
	}

}
