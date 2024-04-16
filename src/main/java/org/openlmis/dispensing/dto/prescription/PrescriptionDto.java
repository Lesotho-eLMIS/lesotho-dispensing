package org.openlmis.dispensing.dto.prescription;

import org.openlmis.dispensing.domain.patient.Contact;
import org.openlmis.dispensing.domain.prescription.Prescription;
import org.openlmis.dispensing.domain.prescription.PrescriptionLineItem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

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
    Prescription Prescription = new Prescription(
        patientType, followUpDate, issueDate, createdDate, capturedDate, lastUpdate,
        isVoided, status, facilityId, userId, lineItems()
    );
    return Prescription;
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
