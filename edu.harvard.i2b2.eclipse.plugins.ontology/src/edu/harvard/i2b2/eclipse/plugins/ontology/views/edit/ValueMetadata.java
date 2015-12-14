package edu.harvard.i2b2.eclipse.plugins.ontology.views.edit;



import edu.harvard.i2b2.ontclient.datavo.vdo.ValueMetadataType;

public class ValueMetadata {

	
	private static ValueMetadata thisInstance;
	static {
		thisInstance = new ValueMetadata();
	}

	public static ValueMetadata getInstance() {
		return thisInstance;
	}
	
	private ValueMetadataType valueMetadataType ;
	
	private String keyword = null;
	private String val = null;
	private String excludingVal = null;
	private boolean enumType = false;
	private boolean numericType = false;
	private boolean stringType = false;
	
	private String normalUnit = null;
	private String equalUnit = null;
	private String excludingUnit = null;
	private String convertingUnit = null;
	private float  multFactor = 1;
	
	private String exclusionComment = null;
	private String maxStringLength = null;

	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public String getExcludingVal() {
		return excludingVal;
	}
	public void setExcludingVal(String excludingVal) {
		this.excludingVal = excludingVal;
	}
	public boolean isEnumType() {
		return enumType;
	}
	public void setEnumType(boolean enumType) {
		this.enumType = enumType;
	}

	public boolean isNumericType() {
		return numericType;
	}
	public void setNumericType(boolean numericType) {
		this.numericType = numericType;
	}
	
	public boolean isStringType() {
		return stringType;
	}
	public void setStringType(boolean stringType) {
		this.stringType = stringType;
	}
	
	
	public String getNormalUnit() {
		return normalUnit;
	}
	public void setNormalUnit(String normalUnit) {
		this.normalUnit = normalUnit;
	}
	public String getEqualUnit() {
		return equalUnit;
	}
	public void setEqualUnit(String equalUnit) {
		this.equalUnit = equalUnit;
	}
	public String getExcludingUnit() {
		return excludingUnit;
	}
	public void setExcludingUnit(String excludingUnit) {
		this.excludingUnit = excludingUnit;
	}
	public String getConvertingUnit() {
		return convertingUnit;
	}
	public void setConvertingUnit(String convertingUnit) {
		this.convertingUnit = convertingUnit;
	}
	public float getMultFactor() {
		return multFactor;
	}
	public void setMultFactor(float multFactor) {
		this.multFactor = multFactor;
	}
	
	public String getExclusionComment() {
		return exclusionComment;
	}
	public void setExclusionComment(String exclusionComment) {
		this.exclusionComment = exclusionComment;
	}
	
	public String getMaxStringLength() {
		return maxStringLength;
	}
	public void setMaxStringLength(String maxStringLength) {
		this.maxStringLength = maxStringLength;
	}
	
	public ValueMetadataType getValueMetadataType() {
		if(valueMetadataType == null)
			valueMetadataType = new ValueMetadataType();
		return valueMetadataType;
	}
	public void setValueMetadataType(ValueMetadataType valueMetadataType) {
		this.valueMetadataType = valueMetadataType;
	}
	
	
	public boolean hasValueMetadataType(){
		if(valueMetadataType == null)
			return false;
		else if(valueMetadataType.getDataType().isEmpty())
			return false;
		else	
			return true;
	}
	
	public void fillEmptyValues(){
		if((valueMetadataType.getLowofLowValue() == null) ||(valueMetadataType.getLowofLowValue().isEmpty()))
			valueMetadataType.setLowofLowValue(valueMetadataType.getHighofLowValue());
		else if((valueMetadataType.getHighofLowValue() == null) ||(valueMetadataType.getHighofLowValue().isEmpty()))
			valueMetadataType.setHighofLowValue(valueMetadataType.getLowofLowValue());
		
		if((valueMetadataType.getLowofHighValue() == null) ||(valueMetadataType.getLowofHighValue().isEmpty()))
			valueMetadataType.setLowofHighValue(valueMetadataType.getHighofHighValue());
		else if((valueMetadataType.getHighofHighValue() == null) ||(valueMetadataType.getHighofHighValue().isEmpty()))
			valueMetadataType.setHighofHighValue(valueMetadataType.getLowofHighValue());
	}
	
	public void clear(){
		keyword = null;
		val = null;;
		excludingVal = null ;
		enumType = false;
		numericType = false;
		normalUnit = null;
		equalUnit = null;
		excludingUnit = null;
		convertingUnit = null;
		multFactor = 1;
		exclusionComment = null;
		valueMetadataType = null;
	}

	
}
