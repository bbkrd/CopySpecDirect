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
package de.bundesbank.jdemetra.importspec;

import ec.nbdemetra.sa.MultiProcessingManager;
import ec.nbdemetra.sa.SaBatchUI;
import ec.nbdemetra.sa.actions.Specification;
import ec.nbdemetra.ui.interchange.Importable;
import ec.nbdemetra.ui.interchange.impl.FileBroker;
import ec.nbdemetra.ws.actions.AbstractViewAction;
import ec.satoolkit.tramoseats.TramoSeatsSpecification;
import ec.satoolkit.x13.X13Specification;
import ec.tss.sa.SaItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
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
          id = "ec.nbdemetra.sa.actions.ImportSpecFromFile")
@ActionRegistration(displayName = "#CTL_ImportSpecFromFile", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH + Specification.PATH, position = 1421)
    ,
    @ActionReference(path = MultiProcessingManager.LOCALPATH + Specification.PATH, position = 1421)
})
@Messages("CTL_ImportSpecFromFile=Import from file")
public final class ImportSpecFromFile extends AbstractViewAction<SaBatchUI> {

    public ImportSpecFromFile() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_ImportSpecFromFile());
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && ui.getSelectionCount() == 1;
    }

    @Override
    protected void process(SaBatchUI cur) {
        SaItem item = cur.getSelection()[0];

        List<ImportData> specs = ImportableSpec.list;
        specs.clear();

        List<Importable> list = new ArrayList<>();
        list.add(new ImportableSpec(X13Specification.class));
        list.add(new ImportableSpec(TramoSeatsSpecification.class));

        FileBroker broker = new FileBroker();
        try {
            broker.performImport(list);
        } catch (IOException ex) {
            Logger.getLogger(ImportSpecFromFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        ImportData spec;
        switch (specs.size()) {
            case 0:
                return;
            case 1:
                spec = specs.get(0);
                break;
            default:
                spec = (ImportData) JOptionPane.showInputDialog(cur, "Spec", "Choose Specification", JOptionPane.QUESTION_MESSAGE, null, specs.toArray(new ImportData[specs.size()]), null);
        }

        if (spec == null) {
            return;
        }
        SaItem newItem = new SaItem(spec.getSpec(), item.getTs());
        newItem.setMetaData(item.getMetaData());
        newItem.setName(item.getRawName());

        cur.getCurrentProcessing().replace(item, newItem);
        cur.redrawAll();
    }
}
