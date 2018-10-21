/**
 * Copyright French Prime minister Office/DINSIC/Vitam Program (2015-2019)
 * <p>
 * contact.vitam@programmevitam.fr
 * <p>
 * This software is developed as a validation helper tool, for constructing Submission Information Packages (archives
 * sets) in the Vitam program whose purpose is to implement a digital archiving back-office system managing high
 * volumetry securely and efficiently.
 * <p>
 * This software is governed by the CeCILL 2.1 license under French law and abiding by the rules of distribution of free
 * software. You can use, modify and/ or redistribute the software under the terms of the CeCILL 2.1 license as
 * circulated by CEA, CNRS and INRIA archiveTransfer the following URL "http://www.cecill.info".
 * <p>
 * As a counterpart to the access to the source code and rights to copy, modify and redistribute granted by the license,
 * users are provided only with a limited warranty and the software's author, the holder of the economic rights, and the
 * successive licensors have only limited liability.
 * <p>
 * In this respect, the user's attention is drawn to the risks associated with loading, using, modifying and/or
 * developing or reproducing the software by the user in light of its specific status of free software, that may mean
 * that it is complicated to manipulate, and that also therefore means that it is reserved for developers and
 * experienced professionals having in-depth computer knowledge. Users are therefore encouraged to load and test the
 * software's suitability as regards their requirements in conditions enabling the security of their systems and/or data
 * to be ensured and, more generally, to use and operate it in the same conditions as regards security.
 * <p>
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL 2.1 license and that you
 * accept its terms.
 */
package fr.gouv.vitam.tools.resip.app;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import javax.swing.*;

import fr.gouv.vitam.tools.resip.data.Work;
import fr.gouv.vitam.tools.resip.frame.*;
import fr.gouv.vitam.tools.mailextract.lib.core.StoreExtractor;
import fr.gouv.vitam.tools.resip.parameters.DiskImportContext;
import fr.gouv.vitam.tools.resip.parameters.ExportContext;
import fr.gouv.vitam.tools.resip.parameters.MailImportContext;
import fr.gouv.vitam.tools.resip.parameters.CreationContext;
import fr.gouv.vitam.tools.resip.parameters.DIPImportContext;
import fr.gouv.vitam.tools.resip.parameters.Prefs;
import fr.gouv.vitam.tools.resip.parameters.SIPImportContext;
import fr.gouv.vitam.tools.resip.utils.ResipException;
import fr.gouv.vitam.tools.resip.utils.ResipLogger;
import fr.gouv.vitam.tools.sedalib.core.DataObjectPackage;
import fr.gouv.vitam.tools.sedalib.droid.DroidIdentifier;

public class ResipGraphicApp implements ActionListener, Runnable {

    // Uniq instance. */
    static ResipGraphicApp theApp = null;

    // Data elements. */
    public Work launchWork;
    public Work currentWork;
    public boolean modifiedWork;
    public String filenameWork;

    // GUI elements. */
    public MainWindow mainWindow;

    // MainWindow menu elements dis/enabled depending on work state and used by controller. */
    private JMenuItem saveMenuItem, saveAsMenuItem, closeMenuItem;
    private JMenu contextMenu, exportMenu;
    private Map<JMenuItem, String> actionByMenuItem = new HashMap<JMenuItem, String>();

    // Thread control elements. */
    public boolean importThreadRunning;
    public boolean addThreadRunning;
    public boolean exportThreadRunning;

    public ResipGraphicApp(CreationContext creationContext, ExportContext exportContext) throws ResipException {
        if (theApp != null)
            throw new ResipException("L'application a déjà été lancée");
        theApp = this;

        // inner variables
        this.launchWork = new Work(null, creationContext, exportContext);
        this.currentWork = null;
        this.modifiedWork = false;
        this.filenameWork = null;
        this.importThreadRunning = false;
        this.exportThreadRunning = false;
        this.addThreadRunning = false;

        // prefs init
        Prefs.getInstance();

        // identification objects initialization
        try {
            DroidIdentifier.getInstance();
        } catch (Exception e) {
            ResipLogger.getGlobalLogger().log(ResipLogger.ERROR, "Erreur fatale, impossible de créer les outils Droid");
            System.exit(1);
        }
        EventQueue.invokeLater(this);
    }

