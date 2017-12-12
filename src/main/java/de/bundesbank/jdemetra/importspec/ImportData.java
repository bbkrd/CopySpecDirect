/*
 * Copyright 2017 Deutsche Bundesbank
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl.html
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
package de.bundesbank.jdemetra.importspec;

import ec.satoolkit.ISaSpecification;

/**
 *
 * @author Thomas Witthohn
 */
@lombok.Getter
@lombok.RequiredArgsConstructor
public class ImportData {

    private final int MAX_LENGTH = 35;

    private final ISaSpecification spec;
    private final String name;

    @Override
    public String toString() {
        String value = spec.toLongString() + ":" + name;
        if (value.length() > MAX_LENGTH) {
            return value.substring(0, MAX_LENGTH) + "...";
        }
        return value;
    }
}
