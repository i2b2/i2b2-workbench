//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.2-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.21 at 10:39:09 AM EDT 
//


package edu.harvard.i2b2.crcxmljaxb.datavo.psm.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for query_definition_requestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="query_definition_requestType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.i2b2.org/xsd/cell/crc/psm/1.1/}requestType">
 *       &lt;sequence>
 *         &lt;element name="query_definition" type="{http://www.i2b2.org/xsd/cell/crc/psm/querydefinition/1.1/}query_definitionType"/>
 *         &lt;element name="result_output_list" type="{http://www.i2b2.org/xsd/cell/crc/psm/1.1/}result_output_optionListType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "query_definition_requestType", propOrder = {
    "queryDefinition",
    "resultOutputList"
})
public class QueryDefinitionRequestType
    extends RequestType
{

    @XmlElement(name = "query_definition", required = true)
    protected QueryDefinitionType queryDefinition;
    @XmlElement(name = "result_output_list", required = true)
    protected ResultOutputOptionListType resultOutputList;

    /**
     * Gets the value of the queryDefinition property.
     * 
     * @return
     *     possible object is
     *     {@link QueryDefinitionType }
     *     
     */
    public QueryDefinitionType getQueryDefinition() {
        return queryDefinition;
    }

    /**
     * Sets the value of the queryDefinition property.
     * 
     * @param value
     *     allowed object is
     *     {@link QueryDefinitionType }
     *     
     */
    public void setQueryDefinition(QueryDefinitionType value) {
        this.queryDefinition = value;
    }

    /**
     * Gets the value of the resultOutputList property.
     * 
     * @return
     *     possible object is
     *     {@link ResultOutputOptionListType }
     *     
     */
    public ResultOutputOptionListType getResultOutputList() {
        return resultOutputList;
    }

    /**
     * Sets the value of the resultOutputList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultOutputOptionListType }
     *     
     */
    public void setResultOutputList(ResultOutputOptionListType value) {
        this.resultOutputList = value;
    }

}