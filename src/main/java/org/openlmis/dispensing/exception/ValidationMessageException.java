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

package org.openlmis.dispensing.exception;

import org.openlmis.dispensing.util.Message;

public class ValidationMessageException extends BaseMessageException {

  public ValidationMessageException(Message message) {
    super(message);
  }

  public ValidationMessageException(Throwable cause, Message message) {
    super(cause, message);
  }

  public ValidationMessageException(Throwable cause, String messageKey) {
    super(cause, new Message(messageKey));
  }

  public ValidationMessageException(String messageKey) {
    super(messageKey);
  }
}
