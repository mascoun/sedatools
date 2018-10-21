/**
 * Copyright French Prime minister Office/DINSIC/Vitam Program (2015-2019)
 *
 * contact.vitam@programmevitam.fr
 * 
 * This software is developed as a validation helper tool, for constructing Submission Information Packages (archives 
 * sets) in the Vitam program whose purpose is to implement a digital archiving back-office system managing high 
 * volumetry securely and efficiently.
 *
 * This software is governed by the CeCILL 2.1 license under French law and abiding by the rules of distribution of free
 * software. You can use, modify and/ or redistribute the software under the terms of the CeCILL 2.1 license as
 * circulated by CEA, CNRS and INRIA archiveTransfer the following URL "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and rights to copy, modify and redistribute granted by the license,
 * users are provided only with a limited warranty and the software's author, the holder of the economic rights, and the
 * successive licensors have only limited liability.
 *
 * In this respect, the user's attention is drawn to the risks associated with loading, using, modifying and/or
 * developing or reproducing the software by the user in light of its specific status of free software, that may mean
 * that it is complicated to manipulate, and that also therefore means that it is reserved for developers and
 * experienced professionals having in-depth computer knowledge. Users are therefore encouraged to load and test the
 * software's suitability as regards their requirements in conditions enabling the security of their systems and/or data
 * to be ensured and, more generally, to use and operate it in the same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had knowledge of the CeCILL 2.1 license and that you
 * accept its terms.
 */
package fr.gouv.vitam.tools.resip.frame;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import fr.gouv.vitam.tools.resip.parameters.MailImportContext;

/**
 * The Class MailImportContextDialog.
 */
