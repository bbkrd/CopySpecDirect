/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
        String value = spec.toLongString() + "::" + name;
        if (value.length() > MAX_LENGTH) {
            return value.substring(0, MAX_LENGTH) + "...";
        }
        return value;
    }
}
