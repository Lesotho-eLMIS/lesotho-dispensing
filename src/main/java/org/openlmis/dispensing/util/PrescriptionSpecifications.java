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
import org.openlmis.dispensing.domain.prescription.Prescription;
import org.springframework.data.jpa.domain.Specification;


public class PrescriptionSpecifications {
  public static final String PATIENT_FIELD = "patient";

  /**
   * Specification.
   *
   * @return Specification.
   */
  public static Specification<Prescription> hasPatientFirstName(String firstName) {
    return (root, query, cb) -> cb.equal(root.get(PATIENT_FIELD).get("firstName"), firstName);
  }

  /**
   * Specification.
   *
   * @return Specification.
   */
  public static Specification<Prescription> hasPatientLastName(String lastName) {
    return (root, query, cb) -> cb.equal(root.get(PATIENT_FIELD).get("lastName"), lastName);
  }

  /**
   * Specification.
   *
   * @return Specification.
   */
  public static Specification<Prescription> hasPatientDateOfBirth(String dateOfBirth) {
    return (root, query, cb) -> cb.equal(root.get(PATIENT_FIELD).get("dateOfBirth"), dateOfBirth);
  }

  /**
   * Specification.
   *
   * @return Specification.
   */
  public static Specification<Prescription> bySearchCriteria(String firstName, String lastName, String dateOfBirth) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (firstName != null) {
        predicates.add(cb.equal(root.get(PATIENT_FIELD).get("firstName"), firstName));
      }
      if (lastName != null) {
        predicates.add(cb.equal(root.get(PATIENT_FIELD).get("lastName"), lastName));
      }
      if (dateOfBirth != null) {
        predicates.add(cb.equal(root.get(PATIENT_FIELD).get("dateOfBirth"), dateOfBirth));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  /**
   * Specification.
   *
   * @return Specification.
   */
  public static Specification<Prescription> byPatientIds(List<String> patientIds) {
    return (root, query, criteriaBuilder) -> {
      if (patientIds != null && !patientIds.isEmpty()) {
        return root.get("patientId").in(patientIds);
      } else {
        return criteriaBuilder.conjunction();
      }
    };
  }
}
