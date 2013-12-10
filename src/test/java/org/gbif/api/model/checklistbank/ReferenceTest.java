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
package org.gbif.api.model.checklistbank;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ReferenceTest {

  @Test
  public void testEquals() {
    Reference r1 = new Reference();
    r1.setKey(123);
    r1.setLink("http://www.example.org");
    r1.setTitle("Please cite www.example.org");

    Reference r2 = new Reference();
    r2.setKey(123);
    r2.setLink("http://www.example.org");
    r2.setTitle("Please cite www.example.org");

    assertEquals(r1, r2);

    r2.setKey(124);

    assertFalse(r1.equals(r2));
  }

}