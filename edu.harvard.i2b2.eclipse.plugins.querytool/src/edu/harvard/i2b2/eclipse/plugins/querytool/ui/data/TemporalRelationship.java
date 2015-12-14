package edu.harvard.i2b2.eclipse.plugins.querytool.ui.data;

import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryAggregateOperatorType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryJoinColumnType;
import edu.harvard.i2b2.crcxmljaxb.datavo.psm.query.QueryOperatorType;
import edu.harvard.i2b2.eclipse.plugins.querytool.ui.utils.UIConst;
import edu.harvard.i2b2.query.data.Query2XML;

public class TemporalRelationship 
{
	
	public static enum EventMarker {START_OF, END_OF};
	public static enum OccurrenceRestriction {FIRST_EVER, LAST_EVER, ANY};
	public static enum Operator {BEFORE, ON_OR_BEFORE, EQUALS, ON_OR_AFTER, AFTER};
	public static enum TimeUnit {SECONDS, MINUTES, HOURS, DAYS, MONTHS, YEARS};
	
	private Event 					topEvent					= null;
	private EventMarker				topEventMarker				= null;
	private OccurrenceRestriction	topOccurrenceRestriction	= null;

	private Operator				operator					= null;

	private Event 					botEvent					= null;
	private EventMarker				botEventMarker				= null;
	private OccurrenceRestriction	botOccurrenceRestriction	= null;

	private OrderedDuration 		myDuration1					= null;
	private OrderedDuration 		myDuration2					= null;
	
	
	
	// default constructor with values from default UI
	public TemporalRelationship()
	{
		topEventMarker = EventMarker.START_OF;
		botEventMarker = EventMarker.START_OF;
		topOccurrenceRestriction = OccurrenceRestriction.FIRST_EVER;
		botOccurrenceRestriction = OccurrenceRestriction.FIRST_EVER;
		operator				 = Operator.BEFORE;
	}
	
	/* getters */
	public Event getTopEvent()		{ return this.topEvent; }
	public Event getBotEvent()		{ return this.botEvent; }
	public EventMarker getTopReferencePoint() { return this.topEventMarker; } 
	public EventMarker getBotReferencePoint() { return this.botEventMarker; }
	public OccurrenceRestriction getTopOccurrenceRestriction() { return this.topOccurrenceRestriction; }
	public OccurrenceRestriction getBotOccurrenceRestriction() { return this.botOccurrenceRestriction; }
	public OrderedDuration	getDuration1()	{ return this.myDuration1; }
	public OrderedDuration	getDuration2()	{ return this.myDuration2; }	

	public Operator getOperator() { return this.operator; }
	
	/* setters */
	public void	setTopEvent(Event ev)	{ topEvent = ev; }
	public void	setBotEvent(Event ev)	{ botEvent = ev; }
	public void setTopEventMarker(String markerString)	{ this.topEventMarker = mapEventMarker( markerString ); }
	public void setBotEventMarker(String markerString)	{ this.botEventMarker = mapEventMarker( markerString ); }
	public void setTopOccurrenceRestriction(String restrictionString) { this.topOccurrenceRestriction = mapOccurrenceRestriction( restrictionString ); }
	public void setBotOccurrenceRestriction(String restrictionString) { this.botOccurrenceRestriction = mapOccurrenceRestriction( restrictionString ); }
	
	public void setDuration1(String op, int number, String unit)
	{
		if ( myDuration1 == null ) // make sure myDuration1 is not null
			myDuration1 = new OrderedDuration();
		setDuration( this.myDuration1, op, number, unit ); 
	}
	public void setDuration2(String op, int number, String unit)
	{ 
		if ( myDuration2 == null ) // make sure myDuration2 is not null
			myDuration2 = new OrderedDuration();
		setDuration( this.myDuration2, op, number, unit ); 
	}
		
	public void resetDuration1() { this.myDuration1 = null; }
	public void resetDuration2() { this.myDuration2 = null; }
	
	public void setOperator( String opString ) { this.operator = mapOperator( opString ); }

	private void setDuration( OrderedDuration duration,  String op, int number, String unit )
	{  duration.setDuration( mapToOperator(op), mapToUnit(unit), number ); }

	
	/* The mapping maps from 
	 * 	(1) user's selection in UI widgets to the enums a TemporalRelationship requires
	 * 	(2) query's XML string to the enums a TemporalRelstionship requires
	 */
	private EventMarker mapEventMarker( String markerString )
	{
		if ( markerString.equals(UIConst.START_0F) ||  markerString.equals( QueryJoinColumnType.STARTDATE.toString() ) )
			return EventMarker.START_OF;
		else if ( markerString.equals(UIConst.END_OF) ||  markerString.equals( QueryJoinColumnType.ENDDATE.toString() ) )
			return EventMarker.END_OF;
		assert false: "TemporalRelationship.mapEventMarker: Occurrence Restriction String: '" + markerString + "' is not recognized.";
		return null;
	}