public class MailImportContextDialog extends JDialog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4092514078236156035L;

	/** The return value. */
	public int returnValue;

	/** The message file check box. */
	private JCheckBox messageFileCheckBox;
	
	/** The attachement file check box. */
	private JCheckBox attachementFileCheckBox;
	
	/** The message metadata check box. */
	private JCheckBox messageMetadataCheckBox;
	
	/** The attachement metadata check box. */
	private JCheckBox attachementMetadataCheckBox;
	
	/** The pst radio button. */
	private JRadioButton pstRadioButton;
	
	/** The msg radio button. */
	private JRadioButton msgRadioButton;
	
	/** The tdb radio button. */
	private JRadioButton tdbRadioButton;
	
	/** The mbox radio button. */
	private JRadioButton mboxRadioButton;

	/**
	 * Create the dialog.
	 *
	 * @param owner the owner
	 * @param mailImportContext the mail import context
	 */
	public MailImportContextDialog(JFrame owner, MailImportContext mailImportContext) {
		super(owner, "Edition des paramètres d'extraction des courriels", true);
		getContentPane().setMinimumSize(new Dimension(600, 50));

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[] { 1.0, 0.1 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0 };
		getContentPane().setLayout(gridBagLayout);

		// Parameters Panel

		JPanel parametersPanel = new JPanel();
		GridBagConstraints gbc_parametersPanel = new GridBagConstraints();
		gbc_parametersPanel.anchor = GridBagConstraints.EAST;
		gbc_parametersPanel.insets = new Insets(5, 5, 5, 5);
		gbc_parametersPanel.gridwidth = 2;
		gbc_parametersPanel.gridx = 0;
		gbc_parametersPanel.gridy = 0;
		getContentPane().add(parametersPanel, gbc_parametersPanel);
		GridBagLayout gbl_parametersPanel = new GridBagLayout();
		gbl_parametersPanel.columnWeights = new double[] { 0.1, 0.5, 0.5 };
		parametersPanel.setLayout(gbl_parametersPanel);

		JLabel protocolLabel = new JLabel("Protocole d'extraction courriel:");
		GridBagConstraints gbc_protocolLabel = new GridBagConstraints();
		gbc_protocolLabel.anchor = GridBagConstraints.EAST;
		gbc_protocolLabel.insets = new Insets(0, 0, 0, 5);
		gbc_protocolLabel.gridx = 0;
		gbc_protocolLabel.gridy = 1;
		parametersPanel.add(protocolLabel, gbc_protocolLabel);

		pstRadioButton = new JRadioButton("Outlook-Pst");
		GridBagConstraints gbc_pstRadioButton = new GridBagConstraints();
		gbc_pstRadioButton.anchor = GridBagConstraints.WEST;
		gbc_pstRadioButton.insets = new Insets(0, 0, 0, 5);
		gbc_pstRadioButton.gridx = 1;
		gbc_pstRadioButton.gridy = 1;
		parametersPanel.add(pstRadioButton, gbc_pstRadioButton);

		msgRadioButton = new JRadioButton("Outlook-Msg");
		GridBagConstraints gbc_msgRadioButton = new GridBagConstraints();
		gbc_msgRadioButton.anchor = GridBagConstraints.WEST;
		gbc_msgRadioButton.insets = new Insets(0, 0, 0, 5);
		gbc_msgRadioButton.gridx = 2;
		gbc_msgRadioButton.gridy = 1;
		parametersPanel.add(msgRadioButton, gbc_msgRadioButton);

		tdbRadioButton = new JRadioButton("Thunderbird");
		GridBagConstraints gbc_tdbRadioButton = new GridBagConstraints();
		gbc_tdbRadioButton.anchor = GridBagConstraints.WEST;
		gbc_tdbRadioButton.insets = new Insets(0, 0, 0, 5);
		gbc_tdbRadioButton.gridx = 1;
		gbc_tdbRadioButton.gridy = 2;
		parametersPanel.add(tdbRadioButton, gbc_tdbRadioButton);

		mboxRadioButton = new JRadioButton("Mbox");
		GridBagConstraints gbc_mboxRadioButton = new GridBagConstraints();
		gbc_mboxRadioButton.anchor = GridBagConstraints.WEST;
		gbc_mboxRadioButton.insets = new Insets(0, 0, 0, 5);
		gbc_mboxRadioButton.gridx = 2;
		gbc_mboxRadioButton.gridy =2;
		parametersPanel.add(mboxRadioButton, gbc_mboxRadioButton);

		ButtonGroup protocolButtonGroup = new ButtonGroup();
		protocolButtonGroup.add(pstRadioButton);
		protocolButtonGroup.add(msgRadioButton);
		protocolButtonGroup.add(tdbRadioButton);
		protocolButtonGroup.add(mboxRadioButton);
		pstRadioButton.setSelected("pst".equals(mailImportContext.getProtocol()));
		msgRadioButton.setSelected("msg".equals(mailImportContext.getProtocol()));
		tdbRadioButton.setSelected("thunderbird".equals(mailImportContext.getProtocol()));
		mboxRadioButton.setSelected("mbox".equals(mailImportContext.getProtocol()));

		JLabel mailFileTextExtractAULabel = new JLabel("Extraction des fichiers textes des courriels:");
		GridBagConstraints gbc_mailFileTextExtractAULabel = new GridBagConstraints();
		gbc_mailFileTextExtractAULabel.anchor = GridBagConstraints.EAST;
		gbc_mailFileTextExtractAULabel.insets = new Insets(0, 0, 5, 5);
		gbc_mailFileTextExtractAULabel.gridx = 0;
		gbc_mailFileTextExtractAULabel.gridy = 3;
		parametersPanel.add(mailFileTextExtractAULabel, gbc_mailFileTextExtractAULabel);

		messageFileCheckBox = new JCheckBox("des messages");
		GridBagConstraints gbc_messageFileCheckBox = new GridBagConstraints();
		gbc_messageFileCheckBox.anchor = GridBagConstraints.WEST;
		gbc_messageFileCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_messageFileCheckBox.gridx = 1;
		gbc_messageFileCheckBox.gridy = 3;
		parametersPanel.add(messageFileCheckBox, gbc_messageFileCheckBox);
		messageFileCheckBox.setSelected(mailImportContext.isExtractMessageTextFile());

		attachementFileCheckBox = new JCheckBox("des pièces jointes");
		GridBagConstraints gbc_attachementFileCheckBox = new GridBagConstraints();
		gbc_attachementFileCheckBox.anchor = GridBagConstraints.WEST;
		gbc_attachementFileCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_attachementFileCheckBox.gridx = 2;
		gbc_attachementFileCheckBox.gridy = 3;
		parametersPanel.add(attachementFileCheckBox, gbc_attachementFileCheckBox);
		attachementFileCheckBox.setSelected(mailImportContext.isExtractAttachmentTextFile());

		JLabel mailMetadataTextExtractAULabel = new JLabel("Extraction des métadonnées textuelles des courriels:");
		GridBagConstraints gbc_mailMetadataTextExtractAULabel = new GridBagConstraints();
		gbc_mailMetadataTextExtractAULabel.anchor = GridBagConstraints.EAST;
		gbc_mailMetadataTextExtractAULabel.insets = new Insets(0, 0, 5, 5);
		gbc_mailMetadataTextExtractAULabel.gridx = 0;
		gbc_mailMetadataTextExtractAULabel.gridy = 4;
		parametersPanel.add(mailMetadataTextExtractAULabel, gbc_mailMetadataTextExtractAULabel);

		messageMetadataCheckBox = new JCheckBox("des messages");
		GridBagConstraints gbc_messageMetadataCheckBox = new GridBagConstraints();
		gbc_messageMetadataCheckBox.anchor = GridBagConstraints.WEST;
		gbc_messageMetadataCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_messageMetadataCheckBox.gridx = 1;
		gbc_messageMetadataCheckBox.gridy = 4;
		parametersPanel.add(messageMetadataCheckBox, gbc_messageMetadataCheckBox);
		messageMetadataCheckBox.setSelected(mailImportContext.isExtractMessageTextMetadata());

		attachementMetadataCheckBox = new JCheckBox("des pièces jointes");
		GridBagConstraints gbc_attachementMetadataCheckBox = new GridBagConstraints();
		gbc_attachementMetadataCheckBox.anchor = GridBagConstraints.WEST;
		gbc_attachementMetadataCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_attachementMetadataCheckBox.gridx = 2;
		gbc_attachementMetadataCheckBox.gridy = 4;
		parametersPanel.add(attachementMetadataCheckBox, gbc_attachementMetadataCheckBox);
		attachementMetadataCheckBox.setSelected(mailImportContext.isExtractAttachmentTextMetadata());

		// Buttons
		JButton cancelButton = new JButton("Annuler");
		GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.insets = new Insets(5, 5, 5, 5);
		gbc_cancelButton.gridx = 0;
		gbc_cancelButton.gridy = 1;
		getContentPane().add(cancelButton, gbc_cancelButton);
		cancelButton.addActionListener(arg0 -> buttonCancel());

		JButton okButton = new JButton("OK");
		GridBagConstraints gbc_okButton = new GridBagConstraints();
		gbc_okButton.insets = new Insets(5, 5, 5, 5);
		gbc_okButton.gridx = 1;
		gbc_okButton.gridy = 1;
		getContentPane().add(okButton, gbc_okButton);
		okButton.addActionListener(arg0 -> buttonOk());

		pack();
		setLocationRelativeTo(owner);
	}

	/**
	 * Button cancel.
	 */
	public void buttonCancel() {
		returnValue = JOptionPane.CANCEL_OPTION;
		setVisible(false);
	}

	/**
	 * Button ok.
	 */
	public void buttonOk() {
		returnValue = JOptionPane.OK_OPTION;
		setVisible(false);
	}

	/**
	 * Sets the mail import context from dialog.
	 *
	 * @param mailImportContext the mail import context
	 */
	public void setMailImportContextFromDialog(MailImportContext mailImportContext) {
		mailImportContext.setExtractMessageTextFile(messageFileCheckBox.isSelected());
		mailImportContext.setExtractMessageTextMetadata(messageMetadataCheckBox.isSelected());
		mailImportContext.setExtractAttachmentTextFile(attachementFileCheckBox.isSelected());
		mailImportContext.setExtractAttachmentTextMetadata(attachementMetadataCheckBox.isSelected());
		mailImportContext.setExtractMessageTextFile(messageFileCheckBox.isSelected());
		if (pstRadioButton.isSelected())
			mailImportContext.setProtocol("pst");
		else if (msgRadioButton.isSelected())
			mailImportContext.setProtocol("msg");
		else if (tdbRadioButton.isSelected())
			mailImportContext.setProtocol("thunderbird");
		else if (mboxRadioButton.isSelected())
			mailImportContext.setProtocol("mbox");
	}
}