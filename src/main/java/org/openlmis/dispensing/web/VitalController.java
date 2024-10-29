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

package org.openlmis.dispensing.web;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.UUID;
import org.openlmis.dispensing.dto.vital.VitalDto;
import org.openlmis.dispensing.service.vital.VitalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Controller used to perform CRUD operations on point of delivery event.
 */
@Controller
@RequestMapping("/api/vital")
public class VitalController extends BaseController {
    public static final String ID_PATH_VARIABLE = "/{id}";
    private static final Logger LOGGER = LoggerFactory.getLogger(VitalController.class);

    @Autowired
    private VitalService vitalService;

    /**
     * Create vital.
     *
     * @param vitalDto a vital dto bound to request body.
     * @return created vital's ID.
     */
    @Transactional
    @RequestMapping(method = POST)
    public ResponseEntity<UUID> createVital(
            @org.springframework.web.bind.annotation.RequestBody VitalDto vitalDto) {
        LOGGER.debug("Try to create a vital");
        Profiler profiler = getProfiler("CREATE_VITAL", vitalDto);

        profiler.start("PROCESS");
        UUID createdVitalId = vitalService.createVital(vitalDto);

        profiler.start("CREATE_RESPONSE");
        ResponseEntity<UUID> response = new ResponseEntity<>(createdVitalId, CREATED);

        return stopProfiler(profiler, response);
    }

    /**
     * Get vital with a given id (uuid).
     * A vitals matching the given id.
     */
    @GetMapping(ID_PATH_VARIABLE)
    @ResponseStatus(OK)
    @ResponseBody
    public ResponseEntity<VitalDto> getVital(@PathVariable UUID id) {
        VitalDto vital = vitalService.getVitalById(id);
        if (null == vital) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        return new ResponseEntity<>(vital, OK);
    }

    /**
     * Update a Vital.
     *
     * @param id  Vital id.
     * @param dto Vital dto.
     * @return Updated Vital dto.
     */
    @Transactional
    @PutMapping(ID_PATH_VARIABLE)
    @ResponseStatus(OK)
    @ResponseBody
    public ResponseEntity<VitalDto> updateVital(@PathVariable UUID id, @RequestBody VitalDto dto) {
        VitalDto updatedVital = vitalService.updateVital(id, dto);
        return new ResponseEntity<>(updatedVital, OK);
    }
}
