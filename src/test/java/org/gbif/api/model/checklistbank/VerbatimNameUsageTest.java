package org.gbif.api.model.checklistbank;

import org.gbif.api.util.IsoDateParsingUtils;
import org.gbif.api.vocabulary.Extension;
import org.gbif.dwc.terms.DcTerm;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.GbifTerm;
import org.gbif.dwc.terms.IucnTerm;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;
import org.gbif.dwc.terms.UnknownTerm;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.RandomStringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class VerbatimNameUsageTest {

  @Test
  public void testHasExtension() throws IOException {
    VerbatimNameUsage v1 = new VerbatimNameUsage();
    v1.setCoreField(DwcTerm.taxonID, "IPNI342");
    v1.setCoreField(DwcTerm.scientificName, "Abies alba");
    v1.setCoreField(DwcTerm.scientificNameAuthorship, "Miller");
    v1.setCoreField(DwcTerm.taxonRank, "species");
    assertFalse(v1.hasExtension(Extension.IDENTIFICATION));

    List<Map<Term, String>> exts = Lists.newArrayList();
    v1.getExtensions().put(Extension.IDENTIFICATION, exts);
    assertFalse(v1.hasExtension(Extension.IDENTIFICATION));
    assertFalse(v1.hasExtension(DwcTerm.Identification));

    exts.add(Maps.<Term, String>newHashMap());
    assertTrue(v1.hasExtension(Extension.IDENTIFICATION));
    assertTrue(v1.hasExtension(DwcTerm.Identification));
    assertFalse(v1.hasExtension(DwcTerm.MeasurementOrFact));

  }

  @Test
  public void testEquality() throws IOException {
    VerbatimNameUsage v1 = new VerbatimNameUsage();
    v1.setCoreField(DwcTerm.taxonID, "IPNI342");
    v1.setCoreField(DwcTerm.scientificName, "Abies alba");
    v1.setCoreField(DwcTerm.scientificNameAuthorship, "Miller");
    v1.setCoreField(DwcTerm.taxonRank, "species");

    VerbatimNameUsage v2 = new VerbatimNameUsage();
    v2.setCoreField(DwcTerm.taxonID, "IPNI342");
    v2.setCoreField(DwcTerm.scientificName, "Abies alba");
    v2.setCoreField(DwcTerm.scientificNameAuthorship, "Miller");
    v2.setCoreField(DwcTerm.taxonRank, "species");

    assertEquals(v1, v2);

    v1.setLastCrawled(new Date());
    assertFalse(v1.equals(v2));
  }

  @Test
  public void testFieldUpdate() {
    VerbatimNameUsage occ = new VerbatimNameUsage();
    String catNum = "abc123";
    occ.setCoreField(DwcTerm.catalogNumber, catNum);
    assertEquals(catNum, occ.getCoreField(DwcTerm.catalogNumber));

    String catNum2 = "qwer";
    occ.setCoreField(DwcTerm.catalogNumber, catNum2);
    assertEquals(catNum2, occ.getCoreField(DwcTerm.catalogNumber));
  }

  @Test
  public void testHasField() {
    VerbatimNameUsage occ = new VerbatimNameUsage();

    occ.setCoreField(DwcTerm.catalogNumber, "abc123");
    assertTrue(occ.hasCoreField(DwcTerm.catalogNumber));
    assertFalse(occ.hasCoreField(DwcTerm.institutionCode));

    occ.setCoreField(DwcTerm.catalogNumber, " ");
    assertTrue(occ.hasCoreField(DwcTerm.catalogNumber));

    occ.setCoreField(DwcTerm.catalogNumber, "");
    assertFalse(occ.hasCoreField(DwcTerm.catalogNumber));

    occ.setCoreField(DwcTerm.catalogNumber, null);
    assertFalse(occ.hasCoreField(DwcTerm.catalogNumber));
  }

  @Test
  public void testFieldFromTermFactory() {
    VerbatimNameUsage occ = new VerbatimNameUsage();
    String prefix = "t_";
    String colName = "t_dwc:basisOfRecord";
    Term term = TermFactory.instance().findTerm(colName.substring(prefix.length()));
    occ.setCoreField(term, "PreservedSpecimen");
    assertEquals(1, occ.getFields().size());
    assertTrue(occ.hasCoreField(DwcTerm.basisOfRecord));
    assertEquals("PreservedSpecimen", occ.getCoreField(DwcTerm.basisOfRecord));
  }

  @Test
  public void testJsonSerde() throws IOException {
    ObjectMapper mapper = new ObjectMapper();

    VerbatimNameUsage verb = new VerbatimNameUsage();
    verb.setCoreField(DwcTerm.institutionCode, "IC");
    verb.setCoreField(DwcTerm.collectionCode, "BUGS");
    verb.setCoreField(DwcTerm.catalogNumber, "MD10782");

    String json = mapper.writeValueAsString(verb);
    VerbatimNameUsage deser = mapper.readValue(json, VerbatimNameUsage.class);
    assertEquals(verb, deser);
    for (DwcTerm term : DwcTerm.values()) {
      assertEquals(verb.getCoreField(term), deser.getCoreField(term));
    }
  }

  @Test
  public void testJsonSerdeAllFields() throws IOException {
    ObjectMapper mapper = new ObjectMapper();


    VerbatimNameUsage verb = new VerbatimNameUsage();
    verb.setKey(123);
    String termPrefix = "I am Jack's ";
    for (Term term : DwcTerm.values()) {
      verb.setCoreField(term, termPrefix + term);
    }
    for (Term term : DcTerm.values()) {
      verb.setCoreField(term, termPrefix + term);
    }
    for (Term term : GbifTerm.values()) {
      verb.setCoreField(term, termPrefix + term);
    }
    for (Term term : IucnTerm.values()) {
      verb.setCoreField(term, termPrefix + term);
    }

    String json = mapper.writeValueAsString(verb);
    VerbatimNameUsage deser = mapper.readValue(json, VerbatimNameUsage.class);
    assertEquals(verb, deser);
    for (Term term : DwcTerm.values()) {
      assertTrue(deser.hasCoreField(term));
      assertEquals(termPrefix + term, deser.getCoreField(term));
    }
    for (Term term : DcTerm.values()) {
      assertTrue(deser.hasCoreField(term));
      assertEquals(termPrefix + term, deser.getCoreField(term));
    }
    for (Term term : GbifTerm.values()) {
      assertTrue(deser.hasCoreField(term));
      assertEquals(termPrefix + term, deser.getCoreField(term));
    }
    for (Term term : IucnTerm.values()) {
      assertTrue(deser.hasCoreField(term));
      assertEquals(termPrefix + term, deser.getCoreField(term));
    }
  }

  @Test
  public void testVerbatimMapSerde() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
    mapper.disable(SerializationConfig.Feature.WRITE_NULL_PROPERTIES);

    VerbatimNameUsage v = new VerbatimNameUsage();
    v.setKey(7);
    v.setLastCrawled(new Date());


    for (Term term : DwcTerm.values()) {
      v.setCoreField(term, RandomStringUtils.randomAlphabetic(20));
    }
    final int numDwcTerms = v.getFields().size();

    for (Term term : DcTerm.values()) {
      v.setCoreField(term, RandomStringUtils.randomAlphabetic(20));
    }
    final int numDcTerms = v.getFields().size() - numDwcTerms;

    for (Term term : GbifTerm.values()) {
      v.setCoreField(term, RandomStringUtils.randomAlphabetic(20));
    }
    final int numGbifTerms = v.getFields().size() - numDwcTerms - numDcTerms;

    for (Term term : IucnTerm.values()) {
      v.setCoreField(term, RandomStringUtils.randomAlphanumeric(20));
    }
    final int numIucnTerms = v.getFields().size() - numDwcTerms - numDcTerms - numGbifTerms;

    v.setCoreField(DwcTerm.scientificName, "Abies alba");
    v.setCoreField(DwcTerm.collectionCode, "BUGS");
    v.setCoreField(DwcTerm.catalogNumber, "MD10782");
    v.setCoreField(UnknownTerm.build("http://rs.un.org/terms/temperatur"), RandomStringUtils.randomAlphabetic(30));
    v.setCoreField(UnknownTerm.build("http://rs.un.org/terms/co2"), RandomStringUtils.randomAlphabetic(30));
    v.setCoreField(UnknownTerm.build("http://rs.un.org/terms/modified"), new Date().toString());
    v.setCoreField(UnknownTerm.build("http://rs.un.org/terms/scientificName"), RandomStringUtils.randomAlphabetic(30));
    final int numOtherTerms = v.getFields().size() - numDwcTerms - numDcTerms - numGbifTerms - numIucnTerms;

    final int numTerms = v.getFields().size();

    assertEquals(numTerms, numDwcTerms + numDcTerms + numGbifTerms + numIucnTerms + numOtherTerms);

    String json = mapper.writeValueAsString(v);
    System.out.println(json);

    VerbatimNameUsage v2 = mapper.readValue(json, VerbatimNameUsage.class);
    // 5 terms
    assertEquals(numTerms, v2.getFields().size());

  }


  @Test
  public void testVerbatimExtensionsMapSerde() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
    mapper.disable(SerializationConfig.Feature.WRITE_NULL_PROPERTIES);

    Map<Term, String> verbatimRecord = new HashMap<Term, String>();
    Date today = new Date();
    verbatimRecord.put(DcTerm.created, IsoDateParsingUtils.IsoDateFormat.FULL.getDateFormat().format(today));
    verbatimRecord.put(DcTerm.creator, "fede");
    verbatimRecord.put(DcTerm.description, "testDescription");
    verbatimRecord.put(DcTerm.format, "jpg");
    verbatimRecord.put(DcTerm.license, "licenseTest");
    verbatimRecord.put(DcTerm.publisher, "publisherTest");
    verbatimRecord.put(DcTerm.title, "titleTest");
    verbatimRecord.put(DcTerm.references, "http://www.gbif.org/");
    verbatimRecord.put(DcTerm.identifier, "http://www.gbif.org/");

    VerbatimNameUsage v = new VerbatimNameUsage();
    v.setKey(7);
    v.setLastCrawled(new Date());
    Map<Extension, List<Map<Term, String>>> extensions = new HashMap<Extension, List<Map<Term, String>>>();
    List<Map<Term, String>> verbatimRecords = Lists.newArrayList();
    verbatimRecords.add(verbatimRecord);
    extensions.put(Extension.MULTIMEDIA, verbatimRecords);
    v.setExtensions(extensions);


    String json = mapper.writeValueAsString(v);
    System.out.println(json);

    VerbatimNameUsage v2 = mapper.readValue(json, VerbatimNameUsage.class);
    assertNotNull(v2.getExtensions());
    assertTrue(!v2.getExtensions().get(Extension.MULTIMEDIA).isEmpty());
    assertEquals(v2.getExtensions().get(Extension.MULTIMEDIA).get(0), verbatimRecord);


  }
}