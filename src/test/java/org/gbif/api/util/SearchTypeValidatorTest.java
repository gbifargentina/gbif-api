package org.gbif.api.util;

import org.gbif.api.model.common.search.SearchParameter;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.gbif.api.model.checklistbank.search.NameUsageSearchParameter.EXTINCT;
import static org.gbif.api.model.occurrence.search.OccurrenceSearchParameter.ALTITUDE;
import static org.gbif.api.model.occurrence.search.OccurrenceSearchParameter.COLLECTOR_NAME;
import static org.gbif.api.model.occurrence.search.OccurrenceSearchParameter.COUNTRY;
import static org.gbif.api.model.occurrence.search.OccurrenceSearchParameter.DATASET_KEY;
import static org.gbif.api.model.occurrence.search.OccurrenceSearchParameter.DATE;
import static org.gbif.api.model.occurrence.search.OccurrenceSearchParameter.GEOMETRY;
import static org.gbif.api.model.occurrence.search.OccurrenceSearchParameter.LATITUDE;
import static org.gbif.api.model.occurrence.search.OccurrenceSearchParameter.LONGITUDE;
import static org.gbif.api.model.occurrence.search.OccurrenceSearchParameter.MODIFIED;
import static org.gbif.api.model.occurrence.search.OccurrenceSearchParameter.MONTH;
import static org.gbif.api.model.occurrence.search.OccurrenceSearchParameter.PUBLISHING_COUNTRY;
import static org.gbif.api.model.occurrence.search.OccurrenceSearchParameter.SCIENTIFIC_NAME;
import static org.gbif.api.model.occurrence.search.OccurrenceSearchParameter.YEAR;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class SearchTypeValidatorTest {

  private final String arg;
  private final Boolean valid;
  private final Boolean range;
  private final SearchParameter parameter;

  public SearchTypeValidatorTest(SearchParameter parameter, String arg, Boolean valid, Boolean range) {
    this.parameter = parameter;
    this.arg = arg;
    this.valid = valid;
    this.range = range;
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    Object[][] data = {
      {MODIFIED, "2000-10,*", true, true},
      {COLLECTOR_NAME, "henry", true, false},
      {ALTITUDE, "1080", true, false},
      {ALTITUDE, "1080.32", false, false},
      {ALTITUDE, "1080m", false, false},
      {ALTITUDE, "*, 900", true, true},
      {ALTITUDE, "100 , *", true, true},
      {ALTITUDE, "100,200", true, true},
      {ALTITUDE, " , 200", false, false},
      {ALTITUDE, "*1,200", false, false},
      {ALTITUDE, "*.1,200", false, false},
      {ALTITUDE, " , ", false, false},
      {ALTITUDE, "[1 TO 2]", false, false},
      {ALTITUDE, "{1,2}", false, false},
      {DATASET_KEY, UUID.randomUUID().toString(), true, false},
      {DATASET_KEY, "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", true, false},
      {DATASET_KEY, "F81D4FAE-7DEC-11D0-A765-00A0C91E6BF6", true, false},
      {DATASET_KEY, "F81D4FAE7DEC11D0A76500A0C91E6BF6", false, false},
      {EXTINCT, "true", true, false},
      {EXTINCT, "FALSE", true, false},
      {EXTINCT, "True", true, false},
      {EXTINCT, "1", false, false},
      {EXTINCT, "0", false, false},
      {EXTINCT, "10", false, false},
      {EXTINCT, "ja", false, false},
      {EXTINCT, "no", false, false},
      {SCIENTIFIC_NAME, "abies%", true, false},
      {GEOMETRY, "POINT (30 10)", true, false},
      {GEOMETRY, "POINT (30 10.12)", true, false},
      {GEOMETRY, "POINT (30 10.12)", true, false},
      {GEOMETRY, "POLYGON ((30 10, 10 20, 20 40, 40 40, 30 10))", true, false},
      {GEOMETRY, "polygon ((30.12 10, 10 20, 20 40, 40 40, 30.12 10))", true, false},
      {GEOMETRY, "Polygon ((30.12 10, 10 20, 20 40, 40 40, 30.1200 10.00))", true, false},
      {GEOMETRY, "POLYGON ((30,12 10, 10 20, 20 40, 40 40, 30,12 10))", false, false},
      {GEOMETRY, "POLYGON ((30 10, 10 20, 20 40, 40 40, 30 10,))", false, false},
      {GEOMETRY, "POLYGON ((30 10, 10 20, 20 40, 40 40, 30 10),)", false, false},
      {GEOMETRY, "POLYGON ((30.12 10, 10 20, 20 40, 40 40, 30.12 10.01))", false, false}, // valid wkt, although not a
// closed polygon
      {GEOMETRY, "POLYGON  ( (30 10 , 10 20 , 20 40 , 40 40 , 30 20)  )", false, false}, // valid wkt, although not a
// closed polygon
      {GEOMETRY, "POLYGON ((35 10, 10 20, 15 40, 45 45, 35 10),(20 30, 35 35, 30 20, 20 30))", true, false}, // valid
// donut
      {GEOMETRY, "LINESTRING (30 10, 10 30, 40 40)", true, false},
      {GEOMETRY, "LINESTRING (30 10, 10 30, 40 40,)", false, false},
      {GEOMETRY, "LINEARRING(30 10,10 20,20 40,40 40,30 10)", true, false},
      {GEOMETRY, "LINEARRING(30 10,10 20,20 40,40 40,30 )", false, false},
      {YEAR, "1991", true, false},
      {YEAR, "1991-01-31", false, false},
      {YEAR, "860", true, false},
      {YEAR, "1860, 1911", true, true},
      {YEAR, "1", true, false},
      {YEAR, "-10", true, false},
      {YEAR, "3018", true, false},
      {MONTH, "1991", false, false},
      {MONTH, "00", false, false},
      {MONTH, "13", false, false},
      {MONTH, "10", true, false},
      {MONTH, "", false, false},
      {MONTH, "0", false, false},
      {MONTH, "1", true, false},
      {MONTH, "-11", false, false},
      {MONTH, "1267", false, false},
      {DATE, "1900-06", true, false},
      {DATE, "01-01", false, false},
      {DATE, "1900-01-01", true, false},
      {DATE, "1900-1-01", true, false},
      {DATE, "1900-1-1", true, false},
      {DATE, "10", false, false},
      {DATE, "2000", true, false},
      {DATE, "*", true, false},
      {DATE, "*,2000", true, true},
      {DATE, "2000-10,*", true, true},
      {MODIFIED, "1900-06", true, false},
      {MODIFIED, "01-01", false, false},
      {MODIFIED, "1900-01-01", true, false},
      {MODIFIED, "1900-1-01", true, false},
      {MODIFIED, "1900-1-1", true, false},
      {MODIFIED, "10", false, false},
      {MODIFIED, "2000", true, false},
      {MODIFIED, "*", true, false},
      {MODIFIED, "*,2000", true, true},
      {MODIFIED, "2000-10,*", true, true},
      {LATITUDE, "90.0", true, false},
      {LATITUDE, "180.0", false, false},
      {LATITUDE, "50.0,92.2", false, true},
      {LATITUDE, "50.5,89.9", true, true},
      {LONGITUDE, "180.0", true, false},
      {LONGITUDE, "180.01", false, false},
      {LONGITUDE, "-190.0,92.2", false, true},
      {LONGITUDE, "-150.5,119.9", true, true},
      {COUNTRY, "CR", true, false},
      {PUBLISHING_COUNTRY, "CR", true, false},
      {COUNTRY, "CRCRCC", false, false},
      {PUBLISHING_COUNTRY, "CRCRCC", false, false}
    };
    return Arrays.asList(data);
  }

  @Test
  public void testValidateFine() throws Exception {
    try {
      SearchTypeValidator.validate(parameter, arg);
      if (!valid) {
        fail(arg + " supposed to be an invalid value for parameter " + parameter);
      }
    } catch (IllegalArgumentException e) {
      if (valid) {
        fail(arg + " supposed to be a valid value for parameter " + parameter);
      }
    }

    // test isRange
    assertEquals("Wrong isRange parsing of value " + arg, range, SearchTypeValidator.isRange(arg));
  }
}