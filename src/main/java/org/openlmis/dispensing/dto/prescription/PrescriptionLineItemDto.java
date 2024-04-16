package org.openlmis.dispensing.dto.prescription;


import org.openlmis.dispensing.domain.prescription.Prescription;
import org.openlmis.dispensing.domain.prescription.PrescriptionLineItem;

public class PrescriptionLineItemDto {
  private String dosage;
  private Integer period;
  private String batchId;
  private Integer quantityPrescribed;
  private Integer quantityDispensed;
  private Boolean servedInternally;
  private String orderableId;
  private String substituteOrderableId;
  private String comments;
  private Prescription prescription;

  public PrescriptionLineItem toPrescriptionLineItem() {
    PrescriptionLineItem PrescriptionLineItem = new PrescriptionLineItem(
        dosage, period, batchId, quantityPrescribed, quantityDispensed, servedInternally, orderableId,
        substituteOrderableId, comments, prescription
    );
    return PrescriptionLineItem;
  }
}
