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

package org.openlmis.dispensing.repository.patient;

import java.time.LocalDate;
import java.util.UUID;
import org.openlmis.dispensing.domain.patient.Patient;
// import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

public interface PatientRepository extends JpaRepository<Patient, UUID>,
    JpaSpecificationExecutor<Patient> {
      
  Patient findByPatientNumber(@Param("patientNumber") String patientNumber);

  int countByFacilityIdAndRegistrationDate(UUID facilityId, LocalDate date);

  int countByFacilityId(UUID facilityId);

  int countByGeoZoneId(UUID facilityId);
}
