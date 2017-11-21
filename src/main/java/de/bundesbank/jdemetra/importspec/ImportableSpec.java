/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
                    } catch (InstantiationException | IllegalAccessException ex) {
                        Logger.getLogger(ImportableSpec.class.getName()).log(Level.SEVERE, null, ex);
                        return null;
                    }
                    spec.read(o);
                    return spec;
                })
                .ifPresent(spec -> {
                    ImportData data = new ImportData(spec, config.getName());
                    list.add(data);
                });
    }

}
