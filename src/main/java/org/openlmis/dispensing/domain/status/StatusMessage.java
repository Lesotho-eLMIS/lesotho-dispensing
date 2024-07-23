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
@Table(name = "status_messages", schema = "prepacking")
@NoArgsConstructor
public class StatusMessage extends BaseTimestampedEntity {

  private static final String UUID = "pg-uuid";

  @ManyToOne(cascade = { CascadeType.REFRESH })
  @JoinColumn(name = "prescriptionId", nullable = false)
  @Getter
  @Setter
  private Prescription prescription;

  @OneToOne(cascade = { CascadeType.ALL })
  @JoinColumn(name = "statusChangeId", nullable = false, unique = true)
  @Getter
  @Setter
  private StatusChange statusChange;

  @Getter
  @Setter
  @Type(type = UUID)
  private UUID authorId;

  @Getter
  @Setter
  private String authorFirstName;

  @Getter
  @Setter
  private String authorLastName;

  @Column(nullable = false)
  @Getter
  @Setter
  private PrescriptionStatus  status;

  @Column(nullable = false)
  @Getter
  @Setter
  private String body;

  private StatusMessage(Prescription prescription, StatusChange statusChange, UUID authorId,
      String authorFirstName, String authorLastName, String body) {
    this.prescription = Objects.requireNonNull(prescription);
    this.statusChange = Objects.requireNonNull(statusChange);
    this.authorId = authorId;
    this.authorFirstName = authorFirstName;
    this.authorLastName = authorLastName;
    this.status = Objects.requireNonNull(prescription.getStatus());
    this.body = Objects.requireNonNull(body);
  }

  public static StatusMessage newStatusMessage(Prescription prescription, StatusChange statusChange,
      UUID authorId, String authorFirstName,
      String authorLastName, String body) {
    return new StatusMessage(prescription, statusChange, authorId,
        authorFirstName, authorLastName, body);
  }

  /**
   * Export this object to the specified exporter (DTO).
   *
   * @param exporter exporter to export to
   */
  public void export(Exporter exporter) {
    exporter.setId(id);
    exporter.setAuthorId(authorId);
    exporter.setAuthorFirstName(authorFirstName);
    exporter.setAuthorLastName(authorLastName);
    exporter.setPrescriptionId(prescription.getId());
    exporter.setStatusChangeId(statusChange.getId());
    exporter.setStatus(status);
    exporter.setBody(body);
    exporter.setCreatedDate(getCreatedDate());

  }

  public interface Exporter {
    void setId(UUID id);

    void setAuthorId(UUID authorId);

    void setAuthorFirstName(String authorFirstName);

    void setAuthorLastName(String authorLastName);

    void setPrescriptionId(UUID prescriptionId);

    void setStatusChangeId(UUID statusChangeId);

    void setBody(String body);

    void setStatus(PrescriptionStatus status);

    void setCreatedDate(ZonedDateTime createdDate);
  }
}
