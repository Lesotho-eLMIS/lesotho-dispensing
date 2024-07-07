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
