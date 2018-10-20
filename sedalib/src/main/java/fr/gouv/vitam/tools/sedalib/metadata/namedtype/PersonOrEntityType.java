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
 * circulated by CEA, CNRS and INRIA archiveDeliveryRequestReply the following URL "http://www.cecill.info".
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
package fr.gouv.vitam.tools.sedalib.metadata.namedtype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import fr.gouv.vitam.tools.sedalib.metadata.*;
import fr.gouv.vitam.tools.sedalib.utils.SEDALibException;
import fr.gouv.vitam.tools.sedalib.xml.SEDAXMLEventReader;

/**
 * The Class PersonOrEntityType.
 * <p>
 * For abstract person type SEDA metadata
 */
public class PersonOrEntityType extends ComplexListType {

    /** Init the metadata possibilities. */ {
        initMetadataOrderedList();
        initMetadataMap();
    }

    /**
     * The metadata ordered list.
     */
    protected static List<String> metadataOrderedList;

    /**
     * The metadata map.
     */
    protected static HashMap<String, MetadataKind> metadataMap;

    /**
     * Instantiates a new person type.
     *
     * @param elementName the element name
     */
    public PersonOrEntityType(String elementName) {
        super(elementName);
    }

    /**
     * Instantiates a new person type with firstname and birthname.
     *
     * @param elementName the element name
     * @param firstName   the first name
     * @param birthName   the birth name
     */
    public PersonOrEntityType(String elementName, String firstName, String birthName) {
        super(elementName);
        try {
            addNewMetadata("FirstName", firstName);
            addNewMetadata("BirthName", birthName);
        } catch (SEDALibException ignored) {
        }
    }

    /**
     * Instantiates a new person type with firstname, birthname and identifier.
     *
     * @param elementName the element name
     * @param firstName   the first name
     * @param birthName   the birth name
     * @param identifier  the identifier
     */
    public PersonOrEntityType(String elementName, String firstName, String birthName, String identifier) {
        super(elementName);
        try {
            addNewMetadata("FirstName", firstName);
            addNewMetadata("BirthName", birthName);
            addNewMetadata("Identifier", identifier);
        } catch (SEDALibException ignored) {
        }
    }

    /**
     * Instantiates a new person type from args.
     *
     * @param elementName the XML element name
     * @param args        the generic args for NameTypeMetadata construction
     * @throws SEDALibException if args are not suitable for constructor
     */
    public PersonOrEntityType(String elementName, Object[] args) throws SEDALibException {
        super(elementName);
        if ((args.length == 2) && (args[0] instanceof String) && (args[1] instanceof String)) {
            addNewMetadata("FirstName", (String) args[0]);
            addNewMetadata("BirthName", (String) args[1]);
        } else if ((args.length == 3) && (args[0] instanceof String) && (args[1] instanceof String) && (args[2] instanceof String)) {
            addNewMetadata("FirstName", (String) args[0]);
            addNewMetadata("BirthName", (String) args[1]);
            addNewMetadata("Identifier", (String) args[2]);
        } else
            throw new SEDALibException("Mauvais arguments pour le constructeur de l'élément [" + elementName + "]");
    }

    /**
     * Import the PersonOrEntityType in XML expected form for the SEDA Manifest.
     *
     * @param xmlReader the SEDAXMLEventReader reading the SEDA manifest
     * @return the read PersonOrEntityType
     * @throws SEDALibException if the XML can't be read or the SEDA scheme is not                          respected
     */
    public static PersonOrEntityType fromSedaXml(SEDAXMLEventReader xmlReader) throws SEDALibException {
        XMLEvent event;
        try {
            event = xmlReader.peekUsefullEvent();
            PersonOrEntityType personType = new PersonOrEntityType(event.asStartElement().getName().getLocalPart());
            fromSedaXmlInObject(xmlReader, personType);
            return personType;
        } catch (XMLStreamException e) {
            throw new SEDALibException("Erreur de lecture XML dans un élément de type PersonOrEntityType\n->" + e.getMessage());
        }
    }

    // Init

    /**
     * Init metadata ordered list.
     */
    protected void initMetadataOrderedList() {
        metadataOrderedList = new ArrayList<String>();
        metadataOrderedList.add("FirstName");
        metadataOrderedList.add("BirthName");
        metadataOrderedList.add("FullName");
        metadataOrderedList.add("GivenName");
        metadataOrderedList.add("Gender");
        metadataOrderedList.add("BirthDate");
        metadataOrderedList.add("BirthPlace");
        metadataOrderedList.add("DeathDate");
        metadataOrderedList.add("DeathPlace");
        metadataOrderedList.add("Nationality");
        metadataOrderedList.add("Corpname");
        metadataOrderedList.add("Identifier");
    }

    /**
     * Init metadata map.
     */
    protected void initMetadataMap() {
        metadataMap = new HashMap<String, ComplexListType.MetadataKind>();
        metadataMap.put("FirstName", new ComplexListType.MetadataKind(StringType.class, false));
        metadataMap.put("BirthName", new ComplexListType.MetadataKind(StringType.class, false));
        metadataMap.put("FullName", new ComplexListType.MetadataKind(StringType.class, false));
        metadataMap.put("GivenName", new ComplexListType.MetadataKind(StringType.class, false));
        metadataMap.put("Gender", new ComplexListType.MetadataKind(StringType.class, false));
        metadataMap.put("BirthDate", new ComplexListType.MetadataKind(StringType.class, false));
        metadataMap.put("BirthPlace", new ComplexListType.MetadataKind(StringType.class, false));
        metadataMap.put("DeathDate", new ComplexListType.MetadataKind(StringType.class, false));
        metadataMap.put("DeathPlace", new ComplexListType.MetadataKind(StringType.class, false));
        metadataMap.put("Nationality", new ComplexListType.MetadataKind(StringType.class, true));
        metadataMap.put("Corpname", new ComplexListType.MetadataKind(StringType.class, false));
        metadataMap.put("Identifier", new ComplexListType.MetadataKind(StringType.class, true));
    }

    // Getters and setters

    @Override
    public List<String> getMetadataOrderedList() {
        if (metadataOrderedList == null)
            initMetadataOrderedList();
        return metadataOrderedList;
    }

    @Override
    public HashMap<String, MetadataKind> getMetadataMap() {
        if (metadataMap == null)
            initMetadataMap();
        return metadataMap;
    }

    @Override
    public boolean isNotExpendable() {
        return true;
    }
}
