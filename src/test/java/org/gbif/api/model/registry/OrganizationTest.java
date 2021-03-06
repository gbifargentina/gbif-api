package org.gbif.api.model.registry;

import org.gbif.api.vocabulary.Language;

import java.net.URI;
import java.util.Set;
import java.util.UUID;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.bval.jsr303.ApacheValidationProvider;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class OrganizationTest {

  @Test
  public void testValidations() {
    ValidatorFactory validatorFactory =
      Validation.byProvider(ApacheValidationProvider.class).configure().buildValidatorFactory();
    Validator validator = validatorFactory.getValidator();

    Organization org = new Organization();
    org.setTitle("a");  // too short
    org.setHomepage(Lists.newArrayList(URI.create("www.gbif.org"))); // doesn't start with http or https
    org.setLogoUrl(URI.create("file:///tmp/aha")); // bad http URI

    // perform validation
    Set<ConstraintViolation<Organization>> violations = validator.validate(org);
    assertTrue("Violations were expected", !violations.isEmpty());

    // ensure all expected properties are caught
    Set<String> propertiesInViolation = Sets.newHashSet("title", "logoUrl");
    for (ConstraintViolation<?> cv : violations) {
      propertiesInViolation.remove(cv.getPropertyPath().toString());
    }
    assertTrue("Properties incorrectly passed validation " + propertiesInViolation, propertiesInViolation.isEmpty());

    // fix validation problems
    org.setTitle("Academy of Natural Sciences");
    org.setHomepage(Lists.newArrayList(URI.create("http://www.gbif.org")));
    org.setLogoUrl(URI.create("http://www.gbif.org/logo.png"));
    org.setLanguage(Language.ENGLISH);
    org.setEndorsingNodeKey(UUID.randomUUID());

    // perform validation again
    violations = validator.validate(org);
    assertTrue("No violations were expected", violations.isEmpty());
  }
}
