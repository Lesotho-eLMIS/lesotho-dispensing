package org.openlmis.dispensing.repository.prescription;

import org.openlmis.dispensing.domain.prescription.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID>,
    JpaSpecificationExecutor<Prescription> {

  List<Prescription> findByPatientNumber(@Param("patientNumber") String patientNumber);

}
