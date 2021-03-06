package fr.gouv.vitam.tools.resip.sedaobjecteditor;

import fr.gouv.vitam.tools.resip.sedaobjecteditor.components.highlevelcomponents.StructuredArchiveUnitEditorPanel;
import fr.gouv.vitam.tools.sedalib.core.ArchiveUnit;
import fr.gouv.vitam.tools.sedalib.core.DataObjectPackage;
import fr.gouv.vitam.tools.sedalib.metadata.ArchiveUnitProfile;
import fr.gouv.vitam.tools.sedalib.metadata.SEDAMetadata;
import fr.gouv.vitam.tools.sedalib.metadata.content.Content;
import fr.gouv.vitam.tools.sedalib.metadata.management.Management;

import javax.swing.*;
import java.awt.*;

public class StructuredArchiveUnitEditorPanelTest {

    public static void main(String[] args) throws Exception {
        if (System.getProperty("os.name").toLowerCase().contains("win"))
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        else
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

        JDialog dialog = new JDialog();
        dialog.setMinimumSize(new Dimension(600, 600));
        dialog.setPreferredSize(new Dimension(600, 600));

        DataObjectPackage dop=new DataObjectPackage();
        ArchiveUnit archiveUnit=new ArchiveUnit();
        SEDAMetadata sedaMetadata;

        archiveUnit.setInDataObjectPackageId("TestID");
        sedaMetadata= SEDAObjectEditor.createSEDAMetadataSample("ArchiveUnitProfile","ArchiveUnitProfile",true);
        archiveUnit.setArchiveUnitProfile((ArchiveUnitProfile)sedaMetadata);
        sedaMetadata= SEDAObjectEditor.createSEDAMetadataSample("Content","Content",true);
        archiveUnit.setContent((Content)sedaMetadata);
        sedaMetadata= SEDAObjectEditor.createSEDAMetadataSample("Management","Management",true);
        archiveUnit.setManagement((Management)sedaMetadata);
        dop.addArchiveUnit(archiveUnit);

        StructuredArchiveUnitEditorPanel sauep=new StructuredArchiveUnitEditorPanel();
        sauep.editArchiveUnit(archiveUnit);

        dialog.setContentPane(sauep);
        dialog.setVisible(true);
    }
}
