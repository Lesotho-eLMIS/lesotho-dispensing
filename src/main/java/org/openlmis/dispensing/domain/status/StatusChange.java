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

package org.openlmis.dispensing.domain.status;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.openlmis.dispensing.domain.BaseTimestampedEntity;
import org.openlmis.dispensing.domain.prescription.Prescription;

@Entity
@Table(name = "status_changes", schema = "dispensing")
@NoArgsConstructor
public class StatusChange extends BaseTimestampedEntity {

  @ManyToOne(cascade = { CascadeType.REFRESH })
  @JoinColumn(name = "prescriptionId", nullable = false)
  @Getter
  @Setter
  private Prescription prescription;

  @OneToOne(mappedBy = "statusChange")
  @Getter
  @Setter
  private StatusMessage statusMessage;

  @Getter
  @Setter
  @Type(type = UUID_TYPE)
  private UUID authorId;

  @Getter
  @Setter
  @Type(type = UUID_TYPE)
  private UUID supervisoryNodeId;

  @Column(nullable = false)
  @Getter
  @Setter
  private PrescriptionStatus status;

  // @OneToMany(mappedBy = "statusChange")
  // @Getter
  // @Setter
  // private List<Rejection> rejections = new ArrayList<>();

  private StatusChange(Prescription prescription, UUID authorId) {
    this.prescription = Objects.requireNonNull(prescription);
    this.authorId = authorId;
    // this.supervisoryNodeId = prescription.getSupervisoryNodeId();
    this.status = Objects.requireNonNull(prescription.getStatus());
  }

  public static StatusChange newStatusChange(Prescription prescription, UUID authorId) {
    return new StatusChange(prescription, authorId);
  }

  /**
   * Export this object to the specified exporter (DTO).
   *
   * @param exporter exporter to export to
   */
  public void export(StatusChange.Exporter exporter) {
    exporter.setCreatedDate(getCreatedDate());
    exporter.setStatus(status);
    exporter.setStatusMessage(statusMessage);
    exporter.setAuthorId(authorId);
    //exporter.setRejections(rejections);
  }

  public interface Exporter {

    void setCreatedDate(ZonedDateTime createdDate);

    void setStatus(PrescriptionStatus status);

    void setStatusMessage(StatusMessage statusMessage);

    void setAuthorId(UUID authorId);

    //void setRejections(List<Rejection> rejections);
  }
}
