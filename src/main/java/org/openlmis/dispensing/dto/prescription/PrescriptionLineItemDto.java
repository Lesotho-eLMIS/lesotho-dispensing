package org.openlmis.dispensing.dto.prescription;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.dispensing.domain.prescription.Prescription;
import org.openlmis.dispensing.domain.prescription.PrescriptionLineItem;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    return new PrescriptionLineItem(
        dosage, period, batchId, quantityPrescribed, quantityDispensed, servedInternally, orderableId,
        substituteOrderableId, comments, prescription
    );
  }
}
