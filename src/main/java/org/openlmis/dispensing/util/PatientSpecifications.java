/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.dispensing.util;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;
import org.openlmis.dispensing.domain.patient.Patient;
import org.springframework.data.jpa.domain.Specification;

public class PatientSpecifications {

  public static final String PERSON_FIELD = "person";
  
  /**
   * Specification.
   *
   * @return Specification.
   */

  public static Specification<Patient> hasPatientNumber(String patientNumber) {
    return (root, query, cb) -> cb.equal(root.get("patientNumber"), patientNumber);
  }

  /**
   * Specification.
   *
   * @return Specification
   */
  public static Specification<Patient> hasFirstName(String firstName) {
    return (root, query, cb) -> cb.equal(root.get(PERSON_FIELD).get("firstName"), firstName);
  }

  /**
   * Specification.
   *
   * @return specification.
   */
  public static Specification<Patient> hasLastName(String lastName) {
    return (root, query, cb) -> cb.equal(root.get(PERSON_FIELD).get("lastName"), lastName);
  }

  /**
   * Specification.
   *
   * @return Specification.
   */
  public static Specification<Patient> hasDateOfBirth(String dateOfBirth) {
    return (root, query, cb) -> cb.equal(root.get(PERSON_FIELD).get("dateOfBirth"), dateOfBirth);
  }

  /**
   * Specification.
   *
   * @return Specification.
   */
  public static Specification<Patient> bySearchCriteria(String patientNumber, String firstName, String lastName, String dateOfBirth) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (patientNumber != null) {
        predicates.add(cb.equal(root.get("patientNumber"), patientNumber));
      }
      if (firstName != null) {
        predicates.add(cb.equal(root.get(PERSON_FIELD).get("firstName"), firstName));
      }
      if (lastName != null) {
        predicates.add(cb.equal(root.get(PERSON_FIELD).get("lastName"), lastName));
      }
      if (dateOfBirth != null) {
        predicates.add(cb.equal(root.get(PERSON_FIELD).get("dateOfBirth"), dateOfBirth));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