	/* The mapping maps from 
	 * 	(1) user's selection in UI widgets to the enums a TemporalRelationship requires
	 * 	(2) query's XML string to the enums a TemporalRelstionship requires
	 */
	private Operator mapOperator(String opString) 
	{
		if ( opString.equals(UIConst.BEFORE) || opString.equals( QueryOperatorType.LESS.toString() ) )
			return Operator.BEFORE;
		else if ( opString.equals(UIConst.ON_OR_BEFORE) || opString.equals( QueryOperatorType.LESSEQUAL.toString() ) )
			return Operator.ON_OR_BEFORE;
		else if ( opString.equals(UIConst.EQUALS) || opString.equals( QueryOperatorType.EQUAL.toString() ) )
			return Operator.EQUALS;
		else if ( opString.equals(UIConst.ON_OR_AFTER) || opString.equals( QueryOperatorType.GREATEREQUAL.toString() ) )
			return Operator.ON_OR_AFTER;
		else if ( opString.equals(UIConst.AFTER) || opString.equals( QueryOperatorType.GREATER.toString() ) )
			return Operator.AFTER;
		assert false: "TemporalRelationship.mapOperator: Operator Name: '" + opString + "' is not recognized.";
		return null;
	}

	/* The mapping maps from 
	 * 	(1) user's selection in UI widgets to the enums a TemporalRelationship requires
	 * 	(2) query's XML string to the enums a TemporalRelstionship requires
	 */
	private OccurrenceRestriction mapOccurrenceRestriction( String restrictionString )
	{
		if ( restrictionString.equals(UIConst.FIRST_EVER) || restrictionString.equals( QueryAggregateOperatorType.FIRST.toString() )) 
			return OccurrenceRestriction.FIRST_EVER;
		else if ( restrictionString.equals(UIConst.LAST_EVER) || restrictionString.equals( QueryAggregateOperatorType.LAST.toString() )) 
			return OccurrenceRestriction.LAST_EVER;
		else if ( restrictionString.equals(UIConst.ANY) || restrictionString.equals( QueryAggregateOperatorType.ANY.toString() ))
			return OccurrenceRestriction.ANY;
		assert false: "TemporalRelationship.mapOccurrenceRestriction: Occurrence Restriction String: '" + restrictionString + "' is not recognized.";
		return null;
	}

	/* The mapping maps from 
	 * 	(1) user's selection in UI widgets to the enums a TemporalRelationship requires
	 * 	(2) query's XML string to the enums a TemporalRelstionship requires
	 */
	private OrderedDuration.Operator mapToOperator( String opName )
	{
		if ( opName.equals(UIConst.GREATER_THAN_SYM) || opName.equals( QueryOperatorType.GREATER.toString() ) )
			return OrderedDuration.Operator.GT;
		else if ( opName.equals(UIConst.GREATER_THAN_EQUALS_TO_SYM) || opName.equals( QueryOperatorType.GREATEREQUAL.toString() ) )
			return OrderedDuration.Operator.GTE;
		else if ( opName.equals(UIConst.EQUAL_SYM) || opName.equals( QueryOperatorType.EQUAL.toString() ))
			return OrderedDuration.Operator.E;
		else if ( opName.equals(UIConst.LESS_THAN_EQUALS_TO_SYM) || opName.equals( QueryOperatorType.LESSEQUAL.toString() ) )
			return OrderedDuration.Operator.LTE;
		else if ( opName.equals(UIConst.LESS_THAN_SYM) || opName.equals( QueryOperatorType.LESS.toString() ) )
			return OrderedDuration.Operator.LT;
		assert false: "TemporalRelationship.mapToOperator: Operator Name: '" + opName + "' is not recognized.";
		return null;
	}

	/* The mapping maps from 
	 * 	(1) user's selection in UI widgets to the enums a TemporalRelationship requires
	 * 	(2) query's XML string to the enums a TemporalRelstionship requires
	 */
	private TimeUnit mapToUnit(String unit) 
	{
		if ( unit.equals(UIConst.SECONDS) || unit.equals( Query2XML.TIME_UNITS[0]) )
			return TimeUnit.SECONDS;
		else if ( unit.equals(UIConst.MINUTES) || unit.equals( Query2XML.TIME_UNITS[1]) )
			return TimeUnit.MINUTES;
		else if ( unit.equals(UIConst.HOURS) || unit.equals( Query2XML.TIME_UNITS[2]))
			return TimeUnit.HOURS;
		else if ( unit.equals(UIConst.DAYS) || unit.equals( Query2XML.TIME_UNITS[3]) )
			return TimeUnit.DAYS;
		else if ( unit.equals(UIConst.MONTHS) || unit.equals( Query2XML.TIME_UNITS[4]) )
			return TimeUnit.MONTHS;
		else if ( unit.equals(UIConst.YEARS) || unit.equals( Query2XML.TIME_UNITS[5]) )
			return TimeUnit.YEARS;
		assert false: "TemporalRelationship.mapToUnit: Unit Name: '" + unit + "' is not recognized.";
		return null;
	}
	                  
	
	//bugbug: temporary toString. Needs better implementation
	public String toString()
	{
		String topEventString = null;
		String botEventString = null;
		if ( this.topEvent != null )
			topEventString = this.topEvent.getName();
		if ( this.botEvent != null )
			botEventString = this.botEvent.getName();
		
		String durations = "";
		if ( this.myDuration1 != null)
		{
			durations = durations + "[" + myDuration1.toString() + "]";
			if ( this.myDuration2 != null )
				durations = durations + " and [" + myDuration2.toString()+"]";
		}
		
		return this.topEventMarker + " " + this.topOccurrenceRestriction + " " + topEventString + " " + this.operator + " " + this.botEventMarker + " " + this.botOccurrenceRestriction + " " + botEventString + durations ;
	}

}
