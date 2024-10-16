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

package org.openlmis.dispensing.domain.patient;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.dispensing.domain.BaseEntity;
import org.openlmis.dispensing.domain.prescription.Prescription;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "patient", schema = "dispensing")
public class Patient extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String patientNumber;

  @Column(nullable = false)
  private UUID facilityId;

  @Column(name = "geozoneid") //named to match naming in flyway migration
  private UUID geoZoneId;

  @Column(nullable = false)
  private LocalDate registrationDate;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "person_id", referencedColumnName = "id")
  private Person person;

  @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MedicalHistory> medicalHistory;
  @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Prescription> prescriptions;
}
