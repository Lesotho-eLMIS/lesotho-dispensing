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

package org.openlmis.dispensing.service.patient;

//import java.util.ArrayList;
//import java.util.Collections;
import java.util.List;
import java.util.Optional;
//import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.openlmis.dispensing.domain.patient.Contact;
import org.openlmis.dispensing.domain.patient.MedicalHistory;
import org.openlmis.dispensing.domain.patient.Patient;
import org.openlmis.dispensing.domain.patient.Person;
import org.openlmis.dispensing.dto.patient.ContactDto;
import org.openlmis.dispensing.dto.patient.MedicalHistoryDto;
import org.openlmis.dispensing.dto.patient.PatientDto;
import org.openlmis.dispensing.dto.patient.PersonDto;
import org.openlmis.dispensing.repository.patient.PatientRepository;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {
  //private static final Logger LOGGER = LoggerFactory.getLogger(PatientService.class);

  @Autowired
  private PatientRepository patientRepository;

  /**
   * Get a list of Patients.
   *
   * @return a list of pod events.
   */
  public List<PatientDto> getPatients() {
    // List<Patient> patients = patientRepository.findAll();
    
    // if (patients == null) {
    //   return Collections.emptyList();
    // }

    // List<PatientDto> patientDtos = new ArrayList<>();
    // for (Patient patient : patients) {
    //   patientDtos.add(patientToDto(patient));
    // }
    // return patientDtos;
    return null;
  }

  /**
   * Get a Patient by id.
   *
   * @param id patient.
   * @return a patient.
   */
  public Optional<Patient> getPatientById(UUID id) {
    return patientRepository.findById(id);
  }

  /**
   * Create a Patient.
   *
   * @param patientDto patientDto.
   * @return id of created patientDto.
   */
  @Transactional
  public UUID createPatient(PatientDto patientDto) {
    // Patient patient = convertToPatientEntity(patientDto);
    // patient.getId();
    Patient patient2 = patientDto.toPatient();
    return patientRepository.save(patient2).getId();
  }

  // /**
  //  * Convert patient dto to jpa model (entity).
  //  *
  //  * @param patientDto Dto.
  //  * @return Patient.
  //  */
  // private Patient convertToPatientEntity(PatientDto patientDto) {
  //   if (null == patientDto) {
  //     return null;
  //   }
  //   Patient patient = new Patient();
  //   patient.setPatientNumber(patientDto.getPatientNumber());
  //   patient.setPerson(convertToPersonEntity(patientDto.getPersonDto()));
  //   if (patientDto.getMedicalHistory() != null) {
  //     patient.setMedicalHistory(patientDto.getMedicalHistory().stream()
  //         .map(this::convertToMedicalHistoryEntity)
  //         .collect(Collectors.toSet()));
  //   }
  //   return patient;
  // }

  // private Person convertToPersonEntity(PersonDto personDto) {
  //   if (personDto == null) {
  //     return null;
  //   }
  //   Person person = new Person();
  //   person.setFirstName(personDto.getFirstName());
  //   person.setLastName(personDto.getLastName());
  //   person.setNationalId(personDto.getNationalId());
  //   person.setDateOfBirth(personDto.getDateOfBirth());
  //   person.setNickName(personDto.getNickName());
  //   person.setSex(personDto.getSex());
  //   person.setNextOfKinFullName(personDto.getNextOfKinFullName());
  //   person.setNextOfKinContact(personDto.getNextOfKinContact());
  //   person.setNickName(personDto.getNickName());
  //   person.setMotherMaidenName(personDto.getMotherMaidenName());
  //   person.setDeceased(personDto.getDeceased());
  //   person.setRetired(personDto.getRetired());
  //   if (personDto.getContacts() != null) {
  //     person.setContacts(personDto.getContacts().stream()
  //                           .map(this::convertToContactEntity)
  //                           .collect(Collectors.toSet()));
  //   }
  //   return person;
  // }

  // private Contact convertToContactEntity(ContactDto contactDto) {
  //   if (contactDto == null) {
  //     return null;
  //   }
  //   return new Contact(contactDto.getContactType(),contactDto.getContacts());
  // }

  // private MedicalHistory convertToMedicalHistoryEntity(MedicalHistoryDto medicalHistoryDto) {
  //   if (medicalHistoryDto == null || medicalHistoryDto.getType() == null
  //         || medicalHistoryDto.getHistory() == null) {
  //     return null;
  //   }
  //   return new MedicalHistory(medicalHistoryDto.getType(), medicalHistoryDto.getHistory());
  // }
  
  /**
   * Create dto from jpa model.
   *
   * @param patient jpa model.
   * @return Patient created dto.
   */
  private PatientDto patientToDto(Patient patient) {
    return PatientDto.builder()
      .id(patient.getId())
      .patientNumber(patient.getPatientNumber())
      .personDto(personToDto(patient.getPerson()))
      .medicalHistory(patient.getMedicalHistory() != null
          ? patient.getMedicalHistory().stream()
                                       .map(this::medicalHistoryToDto)
                                       .collect(Collectors.toList())
          : null)
      .build();
  }

  /**
   * Create dto from jpa model.
   *
   * @param person jpa model.
   * @return created dto.
   */
  private PersonDto personToDto(Person person) {
    return PersonDto.builder()
      .id(person.getId())
      .nationalId(person.getNationalId())
      .firstName(person.getFirstName())
      .lastName(person.getLastName())
      .dateOfBirth(person.getDateOfBirth())
      .sex(person.getSex())
      .isDoBEstimated(person.getIsDoBEstimated())
      .physicalAddress(person.getPhysicalAddress())
      .nextOfKinFullName(person.getNextOfKinFullName())
      .nextOfKinContact(person.getNextOfKinContact())
      .motherMaidenName(person.getMotherMaidenName())
      .deceased(person.getDeceased())
      .retired(person.getRetired())
      .contacts(person.getContacts() != null 
          ? person.getContacts().stream()
                                .map(this::contactToDto)
                                .collect(Collectors.toList())
          : null)
      .build();
  }

  /**
   * Convert Contact entity to ContactDto.
   *
   * @param contact Contact entity.
   * @return ContactDto.
   */
  private ContactDto contactToDto(Contact contact) {
    if (contact == null) {
      return null;
    }

    return ContactDto.builder()
      .id(contact.getId())
      .contactType(contact.getContactType())
      .contactValue(contact.getContactValue())
      .build();
  }

  /**
   * Convert MedicalHistory entity to MedicalHistoryDto.
   *
   * @param medicalHistory MedicalHistory entity.
   * @return ContactDto.
   */
  private MedicalHistoryDto medicalHistoryToDto(MedicalHistory medicalHistory) {
    if (medicalHistory == null) {
      return null;
    }

    return MedicalHistoryDto.builder()
      .id(medicalHistory.getId())
      .type(medicalHistory.getType())
      .history(medicalHistory.getHistory())
      .build();
  }
}
