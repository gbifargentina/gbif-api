/*
 * Copyright 2012 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.api.model.checklistbank.search;


import org.gbif.api.model.common.search.SearchParameter;
import org.gbif.api.vocabulary.NameType;
import org.gbif.api.vocabulary.NomenclaturalStatus;
import org.gbif.api.vocabulary.Rank;
import org.gbif.api.vocabulary.TaxonomicStatus;
import org.gbif.api.vocabulary.ThreatStatus;

import java.util.UUID;

/**
 * Each value in the enum represents a search parameter or facet of the name usage search.
 */
public enum NameUsageSearchParameter implements SearchParameter {

  /**
   * The checklist dataset key as a uuid.
   */
  DATASET_KEY(UUID.class),

  /**
   * Filters by the rank of the name usage.
   */
  RANK(Rank.class),

  /**
   * Filters by any of the higher linnean rank keys.
   * Note this is within the respective checklist and not searching nunb keys across all checklists.
   */
  HIGHERTAXON_KEY(Integer.class),

  /**
   * Filter by the taxonomis status.
   */
  STATUS(TaxonomicStatus.class),

  /**
   * Boolean filter for extinct taxa.
   */
  EXTINCT(Boolean.class),

  /**
   * Filter by the known habitats.
   */
  HABITAT(String.class),

  /**
   * Filter by the threat status.
   */
  THREAT(ThreatStatus.class),

  /**
   * Filter by the nomenclatural status.
   */
  NOMENCLATURAL_STATUS(NomenclaturalStatus.class),

  /**
   * Filter by the name type.
   */
  NAME_TYPE(NameType.class);

  private NameUsageSearchParameter(Class<?> type) {
    this.type = type;
  }

  private final Class<?> type;

  /**
   * @return the data type expected for the value of the respective search parameter
   */
  public Class<?> type() {
    return type;
  }
}