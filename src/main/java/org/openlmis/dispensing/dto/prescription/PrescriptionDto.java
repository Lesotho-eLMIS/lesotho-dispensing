package org.openlmis.dispensing.dto.prescription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.dispensing.domain.patient.Contact;
import org.openlmis.dispensing.domain.prescription.Prescription;
import org.openlmis.dispensing.domain.prescription.PrescriptionLineItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrescriptionDto {
  private String patientType;
  private LocalDate followUpDate;
  private LocalDate issueDate;
  private LocalDate createdDate;
  private LocalDate capturedDate;
  private LocalDate lastUpdate;
  private Boolean isVoided;
  private String status;
  private String facilityId;
  private String userId;
  private List<PrescriptionLineItemDto> lineItems;

  /**
   * Convert dto to jpa model.
   *
   * @return the converted jpa model object.
   */
  public Prescription toPrescription() {
    return new Prescription(
        patientType, followUpDate, issueDate, createdDate, capturedDate, lastUpdate,
        isVoided, status, facilityId, userId, lineItems()
    );
  }

  /**
   * Gets contacts as {@link Contact}.
   */
  public List<PrescriptionLineItem> lineItems() {
    if (null == lineItems) {
      return emptyList();
    }

    List<PrescriptionLineItem> lineItemList = new ArrayList<>();
    for (PrescriptionLineItemDto prescriptionLineItemDto : lineItems) {
      lineItemList.add(prescriptionLineItemDto.toPrescriptionLineItem());
    }
    return lineItemList;
  }
}
