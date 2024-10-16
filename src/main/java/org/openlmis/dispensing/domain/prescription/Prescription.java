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

package org.openlmis.dispensing.domain.prescription;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.openlmis.dispensing.domain.BaseEntity;
import org.openlmis.dispensing.domain.patient.Patient;
import org.openlmis.dispensing.domain.status.PrescriptionStatus;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Prescription", schema = "dispensing")
public class Prescription extends BaseEntity {
  private String patientType;
  private LocalDate followUpDate;
  private LocalDate issueDate;
  private LocalDate createdDate;
  private LocalDate capturedDate;
  private LocalDate lastUpdate;
  private Boolean isVoided;
  private UUID facilityId;
  private UUID prescribedByUserId;
  private UUID servedByUserId;
  @ManyToOne
  @JoinColumn(name = "patient_id")
  private Patient patient;

  @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PrescriptionLineItem> lineItems;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @Getter
  @Setter
  private PrescriptionStatus status;
}
