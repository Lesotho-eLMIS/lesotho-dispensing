package org.openlmis.dispensing.util;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.openlmis.dispensing.domain.prescription.Prescription;
import org.springframework.data.jpa.domain.Specification;

public class PrescriptionSpecification {

  public static Specification<Prescription> patientIdIn(List<UUID> patientIds) {
    return (root, query, cb) -> patientIds == null ? null : root.get("patient").get("id").in(patientIds);
  }

  public static Specification<Prescription> statusEquals(String status) {
    return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
  }

  public static Specification<Prescription> patientTypeEquals(String patientType) {
    return (root, query, cb) -> patientType == null ? null : cb.equal(root.get("patientType"), patientType);
  }

  public static Specification<Prescription> isVoidedEquals(Boolean isVoided) {
    return (root, query, cb) -> isVoided == null ? null : cb.equal(root.get("isVoided"), isVoided);
  }

  public static Specification<Prescription> followUpDateEquals(LocalDate followUpDate) {
    return (root, query, cb) -> followUpDate == null ? null : cb.equal(root.get("followUpDate"), followUpDate);
  }
}
