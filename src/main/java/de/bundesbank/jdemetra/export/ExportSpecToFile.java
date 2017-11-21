/*
 * Copyright 2016 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package de.bundesbank.jdemetra.export;

import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.sa.actions.Specification;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.interchange.Exportable;
import ec.nbdemetra.ui.interchange.impl.FileBroker;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import ec.tss.sa.SaItem;
import ec.tss.tsproviders.utils.Formatters;
import ec.tss.tsproviders.utils.IFormatter;
import ec.tss.xml.information.XmlInformationSet;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.information.InformationSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author Thomas Witthohn
 */
@ActionID(category = "SaProcessing",
          id = "ec.nbdemetra.sa.actions.ExportSpecToFile")
@ActionRegistration(displayName = "#CTL_ExportSpecToFile", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH + Specification.PATH, position = 1420)
    ,
    @ActionReference(path = MultiProcessingManager.LOCALPATH + Specification.PATH, position = 1420)
})
@Messages("CTL_ExportSpecToFile=Export to file")
public final class ExportSpecToFile extends AbstractViewAction<SaBatchUI> {

    public ExportSpecToFile() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_ExportSpecToFile());
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null;
    }

    @Override
    protected void process(SaBatchUI cur) {
        try {
            List<Exportable> exportables = new ArrayList<>();
            for (SaItem saItem : cur.getSelection()) {
                exportables.add(new ExportableSpec(saItem));
            }
            FileBroker broker = new FileBroker();
            broker.performExport(exportables);
        } catch (IOException ex) {
            Logger.getLogger(ExportSpecToFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static final class ExportableSpec implements Exportable {

        private final SaItem item;

        ExportableSpec(SaItem input) {
            this.item = input;
        }

        @Override
        public Config exportConfig() {
            IProcSpecification spec = item.getEstimationSpecification();

            InformationSet set = spec.write(true);
            XmlInformationSet xmlSet = new XmlInformationSet();
            xmlSet.copy(set);

            IFormatter<XmlInformationSet> formatter = Formatters.onJAXB(XmlInformationSet.class, true);

            String domain = spec.getClass().getName();

            String name = item.getRawName().isEmpty() ? item.getTs().getRawName() : item.getRawName();
            name = name.replaceAll("[\\\\/:*?\"<>|]", " ");

            return Config.builder(domain, name, "1.0.0")
                    .put("specification", formatter.formatAsString(xmlSet))
                    .build();
        }
    }
}