    public void run() {
        try {
            mainWindow = new MainWindow(this);
            mainWindow.setVisible(true);

            if (launchWork.getCreationContext() != null) {
                if (launchWork.getCreationContext() instanceof DiskImportContext) {
                    currentWork = launchWork;
                    importFromDisk(launchWork);
                } else if (launchWork.getCreationContext() instanceof SIPImportContext) {
                    currentWork = launchWork;
                    importFromSIP(launchWork);
                } else {
                    currentWork = new Work(new DataObjectPackage(), launchWork.getCreationContext(),
                            launchWork.getExportContext());
                    mainWindow.load();
                }
            } else {
                currentWork = new Work(new DataObjectPackage(),
                        new CreationContext(Prefs.getInstance().getPrefsContextNode()), launchWork.getExportContext());
                mainWindow.load();
            }

            StoreExtractor.initDefaultExtractors();
        } catch (Exception e) {
            System.err.println("Resip.Graphic: Erreur fatale, exécution interrompue (" + e.getMessage() + ")");
            System.exit(1);
        }
    }

    @SuppressWarnings("SameReturnValue")
    public static String getAppName() {
        return "Resip";
    }

    // Menu controller

    public JMenuBar createMenu() {
        JMenuBar menuBar;
        JMenu importMenu, fileMenu;
        JMenuItem menuItem;

        menuBar = new JMenuBar();
        fileMenu = new JMenu("Fichier");
        menuBar.add(fileMenu);

        menuItem = new JMenuItem("Charger...");
        menuItem.addActionListener(this);
        actionByMenuItem.put(menuItem, "LoadWork");
        fileMenu.add(menuItem);

        saveMenuItem = new JMenuItem("Sauver");
        saveMenuItem.addActionListener(this);
        saveMenuItem.setEnabled(false);
        actionByMenuItem.put(saveMenuItem, "SaveWork");
        fileMenu.add(saveMenuItem);

        saveAsMenuItem = new JMenuItem("Sauver sous...");
        saveAsMenuItem.addActionListener(this);
        saveAsMenuItem.setEnabled(false);
        actionByMenuItem.put(saveAsMenuItem, "SaveAsWork");
        fileMenu.add(saveAsMenuItem);

        closeMenuItem = new JMenuItem("Fermer");
        closeMenuItem.addActionListener(this);
        closeMenuItem.setEnabled(false);
        actionByMenuItem.put(closeMenuItem, "CloseWork");
        fileMenu.add(closeMenuItem);

        fileMenu.add(new JSeparator());

        menuItem = new JMenuItem("Préférences...");
        menuItem.addActionListener(this);
        actionByMenuItem.put(menuItem, "EditPrefs");
        fileMenu.add(menuItem);

        contextMenu = new JMenu("Contexte");
        menuBar.add(contextMenu);
        contextMenu.setEnabled(false);

        menuItem = new JMenuItem("Voir le contexte d'import...");
        menuItem.addActionListener(this);
        actionByMenuItem.put(menuItem, "SeeImportContext");
        contextMenu.add(menuItem);

        menuItem = new JMenuItem("Editer le contexte d'export...");
        menuItem.addActionListener(this);
        actionByMenuItem.put(menuItem, "EditSIPExportContext");
        contextMenu.add(menuItem);

        menuItem = new JMenuItem("Régénérer des ID continus");
        menuItem.addActionListener(this);
        actionByMenuItem.put(menuItem, "RegenerateContinuousIds");
        contextMenu.add(menuItem);

        importMenu = new JMenu("Import");
        menuBar.add(importMenu);

        menuItem = new JMenuItem("Importer depuis un répertoire...");
        menuItem.addActionListener(this);
        actionByMenuItem.put(menuItem, "ImportFromDisk");
        importMenu.add(menuItem);

        menuItem = new JMenuItem("Importer depuis un SIP...");
        menuItem.addActionListener(this);
        actionByMenuItem.put(menuItem, "ImportFromSIP");
        importMenu.add(menuItem);

        menuItem = new JMenuItem("Importer depuis un DIP...");
        menuItem.addActionListener(this);
        actionByMenuItem.put(menuItem, "ImportFromDIP");
        importMenu.add(menuItem);

        menuItem = new JMenuItem("Importer depuis un conteneur courriels...");
        menuItem.addActionListener(this);
        actionByMenuItem.put(menuItem, "ImportFromMail");
        importMenu.add(menuItem);

        exportMenu = new JMenu("Export");
        exportMenu.setEnabled(false);
        menuBar.add(exportMenu);

        menuItem = new JMenuItem("Exporter le SIP SEDA...");
        menuItem.addActionListener(this);
        actionByMenuItem.put(menuItem, "ExportToSEDASIP");
        exportMenu.add(menuItem);

        menuItem = new JMenuItem("Exporter le manifest Xml SEDA...");
        menuItem.addActionListener(this);
        actionByMenuItem.put(menuItem, "ExportToSEDAXMLManifest");
        exportMenu.add(menuItem);

        menuItem = new JMenuItem("Exporter la hiérarchie sur disque...");
        menuItem.addActionListener(this);
        actionByMenuItem.put(menuItem, "ExportToDisk");
        exportMenu.add(menuItem);

        return menuBar;
    }

