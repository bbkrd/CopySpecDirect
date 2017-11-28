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

import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.interchange.Importable;
import ec.satoolkit.ISaSpecification;
import ec.tss.tsproviders.utils.Parsers;
import ec.tss.xml.information.XmlInformationSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thomas Witthohn
 */
public class ImportableSpec<X extends ISaSpecification> implements Importable {

    public static final List<ImportData> list = new ArrayList<>();
    protected final Class<X> clazz;

    protected ImportableSpec(Class<X> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getDomain() {
        return clazz.getName();
    }

    @Override
    public void importConfig(Config config) throws IllegalArgumentException {
        if (!getDomain().equals(config.getDomain())) {
            return;
        }

        config.getParam("specification")
                .map(Parsers.onJAXB(XmlInformationSet.class)::parse)
                .map(XmlInformationSet::create)
                .map(o -> {
                    X spec;
                    try {
                        spec = clazz.newInstance();
                        spec.read(o);
                        return spec;
                    } catch (InstantiationException | IllegalAccessException ex) {
                        Logger.getLogger(ImportableSpec.class.getName()).log(Level.SEVERE, null, ex);
                        return null;
                    }
                })
                .ifPresent(spec -> {
                    ImportData data = new ImportData(spec, config.getName());
                    list.add(data);
                });
    }

}
