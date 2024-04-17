package org.openlmis.dispensing.util;

import org.openlmis.dispensing.domain.prescription.Prescription;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

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
}
