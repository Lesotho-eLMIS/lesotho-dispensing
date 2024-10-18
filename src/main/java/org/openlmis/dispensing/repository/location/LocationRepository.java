package org.openlmis.dispensing.repository.location;

import org.openlmis.dispensing.domain.patient.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {
}
