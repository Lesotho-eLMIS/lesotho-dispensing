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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.openlmis.dispensing.domain.BaseEntity;
import org.openlmis.dispensing.domain.status.PrescriptionLineItemStatus;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PrescriptionLineItem", schema = "dispensing")
public class PrescriptionLineItem extends BaseEntity {
  
  //Prescription create attributes
  private Integer dose;
  private String doseUnits;
  private String doseFrequency;
  private String route;
  private Integer duration;
  private String durationUnits;
  private String additionalInstructions;    
  private UUID orderablePrescribed;
  private Integer quantityPrescribed;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @Getter
  @Setter
  private PrescriptionLineItemStatus status = PrescriptionLineItemStatus.REQUESTED;

  //Prescription serve attributes
  private UUID orderableDispensed;
  private UUID lotId;
  private Integer quantityDispensed;
  private Integer remainingBalance;
  private Boolean servedExternally;
  private String comments;
  private LocalDate collectBalanceDate;
  //private UUID programId;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "prescription_id")
  private Prescription prescription;

  /**
   * Constructor for PrescriptionLineItem.
   */
  public PrescriptionLineItem(Integer dose, String doseUnits, String doseFrequency, String route,
      Integer duration, String durationUnits, String additionalInstructions, UUID orderablePrescribed,
      Integer quantityPrescribed, UUID orderableDispensed, UUID lotId, Integer quantityDispensed,
      Integer remainingBalance, Boolean servedExternally, String comments, LocalDate collectBalanceDate) {
    
    // Prescription create attributes
    this.dose = dose;
    this.doseUnits = doseUnits;
    this.doseFrequency = doseFrequency;
    this.route = route;
    this.duration = duration;
    this.durationUnits = durationUnits;
    this.additionalInstructions = additionalInstructions;
    this.orderablePrescribed = orderablePrescribed;
    this.quantityPrescribed = quantityPrescribed;
    this.status = PrescriptionLineItemStatus.REQUESTED;  // Set default value

    // Prescription serve attributes
    this.orderableDispensed = orderableDispensed;
    this.lotId = lotId;
    this.quantityDispensed = quantityDispensed;
    this.remainingBalance = remainingBalance;
    this.servedExternally = servedExternally;
    this.comments = comments;
    this.collectBalanceDate = collectBalanceDate;
  }
}