    public void actionPerformed(final ActionEvent actionEvent) {
        Object source = actionEvent.getSource();

        if (source instanceof JMenuItem) {
            String action = actionByMenuItem.get(source);
            if (action == null)
                System.err.println("unknown menu action");
            else
                switch (action) {
                    // File Menu
                    case "LoadWork":
                        loadWork();
                        break;
                    case "SaveWork":
                        saveWork();
                        break;
                    case "SaveAsWork":
                        saveAsWork();
                        break;
                    case "CloseWork":
                        closeWork();
                        break;
                    case "EditPrefs":
                        editPrefs();
                        break;
                    // Context Menu
                    case "SeeImportContext":
                        seeImportContext();
                        break;
                    case "EditSIPExportContext":
                        editExportContext();
                        break;
                    case "RegenerateContinuousIds":
                        doRegenerateContinuousIds();
                        break;
                    // Import Menu
                    case "ImportFromSIP":
                        importFromSIP();
                        break;
                    case "ImportFromDIP":
                        importFromDIP();
                        break;
                    case "ImportFromDisk":
                        importFromDisk();
                        break;
                    case "ImportFromMail":
                        importFromMail();
                        break;
                    // Export Menu
                    case "ExportToSEDASIP":
                        exportToSEDASIP();
                        break;
                    case "ExportToSEDAXMLManifest":
                        exportToSEDAXMLManifest();
                        break;
                    case "ExportToDisk":
                        exportToDisk();
                        break;
                }
        }
    }

    // Utils

    public static ResipGraphicApp getTheApp() {
        return theApp;
    }

    public void setModifiedContext(boolean isModified) {
        modifiedWork = isModified;
        saveMenuItem.setEnabled(modifiedWork && (filenameWork != null));
    }

    public void setContextLoaded(boolean isLoaded) {
        contextMenu.setEnabled(isLoaded);
        exportMenu.setEnabled(isLoaded);
        saveAsMenuItem.setEnabled(isLoaded);
        closeMenuItem.setEnabled(isLoaded);
    }

    public void setFilenameWork(String fileName) {
        filenameWork = fileName;
        saveMenuItem.setEnabled((filenameWork != null) && modifiedWork);
    }

    // File Menu

    // MenuItem Load

    private void loadWork() {
        String filename = "Non défini";
        try {
            if (importThreadRunning) {
                JOptionPane.showMessageDialog(mainWindow,
                        "Un import est en cours vous devez l'annuler ou attendre la fin avant de faire un chargement.",
                        "Alerte", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if ((currentWork != null) && modifiedWork
                    && (JOptionPane.showConfirmDialog(mainWindow,
                    "Vous avez un contexte en cours non sauvegardé, un chargement l'écrasera.\n"
                            + "Voulez-vous continuer?",
                    "Confirmation", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION))
                return;

            JFileChooser fileChooser = new JFileChooser(Prefs.getInstance().getPrefsLoadDir());
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (fileChooser.showOpenDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
                filename = fileChooser.getSelectedFile().getCanonicalPath();
                currentWork = Work.createFromFile(filename);
                ResipLogger.getGlobalLogger().log(ResipLogger.GLOBAL, "Fichier [" + filename + "] chargé");
                mainWindow.load();
                setFilenameWork(filename);
                setContextLoaded(true);
                setModifiedContext(false);
                Prefs.getInstance().setPrefsLoadDirFromChild(filename);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainWindow,
                    "Erreur de chargement de [" + filename + "]\n->" + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            ResipLogger.getGlobalLogger().log(ResipLogger.STEP, "Erreur de chargement de [" + filename + "]\n->" + e.getMessage());
        }
    }

    // MenuItem Save

    private void saveWork() {
        if (filenameWork != null)
            try {
                currentWork.save(filenameWork);
                setModifiedContext(false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(mainWindow,
                        "Erreur de sauvegarde de [" + filenameWork + "]\n->" + e.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                ResipLogger.getGlobalLogger().log(ResipLogger.STEP,
                        "Resip.Graphic: Erreur de sauvegarde de [" + filenameWork + "]\n->" + e.getMessage());
            }
    }

    // MenuItem SaveAs

    private void saveAsWork() {
        String filename = "Non défini";
        try {
            JFileChooser fileChooser = new JFileChooser(Prefs.getInstance().getPrefsLoadDir());
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (fileChooser.showSaveDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
                filename = fileChooser.getSelectedFile().getCanonicalPath();
                if (new File(filename).exists()) {
                    int dialogResult = JOptionPane.showConfirmDialog(mainWindow,
                            "Le fichier [" + filename + "] existe. Voulez-vous le remplacer?", "Confirmation",
                            JOptionPane.YES_NO_OPTION);
                    if (dialogResult == JOptionPane.NO_OPTION)
                        return;
                }
                currentWork.save(filename);
                ResipLogger.getGlobalLogger().log(ResipLogger.GLOBAL, "Resip.Graphic: Fichier [" + filename + "] sauvegardé");
                setModifiedContext(false);
                filenameWork = filename;
                Prefs.getInstance().setPrefsLoadDirFromChild(filename);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainWindow,
                    "Erreur de sauvegarde de [" + filename + "]\n->" + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            ResipLogger.getGlobalLogger().log(ResipLogger.STEP, "Erreur de sauvegarde de [" + filename + "]\n->" + e.getMessage());
        }
    }

    // MenuItem Close
    private void closeWork() {
        try {
            if ((currentWork != null) && modifiedWork
                    && JOptionPane.showConfirmDialog(mainWindow,
                    "Vous avez un contexte en cours non sauvegardé, la fermeture perdra les modifications.\n"
                            + "Voulez-vous continuer?",
                    "Confirmation", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
                return;

            currentWork = new Work(new DataObjectPackage(),
                    new CreationContext(Prefs.getInstance().getPrefsContextNode()), launchWork.getExportContext());
            setFilenameWork(null);
            setModifiedContext(false);
            setContextLoaded(false);
            mainWindow.load();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainWindow,
                    "Erreur de fermeture du contexte en cours [" + filenameWork + "]\n->" + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            ResipLogger.getGlobalLogger().log(ResipLogger.STEP, "Resip.Graphic: Erreur de fermeture du contexte en cours ["
                    + filenameWork + "]\n->" + e.getMessage());
        }
    }

    // MenuItem Edit Preferences

    void editPrefs() {
        try {
            PrefsDialog prefsDialog = new PrefsDialog(mainWindow);
            prefsDialog.setVisible(true);
            if (prefsDialog.returnValue == JOptionPane.OK_OPTION) {
                prefsDialog.cc.toPrefs(Prefs.getInstance().getPrefsContextNode());
                prefsDialog.dic.toPrefs(Prefs.getInstance().getPrefsContextNode());
                prefsDialog.mic.toPrefs(Prefs.getInstance().getPrefsContextNode());
                prefsDialog.gmc.toPrefs(Prefs.getInstance().getPrefsContextNode());
            }
        } catch (BackingStoreException e) {
            JOptionPane.showMessageDialog(mainWindow,
                    "Erreur fatale, impossible d'éditer les préférences \n->" + e.getMessage());
            ResipLogger.getGlobalLogger().log(ResipLogger.ERROR,
                    "Resip.GraphicApp: Erreur fatale, impossible d'éditer les préférences \n->" + e.getMessage());

        }
    }

    // Context Menu

    // MenuItem See CreationContext

    void seeImportContext() {
        if (currentWork != null) {
            CreationContext oic = currentWork.getCreationContext();
            if (oic != null) {
                CreationContextDialog creationContextDialog = new CreationContextDialog(mainWindow, oic,
                        currentWork.getDataObjectPackage());
                creationContextDialog.setVisible(true);
            } else
                JOptionPane.showMessageDialog(mainWindow, "Pas de contexte de création défini", "Information", JOptionPane.INFORMATION_MESSAGE);
        } else
            JOptionPane.showMessageDialog(mainWindow, "Pas de contexte ouvert", "Alerte", JOptionPane.WARNING_MESSAGE);
    }

    // MenuItem Edit ExportContext

    void editExportContext() {
        if (currentWork != null) {
             if (currentWork.getExportContext() == null) {
                currentWork.setExportContext(new ExportContext(Prefs.getInstance().getPrefsContextNode()));
            }
            ExportContextDialog exportContextDialog = new ExportContextDialog(mainWindow, currentWork.getExportContext());
            exportContextDialog.setVisible(true);
            if (exportContextDialog.returnValue == JOptionPane.OK_OPTION)
                exportContextDialog.setExportContextFromDialog(currentWork.getExportContext());
        } else
            JOptionPane.showMessageDialog(mainWindow, "Pas de contexte ouvert", "Alerte", JOptionPane.WARNING_MESSAGE);
    }

    // MenuItem Regenerate Continuous ids

    void doRegenerateContinuousIds() {
        if (currentWork != null) {
            currentWork.getDataObjectPackage().regenerateContinuousIds();
            mainWindow.allTreeChanged();
        }
    }

    // Import Menu

    // MenuItem Import SIP

    void importFromSIP() {
        try {
            if (importThreadRunning) {
                JOptionPane.showMessageDialog(mainWindow,
                        "Un import est en cours vous devez l'annuler ou attendre la fin avant de faire un autre import.",
                        "Alerte", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if ((currentWork != null) && modifiedWork
                    && (JOptionPane.showConfirmDialog(mainWindow,
                    "Vous avez un contexte en cours non sauvegardé, un import l'écrasera.\n"
                            + "Voulez-vous continuer?",
                    "Confirmation", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION))
                return;

            JFileChooser fileChooser = new JFileChooser(Prefs.getInstance().getPrefsImportDir());
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (fileChooser.showOpenDialog(this.mainWindow) == JFileChooser.APPROVE_OPTION) {
                CreationContext oic = new SIPImportContext(Prefs.getInstance().getPrefsContextNode());
                oic.setOnDiskInput(fileChooser.getSelectedFile().toString());
                Work work = new Work(null, oic, null);
                importFromSIP(work);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainWindow,
                    "Erreur fatale, impossible de faire l'import \n->" + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            ResipLogger.getGlobalLogger().log(ResipLogger.ERROR, "Erreur fatale, impossible de faire l'import \n->" + e.getMessage());
        }
    }

    void importFromSIP(Work work) {
        try {
            InOutDialog inOutDialog = new InOutDialog(mainWindow, "Import",
                    "Import depuis un fichier SIP en " + work.getCreationContext().getOnDiskInput() + "\n");
            ImportThread importThread = new ImportThread(work, inOutDialog);
            importThread.execute();
            inOutDialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainWindow,
                    "Erreur fatale, impossible de faire l'import \n->" + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            ResipLogger.getGlobalLogger().log(ResipLogger.ERROR, "Erreur fatale, impossible de faire l'import \n->" + e.getMessage());
        }
    }

    // MenuItem Import DIP

    void importFromDIP() {
        try {
            if (importThreadRunning) {
                JOptionPane.showMessageDialog(mainWindow,
                        "Un import est en cours vous devez l'annuler ou attendre la fin avant de faire un autre import.",
                        "Alerte", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if ((currentWork != null) && modifiedWork
                    && (JOptionPane.showConfirmDialog(mainWindow,
                    "Vous avez un contexte en cours non sauvegardé, un import l'écrasera.\n"
                            + "Voulez-vous continuer?",
                    "Confirmation", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION))
                return;

            JFileChooser fileChooser = new JFileChooser(Prefs.getInstance().getPrefsImportDir());
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (fileChooser.showOpenDialog(this.mainWindow) == JFileChooser.APPROVE_OPTION) {
                CreationContext oic = new DIPImportContext(Prefs.getInstance().getPrefsContextNode());
                oic.setOnDiskInput(fileChooser.getSelectedFile().toString());
                Work work = new Work(null, oic, null);
                importFromDIP(work);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainWindow,
                    "Erreur fatale, impossible de faire l'import \n->" + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            ResipLogger.getGlobalLogger().log(ResipLogger.ERROR, "Erreur fatale, impossible de faire l'import \n->" + e.getMessage());
        }
    }

    void importFromDIP(Work work) {
        try {
            InOutDialog inOutDialog = new InOutDialog(mainWindow, "Import",
                    "Import depuis un fichier DIP en " + work.getCreationContext().getOnDiskInput() + "\n");
            ImportThread importThread = new ImportThread(work, inOutDialog);
            importThread.execute();
            inOutDialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainWindow,
                    "Erreur fatale, impossible de faire l'import \n->" + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            ResipLogger.getGlobalLogger().log(ResipLogger.ERROR, "Erreur fatale, impossible de faire l'import \n->" + e.getMessage());
        }
    }

    // MenuItem Import from disk

    void importFromDisk() {
        try {
            if (importThreadRunning) {
                JOptionPane.showMessageDialog(mainWindow,
                        "Un import est en cours vous devez l'annuler ou attendre la fin avant de faire un autre import.",
                        "Alerte", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if ((currentWork != null) && modifiedWork
                    && (JOptionPane.showConfirmDialog(mainWindow,
                    "Vous avez un contexte en cours non sauvegardé, un chargement l'écrasera.\n"
                            + "Voulez-vous continuer?",
                    "Confirmation", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION))
                return;

            JFileChooser fileChooser = new JFileChooser(Prefs.getInstance().getPrefsImportDir());
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fileChooser.showOpenDialog(this.mainWindow) == JFileChooser.APPROVE_OPTION) {
                CreationContext oic = new DiskImportContext(Prefs.getInstance().getPrefsContextNode());
                oic.setOnDiskInput(fileChooser.getSelectedFile().toString());
                Work work = new Work(null, oic, null);
                importFromDisk(work);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainWindow,
                    "Erreur fatale, impossible de faire l'import \n->" + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            ResipLogger.getGlobalLogger().log(ResipLogger.ERROR, "Erreur fatale, impossible de faire l'import \n->" + e.getMessage());
        }
    }

    void importFromDisk(Work work) {
        try {
            InOutDialog inOutDialog = new InOutDialog(mainWindow, "Import",
                    "Import depuis une hiérarchie disque en " + work.getCreationContext().getOnDiskInput() + "\n");
            ImportThread importThread = new ImportThread(work, inOutDialog);
            importThread.execute();
            inOutDialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainWindow,
                    "Erreur fatale, impossible de faire l'import \n->" + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            ResipLogger.getGlobalLogger().log(ResipLogger.ERROR, "Erreur fatale, impossible de faire l'import \n->" + e.getMessage());
        }
    }

    // MenuItem Import from disk

    void importFromMail() {
        try {
            if (importThreadRunning) {
                JOptionPane.showMessageDialog(mainWindow,
                        "Un import est en cours vous devez l'annuler ou attendre la fin avant de faire un autre import.",
                        "Alerte", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if ((currentWork != null) && modifiedWork
                    && (JOptionPane.showConfirmDialog(mainWindow,
                    "Vous avez un contexte en cours non sauvegardé, un chargement l'écrasera.\n"
                            + "Voulez-vous continuer?",
                    "Confirmation", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION))
                return;

            JFileChooser fileChooser = new JFileChooser(Prefs.getInstance().getPrefsImportDir());
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            if (fileChooser.showOpenDialog(this.mainWindow) == JFileChooser.APPROVE_OPTION) {
                MailImportContext mic = new MailImportContext(Prefs.getInstance().getPrefsContextNode());
                mic.setOnDiskInput(fileChooser.getSelectedFile().toString());
                MailImportContextDialog micd = new MailImportContextDialog(mainWindow, mic);
                micd.setVisible(true);
                if (micd.returnValue != JOptionPane.OK_OPTION)
                    return;
                micd.setMailImportContextFromDialog(mic);
                Work work = new Work(null, mic, null);
                importFromMail(work);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainWindow,
                    "Erreur fatale, impossible de faire l'import \n->" + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            ResipLogger.getGlobalLogger().log(ResipLogger.ERROR, "Erreur fatale, impossible de faire l'import \n->" + e.getMessage());
        }
    }

    void importFromMail(Work work) {
        try {
            InOutDialog inOutDialog = new InOutDialog(mainWindow, "Import",
                    "Import depuis un conteneur de courriels en " + work.getCreationContext().getOnDiskInput() + "\n");
            ImportThread importThread = new ImportThread(work, inOutDialog);
            importThread.execute();
            inOutDialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainWindow,
                    "Erreur fatale, impossible de faire l'import \n->" + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            ResipLogger.getGlobalLogger().log(ResipLogger.ERROR, "Erreur fatale, impossible de faire l'import \n->" + e.getMessage());
        }
    }

    // Export Menu

    private boolean completeResipWork() {
        if (currentWork == null)
            return true;
        if (currentWork.getExportContext() == null) {
            ExportContext newExportContext = new ExportContext(Prefs.getInstance().getPrefsContextNode());
            ExportContextDialog exportContextDialog = new ExportContextDialog(mainWindow, newExportContext);
            exportContextDialog.setVisible(true);
            if (exportContextDialog.returnValue == JOptionPane.CANCEL_OPTION)
                return true;
            exportContextDialog.setExportContextFromDialog(newExportContext);
            currentWork.setExportContext(newExportContext);
        }
        return false;
    }

    // MenuItem Export SIP

    private void exportToSEDASIP() {
        try {
            if (completeResipWork())
                return;
            JFileChooser fileChooser;
            if (currentWork.getExportContext().getOnDiskOutput() != null)
                fileChooser = new JFileChooser(currentWork.getExportContext().getOnDiskOutput());
            else
                fileChooser = new JFileChooser(Prefs.getInstance().getPrefsExportDir());
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (fileChooser.showSaveDialog(this.mainWindow) == JFileChooser.APPROVE_OPTION) {
                InOutDialog inOutDialog = new InOutDialog(mainWindow, "Export",
                        "Export du manifest SEDA vers " + fileChooser.getSelectedFile().getAbsolutePath() + "\n");
                currentWork.getExportContext().setOnDiskOutput(fileChooser.getSelectedFile().getAbsolutePath());
                ExportThread exportThread = new ExportThread(currentWork, ExportThread.SIP_EXPORT,inOutDialog);
                exportThread.execute();
                inOutDialog.setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainWindow, "Impossible de faire l'export \n->" + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            ResipLogger.getGlobalLogger().log(ResipLogger.STEP, "Impossible de faire l'export \n->" + e.getMessage());
        }
    }

    // MenuItem Export Manifest

    private void exportToSEDAXMLManifest() {
        try {
            if (completeResipWork())
                return;
            JFileChooser fileChooser;
            if (currentWork.getExportContext().getOnDiskOutput() != null)
                fileChooser = new JFileChooser(currentWork.getExportContext().getOnDiskOutput());
            else
                fileChooser = new JFileChooser(Prefs.getInstance().getPrefsExportDir());
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (fileChooser.showSaveDialog(this.mainWindow) == JFileChooser.APPROVE_OPTION) {
                InOutDialog inOutDialog = new InOutDialog(mainWindow, "Export",
                        "Export du manifest SEDA vers " + fileChooser.getSelectedFile().getAbsolutePath() + "\n");
                currentWork.getExportContext().setOnDiskOutput(fileChooser.getSelectedFile().getAbsolutePath());
                ExportThread exportThread = new ExportThread(currentWork, ExportThread.MANIFEST_EXPORT, inOutDialog);
                exportThread.execute();
                inOutDialog.setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainWindow, "Impossible de faire l'export \n->" + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            ResipLogger.getGlobalLogger().log(ResipLogger.STEP, "Impossible de faire l'export \n->" + e.getMessage());
        }
    }

    // MenuItem Export Manifest

    private void exportToDisk() {
        try {
            if (completeResipWork())
                return;
            JFileChooser fileChooser;
            if (currentWork.getExportContext().getOnDiskOutput() != null)
                fileChooser = new JFileChooser(currentWork.getExportContext().getOnDiskOutput());
            else
                fileChooser = new JFileChooser(Prefs.getInstance().getPrefsExportDir());
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fileChooser.showSaveDialog(this.mainWindow) == JFileChooser.APPROVE_OPTION) {
                InOutDialog inOutDialog = new InOutDialog(mainWindow, "Export",
                        "Export sur disque vers " + fileChooser.getSelectedFile().getAbsolutePath() + "\n");
                currentWork.getExportContext().setOnDiskOutput(fileChooser.getSelectedFile().getAbsolutePath());
                ExportThread exportThread = new ExportThread(currentWork, ExportThread.DISK_EXPORT,inOutDialog);
                exportThread.execute();
                inOutDialog.setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainWindow, "Impossible de faire l'export \n->" + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            ResipLogger.getGlobalLogger().log(ResipLogger.STEP, "Impossible de faire l'export \n->" + e.getMessage());
        }
    }

}